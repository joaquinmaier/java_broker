package stind;

/**
 * Type which allows for error handling and error propagation, similar to <a href="https://doc.rust-lang.org/std/result/">Rust's Result type.</a>
 * It is intended to avoid the usage of <i>try/catch statements,</i> and instead makes use of the {@linkplain ErrorHandler ErrorHandler interface} and lambda expressions.
 *
 * @param <T> The type to be contained if the Result is Ok
 * @param <S> The type to be contained if the Result is an error, which must extend Exception
 */
public class Result<T, S extends Exception>
{
	private final boolean type;
	private T val;
	private S err;

	public Result(T value) {
		type = false;
		val = value;
	}

	public Result(S error) {
		type = true;
		err = error;
	}

	/**
	 * Method that returns the contained type if the Result is Ok, or throws the Exception in case it is an error.
	 *
	 * @return				Value contained inside the Result
	 * @throws Exception	Exception contained inside the Result, in case it is an error
	 */
	public T unwrap() throws Exception {
		if (type) throw err;
		return val;
	}

	/**
	 * Method that returns the contained type if the Result is Ok, or executes the provided lambda expression in case it is an error.
	 *
	 * @param error_handler	Lamda expression to be executed in case the Result is an error. Must extend the ErrorHandler interface
	 * @return				The value contained inside the Result, or <i>null</i> in case the Result is an error
	 * @param <E>			The type of the lambda expression, which must extend the ErrorHandler interface
	 */
	public <E extends ErrorHandler> T unwrap_or(E error_handler) {
		if (type) {
			error_handler.run(err);
			return null;
		}
		return val;
	}

	public T unwrap_or_print() {
		if (type) {
			err.printStackTrace();
			return null;
		}
		return val;
	}
}
