package controllers

import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, Controller}
import models.Product
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.mvc.Flash
/**
 * @author jforster 
 */
class Products extends Controller{
  
  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
      "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText  
    )(Product.apply)(Product.unapply)
  )

  def list = Action { implicit request =>
    val products = Product.findAll
    Ok(views.html.list(products))
  }
  
  def show(ean: Long) = Action { implicit request =>
  Product.findByEan(ean).map { product =>
    Ok(views.html.details(product))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()
    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct()).
          flashing(Flash(form.data) +
           ("error" -> Messages("validation.errors")))
      },
      success={newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.Products.show(newProduct.ean)).
          flashing("success" -> message)
      }
      )
  }
  
  def newProduct = Action { implicit request =>
    val form = if (request2flash.get("error").isDefined)
      productForm.bind(request2flash.data)
    else  
      productForm
    Ok(views.html.editProduct(form))
  }

}