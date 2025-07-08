import torch.pickle.Unpickler

import java.nio.file.{Files, Paths}
import scala.collection.mutable

object example {

  def main(args: Array[String]): Unit = {
    // going to pickle a c# datastructure
    val map = new mutable.HashMap[String, Any]
    map.put("apple", 42)
    val path = "D:\\data\\git\\storch-image\\pytorch_muller_model.bin"
//    val path = "D:\\data\\git\\storch-image\\pytorch_model.bin"
    val unpickler = new Unpickler
    val filePath = "D:\\data\\git\\storch-pickle\\testpickle6.dat"
    val paths = Paths.get( path)//"D:\\data\\git\\storch-pickle\\testpickle5.dat")
    val stream = Files.newInputStream(paths)
    val res = unpickler.load(filePath)
//    val res = unpickler.load(path)
    println(res)
  }
}
