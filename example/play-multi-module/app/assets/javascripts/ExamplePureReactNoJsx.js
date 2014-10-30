

(function() {

window.examplePureReactNoJsx = function() {
   React.renderComponent(
     React.DOM.strong(null, 'It works from pure React (no JSX) in app!'),
		   document.getElementById('playground')
   )	
};
}).call(this);
