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

describe('StructureService.createStructure', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('creates the structure in a single request', async () => {
		const postFormDataSpy = jest
			.spyOn(ApiHelper, 'postFormData')
			.mockResolvedValue({data: {id: 1}, error: null});

		const result = await StructureService.createStructure(PROPS);

		expect(result).toEqual({data: {id: 1}, error: null});
		expect(postFormDataSpy).toHaveBeenCalledTimes(1);

		const [formData, url] = postFormDataSpy.mock.calls[0];

		expect(formData.get('deletedGroupERCs')).toBe('');
		expect(formData.get('deletedObjectRelationships')).toBe('[]');
		expect(url).toMatch(/\/cms\/update-structure$/);
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
		['ObjectDefinitionFriendlyURLSeparatorException', 'slug-in-use'],
		[
			'ObjectDefinitionNameException.MustBeLessThan41Characters',
			'unexpected',
		],
		['ObjectDefinitionNameException.MustNotBeDuplicate', 'name-in-use'],
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
