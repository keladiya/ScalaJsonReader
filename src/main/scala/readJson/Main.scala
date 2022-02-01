package readJson

import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.syntax._

import java.io.{BufferedWriter, FileWriter}

object Main extends App {

  case class inputName(official: String)

  case class inputCountries(
                             name: inputName,
                             capital: Seq[String],
                             region: String,
                             area: Double
                           )

  case class outputCountries(
                              name: String,
                              capital: String,
                              area: Int
                            )

  val inputJson = "countries.json"

  {
    try {
      val src = scala.io.Source.fromURL("https://raw.githubusercontent.com/mledoze/countries/master/countries.json")
      val out = new java.io.FileWriter("src/main/resources/" + inputJson)
      out.write(src.mkString)
      out.close
    } catch {
      case e: java.io.IOException => "error occured"
    }
  }

  val fromResourceDecodedCountries: Either[Throwable, List[inputCountries]] =
    ReadJsonFrom.resourceInto[List[inputCountries]](resourceName = inputJson)

  val readedCountriesJson: List[inputCountries] = {
    fromResourceDecodedCountries match {
      case Left(value) => Nil
      case Right(value) => value
    }
  }

  val filteredCountries = {
    for {
      b <- readedCountriesJson
      if b.region == "Africa"
    } yield outputCountries(b.name.official, b.capital.head, b.area.toInt)
  }

  val preparedOutput = filteredCountries.sortBy(_.area).reverse.take(10)

  println("Введите имя выходного файла")
  val outputFilename = scala.io.StdIn.readLine()

  val w = new BufferedWriter(new FileWriter(outputFilename + ".json"))

  w.write(preparedOutput.asJson.spaces4)
  w.close

}
