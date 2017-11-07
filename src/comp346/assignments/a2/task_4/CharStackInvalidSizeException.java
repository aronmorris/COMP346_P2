package comp346.assignments.a2.task_4;

public class CharStackInvalidSizeException extends Exception {

	public CharStackInvalidSizeException()
    {
            super("Invalid stack size specified.");
    }
    public CharStackInvalidSizeException (int piStackSize)
    {
            super ("Invalid stack size specified: " + piStackSize);
     }

}
