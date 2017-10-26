package comp346.assignments.a2.task_1;

public class CharStackFullException extends Exception {
	
	public CharStackFullException() {
		super("Char Stack has reached its capacity of " + CharStack.MAX_SIZE);
	}
}
