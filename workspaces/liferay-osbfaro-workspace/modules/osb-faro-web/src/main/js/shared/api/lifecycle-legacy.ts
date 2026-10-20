import sendRequest from 'shared/util/request';
import type {ILifecycleDetail, ILifecyclesPage} from './lifecycle';

interface ILegacyFetchLifecycles {
	channelId?: string;
	groupId: string;
}

interface ILegacyFetchLifecyclesPage extends ILegacyFetchLifecycles {
	page: number;
	pageSize: number;
	search?: string;
}

const requestLegacyLifecycles = ({
	channelId,
	groupId,
}: ILegacyFetchLifecycles): Promise<ILifecycleDetail[]> =>
	sendRequest({
		data: {...(channelId && {channelId})},
		method: 'GET',
		path: `contacts/${groupId}/account-lifecycle`,
	});

const requestLegacyLifecyclesPage = async ({
	page,
	pageSize,
	search = '',
	...params
}: ILegacyFetchLifecyclesPage): Promise<ILifecyclesPage> => {
	const query = search.trim().toLowerCase();

	const lifecycles = (await requestLegacyLifecycles(params)).filter(
		({name = ''}) => name.toLowerCase().includes(query)
	);

	return {
		items: lifecycles.slice((page - 1) * pageSize, page * pageSize),
		lastPage: Math.max(1, Math.ceil(lifecycles.length / pageSize)),
		page,
		pageSize,
		totalCount: lifecycles.length,
	};
};

export const legacyLifecycleApi = {
	fetchLifecycles: requestLegacyLifecycles,
	fetchLifecyclesPage: requestLegacyLifecyclesPage,
};
