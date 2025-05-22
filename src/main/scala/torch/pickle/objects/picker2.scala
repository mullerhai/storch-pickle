//package torch.pickle.objects
//
//import torch.pickle.Opcodes
//import torch.pickle.objects.Time
//import torch.pickle.objects.TimeDelta
//
//import java.io.ByteArrayOutputStream
//import java.io.IOException
//import java.io.OutputStream
//import java.lang.reflect.InvocationTargetException
//import java.lang.reflect.Method
//import java.lang.reflect.Modifier
//import java.math.BigDecimal
//import java.math.BigInteger
//import java.nio.charset.StandardCharsets
//import scala.collection.mutable
//import scala.jdk.CollectionConverters.*
//
//// 最高支持的Python pickle协议版本
//object Pickler:
//  val HIGHEST_PROTOCOL = 2
//  // 自定义类的pickler注册表
//  private val customPicklers: mutable.Map[Class[_], IObjectPickler] = mutable.HashMap()
//  // 自定义类的解构器注册表
//  private val customDeconstructors: mutable.Map[Class[_], IObjectDeconstructor] = mutable.HashMap()
//
//  // 注册自定义对象pickler
//  def registerCustomPickler(clazz: Class[_], pickler: IObjectPickler): Unit =
//    customPicklers.put(clazz, pickler)
//
//  // 注册自定义对象解构器
//  def registerCustomDeconstructor(clazz: Class[_], deconstructor: IObjectDeconstructor): Unit =
//    customDeconstructors.put(clazz, deconstructor)
//
//// 存储记忆化对象的类
//case class Memo(obj: Any, index: Int)
//
//class Pickler(useMemoParam: Boolean = true, valueCompareParam: Boolean = true):
//  // 递归深度限制
//  private val MAX_RECURSE_DEPTH = 1000
//  // 当前递归级别
//  private var recurse = 0
//  // 输出流
//  private var out: OutputStream = _
//  // Python pickle协议版本
//  private val PROTOCOL = 2
//  // 是否使用记忆化
//  private val useMemo = useMemoParam
//  // 记忆化时是否按值比较
//  private val valueCompare = valueCompareParam
//  // 记忆化缓存
//  private var memo: mutable.HashMap[Int, Memo] = _
//
//  // 关闭pickler流
//  @throws[IOException]
//  def close(): Unit =
//    memo = null
//    out.flush()
//    out.close()
//
//  // 将对象图pickle为字节数组
//  @throws[PickleException]
//  @throws[IOException]
//  def dumps(o: Any): Array[Byte] =
//    val bo = new ByteArrayOutputStream()
//    dump(o, bo)
//    bo.flush()
//    bo.toByteArray
//
//  // 将对象图pickle并写入输出流
//  @throws[IOException]
//  @throws[PickleException]
//  def dump(o: Any, stream: OutputStream): Unit =
//    out = stream
//    recurse = 0
//    if useMemo then
//      memo = mutable.HashMap()
//    out.write(Opcodes.PROTO)
//    out.write(PROTOCOL)
//    save(o)
//    memo = null
//    out.write(Opcodes.STOP)
//    out.flush()
//    if recurse != 0 then
//      throw PickleException("recursive structure error, please report this problem")
//
//  // 对单个对象进行pickle并写入输出流
//  @throws[PickleException]
//  @throws[IOException]
//  def save(o: Any): Unit =
//    recurse += 1
//    if recurse > MAX_RECURSE_DEPTH then
//      throw new StackOverflowError(s"recursion too deep in Pickler.save (> $MAX_RECURSE_DEPTH)")
//
//    if o == null then
//      out.write(Opcodes.NONE)
//      recurse -= 1
//      return
//
//    val t = o.getClass
//    if lookupMemo(t, o) || dispatch(t, o) then
//      recurse -= 1
//      return
//
//    throw PickleException(s"couldn't pickle object of type $t")
//
//  // 将对象写入记忆表并输出记忆写入操作码
//  @throws[IOException]
//  protected def writeMemo(obj: Any): Unit =
//    if!useMemo then
//      return
//    val hash = if valueCompare then obj.hashCode() else System.identityHashCode(obj)
//    if!memo.contains(hash) then
//      val memoIndex = memo.size
//      memo.put(hash, Memo(obj, memoIndex))
//      if memoIndex <= 0xFF then
//        out.write(Opcodes.BINPUT)
//        out.write(memoIndex.toByte)
//      else
//        out.write(Opcodes.LONG_BINPUT)
//        val indexBytes = PickleUtils.integer_to_bytes(memoIndex)
//        out.write(indexBytes, 0, 4)
//
//  // 检查记忆表并在找到对象时输出记忆查找操作码
//  @throws[IOException]
//  private def lookupMemo(objectType: Class[_], obj: Any): Boolean =
//    if!useMemo then
//      return false
//    if!objectType.isPrimitive then
//      val hash = if valueCompare then obj.hashCode() else System.identityHashCode(obj)
//      if memo.contains(hash) && (if valueCompare then memo(hash).obj == obj else memo(hash).obj.eq(obj)) then
//        val memoIndex = memo(hash).index
//        if memoIndex <= 0xff then
//          out.write(Opcodes.BINGET)
//          out.write(memoIndex.toByte)
//        else
//          out.write(Opcodes.LONG_BINGET)
//          val indexBytes = PickleUtils.integer_to_bytes(memoIndex)
//          out.write(indexBytes, 0, 4)
//        return true
//    false
//
//  // 处理单个待pickle的对象
//  @throws[IOException]
//  private def dispatch(t: Class[_], o: Any): Boolean =
//    val componentType = t.getComponentType
//    if componentType != null then
//      if componentType.isPrimitive then
//        put_arrayOfPrimitives(componentType, o)
//      else
//        put_arrayOfObjects(o.asInstanceOf[Array[Any]])
//      return true
//
//    o match
//      case b: Boolean =>
//        put_bool(b)
//        true
//      case b: Byte =>
//        put_long(b.toLong)
//        true
//      case s: Short =>
//        put_long(s.toLong)
//        true
//      case i: Int =>
//        put_long(i.toLong)
//        true
//      case l: Long =>
//        put_long(l)
//        true
//      case f: Float =>
//        put_float(f.toDouble)
//        true
//      case d: Double =>
//        put_float(d)
//        true
//      case c: Char =>
//        put_string(c.toString)
//        true
//      case _ =>
//        val customPickler = getCustomPickler(t)
//        if customPickler != null then
//          customPickler.pickle(o, out, this)
//          writeMemo(o)
//          return true
//
//        val customDeconstructor = getCustomDeconstructor(t)
//        if customDeconstructor != null then
//          put_global(customDeconstructor, o)
//          return true
//
//        val persistentId = persistentId(o)
//        if persistentId != null then
//          persistentId match
//            case s: String if!s.contains("\n") =>
//              out.write(Opcodes.PERSID)
//              out.write(s.getBytes())
//              out.write("\n".getBytes())
//            case _ =>
//              save(persistentId)
//              out.write(Opcodes.BINPERSID)
//          return true
//
//        o match
//          case s: String =>
//            put_string(s)
//            true
//          case bi: BigInteger =>
//            put_bigint(bi)
//            true
//          case bd: BigDecimal =>
//            put_decimal(bd)
//            true
//          case st: java.sql.Time =>
//            val time = new Time(st.getTime)
//            put_time(time)
//            true
//          case sd: java.sql.Date =>
//            put_sqldate(sd)
//            true
//          case cal: Calendar =>
//            put_calendar(cal)
//            true
//          case time: Time =>
//            put_time(time)
//            true
//          case td: TimeDelta =>
//            put_timedelta(td)
//            true
//          case d: java.util.Date =>
//            val cal = GregorianCalendar.getInstance()
//            cal.setTime(d)
//            put_calendar(cal)
//            true
//          case tz: java.util.TimeZone =>
//            put_timezone(tz)
//            true
//          case e: Enumeration[_] =>
//            put_string(e.toString)
//            true
//          case s: Set[_] =>
//            put_set(s.asJava)
//            true
//          case m: Map[_, _] =>
//            put_map(m.asJava)
//            true
//          case l: List[_] =>
//            put_collection(l.asJava)
//            true
//          case c: Collection[_] =>
//            put_collection(c)
//            true
//          case s: java.io.Serializable =>
//            put_javabean(s)
//            true
//          case _ =>
//            false
//
//  // 获取自定义pickler
//  protected def getCustomPickler(t: Class[_]): IObjectPickler =
//    customPicklers.get(t) match
//      case Some(pickler) => pickler
//      case None =>
//        customPicklers.find { case (key, _) => key.isAssignableFrom(t) } match
//          case Some((_, pickler)) => pickler
//          case None => null
//
//  // 获取自定义解构器
//  protected def getCustomDeconstructor(t: Class[_]): IObjectDeconstructor =
//    customDeconstructors.get(t).orNull
//
//  // 处理集合
//  @throws[IOException]
//  private def put_collection(list: Collection[Any]): Unit =
//    out.write(Opcodes.EMPTY_LIST)
//    writeMemo(list)
//    out.write(Opcodes.MARK)
//    list.forEach(save)
//    out.write(Opcodes.APPENDS)
//
//  // 处理映射
//  @throws[IOException]
//  private def put_map(o: java.util.Map[Any, Any]): Unit =
//    out.write(Opcodes.EMPTY_DICT)
//    writeMemo(o)
//    out.write(Opcodes.MARK)
//    o.forEach((k, v) =>
//      save(k)
//      save(v)
//    )
//    out.write(Opcodes.SETITEMS)
//
//  // 处理集合
//  @throws[IOException]
//  private def put_set(o: java.util.Set[Any]): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("__builtin__\nset\n".getBytes())
//    out.write(Opcodes.EMPTY_LIST)
//    out.write(Opcodes.MARK)
//    o.forEach(save)
//    out.write(Opcodes.APPENDS)
//    out.write(Opcodes.TUPLE1)
//    out.write(Opcodes.REDUCE)
//    writeMemo(o)
//
//  // 处理日历对象
//  @throws[IOException]
//  private def put_calendar(cal: Calendar): Unit =
//    if cal.getTimeZone != null then
//      out.write(Opcodes.GLOBAL)
//      out.write("operator\nattrgetter\n".getBytes())
//      put_string("localize")
//      out.write(Opcodes.TUPLE1)
//      out.write(Opcodes.REDUCE)
//      put_timezone(cal.getTimeZone)
//      out.write(Opcodes.TUPLE1)
//      out.write(Opcodes.REDUCE)
//      put_calendar_without_timezone(cal, false)
//      out.write(Opcodes.TUPLE1)
//      out.write(Opcodes.REDUCE)
//      writeMemo(cal)
//      return
//
//    put_calendar_without_timezone(cal, true)
//
//  // 处理不带时区的日历对象
//  @throws[IOException]
//  private def put_calendar_without_timezone(cal: Calendar, writeMemo: Boolean): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("datetime\ndatetime\n".getBytes())
//    out.write(Opcodes.MARK)
//    save(cal.get(Calendar.YEAR))
//    save(cal.get(Calendar.MONTH) + 1)
//    save(cal.get(Calendar.DAY_OF_MONTH))
//    save(cal.get(Calendar.HOUR_OF_DAY))
//    save(cal.get(Calendar.MINUTE))
//    save(cal.get(Calendar.SECOND))
//    save(cal.get(Calendar.MILLISECOND) * 1000)
//    out.write(Opcodes.TUPLE)
//    out.write(Opcodes.REDUCE)
//    if writeMemo then
//      writeMemo(cal)
//
//  // 处理时间差对象
//  @throws[IOException]
//  private def put_timedelta(delta: TimeDelta): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("datetime\ntimedelta\n".getBytes())
//    save(delta.days)
//    save(delta.seconds)
//    save(delta.microseconds)
//    out.write(Opcodes.TUPLE3)
//    out.write(Opcodes.REDUCE)
//    writeMemo(delta)
//
//  // 处理时间对象
//  @throws[IOException]
//  private def put_time(time: Time): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("datetime\ntime\n".getBytes())
//    out.write(Opcodes.MARK)
//    save(time.hours)
//    save(time.minutes)
//    save(time.seconds)
//    save(time.microseconds)
//    out.write(Opcodes.TUPLE)
//    out.write(Opcodes.REDUCE)
//    writeMemo(time)
//
//  // 处理SQL日期对象
//  @throws[IOException]
//  private def put_sqldate(date: java.sql.Date): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("datetime\ndate\n".getBytes())
//    val cal = Calendar.getInstance()
//    cal.setTime(date)
//    save(cal.get(Calendar.YEAR))
//    save(cal.get(Calendar.MONTH) + 1)
//    save(cal.get(Calendar.DAY_OF_MONTH))
//    out.write(Opcodes.TUPLE3)
//    out.write(Opcodes.REDUCE)
//    writeMemo(date)
//
//  // 处理时区对象
//  @throws[IOException]
//  private def put_timezone(timeZone: java.util.TimeZone): Unit =
//    out.write(Opcodes.GLOBAL)
//    if timeZone.getID == "UTC" then
//      out.write("pytz\n_UTC\n".getBytes())
//      out.write(Opcodes.MARK)
//    else
//      out.write("pytz\ntimezone\n".getBytes())
//      out.write(Opcodes.MARK)
//      save(timeZone.getID)
//    out.write(Opcodes.TUPLE)
//    out.write(Opcodes.REDUCE)
//    writeMemo(timeZone)
//
//  // 处理对象数组
//  @throws[IOException]
//  private def put_arrayOfObjects(array: Array[Any]): Unit =
//    array.length match
//      case 0 =>
//        out.write(Opcodes.EMPTY_TUPLE)
//      case 1 =>
//        if array(0) == array then
//          throw PickleException("recursive array not supported, use list")
//        save(array(0))
//        out.write(Opcodes.TUPLE1)
//      case 2 =>
//        if array(0) == array || array(1) == array then
//          throw PickleException("recursive array not supported, use list")
//        save(array(0))
//        save(array(1))
//        out.write(Opcodes.TUPLE2)
//      case 3 =>
//        if array(0) == array || array(1) == array || array(2) == array then
//          throw PickleException("recursive array not supported, use list")
//        save(array(0))
//        save(array(1))
//        save(array(2))
//        out.write(Opcodes.TUPLE3)
//      case _ =>
//        out.write(Opcodes.MARK)
//        array.foreach { o =>
//          if o == array then
//            throw PickleException("recursive array not supported, use list")
//          save(o)
//        }
//        out.write(Opcodes.TUPLE)
//    writeMemo(array)
//
//  // 处理基本类型数组
//  @throws[IOException]
//  private def put_arrayOfPrimitives(t: Class[_], array: Any): Unit =
//    t match
//      case cls if cls == classOf[Boolean] =>
//        val source = array.asInstanceOf[Array[Boolean]]
//        val boolArray = source.map(Boolean.box)
//        put_arrayOfObjects(boolArray)
//      case cls if cls == classOf[Char] =>
//        val s = new String(array.asInstanceOf[Array[Char]])
//        put_string(s)
//      case cls if cls == classOf[Byte] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("__builtin__\nbytearray\n".getBytes())
//        val str = PickleUtils.rawStringFromBytes(array.asInstanceOf[Array[Byte]])
//        put_string(str)
//        put_string("latin-1")
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case cls if cls == classOf[Short] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("array\narray\n".getBytes())
//        out.write(Opcodes.SHORT_BINSTRING)
//        out.write(1)
//        out.write('h')
//        out.write(Opcodes.EMPTY_LIST)
//        out.write(Opcodes.MARK)
//        array.asInstanceOf[Array[Short]].foreach(save)
//        out.write(Opcodes.APPENDS)
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case cls if cls == classOf[Int] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("array\narray\n".getBytes())
//        out.write(Opcodes.SHORT_BINSTRING)
//        out.write(1)
//        out.write('i')
//        out.write(Opcodes.EMPTY_LIST)
//        out.write(Opcodes.MARK)
//        array.asInstanceOf[Array[Int]].foreach(save)
//        out.write(Opcodes.APPENDS)
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case cls if cls == classOf[Long] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("array\narray\n".getBytes())
//        out.write(Opcodes.SHORT_BINSTRING)
//        out.write(1)
//        out.write('l')
//        out.write(Opcodes.EMPTY_LIST)
//        out.write(Opcodes.MARK)
//        array.asInstanceOf[Array[Long]].foreach(save)
//        out.write(Opcodes.APPENDS)
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case cls if cls == classOf[Float] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("array\narray\n".getBytes())
//        out.write(Opcodes.SHORT_BINSTRING)
//        out.write(1)
//        out.write('f')
//        out.write(Opcodes.EMPTY_LIST)
//        out.write(Opcodes.MARK)
//        array.asInstanceOf[Array[Float]].foreach(f => save(f.toDouble))
//        out.write(Opcodes.APPENDS)
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case cls if cls == classOf[Double] =>
//        out.write(Opcodes.GLOBAL)
//        out.write("array\narray\n".getBytes())
//        out.write(Opcodes.SHORT_BINSTRING)
//        out.write(1)
//        out.write('d')
//        out.write(Opcodes.EMPTY_LIST)
//        out.write(Opcodes.MARK)
//        array.asInstanceOf[Array[Double]].foreach(save)
//        out.write(Opcodes.APPENDS)
//        out.write(Opcodes.TUPLE2)
//        out.write(Opcodes.REDUCE)
//        writeMemo(array)
//      case _ =>
//        throw PickleException(s"Unsupported primitive array type: ${t.getName}")
//
//  // 处理全局对象
//  @throws[IOException]
//  private def put_global(deconstructor: IObjectDeconstructor, obj: Any): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write((deconstructor.getModule() + "\n" + deconstructor.getName() + "\n").getBytes())
//    val values = deconstructor.deconstruct(obj)
//    if values.length > 0 then
//      save(values)
//      out.write(Opcodes.REDUCE)
//    writeMemo(obj)
//
//  // 处理大十进制数
//  @throws[IOException]
//  private def put_decimal(d: BigDecimal): Unit =
//    out.write(Opcodes.GLOBAL)
//    out.write("decimal\nDecimal\n".getBytes())
//    put_string(d.toEngineeringString())
//    out.write(Opcodes.TUPLE1)
//    out.write(Opcodes.REDUCE)
//    writeMemo(d)
//
//  // 处理大整数
//  @throws[IOException]
//  private def put_bigint(i: BigInteger): Unit =
//    val b = PickleUtils.encode_long(i)
//    if b.length <= 0xff then
//      out.write(Opcodes.LONG1)
//      out.write(b.length)
//      out.write(b)
//    else
//      out.write(Opcodes.LONG4)
//      out.write(PickleUtils.integer_to_bytes(b.length))
//      out.write(b)
//    writeMemo(i)
//
//  // 处理字符串
//  @throws[IOException]
//  private def put_string(string: String): Unit =
//    val encoded = string.getBytes(StandardCharsets.UTF_8)
//    out.write(Opcodes.BINUNICODE)
//    out.write(PickleUtils.integer_to_bytes(encoded.length))
//    out.write(encoded)
//    writeMemo(string)
//
//  // 处理浮点数
//  @throws[IOException]
//  private def put_float(d: Double): Unit =
//    out.write(Opcodes.BINFLOAT)
//    out.write(PickleUtils.double_to_bytes(d))
//
//  // 处理长整数
//  @throws[IOException]
//  private def put_long(v: Long): Unit =
//    if v >= 0 then
//      if v <= 0xff then
//        out.write(Opcodes.BININT1)
//        out.write(v.toInt)
//        return
//      if v <= 0xffff then
//        out.write(Opcodes.BININT2)
//        out.write((v & 0xff).toInt)
//        out.write((v >> 8).toInt)
//        return
//
//    val highBits = v >> 31
//    if highBits == 0 || highBits == -1 then
//      out.write(Opcodes.BININT)
//      out.write(PickleUtils.integer_to_bytes(v.toInt))
//      return
//
//    put_bigint(BigInteger.valueOf(v))
//
//  // 处理布尔值
//  @throws[IOException]
//  private def put_bool(b: Boolean): Unit =
//    if b then
//      out.write(Opcodes.NEWTRUE)
//    else
//      out.write(Opcodes.NEWFALSE)
//
//  // 处理JavaBean
//  @throws[PickleException]
//  @throws[IOException]
//  private def put_javabean(o: Any): Unit =
//    val map = mutable.HashMap[String, Any]()
//    try
//      o.getClass.getMethods.foreach { m =>
//        val modifiers = m.getModifiers
//        if (modifiers & Modifier.PUBLIC) != 0 && (modifiers & Modifier.STATIC) == 0 then
//          val methodName = m.getName
//          val prefixLen = methodName match
//            case n if n == "getClass" => -1
//            case n if n.startsWith("get") => 3
//            case n if n.startsWith("is") => 2
//            case _ => -1
//          if prefixLen > 0 then
//            val value = m.invoke(o)
//            var name = methodName.substring(prefixLen)
//            if name.length == 1 then
//              name = name.toLowerCase
//            else if!Character.isUpperCase(name.charAt(1)) then
//              name = name(0).toLower + name.substring(1)
//            map.put(name, value)
//      }
//      map.put("__class__", o.getClass.getName)
//      save(map.asJava)
//    catch
//      case e: IllegalArgumentException =>
//        throw PickleException(s"couldn't introspect javabean: $e")
//      case e: IllegalAccessException =>
//        throw PickleException(s"couldn't introspect javabean: $e")
//      case e: InvocationTargetException =>
//        throw PickleException(s"couldn't introspect javabean: $e")
//
//  // 持久化ID的钩子方法
//  protected def persistentId(obj: Any): Any =
//    null
//
//// 自定义异常类
//case class PickleException(message: String) extends Exception(message)