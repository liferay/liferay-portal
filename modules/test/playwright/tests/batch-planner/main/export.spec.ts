/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {dataMigrationCenterPagesTest} from './fixtures/dataMigrationCenterPagesTest';
import {parseJSONLToJSON} from './utils/JSONParser';

export const test = mergeTests(
	dataApiHelpersTest,
	dataMigrationCenterPagesTest,
	featureFlagsTest({
		'COMMERCE-8087': {enabled: true},
	}),
	loginTest()
);

const stockObjectDefinition: ObjectDefinition = {
	active: true,
	externalReferenceCode: 'stockERC',
	label: {
		en_US: 'stock',
	},
	name: 'Stock',
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: 'nameERC',
			indexed: true,
			indexedAsKeyword: true,
			label: {
				en_US: 'name',
			},
			name: 'name',
			required: true,
		},
	],
	pluralLabel: {
		en_US: 'stocks',
	},
	portlet: true,
	scope: 'company',
	status: {
		code: 0,
	},
};

const stockObjectEntry = {
	externalReferenceCode: 'nameERC',
	name: 'Stock Entry',
};

test('can export as JSONT', async ({apiHelpers, dataMigrationCenterPage}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			stockObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(stockObjectEntry, 'c/stocks');

	expect(
		JSON.parse(
			await dataMigrationCenterPage.exportFile(
				'JSONT',
				'Stock (v1.0 - Liferay Object REST)',
				['name']
			)
		)
	).toEqual({
		actions: {
			createBatch: {
				href: '/o/headless-batch-engine/v1.0/import-task/com.liferay.object.rest.dto.v1_0.ObjectEntry',
				method: 'POST',
			},
			deleteBatch: {
				href: '/o/headless-batch-engine/v1.0/import-task/com.liferay.object.rest.dto.v1_0.ObjectEntry',
				method: 'DELETE',
			},
			updateBatch: {
				href: '/o/headless-batch-engine/v1.0/import-task/com.liferay.object.rest.dto.v1_0.ObjectEntry',
				method: 'PUT',
			},
		},
		configuration: {
			callbackURL: null,
			className: 'com.liferay.object.rest.dto.v1_0.ObjectEntry',
			companyId: expect.any(Number),
			multiCompany: false,
			parameters: {
				containsHeaders: 'true',
				createStrategy: 'INSERT',
				importStrategy: 'ON_ERROR_FAIL',
				updateStrategy: 'UPDATE',
			},
			taskItemDelegateName: 'C_Stock',
			userId: expect.any(Number),
			version: 'v1.0',
		},
		items: [
			{
				name: 'Stock Entry',
			},
		],
	});
});

test('can export as JSON with excluded fields', async ({
	apiHelpers,
	dataMigrationCenterPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			stockObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(stockObjectEntry, 'c/stocks');

	expect(
		JSON.parse(
			await dataMigrationCenterPage.exportFile(
				'JSON',
				'Stock (v1.0 - Liferay Object REST)',
				['name']
			)
		)
	).toEqual([
		{
			name: 'Stock Entry',
		},
	]);
});

test('can export as JSON with all field types mapped', async ({
	apiHelpers,
	dataMigrationCenterPage,
}) => {
	const picklist = await apiHelpers.post(
		'/o/headless-admin-list-type/v1.0/list-type-definitions',
		{
			data: {
				externalReferenceCode: 'customPicklistERC',
				name: 'customPicklist',
				name_i18n: {
					en_US: 'customPicklist',
				},
			},
		}
	);

	apiHelpers.data.push({id: picklist.id, type: 'listTypeDefinition'});

	await apiHelpers.post(
		`/o/headless-admin-list-type/v1.0/list-type-definitions/${picklist.id}/list-type-entries`,
		{
			data: {
				key: 'distance1',
				name: 'distance1',
				name_i18n: {
					en_US: 'distance1',
				},
			},
		}
	);

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			externalReferenceCode: 'stockERC',
			label: {
				en_US: 'stock',
			},
			name: 'Stock',
			objectFields: [
				{
					DBType: 'String',
					businessType: 'Text',
					externalReferenceCode: 'nameERC',
					indexed: true,
					indexedAsKeyword: true,
					label: {
						en_US: 'name',
					},
					name: 'name',
					required: true,
				},
				{
					DBType: 'Boolean',
					businessType: 'Boolean',
					externalReferenceCode: 'customBoolean',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {en_US: 'customBoolean'},
					listTypeDefinitionId: 0,
					name: 'customBoolean',
					required: false,
					system: false,
					type: 'Boolean',
				},
				{
					DBType: 'Clob',
					businessType: 'LongText',
					externalReferenceCode: 'customLongText',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {en_US: 'customLongText'},
					listTypeDefinitionId: 0,
					name: 'customLongText',
					required: false,
					system: false,
					type: 'Clob',
				},
				{
					DBType: 'BigDecimal',
					businessType: 'PrecisionDecimal',
					externalReferenceCode: 'customPrecisionDecimal',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: '',
					label: {en_US: 'customPrecisionDecimal'},
					listTypeDefinitionId: 0,
					name: 'customPrecisionDecimal',
					required: false,
					system: false,
					type: 'BigDecimal',
				},
				{
					DBType: 'String',
					businessType: 'Picklist',
					externalReferenceCode: 'customPicklist',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'customPicklist',
					},
					listTypeDefinitionExternalReferenceCode:
						'customPicklistERC',
					name: 'customPicklist',
					required: false,
					state: false,
				},
				{
					DBType: 'Long',
					businessType: 'Attachment',
					indexed: true,
					indexedAsKeyword: false,
					label: {
						en_US: 'customAttachment',
					},
					name: 'customAttachment',
					objectFieldSettings: [
						{
							name: 'acceptedFileExtensions',
							value: 'jpeg, jpg, pdf, png',
						} as any,
						{
							name: 'fileSource',
							value: 'documentsAndMedia',
						} as any,
						{
							name: 'maximumFileSize',
							value: '100',
						} as any,
					],
					required: false,
					type: 'Long',
				},
			],
			pluralLabel: {
				en_US: 'stocks',
			},
			portlet: true,
			scope: 'company',
			status: {
				code: 0,
			},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			customAttachment: {
				fileBase64:
					'iVBORw0KGgoAAAANSUhEUgAAAD0AAAAXCAIAAAA3N9DuAAAAA3NCSVQICAjb4U/gAAAAEHRFWHRTb2Z0d2FyZQBTaHV0dGVyY4LQCQAABX9JREFUWMOVWFuS3EgOA8BUlXpifuYee4u9/3nWdonEfDAzS91+RKw+HOooKUmCAEiZ//3nP7ZJ4jeX7XR+v15VBizSgO2Q+iarAIoMMcv9FgkbBAyMoI0s7yD9EwkDNiJYZRshAihb69Eqg+igJLheV7k66XKVqxPtd668Ml8kg3GOI0QDaZRB8qrKLBtlhEjilSXO2B24k77SWRbRvxIQYVjiDGV4HbLfLTurIkiybAAE+qiyh6jOUlRnTLI7MGIs8Bgc58C36+XGwBhS2SGGomEWmeURFJHlhr9DShB5pUmTrLKkKneRIEIsG4bIxluk55M03ijMbAFU1c7vfpOZmyrllMZfj49DDFFilm280k2ARqtrLlti2VfWBK9w5aytyiJg9yEkYPRpNsiJ9ysL8xxwVtcs8JUWAElfON0ZR8QuQ5z3j3Gex5PElfXKJPDKjFVGh5EaRhxDG6QmdFcIshbBslxLFTN6OctDnKcRV7rFAGAEI6gvisxKAArh95crz/E8H3FEADgivv3IbnRnUGUDIb6yMn2lR9ALrbJhj+BOdyfUD4zgFGhZxMYbt+q0VdgFhALAEu6vr4ijKs/xHEECIzhCTfFGtNEt4zyi/2ySvH1GrCUAACGGeGWrhd9fWSurMmxzNSHTACo9GyryXQC6AP1siLsnZJD6OD4eQ53T5mWVSXT3s/y6agRJRNx8xsDUH0hg8afsso8Rn6AlSYok0ZIAIc+y3g8aBpDVnlir7uKqLRSc9eIxzueYTduV2t7AP4/49iNtvK7qVofocsu3jCuduTGZNkoR7AtdTKO+K1HbwB3O+8Xlkv0Mya6EoO02osc4/36em7sSSdrdQ2R5hNq5bVSrkOBS6tD0uJ5BTYw2l+m5mHNHYmdvW+0/d1f55ey8WeRE13C/UlVS/PV4PoeaBp3QHpDdm86AgFaik4qk4a6n/ZHLOwiQ7ZPommcGPXS6Hz1rCKBe+pS6vyi1n3oPLAmAGOfx8YiYLxiA28WwPDHEgv3WnNuwuyEjKPEqw+b0ykYeLu9zRFRZk453auuoT5zh/uVP5mjbPsbjHNEairZxslqFbR3kPuhtmkY1hYwRcsO7RHyne/fqjXfd3PBO9DtnWK/fyaCqloo44nlE9LwkcGW1XZI0sCcMyRFqF+KyvKZKO49tENIcMS6X3fvJMhnM/Ysk8/vPDti2aB2/ZL9dWxvdlqHjHM8h7r2KxFX+VLMXCTits827DdQAyUz3iWWDcwtoAxAA4p0o49zrivJ/70iebvgHz7nbDqnz+BihKtjIdFZdWe8dkGvxMLD/bZdsmqwFBvd6PcmjBql5Uq50eU1NHX8DDomLlEOB/+c6x3kOHYMGhjRC0wRbW0trIf7IypwDq6dYdeprLRHZm8n0KIF7Tx+KoAj2lnJVAr3Ru9ybHT+p9feV0NeykXN0yB4oBBaPq3xllZ3pI9QEaOPvydKCjlVeD/krTUCe697sdboM3ylu1Oa089tbvr0nVeJXVmOOfX+M8zxOco5MrY1A0wHfHttxehXZnrNJomDvFK8srR2ogkpXCz+rdurBaLcu23p82VUIhiKoPxNG0DnOj0d01LTJxeP+7tLien9kiHMuqpcldltG0Pb5CO0RaDT+L4G9nzRtyiYIWGtO3SVoOKvSRXDb7c9jyzDJ8/h4jngewcasU19LIoCmafvCVuf+eCNxpY+hKy3DdpGCC2Dx0V0biqtyB+51oNuy8rm2LbK+++bsWQnnZyFMLzri+RzPkHr1bZb3l47IH9ccJFWG3Z8dNvZe1VwfncS0IApoRZfITjp9I7dNMF2ihsKMTRjrKdLvOSDwq15FNQSiPo5nEPu7rtlse4i5uFHzvwB4rc60b/aU/Rc6sWizbSKbGQAAAABJRU5ErkJggg==',
				name: 'test.png',
			},
			customBoolean: true,
			customLongText: 'This is a custom LongText field',
			customPicklist: {key: 'distance1'},
			customPrecisionDecimal: 12.55,
			name: 'NameValue',
		},
		'c/stocks'
	);

	apiHelpers.data.push({
		id: objectEntry.customAttachment.id,
		type: 'document',
	});

	expect(
		JSON.parse(
			await dataMigrationCenterPage.exportFile(
				'JSON',
				'Stock (v1.0 - Liferay Object REST)',
				[
					'creator',
					'customAttachment',
					'customPicklist',
					'customBoolean',
					'customPrecisionDecimal',
					'customLongText',
					'name',
				]
			)
		)
	).toEqual([
		{
			creator: {
				additionalName: '',
				contentType: 'UserAccount',
				externalReferenceCode: expect.any(String),
				familyName: 'Test',
				givenName: 'Test',
				id: expect.any(Number),
				name: 'Test Test',
			},
			customAttachment: {
				alternativeText: expect.any(String),
				externalReferenceCode: expect.any(String),
				id: expect.any(Number),
				link: {
					href: expect.any(String),
					label: expect.any(String),
				},
				mimeType: expect.any(String),
				name: expect.any(String),
				scope: {
					externalReferenceCode: expect.any(String),
					type: expect.any(String),
				},
			},
			customBoolean: true,
			customLongText: 'This is a custom LongText field',
			customPicklist: {
				key: 'distance1',
				name: 'distance1',
			},
			customPrecisionDecimal: 12.55,
			name: 'NameValue',
		},
	]);
});

test('can export as JSONL with excluded fields', async ({
	apiHelpers,
	dataMigrationCenterPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			stockObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(stockObjectEntry, 'c/stocks');

	expect(
		await dataMigrationCenterPage.exportFile(
			'JSONL',
			'Stock (v1.0 - Liferay Object REST)',
			['name']
		)
	).toBe('{"name":"Stock Entry"}\n');
});

[
	{format: 'JSON', parse: JSON.parse},
	{format: 'JSONL', parse: parseJSONLToJSON},
].forEach(({format, parse}) => {
	test(`can see actions node in the downloaded ${format} file`, async ({
		apiHelpers,
		dataMigrationCenterPage,
	}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition(
				stockObjectDefinition
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			stockObjectEntry,
			'c/stocks'
		);

		const exportedContent = await dataMigrationCenterPage.exportFile(
			format,
			'Stock (v1.0 - Liferay Object REST)'
		);

		const parsed = parse(exportedContent);

		expect(
			parsed.find((item: any) => item.name === 'Stock Entry').actions
		).toEqual({
			delete: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'DELETE',
			},
			get: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'GET',
			},
			permissions: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'GET',
			},
			replace: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'PUT',
			},
			update: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'PATCH',
			},
			versions: {
				href: expect.stringContaining('/o/c/stocks/'),
				method: 'GET',
			},
		});
	});
});

test('can see correct custom object name in dropdown', async ({
	apiHelpers,
	dataMigrationCenterPage,
}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition(
			stockObjectDefinition
		);

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	await apiHelpers.objectEntry.postObjectEntry(stockObjectEntry, 'c/stocks');

	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToExportFile();

	expect(
		await dataMigrationCenterPage.page
			.getByLabel('Entity Type')
			.textContent()
	).toContain('Stock (v1.0 - Liferay Object REST)');
});

test('can see ObjectDefinition entity type in dropdown', async ({
	dataMigrationCenterPage,
}) => {
	await dataMigrationCenterPage.goto();
	await dataMigrationCenterPage.goToExportFile();
	await dataMigrationCenterPage.exportFileFormatSelector.selectOption('JSON');

	expect(
		await dataMigrationCenterPage.page
			.getByLabel('Entity Type')
			.textContent()
	).toContain('ObjectDefinition (v1.0 - Liferay Object Admin REST)');
});
