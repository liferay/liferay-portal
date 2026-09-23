/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {countriesManagementPageTest} from '../../../fixtures/CountriesManagementPageTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	countriesManagementPageTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'Add organizations',
	{tag: '@LPD-81993'},
	async ({editOrganizationPage, page, usersAndOrganizationsPage}) => {
		const organizationNames = [
			`Organization${getRandomInt()}`,
			`Organization${getRandomInt()}`,
		];

		try {
			for (const orgName of organizationNames) {
				await usersAndOrganizationsPage.goToOrganizations();

				await usersAndOrganizationsPage.addOrganizationButton.click();

				await editOrganizationPage.addOrganization(orgName);

				await expect(editOrganizationPage.backButton).toBeVisible();

				await usersAndOrganizationsPage.goToOrganizations();

				await usersAndOrganizationsPage.organizationsTable.search(
					orgName
				);

				await expect(
					usersAndOrganizationsPage.organizationsTable.cell(orgName)
				).toBeVisible();
				await expect(
					usersAndOrganizationsPage.organizationsTable.cell(
						'Organization'
					)
				).toBeVisible();
			}
		}
		finally {
			page.on('dialog', (dialog) => dialog.accept());

			for (const orgName of organizationNames) {
				await usersAndOrganizationsPage.goToOrganizations();

				await usersAndOrganizationsPage.organizationsTable.search(
					orgName
				);

				await expect(async () => {
					await (
						await usersAndOrganizationsPage.organizationsTable.rowActions(
							orgName
						)
					).click();

					await expect(
						usersAndOrganizationsPage.deleteOrganizationMenuItem
					).toBeVisible({timeout: 500});
				}).toPass({timeout: 5000});

				await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

				await waitForAlert(page);
			}
		}
	}
);

test(
	'Add suborganization',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const parentOrganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		const suborgName = `Suborganization${getRandomInt()}`;

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			parentOrganization.name
		);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				parentOrganization.name
			)
		).toBeVisible();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					parentOrganization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.addOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.addOrganizationMenuItem.click();

		try {
			await editOrganizationPage.addOrganization(suborgName);

			await expect(editOrganizationPage.nameInput).toHaveValue(
				suborgName
			);

			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable
				.valueLink(parentOrganization.name)
				.click();

			await expect(
				usersAndOrganizationsPage.organizationsBreadcrumbLink(
					parentOrganization.name
				)
			).toBeVisible();

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(suborgName)
			).toBeVisible();

			await expect(async () => {
				await (
					await usersAndOrganizationsPage.organizationsTable.rowActions(
						suborgName
					)
				).click();

				await expect(
					usersAndOrganizationsPage.editOrganizationMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await usersAndOrganizationsPage.editOrganizationMenuItem.click();

			await expect(editOrganizationPage.headerTitle).toHaveText(
				`Edit ${suborgName}`
			);
			await expect(editOrganizationPage.nameInput).toHaveValue(
				suborgName
			);
		}
		finally {
			page.on('dialog', (dialog) => dialog.accept());

			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable
				.valueLink(parentOrganization.name)
				.click();

			await expect(async () => {
				await (
					await usersAndOrganizationsPage.organizationsTable.rowActions(
						suborgName
					)
				).click();

				await expect(
					usersAndOrganizationsPage.deleteOrganizationMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

			await waitForAlert(page);
		}
	}
);

test(
	'Cannot delete an organization with suborganization',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		const parentOrganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		const suborganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Suborganization${getRandomInt()}`,
				parentOrganization: {
					externalReferenceCode:
						parentOrganization.externalReferenceCode,
				},
			});

		page.on('dialog', (dialog) => dialog.accept());

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					parentOrganization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.deleteOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

		await expect(usersAndOrganizationsPage.errorMessage).toContainText(
			'You cannot delete organizations that have suborganizations or users.'
		);

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(parentOrganization.name)
			.click();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					suborganization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.removeOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.removeOrganizationMenuItem.click();

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					parentOrganization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.deleteOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

		await waitForAlert(page);

		await usersAndOrganizationsPage.organizationsTable.search(
			parentOrganization.name
		);

		await expect(
			usersAndOrganizationsPage.organizationsTableEmptyMessage
		).toBeVisible();
	}
);

test(
	'Can view listed users and suborganizations not mixed',
	{tag: ['@LPD-81993', '@LPS-198960']},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		const suborganizations = [];

		for (let count = 1; count <= 4; count++) {
			const suborganization =
				await apiHelpers.headlessAdminUser.postOrganization({
					name: `${String.fromCharCode(64 + count)} Suborganization${getRandomInt()}`,
					parentOrganization: {
						externalReferenceCode:
							organization.externalReferenceCode,
					},
				});

			suborganizations.push(suborganization);
		}

		const users = [];

		for (let count = 1; count <= 2; count++) {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization.id,
				user.emailAddress
			);

			users.push(user);
		}

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(organization.name)
			.click();

		await usersAndOrganizationsPage.organizationsTable.changeView('Table');

		await expect(
			usersAndOrganizationsPage.organizationsTableDivider
		).toBeVisible();
		await expect(usersAndOrganizationsPage.usersTableDivider).toBeVisible();

		for (const suborganization of suborganizations) {
			await expect(
				usersAndOrganizationsPage.organizationsTableDivider.locator(
					`~ tr:has-text("${suborganization.name}")`
				)
			).toBeVisible();
		}

		for (const user of users) {
			await expect(
				usersAndOrganizationsPage.usersTableDivider.locator(
					`~ tr:has-text("${user.givenName} ${user.familyName}")`
				)
			).toBeVisible();
		}
	}
);

test(
	'Can search and order organizations by name and tags',
	{tag: '@LPD-81993'},
	async ({apiHelpers, editOrganizationPage, usersAndOrganizationsPage}) => {
		const tag1 = `alpha${getRandomInt()}`;
		const tag2 = `bravo${getRandomInt()}`;

		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `AOrganization${getRandomInt()}`,
			});

		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `ZOrganization${getRandomInt()}`,
			});

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization1.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.addTag(tag1);

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization2.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.addTag(tag2);

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			organization1.name
		);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization1.name
			)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization2.name
			)
		).toHaveCount(0);

		await usersAndOrganizationsPage.organizationsTable.search(
			organization2.name
		);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization2.name
			)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization1.name
			)
		).toHaveCount(0);

		await usersAndOrganizationsPage.organizationsTable.search(tag1);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization1.name
			)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization2.name
			)
		).toHaveCount(0);

		await usersAndOrganizationsPage.organizationsTable.search(tag2);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization2.name
			)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization1.name
			)
		).toHaveCount(0);

		await usersAndOrganizationsPage.organizationsTable.search('');

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization1.name
			)
		).toBeVisible();
		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				organization2.name
			)
		).toBeVisible();

		await expect(async () => {
			await usersAndOrganizationsPage.organizationsTable.orderButton.click();
			await usersAndOrganizationsPage.organizationsTable.clickOrderMenuItem(
				'Name'
			);
		}).toPass({timeout: 5000});

		await expect(async () => {
			await usersAndOrganizationsPage.organizationsTable.orderButton.click();
			await usersAndOrganizationsPage.organizationsTable.clickOrderMenuItem(
				'Ascending'
			);

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					organization1.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await expect(
			usersAndOrganizationsPage.organizationsTable.optionalColumnRow(1, 1)
		).toContainText(organization1.name);

		await expect(async () => {
			await usersAndOrganizationsPage.organizationsTable.orderButton.click();
			await usersAndOrganizationsPage.organizationsTable.clickOrderMenuItem(
				'Descending'
			);

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					organization2.name
				)
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await expect(
			usersAndOrganizationsPage.organizationsTable.optionalColumnRow(1, 1)
		).toContainText(organization2.name);

		await expect(async () => {
			await usersAndOrganizationsPage.organizationsTable.orderButton.click();
			await usersAndOrganizationsPage.organizationsTable.clickOrderMenuItem(
				'Ascending'
			);
		}).toPass({timeout: 5000});
	}
);

test(
	'Edit organization site and delete organization site',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		sitesAdminPage,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.click();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		await sitesAdminPage.goto();
		await sitesAdminPage.searchSite(organization.name);

		await expect(
			sitesAdminPage.sitesTable.valueLink(organization.name)
		).toBeVisible();
		await expect(sitesAdminPage.sitesTable.cell('Private')).toBeVisible();

		const newOrganizationName = `Organization${getRandomInt()}`;

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.nameInput.fill(newOrganizationName);
		await editOrganizationPage.countrySelect.selectOption('United States');
		await editOrganizationPage.regionSelect.selectOption('California');
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(editOrganizationPage.nameInput).toHaveValue(
			newOrganizationName
		);

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			newOrganizationName
		);

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell(
				newOrganizationName
			)
		).toBeVisible();

		await sitesAdminPage.goto();
		await sitesAdminPage.searchSite(organization.name);

		await expect(sitesAdminPage.noSitesMessage).toBeVisible();

		await sitesAdminPage.searchSite(newOrganizationName);

		await expect(
			sitesAdminPage.sitesTable.valueLink(newOrganizationName)
		).toBeVisible();

		await expect(async () => {
			await (
				await sitesAdminPage.sitesTable.rowActions(
					newOrganizationName,
					1,
					false
				)
			).click();

			await expect(sitesAdminPage.deleteSiteMenuItem).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

		await sitesAdminPage.deleteSiteMenuItem.click();
		await sitesAdminPage.deleteModalButton.click();

		await waitForAlert(page);

		await expect(sitesAdminPage.noSitesMessage).toBeVisible();
	}
);

test(
	'Assign user to organization with site',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		sitesAdminPage,
		userPersonalSitePage,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.click();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		await sitesAdminPage.goto();
		await sitesAdminPage.searchSite(organization.name);

		await expect(
			sitesAdminPage.sitesTable.valueLink(organization.name)
		).toBeVisible();

		await userPersonalSitePage.goToMyDashboard();

		await expect(userPersonalSitePage.mySitesLink).toBeVisible();

		await expect(page.getByText(organization.name)).not.toBeVisible();

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			'test@liferay.com'
		);

		try {
			await userPersonalSitePage.goToMyDashboard();

			await expect(userPersonalSitePage.mySitesLink).toBeVisible();

			await expect(page.getByText(organization.name)).toBeVisible();
		}
		finally {
			await apiHelpers.headlessAdminUser.deleteOrganizationUserAccountAssociation(
				organization.id,
				'test@liferay.com'
			);
		}
	}
);

test(
	'Can add organization user after first using invalid email',
	{tag: ['@LPD-81993', '@LPS-140712', '@LPS-141217']},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(usersAndOrganizationsPage.addUserMenuItem).toBeVisible(
				{timeout: 500}
			);
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.addUserMenuItem.click();

		const userScreenName = `usersn${getRandomInt()}`;
		const userEmailAddress = `${userScreenName}@liferay.com`;
		const userFirstName = `userfn${getRandomInt()}`;
		const userLastName = `userln${getRandomInt()}`;

		await usersAndOrganizationsPage.screenNameInput.fill(userScreenName);
		await usersAndOrganizationsPage.emailAddressInput.fill(
			'postmaster@liferay.com'
		);
		await usersAndOrganizationsPage.firstNameInput.fill(userFirstName);
		await usersAndOrganizationsPage.lastNameInput.fill(userLastName);
		await usersAndOrganizationsPage.saveUserButton.click();

		await expect(usersAndOrganizationsPage.errorMessage).toContainText(
			'Please enter a valid email address.'
		);

		await usersAndOrganizationsPage.emailAddressInput.fill(
			userEmailAddress
		);
		await usersAndOrganizationsPage.saveUserButton.click();

		await waitForAlert(page, 'Success:The user was created successfully.');

		try {
			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable
				.valueLink(organization.name)
				.click();

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					`${userFirstName} ${userLastName}`
				)
			).toBeVisible();
		}
		finally {
			const userAccount =
				await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
					userEmailAddress
				);

			if (userAccount?.id) {
				await apiHelpers.headlessAdminUser.deleteUserAccount(
					userAccount.id
				);
			}
		}
	}
);

test(
	'Add multiple suborganizations to parent organization',
	{tag: ['@LPD-57824', '@LPD-70012']},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const parentOrganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: 'Parent Organization' + getRandomInt(),
			});

		const subOrganizations = [];

		for (let count = 1; count <= 5; count++) {
			const subOrganization =
				await apiHelpers.headlessAdminUser.postOrganization({
					name: `Suborganization Name ${count}`,
					parentOrganization: {
						externalReferenceCode:
							parentOrganization.externalReferenceCode,
					},
				});

			subOrganizations.push(subOrganization);
		}

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable
			.valueLink(parentOrganization.name)
			.click();

		for (const subOrganization of subOrganizations) {
			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					subOrganization.name
				)
			).toBeVisible();
		}

		await expect(
			usersAndOrganizationsPage.organizationsTable.cell('Approved')
		).toBeVisible();

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			'Suborganization'
		);

		for (const subOrganization of subOrganizations) {
			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					subOrganization.name
				)
			).toBeVisible();
		}
	}
);

test(
	'Add, view and delete organization, add comment, and assign global category to it.',
	{tag: '@LPD-57824'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const orgName = 'Organization' + getRandomInt();

		const deleteOrganization = async () => {
			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable.search(orgName);
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					orgName
				)
			).click();
			await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

			await waitForAlert(page);

			await usersAndOrganizationsPage.organizationsTable.search(orgName);

			await expect(
				usersAndOrganizationsPage.organizationsTableEmptyMessage
			).toBeVisible();
		};

		await test.step('Search and view organization created via API', async () => {
			await apiHelpers.headlessAdminUser.postOrganization({
				name: orgName,
			});

			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable.search(orgName);

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(orgName)
			).toBeVisible();
			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(
					'Organization'
				)
			).toBeVisible();

			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					orgName
				)
			).click();
			await usersAndOrganizationsPage.editOrganizationMenuItem.click();

			await expect(editOrganizationPage.headerTitle).toHaveText(
				`Edit ${orgName}`
			);
			await expect(editOrganizationPage.nameInput).toHaveValue(orgName);
			await expect(editOrganizationPage.typeLabel).toHaveValue(
				'Organization'
			);

			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable.search(
				'Nonexistent Org'
			);

			await expect(
				usersAndOrganizationsPage.organizationsTable.cell(orgName)
			).not.toBeVisible();
		});

		await test.step('Delete organization created via API', async () => {
			await deleteOrganization();
		});

		await test.step('Set category and insert comment for organization created via UI', async () => {
			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const group = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				companyId,
				companyId
			);

			const categoryName = 'Category' + getRandomInt();
			const vocabularyName = 'Vocabulary' + getRandomInt();

			try {
				await createCategories({
					apiHelpers,
					categoryNames: [{name: categoryName}],
					siteId: group.groupId,
					vocabularyName,
				});

				await usersAndOrganizationsPage.addOrganizationButton.click();
				await editOrganizationPage.nameInput.fill(orgName);
				await editOrganizationPage.countrySelect.selectOption(
					'United States'
				);
				await editOrganizationPage.regionSelect.selectOption(
					'California'
				);

				await expect(async () => {
					await editOrganizationPage
						.categoryInput(vocabularyName)
						.click();
					await editOrganizationPage
						.categoryOption(categoryName)
						.click({timeout: 1000});
				}).toPass({timeout: 5000});

				const comment = 'This is a test comment!';

				await editOrganizationPage.commentsInput.fill(comment);
				await editOrganizationPage.saveButton.click();

				await waitForAlert(page);

				await usersAndOrganizationsPage.goToOrganizations();

				await (
					await usersAndOrganizationsPage.organizationsTable.rowActions(
						orgName
					)
				).click();
				await usersAndOrganizationsPage.editOrganizationMenuItem.click();

				await expect(editOrganizationPage.commentsInput).toHaveValue(
					comment
				);
				await expect(
					editOrganizationPage.categoryGridCell(categoryName)
				).toBeVisible();
			}
			finally {
				const vocabulary =
					await apiHelpers.headlessAdminTaxonomy.getTaxonomyVocabularyBySiteId(
						group.groupId
					);

				await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
					vocabulary.id
				);
			}
		});

		await test.step('Delete organization created via UI', async () => {
			await deleteOrganization();
		});
	}
);

test(
	'Can view status when assigning an organization role',
	{tag: ['@@LPD-59032']},
	async ({apiHelpers, usersAndOrganizationsPage}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await usersAndOrganizationsPage.goToOrganizations();
		await (
			await usersAndOrganizationsPage.organizationsTable.rowActions(
				organization.name
			)
		).click();
		await usersAndOrganizationsPage.assignOrganizationRolesMenuItem.click();

		await expect(
			await usersAndOrganizationsPage.assignOrganizationRolesTableStatus(
				'Account Manager',
				'Approved'
			)
		).toBeVisible();
	}
);

test(
	'Country and region should not be required for a default organization.',
	{tag: '@LPD-63206'},
	async ({editOrganizationPage, usersAndOrganizationsPage}) => {
		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await usersAndOrganizationsPage.addOrganizationButton.click();

			await expect(editOrganizationPage.countrySelect).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 6000});

		await expect(editOrganizationPage.countrySelect).not.toHaveAttribute(
			'required'
		);
		await expect(editOrganizationPage.regionSelect).not.toHaveAttribute(
			'required'
		);
	}
);

test(
	'User should not be able to trigger stored XSS using organization name via info panel',
	{tag: ['@LPD-70029']},
	async ({
		editOrganizationPage,
		page,
		sitesAdminPage,
		usersAndOrganizationsPage,
	}) => {
		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsLink.click();
		await expect(async () => {
			await usersAndOrganizationsPage.addOrganizationButton.click();

			await expect(editOrganizationPage.countrySelect).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 6000});

		const organizationName = `"><img src=x onerror=prompt(${getRandomInt()})></img>`;

		await editOrganizationPage.nameInput.fill(organizationName);
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		try {
			await editOrganizationPage.organizationSiteLink.click();
			await editOrganizationPage.createSiteToggle.click();
			await editOrganizationPage.organizationSiteSaveButton.click();

			await waitForAlert(page);

			await sitesAdminPage.goto();
			await sitesAdminPage.searchSite(organizationName);

			await sitesAdminPage.infoPanelButton.click();

			await page.getByTitle('Select', {exact: true}).check();

			let dialogTriggered = false;

			await page
				.waitForEvent('dialog', {timeout: 500})
				.then(async (dialog) => {
					dialogTriggered = true;
					await dialog.dismiss();
				})
				.catch(() => {});

			await expect(sitesAdminPage.componentTitle).toContainText(
				organizationName
			);

			expect(dialogTriggered).toBe(false);
		}
		finally {
			page.once('dialog', async (dialog) => {
				await dialog.accept();
			});

			await usersAndOrganizationsPage.goToOrganizations();

			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organizationName
				)
			).click();
			await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();
		}
	}
);

test(
	'Text XSS vulnerability in country and region key value',
	{tag: '@LPD-72270'},
	async ({
		countriesManagementPage,
		editCountryPage,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const xssString = `AnyName<img src=x onerror="alert('xssCountry')">`;

		await countriesManagementPage.goto();

		const country = {
			key: `${xssString}`,
			number: String(getRandomInt()),
			priority: '0',
			threeLetterIsocode: getRandomString().substring(0, 3),
			title: '',
			twoLetterIsocode: getRandomString().substring(0, 2),
		};

		await expect(async () => {
			await expect(
				countriesManagementPage.countriesTable.searchInput
			).toBeEditable();

			await countriesManagementPage.countriesTable.newButton.click();

			await expect(editCountryPage.titleInput).toBeVisible({
				timeout: 500,
			});
		}).toPass({timeout: 5000});

		await editCountryPage.editCountry(country);

		await usersAndOrganizationsPage.goToOrganizations();

		await expect(async () => {
			await usersAndOrganizationsPage.addOrganizationButton.click();

			await expect(editOrganizationPage.nameInput).toBeVisible({
				timeout: 5000,
			});
		}).toPass({timeout: 20000});

		const xssOrgNamePrefix = `AnyName${getRandomInt()}`;
		const xssOrgName = `${xssOrgNamePrefix}<img src=x onerror="alert('xssOrg')">`;

		await editOrganizationPage.nameInput.fill(xssOrgName);
		await editOrganizationPage.countrySelect.selectOption(`${country.key}`);
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		try {
			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.changeView('List');

			page.on('dialog', async (dialog) => {
				if (dialog.type() === 'alert') {
					throw new Error('XSS');
				}
			});

			await expect(async () => {
				await usersAndOrganizationsPage.organizationsTable.search(
					xssOrgNamePrefix
				);

				await expect(
					page.getByText(xssOrgName, {exact: true})
				).toBeVisible({timeout: 5000});
				await expect(
					page.getByText(xssString, {exact: true})
				).toBeVisible({timeout: 5000});
			}).toPass({timeout: 60000});
		}
		finally {
			page.on('dialog', async (dialog) => await dialog.accept());

			await countriesManagementPage.goto();

			await expect(async () => {
				await countriesManagementPage.countriesTable.search(
					country.key
				);
				await (
					await countriesManagementPage.countriesTable.rowActions(
						country.key
					)
				).click();
				await countriesManagementPage.deleteButton.click({
					timeout: 500,
				});

				await waitForAlert(page);
			}).toPass({timeout: 5000});

			await usersAndOrganizationsPage.goToOrganizations();
			await usersAndOrganizationsPage.changeView('Table');

			await usersAndOrganizationsPage.goToOrganizations();

			await usersAndOrganizationsPage.organizationsTable.search(
				xssOrgNamePrefix
			);

			await expect(async () => {
				await (
					await usersAndOrganizationsPage.organizationsTable.rowActions(
						xssOrgName
					)
				).click();

				await expect(
					usersAndOrganizationsPage.deleteOrganizationMenuItem
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await usersAndOrganizationsPage.deleteOrganizationMenuItem.click();

			await waitForAlert(page);
		}
	}
);

test(
	'View organization site via sites directory',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		site,
		usersAndOrganizationsPage,
		widgetPagePage,
	}) => {
		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});
		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		const sitesDirectoryLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: `SitesDirectoryPage${getRandomInt()}`,
			});

		await page.goto(
			`/web${site.friendlyUrlPath}${sitesDirectoryLayout.friendlyURL}`
		);

		await widgetPagePage.addPortlet('Sites Directory');

		await expect(
			page.getByText(organization1.name, {exact: true})
		).not.toBeVisible();
		await expect(
			page.getByText(organization2.name, {exact: true})
		).not.toBeVisible();

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			organization1.name
		);

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization1.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.organizationSiteLink.click();
		await editOrganizationPage.createSiteToggle.click();
		await editOrganizationPage.organizationSiteSaveButton.click();

		await waitForAlert(page);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const organizationSite =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				companyId,
				`${organization1.name} LFR_ORGANIZATION`
			);

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: organizationSite.groupId,
			title: 'Test Page',
		});

		await page.goto(
			`/web${site.friendlyUrlPath}${sitesDirectoryLayout.friendlyURL}`
		);

		await expect(
			page.getByText(organization1.name, {exact: true})
		).toBeVisible();
		await expect(
			page.getByText(organization2.name, {exact: true})
		).not.toBeVisible();

		await page.getByText(organization1.name, {exact: true}).click();

		await widgetPagePage.addPortlet('Breadcrumb');

		await page.goto(
			`/web${site.friendlyUrlPath}${sitesDirectoryLayout.friendlyURL}`
		);

		await page.getByText(organization1.name, {exact: true}).click();

		await expect(
			page.locator('.breadcrumb').getByText(organization1.name)
		).toBeVisible();
		await expect(
			page.locator('.breadcrumb').getByText('Test Page')
		).toBeVisible();
	}
);

test(
	'User without permissions cannot add organization via URL',
	{tag: ['@LPD-81993', '@LPS-75084']},
	async ({apiHelpers, page}) => {
		await apiHelpers.headlessAdminUser.postOrganization({
			name: `Organization${getRandomInt()}`,
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performUserSwitch(page, user.alternateName);

		await page.goto(
			'/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_MyOrganizationsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_mvcRenderCommandName=%2Fusers_admin%2Fedit_organization&_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_type=organization'
		);

		await expect(page.locator('.alert-danger').first()).toContainText(
			'You do not have the required permissions.'
		);

		await page.goto(
			'/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_MyOrganizationsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_mvcPath=%2Fedit_organization.jsp&_com_liferay_users_admin_web_portlet_MyOrganizationsPortlet_type=organization'
		);

		await expect(page.locator('.alert-danger').first()).toContainText(
			'You do not have the required permissions.'
		);

		await page.goto(
			'/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_MyOrganizationsPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_screenNavigationCategoryKey=organizations&_com_liferay_users_admin_web_portlet_UsersAdminPortlet_usersListView=flat-organizations'
		);

		await expect(page.locator('.alert-info').first()).toContainText(
			'You do not belong to an organization and are not allowed to view other organizations.'
		);
	}
);

test(
	'Can manage organization contact information and security questions',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomInt()}`,
			});

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			organization.name
		);

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.contactLink.click();

		await editOrganizationPage.addAddressButton.click();

		await editOrganizationPage.addAddressTypeSelect.selectOption('Billing');
		await editOrganizationPage.addAddressStreet1Input.fill(
			'1400 Montefino Ave'
		);
		await editOrganizationPage.addAddressCityInput.fill('Diamond Bar');
		await editOrganizationPage.addAddressCountrySelect.selectOption(
			'United States'
		);
		await editOrganizationPage.addAddressRegionSelect.selectOption(
			'California'
		);
		await editOrganizationPage.addAddressPostalCodeInput.fill('91765');
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(page.getByText('Billing')).toBeVisible();
		await expect(page.getByText('1400 Montefino Ave')).toBeVisible();
		await expect(
			page.getByText('Diamond Bar, California 91765')
		).toBeVisible();
		await expect(page.getByText('United States')).toBeVisible();

		await editOrganizationPage.contactInformationLink.click();

		await editOrganizationPage.addPhoneNumberButton.click();
		await editOrganizationPage.addPhoneNumberInput.fill('1-877-543-3729');
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(page.getByText('1-877-543-3729')).toBeVisible();

		await editOrganizationPage.addEmailAddressButton.click();
		await editOrganizationPage.addEmailAddressInput.fill(
			'testuser@liferay.com'
		);
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(page.getByText('testuser@liferay.com')).toBeVisible();

		await editOrganizationPage.addWebsiteButton.click();
		await editOrganizationPage.addWebsiteUrlInput.fill(
			'https://www.liferay.com'
		);
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(page.getByText('https://www.liferay.com')).toBeVisible();

		await editOrganizationPage.openingHoursLink.click();

		await editOrganizationPage.addOpeningHoursButton.click();
		await editOrganizationPage.mondayOpenSelect.selectOption('08:00');
		await editOrganizationPage.mondayCloseSelect.selectOption('18:00');
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await expect(page.getByText('Monday')).toBeVisible();
		await expect(page.getByText('08:00')).toBeVisible();
		await expect(page.getByText('18:00')).toBeVisible();

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			organization.name
		);

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		const comment = getRandomString();

		await editOrganizationPage.commentsInput.fill(comment);
		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await page.reload();

		await expect(editOrganizationPage.commentsInput).toHaveValue(comment);

		await editOrganizationPage.securityQuestionsLink.click();

		const securityQuestion = getRandomString();

		await editOrganizationPage.securityQuestionsInput.click();
		await page.keyboard.type(securityQuestion);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: editOrganizationPage.securityQuestionsLocaleItem('ja-JP'),
			trigger: editOrganizationPage.securityQuestionsLocaleButton,
		});

		await editOrganizationPage.securityQuestionsInput.click();
		await page.keyboard.type('Nihongo');

		await editOrganizationPage.saveButton.click();

		await waitForAlert(page);

		await usersAndOrganizationsPage.goToOrganizations();

		await usersAndOrganizationsPage.organizationsTable.search(
			organization.name
		);

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.organizationsTable.rowActions(
					organization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.securityQuestionsLink.click();

		await expect(editOrganizationPage.securityQuestionsInput).toHaveValue(
			securityQuestion
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: editOrganizationPage.securityQuestionsLocaleItem('ja-JP'),
			trigger: editOrganizationPage.securityQuestionsLocaleButton,
		});

		await expect(editOrganizationPage.securityQuestionsInput).toHaveValue(
			'Nihongo'
		);
	}
);

test(
	'Cannot add organization without permission',
	{tag: '@LPD-81993'},
	async ({apiHelpers, page, usersAndOrganizationsPage}) => {
		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `RegRole${getRandomString()}`,
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_users_admin_web_portlet_UsersAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.portal.kernel.model.UserGroup',
					scope: 1,
				},
			],
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: userData['test'].password,
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await test.step('Verify user cannot see Add button or organizations', async () => {
			await performUserSwitch(page, user.alternateName);

			await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

			await expect(
				usersAndOrganizationsPage.addOrganizationButton
			).not.toBeVisible();
			await expect(
				page.getByText(
					'You do not belong to an organization and are not allowed to view other organizations.'
				)
			).toBeVisible();
		});

		await test.step('Grant ADD_ORGANIZATION, create org as admin, then revoke', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.addResourcePermission(
				'ADD_ORGANIZATION',
				companyId,
				'0',
				'90',
				companyId,
				String(role.id),
				'1'
			);

			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomString()}`,
			});

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.removeResourcePermission(
				'ADD_ORGANIZATION',
				companyId,
				'0',
				'90',
				companyId,
				String(role.id),
				'1'
			);
		});

		await test.step('Verify user still cannot see Add button or organizations', async () => {
			await performUserSwitch(page, user.alternateName);

			await usersAndOrganizationsPage.goToOrganizationsWithLimitedAccess();

			await expect(
				usersAndOrganizationsPage.addOrganizationButton
			).not.toBeVisible();
			await expect(
				page.getByText(
					'You do not belong to an organization and are not allowed to view other organizations.'
				)
			).toBeVisible();
		});
	}
);

test(
	'Organization admin can manage suborganizations',
	{tag: '@LPD-81993'},
	async ({
		apiHelpers,
		editOrganizationPage,
		page,
		usersAndOrganizationsPage,
	}) => {
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: userData['test'].password,
			surname: user.familyName,
		};

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization${getRandomString()}`,
			});
		const suborganization =
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Suborganization${getRandomString()}`,
				parentOrganization: {id: organization.id},
			});

		await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
			organization.id,
			user.emailAddress
		);

		const organizationAdminRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Organization Administrator'
			);

		await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
			organizationAdminRole!.id!,
			String(user.id),
			organization.id
		);

		await performUserSwitch(page, user.alternateName);

		await usersAndOrganizationsPage.goToMyOrganizations();

		await (
			await usersAndOrganizationsPage.myOrganizationsTableRowLink(
				organization.name
			)
		).click();

		const editedName = `SuborganizationEdit${getRandomString()}`;

		await expect(async () => {
			await (
				await usersAndOrganizationsPage.myOrganizationsTableRowActions(
					suborganization.name
				)
			).click();

			await expect(
				usersAndOrganizationsPage.editOrganizationMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await usersAndOrganizationsPage.editOrganizationMenuItem.click();

		await editOrganizationPage.nameInput.fill(editedName);
		await editOrganizationPage.countrySelect.selectOption({
			label: 'United States',
		});
		await editOrganizationPage.regionSelect.selectOption({
			label: 'California',
		});
		await editOrganizationPage.saveButton.click();

		await page.waitForLoadState('networkidle');

		await page.goto('/');
		await page.waitForLoadState('networkidle');

		await usersAndOrganizationsPage.goToMyOrganizations();

		await (
			await usersAndOrganizationsPage.myOrganizationsTableRowLink(
				organization.name
			)
		).click();

		await expect(
			await usersAndOrganizationsPage.myOrganizationsUserAndOrgsTableRowLink(
				editedName
			)
		).toBeVisible();
	}
);
