package gift.exception;

public class NameDuplicationException extends RuntimeException{

    public NameDuplicationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
