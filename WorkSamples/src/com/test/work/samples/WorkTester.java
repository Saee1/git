package com.test.work.samples;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
 
public class WorkTester {
 
       /**
       *
        * The main method of this tester class
       * @param args
       *
        */
 
       public static void main(String[] args) {
              BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<Order>(5);
              Worker worker = new Worker(orderQueue);
              Thread t1 = new Thread(worker);
              t1.setDaemon(true);
              t1.start();
             
              // Starting 10 work produce threads
              for(int i=0;i<10;i++){
            	  OrderProducer opd = new OrderProducer(orderQueue,new Order(i+1));
            	  opd.start();
              }
             
             
       }
}
 
class OrderProducer extends Thread{
       private BlockingQueue<Order> orderQueue;
       private Order order;
       public OrderProducer(BlockingQueue<Order> orderQueue,Order order) {
              this.orderQueue = orderQueue;
              this.order = order;
       }
      
       @Override
       public void run() {
              try {
                     System.out.println("Adding Order["+order.getOrderId()+"] to the queue.");
                     orderQueue.put(order);
              } catch (InterruptedException e) {
                     e.printStackTrace();
              }
       }
      
}
 
class Worker implements Runnable {
       BlockingQueue<Order> queue;
       public Worker(BlockingQueue<Order> queue){
              this.queue = queue;
       }
       @Override
       public void run() {
              try {
                     while(true){
                           Order order = queue.take();
                           processOrder(order);
                     }
              } catch (InterruptedException e) {
                     e.printStackTrace();
              }
       }
      
       public void processOrder(Order order){
              order.setState(Order.ORDERSTATE.FULFILLED);
       }
}
 
class Order {
       private int orderId;
       public enum  ORDERSTATE{
              NEW,
              FULFILLED
       };
      
       private ORDERSTATE  state;
      
       public Order (int orderId){
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
              System.out.println("For OrderId:["+orderId+"] the status is now:["+state+"]");
              this.state = state;
       }
      
}