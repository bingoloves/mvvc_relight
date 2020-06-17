package com.ittianyu.relight.widget.stateful.state;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ittianyu.relight.thread.MainHandler;
import com.ittianyu.relight.thread.ThreadPool;
import com.ittianyu.relight.widget.Widget;
import com.ittianyu.relight.widget.WidgetUpdater;
import com.ittianyu.relight.widget.stateful.state.strategy.CacheStrategy;
import com.ittianyu.relight.widget.stateful.state.strategy.CacheThenTaskStrategy;
import com.ittianyu.relight.widget.stateful.state.strategy.FilterStrategy;
import com.ittianyu.relight.widget.stateful.state.strategy.NotRepeatFilterStrategy;
import com.ittianyu.relight.widget.stateful.state.task.AsyncTask;
import com.ittianyu.relight.widget.stateful.state.task.CacheAsyncTask;
import com.ittianyu.relight.widget.stateful.state.task.CountAsyncTask;
import com.ittianyu.relight.widget.stateful.state.task.FutureSet;
import com.ittianyu.relight.widget.stateful.state.task.TaskSet;
import com.ittianyu.relight.widget.stateful.state.task.UpdateTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * call in order:
 * 1. init
 * 2. build
 * 3. willUpdate
 * 4. update
 * 5. didUpdate
 * <p>
 * You can call dispose to stop the state operations and release resources.
 */
public abstract class State<T extends Widget> implements SetState {
    private static final String TAG = "State";
    public static FilterStrategy defaultFilterStrategy = new NotRepeatFilterStrategy();
    private static Class<? extends CacheStrategy> defaultCacheStrategy = CacheThenTaskStrategy.class;
    private static Handler handler = new MainHandler(Looper.getMainLooper());
    private WidgetUpdater widgetUpdater;
    private FilterStrategy filterStrategy;
    private UpdateTask updateTask = new UpdateTask(this);
    private Map<Runnable, Future> updateStateMap = new HashMap<>();
    private boolean disposed;

    public State() {
        this(null);
    }

    /**
     *
     * @param filterStrategy if null, it don't filter any func
     */
    public State(FilterStrategy filterStrategy) {
        if (null == filterStrategy) {
            filterStrategy = defaultFilterStrategy;
        }
        this.filterStrategy = filterStrategy;
    }

    public void setOnUpdateListener(WidgetUpdater widgetUpdater) {
        this.widgetUpdater = widgetUpdater;
    }

    public static void setDefaultFilterStrategy(FilterStrategy defaultFilterStrategy) {
        State.defaultFilterStrategy = defaultFilterStrategy;
    }

    public static void setDefaultCacheStrategy(Class<? extends CacheStrategy> defaultCacheStrategy) {
        State.defaultCacheStrategy = defaultCacheStrategy;
    }

    public void init() {}

    public abstract T build(Context context, Lifecycle lifecycle);

    public void willUpdate() {}

    public void update() {
        if (widgetUpdater != null)
            widgetUpdater.update();
    }

    public void didUpdate() {
        cleanFinishedTask();
    }

    public void dispose() {
        disposed = true;
        for (Future future : updateStateMap.values()) {
            if (future.isDone())
                continue;
            future.cancel(false);
        }
        updateStateMap = null;
    }

    public boolean isDisposed() {
        return disposed;
    }

    /**
     * run func in main thread.
     * @param func
     */
    @Override
    public void setState(Runnable func) {
        willUpdate();
        if (null != func)
            func.run();
        update();
        didUpdate();
    }

    /**
     * run func in other thread. And update in main thread.
     *
     * @param func
     */
    @Override
    public void setStateAsync(Runnable func) {
        if (shouldIgnored(func)) {
            return;
        }

        willUpdate();
        AsyncTask task = new AsyncTask(handler, func, updateTask);
        Future<?> result = ThreadPool.get().submit(task);
        updateStateMap.put(func, result);
    }

    @Override
    public void setStateAsync(Runnable... tasks) {
        if (tasks == null || tasks.length == 0) {
            return;
        }
        TaskSet taskSet = new TaskSet(tasks);
        if (shouldIgnored(taskSet)) {
            return;
        }

        willUpdate();
        AtomicInteger count = new AtomicInteger(tasks.length);
        FutureSet futureSet = new FutureSet();
        for (Runnable task : tasks) {
            CountAsyncTask countAsyncTask = new CountAsyncTask(handler, task, updateTask, count);
            Future<?> result = ThreadPool.get().submit(countAsyncTask);
            futureSet.add(result);
        }
        updateStateMap.put(taskSet, futureSet);
    }

    @Override
    public void setStateAsyncWithCache(Runnable cacheFunc, Runnable func) {
        try {
            setStateAsyncWithCache(defaultCacheStrategy.newInstance(), cacheFunc, func);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStateAsyncWithCache(CacheStrategy cacheStrategy, Runnable cacheFunc,
        Runnable func) {
        // cacheFunc or func is running, ignore
        if (shouldIgnored(func)) {
            return;
        }

        willUpdate();
        // run by cache strategy
        CacheAsyncTask task = new CacheAsyncTask(handler, cacheFunc, func, updateTask, cacheStrategy);
        Future<?> result = ThreadPool.get().submit(task);
        updateStateMap.put(func, result);
    }

    private boolean shouldIgnored(Object func) {
        if (filterStrategy == null) {
            return false;
        }
        return !filterStrategy.filter(updateStateMap, func);
    }

    private void cleanFinishedTask() {
        if (updateStateMap == null) {
            return;
        }
        Iterator<Map.Entry<Runnable, Future>> it = updateStateMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Runnable, Future> entry = it.next();
            if (entry.getValue().isDone()) {
                it.remove();
            }
        }
    }
}
