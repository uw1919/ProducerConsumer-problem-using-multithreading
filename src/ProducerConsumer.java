/*
 * ProducerConsumer.java
 * 
 * @version
 * $Id: ProducerConsumer.java, Version 1.0 11/01/2014 $
 * 
 * @revision
 * $Log initial version $
 * 
 */

/**
 * The class uses threads to emulate a producer producing product parts and 
 * consumer using them to assemble a lamp
 * 
 * @author Uday Vilas Wadhone
 * 
 *
 */
public class ProducerConsumer extends Thread {

	//count variables to keep track of number of each part
	static volatile int screwsCount = 0;
	static volatile int basesCount = 0;
	static volatile int standsCount = 0;
	static volatile int socketsCount = 0;
	static volatile int bulbCount = 0;

	//variables to keep track of number of parts used by consumer
	int screwsUsed = 0;
	int basesUsed = 0;
	int standsUsed = 0;
	int socketsUsed = 0;
	int bulbUsed = 0;

	//boolean variable to know if a producer is in use
	static boolean producerBeingUsed = false;

	//constant variables for max number of parts allowed to be produced
	final int maxscrews = 4;
	final int maxbases = 2;
	final int maxstands = 4;
	final int maxsockets = 7;
	final int maxbulbs = 4;

	//object used for each instance
	Object o;
	//object used for synchronization
	static Object s = new Object();

	//initialise consumer name to empty string
	String consumer = "";

	/**
	 * Constructor for class initialising consumer name and object
	 * 
	 * @param	 consumer	current consumer
	 * @param	 o			current object passed
	 */
	public ProducerConsumer(String consumer, Object o) {
		this.consumer = consumer;
		this.o = o;
	}

	/**
	 * Main function - Creates threads of consumer and starts them
	 * @param args
	 * 
	 */
	public static void main(String[] args) {

		//objects for each consumer -  passed when thread is created
		Object o1 = new Object();
		Object o2 = new Object();

		//consumer threads are created
		ProducerConsumer consumer1 = new ProducerConsumer("Consumer 1", o1);
		ProducerConsumer consumer2 = new ProducerConsumer("Consumer 2", o2);

		//start consumer threads
		consumer1.start();
		consumer2.start();

	}

	/**
	 * Run function for each consumer that calls the lampAssemply() function
	 */
	public void run() {
		lampAssembly();
	}

	/**
	 * Returns true if producer should create more parts for lamp
	 * 
	 * @return	true if producer needs to start again otherwise false
	 * 
	 */
	public boolean isAnyProducerEmpty() {
		
		//check if count of any part is empty
		return (screwsCount == 0 || basesCount == 0 || standsCount == 0
				|| socketsCount == 0 || bulbCount == 0);
	}

	/**
	 * Boolean function to check if lamp is made 
	 * 
	 * @return true if lamp is made, otherwise ,false
	 */
	public boolean LampMade() {
		
		//check if lamp is made
		return (screwsUsed == maxscrews && basesUsed == maxbases
				&& standsUsed == maxstands && socketsUsed == maxsockets && bulbUsed == maxbulbs);
	}

	/**
	 * Function to assemble lamp by consumer using parts produced by producer
	 * and notify producer to create more parts if consumer needs them
	 * 
	 */
	public void lampAssembly() {

		System.out.println(consumer + " is assembling lamp");
		/*
		 * synchronized on object o so that only one consumer thread can access
		 * the parts produced 
		 */
		synchronized (o) {
			
			//run loop until lamp is made
			while (!LampMade()) {

				//run loop until no producer is out of parts
				while (!isAnyProducerEmpty()) {

					/*
					 * check constraints for each producer and consumer consumes
					 * the parts, printing the number of parts used and the
					 * number of parts left
					 * 
					 */
					if (screwsCount > 0 && screwsUsed < maxscrews) {
						screwsUsed++;
						screwsCount--;
						System.out.println(consumer + " consumed 1 screw");
						System.out.println("Total screws now left : "
								+ screwsCount);
					}

					if (basesCount > 0 && basesUsed < maxbases) {
						basesUsed++;
						basesCount--;
						System.out.println(consumer + " consumed 1 base");
						System.out.println("Total bases now left : "
								+ basesCount);
					}

					if (standsCount > 0 && standsUsed < maxstands) {
						standsUsed++;
						standsCount--;
						System.out.println(consumer + " used 1 stand");
						System.out.println("Total stands now left :  "
								+ standsCount);
					}

					if (socketsCount > 0 && socketsUsed < maxsockets) {
						socketsUsed++;
						socketsCount--;
						System.out.println(consumer + " consumed 1 socket");
						System.out.println("Total sockets now left : "
								+ socketsCount);
					}

					if (bulbCount > 0 && bulbUsed < maxbulbs) {
						bulbUsed++;
						bulbCount--;
						System.out.println(consumer + " consumed 1 bulb");
						System.out.println("Total bulbs now left :  "
								+ bulbCount);
					}
				}

				/**
				 * Consumer threads synchronized on object s so that only one 
				 * consumer can enter the block at a time
				 * 
				 */
				synchronized (s) {
					
					//check if producer is in use
					if (producerBeingUsed == false) {
						//check if any producer is empty
						if (isAnyProducerEmpty()) {
							try {
								//create and new producer and start it.
								Producer p = new Producer();
								p.start();
								System.out.println("Producer started by :  "
										+ consumer);
								System.out.println(consumer + " waiting");
								
								/*
								 * make make consumer wait until producer
								 * can make parts required by consumer
								 */
								s.wait();
								//notify all consumers that parts are ready
								s.notifyAll();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						try {
						    /*
						     * if any producer is empty then consumer is put in
						     * wait
						     * 
						     */
							s.wait();
							s.notifyAll();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}

			}
		}

		/*
		 * output how many parts each consumer uses and when consumer has
		 * successfully created the lamp
		 *  
		 */
		System.out.println(consumer + " consumed : " + screwsUsed + " screws");
		System.out.println(consumer + " consumed : " + basesUsed + " base");
		System.out.println(consumer + " consumed : " + standsUsed + " stands");
		System.out
				.println(consumer + " consumed : " + socketsUsed + " sockets");
		System.out.println(consumer + " consumed : " + bulbUsed + " bulbs");
		System.out.println("<---------->" + consumer + " has assembled lamp"
				+ "<---------->");

	}

	/**
	 * Producer class to produce individual parts of the lamp. Called by 
	 * consumer when more items are required
	 * 
	 * @author Uday Vilas Wadhone
	 * 
	 *
	 */
	public class Producer extends Thread {

		/**
		 * Run function of producer thread that calls the produce() function
		 * to start producing parts
		 * 
		 */
		public void run() {
			
			//make boolean variable true to announce that producer is running
			producerBeingUsed = true;
			produce();
		}

		/**
		 * Produce function creates more parts as required by user
		 * 
		 */
		public void produce() {

			/*
			 * synchronized on object s so that there are no two instances of
			 * producer class conflicting for access
			 * 
			 */
			synchronized (s) {
				
				/*
				 * if conditions to check if production of each part is required
				 * and set each part to max value when production takes place
				 * 
				 */
				if (screwsCount == 0) {
					screwsCount = maxscrews;
					System.out.println("Producer has produced :  " + maxscrews
							+ " screws");
				}

				if (basesCount == 0) {
					basesCount = maxbases;
					System.out.println("Producer has produced :  " + maxbases
							+ " bases");
				}

				if (standsCount == 0) {
					standsCount = maxstands;
					System.out.println("Producer has produced :  " + maxstands
							+ " stands");
				}

				if (socketsCount == 0) {
					socketsCount = maxsockets;
					System.out.println("Producer has produced :  " + maxsockets
							+ " sockets");
				}

				if (bulbCount == 0) {
					bulbCount = maxbulbs;
					System.out.println("Producer has produced :  " + maxbulbs
							+ " bulbs");
				}
				
				/*
				 * notify consumers that producer has finished producing parts
				 * required by the consumer
				 * 
				 */
				s.notifyAll();
				/*
				 * make boolean variable false to notify that producer is no
				 * longer in use
				 * 
				 */
				producerBeingUsed = false;

			}

		}
	}

}