package torch.pickle.objects

import scala.collection.mutable.ArrayBuffer

import torch.pickle.IObjectConstructor
import torch.pickle.PickleException
import torch.pickle.PickleUtils

/** Creates arrays of objects. Returns a primitive type array such as Int Array
  * if the objects are Ints, etc. Returns an ArrayBuffer[Any] if it needs to
  * contain arbitrary objects (such as lists).
  */
class ArrayConstructor extends IObjectConstructor:

  override def construct(args: Array[AnyRef]): AnyRef = args.length match
    case 4 =>
      val constructor = args(0).asInstanceOf[ArrayConstructor]
      val typecode = args(1).asInstanceOf[String].charAt(0)
      val machinecodeType = args(2).asInstanceOf[Int]
      val data = args(3).asInstanceOf[Array[Byte]]
      constructor.construct(typecode, machinecodeType, data)
    case 2 =>
      val typecode = args(0).asInstanceOf[String]
      args(1) match
        case s: String =>
          throw PickleException("unsupported Python 2.6 array pickle format")
        case values: ArrayBuffer[Any] => typecode.charAt(0) match
            case 'c' | 'u' =>
              val result = new Array[Char](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[String].charAt(0)
              result.asInstanceOf[AnyRef]
            case 'b' =>
              val result = new Array[Byte](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].byteValue()
              result.asInstanceOf[AnyRef]
            case 'B' | 'h' =>
              val result = new Array[Short](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].shortValue()
              result.asInstanceOf[AnyRef]
            case 'H' | 'i' | 'l' =>
              val result = new Array[Int](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].intValue()
              result.asInstanceOf[AnyRef]
            case 'I' | 'L' =>
              val result = new Array[Long](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].longValue()
              result.asInstanceOf[AnyRef]
            case 'f' =>
              val result = new Array[Float](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].floatValue()
              result.asInstanceOf[AnyRef]
            case 'd' =>
              val result = new Array[Double](values.size)
              for (i <- values.indices)
                result(i) = values(i).asInstanceOf[Number].doubleValue()
              result.asInstanceOf[AnyRef]
            case _ => throw PickleException(s"invalid array typecode: $typecode")
    case _ => throw PickleException(
        s"invalid pickle data for array; expected 2 args, got ${args.length}",
      )

  def construct(typecode: Char, machinecode: Int, data: Array[Byte]): AnyRef =
    if machinecode < 0 then throw PickleException("unknown machine type format")

    typecode match
      case 'c' | 'u' =>
        if machinecode != 18 && machinecode != 19 && machinecode != 20 &&
          machinecode != 21
        then throw PickleException("for c/u type must be 18/19/20/21")
        if machinecode == 18 || machinecode == 19 then
          if data.length % 2 != 0 then
            throw PickleException("data size alignment error")
          constructCharArrayUTF16(machinecode, data)
        else
          if data.length % 4 != 0 then
            throw PickleException("data size alignment error")
          constructCharArrayUTF32(machinecode, data)
      case 'b' =>
        if machinecode != 1 then throw PickleException("for b type must be 1")
        data.asInstanceOf[AnyRef]
      case 'B' =>
        if machinecode != 0 then throw PickleException("for B type must be 0")
        constructShortArrayFromUByte(data)
      case 'h' =>
        if machinecode != 4 && machinecode != 5 then
          throw PickleException("for h type must be 4/5")
        if data.length % 2 != 0 then
          throw PickleException("data size alignment error")
        constructShortArraySigned(machinecode, data)
      case 'H' =>
        if machinecode != 2 && machinecode != 3 then
          throw PickleException("for H type must be 2/3")
        if data.length % 2 != 0 then
          throw PickleException("data size alignment error")
        constructIntArrayFromUShort(machinecode, data)
      case 'i' =>
        if machinecode != 8 && machinecode != 9 then
          throw PickleException("for i type must be 8/9")
        if data.length % 4 != 0 then
          throw PickleException("data size alignment error")
        constructIntArrayFromInt32(machinecode, data)
      case 'l' =>
        if machinecode != 8 && machinecode != 9 && machinecode != 12 &&
          machinecode != 13
        then throw PickleException("for l type must be 8/9/12/13")
        if (machinecode == 8 || machinecode == 9) && data.length % 4 != 0 then
          throw PickleException("data size alignment error")
        if (machinecode == 12 || machinecode == 13) && data.length % 8 != 0 then
          throw PickleException("data size alignment error")
        if machinecode == 8 || machinecode == 9 then
          constructIntArrayFromInt32(machinecode, data)
        else constructLongArrayFromInt64(machinecode, data)
      case 'I' =>
        if machinecode != 6 && machinecode != 7 then
          throw PickleException("for I type must be 6/7")
        if data.length % 4 != 0 then
          throw PickleException("data size alignment error")
        constructLongArrayFromUInt32(machinecode, data)
      case 'L' =>
        if machinecode != 6 && machinecode != 7 && machinecode != 10 &&
          machinecode != 11
        then throw PickleException("for L type must be 6/7/10/11")
        if (machinecode == 6 || machinecode == 7) && data.length % 4 != 0 then
          throw PickleException("data size alignment error")
        if (machinecode == 10 || machinecode == 11) && data.length % 8 != 0 then
          throw PickleException("data size alignment error")
        if machinecode == 6 || machinecode == 7 then
          constructLongArrayFromUInt32(machinecode, data)
        else constructLongArrayFromUInt64(machinecode, data)
      case 'f' =>
        if machinecode != 14 && machinecode != 15 then
          throw PickleException("for f type must be 14/15")
        if data.length % 4 != 0 then
          throw PickleException("data size alignment error")
        constructFloatArray(machinecode, data)
      case 'd' =>
        if machinecode != 16 && machinecode != 17 then
          throw PickleException("for d type must be 16/17")
        if data.length % 8 != 0 then
          throw PickleException("data size alignment error")
        constructDoubleArray(machinecode, data)
      case _ => throw PickleException(s"invalid array typecode: $typecode")

  protected def constructIntArrayFromInt32(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Int] =
    val result = new Array[Int](data.length / 4)
    val bigendian = new Array[Byte](4)
    for (i <- 0 until data.length / 4)
      if machinecode == 8 then
        result(i) = PickleUtils.bytes_to_integer(data, i * 4, 4)
      else
        bigendian(0) = data(3 + i * 4)
        bigendian(1) = data(2 + i * 4)
        bigendian(2) = data(1 + i * 4)
        bigendian(3) = data(0 + i * 4)
        result(i) = PickleUtils.bytes_to_integer(bigendian)
    result

  protected def constructLongArrayFromUInt32(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Long] =
    val result = new Array[Long](data.length / 4)
    val bigendian = new Array[Byte](4)
    for (i <- 0 until data.length / 4)
      if machinecode == 6 then result(i) = PickleUtils.bytes_to_uint(data, i * 4)
      else
        bigendian(0) = data(3 + i * 4)
        bigendian(1) = data(2 + i * 4)
        bigendian(2) = data(1 + i * 4)
        bigendian(3) = data(0 + i * 4)
        result(i) = PickleUtils.bytes_to_uint(bigendian, 0)
    result

  protected def constructLongArrayFromUInt64(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Long] =
    throw PickleException("unsupported datatype: 64-bits unsigned long")

  protected def constructLongArrayFromInt64(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Long] =
    val result = new Array[Long](data.length / 8)
    val bigendian = new Array[Byte](8)
    for (i <- 0 until data.length / 8)
      if machinecode == 12 then
        result(i) = PickleUtils.bytes_to_long(data, i * 8)
      else
        bigendian(0) = data(7 + i * 8)
        bigendian(1) = data(6 + i * 8)
        bigendian(2) = data(5 + i * 8)
        bigendian(3) = data(4 + i * 8)
        bigendian(4) = data(3 + i * 8)
        bigendian(5) = data(2 + i * 8)
        bigendian(6) = data(1 + i * 8)
        bigendian(7) = data(0 + i * 8)
        result(i) = PickleUtils.bytes_to_long(bigendian, 0)
    result

  protected def constructDoubleArray(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Double] =
    val result = new Array[Double](data.length / 8)
    val bigendian = new Array[Byte](8)
    for (i <- 0 until data.length / 8)
      if machinecode == 17 then
        result(i) = PickleUtils.bytes_to_double(data, i * 8)
      else
        bigendian(0) = data(7 + i * 8)
        bigendian(1) = data(6 + i * 8)
        bigendian(2) = data(5 + i * 8)
        bigendian(3) = data(4 + i * 8)
        bigendian(4) = data(3 + i * 8)
        bigendian(5) = data(2 + i * 8)
        bigendian(6) = data(1 + i * 8)
        bigendian(7) = data(0 + i * 8)
        result(i) = PickleUtils.bytes_to_double(bigendian, 0)
    result

  protected def constructFloatArray(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Float] =
    val result = new Array[Float](data.length / 4)
    val bigendian = new Array[Byte](4)
    for (i <- 0 until data.length / 4)
      if machinecode == 15 then
        result(i) = PickleUtils.bytes_to_float(data, i * 4)
      else
        bigendian(0) = data(3 + i * 4)
        bigendian(1) = data(2 + i * 4)
        bigendian(2) = data(1 + i * 4)
        bigendian(3) = data(0 + i * 4)
        result(i) = PickleUtils.bytes_to_float(bigendian, 0)
    result

  protected def constructIntArrayFromUShort(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Int] =
    val result = new Array[Int](data.length / 2)
    for (i <- 0 until data.length / 2) {
      val b1 = data(0 + i * 2) & 0xff
      val b2 = data(1 + i * 2) & 0xff
      if machinecode == 2 then result(i) = b2 << 8 | b1
      else result(i) = b1 << 8 | b2
    }
    result

  protected def constructShortArraySigned(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Short] =
    val result = new Array[Short](data.length / 2)
    for (i <- 0 until data.length / 2) {
      val b1 = data(0 + i * 2)
      val b2 = data(1 + i * 2)
      if machinecode == 4 then result(i) = (b2 << 8 | b1 & 0xff).toShort
      else result(i) = (b1 << 8 | b2 & 0xff).toShort
    }
    result

  protected def constructShortArrayFromUByte(data: Array[Byte]): Array[Short] =
    val result = new Array[Short](data.length)
    for (i <- data.indices) result(i) = (data(i) & 0xff).toShort
    result

  protected def constructCharArrayUTF32(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Char] =
    val result = new Array[Char](data.length / 4)
    val bigendian = new Array[Byte](4)
    for (index <- 0 until data.length / 4)
      if machinecode == 20 then
        val codepoint = PickleUtils.bytes_to_integer(data, index * 4, 4)
        val cc = Character.toChars(codepoint)
        if cc.length > 1 then
          throw PickleException(
            s"cannot process UTF-32 character codepoint $codepoint",
          )
        result(index) = cc(0)
      else
        bigendian(0) = data(3 + index * 4)
        bigendian(1) = data(2 + index * 4)
        bigendian(2) = data(1 + index * 4)
        bigendian(3) = data(index * 4)
        val codepoint = PickleUtils.bytes_to_integer(bigendian)
        val cc = Character.toChars(codepoint)
        if cc.length > 1 then
          throw PickleException(
            s"cannot process UTF-32 character codepoint $codepoint",
          )
        result(index) = cc(0)
    result

  protected def constructCharArrayUTF16(
      machinecode: Int,
      data: Array[Byte],
  ): Array[Char] =
    val result = new Array[Char](data.length / 2)
    val bigendian = new Array[Byte](2)
    for (index <- 0 until data.length / 2)
      if machinecode == 18 then
        result(index) = PickleUtils.bytes_to_integer(data, index * 2, 2).toChar
      else
        bigendian(0) = data(1 + index * 2)
        bigendian(1) = data(0 + index * 2)
        result(index) = PickleUtils.bytes_to_integer(bigendian).toChar
    result
