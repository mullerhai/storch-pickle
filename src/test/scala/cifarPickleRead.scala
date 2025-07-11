import scala.collection.mutable

import torch.pickle.Unpickler
import torch.pickle.objects.MulitNumpyNdArray

object cifarPickleRead {

  def main(args: Array[String]): Unit =

    readCifar100TestDataset()
//
  // <class 'list'>
  // <class 'list'>
  // <class 'str'>
  // <class 'list'>
  // <class 'numpy.ndarray'>

  def readCifar100TestDataset(): Unit = {
    // dict_keys(['filenames', 'batch_label', 'fine_labels', 'coarse_labels', 'data'])
    val unpickler = new Unpickler
//    val filePath = "D:\\data\\git\\storch-pickle\\random_structured_array.pkl"
    val filePath = "D:\\data\\git\\storch-pickle\\random_array.pkl"
//    val filePath = "D:\\data\\git\\storch-pickle\\newOnlyNumpyArray.pkl"
//    val filePath = "D:\\data\\git\\storch-pickle\\newtests.pkl"
//    val filePath = "C:\\Users\\hai71\\PycharmProjects\\PythonProject1\\data\\cifar-100-python\\newtrain.pkl"
//    val filePath = "D:\\data\\git\\storch-pickle\\newtestsnonumpy.pkl"
//    val filePath = "D:\\data\\git\\storch-pickle\\tests.pkl"
    val res = unpickler.load(filePath)
    println("尝试读取 内容")
    val npARR = res.asInstanceOf[MulitNumpyNdArray].numpyNdarray.toNDArray()
    println(npARR.printArray())

//    println(unpickler.memo)

    val resMap = res.asInstanceOf[mutable.HashMap[String, ?]]

//    println(resMap.get("fine_labels").get)
//    println(resMap.get("batch_label").get)
//    println(resMap.get("filenames").get)
//    println(resMap.get("coarse_labels").get)
    println(resMap.get("data").get)

//    println(resMap.get("fine_labels").get.asInstanceOf[mutable.ListBuffer[Int]].size)
    //    println(resMap.get("coarse_label_names").get)
    //    val labelNames = resMap.get("fine_label_names").get.asInstanceOf[mutable.ListBuffer[String]]
    //    println(labelNames)
    //    val labelNamesSeq = labelNames.toSeq
  }

  def readCifar100Meta(): Unit = {
    val unpickler = new Unpickler

    val filePath = "D:\\data\\git\\storch-pickle\\meta.pkl"
    val res = unpickler.load(filePath)
    println(res)
    val resMap = res.asInstanceOf[mutable.HashMap[String, ?]]
    println("尝试读取 内容")
    println(resMap)
    println(resMap.get("coarse_label_names").get)
    val labelNames = resMap.get("fine_label_names").get
      .asInstanceOf[mutable.ListBuffer[String]]
    println(labelNames)
    val labelNamesSeq = labelNames.toSeq

  }
}
