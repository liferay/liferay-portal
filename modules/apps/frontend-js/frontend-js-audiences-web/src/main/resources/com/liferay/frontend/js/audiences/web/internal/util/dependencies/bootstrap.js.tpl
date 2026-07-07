const BASE_URL = new URL(import.meta.url.replace(/\?.*/, '') + '/../../../..');

const {audiences} = await import(`${BASE_URL}o/frontend-js-audiences-web/__liferay__/index.js`);

audiences.setLogEnabled([$ENABLE_LOG$]);

// Register the element variation handlers once. ES modules are evaluated at
// most once per document, so neither this bootstrap module nor the variations
// module below re-runs on SPA navigations. The handlers are therefore
// registered a single time and kept registered, while detection and handler
// execution are re-run on every navigation through the listener below.

await import(`${BASE_URL}o/audiences/[$PLID$]/variations.([$ELEMENT_VARIATIONS_HASH$]).js`);

async function runAudiences() {
	audiences.clear();

	await audiences.runDetection(
		`${BASE_URL}o/audiences/definition.([$AUDIENCES_DEFINITION_HASH$]).json`
	);

	await audiences.runHandlers();
}

// Run detection and handlers for the current page, then again after every SPA
// navigation. Senna keeps this script (data-senna-track="permanent") from
// being re-evaluated, so this listener is the only thing that runs per page.

await runAudiences();

Liferay.on('endNavigate', runAudiences);