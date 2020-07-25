package controllers

import javax.inject._
import models.MovieRecord
import models.repositories.MovieRepository
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class MovieController @Inject()(controllerComponents: ControllerComponents,
                                movieRepository: MovieRepository)
    extends AbstractController(controllerComponents) {

  implicit val movieSerializer = Json.format[MovieRecord]
  val logger = play.Logger.of("MovieController")

  def getMovies = Action.async {
    movieRepository.getAll
      .map(movies => {
        val jsonMovies = Json.obj(
          "data" -> movies,
          "message" -> "All movies returned"
        )
        Ok(jsonMovies)
      })
      .recover {
        case exception =>
          logger.error("Falló en getMovies", exception)
          InternalServerError(
            s"Hubo un error: ${exception.getLocalizedMessage}")
      }
  }
  def getMovieById(id: String) = Action.async {
    movieRepository
      .getMovieById(id)
      .map(movie => {
        val jsonMovie = Json.obj(
          "data" -> movie,
          "message" -> s"Movie with id: $id returned"
        )
        Ok(jsonMovie)
      })
      .recover {
        case exception =>
          logger.error("Falló en getMovieById", exception)
          InternalServerError(
            s"Hubo un error: ${exception.getLocalizedMessage}")
      }
  }

  def createMovie = Action.async(parse.json) { request =>
    val movieValidator = request.body.validate[MovieRecord]

    movieValidator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) => {
        movieRepository
          .create(movie)
          .map(movie => {
            val jsonMovie = Json.obj(
              "data" -> movie,
              "message" -> "Movie created"
            )
            Ok(jsonMovie)
          })
          .recover {
            case exception =>
              logger.error("Falló en createMovie", exception)
              InternalServerError(
                s"Hubo un error: ${exception.getLocalizedMessage}")
          }
      }
    }
  }

  def updateMovie(id: String) = Action.async(parse.json) { request =>
    val movieValidator = request.body.validate[MovieRecord]

    movieValidator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) => {
        movieRepository
          .update(id, movie)
          .map(movie => {
            val jsonMovie = Json.obj(
              "data" -> movie,
              "message" -> "Movie updated"
            )
            Ok(jsonMovie)
          })
          .recover {
            case exception =>
              logger.error("Falló en updateMovie", exception)
              InternalServerError(
                s"Hubo un error: ${exception.getLocalizedMessage}")
          }
      }
    }
  }

  def deleteMovie(id: String) = Action.async {
    movieRepository
      .delete(id)
      .map(movie => {
        val jsonMovie = Json.obj(
          "data" -> movie,
          "message" -> s"Movie with id: $id deleted"
        )
        Ok(jsonMovie)
      })
      .recover {
        case exception =>
          logger.error("Falló en deleteMovie", exception)
          InternalServerError(
            s"Hubo un error: ${exception.getLocalizedMessage}")
      }
  }

}
