/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';

const test = mergeTests(
	apiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-35443': {enabled: true},
		'LPD-57283': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	loginTest()
);

test(
	'Can create a style book within a design library',
	{tag: '@LPD-88092'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		try {
			await test.step('Open the design library resources view', async () => {
				await designLibrariesPage.goToDesignLibrary(designLibraryName);

				await expect(
					page.getByRole('button', {exact: true, name: 'New'})
				).toBeVisible();
			});

			const modal = page.getByRole('dialog');

			await test.step('Cancelling the modal does not create an entry', async () => {
				await designLibrariesPage.clickNewStyleBook();

				await expect(modal).toBeVisible();
				await expect(
					modal.getByRole('heading', {name: 'Add Style Book'})
				).toBeVisible();

				await modal.getByRole('button', {name: 'Cancel'}).click();

				await expect(modal).toBeHidden();
			});

			await test.step('Submitting the modal redirects to the style book editor', async () => {
				await designLibrariesPage.clickNewStyleBook();

				await expect(modal).toBeVisible();

				const styleBookName = getRandomString();

				await modal.getByLabel('Name').fill(styleBookName);

				await modal.getByRole('button', {name: 'Save'}).click();

				await expect(modal).toBeHidden();
				await expect(page).toHaveURL(/style_book.+edit/);

				const breadcrumb = page
					.getByRole('navigation', {name: 'Breadcrumb'})
					.last();

				await expect(breadcrumb.getByText(styleBookName)).toBeVisible();
			});

			await test.step('Clicking the back button returns to the design library', async () => {
				await page
					.locator('.control-menu-nav-item')
					.getByTitle(`Go to ${designLibraryName}`)
					.click();

				await expect(
					page.getByRole('heading', {name: 'Design Libraries'})
				).toBeVisible();
			});
		}
		finally {
			await test.step('Remove the design library', async () => {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);
			});
		}
	}
);

test(
	'New button is not visible without permissions',
	{tag: '@LPD-88092'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		const unprivilegedUser =
			await test.step('Create an unprivileged user', async () => {
				const user =
					await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				return user;
			});

		try {
			const newButton = page.getByRole('button', {
				name: 'New',
			});

			const designLibraryURL =
				await test.step('Open the design library resources view as admin', async () => {
					await designLibrariesPage.goToDesignLibrary(
						designLibraryName
					);

					await expect(newButton).toBeVisible();

					return page.url();
				});

			await test.step('Switch to the unprivileged user', async () => {
				await performLogout(page);

				await performLoginViaApi({
					page,
					screenName: unprivilegedUser.alternateName,
				});
			});

			await test.step('New Style Book button is not visible without permissions', async () => {
				await page.goto(designLibraryURL);

				await expect(newButton).toBeHidden();
			});
		}
		finally {
			await test.step('Switch back to the admin user and clean up', async () => {
				await performLogout(page);

				await performLoginViaApi({page, screenName: 'test'});

				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);

				await apiHelpers.headlessAdminUser.deleteUserAccount(
					Number(unprivilegedUser.id)
				);
			});
		}
	}
);

test(
	'Design Library content screen lists the style books added to it',
	{tag: '@LPD-74829'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const designLibraryName = getRandomString();
		const styleBookName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a new design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		await test.step('Add a style book to the design library via UI', async () => {
			await designLibrariesPage.createStyleBook(
				designLibraryName,
				styleBookName
			);
		});

		await test.step('Check that the style book is listed', async () => {
			const contentTable = page.locator(
				'.design-library-fds-wrapper--resources table'
			);

			await expect(
				contentTable.getByRole('row', {name: styleBookName})
			).toBeVisible();
		});

		await test.step('Check that the author column shows the creator name', async () => {
			const contentTable = page.locator(
				'.design-library-fds-wrapper--resources table'
			);

			const styleBookRow = contentTable.getByRole('row', {
				name: styleBookName,
			});

			await expect(
				styleBookRow.getByRole('cell', {name: 'Test Test'})
			).toBeVisible();
		});

		await test.step('Check that the row action menu exposes Edit and Delete', async () => {
			const contentTable = page.locator(
				'.design-library-fds-wrapper--resources table'
			);

			const styleBookRow = contentTable.getByRole('row', {
				name: styleBookName,
			});

			await styleBookRow.getByRole('button', {name: /Actions$/}).click();

			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: 'Edit in Style Book Editor',
				})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Delete'})
			).toBeVisible();
		});

		await test.step('Remove the design library', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);
		});
	}
);

test(
	'Editing a Design Library style book opens the editor with Design-Library-aware experience',
	{tag: '@LPD-74829'},
	async ({apiHelpers, designLibrariesPage, page}) => {
		const connectedSiteName = getRandomString();
		const designLibraryName = getRandomString();
		const pageName = getRandomString();
		const styleBookName = getRandomString();

		const createdDesignLibrary =
			await test.step('Create a new design library via headless', async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			});

		const connectedSite =
			await test.step('Create a connected site with a public page via headless', async () => {
				const site = await apiHelpers.headlessAdminSite.postSite({
					name: connectedSiteName,
				});

				await apiHelpers.headlessAdminSite.createPage(
					site.externalReferenceCode,
					{
						name_i18n: {en_US: pageName},
						type: 'WidgetPage',
					}
				);

				return site;
			});

		await test.step('Connect the site to the design library', async () => {
			await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
				createdDesignLibrary.id,
				connectedSite.id
			);
		});

		await test.step('Add a style book to the design library via UI', async () => {
			await designLibrariesPage.createStyleBook(
				designLibraryName,
				styleBookName
			);
		});

		await test.step('Open the style book editor from the design library content screen', async () => {
			await designLibrariesPage.goToDesignLibrary(designLibraryName);

			const styleBookRow = page
				.locator('.design-library-fds-wrapper--resources table')
				.getByRole('row', {name: styleBookName});

			await styleBookRow.getByRole('button', {name: /Actions$/}).click();

			await page
				.getByRole('menuitem', {
					exact: true,
					name: 'Edit in Style Book Editor',
				})
				.click();

			await expect(page).toHaveURL(/edit_style_book_entry/);
		});

		const breadcrumb = page
			.getByRole('navigation', {name: 'Breadcrumb'})
			.last();

		await test.step('Verify the editor breadcrumb structure', async () => {
			await expect(breadcrumb).toBeVisible();

			const items = breadcrumb.getByRole('listitem');

			await expect(items).toHaveCount(2);

			await expect(items.first()).toContainText(designLibraryName);

			await expect(items.last()).toContainText(styleBookName);

			await expect(items.last()).toHaveClass(/active/);
		});

		await test.step('Check that the preview dropdown lists the connected site page', async () => {
			await page
				.getByRole('button', {name: 'Pages'})
				.or(page.getByRole('button', {name: 'Fragments'}))
				.or(page.getByRole('button', {name: 'Masters'}))
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Pages'}).click();

			await expect(
				page.getByRole('button', {
					name: `[${connectedSiteName}] ${pageName}`,
				})
			).toBeVisible();
		});

		await test.step('Click back and verify the design library content screen is restored', async () => {
			await breadcrumb
				.getByRole('link', {name: designLibraryName})
				.click();

			await expect(page).toHaveURL(/view_resources_design_library/);

			await expect(
				page
					.locator('.design-library-fds-wrapper--resources table')
					.getByRole('row', {name: styleBookName})
			).toBeVisible();
		});

		await test.step('Remove the design library and the connected site', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
				createdDesignLibrary.externalReferenceCode
			);

			await apiHelpers.headlessAdminSite.deleteSite(
				connectedSite.externalReferenceCode
			);
		});
	}
);

const testWithSite = mergeTests(test, isolatedSiteTest, pagesAdminPagesTest);

testWithSite(
	'Design library style books are filtered by page theme in the style book selector',
	{tag: '@LPD-83671'},
	async ({apiHelpers, designLibrariesPage, page, pagesAdminPage, site}) => {
		const classicStyleBookName = getRandomString();
		const cmsStyleBookName = getRandomString();
		const designLibraryName = getRandomString();
		const pageName = getRandomString();

		const createdDesignLibrary = await testWithSite.step(
			'Create a design library via headless',
			async () => {
				return await apiHelpers.headlessAssetLibrary.createAssetLibrary(
					{
						name: designLibraryName,
						settings: {},
						type: 'DesignLibrary',
					}
				);
			}
		);

		try {
			await testWithSite.step(
				'Create a widget page in the site',
				async () => {
					await apiHelpers.jsonWebServicesLayout.addLayout({
						groupId: String(site.id),
						options: {type: 'portlet'},
						title: pageName,
					});
				}
			);

			await testWithSite.step(
				'Create a Classic Theme style book in the design library',
				async () => {
					await designLibrariesPage.createStyleBook(
						designLibraryName,
						classicStyleBookName,
						'Classic Theme'
					);
				}
			);

			await testWithSite.step(
				'Create a CMS Theme style book in the design library',
				async () => {
					await designLibrariesPage.createStyleBook(
						designLibraryName,
						cmsStyleBookName,
						'CMS Theme'
					);
				}
			);

			await testWithSite.step(
				'Assert no design library style books are visible when not connected to the site',
				async () => {
					await pagesAdminPage.goto(site.friendlyUrlPath);

					await pagesAdminPage.goToDesignTabConfiguration(pageName);

					const styleBookTextbox = page.getByRole('textbox', {
						name: 'Style Book',
					});

					const selectStyleBookDialog = page.getByRole('dialog', {
						name: 'Select Style Book',
					});

					await clickAndExpectToBeVisible({
						target: selectStyleBookDialog,
						trigger: styleBookTextbox,
					});

					await expect(
						selectStyleBookDialog.getByText(
							'Styles from Classic Theme',
							{exact: true}
						)
					).toBeVisible();

					await expect(
						selectStyleBookDialog.getByText(classicStyleBookName, {
							exact: true,
						})
					).toBeHidden();

					await expect(
						selectStyleBookDialog.getByText(cmsStyleBookName, {
							exact: true,
						})
					).toBeHidden();

					await selectStyleBookDialog.getByLabel('Close').click();
				}
			);

			await testWithSite.step(
				'Connect the design library to the site',
				async () => {
					await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
						createdDesignLibrary.id,
						site.id
					);
				}
			);

			await testWithSite.step(
				'Assert only the Classic Theme style book is visible in the selector after connecting',
				async () => {
					await pagesAdminPage.goto(site.friendlyUrlPath);

					await pagesAdminPage.goToDesignTabConfiguration(pageName);

					const styleBookTextbox = page.getByRole('textbox', {
						name: 'Style Book',
					});

					const selectStyleBookDialog = page.getByRole('dialog', {
						name: 'Select Style Book',
					});

					await clickAndExpectToBeVisible({
						target: selectStyleBookDialog,
						trigger: styleBookTextbox,
					});

					await expect(
						selectStyleBookDialog.getByText(classicStyleBookName, {
							exact: true,
						})
					).toBeVisible();

					await expect(
						selectStyleBookDialog.getByText(cmsStyleBookName, {
							exact: true,
						})
					).toBeHidden();

					await selectStyleBookDialog.getByLabel('Close').click();
				}
			);
		}
		finally {
			await testWithSite.step('Remove the design library', async () => {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					createdDesignLibrary.externalReferenceCode
				);
			});
		}
	}
);
