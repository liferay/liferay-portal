/**
 * Everything specific to the AI Hub chat widget: where it is served from, which
 * chatbot it opens, and the overrides that let its toggle share the corner with
 * the help widget. `external-scripts.js` owns the generic machinery that puts a
 * script or a stylesheet on the page and pulls the descriptors below into it.
 */

const STAGING_CHATBOT_EXTERNAL_REFERENCE_CODE = 'chatbot-ldp-stg-liferay-com';

/**
 * A chatbot is provisioned per environment and answers to its own external
 * reference code, so the widget loads only where this map names one.
 *
 * Keyed on the portal's own `faroURL` rather than on `FARO_ENV`, because the
 * build-time environment name cannot be trusted to tell the deployments apart:
 * `FaroEnv` has no member for the internal environment, and nothing in this
 * repository pins which value `FARO_ENVIRONMENT_NAME` carries there. Were
 * internal to build as `stg`, keying on the environment would quietly serve it
 * staging's chatbot. Keying on the URL the portal reports for itself makes the
 * map fail closed instead: an environment it does not name renders nothing.
 *
 * TODO: Add the internal environment, `https://ldp-internal.liferay.com`, once
 * its chatbot is provisioned. Leaving it out is what keeps the widget off that
 * environment in the meantime.
 */
const CHATBOT_EXTERNAL_REFERENCE_CODES: Record<string, string | undefined> = {
	'https://ldp-stg.liferay.com': STAGING_CHATBOT_EXTERNAL_REFERENCE_CODE,
};

// The dev server rewrites `faroURL` to its own origin, so the URL cannot
// identify the environment locally. Development always talks to the staging
// chatbot, whichever backend `FARO_URL` points at.

const CHATBOT_EXTERNAL_REFERENCE_CODE =
	FARO_ENV === 'local'
		? STAGING_CHATBOT_EXTERNAL_REFERENCE_CODE
		: CHATBOT_EXTERNAL_REFERENCE_CODES[
				window.faroConstants.faroURL.replace(/\/$/, '')
			];

const CHATBOT_HOST_ID = 'aihub-chatbot-host';

const CHATBOT_SCRIPT_ID = 'aihub-chatbot-widget-script';

// The widget only renders once its configuration request resolves, so the host
// is not on the page yet when this module runs.

const CHATBOT_STYLE_TIMEOUT = 30000;

/**
 * The widget ships a 3.5rem toggle pinned to the bottom right corner, which is
 * where the help widget already sits, so out of the box it covers that button
 * behind a `z-index` of 2147483647. These rules size it to match the help
 * button and leave it in the corner, while `_help_widget.scss` shifts the help
 * button one slot to the left.
 *
 * They have to be injected into the widget's shadow root, because nothing on
 * the page can reach inside one: the widget exposes no `::part()`, and the only
 * custom properties it declares are colors. The icon keeps the toggle's own
 * half-of-the-button proportion rather than the help widget's 16px.
 *
 * The offsets go on the container rather than on the toggle, which is laid out
 * statically: the toggle carries `bottom` and `right` of its own, but they are
 * inert, and overriding them moves nothing.
 */
const CHATBOT_TOGGLE_STYLES = `
	#aihub-chatbot-widget {
		bottom: 16px;
		right: 16px;
	}

	.aihub-toggle {
		height: 40px;
		width: 40px;
	}

	.aihub-toggle .lexicon-icon {
		height: 20px;
		width: 20px;
	}
`;

/**
 * Locally the widget is pointed at the dev server rather than at the AI Hub, so
 * that the configuration `fetch` it makes on startup is same-origin: the AI Hub
 * returns no `Access-Control-Allow-Origin`, so the browser blocks that call from
 * `http://localhost:<port>` before it is ever sent. The dev server forwards the
 * prefix to the real host — see `AI_HUB_PROXY_PATH` in `webpack.dev.js`.
 *
 * Compared against the `FaroEnv` value as a string literal rather than through
 * the enum, because webpack evaluates comparisons to literals at build time and
 * drops the branch that can never be reached, keeping the dev-only path out of
 * the deployed bundle.
 */
const AI_HUB_URL =
	FARO_ENV === 'local' ? '/__aihub__' : 'https://na1.hub.liferay.com';

export const AI_HUB_CHATBOT_LINKS = CHATBOT_EXTERNAL_REFERENCE_CODE
	? [
			{
				href: `${AI_HUB_URL}/documents/d/global/index-css`,
				rel: 'stylesheet',
			},
		]
	: [];

export const AI_HUB_CHATBOT_SCRIPTS = CHATBOT_EXTERNAL_REFERENCE_CODE
	? [
			{
				attributes: {
					'ai-hub-url': AI_HUB_URL,
					'chatbot-external-reference-code':
						CHATBOT_EXTERNAL_REFERENCE_CODE,
				},
				id: CHATBOT_SCRIPT_ID,
				src: `${AI_HUB_URL}/documents/d/global/index-js`,
			},
		]
	: [];

/**
 * Adds the overrides above to the chatbot's shadow root, if the widget has
 * built it. Returns whether it did, so the caller can stop waiting.
 */
function addToggleStyles() {
	const shadowRoot = document.getElementById(CHATBOT_HOST_ID)?.shadowRoot;

	if (!shadowRoot) {
		return false;
	}

	const style = document.createElement('style');

	style.textContent = CHATBOT_TOGGLE_STYLES;

	const nonce = (Liferay as unknown as {CSP?: {nonce?: string}}).CSP?.nonce;

	if (nonce) {
		style.setAttribute('nonce', nonce);
	}

	shadowRoot.appendChild(style);

	return true;
}

/**
 * Waits for the widget to put its host on the page, then restyles its toggle.
 */
export function styleAIHubChatbot() {
	if (!CHATBOT_EXTERNAL_REFERENCE_CODE || addToggleStyles()) {
		return;
	}

	const observer = new MutationObserver(() => {
		if (addToggleStyles()) {
			observer.disconnect();
		}
	});

	observer.observe(document.body, {childList: true, subtree: true});

	// A failed configuration request leaves the widget unrendered, so give up
	// rather than watching the whole body for the life of the page.

	setTimeout(() => observer.disconnect(), CHATBOT_STYLE_TIMEOUT);
}
