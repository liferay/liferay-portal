/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ObjectEntryLinkService, {
	toLinkContext,
	toLinkedAsset,
} from '../../../../src/main/resources/META-INF/resources/js/common/services/ObjectEntryLinkService';
import {mockFetch} from '../../__mocks__/frontend-js-web';

const CONTEXT = {
	objectEntryId: '55',
	relationshipObjectFieldName: 'r_cmpTaskToCMPTaskLinks_c_cmpTaskId',
	restContextPath: '/o/cmp/task-links',
	scopeGroupId: '1001',
};

const LINKED_ASSET = {
	classExternalReferenceCode: 'ASSET-1',
	className: 'com.example.Content',
	groupExternalReferenceCode: 'SPACE-1',
};

function fetchCall(index: number): [string, RequestInit] {
	const calls = mockFetch.mock.calls as unknown as [string, RequestInit][];

	return calls[index];
}

function mockJSONResponse(data: unknown) {
	return {
		json: async () => data,
		ok: true,
		status: 200,
	} as Response;
}

describe('ObjectEntryLinkService', () => {
	afterEach(() => {
		mockFetch.mockReset();
	});

	describe('linkAsset', () => {
		it('creates the link row in the scope of the object entry', async () => {
			mockFetch.mockResolvedValueOnce(mockJSONResponse({id: 99}));

			const {data} = await ObjectEntryLinkService.linkAsset({
				context: CONTEXT,
				linkedAsset: LINKED_ASSET,
			});

			expect(data).toEqual({id: 99});

			const [url, options] = fetchCall(0);

			expect(url).toBe('/o/cmp/task-links/scopes/1001');
			expect(options.method).toBe('POST');
			expect(JSON.parse(options.body as string)).toEqual({
				...LINKED_ASSET,
				r_cmpTaskToCMPTaskLinks_c_cmpTaskId: 55,
			});
		});

		it('surfaces the error when the link row cannot be created', async () => {
			mockFetch.mockResolvedValueOnce({
				json: async () => ({title: 'error-message'}),
				ok: false,
				status: 400,
			} as Response);

			const {data, error} = await ObjectEntryLinkService.linkAsset({
				context: CONTEXT,
				linkedAsset: LINKED_ASSET,
			});

			expect(data).toBeNull();
			expect(error).toBe('error-message');
		});
	});

	describe('unlinkAsset', () => {
		it('resolves the link row by its pair and then deletes it', async () => {
			mockFetch
				.mockResolvedValueOnce(mockJSONResponse({items: [{id: 12}]}))
				.mockResolvedValueOnce({ok: true, status: 204} as Response);

			const {error} = await ObjectEntryLinkService.unlinkAsset({
				context: CONTEXT,
				linkedAsset: LINKED_ASSET,
			});

			expect(error).toBeNull();

			const [lookupURL] = fetchCall(0);

			expect(lookupURL).toContain(
				'/o/cmp/task-links/scopes/1001?filter='
			);
			expect(decodeURIComponent(lookupURL)).toContain(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId eq '55'"
			);
			expect(decodeURIComponent(lookupURL)).toContain(
				"classExternalReferenceCode eq 'ASSET-1'"
			);

			const [deleteURL, deleteOptions] = fetchCall(1);

			expect(deleteURL).toBe('/o/cmp/task-links/12');
			expect(deleteOptions.method).toBe('DELETE');
		});

		it('escapes an apostrophe so the filter stays parseable', async () => {
			mockFetch.mockResolvedValueOnce(mockJSONResponse({items: []}));

			await ObjectEntryLinkService.unlinkAsset({
				context: CONTEXT,
				linkedAsset: {
					...LINKED_ASSET,
					classExternalReferenceCode: "O'BRIEN",
				},
			});

			expect(decodeURIComponent(fetchCall(0)[0])).toContain(
				"classExternalReferenceCode eq 'O''BRIEN'"
			);
		});

		it('reports an error and deletes nothing when no link row matches', async () => {
			mockFetch.mockResolvedValueOnce(mockJSONResponse({items: []}));

			const {data, error} = await ObjectEntryLinkService.unlinkAsset({
				context: CONTEXT,
				linkedAsset: LINKED_ASSET,
			});

			expect(data).toBeNull();
			expect(error).toBeTruthy();

			expect(mockFetch).toHaveBeenCalledTimes(1);
		});

		it('surfaces the lookup error without attempting a delete', async () => {
			mockFetch.mockResolvedValueOnce({
				json: async () => ({title: 'error-message'}),
				ok: false,
				status: 400,
			} as Response);

			const {error} = await ObjectEntryLinkService.unlinkAsset({
				context: CONTEXT,
				linkedAsset: LINKED_ASSET,
			});

			expect(error).toBe('error-message');

			expect(mockFetch).toHaveBeenCalledTimes(1);
		});
	});

	describe('toLinkContext', () => {
		it('returns the context when every field is present', () => {
			expect(toLinkContext(CONTEXT)).toEqual(CONTEXT);
		});

		it('returns null when a field is missing', () => {
			expect(
				toLinkContext({...CONTEXT, restContextPath: undefined})
			).toBeNull();
			expect(toLinkContext({})).toBeNull();
		});
	});

	describe('toLinkedAsset', () => {
		it('narrows a content list item to the stored soft reference', () => {
			expect(
				toLinkedAsset({
					embedded: {
						externalReferenceCode: 'ASSET-1',
						systemProperties: {
							scope: {externalReferenceCode: 'SPACE-1'},
						},
					},
					entryClassName: 'com.example.Content',
				})
			).toEqual(LINKED_ASSET);
		});

		it('falls back to an empty group when the scope is absent', () => {
			expect(
				toLinkedAsset({
					embedded: {externalReferenceCode: 'ASSET-1'},
					entryClassName: 'com.example.Content',
				})
			).toEqual({...LINKED_ASSET, groupExternalReferenceCode: ''});
		});
	});
});
