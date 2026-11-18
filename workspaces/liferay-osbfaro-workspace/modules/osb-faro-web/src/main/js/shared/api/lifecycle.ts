import sendRequest from 'shared/util/request';
import {buildSortString, NAME} from 'shared/util/pagination';
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
	name: string;
	position: number;
}

export interface ILifecycleDetail extends ILifecycle {
	accountLifecycleStages?: ILifecycleStage[];
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

export const fetchLifecycles = async (
	params: IFetchLifecycles
): Promise<ILifecycleDetail[]> =>
	(await requestFirstLifecyclePage(params)).items;

export const fetchLifecyclesPage = ({
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

export const fetchLifecycle = ({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<ILifecycleDetail> =>
	sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});

export const fetchChannelLifecycles = async ({
	channelId,
	groupId,
}: {
	channelId?: string;
	groupId: string;
}): Promise<ILifecycleDetail[]> =>
	(
		await fetchLifecyclesPage({
			channelId,
			groupId,
			page: 1,
			pageSize: 200,
		})
	).items;

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
	name: string;
	position: number;
}

interface ICreateLifecycle {
	channelId: string;
	description: string;
	groupId: string;
	name: string;
	segmentId?: string;
	stages: ILifecycleStagePayload[];
}

export async function createLifecycle({
	channelId,
	description,
	groupId,
	name,
	segmentId,
	stages,
}: ICreateLifecycle): Promise<ILifecycleDetail> {
	return sendRequest({
		data: {
			accountLifecycle: {
				accountLifecycleStages: stages,
				description,
				name,
				...(segmentId && {segmentId}),
			},
		},
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
	return sendRequest({
		data: {
			accountLifecycle: {
				accountLifecycleStages: stages,
				description,
				name,
			},
		},
		method: 'PUT',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
}

export async function deleteLifecycle({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<unknown> {
	return sendRequest({
		method: 'DELETE',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
}

export interface IAccountLifecycleStage {
	lifecycleId: string;
	lifecycleName: string;
	name: string;
	stageExternalReferenceCode: string;
}

export interface IAccountLifecycleStageStatus {
	description?: string;
	endDate?: number | null;
	id: string;
	maxDuration?: number | null;
	name: string;
	position: number;
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
