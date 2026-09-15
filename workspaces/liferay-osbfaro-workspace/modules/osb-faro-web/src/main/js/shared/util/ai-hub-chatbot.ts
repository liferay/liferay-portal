/**
 * The AI Hub chat widget: which chatbot to open, where it is served from, and
 * the overrides that let its toggle share the corner with the help widget.
 */

import {appendLink, appendScript, applyNonce} from './external-script';

interface Chatbot {
	aiHubURL: string;
	externalReferenceCode: string;

	// The `:groupId` segment of `/workspace/:groupId/...`: a project's friendly
	// URL when it has one, its group id otherwise. A chatbot that names no
	// workspace answers on every one of them.

	workspace?: string;
}

// Keyed on the URL the portal reports for itself rather than on `FARO_ENV`,
// which has no value of its own for internal and would serve it staging's
// chatbot. An environment this map does not name renders nothing.

const CHATBOTS: Record<string, Chatbot | undefined> = {
	'https://ldp-internal.liferay.com': {
		aiHubURL: 'https://ai-uat.liferay.net',
		externalReferenceCode: 'L_AIHUB_CHATBOT_LDP',
		workspace: 'liferay.com',
	},
	'https://ldp-stg.liferay.com': {
		aiHubURL: 'https://na1.hub.liferay.com',
		externalReferenceCode: 'chatbot-ldp-stg-liferay-com',
	},
};

// The dev server reports its own origin, which the map does not name, so the
// widget stays off locally and the unit tests are what cover it.

const CHATBOT = CHATBOTS[window.faroConstants.faroURL.replace(/\/$/, '')];

const CHATBOT_HOST_ID = 'aihub-chatbot-host';

const CHATBOT_LINK_ID = 'aihub-chatbot-widget-style';

const CHATBOT_SCRIPT_ID = 'aihub-chatbot-widget-script';

const CHATBOT_STYLE_TIMEOUT = 30000;

// Injected into the widget's shadow root, which nothing on the page can reach
// into: it exposes no `::part()`. The offsets go on the container because the
// toggle carries its own, inert, and `_help_widget.scss` shifts the help button
// one slot to the left.

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

let toggleStylesObserver: MutationObserver | undefined;

/**
 * Returns whether the widget had built its host yet, so the caller can stop
 * waiting for it.
 */
function addToggleStyles() {
	const shadowRoot = document.getElementById(CHATBOT_HOST_ID)?.shadowRoot;

	if (!shadowRoot) {
		return false;
	}

	const style = document.createElement('style');

	style.textContent = CHATBOT_TOGGLE_STYLES;

	applyNonce(style);

	shadowRoot.appendChild(style);

	return true;
}

function stopStylingToggle() {
	toggleStylesObserver?.disconnect();

	toggleStylesObserver = undefined;
}

function styleToggle() {
	if (addToggleStyles()) {
		return;
	}

	toggleStylesObserver = new MutationObserver(() => {
		if (addToggleStyles()) {
			stopStylingToggle();
		}
	});

	toggleStylesObserver.observe(document.body, {
		childList: true,
		subtree: true,
	});

	// Give up rather than watching the body for the life of a page whose
	// configuration request failed and left the widget unrendered.

	setTimeout(stopStylingToggle, CHATBOT_STYLE_TIMEOUT);
}

function mount(chatbot: Chatbot) {
	const {aiHubURL} = chatbot;

	appendLink({
		href: `${aiHubURL}/documents/d/global/index-css`,
		id: CHATBOT_LINK_ID,
		rel: 'stylesheet',
	});

	appendScript({
		attributes: {
			'ai-hub-url': aiHubURL,
			'chatbot-external-reference-code': chatbot.externalReferenceCode,
		},
		id: CHATBOT_SCRIPT_ID,
		src: `${aiHubURL}/documents/d/global/index-js`,
	});

	styleToggle();
}

/**
 * Removing the host is what allows the next mount, since the widget bootstraps
 * behind `if (!document.getElementById(<host>))`. It exposes no teardown, so
 * the React root inside is dropped rather than unmounted, stranding one per
 * visit to the workspace.
 */
function unmount() {
	stopStylingToggle();

	for (const id of [CHATBOT_HOST_ID, CHATBOT_LINK_ID, CHATBOT_SCRIPT_ID]) {
		document.getElementById(id)?.remove();
	}
}

/**
 * Mounts or removes the widget to match `workspace`, the `:groupId` the router
 * matched, which is `undefined` outside a workspace.
 */
export function syncAIHubChatbot(workspace?: string) {
	const chatbot =
		CHATBOT && (!CHATBOT.workspace || CHATBOT.workspace === workspace)
			? CHATBOT
			: undefined;

	const mounted = Boolean(document.getElementById(CHATBOT_SCRIPT_ID));

	if (Boolean(chatbot) === mounted) {
		return;
	}

	if (chatbot) {
		mount(chatbot);
	}
	else {
		unmount();
	}
}
