package torch.pickle

import java.util
import scala.collection.immutable.HashMap

/**
 * Exception thrown that represents a certain Python exception.
 *
 * 
 */
@SerialVersionUID(4884843316742683086L)
class PythonException extends RuntimeException {
  var _pyroTraceback: String = null
  var pythonExceptionType: String = null

  def this(message: String, cause: Throwable) = {
    this()
    RuntimeException (message, cause)
  }

  def this(message: String) = {
    this()
    RuntimeException (message)
  }

  def this(cause: Throwable) = {
    this()
    RuntimeException (cause)
  }

  // special constructor for UnicodeDecodeError
  def this(encoding: String, data: Array[Byte], i1: Integer, i2: Integer, message: String)= {
    this()
    RuntimeException ("UnicodeDecodeError: " + encoding + ": " + message)
  }

  /**
   * called by the unpickler to restore state
   */
  def __setstate__(args: HashMap[String, AnyRef]): Unit = {
    val tb = args.get("_pyroTraceback")
    // if the traceback is a list of strings, create one string from it
    if (tb.isInstanceOf[Seq[?]]) {
      val sb = new StringBuilder
      
      for (line <- tb.asInstanceOf[Seq[?]]) {
        sb.append(line)
      }
      _pyroTraceback = sb.toString
    }
    else _pyroTraceback = tb.asInstanceOf[String]
  }
}