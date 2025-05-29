package torch.pickle.objects

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import torch.pickle.IObjectConstructor

/** This object constructor creates sets.
  */
class SetConstructor extends IObjectConstructor {
//  override def construct(args: Array[AnyRef]): AnyRef = {
//    // create a HashSet, args=arraylist of stuff to put in it
//    @SuppressWarnings(Array("unchecked"))
//    val data = args(0).asInstanceOf[ListBuffer[AnyRef]]
//    new mutable.HashSet[AnyRef](data)
//  }

  override def construct(args: Array[AnyRef]): AnyRef =
    // 假设 args 的第一个元素是一个 ListBuffer，将其转换为 ListBuffer[AnyRef]
    val data = args.headOption match
      case Some(list) => list.asInstanceOf[ListBuffer[AnyRef]]
      case None => throw new IllegalArgumentException("Args array is empty.")
    // 创建一个空的 HashSet 并添加 data 中的元素
    val set = mutable.HashSet.empty[AnyRef]
    set ++= data
    set
}
