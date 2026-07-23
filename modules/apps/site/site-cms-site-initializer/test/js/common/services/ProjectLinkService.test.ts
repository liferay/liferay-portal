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
	groupExternalReferenceCode = 'SPACE-1',
	id = 11,
	projectId = 1,
}: {
	classExternalReferenceCode?: string;
	className?: string;
	groupExternalReferenceCode?: string;
	id?: number;
	projectId?: number;
}) {
	return {
		embedded: {
			classExternalReferenceCode,
			className,
			groupExternalReferenceCode,
			id,
			r_cmpProjectToCMPProjectLinks_c_cmpProjectId: projectId,
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

	it('groups the asset tasks by project id', async () => {
		mockFetch.mockResolvedValueOnce(
			mockSearchResponse([
				{
					embedded: {
						id: 101,
						r_cmpProjectToCMPTasks_c_cmpProjectId: 1,
						title: 'Task A',
					},
				},
				{
					embedded: {
						id: 102,
						r_cmpProjectToCMPTasks_c_cmpProjectId: 1,
						title: 'Task B',
					},
				},
				{
					embedded: {
						id: 201,
						r_cmpProjectToCMPTasks_c_cmpProjectId: 2,
						title: 'Task C',
					},
				},
				{embedded: {id: 301, title: 'Orphan'}},
			])
		);

		const {data} = await ProjectLinkService.getLinkedTasks({
			assetKeywords: ["L_CMP_TASK_O'BRIEN", 'unrelated-tag'],
			cmpTaskObjectDefinitionId: 42,
		});

		expect(data).toEqual({
			1: [
				{id: 101, title: 'Task A'},
				{id: 102, title: 'Task B'},
			],
			2: [{id: 201, title: 'Task C'}],
		});

		const [url] = fetchCall(0);

		expect(url).toContain("O''BRIEN");
		expect(url).not.toContain('unrelated-tag');
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
				relationshipItem({id: 11, projectId: 1}),
				relationshipItem({
					classExternalReferenceCode: 'OTHER-ASSET',
					id: 12,
					projectId: 2,
				}),
				relationshipItem({
					groupExternalReferenceCode: 'OTHER-SPACE',
					id: 13,
					projectId: 3,
				}),
				relationshipItem({
					className: 'com.example.Other',
					id: 14,
					projectId: 4,
				}),
			])
		);

		const {data} = await ProjectLinkService.getProjectLinks({
			...ASSET_IDENTITY,
			cmpProjectLinkObjectDefinitionId: 42,
		});

		expect(data).toEqual([{id: 11, projectId: 1}]);
	});

	it('returns empty results when the object definition id is missing', async () => {
		const linksResult = await ProjectLinkService.getProjectLinks({
			...ASSET_IDENTITY,
			cmpProjectLinkObjectDefinitionId: null,
		});
		const projectsResult = await ProjectLinkService.getProjects({
			cmpProjectObjectDefinitionId: null,
		});
		const tasksResult = await ProjectLinkService.getLinkedTasks({
			assetKeywords: ['L_CMP_TASK_X'],
			cmpTaskObjectDefinitionId: null,
		});

		expect(linksResult.data).toEqual([]);
		expect(projectsResult.data).toEqual([]);
		expect(tasksResult.data).toEqual({});

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('skips the className check when the asset entry class name is unknown', async () => {
		mockFetch.mockResolvedValueOnce(
			mockSearchResponse([
				relationshipItem({id: 11, projectId: 1}),
				relationshipItem({
					className: 'com.example.Other',
					id: 12,
					projectId: 2,
				}),
			])
		);

		const {data} = await ProjectLinkService.getProjectLinks({
			cmpProjectLinkObjectDefinitionId: 42,
			entryExternalReferenceCode: 'ASSET-1',
			entryGroupExternalReferenceCode: 'SPACE-1',
		});

		expect(data).toEqual([
			{id: 11, projectId: 1},
			{id: 12, projectId: 2},
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
