/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import TagService from '../../../../src/main/resources/META-INF/resources/js/common/services/TagService';

describe('TagService.createTag', () => {
	const cmsGroupId = 20121;
	const assetLibraryId = 37540;

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('PATCHes the existing tag when the same-case tag already exists in an asset library', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: [{name: 'hola'}, {name: 'Hola'}]},
			error: null,
		} as any);

		const patchSpy = jest
			.spyOn(ApiHelper, 'patch')
			.mockResolvedValue({data: {name: 'Hola'}} as any);
		const postSpy = jest.spyOn(ApiHelper, 'post');

		await TagService.createTag({
			assetLibraryId,
			cmsGroupId,
			name: 'Hola',
		});

		expect(postSpy).not.toHaveBeenCalled();
		expect(patchSpy).toHaveBeenCalledWith(
			{
				assetLibraries: [{id: assetLibraryId}],
				name: 'Hola',
			},
			`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`
		);
	});

	it('POSTs a new tag when no case-insensitive match exists', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		} as any);

		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {name: 'Hola'}} as any);

		await TagService.createTag({
			assetLibraryId,
			cmsGroupId,
			name: 'Hola',
		});

		expect(postSpy).toHaveBeenCalledWith(
			`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`,
			{
				assetLibraries: [{id: assetLibraryId}],
				name: 'Hola',
			}
		);
	});

	it('POSTs a new tag when only a different-case tag exists', async () => {

		// The OData filter `name eq` is case-insensitive (it maps to the
		// lowercased sortable index field), so a search for 'Hola' returns
		// 'hola'. The service must still POST 'Hola' as a new tag, not
		// PATCH the existing 'hola'.

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: [{name: 'hola'}]},
			error: null,
		} as any);

		const patchSpy = jest.spyOn(ApiHelper, 'patch');
		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {name: 'Hola'}} as any);

		await TagService.createTag({
			assetLibraryId,
			cmsGroupId,
			name: 'Hola',
		});

		expect(patchSpy).not.toHaveBeenCalled();
		expect(postSpy).toHaveBeenCalledWith(
			`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`,
			{
				assetLibraries: [{id: assetLibraryId}],
				name: 'Hola',
			}
		);
	});

	it('POSTs to the CMS group scope when no asset library is given', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		} as any);

		const postSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {name: 'Hola'}} as any);

		await TagService.createTag({
			assetLibraryId: null,
			cmsGroupId,
			name: 'Hola',
		});

		expect(postSpy).toHaveBeenCalledWith(
			`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`,
			{name: 'Hola'}
		);
	});

	it('returns the existing same-case tag without writing when no asset library is given', async () => {
		const existing = {name: 'Hola'};

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: [{name: 'hola'}, existing]},
			error: null,
		} as any);

		const patchSpy = jest.spyOn(ApiHelper, 'patch');
		const postSpy = jest.spyOn(ApiHelper, 'post');

		const result = await TagService.createTag({
			assetLibraryId: null,
			cmsGroupId,
			name: 'Hola',
		});

		expect(patchSpy).not.toHaveBeenCalled();
		expect(postSpy).not.toHaveBeenCalled();
		expect(result).toEqual({data: existing, error: null, status: null});
	});
});
