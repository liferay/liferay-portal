/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getGlobalSite from '../../../utils/getGlobalSite';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {closeProductMenu} from '../../../utils/productMenu';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';
import {navigationMenusPagesTest} from '../../site-navigation-admin-web/main/fixtures/navigationMenusPagesTest';
import {NavigationMenusPage} from '../../site-navigation-admin-web/main/pages/NavigationMenusPage';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';
import {navigationMenuWidgetPagesTest} from './fixtures/navigationMenuWidgetPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	customFieldsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	navigationMenusPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	templatesPageTest,
	pageViewModePagesTest,
	sitesAdminPagesTest,
	navigationMenuWidgetPagesTest
);

test.describe('Missing reference warnings for Navigation Menu Items', () => {
	test(
		'Missing reference for a Asset Vocabulary type Navigation Menu Item',
		{
			tag: '@LPD-71409',
		},
		async ({apiHelpers, navigationMenusPage, page, site}) => {
			const categoryName1 = getRandomString();
			const categoryName2 = getRandomString();

			const {vocabulary1, vocabulary2} =
				await test.step('Create Vocabulary', async () => {
					const vocabulary1 =
						await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
							{
								name: getRandomString(),
								siteId: site.id,
							}
						);

					await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
						{
							name: categoryName1,
							vocabularyId: vocabulary1.id,
						}
					);

					const vocabulary2 =
						await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
							{
								name: getRandomString(),
								siteId: site.id,
							}
						);

					await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
						{
							name: categoryName2,
							vocabularyId: vocabulary2.id,
						}
					);

					return {vocabulary1, vocabulary2};
				});

			const navigationMenuName = getRandomString();

			await test.step('Add Navigation Menu Items for created Vocabularies', async () => {
				await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
					site.externalReferenceCode,
					{
						name: navigationMenuName,
						navigationMenuItems: [
							{
								navigationMenuItemSettings: {
									externalReferenceCode:
										vocabulary1.externalReferenceCode,
									title: vocabulary1.name,
								},
								type: 'asset_vocabulary',
							},
							{
								navigationMenuItemSettings: {
									externalReferenceCode:
										vocabulary2.externalReferenceCode,
									title: vocabulary2.name,
								},
								type: 'asset_vocabulary',
							},
						],
					}
				);
			});

			await test.step('Delete a Vocabulary to simulate a missing reference', async () => {
				await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
					vocabulary1.id
				);
			});

			await test.step('Verify missing reference warnings in left and right panes', async () => {
				await navigationMenusPage.goto(site.friendlyUrlPath);

				await page
					.getByRole('link', {name: navigationMenuName})
					.click();

				await assertMissingReferenceWarnings(
					page,
					vocabulary1.name,
					navigationMenusPage
				);
			});

			await test.step("Verify that Navigation Menu's rendered preview doesn't show Menu Items with missing references", async () => {
				await navigationMenusPage.previewButton.click();

				await navigationMenusPage.displayTemplate.selectOption(
					'LIST-MENU-FTL'
				);

				await expect(
					await navigationMenusPage.getModalListItem(categoryName1)
				).not.toBeVisible();
				await expect(
					await navigationMenusPage.getModalListItem(categoryName2)
				).toBeVisible();
			});
		}
	);

	test(
		'Missing reference for a Display Page type Navigation Menu Item',
		{
			tag: '@LPD-71409',
		},
		async ({apiHelpers, navigationMenusPage, page, site}) => {
			const {webContent1, webContent2} =
				await test.step('Create Web Content Articles', async () => {
					const ddmStructureId =
						await getBasicWebContentStructureId(apiHelpers);

					const webContent1 =
						await apiHelpers.jsonWebServicesJournal.addWebContent({
							ddmStructureId,
							groupId: site.id,
							title: getRandomString(),
						});

					const webContent2 =
						await apiHelpers.jsonWebServicesJournal.addWebContent({
							ddmStructureId,
							groupId: site.id,
							title: getRandomString(),
						});

					return {webContent1, webContent2};
				});

			const navigationMenuName = getRandomString();

			await test.step('Add Navigation Menu Items for created Web Content Articles', async () => {
				await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
					site.externalReferenceCode,
					{
						name: navigationMenuName,
						navigationMenuItems: [
							{
								navigationMenuItemSettings: {
									className:
										'com.liferay.journal.model.JournalArticle',
									externalReferenceCode:
										webContent1.externalReferenceCode,
									title: webContent1.title,
								},
								type: 'com.liferay.journal.model.JournalArticle',
							},
							{
								navigationMenuItemSettings: {
									className:
										'com.liferay.journal.model.JournalArticle',
									externalReferenceCode:
										webContent2.externalReferenceCode,
									title: webContent2.title,
								},
								type: 'com.liferay.journal.model.JournalArticle',
							},
						],
					}
				);
			});

			await test.step('Delete a Web Content Article to simulate a missing reference', async () => {
				await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
					site.id,
					webContent1.articleId
				);
			});

			await test.step('Verify missing reference warnings in left and right panes', async () => {
				await navigationMenusPage.goto(site.friendlyUrlPath);

				await page
					.getByRole('link', {name: navigationMenuName})
					.click();

				await assertMissingReferenceWarnings(
					page,
					webContent1.title,
					navigationMenusPage
				);
			});

			await test.step("Verify that Navigation Menu's rendered preview doesn't show Menu Items with missing references", async () => {
				await navigationMenusPage.previewButton.click();

				await navigationMenusPage.displayTemplate.selectOption(
					'LIST-MENU-FTL'
				);

				await expect(
					await navigationMenusPage.getModalListItem(
						webContent1.title
					)
				).not.toBeVisible();
				await expect(
					await navigationMenusPage.getModalListItem(
						webContent2.title
					)
				).toBeVisible();
			});
		}
	);

	test(
		'Missing reference for a Layout type Navigation Menu Item',
		{
			tag: '@LPD-71409',
		},
		async ({apiHelpers, navigationMenusPage, page, site}) => {
			const {layout1, layout2} =
				await test.step('Create Layouts', async () => {
					const layout1 =
						await apiHelpers.jsonWebServicesLayout.addLayout({
							groupId: site.id,
							title: getRandomString(),
						});

					const layout2 =
						await apiHelpers.jsonWebServicesLayout.addLayout({
							groupId: site.id,
							title: getRandomString(),
						});

					return {layout1, layout2};
				});

			const navigationMenuName = getRandomString();

			await test.step('Add Navigation Menu Items for created Layouts', async () => {
				await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
					site.externalReferenceCode,
					{
						name: navigationMenuName,
						navigationMenuItems: [
							{
								navigationMenuItemSettings: {
									externalReferenceCode:
										layout1.externalReferenceCode,
									privatePage: false,
									title: layout1.nameCurrentValue,
								},
								type: 'layout',
							},
							{
								navigationMenuItemSettings: {
									externalReferenceCode:
										layout2.externalReferenceCode,
									privatePage: false,
									title: layout2.nameCurrentValue,
								},
								type: 'layout',
							},
						],
					}
				);
			});

			await test.step('Delete a Layout to simulate a missing reference', async () => {
				await apiHelpers.jsonWebServicesLayout.deleteLayout(
					layout1.plid
				);
			});

			await test.step('Verify missing reference warnings in left and right panes', async () => {
				await navigationMenusPage.goto(site.friendlyUrlPath);

				await page
					.getByRole('link', {name: navigationMenuName})
					.click();

				// Layout type Navigation Menu Items are deleted from the Navigation Menu when the Layout is deleted

				await expect(
					page.getByText(layout1.nameCurrentValue)
				).not.toBeVisible();
			});

			await test.step("Verify that Navigation Menu's rendered preview doesn't show Menu Items with missing references", async () => {
				await navigationMenusPage.previewButton.click();

				await navigationMenusPage.displayTemplate.selectOption(
					'LIST-MENU-FTL'
				);

				await expect(
					await navigationMenusPage.getModalListItem(
						layout1.nameCurrentValue
					)
				).not.toBeVisible();
				await expect(
					await navigationMenusPage.getModalListItem(
						layout2.nameCurrentValue
					)
				).toBeVisible();
			});
		}
	);

	async function assertMissingReferenceWarnings(
		page: Page,
		menuItemName: string,
		navigationMenusPage: NavigationMenusPage
	) {
		await expect(page.getByText(menuItemName)).toBeVisible();

		const menuItemCard1 =
			await navigationMenusPage.getMenuItemCard(menuItemName);

		await expect(
			menuItemCard1.locator('.lexicon-icon-warning-full')
		).toBeVisible();

		await menuItemCard1.click();

		await expect(
			navigationMenusPage.sidebarBody.getByText('No Reference found')
		).toBeVisible();
	}
});

test(
	'Add URL type Navigation Menu Item with "open in a new tab" checkbox unchecked',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByText(urlItemName).click();

		const currentURL = page.url();

		expect(currentURL).toContain('https://www.liferay.com');
	}
);

test(
	'Add URL type Navigation Menu Item with "open in a new tab" checkbox checked',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName, undefined, true);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		const [newPage] = await Promise.all([
			page.context().waitForEvent('page'),
			page.getByText(urlItemName).click(),
		]);

		await newPage.waitForLoadState();

		const newTabURL = newPage.url();

		expect(newTabURL).toContain('https://www.liferay.com');
	}
);

test(
	'Cannot view global Navigation Menu in Navigation Menu Widget without permission',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const navigationMenuName = getRandomString();
		const urlMenuItemName = getRandomString();

		const globalSite = await getGlobalSite(apiHelpers);

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			globalSite.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: urlMenuItemName},
						navigationMenuItemSettings: {
							url: 'https://www.liferay.com',
						},
						type: 'url',
					},
				],
			}
		);

		await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Permissions'}),
			trigger: page
				.getByRole('row', {name: navigationMenuName})
				.getByLabel('Show Actions'),
		});

		const permissionsModal = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		const guestViewCheckbox =
			permissionsModal.locator('#guest_ACTION_VIEW');

		await guestViewCheckbox.uncheck({trial: true});

		await guestViewCheckbox.uncheck();

		await permissionsModal.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(permissionsModal);

		await page.locator('.modal').getByLabel('Close', {exact: true}).click();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await performLogout(page);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await expect(
			page.getByRole('menuitem', {name: urlMenuItemName})
		).not.toBeVisible();
	}
);

test(
	'Configure Display Template of Navigation Menu Widget',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layoutName1 = getRandomString();

		const layout1 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName1,
		});

		await widgetPagePage.goto(layout1, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(layoutName1);

		await navigationMenuWidgetPage.selectDisplayTemplate(
			'Split Button Dropdowns'
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await expect(page.getByRole('link', {name: layoutName1})).toBeVisible();

		const layoutName2 = getRandomString();

		const layout2 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName2,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(layoutName2, 'General');

		const newLayoutName = getRandomString();

		await pageConfigurationPage.fillName(newLayoutName);

		await pageConfigurationPage.save();

		await widgetPagePage.goto(layout2, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Blogs');

		await page.locator('header').filter({hasText: 'Blogs'}).hover();

		await page
			.locator(
				'#portlet-topper-toolbar_com_liferay_blogs_web_portlet_BlogsPortlet'
			)
			.getByLabel('Options')
			.click();

		await page
			.getByRole('menuitem', {name: 'Look and Feel Configuration'})
			.click();

		const lookAndFeelIFrame = page.frameLocator(
			'iframe[title="Look and Feel Configuration"]'
		);

		await lookAndFeelIFrame.getByLabel('Use Custom Title').check();

		await lookAndFeelIFrame
			.locator(
				'[id="_com_liferay_portlet_configuration_css_web_portlet_PortletConfigurationCSSPortlet_customTitle"]'
			)
			.fill('Blogs Custom');

		await lookAndFeelIFrame.getByRole('button', {name: 'Save'}).click();
	}
);

test(
	'Edit global Navigation Menu via Navigation Menu Widget in normal site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const navigationMenuName = getRandomString();
		const urlMenuItemName = getRandomString();

		const globalSite = await getGlobalSite(apiHelpers);

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			globalSite.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: urlMenuItemName},
						navigationMenuItemSettings: {
							url: 'https://www.liferay.com',
						},
						type: 'url',
					},
				],
			}
		);

		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(layoutName);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await expect(
			page.getByRole('menuitem', {name: urlMenuItemName})
		).toBeVisible();

		await page.getByRole('button', {name: 'Edit'}).click();

		await page.getByRole('menuitem', {name: 'Edit'}).click();

		await page.waitForTimeout(1500);

		expect(page.url()).toContain(PORTLET_URLS.navigationMenus);
	}
);

test(
	'Hide Pages',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layoutName1 = getRandomString();

		const layoutName2 = getRandomString();

		const layout1 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName1,
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName2,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(layoutName2, 'General');

		await page.getByLabel('Hidden from Menu Display').check();

		await pageConfigurationPage.save();

		await widgetPagePage.goto(layout1, site.friendlyUrlPath);

		await expect(
			page.getByRole('menuitem', {name: layoutName1})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: layoutName2})
		).not.toBeVisible();
	}
);

test(
	'Navigate to Pages',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const parentLayoutName = getRandomString();

		const childLayoutName = getRandomString();

		const parentLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: parentLayoutName,
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: parentLayout.layoutId,
			title: childLayoutName,
		});

		await widgetPagePage.goto(parentLayout, site.friendlyUrlPath);

		await expect(
			page.getByRole('heading', {name: parentLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: parentLayoutName}).hover();

		await page.getByRole('menuitem', {name: childLayoutName}).click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();
	}
);

test('Navigation Menu widget defaults to public pages when menu is deleted', async ({
	navigationMenuWidgetPage,
	page,
	site,
	widgetPagePage,
}) => {
	const apiHelpers = new ApiHelpers(page);

	const layoutName = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: layoutName,
	});

	const navigationMenuName = getRandomString();
	const urlItemName = getRandomString();
	const navigationMenuERC = getRandomString();

	await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
		site.externalReferenceCode,
		{
			externalReferenceCode: navigationMenuERC,
			name: navigationMenuName,
			navigationMenuItems: [
				{
					name_i18n: {en_US: urlItemName},
					navigationMenuItemSettings: {
						url: 'https://www.liferay.com',
					},
					type: 'url',
				},
			],
		}
	);

	await widgetPagePage.goto(layout, site.friendlyUrlPath);

	await navigationMenuWidgetPage.openConfigurationModal(layoutName);

	await navigationMenuWidgetPage.selectCustomNavigationMenu(
		navigationMenuName
	);

	await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

	await expect(page.getByRole('menuitem', {name: urlItemName})).toBeVisible();

	await apiHelpers.headlessAdminSite.deleteSiteNavigationMenu(
		site.externalReferenceCode,
		navigationMenuERC
	);

	await widgetPagePage.goto(layout, site.friendlyUrlPath);

	await expect(page.getByRole('menuitem', {name: layoutName})).toBeVisible();
});

test(
	'Select global Navigation Menu in normal site',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, navigationMenuWidgetPage, site, widgetPagePage}) => {
		const navigationMenuName = getRandomString();

		const globalSite = await getGlobalSite(apiHelpers);

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			globalSite.externalReferenceCode,
			{
				name: navigationMenuName,
			}
		);

		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(layoutName);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();
	}
);

test(
	'Select Navigation Menu from grandparent site',
	{
		tag: '@LPD-50258',
	},
	async ({navigationMenuWidgetPage, page, widgetPagePage}) => {
		const apiHelpers = new ApiHelpers(page);

		// Create sites

		const parentSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const childSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
			parentSiteExternalReferenceCode: parentSite.externalReferenceCode,
		});

		const grandchildSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
			parentSiteExternalReferenceCode: childSite.externalReferenceCode,
		});

		// Add Layout to the grandchild site

		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(grandchildSite.id),
			title: layoutName,
		});

		// Add Navigation Menu to the Parent site

		const navigationMenuERC = getRandomString();
		const navigationMenuName = getRandomString();
		const urlItemName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			parentSite.externalReferenceCode,
			{
				externalReferenceCode: navigationMenuERC,
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: urlItemName},
						navigationMenuItemSettings: {
							url: 'https://www.liferay.com',
						},
						type: 'url',
					},
				],
			}
		);

		await widgetPagePage.goto(layout, grandchildSite.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(layoutName);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await expect(
			page.getByRole('menuitem', {name: urlItemName})
		).toBeVisible();
	}
);

test(
	'Select Navigation Menu from parent Site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {

		// Create Navigation Menu

		const navigationMenuName = getRandomString();
		const urlItemName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: urlItemName},
						navigationMenuItemSettings: {
							url: 'https://www.liferay.com',
						},
						type: 'url',
					},
				],
			}
		);

		const childSiteName = getRandomString();

		const childSite = await apiHelpers.headlessAdminSite.postSite({
			name: childSiteName,
			parentSiteExternalReferenceCode: site.externalReferenceCode,
		});

		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: childSite.id,
			title: layoutName,
		});

		await widgetPagePage.goto(layout, childSite.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await expect(page.getByText(urlItemName)).toBeVisible();
	}
);

test(
	'Select page as root menu item for Navigation Menu widget',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const parentLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: parentLayout.layoutId,
			title: getRandomString(),
		});

		await widgetPagePage.goto(parentLayout, site.friendlyUrlPath);

		await closeProductMenu(page);

		await widgetPagePage.clickOnAction('Menu Display', 'Configuration');

		const configurationIFrame = page.frameLocator(
			'iframe[title*="Menu Display"]'
		);

		await configurationIFrame
			.getByLabel('Start with Menu Items In')
			.selectOption('Select Parent');

		await configurationIFrame
			.getByRole('button', {name: 'Menu Item'})
			.click();

		await configurationIFrame
			.frameLocator('iframe[title="Select Site Navigation Menu Item"]')
			.getByText('Pages Hierarchy')
			.click();
		await configurationIFrame
			.frameLocator('iframe[title="Select Site Navigation Menu Item"]')
			.getByText(parentLayout.nameCurrentValue)
			.click();

		await widgetPagePage.saveAndClose('Menu Display');

		await expect(
			page.getByRole('menuitem', {name: childLayout.nameCurrentValue})
		).toBeVisible();

		await widgetPagePage.clickOnAction('Menu Display', 'Configuration');

		await expect(
			configurationIFrame.getByText(parentLayout.nameCurrentValue)
		).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select custom menu and bar minimally styled',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {

		// Create Layouts in the same level

		const layoutName1 = getRandomString();

		const layoutName2 = getRandomString();

		const layoutName3 = getRandomString();

		const layoutName4 = getRandomString();

		const layoutNames = [layoutName2, layoutName3, layoutName4];

		const layouts = [];

		layouts.push(
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				title: layoutName1,
			})
		);

		for (const name of layoutNames) {
			layouts.push(
				await apiHelpers.jsonWebServicesLayout.addLayout({
					externalReferenceCode: getRandomString(),
					groupId: site.id,
					title: name,
				})
			);
		}

		const navigationMenuName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						navigationMenuItemSettings: {
							externalReferenceCode:
								layouts[0].externalReferenceCode,
							privatePage: false,
							title: layoutName1,
						},
						navigationMenuItems: [
							{
								navigationMenuItemSettings: {
									externalReferenceCode:
										layouts[1].externalReferenceCode,
									privatePage: false,
									title: layoutName2,
								},
								navigationMenuItems: [
									{
										navigationMenuItemSettings: {
											externalReferenceCode:
												layouts[2]
													.externalReferenceCode,
											privatePage: false,
											title: layoutName3,
										},
										navigationMenuItems: [
											{
												navigationMenuItemSettings: {
													externalReferenceCode:
														layouts[3]
															.externalReferenceCode,
													privatePage: false,
													title: layoutName4,
												},
												type: 'layout',
											},
										],
										type: 'layout',
									},
								],
								type: 'layout',
							},
						],
						type: 'layout',
					},
				],
			}
		);

		const layout = layouts[0];

		// Select the created Navigation Menu in the Navigation Menu Widget

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		// Assert that the Navigation Menu Items are viewable when the mouse hove over the first Page Menu Item

		await expect(
			page.getByRole('menuitem', {name: layoutName1})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName1}).hover();

		await expect(
			page.getByRole('menuitem', {name: layoutName2})
		).toBeVisible();

		await expect(page.getByRole('link', {name: layoutName3})).toBeVisible();

		await expect(page.getByRole('link', {name: layoutName4})).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select Public Pages hierarchy and Bar Minimally Styled',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(childLayoutName).click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(grandChildLayoutName).click();

		await expect(
			page.getByRole('heading', {name: grandChildLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(greatGrandChildLayoutName).click();

		await expect(
			page.getByRole('heading', {name: greatGrandChildLayoutName})
		).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select Public Pages hierarchy and List Menu',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await page.waitForTimeout(1500);

		await navigationMenuWidgetPage.selectDisplayTemplate('List Menu');

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: childLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: grandChildLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: grandChildLayoutName})
		).toBeVisible();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: greatGrandChildLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: greatGrandChildLayoutName})
		).toBeVisible();
	}
);

test(
	'Show the same amount of levels with the number of levels to display',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.menuDisplayModal
			.getByLabel('Levels to Display')
			.selectOption('2');

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await expect(
			page.getByRole('menuitem', {name: layoutName})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: childLayoutName})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: grandChildLayoutName})
		).not.toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: greatGrandChildLayoutName})
		).not.toBeVisible();
	}
);

test(
	'View custom field of Navigation Menu',
	{
		tag: '@LPD-50258',
	},
	async ({
		addCustomFieldPage,
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		templatesPage,
		viewAttributesPage,
		widgetPagePage,
	}) => {
		try {

			// Create Custom Field for Navigation Menu Items

			const customFieldName = 'Subtitle';

			const customField: TCustomField = {
				fieldName: customFieldName,
				fieldType: 'inputField',
				resource: 'Site Navigation Menu Item',
			};

			await addCustomFieldPage.addCustomField(customField);

			// Create layout

			const layoutName = getRandomString();

			const submenuItemName = getRandomString();

			const urlItemName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				title: layoutName,
			});

			// Create Navigation Menu

			await navigationMenusPage.goto(site.friendlyUrlPath);

			const navigationMenuName = getRandomString();

			await navigationMenusPage.createNavigationMenu(navigationMenuName);

			// Create Navigation Menu Items

			await navigationMenusPage.addPageItem([layoutName]);

			await navigationMenusPage.addSubmenuItem(submenuItemName);

			await navigationMenusPage.addURLItem(urlItemName);

			// Fill the Custom Fields of the Navigation Menu Items and store its value

			const value1 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					layoutName,
					customFieldName
				);

			const value2 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					submenuItemName,
					customFieldName
				);

			const value3 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					urlItemName,
					customFieldName
				);

			// Create Widget Template with a custom template script

			await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

			const widgetTemplateName = getRandomString();

			await templatesPage.createWidgetTemplate(
				widgetTemplateName,
				'Menu Display Template'
			);

			await templatesPage.editTemplate(widgetTemplateName);

			await templatesPage.importInformationTemplate(
				__dirname,
				'custom_field_template.ftl'
			);

			await templatesPage.saveTemplate(widgetTemplateName);

			// Use the created Navigation Menu in the Navigation Menu Widget and apply the created Widget Template

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(
				layout.nameCurrentValue
			);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.selectDisplayTemplate(
				widgetTemplateName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			// Verify that the changes where applied

			await expect.soft(page.getByText(value1)).toBeVisible();

			await expect.soft(page.getByText(value2)).toBeVisible();

			await expect.soft(page.getByText(value3)).toBeVisible();
		}
		finally {
			await viewAttributesPage.goto('Site Navigation Menu Item');

			await page.getByLabel('Select All Items on the Page').check();

			await page.getByRole('button', {name: 'Delete'}).click();
		}
	}
);

test(
	'View default selected navigation',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, navigationMenuWidgetPage, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByLabel(
				'Select Navigation'
			)
		).toBeChecked();

		await expect(
			navigationMenuWidgetPage.navigationSelector.locator(
				'option:checked'
			)
		).toHaveText('Pages Hierarchy');
	}
);

test(
	'View warning message in preview window when no navigation available',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		navigationMenuWidgetPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.journal.model.JournalArticle'
			);

		const displayPageTemplateName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				classTypeKey: 'BASIC-WEB-CONTENT',
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

		displayPageTemplatesPage.goto(site.friendlyUrlPath);

		displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addWidget('Content Management', 'Menu Display');

		await page
			.locator('ul')
			.filter({hasText: 'Menu Display'})
			.getByLabel('Options')
			.click();

		await page
			.getByRole('menuitem', {exact: true, name: 'Configuration'})
			.click();

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByLabel(
				'Select Navigation'
			)
		).toBeChecked();

		await navigationMenuWidgetPage.navigationSelector.selectOption('1');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Primary Navigation available for the current site.'
		);

		await page.waitForTimeout(300);

		await navigationMenuWidgetPage.navigationSelector.selectOption('2');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Secondary Navigation available for the current site.'
		);

		await page.waitForTimeout(300);

		await navigationMenuWidgetPage.navigationSelector.selectOption('3');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Social Navigation available for the current site.'
		);
	}
);

test(
	'Ensure nested submenu with browsable element is displayed',
	{
		tag: '@LPD-64079',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const navigationMenuName = getRandomString();
		const parentSubmenuItemName = getRandomString();
		const childSubmenuItemName = getRandomString();
		const urlItemName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: parentSubmenuItemName},
						navigationMenuItems: [
							{
								name_i18n: {en_US: childSubmenuItemName},
								navigationMenuItems: [
									{
										name_i18n: {en_US: urlItemName},
										navigationMenuItemSettings: {
											url: 'https://www.liferay.com',
										},
										type: 'url',
									},
								],
								type: 'node',
							},
						],
						type: 'node',
					},
				],
			}
		);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByRole('menuitem', {name: parentSubmenuItemName}).hover();

		await expect(page.getByRole('link', {name: urlItemName})).toBeVisible();
	}
);

test(
	'Ensure nested submenu without browsable element is not displayed',
	{
		tag: '@LPD-76455',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const layoutWithViewPermission =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

		const layoutWithoutViewPermission =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.changePagesPermissions(
			[layoutWithoutViewPermission.nameCurrentValue],
			['guest_ACTION_VIEW']
		);

		const navigationMenuName = getRandomString();
		const parentSubmenuItemName = getRandomString();
		const firstChildSubmenuItemName = getRandomString();
		const secondChildSubmenuItemName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: parentSubmenuItemName},
						navigationMenuItems: [
							{
								name_i18n: {en_US: firstChildSubmenuItemName},
								navigationMenuItems: [
									{
										navigationMenuItemSettings: {
											externalReferenceCode:
												layoutWithViewPermission.externalReferenceCode,
											privatePage: false,
											title: layoutWithViewPermission.nameCurrentValue,
										},
										type: 'layout',
									},
								],
								type: 'node',
							},
							{
								name_i18n: {en_US: secondChildSubmenuItemName},
								navigationMenuItems: [
									{
										navigationMenuItemSettings: {
											externalReferenceCode:
												layoutWithoutViewPermission.externalReferenceCode,
											privatePage: false,
											title: layoutWithoutViewPermission.nameCurrentValue,
										},
										type: 'layout',
									},
								],
								type: 'node',
							},
						],
						type: 'node',
					},
				],
			}
		);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await performLogout(page);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByRole('menuitem', {name: parentSubmenuItemName}).hover();

		await expect(
			page.getByText(secondChildSubmenuItemName)
		).not.toBeVisible();
		await expect(
			page.getByText(layoutWithoutViewPermission.nameCurrentValue)
		).not.toBeVisible();
	}
);

test(
	'Translation is shown for Display Page type Navigation Menu Item',
	{
		tag: '@LPD-76461',
	},
	async ({apiHelpers, navigationMenusPage, page, site}) => {
		const navigationMenuName = getRandomString();
		const webContentTitle = getRandomString();

		await test.step('Create Web Content Article', async () => {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: site.id,
				titleMap: {en_US: webContentTitle},
			});
		});

		await test.step('Add Navigation Menu for created Web Content Article', async () => {
			await navigationMenusPage.goto(site.friendlyUrlPath);

			await navigationMenusPage.createNavigationMenu(navigationMenuName);

			await navigationMenusPage.addWebContentArticleItem(webContentTitle);
		});

		await test.step('Switch to es_ES and add translation', async () => {
			await page.getByText(webContentTitle).click();

			await page.getByText('Use Custom Name').click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.locator('#es_ES'),
				trigger: page.getByLabel('Select a Language'),
			});

			await page.getByLabel('Name', {exact: true}).fill('Spanish');

			await page.getByRole('button', {name: 'Save'}).click();
		});

		await test.step('Assert translation is visible', async () => {
			await expect(page.getByLabel('Name', {exact: true})).toHaveValue(
				webContentTitle
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.locator('#es_ES'),
				trigger: page.getByLabel('Select a Language'),
			});

			await expect(page.getByLabel('Name', {exact: true})).toHaveValue(
				'Spanish'
			);
		});
	}
);

test(
	'Update the preview for unsaved navigation menu and parent menu item selections',
	{
		tag: '@LPD-107168',
	},
	async ({apiHelpers, navigationMenuWidgetPage, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const childURLItemName = getRandomString();
		const navigationMenuName = getRandomString();
		const siblingURLItemName = getRandomString();
		const submenuItemName = getRandomString();

		await apiHelpers.headlessAdminSite.postSiteNavigationMenu(
			site.externalReferenceCode,
			{
				name: navigationMenuName,
				navigationMenuItems: [
					{
						name_i18n: {en_US: submenuItemName},
						navigationMenuItems: [
							{
								name_i18n: {en_US: childURLItemName},
								navigationMenuItemSettings: {
									url: 'https://www.liferay.com',
								},
								type: 'url',
							},
						],
						type: 'node',
					},
					{
						name_i18n: {en_US: siblingURLItemName},
						navigationMenuItemSettings: {
							url: 'https://www.liferay.com',
						},
						type: 'url',
					},
				],
			}
		);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		const preview = navigationMenuWidgetPage.menuDisplayModal.locator(
			'.taglib-portlet-preview'
		);

		await expect(preview.getByText(siblingURLItemName)).toBeVisible();

		await navigationMenuWidgetPage.menuDisplayModal
			.getByLabel('Start with Menu Items In')
			.selectOption('Select Parent');

		await navigationMenuWidgetPage.menuDisplayModal
			.getByRole('button', {name: 'Menu Item'})
			.click();

		const selectSiteNavigationMenuItemModal =
			navigationMenuWidgetPage.menuDisplayModal.frameLocator(
				'iframe[title="Select Site Navigation Menu Item"]'
			);

		await clickAndExpectToBeVisible({
			target: selectSiteNavigationMenuItemModal.getByText(
				submenuItemName,
				{exact: true}
			),
			trigger: selectSiteNavigationMenuItemModal.getByText(
				navigationMenuName,
				{exact: true}
			),
		});

		await selectSiteNavigationMenuItemModal
			.getByText(submenuItemName, {exact: true})
			.click();

		await expect(preview.getByText(childURLItemName)).toBeAttached();
		await expect(preview.getByText(siblingURLItemName)).toHaveCount(0);
	}
);
