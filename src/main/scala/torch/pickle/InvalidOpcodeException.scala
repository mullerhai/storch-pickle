package torch.pickle

/**
 * Exception thrown when the unpickler encounters an invalid opcode.
 *
 * 
 */
@SerialVersionUID(-7691944009311968713L)
class InvalidOpcodeException extends PickleException {
  def this(message: String, cause: Throwable) ={
    this()
    new PickleException(message, cause)
//    super (message, cause)
  }

  def this(message: String)= {
    this()
    new PickleException (message)
  }
}