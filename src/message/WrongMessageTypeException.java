package message;

public class WrongMessageTypeException extends Exception{
    public WrongMessageTypeException(){
        super("Wrong Message Type");
    }
}
