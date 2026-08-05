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
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {watchForDialog} from '../../../utils/watchForDialog';
import {blogsPagesTest} from './fixtures/blogsPagesTest';
import {blogsCategorizedFriendlyUrlSetup} from './utils/blogsCategorizedFriendlyUrlSetup';

const PREFIX_FRIENDLY_URL = '/-/blogs/';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	blogsPagesTest,
	loginTest(),
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	})
);

test(
	'Permission sets are differing depending on autosaving of a blog entry',
	{
		tag: '@LPD-22497',
	},
	async ({blogsEditBlogEntryPage, blogsPage, page, site}) => {
		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			publish: false,
			title,
		});

		await expect(
			page.locator(
				'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_saveStatus'
			)
		).toContainText('Draft Saved at', {
			timeout: 40000,
		});

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.assertBlogEntryPermissions(
			[
				{enabled: true, locator: '#guest_ACTION_ADD_DISCUSSION'},
				{enabled: true, locator: '#guest_ACTION_VIEW'},
				{enabled: true, locator: '#site-member_ACTION_ADD_DISCUSSION'},
				{enabled: true, locator: '#site-member_ACTION_VIEW'},
			],
			title
		);
	}
);

test(
	'Select categories for the custom friendly URL',
	{
		tag: '@LPD-26752',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'category-1'},
			{name: 'category-2'},
			{name: 'category-3'},
		];

		await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: false,
			title,
		});

		await test.step('Check input addon categories preview', async () => {
			await expect(
				page.getByText(
					`${PREFIX_FRIENDLY_URL}${friendlyUrlCategories
						.map(({name}) => name)
						.join('/')}/`
				)
			).toBeVisible();
		});

		await test.step('Check categories in friendly URL', async () => {
			await blogsEditBlogEntryPage.publishBlogEntry();

			const response = await page.goto(
				`/web${site.friendlyUrlPath}/b/${title}`
			);

			await expect(response.url()).toContain(
				`/web${site.friendlyUrlPath}/b/${friendlyUrlCategories
					.map(({name}) => name)
					.join('/')}/${title}`
			);
		});
	}
);

test(
	'Categories with blank spaces in friendly URL',
	{
		tag: '@LPD-24858',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'category 1'},
			{name: 'category 2'},
			{name: 'category 3'},
		];

		await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: true,
			title,
		});

		const response = await page.goto(
			`/web${site.friendlyUrlPath}/b/${title}`
		);

		await expect(response.url()).toContain(
			`/web${site.friendlyUrlPath}/b/${friendlyUrlCategories
				.map(({name}) => encodeURIComponent(name))
				.join('/')}/${title}`
		);
	}
);

test(
	'The URL changes when a category is modified',
	{
		tag: '@LPD-26753',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'category-1'},
			{name: 'category-2'},
			{name: 'category-3'},
		];

		const {categories} = await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: true,
			title,
		});

		const initialResponse = await page.goto(
			`/web${site.friendlyUrlPath}/b/${title}`
		);

		await test.step('Check categories in friendly URL', async () => {
			await expect(initialResponse.url()).toContain(
				`/web${site.friendlyUrlPath}/b/${friendlyUrlCategories
					.map(({name}) => name)
					.join('/')}/${title}`
			);
		});

		await test.step('Check redirection and edited category in the initial friendly URL', async () => {
			friendlyUrlCategories[0].name = `${friendlyUrlCategories[0].name}-edited`;
			await apiHelpers.headlessAdminTaxonomy.patchTaxonomyCategory({
				id: categories[0].id,
				name: friendlyUrlCategories[0].name,
			});

			const editedResponse = await page.goto(initialResponse.url());
			await expect(editedResponse.url()).toContain(
				`/web${site.friendlyUrlPath}/b/${friendlyUrlCategories
					.map(({name}) => name)
					.join('/')}/${title}`
			);
		});
	}
);

test(
	'The URL redirects to the correct language when a category has a translation',
	{
		tag: '@LPS-26755',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'lifestyle', name_i18n: {['ES-es']: 'estilo-de-vida'}},
			{name: 'fashion', name_i18n: {['ES-es']: 'moda'}},
			{name: 'places', name_i18n: {['ES-es']: 'lugares'}},
		];

		await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: true,
			title,
		});

		const response = await page.goto(
			`/es/web${site.friendlyUrlPath}/b/${title}`
		);
		await expect(response.url()).toContain(
			`/es/web${site.friendlyUrlPath}/b/${friendlyUrlCategories
				.map((category) => category.name_i18n['ES-es'])
				.join('/')}/${title}`
		);

		// change back to english language

		await page.goto('/en');
	}
);

test(
	'Reorder categories in the friendly URL',
	{
		tag: '@LPD-26659',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'category-1'},
			{name: 'category-2'},
			{name: 'category-3'},
		];

		await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: false,
			title,
		});

		await page.getByLabel('In Use').selectOption('category-1');
		await page.getByLabel('Reorder Down').click();
		await page.getByLabel('Reorder Down').click();

		const expectedCategoriesPartialURL = 'category-2/category-3/category-1';

		await test.step('Check categories order in the input addon categories preview', async () => {
			await expect(
				page.getByText(
					`${PREFIX_FRIENDLY_URL}${expectedCategoriesPartialURL}`
				)
			).toBeVisible();
		});

		await test.step('Check categories order in friendly URL', async () => {
			await blogsEditBlogEntryPage.publishBlogEntry();

			const response = await page.goto(
				`/web${site.friendlyUrlPath}/b/${title}`
			);

			await expect(response.url()).toContain(
				`/web${site.friendlyUrlPath}/b/${expectedCategoriesPartialURL}/${title}`
			);
		});
	}
);

test(
	'Only blogs entry categories in the friendly URL',
	{
		tag: '@LPD-26659',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const vocabularyName = getRandomString();
		const friendlyUrlCategories = [
			{name: 'category-1'},
			{name: 'category-2'},
			{name: 'category-3'},
		];

		await blogsCategorizedFriendlyUrlSetup({
			apiHelpers,
			friendlyUrlCategories,
			page,
			site,
			vocabularyName,
		});

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			friendlyUrl: {
				categories: friendlyUrlCategories,
				vocabularyName,
			},
			publish: false,
			title,
		});

		await page.getByLabel('Remove category-1').click();
		await page.getByLabel('In Use').selectOption('category-2');
		await page.getByLabel('Reorder Down').click();

		const expectedCategoriesPartialURL = 'category-3/category-2';

		await test.step('Check categories in the input addon categories preview', async () => {
			await expect(
				page.getByText(
					`${PREFIX_FRIENDLY_URL}${expectedCategoriesPartialURL}`
				)
			).toBeVisible();
		});

		await test.step('Check categories in friendly URL', async () => {
			await blogsEditBlogEntryPage.publishBlogEntry();

			const response = await page.goto(
				`/web${site.friendlyUrlPath}/b/${title}`
			);

			await expect(response.url()).toContain(
				`/web${site.friendlyUrlPath}/b/${expectedCategoriesPartialURL}/${title}`
			);
		});
	}
);

test(
	'Updates the cover image position correctly after a drag',
	{
		tag: '@LPD-45514',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		const coverImageTitle = 'image1.jpeg';

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				title: coverImageTitle,
			}
		);

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await blogsEditBlogEntryPage.editBlogEntry({
			content: getRandomString(),
			publish: false,
			title: getRandomString(),
		});

		await blogsEditBlogEntryPage.selectCoverImage(coverImageTitle);

		// Simulate mouse movement to drag the image

		const coverImageWrapper = await page.locator('.image-wrapper');

		const coverImageSize = await coverImageWrapper.boundingBox();

		await coverImageWrapper.hover();
		await page.mouse.down();
		await page.mouse.move(
			coverImageSize.x + coverImageSize.width / 2,
			coverImageSize.y + coverImageSize.height / 2 - 30
		);
		await page.mouse.up();

		const coverImageCropRegionValue = await page
			.locator(
				'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_coverImageFileEntryCropRegion'
			)
			.inputValue();

		const topValue = JSON.parse(coverImageCropRegionValue).y;

		expect(topValue).toBeGreaterThan(20);
	}
);

test(
	'Validate viewEntryURL at entry_cover_image_caption jsp page',
	{
		tag: '@LPD-64234',
	},
	async ({page}) => {
		const html = `
	    <html>
	      <body>
		<form action="${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp"
		      method="POST" enctype="multipart/form-data">
		  <input type="hidden" name="coverImageCaption" value="x" />
		  <input type="hidden" name="coverImageURL" value="https://wwww.liferay.com" />
		  <input type="hidden" name="viewEntryURL" value="javascript:alert(document.domain)" />
		  <input type="submit" value="Submit request" />
		</form>
		<script>
		  document.forms[0].submit();
		</script>
	      </body>
	    </html>
	  `;

		await page.setContent(html);

		await page.waitForLoadState('networkidle');

		const links = await page.locator('a[href]');

		await expect(links).toHaveCount(0);
	}
);

test(
	'Can add a blog entry with title, subtitle and content',
	{
		tag: '@LPS-136760',
	},
	async ({blogsEditBlogEntryPage, blogsPage, site}) => {
		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = getRandomString();
		const subtitle = getRandomString();
		const content = getRandomString();

		await blogsEditBlogEntryPage.editBlogEntry({
			content,
			subtitle,
			title,
		});

		await blogsPage.goto(site.friendlyUrlPath);
		await blogsPage.goToBlogEntryAction('Edit', title);

		await expect(blogsEditBlogEntryPage.titleInput).toHaveValue(title);
		await expect(blogsEditBlogEntryPage.subtitleInput).toHaveValue(
			subtitle
		);
		await expect(blogsEditBlogEntryPage.contentEditor).toContainText(
			content
		);
	}
);

test(
	'Can add a 400-character description',
	{
		tag: '@LPS-136763',
	},
	async ({apiHelpers, blogsEditBlogEntryPage, blogsPage, site}) => {
		const content = Array.from({length: 40}, (_, i) => {
			const n = String(i + 1);

			return '|'.repeat(10 - n.length) + n;
		}).join('');

		const title = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: content,
			headline: title,
		});

		await blogsPage.goto(site.friendlyUrlPath);
		await blogsPage.goToBlogEntryAction('Edit', title);

		await expect(blogsEditBlogEntryPage.contentEditor).toContainText(
			content
		);
	}
);

test(
	'Trims the title to 255 characters when over',
	{
		tag: '@LPD-86549',
	},
	async ({blogsEditBlogEntryPage, blogsPage, site}) => {
		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const longTitle =
			Array.from({length: 26}, (_, i) => {
				const n = String(i + 1);

				return '|'.repeat(10 - n.length) + n;
			}).join('') + 'XX';
		const trimmedTitle = longTitle.slice(0, 255);

		await blogsEditBlogEntryPage.editBlogEntry({
			content: 'Blogs Entry Content',
			title: longTitle,
		});

		await blogsPage.goto(site.friendlyUrlPath);
		await blogsPage.goToBlogEntryAction('Edit', trimmedTitle);

		await expect(blogsEditBlogEntryPage.titleInput).toHaveValue(
			trimmedTitle
		);
	}
);

test(
	'Can add titles with escape characters without escaping them',
	{
		tag: '@LPS-130537',
	},
	async ({blogsEditBlogEntryPage, blogsPage, page, site}) => {
		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		const title = '&amp; &lt; &gt; &quot; > <';
		const content = 'Blogs Entry Content';

		await blogsEditBlogEntryPage.editBlogEntry({
			content,
			title,
		});

		await blogsPage.goto(site.friendlyUrlPath);
		await blogsPage.goToBlogEntryAction('Edit', title);

		await expect(page.getByPlaceholder('Title *')).toHaveValue(title);
	}
);

test(
	'Cannot add a blog entry with an empty title',
	{
		tag: '@LPD-86549',
	},
	async ({blogsEditBlogEntryPage, site}) => {
		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await blogsEditBlogEntryPage.fillContent('Blogs Entry Content');

		await blogsEditBlogEntryPage.publishBlogEntryExpectError(
			'Title field is required'
		);
	}
);

test(
	'Title does not execute XSS',
	{
		tag: '@LPS-128451',
	},
	async ({blogsEditBlogEntryPage, blogsPage, page, site}) => {
		const watcher = watchForDialog(page);

		try {
			await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

			const title = '"><img src=xss onerror=alert(1)>';

			await blogsEditBlogEntryPage.editBlogEntry({
				content: 'Blogs Entry Content',
				subtitle: title,
				title,
			});

			await blogsPage.goto(site.friendlyUrlPath);

			watcher.assertNoDialog();

			await expect(blogsPage.blogName(title).first()).toBeVisible();
		}
		finally {
			watcher.dispose();
		}
	}
);

test(
	'Can delete multiple blog entries sequentially',
	{
		tag: '@LPD-86549',
	},
	async ({apiHelpers, blogsPage, site}) => {
		const titles = [getRandomString(), getRandomString()];

		for (const headline of titles) {
			await apiHelpers.headlessDelivery.postBlog(site.id, {
				articleBody: 'Blogs Entry Content',
				headline,
			});
		}

		for (const title of titles) {
			await blogsPage.goto(site.friendlyUrlPath);
			await blogsPage.moveEntryToRecycleBin(title);
		}

		await blogsPage.goto(site.friendlyUrlPath);

		for (const title of titles) {
			await blogsPage.assertEntryPresent(title, false);
		}
	}
);

test(
	'Prevents attribute injection via coverImageCaption',
	{
		tag: '@LPD-86554',
	},
	async ({page}) => {
		const payload = `" onmouseover=alert(1) x="`;

		await page.goto(
			`${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp?coverImageCaption=${encodeURIComponent(payload)}&coverImageURL=https://example.com/image.png`
		);

		await expect(page.locator('.cover-image')).not.toHaveAttribute(
			'onmouseover',
			/.+/
		);
		await expect(page.locator('.cover-image')).toHaveAttribute(
			'aria-label',
			/onmouseover=alert\(1\)/
		);
	}
);

test(
	'Prevents HTML injection via coverImageCaption',
	{
		tag: '@LPD-86554',
	},
	async ({page}) => {
		const payload = `"><img src=x onerror=alert(1)>`;

		await page.goto(
			`${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp?coverImageCaption=${encodeURIComponent(payload)}&coverImageURL=https://example.com/image.png`
		);

		await expect(page.locator('img')).toHaveCount(0);
	}
);

test(
	'Does not allow javascript URL in coverImageURL',
	{
		tag: '@LPD-86554',
	},
	async ({page}) => {
		const payload = `javascript:alert(1)`;

		await page.goto(
			`${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp?coverImageCaption=safe&coverImageURL=${encodeURIComponent(payload)}`
		);

		await expect(page.locator('.cover-image')).toHaveCount(0);
	}
);

test(
	'Only valid URLs are applied to background-image',
	{
		tag: '@LPD-86554',
	},
	async ({page}) => {
		const validUrl = 'https://example.com/image.png';

		await page.goto(
			`${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp?coverImageCaption=safe&coverImageURL=${encodeURIComponent(validUrl)}`
		);

		await expect(page.locator('.cover-image')).toHaveAttribute(
			'style',
			new RegExp(validUrl)
		);
	}
);

test(
	'Invalid URL prevents background-image rendering',
	{
		tag: '@LPD-86554',
	},
	async ({page}) => {
		const invalidUrl = `not-a-valid-url`;

		await page.goto(
			`${liferayConfig.environment.baseUrl}/o/blogs-web/blogs/entry_cover_image_caption.jsp?coverImageCaption=safe&coverImageURL=${encodeURIComponent(invalidUrl)}`
		);

		await expect(page.locator('.cover-image')).toHaveCount(0);
	}
);

test(
	'Shows an icon beside every blog entry action',
	{
		tag: '@LPD-83769',
	},
	async ({apiHelpers, blogsPage, site}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: 'Blogs Entry Content',
			headline,
		});

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.assertBlogEntryActionIcons(
			[
				{action: 'Edit', icon: 'pencil'},
				{action: 'Share', icon: 'share'},
				{action: 'Permissions', icon: 'password-policies'},
				{action: 'Delete', icon: 'trash'},
			],
			headline
		);
	}
);
