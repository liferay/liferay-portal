/**
 * The external scripts the app appends at load. The AI Hub chatbot is absent:
 * it is provisioned per workspace, so `RootLayout` drives it by route.
 */

import {appendScript} from 'shared/util/external-script';
import {PENDO_SCRIPTS} from 'shared/util/pendo-script';

PENDO_SCRIPTS.forEach((script) => appendScript(script));
