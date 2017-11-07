package comp346.assignments.a2.task_4;

class Semaphore {
	private int value;
	
	private int startValue;

	public Semaphore(int value) {
		this.startValue = Math.abs(value);
		
		this.value = startValue;
	}

	public Semaphore() {
		this(0);
	}

	public synchronized void Wait() {
		this.value--; 
		while (this.value < 0) { //there was a bug here, where value <= 0 would catch threads trying to move through at value = 1 getting
			try {				 //decremented and getting stuck forever when there was a legal number of flags.
				wait();
			} catch (InterruptedException e) {
				System.out.println("Semaphore::Wait() - caught InterruptedException: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public synchronized void Signal() {
		++this.value;
		notifyAll(); //notify changed to notifyAll, which gives every process waiting to proceed a fair opportunity to do so.
					 //this fixes the deadlock problem encountered at times.
	}

	public synchronized void P() {
		this.Wait();
	}

	public synchronized void V() {
		this.Signal();
	}
	
	public synchronized int getWaitingProcesses() { //returns suspended processes (-1 = 1 waiting, -2 = 2 waiting.
													//>=0 means a license has potentially been taken by a process which is executing
		int waiting = startValue - Math.abs(value);
		
		if (value >= 0) {
			return 0;
		}
		else {
			return Math.abs(value);
		}
	}
}