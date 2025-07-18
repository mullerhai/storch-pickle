package torch.pickle

/** Interface for Object Constructors that are used by the unpickler to create
  * instances of non-primitive or custom classes.
  */
trait IObjectConstructor {

  /** Create an object. Use the given args as parameters for the constructor.
    */
  @throws[PickleException]
  def construct(args: Array[AnyRef]): AnyRef
}
