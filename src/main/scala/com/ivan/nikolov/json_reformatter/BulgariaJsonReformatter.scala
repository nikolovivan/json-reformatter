package com.ivan.nikolov.json_reformatter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.io.Source
import java.io.File
import org.geojson._
import scala.collection.JavaConversions._
import scala.math.BigDecimal.RoundingMode

/**
 * Created by volcom on 25/04/14.
 */
case class BulgariaJsonReformatter(input: String, encodingInput: String, output: String) {

  // create the mapper.
  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def parseInput() = mapper.readValue(
    Source.fromFile(input, encodingInput).getLines().mkString("\n"),
    classOf[FeatureCollection]
  )

  def doubleConvert(num: Double): Double = try {
    BigDecimal(num).setScale(2, RoundingMode.HALF_UP).doubleValue()
  } catch {
    case _ => num
  }

    private def modifyDoubleListOfLngLanAlt(list: java.util.List[java.util.List[LngLatAlt]]) {
      list.foreach(
        l => l.foreach(
          f => {
            f.setAltitude(doubleConvert(f.getAltitude))
            f.setLatitude(doubleConvert(f.getLatitude))
            f.setLongitude(doubleConvert(f.getLongitude))
          }
        )
      )
    }

    def modifyPolygon(polygon: Polygon) {
      modifyDoubleListOfLngLanAlt(polygon.getCoordinates)
    }

    def modifyMultiPolygon(polygon: MultiPolygon) {
      polygon.getCoordinates.foreach(
        modifyDoubleListOfLngLanAlt
      )
    }

    def modifyFeatures(features: FeatureCollection) = {
      val it = features.iterator()
      while (it.hasNext) {
        val feature = it.next()
        val geometry = feature.getGeometry

        if (geometry.isInstanceOf[Polygon]) {
          modifyPolygon(geometry.asInstanceOf[Polygon])
        } else if (geometry.isInstanceOf[MultiPolygon]) {
          modifyMultiPolygon(geometry.asInstanceOf[MultiPolygon])
        }
      }
      features
    }

    def writeToOutputFile(geoJson: FeatureCollection) {
      mapper.writeValue(new File(output), geoJson)
    }
}

object BulgariaJsonReformatter {

  // The encoding of the input file was: ISO-8859-1

  def main(args: Array[String]) {
    if (args.size == 0) {
      System.err.println("Usage: BulgariaJsonReformatter <json_input> <encoding_input> <json_output>")
      System.exit(1)
    }

    val Array(input, encodingInput, output) = args

    val reformatter = BulgariaJsonReformatter(input, encodingInput, output)
    val geoJson = reformatter.parseInput()
    val roundedJson = reformatter.modifyFeatures(geoJson)
    reformatter.writeToOutputFile(roundedJson)

    System.out.println("Success!")
  }
}
