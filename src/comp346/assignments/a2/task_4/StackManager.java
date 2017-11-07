package comp346.assignments.a2.task_4;

public class StackManager {
	// The Stack
	private static CharStack stack = new CharStack();
	@SuppressWarnings("unused")
	private static final int NUM_ACQREL = 4; // Number of Producer/Consumer
												// threads
	@SuppressWarnings("unused")
	private static final int NUM_PROBERS = 1; // Number of threads dumping stack
	private static int iThreadSteps = 3; // Number of steps they take
	
	// Semaphore declarations. Insert your code in the following:
	static Semaphore full = new Semaphore(1);
	static Semaphore empty = new Semaphore(1);
	
	static Semaphore producerMutex = new Semaphore(0);
		
	// The main()

	@SuppressWarnings("static-access")
	public static void main(String[] argv) {
		// Some initial stats...
		try {
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + stack.getTop() + ".");
			System.out.println("Initial value of stack top = " + stack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");
		} catch (CharStackEmptyException e) {
			System.out.println("Caught exception: StackCharEmptyException");
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
		/*
		 * The birth of threads
		 */
		Consumer ab1 = new Consumer();
		Consumer ab2 = new Consumer();
		System.out.println("Two Consumer threads have been created.");
		Producer rb1 = new Producer();
		Producer rb2 = new Producer();
		System.out.println("Two Producer threads have been created.");
		CharStackProber csp = new CharStackProber();
		System.out.println("One CharStackProber thread has been created.");
		/*
		 * start executing
		 */
		ab1.start();
		rb1.start();
		ab2.start();
		rb2.start();
		csp.start();
		/*
		 * Wait by here for all forked threads to die
		 */
		try {
			ab1.join();
			ab2.join();
			rb1.join();
			rb2.join();
			csp.join();
			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + stack.getTop() + ".");
			System.out.println("Final value of stack top = " + stack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
			// System.out.println("Stack access count = " +
			// stack.getAccessCounter()); //TODO make this a thing I guess
		} catch (InterruptedException e) {
			System.out.println("Caught InterruptedException: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Caught exception: " + e.getClass().getName());
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
	} // main()
	/*
	 * Inner Consumer thread class
	 */

	static class Consumer extends BaseThread {
		private char copy; // A copy of a block returned by pop()

		public void run() {
			System.out.println("Consumer thread [TID=" + this.iTID + "] starts executing.");
			
			producerMutex.Wait(); //Immediately wait until signaled by a producer
			
			System.out.println("Consumer " + this.iTID + " past the producerMutex");
			for (int i = 0; i < StackManager.iThreadSteps; i++) {
				// Insert your code in the following:
				
				try {
					
					//empty.Wait();
					
					consume();
					
				} catch (CharStackEmptyException e) {
					e.printStackTrace();
				} finally {
					
					System.out.println("Consumer thread [TID=" + this.iTID + "] consumes character = " + this.copy);
					
					//empty.Signal();
				}
					
			}
			
			System.out.println("Consumer thread [TID=" + this.iTID + "] terminates.");
		}
		
		@SuppressWarnings("static-access")
		private void consume() throws CharStackEmptyException {
			
			if (stack.getTop() == -1) { //trying to consume a nothing, stack is empty
				System.out.println("EMPTY STACK");
			}
			else {
				copy = stack.pop(); //retrieve removed element for logging
			}
		
		}
		
	} // class Consumer
	/*
	 * Inner class Producer
	 */

	static class Producer extends BaseThread {
		private char block; // block to be returned

		public void run() {
			System.out.println("Producer thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < StackManager.iThreadSteps; i++) {
								
				try {
					
					//full.Wait();
					
					produce();
					
					//full.Signal();
		
					
				} catch (CharStackEmptyException e) {
					System.err.println("Stack is empty, cannot read value.\n");
					e.printStackTrace();
				} catch (CharStackFullException e) {
					System.err.println("Stack is full, cannot push new value.");
					e.printStackTrace();
				} finally {
					
					System.out.println("Producer thread [TID=" + this.iTID + "] pushes character = " + this.block);
					
					//full.Signal(); //this is in the finally block to ensure it can never be missed due to an error being thrown
					
				}
				
			}
			
			System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
			
			producerMutex.Signal(); //signal only once all work is completed
		}
		
		@SuppressWarnings("static-access")
		private void produce() throws CharStackEmptyException, CharStackFullException {
			
			block = stack.pick();
			
			block += 1;
		
			stack.push((char) (block));
		}
		
	} // class Producer
	/*
	 * Inner class CharStackProber to dump stack contents
	 */

	static class CharStackProber extends BaseThread {
		public void run() {
			System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < 2 * StackManager.iThreadSteps; i++) {
				
				// Insert your code in the following. Note that the stack state
				// must be
				// printed in the required format.
				
				/*
				 * The stack is first copied to preserve changes that have been made to it so far, as string
				 * assembly & printing is slower than adding/removing from it and the stack may change during
				 * the process (synchronize not being allowed in this assignment for this class).
				 * 
				 * The prober uses a StringBuilder to assemble a string and then print the whole of the thing
				 * at once so the output cannot be corrupted by another thread being scheduled in the middle of
				 * a printing loop.
				 */
				CharStack copy = stack;
				
				StringBuilder sb = new StringBuilder();
				
				int size = copy.getTop();
					
				sb.append("Stack S = (");
				
				//this loop prints the letters
				for (int j = 0; j <= size; j++) {
					try {
						sb.append("["+ copy.getAt(j) +"]");
					} catch (CharStackInvalidAccessException e) {
						e.printStackTrace();
					}
				}
				
				//this loop prints the $ tail of the copy and no letters
				for (int j = copy.getTop() + 1; j < copy.getSize(); j++) {
					try {
						sb.append("["+ copy.getAt(j) +"]");
					} catch (CharStackInvalidAccessException e) {
						e.printStackTrace();
					}
				}
				
				sb.append(")");
				
				System.out.println(sb.toString());
				
				//stackSem.Signal();
			}
		}
	} // class CharStackProber
} // class StackManager
