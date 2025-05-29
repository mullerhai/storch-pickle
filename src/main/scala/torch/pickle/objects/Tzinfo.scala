package torch.pickle.objects

import java.util.TimeZone

import scala.collection.mutable

import torch.pickle.PickleException

/** Timezone offset class that implements __setstate__ for the unpickler to
  * track what TimeZone a dateutil.tz.tzoffset or tzutc should unpickle to
  */
class Tzinfo {

  private final var forceTimeZone = false
  private var timeZone: TimeZone = null

  def this(timeZone: TimeZone) = {
    this()
    this.forceTimeZone = true
    this.timeZone = timeZone
  }

  def getTimeZone: TimeZone = this.timeZone

  /** called by the unpickler to restore state
    */
  def __setstate__(args: mutable.HashMap[String, AnyRef]): Unit = {
    if (this.forceTimeZone) return
    throw new PickleException(
      "unexpected pickle data for tzinfo objects: can't __setstate__ with anything other than an empty dict, anything else is unimplemented",
    )
  }
}
