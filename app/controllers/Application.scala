package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Redirect(routes.Products.list())
  }
  
  def hello(name: String) = Action {
    Ok(views.html.hello(name))
  }

}
