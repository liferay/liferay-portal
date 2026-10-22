/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js/v1_0';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

test('can create entry related to Commerce Product Group', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: pricingClassDefinition} =
		await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			'L_COMMERCE_PRODUCT_GROUP'
		);

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			pricingClassDefinition.externalReferenceCode,
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	const pricingClassName = 'PG' + getRandomInt();

	const pricingClass = await apiHelpers.post(
		`${apiHelpers.baseUrl}headless-commerce-admin-catalog/v1.0/product-groups`,
		{
			data: {
				title: {en_US: pricingClassName},
			},
		}
	);

	try {
		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectDropdownItemWithSearch(
			pricingClassName
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('table td').getByText(pricingClassName).first()
		).toBeVisible();
	}
	finally {
		await apiHelpers.delete(
			`${apiHelpers.baseUrl}headless-commerce-admin-catalog/v1.0/product-groups/${pricingClass.id}`
		);

		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('can create entry related to Commerce Products', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: cpDefinition} =
		await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			'L_COMMERCE_PRODUCT_DEFINITION'
		);

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const relationshipName = 'relationship' + getRandomInt();

	const {body: relationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			cpDefinition.externalReferenceCode,
			{
				label: {en_US: 'Relationship'},
				name: relationshipName,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type: 'oneToMany',
			}
		);

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const productName = 'Simple T-Shirt ' + getRandomInt();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: productName},
		productType: 'simple',
	});

	try {
		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.selectDropdownItemWithSearch(productName);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await viewObjectEntriesPage.backButton.click();

		await expect(
			page.locator('table td').getByText(productName).first()
		).toBeVisible();
	}
	finally {
		await apiHelpers.headlessCommerceAdminCatalog.deleteProduct(product.id);

		await objectRelationshipAPIClient.deleteObjectRelationship(
			relationship.id
		);
	}
});

test('object scoped by company not displayed on site channel notifications', async ({
	apiHelpers,
	page,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		currencyCode: 'USD',
		name: 'TestChannel' + getRandomInt(),
		type: 'site',
	});

	await page.goto(PORTLET_URLS.commerceChannels, {waitUntil: 'networkidle'});

	await page.getByRole('link', {exact: true, name: channel.name}).click();

	await page.getByRole('link', {name: /Notification Templates/}).click();

	await page.waitForLoadState('networkidle');

	await page
		.getByRole('button', {name: 'Add Notification Template'})
		.first()
		.click();

	for (const optionType of ['Create', 'Delete', 'Update']) {
		await expect(
			page.locator(`option:text-is("${optionType}")`)
		).toBeHidden();
	}
});
