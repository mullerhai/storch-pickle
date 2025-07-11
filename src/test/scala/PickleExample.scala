import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

import torch.pickle.Pickler
import torch.pickle.Unpickler

object PickleExamplee {
  @throws[IOException]
  def main(args: Array[String]): Unit = {
//     going to pickle a c# datastructure
    val map = new mutable.HashMap[String, Any]
    map.put("apple", 42)
    map.put("microsoft", "hello")
    val mk = Seq(34, 12, 45)
    map.put("mk", mk)
//    val mk2 = List.of(12.5f, 34.9f, 20f, 902f)
    val mk3 = Seq(12.5f, 34.9f, 20f, 902f)
    map.put("listMk", mk3)
    val values = new mutable.ListBuffer[Double]
    values.append(23.11)
    values.append(1.13)
//    (1 to 100).foreach(i => values.append(3.444) )
    values.append(1.11)
    values.append(2.22)
    values.append(3.33)
    values.append(4.44)
    values.append(5.55)
    map.put("listvalues", values.toSeq)
    // You can add many other types if you like. See the readme about the type mappings.
    val pickleFilename = "testpickle6.dat"
    System.out.println("Writing pickle to '" + pickleFilename + "'")
    val pickler = new Pickler(true)
//    val fos = new FileOutputStream(pickleFilename)
//    pickler.dump(map, fos)
//    fos.close()
    // pickler.save(map,pickleFilename)
    System.out.println("Done. Try unpickling it in python.\n")
    System.out.println("Reading a pickle created in python...")

    // the following pickle was created in Python 3.4.
    // it is this data:     [1, 2, 3, (11, 12, 13), {'banana', 'grape', 'apple'}]
    val pythonpickle = Array[Byte](
      128.toByte,
      4,
      149.toByte,
      48,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      93,
      148.toByte,
      40,
      75,
      1,
      75,
      2,
      75,
      3,
      75,
      11,
      75,
      12,
      75,
      13,
      135.toByte,
      148.toByte,
      143.toByte,
      148.toByte,
      40,
      140.toByte,
      6,
      98,
      97,
      110,
      97,
      110,
      97,
      148.toByte,
      140.toByte,
      5,
      103,
      114,
      97,
      112,
      101,
      148.toByte,
      140.toByte,
      5,
      97,
      112,
      112,
      108,
      101,
      148.toByte,
      144.toByte,
      101,
      46,
    )
    val unpickler = new Unpickler
    val filePath = "D:\\data\\git\\storch-pickle\\testpickle6.dat"
    val path = Paths.get("D:\\data\\git\\storch-pickle\\testpickle5.dat")
    val stream = Files.newInputStream(path)
    val res = unpickler.load(filePath)
    println(s"res $res")
    val resMap = res.asInstanceOf[mutable.HashMap[String, ?]]
    resMap.get("listvalues").get.asInstanceOf[ListBuffer[Double]]
      .foreach(ele => println(ele))
    resMap.get("listMk").get.asInstanceOf[ListBuffer[Double]]
      .foreach(ele => println(ele))
    println(s"res Map size ${resMap.size}")
    val result = unpickler.loads(pythonpickle)
    println(result)
//    System.out.println("type: " + result.getClass)
//    val list = result.asInstanceOf[java.util.List[_]]
//    val integer1 = list.get(0).asInstanceOf[Integer]
//    val integer2 = list.get(1).asInstanceOf[Integer]
//    val integer3 = list.get(2).asInstanceOf[Integer]
//    val tuple = list.get(3).asInstanceOf[Array[AnyRef]]
//    val set = list.get(4).asInstanceOf[java.util.Set[_]]
//    System.out.println("1-3: integers: " + integer1 + "," + integer2 + "," + integer3)
//    System.out.println("4: tuple: (" + tuple(0) + "," + tuple(1) + "," + tuple(2) + ")")
//    System.out.println("5: set: " + set)
  }
}
