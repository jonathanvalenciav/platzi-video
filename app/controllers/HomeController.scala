package controllers

import javax.inject._
import models.repositories.MovieRepository
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(controllerComponents: ControllerComponents, movieRepository: MovieRepository)
  extends AbstractController(controllerComponents) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def dbInit() = Action.async { request => movieRepository.dbInit
        .map(_ => Created("BD created."))
        .recover{ exception =>
          play.Logger.of("dbInit").debug("Error on dbInit", exception)
          InternalServerError(s"There was an issue")
        }
  }
}
