import torch.pickle.Unpickler

import java.nio.file.{Files, Paths}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@main
def main(): Unit =
  // TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
  // to see how IntelliJ IDEA suggests fixing it.
    (1 to 5).map(println)
    val unpickler = new Unpickler
    val filePath = "src/main/resources/testpickle.dat"
//    val paths = Paths.get(path) // "D:\\data\\git\\storch-pickle\\testpickle5.dat")
//    val stream = Files.newInputStream(paths)
    val res = unpickler.load(filePath)
    println(s"res $res")
    for (i <- 1 to 5) do
      // TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
      // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
      println(s"i = $i")
