/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import StructureService from '../../../../src/main/resources/META-INF/resources/js/common/services/StructureService';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildGroupObjectDefinitions',
	() => ({
		__esModule: true,
		default: () => [
			{externalReferenceCode: 'group-1'},
			{externalReferenceCode: 'group-2'},
		],
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition',
	() => ({
		__esModule: true,
		default: () => ({externalReferenceCode: 'structure'}),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectRelationships',
	() => ({
		__esModule: true,
		default: () => [{objectDefinitionExternalReferenceCode1: 'structure'}],
	})
);

const PROPS = {
	children: new Map(),
	erc: 'structure',
	label: {en_US: 'Structure'},
	name: 'Structure',
	publishedChildren: new Set<any>(),
	settings: {},
	slug: 'structure',
	spaces: 'all' as const,
	status: 'draft' as const,
	titleFieldName: 'title',
	workflows: {},
};

function getDeletedIds(postFormDataSpy: jest.SpyInstance) {
	return postFormDataSpy.mock.calls.map(([formData]) =>
		Number(formData.get('objectDefinitionId'))
	);
}

describe('StructureService.createStructure', () => {
	let postFormDataSpy: jest.SpyInstance;

	beforeEach(() => {
		postFormDataSpy = jest
			.spyOn(ApiHelper, 'postFormData')
			.mockResolvedValue({data: {}, error: null});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('deletes the repeatable groups it created when a later group is rejected', async () => {
		jest.spyOn(ApiHelper, 'put')
			.mockResolvedValueOnce({data: {id: 1}, error: null})
			.mockResolvedValueOnce({
				data: null,
				error: 'error',
				type: 'ObjectDefinitionNameException.MustNotBeDuplicate',
			});

		const result = await StructureService.createStructure(PROPS);

		expect(result).toEqual({data: null, error: 'name-in-use'});
		expect(getDeletedIds(postFormDataSpy)).toEqual([1]);
	});

	it('deletes the repeatable groups it created when the structure is rejected', async () => {
		jest.spyOn(ApiHelper, 'put')
			.mockResolvedValueOnce({data: {id: 1}, error: null})
			.mockResolvedValueOnce({data: {id: 2}, error: null});
		jest.spyOn(ApiHelper, 'post').mockResolvedValue({
			data: null,
			error: 'error',
			type: 'ObjectDefinitionFriendlyURLSeparatorException',
		});

		const result = await StructureService.createStructure(PROPS);

		expect(result.error).toBe('slug-in-use');
		expect(getDeletedIds(postFormDataSpy)).toEqual([1, 2]);
	});

	it('deletes the structure and its repeatable groups when a relationship is rejected', async () => {
		jest.spyOn(ApiHelper, 'put')
			.mockResolvedValueOnce({data: {id: 1}, error: null})
			.mockResolvedValueOnce({data: {id: 2}, error: null});
		jest.spyOn(ApiHelper, 'post')
			.mockResolvedValueOnce({data: {id: 3}, error: null})
			.mockResolvedValueOnce({
				data: null,
				error: 'error',
				type: 'ObjectRelationshipNameException',
			});

		const result = await StructureService.createStructure(PROPS);

		expect(result).toEqual({data: null, error: 'unexpected'});
		expect(getDeletedIds(postFormDataSpy)).toEqual([3, 1, 2]);
	});

	it('deletes nothing when the structure is created', async () => {
		jest.spyOn(ApiHelper, 'put')
			.mockResolvedValueOnce({data: {id: 1}, error: null})
			.mockResolvedValueOnce({data: {id: 2}, error: null});
		jest.spyOn(ApiHelper, 'post')
			.mockResolvedValueOnce({data: {id: 3}, error: null})
			.mockResolvedValueOnce({data: {}, error: null});

		const result = await StructureService.createStructure(PROPS);

		expect(result).toEqual({data: {id: 3}, error: null});
		expect(postFormDataSpy).not.toHaveBeenCalled();
	});
});

describe('StructureService.updateStructure', () => {
	const history = {
		deletedChildren: [],
		deletedGroupERCs: [],
		deletedRelationships: [],
		modifiedNames: new Set<any>(),
		modifiedSlugs: new Set<any>(),
	};

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it.each([
		[
			'DuplicateObjectDefinitionExternalReferenceCodeException',
			'erc-in-use',
		],
		['ObjectDefinitionNameException.MustNotBeDuplicate', 'name-in-use'],
		[
			'ObjectDefinitionNameException.MustBeLessThan41Characters',
			'unexpected',
		],
		['PrincipalException.MustHavePermission', 'permission'],
	])('classifies %s as %s', async (type, error) => {
		jest.spyOn(ApiHelper, 'postFormData').mockResolvedValue({
			data: null,
			error: 'message',
			type,
		});

		const result = await StructureService.updateStructure({
			...PROPS,
			history,
			id: 1,
		});

		expect(result.error).toBe(error);
	});
});
