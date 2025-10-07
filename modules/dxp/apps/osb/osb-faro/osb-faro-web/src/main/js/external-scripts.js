/**
 * A list of external scripts to be appended to the page. Each script
 * can also specify the attributes it needs. For example, the zendesk
 * widget requires that its script tag has a certain id attribute.
 *
 * Also, note that webpack will actually evaluate the boolean expressions
 * below at build time and remove any cases that can never be reached
 * (dead-code elim). This means we don't have to worry about the development
 * scripts being present in our production bundle. To keep this working, make
 * sure that we only do comparisons to string or number literals.
 */

import {Pendo} from 'shared/util/pendo';

const pendo = new Pendo();

const scripts = [
	/* Pendo */
	{
		innerHTML: pendo.script
	}
];

/**
 * Runtime logic for adding external scripts to the page.
 */
function appendScript(options) {
	const script = document.createElement('script');

	if (options.src) {
		script.async = true;
	}

	for (const [name, value] of Object.entries(options)) {
		script[name] = value;
	}

	document.body.appendChild(script);
}

scripts.filter(({innerHTML, src}) => src || innerHTML).forEach(appendScript);
