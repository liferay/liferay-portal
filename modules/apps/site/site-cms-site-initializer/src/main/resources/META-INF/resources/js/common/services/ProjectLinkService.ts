/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

/**
 * Identifies the asset being linked. `className` and `externalReferenceCode`
 * plus `groupExternalReferenceCode` are the fields the
 * CMPProjectLink object stores to point back at the asset.
 */
type AssetIdentity = {
	entryClassName?: string;
	entryExternalReferenceCode?: string;
	entryGroupExternalReferenceCode?: string;
};

export type CMPProject = {
	dueDate?: string;
	id: number;
	linkId?: number;
	scopeKey?: string;
	state?: {
		key: string;
		name: string;
	};
	title: string;
};

export type CMPTask = {
	id: number;
	title: string;
};

/**
 * A CMPProjectLink entry reduced to what the panels need: the
 * entry id (required to unlink) and the linked project id.
 */
export type ProjectLink = {
	id?: number;
	projectId: number;
};

type ProjectLinkSearchItem = {
	embedded: {
		classExternalReferenceCode: string;
		className: string;
		groupExternalReferenceCode: string;
		id: number;
		r_cmpProjectToCMPProjectLinks_c_cmpProjectId: number;
	};
};

type ProjectSearchItem = {
	embedded: {
		dueDate?: string;
		id: number;
		scopeKey: string;
		state?: {key: string; name: string};
		title: string;
	};
};

type TaskSearchItem = {
	embedded: {
		id: number;
		r_cmpProjectToCMPTasks_c_cmpProjectId?: number;
		title: string;
	};
};

const PROJECT_LINKS_URL = '/o/cmp/project-links';

const TASK_TAG_PREFIX = 'L_CMP_TASK';

function buildSearchURL(
	objectDefinitionId: number,
	page: number,
	filter?: string
): string {
	let filterString = `objectDefinitionId eq ${objectDefinitionId}`;

	if (filter) {
		filterString = `${filterString} and ${filter}`;
	}

	return `/o/search/v1.0/search?emptySearch=true&nestedFields=embedded&page=${page}&pageSize=500&filter=${encodeURIComponent(
		filterString
	)}`;
}

/**
 * /o/search clamps pageSize to 500, so page through every result rather than
 * reading only the first page.
 */
async function fetchAllSearchItems<T>({
	filter,
	objectDefinitionId,
	signal,
}: {
	filter?: string;
	objectDefinitionId: number;
	signal?: AbortSignal;
}): Promise<RequestResult<T[]>> {
	const items: T[] = [];

	let lastPage = 1;
	let page = 1;

	while (page <= lastPage) {
		const {data, error, status, type} = await ApiHelper.get<{
			items: T[];
			lastPage: number;
		}>(buildSearchURL(objectDefinitionId, page, filter), signal);

		if (error !== null) {
			return {data: null, error, status, type};
		}

		items.push(...data.items);

		lastPage = data.lastPage;
		page += 1;
	}

	return {data: items, error: null};
}

/**
 * Lists the asset's associated tasks grouped by project id, resolved from the
 * asset's keywords (see TASK_TAG_PREFIX) in a single search. A project id
 * absent from the result simply has no associated tasks.
 */
async function getLinkedTasks({
	assetKeywords,
	cmpTaskObjectDefinitionId,
	signal,
}: {
	assetKeywords?: string[];
	cmpTaskObjectDefinitionId?: number | null;
	signal?: AbortSignal;
}): Promise<RequestResult<{[projectId: number]: CMPTask[]}>> {
	const taskTags = (assetKeywords ?? []).filter((keyword) =>
		keyword.startsWith(TASK_TAG_PREFIX)
	);

	if (!cmpTaskObjectDefinitionId || !taskTags.length) {
		return {data: {}, error: null};
	}

	const {data, error, status, type} =
		await fetchAllSearchItems<TaskSearchItem>({
			filter: `keywords/any(k:k in (${taskTags
				.map((tag) => `'${tag.replace(/'/g, "''")}'`)
				.join(',')}))`,
			objectDefinitionId: cmpTaskObjectDefinitionId,
			signal,
		});

	if (error !== null) {
		return {data: null, error, status, type};
	}

	const tasksByProjectId: {[projectId: number]: CMPTask[]} = {};

	data.forEach(({embedded}) => {
		const projectId = embedded.r_cmpProjectToCMPTasks_c_cmpProjectId;

		if (projectId === undefined) {
			return;
		}

		if (!tasksByProjectId[projectId]) {
			tasksByProjectId[projectId] = [];
		}

		tasksByProjectId[projectId].push({
			id: embedded.id,
			title: embedded.title,
		});
	});

	return {data: tasksByProjectId, error: null};
}

/**
 * Lists the CMPProjectLink entries pointing at the given asset.
 * The `/o/search` filter does not cover object entry fields, so every entry of
 * the relationship object is fetched and matched against the asset client
 * side.
 */
async function getProjectLinks({
	cmpProjectLinkObjectDefinitionId,
	entryClassName,
	entryExternalReferenceCode,
	entryGroupExternalReferenceCode,
	signal,
}: AssetIdentity & {
	cmpProjectLinkObjectDefinitionId?: number | null;
	signal?: AbortSignal;
}): Promise<RequestResult<ProjectLink[]>> {
	if (!cmpProjectLinkObjectDefinitionId) {
		return {data: [], error: null};
	}

	const {data, error, status, type} =
		await fetchAllSearchItems<ProjectLinkSearchItem>({
			objectDefinitionId: cmpProjectLinkObjectDefinitionId,
			signal,
		});

	if (error !== null) {
		return {data: null, error, status, type};
	}

	const links: ProjectLink[] = [];

	data.forEach(({embedded}) => {
		if (
			(entryClassName && embedded.className !== entryClassName) ||
			embedded.classExternalReferenceCode !==
				entryExternalReferenceCode ||
			embedded.groupExternalReferenceCode !==
				entryGroupExternalReferenceCode
		) {
			return;
		}

		links.push({
			id: embedded.id,
			projectId: embedded.r_cmpProjectToCMPProjectLinks_c_cmpProjectId,
		});
	});

	return {data: links, error: null};
}

/**
 * Lists every CMP project the author can link to, used to populate the
 * "Search or Select a Project" picker.
 */
async function getProjects({
	cmpProjectObjectDefinitionId,
	signal,
}: {
	cmpProjectObjectDefinitionId?: number | null;
	signal?: AbortSignal;
}): Promise<RequestResult<CMPProject[]>> {
	if (!cmpProjectObjectDefinitionId) {
		return {data: [], error: null};
	}

	const {data, error, status, type} =
		await fetchAllSearchItems<ProjectSearchItem>({
			objectDefinitionId: cmpProjectObjectDefinitionId,
			signal,
		});

	if (error !== null) {
		return {data: null, error, status, type};
	}

	return {
		data: data.map(({embedded}) => ({
			dueDate: embedded.dueDate,
			id: embedded.id,
			scopeKey: embedded.scopeKey,
			state: embedded.state,
			title: embedded.title,
		})),
		error: null,
	};
}

/**
 * Links a CMP project to the asset. Auto-saved: called as soon as the author
 * picks a project. Returns the created relationship entry (its `id` is the
 * `linkId` needed to unlink later).
 */
async function linkProject({
	entryClassName,
	entryExternalReferenceCode,
	entryGroupExternalReferenceCode,
	project,
}: AssetIdentity & {
	project: CMPProject;
}): Promise<RequestResult<{id: number}>> {
	return ApiHelper.post<{id: number}>(
		`${PROJECT_LINKS_URL}/scopes/${project.scopeKey}`,
		{
			classExternalReferenceCode: entryExternalReferenceCode,
			className: entryClassName,
			groupExternalReferenceCode: entryGroupExternalReferenceCode,
			r_cmpProjectToCMPProjectLinks_c_cmpProjectId: project.id,
		}
	);
}

/**
 * Unlinks a CMP project from the asset. Auto-saved: called as soon as the
 * author removes a project. Keyed by the relationship entry id.
 */
async function unlinkProject({
	linkId,
}: {
	linkId: number;
}): Promise<RequestResult<null>> {
	return ApiHelper.delete(`${PROJECT_LINKS_URL}/${linkId}`);
}

const ProjectLinkService = {
	getLinkedTasks,
	getProjectLinks,
	getProjects,
	linkProject,
	unlinkProject,
};

export default ProjectLinkService;
