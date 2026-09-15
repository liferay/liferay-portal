const CHATBOT_HOST_ID = 'aihub-chatbot-host';
const CHATBOT_LINK_ID = 'aihub-chatbot-widget-style';
const CHATBOT_SCRIPT_ID = 'aihub-chatbot-widget-script';
const DEV_SERVER_URL = 'http://localhost:3000';
const INTERNAL_AI_HUB_URL = 'https://ai-uat.liferay.net';
const INTERNAL_CHATBOT = 'L_AIHUB_CHATBOT_LDP';
const INTERNAL_FARO_URL = 'https://ldp-internal.liferay.com';
const INTERNAL_WORKSPACE = 'liferay.com';
const OTHER_WORKSPACE = 'acme.com';
const PRODUCTION_FARO_URL = 'https://ldp.liferay.com';
const STAGING_AI_HUB_URL = 'https://na1.hub.liferay.com';
const STAGING_CHATBOT = 'chatbot-ldp-stg-liferay-com';
const STAGING_FARO_URL = 'https://ldp-stg.liferay.com';

describe('AI Hub chatbot', () => {
	const originalFaroURL = global.faroConstants.faroURL;

	afterEach(() => {
		global.faroConstants.faroURL = originalFaroURL;

		document.body.innerHTML = '';
		document.head.innerHTML = '';
	});

	// The environment is read when the module is evaluated, so each case has to
	// set the URL the portal reports before loading it again.

	function loadChatbot(faroURL) {
		global.faroConstants.faroURL = faroURL;

		let chatbot;

		jest.isolateModules(() => {
			chatbot = require('../ai-hub-chatbot');
		});

		return chatbot.syncAIHubChatbot;
	}

	function getLink() {
		return document.getElementById(CHATBOT_LINK_ID);
	}

	function getReferenceCode() {
		return getScript().getAttribute('chatbot-external-reference-code');
	}

	function getScript() {
		return document.getElementById(CHATBOT_SCRIPT_ID);
	}

	// jsdom never fetches the widget's script, so the host it would have built
	// is put on the page by hand.

	function renderWidgetHost() {
		const host = document.createElement('div');

		host.id = CHATBOT_HOST_ID;

		host.attachShadow({mode: 'open'});

		document.body.appendChild(host);

		return host;
	}

	it('mounts the staging chatbot on staging', () => {
		const syncAIHubChatbot = loadChatbot(STAGING_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getLink()).not.toBeNull();
		expect(getReferenceCode()).toBe(STAGING_CHATBOT);
	});

	// Staging's chatbot belongs to no workspace in particular, so it answers
	// everywhere, including the pages that sit outside a workspace.

	it('mounts the staging chatbot outside a workspace', () => {
		const syncAIHubChatbot = loadChatbot(STAGING_FARO_URL);

		syncAIHubChatbot(undefined);

		expect(getReferenceCode()).toBe(STAGING_CHATBOT);
	});

	it('tolerates a trailing slash on the reported URL', () => {
		const syncAIHubChatbot = loadChatbot(`${STAGING_FARO_URL}/`);

		syncAIHubChatbot(undefined);

		expect(getScript()).not.toBeNull();
	});

	it('mounts the internal chatbot on the workspace it is provisioned for', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getLink()).not.toBeNull();
		expect(getReferenceCode()).toBe(INTERNAL_CHATBOT);
	});

	// Internal hosts workspaces the chatbot was not provisioned for, and it has
	// to stay off every one of them.

	it('mounts nothing on another workspace of the internal environment', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(OTHER_WORKSPACE);

		expect(getLink()).toBeNull();
		expect(getScript()).toBeNull();
	});

	it('mounts nothing outside a workspace on the internal environment', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(undefined);

		expect(getLink()).toBeNull();
		expect(getScript()).toBeNull();
	});

	it('mounts nothing on production', () => {
		const syncAIHubChatbot = loadChatbot(PRODUCTION_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getLink()).toBeNull();
		expect(getScript()).toBeNull();
	});

	// The dev server reports its own origin as `faroURL`, which is why the
	// widget does not load in development and these tests are what cover it.

	it('mounts nothing against a dev server origin', () => {
		const syncAIHubChatbot = loadChatbot(DEV_SERVER_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getLink()).toBeNull();
		expect(getScript()).toBeNull();
	});

	// Each environment's chatbot is provisioned on its own AI Hub, so the host
	// the widget loads from travels with the chatbot.

	it('serves each chatbot from the AI Hub it was provisioned on', () => {
		loadChatbot(STAGING_FARO_URL)(undefined);

		expect(getScript().getAttribute('ai-hub-url')).toBe(STAGING_AI_HUB_URL);
		expect(getScript().getAttribute('src')).toBe(
			`${STAGING_AI_HUB_URL}/documents/d/global/index-js`
		);
		expect(getLink().getAttribute('href')).toBe(
			`${STAGING_AI_HUB_URL}/documents/d/global/index-css`
		);

		document.body.innerHTML = '';
		document.head.innerHTML = '';

		loadChatbot(INTERNAL_FARO_URL)(INTERNAL_WORKSPACE);

		expect(getScript().getAttribute('ai-hub-url')).toBe(
			INTERNAL_AI_HUB_URL
		);
		expect(getScript().getAttribute('src')).toBe(
			`${INTERNAL_AI_HUB_URL}/documents/d/global/index-js`
		);
		expect(getLink().getAttribute('href')).toBe(
			`${INTERNAL_AI_HUB_URL}/documents/d/global/index-css`
		);
	});

	// The whole point of driving this from the router: the widget has to follow
	// a client side navigation, which never re-evaluates the module.

	it('unmounts the widget when navigation leaves the workspace', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		renderWidgetHost();

		syncAIHubChatbot(OTHER_WORKSPACE);

		expect(document.getElementById(CHATBOT_HOST_ID)).toBeNull();
		expect(getLink()).toBeNull();
		expect(getScript()).toBeNull();
	});

	it('mounts the widget when navigation reaches the workspace', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(undefined);

		expect(getScript()).toBeNull();

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getReferenceCode()).toBe(INTERNAL_CHATBOT);
	});

	// The widget's own guard is the presence of its host, so a second script
	// tag would be inert — but it would also leave the page with two of them.

	it('leaves the widget alone while navigation stays in the workspace', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		const script = getScript();

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(document.querySelectorAll(`#${CHATBOT_SCRIPT_ID}`)).toHaveLength(
			1
		);
		expect(getScript()).toBe(script);
	});

	it('remounts the widget on returning to the workspace', () => {
		const syncAIHubChatbot = loadChatbot(INTERNAL_FARO_URL);

		syncAIHubChatbot(INTERNAL_WORKSPACE);

		renderWidgetHost();

		syncAIHubChatbot(OTHER_WORKSPACE);
		syncAIHubChatbot(INTERNAL_WORKSPACE);

		expect(getReferenceCode()).toBe(INTERNAL_CHATBOT);
	});

	it('styles the toggle once the widget builds its shadow root', async () => {
		const syncAIHubChatbot = loadChatbot(STAGING_FARO_URL);

		syncAIHubChatbot(undefined);

		const host = renderWidgetHost();

		await Promise.resolve();

		// Spreading a shadow root NodeList loses the nodes in this jsdom, so
		// read the style element straight off the root.

		expect(host.shadowRoot.querySelector('style').textContent).toContain(
			'#aihub-chatbot-widget'
		);
	});
});
