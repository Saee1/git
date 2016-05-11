package com.test.work.samples;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkTester {
	public static final int WORKER_THREAD_COUNT = 10;
	public static final int QUEUE_SIZE = 5;
	BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<Order>(QUEUE_SIZE);

	public void startWorkerThread() {
		// Starting the worker thread
		Worker worker = new Worker(orderQueue);
		Thread workerThread = new Thread(worker);
		workerThread.setDaemon(true);
		workerThread.start();

	}

	public void startOrderProducers(int noOfOrders) {

		ExecutorService workExecutorService = null;
		try {
			workExecutorService = Executors.newFixedThreadPool(WORKER_THREAD_COUNT);
			for (int i = 0; i < noOfOrders; i++) {
				OrderProducer opd = new OrderProducer(orderQueue, new Order(i + 1));
				workExecutorService.submit(opd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workExecutorService.shutdown();
		}

	}

	/**
	 * The main method of this tester class
	 * @param args
	 */

	public static void main(String[] args) {
		WorkTester wt = new WorkTester();
		wt.startWorkerThread();
		wt.startOrderProducers(20);
	}

}

class OrderProducer extends Thread {
	private BlockingQueue<Order> orderQueue;
	private Order order;
	public OrderProducer(BlockingQueue<Order> orderQueue, Order order) {
		this.orderQueue = orderQueue;
		this.order = order;
	}

	@Override
	public void run() {
		try {
			System.out.println("Adding Order[" + order.getOrderId()
					+ "] to the queue.");

			orderQueue.put(order);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Worker implements Runnable {
	BlockingQueue<Order> queue;
	public Worker(BlockingQueue<Order> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Order order = queue.take();
				processOrder(order);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void processOrder(Order order) {
		order.setState(Order.ORDERSTATE.FULFILLED);
	}

}

class Order {
	private int orderId;
	public enum ORDERSTATE {
		NEW,
		FULFILLED
	};

	private ORDERSTATE state;

	public Order(int orderId) {
		this.orderId = orderId;
		this.state = ORDERSTATE.NEW;

	}

	public int getOrderId() {
		return orderId;
	}

	public ORDERSTATE getState() {
		return state;
	}

	public void setState(ORDERSTATE state) {
		System.out.println("For OrderId:[" + orderId + "] the status is now:["
				+ state + "]");
		this.state = state;

	}

}