const BASE_URL = new URL(import.meta.url.replace(/\?.*/, '') + '/../../../..');

const {audiences} = await import(`${BASE_URL}o/frontend-js-audiences-web/__liferay__/index.js`);

audiences.setLogEnabled([$ENABLE_LOG$]);

const DEFINITION_URL = `${BASE_URL}o/audiences/definition.([$AUDIENCES_DEFINITION_HASH$]).json`;

let currentNavigationId = 0;

async function runAudiences() {

	// A rapid sequence of SPA navigations can start several runAudiences() in
	// parallel, since the endNavigate listener is not awaited. Tag each run and
	// bail after every await once a newer navigation has started, so only the
	// latest navigation registers handlers and applies variations.

	const navigationId = ++currentNavigationId;

	// Always start the navigation from a clean handler set, even when the page
	// has no variations, so a previous page's handlers never linger.

	audiences.clearHandlers();

	const meta = document.head.querySelector(
		'meta[name="audiences-variations"]'
	);

	if (!meta) {
		return;
	}

	const [plid, segmentsExperienceId, elementVariationsHash] =
		meta.content.split(':');

	const variations = await import(
		`${BASE_URL}o/audiences/${plid}/${segmentsExperienceId}/variations.(${elementVariationsHash}).js`
	);

	if (navigationId !== currentNavigationId) {
		return;
	}

	variations.register();

	audiences.clear();

	await audiences.runDetection(DEFINITION_URL);

	if (navigationId !== currentNavigationId) {
		return;
	}

	await audiences.runHandlers();
}

await runAudiences();

Liferay.on('endNavigate', runAudiences);