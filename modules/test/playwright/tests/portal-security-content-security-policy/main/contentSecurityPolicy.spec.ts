/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {contentSecurityPolicyPagesTest} from '../../../fixtures/contentSecurityPolicyPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {ContentSecurityPolicyPage} from '../../../pages/portal-security-content-security-policy/ContentSecurityPolicyPage';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

export const test = mergeTests(
	apiHelpersTest,
	contentSecurityPolicyPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	virtualInstancesPagesTest
);

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

const defaultBaseUrl = liferayConfig.environment.baseUrl;

let hasVirtualInstance: boolean = false;

test.afterEach(
	'Reset CSP configuration',
	async ({contentSecurityPolicyPage, page, virtualInstancesPage}) => {
		test.setTimeout(180000);

		liferayConfig.environment.baseUrl = defaultBaseUrl;

		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isVisible()) {
			await performLoginViaApi({page, screenName: 'test'});
		}

		if (hasVirtualInstance) {
			await virtualInstancesPage.deleteVirtualInstance(
				DEFAULT_VIRTUAL_INSTANCE_NAME
			);

			hasVirtualInstance = false;
		}
		else {
			await contentSecurityPolicyPage.goto();

			await contentSecurityPolicyPage.resetCSPConfiguration();
		}
	}
);

test("CSP connect-src allows connections to 'self'", async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`connect-src 'self'`
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	const portalURL = liferayConfig.environment.baseUrl;

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<a  ping="${portalURL}" href="${portalURL}" target="_blank">
		 			Test CSP
					<script>
						const response = fetch("${portalURL}");

						const xmlHttpRequest = new XMLHttpRequest();
						xmlHttpRequest.open("GET", "${portalURL}");
						xmlHttpRequest.send();

						const webSocket = new WebSocket("${portalURL}");

						const eventSource = new EventSource("${portalURL}");

						navigator.sendBeacon("${portalURL}", {
						/* … */
						});
					</script>
				</a>`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (msg) => {
		if (
			msg.type() === 'error' &&
			msg
				.text()
				.includes('Content Security Policy directive: "connect-src')
		) {
			errors.push({text: msg.text(), type: msg.type()});
		}
	});

	page.on('requestfailed', (request) => {
		if (
			request.url().includes('localhost') &&
			request.failure().errorText.includes('csp')
		) {
			errors.push({
				failure: request.failure(),
				url: request.url(),
			});
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	await page.getByRole('link', {name: 'Test CSP'}).click({
		button: 'middle',
	});

	expect(errors).toHaveLength(0);
});

test('CSP connect-src allows connections to specific domain', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`connect-src 'self' http://www.able.com:${liferayConfig.environment.port}/ wss://www.able.com:${liferayConfig.environment.port}/;`
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<a  ping="http://www.able.com:${liferayConfig.environment.port}" href="http://localhost:${liferayConfig.environment.port}" target="_blank">
		 			Test CSP
					<script>
						const response = fetch("http://www.able.com:${liferayConfig.environment.port}");

						const xmlHttpRequest = new XMLHttpRequest();
						xmlHttpRequest.open("GET", "http://www.able.com:${liferayConfig.environment.port}");
						xmlHttpRequest.send();

						const webSocket = new WebSocket("wss://www.able.com:${liferayConfig.environment.port}/");

						const eventSource = new EventSource("http://www.able.com:${liferayConfig.environment.port}");

						navigator.sendBeacon("http://www.able.com:${liferayConfig.environment.port}", {
						/* … */
						});
					</script>
				</a>`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (msg) => {
		if (
			msg.type() === 'error' &&
			msg
				.text()
				.includes('Content Security Policy directive: "connect-src')
		) {
			errors.push({text: msg.text(), type: msg.type()});
		}
	});

	page.on('requestfailed', (request) => {
		if (
			request.url().includes('www.able.com') &&
			request.failure().errorText.includes('csp')
		) {
			errors.push({
				failure: request.failure(),
				url: request.url(),
			});
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	await page.getByRole('link', {name: 'Test CSP'}).click({
		button: 'middle',
	});

	expect(errors).toHaveLength(0);
});

test('CSP connect-src blocks connections', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`connect-src 'self';`
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<a  ping="http://www.able.com:${liferayConfig.environment.port}" href="http://localhost:${liferayConfig.environment.port}" target="_blank">
		 			Test CSP
					<script>
						const response = fetch("http://www.able.com:${liferayConfig.environment.port}");

						const xmlHttpRequest = new XMLHttpRequest();
						xmlHttpRequest.open("GET", "http://www.able.com:${liferayConfig.environment.port}");
						xmlHttpRequest.send();

						const webSocket = new WebSocket("wss://www.able.com:${liferayConfig.environment.port}/");

						const eventSource = new EventSource("http://www.able.com:${liferayConfig.environment.port}");

						navigator.sendBeacon("http://www.able.com:${liferayConfig.environment.port}", {
						/* … */
						});
					</script>
				</a>`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (msg) => {
		if (
			msg.type() === 'error' &&
			msg
				.text()
				.includes('Content Security Policy directive: "connect-src')
		) {
			errors.push({text: msg.text(), type: msg.type()});
		}
	});

	page.on('requestfailed', (request) => {
		if (
			request.url().includes('www.able.com') &&
			request.failure().errorText.includes('csp')
		) {
			errors.push({
				failure: request.failure(),
				url: request.url(),
			});
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	await page.getByRole('link', {name: 'Test CSP'}).click({
		button: 'middle',
	});

	expect(errors.length).toBeGreaterThanOrEqual(9);
});

test('CSP-Report-Only connect-src alerts connections', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`connect-src 'self';`,
		true
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<a  ping="http://www.able.com:${liferayConfig.environment.port}" href="http://localhost:${liferayConfig.environment.port}" target="_blank">
		 			Test CSP
					<script>
						const response = fetch("http://www.able.com:${liferayConfig.environment.port}");

						const xmlHttpRequest = new XMLHttpRequest();
						xmlHttpRequest.open("GET", "http://www.able.com:${liferayConfig.environment.port}");
						xmlHttpRequest.send();

						const webSocket = new WebSocket("wss://www.able.com:${liferayConfig.environment.port}/");

						const eventSource = new EventSource("http://www.able.com:${liferayConfig.environment.port}");

						navigator.sendBeacon("http://www.able.com:${liferayConfig.environment.port}", {
						/* … */
						});
					</script>
				</a>`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	const logs = [];

	page.on('console', (msg) => {
		if (
			msg.type() === 'info' &&
			msg.text().includes('[Report Only] Refused to connect to') &&
			msg
				.text()
				.includes('Content Security Policy directive: "connect-src')
		) {
			logs.push({text: msg.text(), type: msg.type()});
		}
	});

	page.on('requestfailed', (request) => {
		if (
			request.url().includes('www.able.com') &&
			request.failure().errorText.includes('csp')
		) {
			errors.push({
				failure: request.failure(),
				url: request.url(),
			});
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	await page.getByRole('link', {name: 'Test CSP'}).click({
		button: 'middle',
	});

	expect(errors).toHaveLength(0);
	expect(logs.length).toBeGreaterThanOrEqual(7);
});

test('CSP frame-ancestors allows framing from specific domain', async ({
	apiHelpers,
	browser,
	page,
	pageEditorPage,
	site,
	virtualInstancesPage,
}) => {
	test.setTimeout(360000);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<object
					type="text/html"
					data="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}"
					width="300"
					height="200">
				</object>

				<embed
					type="text/html"
					src="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}"
					width="300"
					height="200" />

				<iframe
					id="inlineFrameExample"
					title="Inline Frame Example"
					width="300"
					height="200"
					src="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}">
				</iframe>`,
	});

	await pageEditorPage.publishPage();

	await virtualInstancesPage.addNewVirtualInstance(
		DEFAULT_VIRTUAL_INSTANCE_NAME
	);

	hasVirtualInstance = true;

	liferayConfig.environment.baseUrl = `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`;

	const newInstancePage = await browser.newPage({
		baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`,
	});

	await performLoginViaApi({
		domain: `@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`,
		page: newInstancePage,
		screenName: 'test',
	});

	const virtualInstanceContentSecurityPolicyPage =
		new ContentSecurityPolicyPage(newInstancePage);

	await virtualInstanceContentSecurityPolicyPage.gotoAndConfigurePolicy(
		`frame-ancestors 'self' ${defaultBaseUrl};`
	);

	await expect(async () => {
		liferayConfig.environment.baseUrl = defaultBaseUrl;

		const errors = [];

		page.on('console', (msg) => {
			if (
				msg.type() === 'error' &&
				msg
					.text()
					.includes(
						`Content Security Policy directive: "frame-ancestors`
					)
			) {
				errors.push({text: msg.text(), type: msg.type()});
			}
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`, {
			waitUntil: 'domcontentloaded',
		});

		expect(errors).toHaveLength(0);
	}).toPass();
});

test('CSP frame-ancestors blocks framing from specific domain', async ({
	apiHelpers,
	browser,
	page,
	pageEditorPage,
	site,
	virtualInstancesPage,
}) => {
	test.setTimeout(360000);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<object
					type="text/html"
					data="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}"
					width="300"
					height="200">
				</object>

				<embed
					type="text/html"
					src="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}"
					width="300"
					height="200" />

				<iframe
					id="inlineFrameExample"
					title="Inline Frame Example"
					width="300"
					height="200"
					src="http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}">
				</iframe>`,
	});

	await pageEditorPage.publishPage();

	await virtualInstancesPage.addNewVirtualInstance(
		DEFAULT_VIRTUAL_INSTANCE_NAME
	);

	hasVirtualInstance = true;

	liferayConfig.environment.baseUrl = `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`;

	const newInstancePage = await browser.newPage({
		baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:${liferayConfig.environment.port}`,
	});

	await performLoginViaApi({
		domain: `@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`,
		page: newInstancePage,
		screenName: 'test',
	});

	const virtualInstanceContentSecurityPolicyPage =
		new ContentSecurityPolicyPage(newInstancePage);

	await virtualInstanceContentSecurityPolicyPage.gotoAndConfigurePolicy(
		`frame-ancestors 'self';`
	);

	await expect(async () => {
		liferayConfig.environment.baseUrl = defaultBaseUrl;

		const errors = [];

		page.on('console', (msg) => {
			if (
				msg.type() === 'error' &&
				msg
					.text()
					.includes(
						`Content Security Policy directive: "frame-ancestors`
					)
			) {
				errors.push({text: msg.text(), type: msg.type()});
			}
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		expect(errors.length).toBeGreaterThanOrEqual(5);
	}).toPass();
});

test('CSP frame-ancestors directive in the same instance', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<object
					type="text/html"
					data="${liferayConfig.environment.baseUrl}"
					width="300"
					height="200">
				</object>

				<embed
					type="text/html"
					src="${liferayConfig.environment.baseUrl}"
					width="300"
					height="200" />

				<iframe
					id="inlineFrameExample"
					title="Inline Frame Example"
					width="300"
					height="200"
					src="${liferayConfig.environment.baseUrl}">
				</iframe>`,
	});

	await pageEditorPage.publishPage();

	await test.step('CSP frame-ancestors allows framing from same instance', async () => {
		await contentSecurityPolicyPage.gotoAndConfigurePolicy(
			`frame-ancestors 'self';`
		);

		const errors = [];

		page.on('console', (msg) => {
			if (
				msg.type() === 'error' &&
				msg
					.text()
					.includes(
						'Content Security Policy directive: "frame-ancestors'
					)
			) {
				errors.push({text: msg.text(), type: msg.type()});
			}
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		expect(errors).toHaveLength(0);
	});

	await test.step('CSP frame-ancestors blocks framing from same instance', async () => {
		await contentSecurityPolicyPage.gotoAndConfigurePolicy(
			`frame-ancestors 'none';`
		);

		const errors = [];

		page.on('console', (msg) => {
			if (
				msg.type() === 'error' &&
				msg
					.text()
					.includes(
						'Content Security Policy directive: "frame-ancestors'
					)
			) {
				errors.push({text: msg.text(), type: msg.type()});
			}
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		expect(errors.length).toBeGreaterThanOrEqual(4);
	});
});

test("CSP frame-src allow frames from 'self'", async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy("frame-src 'self';");

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	const portalURL = liferayConfig.environment.baseUrl;

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<iframe src="${portalURL}" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (text.includes(`Refused to frame '${portalURL}'`)) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors).toHaveLength(0);
});

test('CSP frame-src allows frames from specific domains', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`frame-src 'self' http://www.able.com:${liferayConfig.environment.port};`
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<iframe src="http://www.able.com:${liferayConfig.environment.port}/" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`Refused to frame 'http://www.able.com:${liferayConfig.environment.port}/'`
				)
			) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors).toHaveLength(0);
});

test('CSP frame-src blocks frames', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy("frame-src 'self';");

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<iframe src="http://www.able.com:${liferayConfig.environment.port}/" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`http://www.able.com:${liferayConfig.environment.port}/`
				)
			) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors.length).toBeGreaterThanOrEqual(2);
});

test('CSP-Report-Only frame-src allerts frames', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		"frame-src 'self';",
		true
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<iframe src="http://www.able.com:${liferayConfig.environment.port}/" />`,
	});

	await pageEditorPage.publishPage();

	const logs = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`http://www.able.com:${liferayConfig.environment.port}/`
				) &&
				text.includes('[Report Only] Refused to frame')
			) {
				logs.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	await expect(
		page.locator(
			`iframe[src="http://www.able.com:${liferayConfig.environment.port}/"]`
		)
	).toBeVisible();

	expect(logs.length).toBeGreaterThanOrEqual(6);
});

test("CSP img-src allow images from 'self'", async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy("img-src 'self';");

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	const portalURL = liferayConfig.environment.baseUrl;

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<img src="${portalURL}/html/icons/default.png" alt="example picture" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`Refused to load the image '${portalURL}/html/icons/default.png'`
				)
			) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors).toHaveLength(0);
});

test('CSP img-src allows images from specific domains', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		`img-src 'self' http://www.able.com:${liferayConfig.environment.port};`
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<img src="http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png" alt="example picture" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`Refused to load the image 'http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png'`
				)
			) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors).toHaveLength(0);
});

test('CSP img-src blocks images', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy("img-src 'self';");

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<img src="http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png" alt="example picture" />`,
	});

	await pageEditorPage.publishPage();

	const errors = [];

	page.on('console', (message) => {
		if (message.type() === 'error') {
			const text = message.text();
			if (
				text.includes(
					`http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png`
				)
			) {
				errors.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(errors.length).toBeGreaterThanOrEqual(1);
});

test('CSP-Report-Only img-src alerts images', async ({
	apiHelpers,
	contentSecurityPolicyPage,
	page,
	pageEditorPage,
	site,
}) => {
	await contentSecurityPolicyPage.gotoAndConfigurePolicy(
		"img-src 'self';",
		true
	);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-html',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.editHTMLEditable({
		editableId: 'element-html',
		fragmentId: await pageEditorPage.getFragmentId('HTML'),
		value: `<img src="http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png" alt="example picture" />`,
	});

	await pageEditorPage.publishPage();

	const logs = [];

	page.on('console', (message) => {
		if (message.type() === 'info') {
			const text = message.text();
			if (
				text.includes(
					`http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png`
				) &&
				text.includes('[Report Only] Refused to load the image')
			) {
				logs.push(text);
			}
		}
	});

	await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

	expect(logs.length).toBeGreaterThanOrEqual(1);

	await expect(
		page.locator(
			`img[src="http://www.able.com:${liferayConfig.environment.port}/html/icons/default.png"]`
		)
	).toBeVisible();
});
