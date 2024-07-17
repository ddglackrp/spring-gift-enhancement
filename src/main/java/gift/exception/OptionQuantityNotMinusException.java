package gift.exception;

public class OptionQuantityNotMinusException extends RuntimeException{

    private static final String MESSAGE = "옵션 수량은 0보다 커야합니다.";

    public OptionQuantityNotMinusException(){
        super(MESSAGE);
    }


    public OptionQuantityNotMinusException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
