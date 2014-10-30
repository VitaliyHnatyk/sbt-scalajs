

(function() {

window.examplePureJS = function() {
  var paragraph = document.createElement("p");
  paragraph.innerHTML = "<strong>It works from pure JS in app! </strong>";
  document.getElementById("playground").appendChild(paragraph);
}; 
}).call(this); 
