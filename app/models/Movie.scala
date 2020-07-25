package models

import java.util.UUID

import slick.lifted.Tag
import slick.jdbc.SQLiteProfile.api._

case class MovieRecord (
  id:            Option[String] = Option(UUID.randomUUID.toString),
  title:         String,
  year:          Int,
  cover:         String,
  description:   String,
  duration:      Int,
  contentRating: String,
  source:        String,
  tags:          Option[String]
)

class TB_MOVIE (tag : Tag) extends Table[MovieRecord](tag, "TEMP_MOVIE") {
  def cdMovie        = column[String]("CDMOVIE", O.PrimaryKey)
  def dsTitle        = column[String]("DSTITLE")
  def nmYear            = column[Int]("NMYEAR")
  def dsCover        = column[String]("DSCOVER")
  def dsDescription  = column[String]("DSDESCRIPTION")
  def nmDuration        = column[Int]("NMDURATION")
  def dsContentRating= column[String]("DSCONTENT_RATING")
  def dsSource       = column[String]("DSSOURCE")
  def dsTags = column[Option[String]]("DSTAGS", O.Length(2000, varying = true))

  def * =
    (cdMovie.?, dsTitle, nmYear, dsCover, dsDescription, nmDuration, dsContentRating, dsSource, dsTags) <> (MovieRecord.tupled, MovieRecord.unapply)
}