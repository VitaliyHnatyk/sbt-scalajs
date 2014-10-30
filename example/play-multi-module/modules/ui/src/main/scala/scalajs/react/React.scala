package scalajs.react

import scala.scalajs.js
import org.scalajs.dom.HTMLElement

trait ReactDOM extends js.Object {
  def p(comp:ReactComponent, value: js.String): ReactComponent = ???
  def strong(comp:ReactComponent, value: js.String): ReactComponent = ???
 
  
}
trait ReactComponent extends js.Object {
  def getDOMNode(): HTMLElement = ???
}


trait ReactApi extends js.Object {
  def initializeTouchEvent(shouldUseTouchEvents: Boolean): Unit = ???
  def renderComponent(nextComponent: ReactComponent, container: HTMLElement , callback: js.Any = null): ReactComponent = ???
  def renderComponentToString(component: ReactComponent,  callback: (String) => Unit): Unit = ???
}
