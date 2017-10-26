package comp346.assignments.a2.task_2;

class Semaphore {
	private int value;

	public Semaphore(int value) {
		this.value = Math.abs(value);
	}

	public Semaphore() {
		this(0);
	}

	public synchronized void Wait() {
		this.value--;
		while (this.value <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Semaphore::Wait() - caught InterruptedException: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public synchronized void Signal() {
		++this.value;
		notify();
	}

	public synchronized void P() {
		this.Wait();
	}

	public synchronized void V() {
		this.Signal();
	}
	
	public synchronized int getWaitingProcesses() { //returns waiting processes (0 = 1 waiting, -1 = 2 waiting)
		if (value > 0) {
			return 0;
		}
		else {
			return Math.abs(value) + 1;
		}
	}
}