package torch.pickle

/**
 * Exception thrown when something goes wrong with pickling or unpickling.
 *
 * 
 */
@SerialVersionUID(-5870448664938735316L)
class PickleException extends RuntimeException {
  def this(message: String, cause: Throwable) ={
    this()
    new RuntimeException (message, cause)
  }

  def this(message: String) ={
    this()
    new RuntimeException (message)
  }
}