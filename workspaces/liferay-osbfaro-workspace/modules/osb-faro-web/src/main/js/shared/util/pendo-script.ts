/**
 * The Pendo agent loader as a script descriptor for `external-scripts.js`. The
 * snippet and the consent it depends on belong to `Pendo` in `pendo.ts`.
 */

import {Pendo} from './pendo';

const PENDO_SCRIPT = new Pendo().script;

// The snippet is empty until the user accepts tracking, because nothing may be
// requested from pendo.io before that, the agent script included.

export const PENDO_SCRIPTS = PENDO_SCRIPT
	? [
			{
				innerHTML: PENDO_SCRIPT,
			},
		]
	: [];
