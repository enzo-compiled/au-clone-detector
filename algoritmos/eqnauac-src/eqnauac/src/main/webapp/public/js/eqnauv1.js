function run_anti_unif(linear) {
	main([document.getElementById('maxlines').value, document.getElementById('compact').checked.toString(), 'AU', document.getElementById('loglevel').value, document.getElementById('problemset').value, document.getElementById('nabla').value, document.getElementById('associative').value, document.getElementById('commutative').value, document.getElementById('extraatoms').value, document.getElementById('align').checked.toString(), linear, document.getElementById('branchlimit').value]);
}
function run_equivariance(linear) {
	main([document.getElementById('maxlines').value, document.getElementById('compact').checked.toString(), 'EQ', document.getElementById('loglevel').value, document.getElementById('problemset').value, document.getElementById('nabla').value, document.getElementById('associative').value, document.getElementById('commutative').value, document.getElementById('extraatoms').value, document.getElementById('align').checked.toString(), linear, document.getElementById('branchlimit').value]);
}
