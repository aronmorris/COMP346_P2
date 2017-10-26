package comp346.assignments.a2;

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
