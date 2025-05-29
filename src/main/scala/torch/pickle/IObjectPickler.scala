package torch.pickle

import java.io.IOException
import java.io.OutputStream

/** Interface for Object Picklers used by the pickler, to pickle custom classes.
  */
trait IObjectPickler {

  /** Pickle an object.
    */
  @throws[PickleException]
  @throws[IOException]
  def pickle(o: Any, out: OutputStream, currentPickler: Pickler): Unit
}
