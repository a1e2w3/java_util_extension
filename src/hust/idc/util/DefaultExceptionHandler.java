package hust.idc.util;

public class DefaultExceptionHandler implements ExceptionHandler {

	@Override
	public void handle(Throwable th) {
		// TODO Auto-generated method stub
		th.printStackTrace();
	}

}
