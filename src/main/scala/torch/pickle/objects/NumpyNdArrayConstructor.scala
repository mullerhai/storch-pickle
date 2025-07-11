package torch.pickle.objects

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.ObjectOutputStream

import scala.collection.mutable

import torch.numpy.enums.DType
import torch.numpy.enums.DType.Float16
import torch.numpy.enums.Order
import torch.numpy.matrix.NDArray
import torch.numpy.serve.Numpy
import torch.pickle.FloatByteConversion
import torch.pickle.IObjectConstructor
import torch.pickle.PickleException

class NumpyNdMultiArrayConstructor extends IObjectConstructor {

  /** Create an object. Use the given args as parameters for the constructor.
    */
  override def construct(args: Array[AnyRef]): AnyRef = {
    val ndArrayConstructor = args(0).asInstanceOf[NumpyNdArrayConstructor]
    val shape = args(1).asInstanceOf[Array[Object]]
    val dtype = args(2).asInstanceOf[Array[Byte]] // NumpyNdArrayDTypeConstructor]
    println(
      "\r\n ndArrayConstructor: " + ndArrayConstructor + "  shape: " + shape +
        s"  dtype: $dtype \r\n ",
    )
    println(s"NumpyNdMultiArrayConstructor  NdMultiArray Constructor INVOKE SUCCESS  args(2) is bytearray ${args(2).isInstanceOf[Array[Byte]]} args->:  ${args.mkString(", ")} ")
    MulitNumpyNdArray(ndArrayConstructor.numpyNdarray, shape, dtype, null)
  }
}

case class MulitNumpyNdArray(
    numpyNdarray: NumpyNdarray,
    shape: Array[Object],
    dtype: Array[Byte],
    data: NumpyNdarray,
) {

  def __setstate__(args: Array[Object]): MulitNumpyNdArray = {
    println(
      s"MulitNumpyNdArray __setstate__ try to load   ....args array:->  ${args
          .mkString(", ")}",
    )
    val data = args(0).asInstanceOf[Array[Byte]]
    val bool1 = args(1).asInstanceOf[Boolean]
    val dtype = args(2).asInstanceOf[NumpyDtype]
    val shape = args(3).asInstanceOf[Array[Object]].map(_.asInstanceOf[Int])
    val strides = args(4).asInstanceOf[Int]
    println("Muller MulitNumpyNdArray.__setstate__ args: " + args)
    val array = NumpyNdarray(
      shape = shape.toSeq,
      dtype = dtype,
      data = data,
      offset = bool1,
      strides = strides,
    )

    println(s"MulitNumpyNdArray case  class __setstate__ INVOKE SUCCESS shape ${array
      .shape.mkString(", ")}")
    MulitNumpyNdArray(
      numpyNdarray = array,
      shape = this.shape,
      dtype = this.dtype,
      data = array,
    )
  }
}

//https://github.com/sbinet/npyio/pull/22/files
//subtype, descr, shape, strides, data, flags)
case class NumpyNdarray(
    data: Array[Byte],
    offset: Boolean,
    dtype: NumpyDtype,
    shape: Seq[Int],
    strides: Int = 1,
) {

  def toNDArray[T](): NDArray[T] = {
    val dataType = dtype.dtype match {
      case "f4" => DType.Float32
      case "f8" => DType.Float64
      case "i4" => DType.Int32
      case "i8" => DType.Int64
      case "u1" => DType.UInt8
      case "u2" => DType.UInt16
      case "u4" => DType.UInt32
      case "u8" => DType.UInt64
      case "b1" => DType.Bool
      case "f2" => DType.Float16
      case _ => throw new IllegalArgumentException(
          s"Unsupported data type: ${dtype.dtype}",
        )
    }
    val ndData = dtype.dtype match {
      case "f2" => FloatByteConversion
          .byteArrayToFloatArray(data.asInstanceOf[Array[Byte]])
      case "f4" => FloatByteConversion
          .byteArrayToFloatArray(data.asInstanceOf[Array[Byte]])
      case "f8" => FloatByteConversion
          .byteArrayToDoubleArray(data.asInstanceOf[Array[Byte]])
      case "i2" => FloatByteConversion
          .byteArrayToShortArray(data.asInstanceOf[Array[Byte]])
      case "i4" => FloatByteConversion
          .byteArrayToIntArray(data.asInstanceOf[Array[Byte]])
      case "i8" => FloatByteConversion
          .byteArrayToLongArray(data.asInstanceOf[Array[Byte]])
      case "u1" => data
      case "u2" => FloatByteConversion
          .byteArrayToShortArray(data.asInstanceOf[Array[Byte]])
      case "u4" => FloatByteConversion
          .byteArrayToIntArray(data.asInstanceOf[Array[Byte]])
      case "u8" => FloatByteConversion
          .byteArrayToLongArray(data.asInstanceOf[Array[Byte]])
      case "b1" => data.asInstanceOf[Array[Boolean]]
      case _ => throw new IllegalArgumentException(
          s"Unsupported data type: ${dtype.dtype}",
        )
    }
    val ndArray = new NDArray[T](
      data = ndData.asInstanceOf[Array[T]],
      shape = shape,
      ndim = shape.size,
      dType = dataType,
    )
    ndArray.reshape(shape*)
  }

  def __setstate__(args: Array[Object]): Unit = {
    println(
      s"NumpyNDArray __setstate__ ....args array ${args.mkString(", ")} index 3 class ${args(3).getClass.getName}"
    )
    val data = args(0).asInstanceOf[Array[Byte]]
    val offset = args(1).asInstanceOf[Boolean]
    val dtype = args(2).asInstanceOf[NumpyDtype]
    val shape = args(3).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[Int])
    val strides = args(4).asInstanceOf[Int]
    println(s"NumpyNdArray case  class __setstate__ INVOKE SUCCESS")
    NumpyNdarray(
      shape = shape.toSeq,
      dtype = dtype,
      data = data,
      offset = offset,
      strides = strides,
    )

  }
}

//2025/07/08 10:36:55 loading "./data_batch_1"...
//2025/07/08 10:36:55 got: *types.Dict
//{batch_label: training batch 1 of 5, labels: [6, 9, 9, 4, 1, 1, 2, 7, 8, 3, 4, 7, 7, 2, 9, 9, 9, 3,
//2, 6, 4, 3, 6, 6, 2, 6, 3, 5, 4, 0, 0, 9, 1, 3, 4, 0, 3, 7, 3, 3, 5, 2, 2, 7, 1, 1, 1, 2, 2, 0, 9, 5, 7, 9,
//[...], 1, 1, 5],
//data: Array{descr: ArrayDescr{kind: 'u', order: '|', flags: 0, esize: 1, align: 1,
// subarr: <nil>, names: [], fields: {}, meta: map[]}, shape: [10000 3072], strides: [3072 1],
// fortran: false, data: []},
//filenames: [leptodactylus_pentadactylus_s_000004.png, ...,
//rana_temporaria_s_000775.png, tabby_s_002228.png, truck_s_000036.png,
//car_s_002296.png, estate_car_s_001433.png, cur_s_000170.png]}
//args  { 0 0 ,1 -1 2,-1 , 6 "|" 7 3 }
//args  0,-1,-1,null,null,null,|,3
//(data:Array[Float],shape:Array[Int])
class NumpyNdArrayConstructor extends IObjectConstructor {

  var numpyNdarray: NumpyNdarray = null

  /** Create an object. Use the given args as parameters for the constructor.
    */
  override def construct(args: Array[AnyRef]): AnyRef = {

    println(s"NumpyNdArrayConstructor try to load args->:  ${args.mkString(", ")}")
    val data = args(0).asInstanceOf[Array[Byte]]
    val offset = args(1).asInstanceOf[Boolean]
    val dtype = args(2).asInstanceOf[NumpyDtype]
    val shape = args(3).asInstanceOf[Array[AnyRef]].map(_.asInstanceOf[Int])
    val strides = args(4).asInstanceOf[Int]
    val dataArray = NumpyNdarray(
      shape = shape.toSeq,
      dtype = dtype,
      data = data,
      offset = offset,
      strides = strides,
    )
    this.numpyNdarray = dataArray
    println(s"NumpyNdArrayConstructor create numpy array success : ${this.numpyNdarray}")
    this.numpyNdarray

//    NumpyNdarrayRaw(shape = Seq(1, 3), dtype = NumpyDtype("f4-ndcon", false, false))
  }

}
//align
//case class NumpyDtype(descr: String, fortran_order: Boolean, shape: Boolean)

case class NumpyDtype(
    dtype: String,
    align: Boolean = false,
    copy: Boolean = false,
    metadata: Option[mutable.HashMap[String, Any]] = None,
) {

  // mutable.HashMap[String, AnyRef]
  def __setstate__(args: Array[Object]): Unit = {
//    if (this.forceTimeZone) return
//    throw new PickleException(
//      " anything other than an empty dict, anything else is unimplemented",
//    )
//    try to load numpyDtype... from __setstate__ args 152, 4, 424, HashMap(Some(data) -> [Ljava.lang.Object;@11a9e7c8, Some(names) -> [Ljava.lang.Object;@3901d134, Some(metadata) -> [Ljava.lang.Object;@14d3bc22), [Ljava.lang.Object;@12d4bf7e, null, |, 3.
// dtype: V424  align: false  copy: true
    // args 0, -1, -1, null, null, null, <, 3.
    // try to load numpyDtype... from __setstate__ args 0, -1, -1, null, null, null, |, 3.
    println(s"try to load numpyDtype... from __setstate__ args ${args
        .asInstanceOf[Array[?]].mkString(", ")}.")
    println(s"NumpyDtype case  class __setstate__ INVOKE SUCCESS")
//    val dtype = args(0).asInstanceOf[String]
//    val align = args(1).asInstanceOf[Boolean]
//    val copy = args(2).asInstanceOf[Boolean]
//    println("Muller NumpyDtype.__setstate__ args: " + args)
//    NumpyDtype("f4ddd", false, false)
//    NumpyDtype(dtype, align, copy)
  }
}

class NumpyNdArrayDTypeConstructor extends IObjectConstructor {

  /** Create an object. Use the given args as parameters for the constructor.
    */
  override def construct(args: Array[AnyRef]): AnyRef = {

    val dtype = args(0).asInstanceOf[String]
    val align = args(1).asInstanceOf[Boolean]
    val copy = args(2).asInstanceOf[Boolean]
    println(
      "\r\n dtype: " + dtype + "  align: " + align + s"  copy: $copy \r\n ",
    )
    println(s"NumpyNdArray DType Constructor INVOKE SUCCESS")
    NumpyDtype(dtype, align, copy)
  }
}

class NumpyNdDataConstructor extends IObjectConstructor {

  /** Create an object. Use the given args as parameters for the constructor.
    */
  override def construct(args: Array[AnyRef]): AnyRef = {
    val NumpyNdArray = args(0).asInstanceOf[Boolean]
    val fortran_order = args(1).asInstanceOf[Object]
    val shape = args(2).asInstanceOf[AnyRef]
    println(
      "\r\n data  NumpyNdArray: " + NumpyNdArray + "  fortran_order: " +
        fortran_order + s"  shape: $shape \r\n ",
    )
    None
  }
}

case class NumpyNdarrayRaw(
    shape: Seq[Int],
    dtype: NumpyDtype,
    buffer: Option[Array[Byte]] = None,
    offset: Int = 0,
    strides: Option[Seq[Int]] = None,
    order: Option[Order] = None,
) {

  def __setstate__(args: Array[Object]): Unit = {
    println(s"try to load numpyNDArray....args array ${args.mkString("  &&  ")}")
    val shape = args(0).asInstanceOf[Array[Int]]
    val dtype = args(1).asInstanceOf[NumpyDtype]

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
