package torch.pickle

import java.nio.ByteBuffer
import java.nio.ByteOrder

object FloatByteConversion {
  def floatArrayToByteArray(floatArray: Array[Float]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 float 数组长度乘以 4（每个 float 占 4 字节）
    val buffer = ByteBuffer.allocate(floatArray.length * 4)
      .order(ByteOrder.nativeOrder())
    // 将 float 数组元素放入 ByteBuffer
    floatArray.foreach(buffer.putFloat)
    // 将 ByteBuffer 中的内容转换为字节数组
    buffer.array()
  }

  def shortArrayToByteArray(shortArray: Array[Short]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 short 数组长度乘以 2（每个 short 占 2 字节）
    val buffer = ByteBuffer.allocate(shortArray.length * 2)
      .order(ByteOrder.nativeOrder())
    // 将 short 数组元素放入 ByteBuffer
    shortArray.foreach(buffer.putShort)
    // 将 ByteBuffer 中的内容转换为字节数组
    buffer.array()
  }

  def intArrayToByteArray(intArray: Array[Int]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 int 数组长度乘以 4（每个 int 占 4 字节）
    val buffer = ByteBuffer.allocate(intArray.length * 4)
      .order(ByteOrder.nativeOrder())
    // 将 int 数组元素放入 ByteBuffer
    intArray.foreach(buffer.putInt)
    // 将 ByteBuffer 中的内容转换为字节数组
    buffer.array()
  }

  def longArrayToByteArray(longArray: Array[Long]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 long 数组长度乘以 8（每个 long 占 8 字节）
    val buffer = ByteBuffer.allocate(longArray.length * 8)
      .order(ByteOrder.nativeOrder())
    // 将 long 数组元素放入 ByteBuffer
    longArray.foreach(buffer.putLong)
    // 将 ByteBuffer 中的内容转换为字节数组
    buffer.array()
  }

  def doubleArrayToByteArray(doubleArray: Array[Double]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 double 数组长度乘以 8（每个 double 占 8 字节）
    val buffer = ByteBuffer.allocate(doubleArray.length * 8)
      .order(ByteOrder.nativeOrder())
    // 将 double 数组元素放入 ByteBuffer
    doubleArray.foreach(buffer.putDouble)
    // 将 ByteBuffer 中的内容转换为字节数组
    buffer.array()
  }

  def byteArrayToFloatArray(byteArray: Array[Byte]): Array[Float] = {
    // 创建一个 ByteBuffer，包装字节数组
    val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
    // 计算 float 数组的长度
    val floatArray = new Array[Float](byteArray.length / 4)
    // 从 ByteBuffer 中读取 float 元素到 float 数组
    buffer.asFloatBuffer().get(floatArray)
    floatArray
  }

  def byteArrayToShortArray(byteArray: Array[Byte]): Array[Short] = {
    // 创建一个 ByteBuffer，包装字节数组
    val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
    // 计算 short 数组的长度
    val shortArray = new Array[Short](byteArray.length / 2)
    // 从 ByteBuffer 中读取 short 元素到 short 数组
    buffer.asShortBuffer().get(shortArray)
    shortArray
  }

  def byteArrayToIntArray(byteArray: Array[Byte]): Array[Int] = {
    // 创建一个 ByteBuffer，包装字节数组
    val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
    // 计算 int 数组的长度
    val intArray = new Array[Int](byteArray.length / 4)
    // 从 ByteBuffer 中读取 int 元素到 int 数组
    buffer.asIntBuffer().get(intArray)
    intArray
  }

  def byteArrayToLongArray(byteArray: Array[Byte]): Array[Long] = {
    // 创建一个 ByteBuffer，包装字节数组
    val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
    // 计算 long 数组的长度
    val longArray = new Array[Long](byteArray.length / 8)
    // 从 ByteBuffer 中读取 long 元素到 long 数组
    buffer.asLongBuffer().get(longArray)
    longArray
  }

  def byteArrayToDoubleArray(byteArray: Array[Byte]): Array[Double] = {
    // 创建一个 ByteBuffer，包装字节数组
    val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
    // 计算 double 数组的长度
    val doubleArray = new Array[Double](byteArray.length / 8)
    // 从 ByteBuffer 中读取 double 元素到 double 数组
    buffer.asDoubleBuffer().get(doubleArray)
    doubleArray
  }

  def main(args: Array[String]): Unit = {
    val originalFloatArray = Array(1.1f, 2.2f, 3.3f, 4.4f)
    // 将 float 数组转换为字节数组
    val byteArray = floatArrayToByteArray(originalFloatArray)

    println(s"Original float array: ${originalFloatArray.mkString(", ")}")
//    println(s"Byte array: ${byteArray.map(_.toHexString).mkString(", ")}")
    println(s"byteArray array: ${byteArray.mkString(", ")}")
    // 将字节数组转换回 float 数组
    val restoredFloatArray = byteArrayToFloatArray(byteArray)

    println(s"Original float array: ${originalFloatArray.mkString(", ")}")
//    println(s"Byte array: ${byteArray.map(_.toHexString).mkString(", ")}")
    println(s"Restored float array: ${restoredFloatArray.mkString(", ")}")
  }
}
