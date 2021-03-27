package util;

import java.lang.Thread.UncaughtExceptionHandler;

public class ThreadUtil {
	public static class ThreadException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		public ThreadException(String msg){
			super(msg);
		}
	}
	/**
	 * 设置未捕获线程的异常处理器
	 * @param handler
	 * @return
	 */
	public static boolean setDefaultUncaughtExceptionHandlerNX(UncaughtExceptionHandler handler){
		UncaughtExceptionHandler exHandler = Thread.getDefaultUncaughtExceptionHandler();
		if(exHandler==null){
			Thread.setDefaultUncaughtExceptionHandler(handler);
			return true;
		}
		return false;
	}
	/**
	 * 配合getAllThreads、ensureStatus/checkStatus,实现优雅停机
	 * 代码中，根据业务场景，决定是否调用ensureStatus/checkStatus
	 * @param t
	 */
	public static void interruptThread(Thread t){
		t.interrupt();
	}
	public static void ensureStatus(){
		if(Thread.currentThread().isInterrupted()){
			throw new ThreadException("线程已被中断");
		}
	}
	public static boolean checkStatus(){
		return Thread.currentThread().isInterrupted();
	}

	/**
	 * 获取所有线程
	 * 
	 * @return
	 */
	public static Thread[] getAllThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;
		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		return getThreads(topGroup);
	}

	/**
	 * 获取当前线程组的所有线程
	 * 
	 * @return
	 */
	public static Thread[] getCurrentGroupThreads() {
		return getThreads(Thread.currentThread().getThreadGroup());
	}

	/**
	 * 获取指定线程组的所有线程
	 * 
	 * @param group
	 * @return
	 */
	public static Thread[] getThreads(ThreadGroup group) {
		// 激活的线程数加倍
		int estimatedSize = group.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// 获取线程组的所有线程
		int actualSize = group.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		return list;
	}
}
