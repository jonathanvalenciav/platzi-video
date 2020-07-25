package models.repositories

import javax.inject.Inject
import models.{MovieRecord, TB_MOVIE}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class MovieRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,  cc: ControllerComponents)
                                (implicit  ec: ExecutionContext)
    extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile] {

  private lazy val movieQuery = TableQuery[TB_MOVIE]

  def dbInit: Future[Unit] = {
    val createSchema = movieQuery.schema.createIfNotExists
    db.run(createSchema)
  }

  def getAll = {
    val allMoviesQuery = movieQuery.sortBy(_.cdMovie)
    db.run(allMoviesQuery.result)
  }
  def getMovieById(cdMovie: String) = {
    val movieById = movieQuery.filter(_.cdMovie === cdMovie)
    db.run(movieById.result.headOption)
  }

  def create(movie : MovieRecord) = {
    val insertMovie = movieQuery += movie
    db.run(insertMovie)
      .flatMap(_ => getMovieById(movie.id.getOrElse("")))
  }
  def update(cdMovie: String, movie: MovieRecord) = {
    val movieToUpdate = movieQuery.filter(_.cdMovie === movie.id && movie.id.contains(cdMovie))
    val updateMovie = movieToUpdate.update(movie)
    db.run(updateMovie)
      .flatMap(_ => db.run(movieToUpdate.result.headOption))

  }
  def delete(cdMovie: String) = {
    val movieToDelete = movieQuery.filter(_.cdMovie === cdMovie)

    for {
      movieDeleted <- db.run(movieToDelete.result.headOption)
      _ <- db.run(movieToDelete.delete)
    } yield movieDeleted
  }
}