import sendRequest from 'shared/util/request';
import {buildSortString, NAME} from 'shared/util/pagination';
import {
	ENABLE_LIFECYCLE_DELETE_MOCK_DATA,
	ENABLE_LIFECYCLE_STAGES_MOCK_DATA,
} from 'shared/util/feature-flags';
import {
	applyMockDeletes,
	deleteMockLifecycle,
	getMockDefaultLifecycle,
	getMockLifecycle,
	getMockLifecycleStages,
	isMockLifecycleDeleted,
	removeMockDeletedLifecycles,
	saveMockLifecycle,
} from 'lifecycle/utils/lifecycleMock';
import {OrderByDirections} from 'shared/util/constants';

export interface ILifecycle {
	authorName?: string | null;
	defaultAccountLifecycle?: boolean;
	description?: string;
	externalReferenceCode: string;
	id: string;
	modifiedDate?: number | null;
	name?: string;
	processedDate?: number | null;
	segmentId?: string;
}

export interface IAccountLifecycleStageRule {
	filterMetadata: string | null;
	filterString: string;
	name?: string;
}

export interface ILifecycleStage {
	accountLifecycleStageRule?: IAccountLifecycleStageRule;
	description: string;
	externalReferenceCode: string;
	id: string;
	maxDuration: number | null;
	priority: number;
	stageName: string;
}

export interface ILifecycleDetail extends ILifecycle {
	stages?: ILifecycleStage[];
}

export interface ILifecyclesPage {
	items: ILifecycleDetail[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

interface IFetchLifecycles {
	channelId?: string;
	defaultAccountLifecycle?: boolean;
	groupId: string;
}

interface IFetchLifecyclesPage {
	channelId?: string;
	groupId: string;
	page: number;
	pageSize: number;
	search?: string;
}

interface IFetchLifecycle {
	groupId: string;
	lifecycleId: string;
}

const MOCK_DELETE_LIFECYCLES_PAGE_SIZE = 1000;

const requestFirstLifecyclePage = ({
	channelId,
	defaultAccountLifecycle,
	groupId,
}: IFetchLifecycles): Promise<ILifecyclesPage> =>
	sendRequest({
		data: {
			...(channelId && {channelId}),
			...(defaultAccountLifecycle && {defaultAccountLifecycle}),
			page: 1,
			pageSize: 1,
			sort: buildSortString({
				field: NAME,
				sortOrder: OrderByDirections.Ascending,
			}),
		},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle`,
	});

const requestLifecycles = async (
	params: IFetchLifecycles
): Promise<ILifecycleDetail[]> =>
	(await requestFirstLifecyclePage(params)).items;

const requestLifecyclesPage = ({
	channelId,
	groupId,
	page,
	pageSize,
	search = '',
}: IFetchLifecyclesPage): Promise<ILifecyclesPage> =>
	sendRequest({
		data: {
			...(channelId && {channelId}),
			page,
			pageSize,
			...(search.trim() && {search: search.trim()}),
			sort: buildSortString({
				field: NAME,
				sortOrder: OrderByDirections.Ascending,
			}),
		},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle`,
	});

const requestLifecycle = ({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<ILifecycleDetail> => {
	if (ENABLE_LIFECYCLE_STAGES_MOCK_DATA) {
		return getMockLifecycle();
	}

	return sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
};

export const fetchLifecycle = async (
	params: IFetchLifecycle
): Promise<ILifecycleDetail> => {
	const lifecycle = await requestLifecycle(params);

	if (!ENABLE_LIFECYCLE_DELETE_MOCK_DATA) {
		return lifecycle;
	}

	if (isMockLifecycleDeleted(lifecycle.id)) {
		throw new Error('not-found');
	}

	return applyMockDeletes([lifecycle])[0];
};

export const fetchLifecycles = async (
	params: IFetchLifecycles
): Promise<ILifecycleDetail[]> => {
	if (!ENABLE_LIFECYCLE_DELETE_MOCK_DATA) {
		return requestLifecycles(params);
	}

	const mockDefaultLifecycle = getMockDefaultLifecycle();

	if (params.defaultAccountLifecycle && mockDefaultLifecycle !== undefined) {
		return mockDefaultLifecycle ? [mockDefaultLifecycle] : [];
	}

	return applyMockDeletes(await requestLifecycles(params));
};

export const fetchLifecyclesPage = async (
	params: IFetchLifecyclesPage
): Promise<ILifecyclesPage> => {
	const page = await requestLifecyclesPage(params);

	return ENABLE_LIFECYCLE_DELETE_MOCK_DATA
		? removeMockDeletedLifecycles(page)
		: page;
};

export const setDefaultLifecycle = ({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<ILifecycleDetail> =>
	sendRequest({
		data: {defaultAccountLifecycle: true},
		method: 'PATCH',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});

export interface ILifecycleStagePayload {
	accountLifecycleStageRule: IAccountLifecycleStageRule;
	description: string;
	id?: string;
	maxDuration: number | null;
	priority: number;
	stageName: string;
}

interface ICreateLifecycle {
	channelId: string;
	description: string;
	groupId: string;
	name: string;
	stages: ILifecycleStagePayload[];
}

export async function createLifecycle({
	channelId,
	description,
	groupId,
	name,
	stages,
}: ICreateLifecycle): Promise<ILifecycleDetail> {
	if (ENABLE_LIFECYCLE_STAGES_MOCK_DATA) {
		return saveMockLifecycle({description, name, stages});
	}

	return sendRequest({
		data: {accountLifecycle: {description, name, stages}},
		method: 'POST',
		path: `contacts/${groupId}/account-lifecycle?channelId=${channelId}`,
	});
}

interface IUpdateLifecycle {
	description: string;
	groupId: string;
	lifecycleId: string;
	name: string;
	stages: ILifecycleStagePayload[];
}

export async function updateLifecycle({
	description,
	groupId,
	lifecycleId,
	name,
	stages,
}: IUpdateLifecycle): Promise<ILifecycleDetail> {
	if (ENABLE_LIFECYCLE_STAGES_MOCK_DATA) {
		return saveMockLifecycle({description, name, stages});
	}

	return sendRequest({
		data: {accountLifecycle: {description, name, stages}},
		method: 'PUT',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
}

interface IDeleteLifecycle extends IFetchLifecycle {
	channelId?: string;
}

export async function deleteLifecycle({
	channelId,
	groupId,
	lifecycleId,
}: IDeleteLifecycle): Promise<unknown> {
	if (ENABLE_LIFECYCLE_DELETE_MOCK_DATA) {
		const {items} = await requestLifecyclesPage({
			channelId,
			groupId,
			page: 1,
			pageSize: MOCK_DELETE_LIFECYCLES_PAGE_SIZE,
		});

		return deleteMockLifecycle(lifecycleId, items);
	}

	return sendRequest({
		method: 'DELETE',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
}

export interface IAccountLifecycle {
	accountId?: string;
	id: string;
}

export interface IAccountLifecycleStageStatus {
	description?: string;
	endDate?: number | null;
	id: string;
	maxDuration?: number | null;
	priority: number;
	stageType: string;
	startDate?: number | null;
}

export interface IAccountLifecycleStatus {
	accountLifecycleStageStatuses?: IAccountLifecycleStageStatus[];
	id: string;
	name: string;
}

interface IFetchOverviewMetrics {
	country?: string;
	groupId?: string;
	industry?: string;
	lifecycleId: string;
}

export async function fetchOverviewMetrics({
	country,
	groupId,
	industry,
	lifecycleId,
}: IFetchOverviewMetrics) {
	return sendRequest({
		data: {
			...(country && {country}),
			...(industry && {industry}),
		},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}/overview`,
	});
}

interface IFetchLifecycleStages {
	country?: string;
	groupId?: string;
	industry?: string;
	lifecycleId: string;
	segmentId?: string | null;
}

export async function fetchLifecycleStages({
	country,
	groupId,
	industry,
	lifecycleId,
	segmentId,
}: IFetchLifecycleStages) {
	if (ENABLE_LIFECYCLE_STAGES_MOCK_DATA) {
		return getMockLifecycleStages();
	}

	return sendRequest({
		data: {
			...(country && {country}),
			...(industry && {industry}),
			...(segmentId && {segmentId}),
		},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}/stages`,
	});
}

export async function fetchAccountLifecycles({
	channelId,
	groupId,
}: {
	channelId?: string;
	groupId: string;
}): Promise<IAccountLifecycle[]> {
	return fetchLifecycles({
		channelId,
		defaultAccountLifecycle: !!channelId,
		groupId,
	});
}
