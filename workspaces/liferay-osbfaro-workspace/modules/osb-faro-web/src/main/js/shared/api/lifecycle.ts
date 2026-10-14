import sendRequest from 'shared/util/request';
import {createMockLifecycleApi} from './lifecycle-mock';
import {ENABLE_LIFECYCLE_DASHBOARDS_MOCK_DATA} from 'shared/util/feature-flags';

export interface ILifecycle {
	default?: boolean;
	description?: string;
	id: string;
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
	displayOrder: number;
	externalReferenceCode: string;
	id: string;
	maxDuration: number | null;
	stageType: string;
}

export interface ILifecycleDetail extends ILifecycle {
	stages?: ILifecycleStage[];
}

export const getDefaultLifecycle = (
	lifecycles: ILifecycle[]
): ILifecycle | undefined =>
	lifecycles.find((lifecycle) => lifecycle.default) ?? lifecycles[0];

interface IFetchLifecycles {
	channelId?: string;
	groupId: string;
}

interface IFetchLifecycle {
	groupId: string;
	lifecycleId: string;
}

const requestLifecycles = ({
	groupId,
}: IFetchLifecycles): Promise<ILifecycle[]> =>
	sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle`,
	});

const requestLifecycle = ({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<ILifecycleDetail> =>
	sendRequest({
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});

const requestDefaultLifecycle = ({
	groupId,
	lifecycleId,
}: IFetchLifecycle): Promise<void> =>
	sendRequest({
		method: 'PUT',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}/default`,
	});

const realLifecycleApi = {
	fetchLifecycle: requestLifecycle,
	fetchLifecycles: requestLifecycles,
	setDefaultLifecycle: requestDefaultLifecycle,
};

const lifecycleApi: typeof realLifecycleApi =
	ENABLE_LIFECYCLE_DASHBOARDS_MOCK_DATA
		? createMockLifecycleApi({...realLifecycleApi, getDefaultLifecycle})
		: realLifecycleApi;

export const {
	fetchLifecycle,
	fetchLifecycles,
	setDefaultLifecycle,
} = lifecycleApi;

interface ILifecycleStagePayload {
	accountLifecycleStageRule: IAccountLifecycleStageRule;
	description: string;
	displayOrder: number;
	id?: string;
	maxDuration: number | null;
	stageType: string;
}

interface ICreateLifecycle {
	channelId: string;
	groupId: string;
	name: string;
	stages: ILifecycleStagePayload[];
}

export async function createLifecycle({
	channelId,
	groupId,
	name,
	stages,
}: ICreateLifecycle): Promise<ILifecycle> {
	return sendRequest({
		data: {accountLifecycle: {name, stages}},
		method: 'POST',
		path: `contacts/${groupId}/account-lifecycle?channelId=${channelId}`,
	});
}

interface IUpdateLifecycle {
	groupId: string;
	lifecycleId: string;
	name: string;
	stages: ILifecycleStagePayload[];
}

export async function updateLifecycle({
	groupId,
	lifecycleId,
	name,
	stages,
}: IUpdateLifecycle): Promise<ILifecycle> {
	return sendRequest({
		data: {accountLifecycle: {name, stages}},
		method: 'PUT',
		path: `contacts/${groupId}/account-lifecycle/${lifecycleId}`,
	});
}

export interface IAccountLifecycle {
	accountId?: string;
	id: string;
}

export interface IAccountLifecycleStageStatus {
	description?: string;
	displayOrder: number;
	endDate?: number | null;
	id: string;
	maxDuration?: number | null;
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
	return sendRequest({
		data: {channelId},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle`,
	});
}
