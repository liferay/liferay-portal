/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ProjectLinkService from '../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';
import {mockFetch} from '../../__mocks__/frontend-js-web';

const ASSET_IDENTITY = {
	entryClassName: 'com.example.Content',
	entryExternalReferenceCode: 'ASSET-1',
	entryGroupExternalReferenceCode: 'SPACE-1',
};

function fetchCall(index: number): [string, RequestInit] {
	const calls = mockFetch.mock.calls as unknown as [string, RequestInit][];

	return calls[index];
}

function mockSearchResponse(items: unknown[], lastPage: number = 1) {
	return {
		json: async () => ({items, lastPage}),
		ok: true,
		status: 200,
	} as Response;
}

function relationshipItem({
	classExternalReferenceCode = 'ASSET-1',
	className = 'com.example.Content',
	cmpProjectObjectEntryId = 1,
	groupExternalReferenceCode = 'SPACE-1',
	id = 11,
}: {
	classExternalReferenceCode?: string;
	className?: string;
	cmpProjectObjectEntryId?: number;
	groupExternalReferenceCode?: string;
	id?: number;
}) {
	return {
		embedded: {
			classExternalReferenceCode,
			className,
			groupExternalReferenceCode,
			id,
			r_cmpProjectToCMPProjectLinks_c_cmpProjectId:
				cmpProjectObjectEntryId,
		},
	};
}

describe('ProjectLinkService', () => {
	afterEach(() => {
		mockFetch.mockReset();
	});

	it('fetches every search page when listing projects', async () => {
		mockFetch
			.mockResolvedValueOnce(
				mockSearchResponse(
					[{embedded: {id: 1, scopeKey: 'P1', title: 'One'}}],
					2
				)
			)
			.mockResolvedValueOnce(
				mockSearchResponse(
					[{embedded: {id: 2, scopeKey: 'P2', title: 'Two'}}],
					2
				)
			);

		const {data, error} = await ProjectLinkService.getProjects({
			cmpProjectObjectDefinitionId: 42,
		});

		expect(error).toBeNull();
		expect(data).toHaveLength(2);

		expect(mockFetch).toHaveBeenCalledTimes(2);
		expect(fetchCall(0)[0]).toContain(
			encodeURIComponent('objectDefinitionId eq 42')
		);
		expect(fetchCall(0)[0]).toContain('page=1');
		expect(fetchCall(1)[0]).toContain('page=2');
	});

	it('links a project in the scope of its depot', async () => {
		mockFetch.mockResolvedValueOnce({
			json: async () => ({id: 99}),
			ok: true,
			status: 200,
		} as Response);

		const {data} = await ProjectLinkService.linkProject({
			...ASSET_IDENTITY,
			project: {id: 7, scopeKey: 'PROJECT-DEPOT', title: 'Project'},
		});

		expect(data).toEqual({id: 99});

		const [url, options] = fetchCall(0);

		expect(url).toBe('/o/cmp/project-links/scopes/PROJECT-DEPOT');
		expect(options.method).toBe('POST');
		expect(JSON.parse(options.body as string)).toEqual({
			classExternalReferenceCode: 'ASSET-1',
			className: 'com.example.Content',
			groupExternalReferenceCode: 'SPACE-1',
			r_cmpProjectToCMPProjectLinks_c_cmpProjectId: 7,
		});
	});

	it('matches relationship entries against the asset identity', async () => {
		mockFetch.mockResolvedValueOnce(
			mockSearchResponse([
				relationshipItem({cmpProjectObjectEntryId: 1, id: 11}),
				relationshipItem({
					classExternalReferenceCode: 'OTHER-ASSET',
					cmpProjectObjectEntryId: 2,
					id: 12,
				}),
				relationshipItem({
					cmpProjectObjectEntryId: 3,
					groupExternalReferenceCode: 'OTHER-SPACE',
					id: 13,
				}),
				relationshipItem({
					className: 'com.example.Other',
					cmpProjectObjectEntryId: 4,
					id: 14,
				}),
			])
		);

		const {data} = await ProjectLinkService.getProjectLinks({
			...ASSET_IDENTITY,
			cmpProjectLinkObjectDefinitionId: 42,
		});

		expect(data).toEqual([{cmpProjectObjectEntryId: 1, id: 11}]);
	});

	it('returns empty results when the object definition id is missing', async () => {
		const linksResult = await ProjectLinkService.getProjectLinks({
			...ASSET_IDENTITY,
			cmpProjectLinkObjectDefinitionId: null,
		});
		const projectsResult = await ProjectLinkService.getProjects({
			cmpProjectObjectDefinitionId: null,
		});

		expect(linksResult.data).toEqual([]);
		expect(projectsResult.data).toEqual([]);

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('skips the className check when the asset entry class name is unknown', async () => {
		mockFetch.mockResolvedValueOnce(
			mockSearchResponse([
				relationshipItem({cmpProjectObjectEntryId: 1, id: 11}),
				relationshipItem({
					className: 'com.example.Other',
					cmpProjectObjectEntryId: 2,
					id: 12,
				}),
			])
		);

		const {data} = await ProjectLinkService.getProjectLinks({
			cmpProjectLinkObjectDefinitionId: 42,
			entryExternalReferenceCode: 'ASSET-1',
			entryGroupExternalReferenceCode: 'SPACE-1',
		});

		expect(data).toEqual([
			{cmpProjectObjectEntryId: 1, id: 11},
			{cmpProjectObjectEntryId: 2, id: 12},
		]);
	});

	it('unlinks a project by its relationship entry id', async () => {
		mockFetch.mockResolvedValueOnce({
			json: async () => null,
			ok: true,
			status: 204,
		} as Response);

		const {error} = await ProjectLinkService.unlinkProject({linkId: 12});

		expect(error).toBeNull();

		const [url, options] = fetchCall(0);

		expect(url).toBe('/o/cmp/project-links/12');
		expect(options.method).toBe('DELETE');
	});
});
