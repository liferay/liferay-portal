/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Add multiple suborganizations to parent organization',
	{tag: '@LPD-57824'},
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
		await page.on('dialog', (dialog) => dialog.accept());

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
				}).toPass();

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

		await usersAndOrganizationsPage.addOrganizationButton.click();

		await expect(editOrganizationPage.countrySelect).not.toHaveAttribute(
			'required'
		);
		await expect(editOrganizationPage.regionSelect).not.toHaveAttribute(
			'required'
		);
	}
);
