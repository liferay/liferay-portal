/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Request, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import isSPAEnabled from '../../../utils/isSPAEnabled';

export const testStyles = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

export const testActionScreen = mergeTests(
	isolatedLayoutTest({publish: false}),
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest
);

testStyles(
	'Assert it appends existing temporary styles with id in the same place as the reference upon navigation',
	{
		tag: '@LPD-49303',
	},
	async ({apiHelpers, page, site}) => {
		const firstLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: 'LPD-49303-firstLayout',
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: 'LPD-49303-secondLayout',
		});

		await testStyles.step(
			'Navigate to root page and assert SPA is enabled',
			async () => {
				await page.goto('/');

				expect(isSPAEnabled({page})).toBeTruthy();
			}
		);

		await testStyles.step(
			'Navigate to first page and assert body background color',
			async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}${firstLayout.friendlyURL}`
				);

				const bodyBackgroundColor = await page.evaluate(() => {
					return window.getComputedStyle(document.body)[
						'background-color'
					];
				});

				expect(bodyBackgroundColor).toEqual('rgb(0, 0, 255)');
			}
		);

		await testStyles.step(
			'Navigate to second page and assert styles are applied according to the order in DOM',
			async () => {

				// Navigate to second page using SPA. If we use page.goto() is loading a new page

				const secondPageMenuItem = page.getByRole('menuitem', {
					name: 'LPD-49303-secondLayout',
				});

				// Install a Promise in the browser that resolves when endNavigate
				// is fired. This is needed because endNavigate happens after
				// styles are evaluated by SPA. Otherwise, asserting the expected
				// style might happen before SPA applies the styles

				await page.evaluate(() => {
					window['stylePromise'] = new Promise((resolve) => {
						Liferay.once('endNavigate', () => {
							resolve({
								bodyBackgroundColor: window.getComputedStyle(
									document.body
								)['background-color'],
								bodyColor: window.getComputedStyle(
									document.body
								)['color'],
							});
						});
					});
				});

				await secondPageMenuItem.click();

				// Wait for the Promise we installed a few lines above then return
				// its value to Playwright domain.

				const {bodyBackgroundColor, bodyColor} = await page.evaluate(
					() => window['stylePromise']
				);

				expect(bodyBackgroundColor).toEqual('rgb(0, 0, 255)');

				expect(bodyColor).toEqual('rgb(1, 2, 3)');
			}
		);
	}
);

testActionScreen(
	'ActionScreen is not called when URL targets excluded portlets',
	{tag: '@LPD-55693'},
	async ({layout, page, pageEditorPage}) => {
		interface TestRequest {
			key: string;
			resolveMethod: Function | undefined;
			resolveMethodCalled: boolean;
		}

		const buildURL = (
			ppid: string,
			redirectParameterName?: string,
			redirectURL?: string
		) => {
			const url = new URL(page.url());

			url.searchParams.set('p_p_lifecycle', '1');
			url.searchParams.set('p_p_id', ppid);
			if (redirectParameterName) {
				url.searchParams.set(
					'_' + ppid + '_' + redirectParameterName,
					redirectURL
				);
			}

			return url.href;
		};

		const portletId = 'com_liferay_FooPortlet';

		const excludedPortletId =
			'com_liferay_server_admin_web_portlet_ServerAdminPortlet';

		const portletURL = buildURL(portletId);

		const excludedPortletURL = buildURL(excludedPortletId);

		const testURLs = {
			url: portletURL,
			url_backURL: buildURL(portletId, 'backURL', portletURL),
			url_backURL_excluded: buildURL(
				portletId,
				'backURL',
				excludedPortletURL
			),
			url_excluded: excludedPortletURL,
			url_excluded_backURL: buildURL(
				excludedPortletId,
				'backURL',
				portletURL
			),
			url_excluded_plbackURL: buildURL(
				excludedPortletId,
				'p_l_back_url',
				portletURL
			),
			url_excluded_redirect: buildURL(
				excludedPortletId,
				'redirect',
				portletURL
			),
			url_plbackURL: buildURL(portletId, 'p_l_back_url', portletURL),
			url_plbackURL_excluded: buildURL(
				portletId,
				'p_l_back_url',
				excludedPortletURL
			),
			url_redirect: buildURL(portletId, 'redirect', portletURL),
			url_redirect_excluded: buildURL(
				portletId,
				'redirect',
				excludedPortletURL
			),
			url_redirect_redirect_excluded: buildURL(
				portletId,
				'redirect',
				buildURL(portletId, 'redirect', excludedPortletURL)
			),
		};

		const buildTestMarkup = (): string => {
			const markup = [];

			markup.push('<div id="sampleLinkContainer">\n');
			markup.push('	<ul>\n');

			for (const [key, value] of Object.entries(testURLs)) {
				markup.push('	<li>\n');
				markup.push(`		<a id="${key}" href="${value}">${key}</a>\n`);
				markup.push('	</li>\n');
			}
			markup.push('	<ul>\n');
			markup.push('</div>');

			return markup.join('');
		};

		await testStyles.step('Prepare HTML content with links', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addFragment('Basic Components', 'HTML');

			const htmlFragmentId = await pageEditorPage.getFragmentId('HTML');

			await pageEditorPage.selectEditable(htmlFragmentId, 'element-html');

			await page.getByText('HTML Example').click();

			const htmlEditor = page.getByText('<h1>HTML Example</h1>');

			await htmlEditor.click();

			await page.keyboard.press('Control+A');

			await page.keyboard.press('Backspace');

			await page.keyboard.insertText(buildTestMarkup());

			await page.getByRole('button', {name: 'Save'}).click();

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();

			await page.goto(`/web/guest/${layout.friendlyURL}`);
		});

		await testStyles.step(
			'Navigate and assert ActionScreen manages the request only if portlet is not excluded',
			async () => {
				const testRequests: Record<string, TestRequest> = {};

				for (const [key, url] of Object.entries(testURLs)) {
					testRequests[url as string] = {
						key,
						resolveMethod: undefined,
						resolveMethodCalled: false,
					};
				}

				page.on('request', async (request: Request) => {
					const testRequest = testRequests[request.url()];

					if (
						testRequest &&
						testRequest.resolveMethod &&
						!testRequest.resolveMethodCalled
					) {
						testRequest.resolveMethod(request.method());

						// we want to resolve promise just once as server
						// will redirect POST requests into GET ones

						testRequest.resolveMethodCalled = true;
					}
				});

				const container = page.locator('#sampleLinkContainer');

				for (const testRequest of Object.values(testRequests)) {
					const methodPromise = new Promise((resolve) => {

						// store promise method resolve function in the test object
						// this needs to be done before link is clicked, so that
						// request handler can call the resolve function on
						// behalf of this promise instance

						testRequest.resolveMethod = resolve;
					});

					await page.goto(`/web/guest/${layout.friendlyURL}`);

					await container
						.getByRole('link', {
							exact: true,
							name: testRequest.key,
						})
						.click();

					expect(await methodPromise).toEqual(
						testRequest.key.includes('excluded') ? 'GET' : 'POST'
					);
				}
			}
		);
	}
);
