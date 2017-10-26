package comp346.assignments.a2;

public class CharStackInvalidAccessException extends Exception {
	
	public CharStackInvalidAccessException() {
		super("Illegal access outside of stack range.");
	}
}
