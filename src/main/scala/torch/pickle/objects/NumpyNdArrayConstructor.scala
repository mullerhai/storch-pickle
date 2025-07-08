package torch.pickle.objects

import torch.numpy.enums.{DType, Order}
import torch.numpy.enums.DType.Float16
import torch.numpy.serve.Numpy
import torch.pickle.{IObjectConstructor, PickleException}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream, ObjectOutputStream}
import scala.collection.mutable

class NumpyNdDataConstructor extends IObjectConstructor {


  /** Create an object. Use the given args as parameters for the constructor.
   */
  override def construct(args: Array[AnyRef]): AnyRef = {
    val NumpyNdArray = args(0).asInstanceOf[Boolean]
    val fortran_order = args(1).asInstanceOf[Object]
    val shape = args(2).asInstanceOf[AnyRef]
    println("\r\n data  NumpyNdArray: " + NumpyNdArray + "  fortran_order: " + fortran_order + s"  shape: ${shape} \r\n ")
    None
  }
}
class NumpyNdMultiArrayConstructor extends IObjectConstructor {


  /** Create an object. Use the given args as parameters for the constructor.
   */
  override def construct(args: Array[AnyRef]): AnyRef = {
    val NumpyNdArray = args(0).asInstanceOf[NumpyNdArrayConstructor]
    val shape = args(1).asInstanceOf[Object]
    val dtype = args(2).toString//NumpyNdArrayDTypeConstructor]
    println("args.length: " + args.length)
    if (args(2).isInstanceOf[Array[Byte]]) {
      println(s"args(2) is bytearray , length ${args(2).asInstanceOf[Array[Byte]].length}")
      val byteArray = args(2).asInstanceOf[Array[Byte]]


      //        target = target.getClass.getMethod("apply", classOf[Array[AnyRef]]).invoke(target, argsArray)
    }
    println(s"NumpyNdMultiArrayConstructor args->:  ${args.mkString("  &&  ")}")
    println("\r\n NumpyNdArray: " + NumpyNdArray + "  shape: " + shape + s"  dtype: ${dtype} \r\n ")
    NumpyNdarray(args(2).isInstanceOf[Array[Byte]] match {
      case true => args(2).asInstanceOf[Array[Byte]]
      case false => null
    }, true, NumpyDtype("f4", false, false), Seq(1, 2, 3), 1)
  }
}

case class NumpyNdarrayRaw(shape: Seq[Int], dtype: NumpyDtype, buffer: Option[Array[Byte]] = None, offset : Int= 0, strides: Option[Seq[Int]] =None, order: Option[Order]= None )
//https://github.com/sbinet/npyio/pull/22/files
//subtype, descr, shape, strides, data, flags)
case class NumpyNdarray(data: Array[Byte], offset: Boolean, dtype: NumpyDtype, shape: Seq[Int], strides: Int = 1) {

  def __setstate__(args: Array[Object] ): Unit = {
    println("Muller NumpyNdarray.__setstate__ args: " + args)
    NumpyNdarray(shape = Seq(1, 2, 3), dtype = NumpyDtype("f4", false, false), data = null, offset = true, strides = 1)
  }
}

//(data:Array[Float],shape:Array[Int])
class NumpyNdArrayConstructor extends IObjectConstructor  {

  /** Create an object. Use the given args as parameters for the constructor.
   */
  override def construct(args: Array[AnyRef]): AnyRef = {
    println("args.length: " + args.length)
    println(s"NumpyNdArrayConstructor args->:  ${args.mkString("  &&  ")}")
    println("args(0): " + args(0).getClass.getName)
    println("args(1): " + args(1).getClass.getName)
    println("args(2): " + args(2).getClass.getName)


    NumpyNdarrayRaw(shape = Seq(1, 2, 3), dtype = NumpyDtype("f4", false, false))
  }



}
//align
//case class NumpyDtype(descr: String, fortran_order: Boolean, shape: Boolean)

case class NumpyDtype(dtype: String, align: Boolean = false, copy: Boolean = false) {

  //mutable.HashMap[String, AnyRef]
  def __setstate__(args: Array[Object] ): Unit = {
//    if (this.forceTimeZone) return
//    throw new PickleException(
//      " anything other than an empty dict, anything else is unimplemented",
//    )
    println("Muller NumpyDtype.__setstate__ args: " + args)
    NumpyDtype("f4", false, false)
  }
}

class NumpyNdArrayDTypeConstructor extends IObjectConstructor {

  /** Create an object. Use the given args as parameters for the constructor.
   */
  override def construct(args: Array[AnyRef]): AnyRef = {
    val dtype = args(0).asInstanceOf[String]
    val align = args(1).asInstanceOf[Boolean]
    val copy = args(2).asInstanceOf[Boolean]
    println("\r\n dtype: " + dtype + "  align: " + align + s"  copy: ${copy} \r\n ")
    NumpyDtype(dtype, align, copy)
  }
}


//    subtype = args[0] // ex: numpy.ndarray
// shape   = args[1] // a tuple, usually (0,)
// dtype   = args[2] // a dummy dtype (usually "b")
//    args(0).asInstanceOf[Array[Byte]].foreach(println)
//    val bos = new ByteArrayOutputStream()
//    val oos = new ObjectOutputStream(bos)
//    oos.writeObject(args(0))
//    oos.close()
//    val byteArray = bos.toByteArray
//    // 将字节数组转换为 InputStream
//    val inputStream = new ByteArrayInputStream(byteArray)
//    val npArray  =new Numpy(inputStream)
//    args(0).asInstanceOf[Numpy[?]]