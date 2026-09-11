const INTERNAL_FARO_URL = 'https://ldp-internal.liferay.com';
const PRODUCTION_FARO_URL = 'https://ldp.liferay.com';
const STAGING_CHATBOT = 'chatbot-ldp-stg-liferay-com';
const STAGING_FARO_URL = 'https://ldp-stg.liferay.com';

describe('AI Hub chatbot', () => {
	const originalFaroEnv = global.FARO_ENV;
	const originalFaroURL = global.faroConstants.faroURL;

	afterEach(() => {
		global.FARO_ENV = originalFaroEnv;
		global.faroConstants.faroURL = originalFaroURL;
	});

	// The descriptors are decided when the module is evaluated, so each case
	// has to set the environment up before loading it again.

	function loadChatbot(faroEnv, faroURL) {
		global.FARO_ENV = faroEnv;
		global.faroConstants.faroURL = faroURL;

		let chatbot;

		jest.isolateModules(() => {
			chatbot = require('../ai-hub-chatbot');
		});

		return chatbot;
	}

	it('opens the staging chatbot on staging', () => {
		const {AI_HUB_CHATBOT_LINKS, AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'stg',
			STAGING_FARO_URL
		);

		expect(AI_HUB_CHATBOT_LINKS).toHaveLength(1);
		expect(AI_HUB_CHATBOT_SCRIPTS).toHaveLength(1);
		expect(
			AI_HUB_CHATBOT_SCRIPTS[0].attributes[
				'chatbot-external-reference-code'
			]
		).toBe(STAGING_CHATBOT);
	});

	it('tolerates a trailing slash on the reported URL', () => {
		const {AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'stg',
			`${STAGING_FARO_URL}/`
		);

		expect(AI_HUB_CHATBOT_SCRIPTS).toHaveLength(1);
	});

	// The internal environment has no chatbot provisioned yet, and the build
	// time environment name cannot be relied on to tell it apart from staging.
	// The URL the portal reports for itself is what has to keep the widget off
	// that environment, even when the two are built the same way.

	it('loads nothing on the internal environment built as staging', () => {
		const {AI_HUB_CHATBOT_LINKS, AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'stg',
			INTERNAL_FARO_URL
		);

		expect(AI_HUB_CHATBOT_LINKS).toEqual([]);
		expect(AI_HUB_CHATBOT_SCRIPTS).toEqual([]);
	});

	it('loads nothing on the internal environment built as internal', () => {
		const {AI_HUB_CHATBOT_LINKS, AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'internal',
			INTERNAL_FARO_URL
		);

		expect(AI_HUB_CHATBOT_LINKS).toEqual([]);
		expect(AI_HUB_CHATBOT_SCRIPTS).toEqual([]);
	});

	it('loads nothing on production', () => {
		const {AI_HUB_CHATBOT_LINKS, AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'prd',
			PRODUCTION_FARO_URL
		);

		expect(AI_HUB_CHATBOT_LINKS).toEqual([]);
		expect(AI_HUB_CHATBOT_SCRIPTS).toEqual([]);
	});

	// Development proxies to a backend whose URL the dev server rewrites to its
	// own origin, so the environment name is the only signal left there.

	it('opens the staging chatbot in development, whichever URL is reported', () => {
		const {AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'local',
			'http://localhost:3000'
		);

		expect(AI_HUB_CHATBOT_SCRIPTS).toHaveLength(1);
		expect(
			AI_HUB_CHATBOT_SCRIPTS[0].attributes[
				'chatbot-external-reference-code'
			]
		).toBe(STAGING_CHATBOT);
	});

	it('points the widget at the dev server proxy in development', () => {
		const {AI_HUB_CHATBOT_SCRIPTS} = loadChatbot(
			'local',
			'http://localhost:3000'
		);

		expect(AI_HUB_CHATBOT_SCRIPTS[0].attributes['ai-hub-url']).toBe(
			'/__aihub__'
		);
	});

	it('adds no styles when the widget is not loaded', () => {
		const {styleAIHubChatbot} = loadChatbot('stg', INTERNAL_FARO_URL);

		styleAIHubChatbot();

		expect(document.getElementById('aihub-chatbot-host')).toBeNull();
	});
});
