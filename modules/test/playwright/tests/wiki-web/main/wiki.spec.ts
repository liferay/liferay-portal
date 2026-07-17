/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {wikiPagesTest} from '../../../fixtures/wikiPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-35013': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	wikiPagesTest
);

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

export const testWithWikiDeprecation = mergeTests(
	featureFlagsTest({
		'LPD-35013': {enabled: false},
	}),
	loginTest(),
	productMenuPageTest,
	wikiPagesTest
);

test(
	'Icon menu should close when another icon menu is open',
	{
		tag: '@LPD-26435',
	},
	async ({page, site, wikiPage}) => {
		await wikiPage.goto(site.friendlyUrlPath);

		await wikiPage.createNewWikiNode('Wiki Node Title');

		const wikiNodeMenu = page.locator(
			'[id="_com_liferay_wiki_web_portlet_WikiAdminPortlet_wikiNodes_1_menu"]'
		);

		await test.step('Check menu gets closed', async () => {
			const menuOne = page.locator(
				'[aria-labelledby="_com_liferay_wiki_web_portlet_WikiAdminPortlet_wikiNodes_1_menu"]'
			);

			await clickAndExpectToBeVisible({
				autoClick: false,
				target: menuOne,
				trigger: wikiNodeMenu,
			});

			await page
				.locator(
					'[id="_com_liferay_wiki_web_portlet_WikiAdminPortlet_wikiNodes_2_menu"]'
				)
				.click();

			const menuTwo = page.locator(
				'[aria-labelledby="_com_liferay_wiki_web_portlet_WikiAdminPortlet_wikiNodes_2_menu"]'
			);

			await expect(menuTwo).toBeVisible();

			await expect(menuOne).toBeHidden();
		});
	}
);

test('LPD-28898 Image added via rich text editor on Wiki tool losing reference', async ({
	page,
	site,
	wikiPage,
}) => {
	await wikiPage.goto(site.friendlyUrlPath);

	await wikiPage.goToWikiNode('Main');

	await wikiPage.goToEditFirstWikiPage();

	const image =
		'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOAAAADgCAMAAAAt85rTAAAAeFBMVEX///8LY84AXM+frr4AYM0AWcwAXMwAUspRgta9zO2NquIAYc0AXs35+ffL0tqotsS5zOkRadH///sAV8u0xefv8/sueNOyye2HpN5vldjA0efu8fR5ntnn7PJjktiowuVfjNfR3ezX4OqOqtpIhdcob9CNseMYbtMc8dVDAAADbUlEQVR4nO2da3OqMBBAQwv4CGoFi4+qtbW2//8fFmE6t9mAo3cwpMw5H3e2jqcwCYlLVikAAAAAAAAAAAAAAAAAAAAAAAAAAACAP8hkOnt0xGw6ca73vAyyB2dkyfLZrd9TnoWBQ3SWP7n1S1zqnUlcGj679zsburtLl5l7vyDIlq78JonuQjAMXI2l004uYHEJp44EZw/dCD7MHAk+diX4iCCCCCKIIII+CuokNqnWUlpEq0c8K/naB7/uBHVwWI1/sxqW4dyMjvfzwkXP12Z0lV9p2J1gctiJlLe8uIbZJjWjL9u4SN6+mNF0c+WzbXeC8djKGRaCAyu6iIIgGlnhQW8ERwgi6AIEFYImCCLYLggqBE0QRLBdbMGVTElLwVSGy9XEwkr2XjAcvomvvDkVi9joVRju9kmxHtyLxWP6GvkuGASnocnpHNRfIppXybkIf3m/oi+uoaSM6tqolez/nowjELyfoM4GJlF528UieqkyQ0cy2bpzO9w2zDdmRvp6HjjiTzGKvn80Guq5HHI31m5il/OgnPGqedD6TX3ROCGEQ+sjVrE/grc8yTQJWsljBO8FggpBBBWCdwRBhSCCCsE7gqDqm2CyFoUTaldWWbyL6OQov/M/wVwWarysZeV7hwve+XZksNiXX/pjYYaPl/bP9mbyYjv3Z8Eb6CQyqf75oYherGmSH2Ens+mE4H8L6rj2Fq3HSq52oqy73KNbtBhkxAixv+D3dRQjUrXXJgaZkU+DTLKvnSbqiY9ys+09832aaJjo66n/fdDviR5BBBFEsE0QVAiaIIhguyCoEDTxX7C+CKFB0CrGmwx8L0JoKCOpJ/wQu4npZ3xeBvtcRtJUCNRgKJPLK+V1IZAjEHQueFM5ZX1FpteC1xbEVttntTW1fgveUtKsT/J92DdrpvFO8JaidHseVB7Ng02C1l82v1bg95MMgj8g+BsE7waCCAoQRLBdEERQ0DNBazXxZwVrjjwq1oNx7ZFHYS7eklW7gz9VFg1Yh1atLx1aNRTnXh0Cj7YNmwxvOXYsrE32W7BlEEQQQQQRRNAHwd4fJN77o+AngdNmGj/oxFljlL63Y+h/Q43et0Qpm9o4baoROm5qc25LlLhsSxS4bkuket9YCgAAAAAAAAAAAAAAAAAAAAAAAAAAAKAFvgGY6WrR7U77yAAAAABJRU5ErkJggg==';

	await wikiPage.addSourceContentToWikiPage(`{{${image}|alternate text}}`);

	const ckeditorIframeLocator = page.frameLocator(
		'iframe[title="Editor, _com_liferay_wiki_web_portlet_WikiAdminPortlet_contentEditor"]'
	);

	await wikiPage.assertAddedImage(
		ckeditorIframeLocator.getByAltText('alternate text'),
		image
	);

	await wikiPage.publishPage();

	await wikiPage.goto(site.friendlyUrlPath);

	await wikiPage.goToWikiNode('Main');

	await wikiPage.goToFirstWikiPage();

	await wikiPage.assertAddedImage(page.getByAltText('alternate text'), image);
});

test(
	'Add deprecation flag Wiki with FF enabled',
	{
		tag: '@LPD-37306',
	},
	async ({page, site, wikiPage}) => {
		await wikiPage.goto(site.friendlyUrlPath);

		await wikiPage.createNewWikiNode('Wiki Node Title');

		await expect(
			page.getByRole('link', {exact: true, name: 'Wiki Node Title'})
		).toBeVisible();

		await expect(page.getByText('Deprecated')).toBeVisible();
	}
);

testWithWikiDeprecation(
	'Add deprecation flag Wiki with FF disabled',
	{
		tag: '@LPD-37306',
	},
	async ({page, productMenuPage}) => {
		await productMenuPage.openProductMenuIfClosed();

		await expect(
			page.getByRole('menuitem', {
				name: 'Content & Data',
			})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {
				name: 'Wiki',
			})
		).not.toBeVisible();
	}
);

test('Adds an image to a wiki page through the URL tab', async ({
	apiHelpers,
	page,
	site,
	wikiPage,
}) => {
	const fileName = `${getRandomString()}.png`;

	// Seed a document to reference by its URL

	const document = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(SAMPLE_IMAGE),
		{fileName}
	);

	// Open the wiki page editor and insert the document image via the URL tab

	await wikiPage.goto(site.friendlyUrlPath);

	await wikiPage.goToWikiNode('Main');

	await wikiPage.goToEditFirstWikiPage();

	await page.getByRole('button', {name: 'Image'}).click();

	const itemSelector = page.frameLocator('iframe[title="Select Item"]');

	await itemSelector.getByRole('link', {name: 'URL'}).click();

	await itemSelector.getByPlaceholder('http://').fill(document.contentUrl);

	const addImageButton = itemSelector.getByRole('button', {name: 'Add'});

	await expect(addImageButton).toBeEnabled();

	await addImageButton.click();

	const contentEditor = page.frameLocator(
		'iframe[title="Editor, _com_liferay_wiki_web_portlet_WikiAdminPortlet_contentEditor"]'
	);

	await expect(
		contentEditor.locator(`img[src*="${fileName}"]`)
	).toBeVisible();

	// Publish, reopen the page, and verify the image renders

	await wikiPage.publishPage();

	await wikiPage.goto(site.friendlyUrlPath);

	await wikiPage.goToWikiNode('Main');

	await wikiPage.goToFirstWikiPage();

	await expect(page.locator(`img[src*="${fileName}"]`)).toBeVisible();
});
