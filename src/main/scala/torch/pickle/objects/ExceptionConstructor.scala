package torch.pickle.objects

import java.lang.reflect.Constructor
import java.lang.reflect.Field

import torch.pickle.IObjectConstructor
import torch.pickle.PickleException

/** This creates Python Exception instances. It keeps track of the original
  * Python exception type name as well.
  */
class ExceptionConstructor(
    private val types: Class[?],
    module: String,
    name: String,
) extends IObjectConstructor {

  private final var pythonExceptionType: String =
    if (module != null) module + "." + name else name

  override def construct(param: Array[AnyRef]): AnyRef =
    try {
      var args = param
      if (pythonExceptionType != null)
        // put the python exception type somewhere in the message
        if (args == null || args.length == 0)
          args = Array[AnyRef]("[" + pythonExceptionType + "]")
        else {
          val msg = "[" + pythonExceptionType + "] " + args(0)
          args = Array[AnyRef](msg)
        }
      val paramtypes = new Array[Class[?]](args.length)
      for (i <- 0 until args.length) paramtypes(i) = args(i).getClass
      val cons = types.getConstructor(paramtypes*)
      val ex = cons.newInstance(args)
      try {
        val prop = ex.getClass.getField("pythonExceptionType")
        prop.set(ex, pythonExceptionType)
      } catch {
        case x: NoSuchFieldException =>

        // meh.
      }
      ex
    } catch {
      case x: Exception =>
        throw new PickleException("problem construction object: " + x)
    }
}
