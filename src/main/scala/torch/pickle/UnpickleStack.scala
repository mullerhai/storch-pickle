package torch.pickle

import java.io.Serializable
import java.util
import java.util.Collections

import scala.collection.mutable.ListBuffer

/** Helper type that represents the unpickler working stack.
  */

class UnpickleStack extends Serializable:
  val stack: ListBuffer[Any] = ListBuffer.empty
  val MARKER: Any = new Object()

  def add(o: Any): Unit = stack.append(o)

  def add_mark: Unit = stack.append(MARKER)

  def pop: Any =
    if stack.isEmpty then throw new NoSuchElementException("Stack is empty")
    val result = stack.last
    stack.remove(stack.length - 1)
    result

  def pop_all_since_marker: ListBuffer[Any] =
    val result = ListBuffer[Any]()
    var o = pop
    while o != MARKER do
      result.prepend(o)
      o = pop
    result.reverse

  def peek: Any =
    if stack.isEmpty then throw new NoSuchElementException("Stack is empty")
    stack.last

  def trim(): Unit =
    stack.trimEnd(stack.length - 1)
    // ListBuffer 没有类似 trimToSize 的方法，这里可认为无操作
    ()

  def size(): Int = stack.length

  def clear(): Unit = stack.clear()

@SerialVersionUID(5032718425413805422L)
class UnpickleStacks extends Serializable {
  // any new unique object
  private final var stack: ListBuffer[AnyRef] = new ListBuffer[AnyRef]()
  final var MARKER: AnyRef = null

  def add(o: AnyRef): Unit = this.stack.append(o)

  def add_mark(): Unit = this.stack.append(this.MARKER)

  def pop: AnyRef = {
    val size = this.stack.size
    val result = this.stack(size - 1)
    this.stack.remove(size - 1)
    result
  }

  def pop_all_since_marker: ListBuffer[AnyRef] = {
    val result = new ListBuffer[AnyRef]
    var o = pop
    while (o != this.MARKER) {
      result.append(o)
      o = pop
    }
//    result.trimToSize()
//    Collections.reverse(result)
    result.reverse
  }

  def peek: AnyRef = this.stack(this.stack.size - 1)

  def trim(): Unit = this.stack.toArray // .trimToSize()

  def size: Int = this.stack.size

  def clear(): Unit = {
    this.stack.clear()
    this.stack.toArray // .trimToSize()
  }
}
