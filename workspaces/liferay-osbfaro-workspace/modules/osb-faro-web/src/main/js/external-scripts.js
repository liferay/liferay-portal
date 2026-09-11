/**
 * Appends the external scripts and stylesheets the app pulls from third
 * parties. Each descriptor can also specify the attributes its script needs.
 * For example, the AI Hub chatbot widget requires that its script tag carry the
 * chatbot it should open.
 *
 * One module per third party owns its own descriptors, so this file holds only
 * the machinery that puts them on the page. Those modules decide whether they
 * contribute anything at all, which is how an entry stays out of an environment
 * it does not belong in.
 */

import {
	AI_HUB_CHATBOT_LINKS,
	AI_HUB_CHATBOT_SCRIPTS,
	styleAIHubChatbot,
} from 'shared/util/ai-hub-chatbot';
import {PENDO_SCRIPTS} from 'shared/util/pendo-script';

const links = [...AI_HUB_CHATBOT_LINKS];

const scripts = [...PENDO_SCRIPTS, ...AI_HUB_CHATBOT_SCRIPTS];

function getNonce() {
	return Liferay.CSP?.nonce;
}

/**
 * Runtime logic for adding external stylesheets to the page.
 */
function appendLink(options) {
	const link = document.createElement('link');

	for (const [name, value] of Object.entries(options)) {
		link[name] = value;
	}

	const nonce = getNonce();

	if (nonce) {
		link.setAttribute('nonce', nonce);
	}

	document.head.appendChild(link);
}

/**
 * Runtime logic for adding external scripts to the page.
 */
function appendScript({attributes = {}, ...options}) {
	const script = document.createElement('script');

	if (options.src) {
		script.async = true;
	}

	for (const [name, value] of Object.entries(options)) {
		script[name] = value;
	}

	// Loaders that read their configuration back with `getAttribute` need real
	// HTML attributes, which a plain property assignment does not create for
	// non-standard names such as `ai-hub-url`.

	for (const [name, value] of Object.entries(attributes)) {
		script.setAttribute(name, value);
	}

	const nonce = getNonce();

	if (nonce) {
		script.setAttribute('nonce', nonce);
	}

	document.body.appendChild(script);
}

links.forEach(appendLink);

scripts.forEach(appendScript);

styleAIHubChatbot();
