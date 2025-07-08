import java.nio.ByteBuffer
import java.nio.ByteOrder

object FloatByteConversion {
  def floatArrayToByteArray(floatArray: Array[Float]): Array[Byte] = {
    // 创建一个 ByteBuffer，容量为 float 数组长度乘以 4（每个 float 占 4 字节）
    val buffer = ByteBuffer.allocate(floatArray.length * 4).order(ByteOrder.nativeOrder())
    // 将 float 数组元素放入 ByteBuffer
    floatArray.foreach(buffer.putFloat)
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
