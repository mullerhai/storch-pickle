package torch.pickle.objects

import scala.collection.mutable

/** A dictionary containing just the fields of the class.
  */
@SerialVersionUID(6157715596627049511L)
class ClassDict(modulename: String, var classname: String)
    extends mutable.HashMap[String, AnyRef] {
  classname =
    if (modulename == null) classname else modulename + "." + classname
  this.put("__class__", this.classname)

  /** for the unpickler to restore state
    */
  def __setstate__(values: mutable.HashMap[String, AnyRef]): Unit = {
    this.clear()
    this.put("__class__", this.classname)
    this.++=(values)
//    this.putAll(values)
  }

  /** retrieve the (python) class name of the object that was pickled.
    */
  def getClassName: String = this.classname
}
