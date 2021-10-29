package com.superslow.locker.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ExecuteTaskManager implements Runnable {

    private static final int COMMON_EXECUTE_TASK_TYPE = 0;

    public volatile boolean isRunning = false;

    private volatile boolean isHasInit = false;

    private static final int DEFAULT_THREAD_NUM = 5;

    private volatile int threadNum = DEFAULT_THREAD_NUM;

    private static ScheduledExecutorService singlePool = null;

    private static ExecutorService threadPool = null;

    private static ConcurrentLinkedQueue<ExecuteTask> allExecuteTask = null;

    private static ConcurrentHashMap<Integer, Object> uniqueListenerList = null;

    private static ConcurrentSkipListSet<String> md5FilterList = null;


    public Handler getHandler() {
        return handler;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public boolean isHasInit() {
        return isHasInit;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public interface GetExecuteTaskCallback {
        void onDataLoaded(ExecuteTask task);
    }

    private final static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            long start = System.currentTimeMillis();

            switch (msg.what) {
                case COMMON_EXECUTE_TASK_TYPE:
                    if (msg.obj != null && msg.obj instanceof ExecuteTask) {
                        ExecuteTaskManager.getInstance().doCommonHandler((ExecuteTask) msg.obj);
                    } else {
                    }
                    break;
                default:
                    break;
            }
            long end = System.currentTimeMillis();

        }
    };


    private static ExecuteTaskManager instance = null;

    private ExecuteTaskManager() {
    }

    public static ExecuteTaskManager getInstance() {
        if (instance == null) {
            synchronized (ExecuteTaskManager.class) {
                if (instance == null) {
                    instance = new ExecuteTaskManager();
                }
            }
        }
        return instance;
    }

    public void init() {
        init(threadNum);
    }

    public synchronized void init(int initNum) {
        if (!isHasInit) {

            isRunning = true;
            if (initNum > 0) {
                threadNum = initNum;
            }
            threadPool = Executors.newFixedThreadPool(threadNum);
            singlePool = Executors.newSingleThreadScheduledExecutor();
            allExecuteTask = new ConcurrentLinkedQueue<>();
            uniqueListenerList = new ConcurrentHashMap<>();
            md5FilterList = new ConcurrentSkipListSet<>();

            for (int i = 0; i < threadNum; i++) {
                threadPool.execute(this);
            }
            isHasInit = true;
        } else {
        }
    }


    public void doDestroy() {
        isRunning = false;
        isHasInit = false;
        if (allExecuteTask != null) {
            allExecuteTask.clear();
            allExecuteTask = null;
        }
        if (uniqueListenerList != null) {
            uniqueListenerList.clear();
            uniqueListenerList = null;
        }
        if (md5FilterList != null) {
            md5FilterList.clear();
            md5FilterList = null;
        }
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
        if (singlePool != null) {
            singlePool.shutdown();
            singlePool = null;
        }
    }


    public void newExecuteTask(ExecuteTask task) {
        if (task != null) {

            if (!TextUtils.isEmpty(task.getMd5Id()) && md5FilterList.contains(task.getMd5Id())) {
                return;
            }

            allExecuteTask.offer(task);
            long timeOne = System.currentTimeMillis();
            synchronized (allExecuteTask) {
                allExecuteTask.notifyAll();
            }
            long timeTwo = System.currentTimeMillis();
        } else {
        }
    }


    public void getData(ExecuteTask task, GetExecuteTaskCallback callback) {

        try {
            if (task != null && callback != null) {

                if (!TextUtils.isEmpty(task.getMd5Id()) && md5FilterList.contains(task.getMd5Id())) {
                    return;
                }

                if (task.getUniqueID() > 0 && uniqueListenerList.containsKey(task.getUniqueID())) {
                    return;
                }


                if (task.getUniqueID() == 0) {
                    task.setUniqueID(task.hashCode());
                }
                uniqueListenerList.put(task.getUniqueID(), callback);

                newExecuteTask(task);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeExecuteTask(ExecuteTask task) {
        if (task != null) {
            if (task.getUniqueID() > 0) {
                uniqueListenerList.remove(task.getUniqueID());
            }
            if (!TextUtils.isEmpty(task.getMd5Id())) {
                md5FilterList.remove(task.getMd5Id());
            }
            allExecuteTask.remove(task);
        } else {
        }
    }


    public void removeAllExecuteTask() {
        allExecuteTask.clear();
        uniqueListenerList.clear();
        md5FilterList.clear();
    }

    @Override
    public void run() {
        while (isRunning) {

            ExecuteTask lastExecuteTask = allExecuteTask.poll();

            if (lastExecuteTask != null) {
                try {
                    doExecuteTask(lastExecuteTask);
                } catch (Exception e) {
                    e.printStackTrace();
                    lastExecuteTask.setStatus(ExecuteTask.EXCUTE_TASK_ERROR);
                    doSendMessage(lastExecuteTask);
                }
            } else {
                try {
                    synchronized (allExecuteTask) {
                        allExecuteTask.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doExecuteTask(ExecuteTask task) {
        if (task == null) {
            return;
        }

        long startTime = System.currentTimeMillis();

        ExecuteTask result = task.doTask();

        if (result != null && task == result && result.getUniqueID() != 0) {
            doSendMessage(task);
        } else {
            if (task.getUniqueID() > 0) {
                uniqueListenerList.remove(task.getUniqueID());
            }
            if (!TextUtils.isEmpty(task.getMd5Id())) {
                md5FilterList.remove(task.getMd5Id());
            }
        }
    }


    private void doSendMessage(ExecuteTask result) {
        if (result.isMainThread()) {
            Message msg = Message.obtain();
            msg.what = COMMON_EXECUTE_TASK_TYPE;
            msg.obj = result;
            handler.sendMessage(msg);
        } else {
            doCommonHandler(result);
        }
    }

    private void doCommonHandler(ExecuteTask task) {
        long start = System.currentTimeMillis();

        if (task != null) {

            try {
                if (uniqueListenerList.get(task.getUniqueID()) instanceof GetExecuteTaskCallback) {
                    ((GetExecuteTaskCallback) uniqueListenerList.get(task.getUniqueID())).onDataLoaded(task);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (task.getUniqueID() > 0) {
                uniqueListenerList.remove(task.getUniqueID());
            }
            if (!TextUtils.isEmpty(task.getMd5Id())) {
                md5FilterList.remove(task.getMd5Id());
            }

        } else {
        }
        long end = System.currentTimeMillis();
    }

    public void execute(Runnable runnable) {
        singlePool.execute(runnable);
    }

    public void execute(Runnable runnable, long delay) {
        singlePool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public void execute(Runnable runnable, long delay, TimeUnit timeUnit) {
        singlePool.schedule(runnable, delay, timeUnit);
    }

    public void scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        singlePool.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    public void scheduleAtFixedRate(Runnable runnable, long delay, long period) {
        singlePool.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public void scheduleAtFixedRate(Runnable runnable, long period) {
        singlePool.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MILLISECONDS);
    }

    public ScheduledExecutorService getSinglePool() {
        return singlePool;
    }
}

