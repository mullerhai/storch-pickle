package torch.pickle.objects

import torch.pickle.IObjectConstructor
import torch.pickle.PickleException
import java.lang.reflect.Constructor
import java.math.BigDecimal

/**
 * This object constructor uses reflection to create instances of any given class.
 *
 * 
 */
//class AnyClassConstructors(private val types: Class[?]) extends IObjectConstructor {
//  override def construct(args: Array[AnyRef]): AnyRef = try {
//    val paramtypes = new Array[Class[?]](args.length)
//    for (i <- 0 until args.length) {
//      paramtypes(i) = args(i).getClass
//    }
//    val cons = types.getConstructor(paramtypes)
//    // special case BigDecimal("NaN") which is not supported in Java, return this as Double.NaN
//    if ((types eq classOf[BigDecimal]) && args.length == 1) {
//      val nan = args(0).asInstanceOf[String]
//      if (nan.equalsIgnoreCase("nan")) return java.lang.Double.NaN
//    }
//    cons.newInstance(args)
//  } catch {
//    case x: Exception =>
//      throw new PickleException("problem construction object: " + x)
//  }
//}

import java.lang.reflect.Constructor
//import java.math.BigDecimal

/**
 * This object constructor uses reflection to create instances of any given class.
 *
 
 */
class AnyClassConstructor(val types: Class[?]) extends IObjectConstructor:

  override def construct(args: Array[AnyRef]): AnyRef =
    try
      val paramtypes = args.map(_.getClass)
      // 获取构造函数，注意这里传入数组参数
      val cons: Constructor[?] = types.getConstructor(paramtypes*)

      // special case BigDecimal("NaN") which is not supported in Java, return this as Double.NaN
      if types == classOf[BigDecimal] && args.length == 1 then
        val nan = args(0).asInstanceOf[String]
        if nan.equalsIgnoreCase("nan") then
          return java.lang.Double.valueOf(Double.NaN).asInstanceOf[AnyRef]

      cons.newInstance(args*).asInstanceOf[AnyRef]
    catch
      case x: Exception =>
        throw PickleException("problem construction object: " + x)