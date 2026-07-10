const BASE_URL = new URL(import.meta.url.replace(/\?.*/, '') + '/../../../..');

const {audiences} = await import(`${BASE_URL}o/frontend-js-audiences-web/__liferay__/index.js`);

audiences.setLogEnabled([$ENABLE_LOG$]);

const DEFINITION_URL = `${BASE_URL}o/audiences/definition.([$AUDIENCES_DEFINITION_HASH$]).json`;

async function runAudiences() {
	const meta = document.head.querySelector(
		'meta[name="audiences-variations"]'
	);

	// A page without element variations has no metadata. There is nothing to
	// apply, so leave any previously registered handlers untouched.

	if (!meta) {
		return;
	}

	const [plid, elementVariationsHash] = meta.content.split(':');

	audiences.clearHandlers();

	const variations = await import(
		`${BASE_URL}o/audiences/${plid}/variations.(${elementVariationsHash}).js`
	);

	variations.register();

	audiences.clear();

	await audiences.runDetection(DEFINITION_URL);

	await audiences.runHandlers();
}

await runAudiences();

Liferay.on('endNavigate', runAudiences);