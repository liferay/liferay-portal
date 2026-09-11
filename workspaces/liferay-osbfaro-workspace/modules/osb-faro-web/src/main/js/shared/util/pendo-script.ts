/**
 * The Pendo agent loader as an external script descriptor, which
 * `external-scripts.js` appends to the page.
 *
 * The agent's behavior — the consent it depends on, and the snippet itself —
 * belongs to `Pendo` in `pendo.ts`, which other parts of the app also use. Only
 * the decision to put a script tag on the page lives here.
 */

import {Pendo} from './pendo';

const PENDO_SCRIPT = new Pendo().script;

// Outside production the snippet is an inert stub, and inside production it is
// empty until the user accepts tracking: the page must not request anything
// from pendo.io before that, not even the agent script.

export const PENDO_SCRIPTS = PENDO_SCRIPT
	? [
			{
				innerHTML: PENDO_SCRIPT,
			},
		]
	: [];
