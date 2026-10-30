import * as API from 'shared/api';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {useRequest} from 'shared/hooks/useRequest';

export const useChannelLifecycles = ({
	channelId,
	groupId,
}: {
	channelId?: string;
	groupId: string;
}): {lifecycles: ILifecycleDetail[]; loading: boolean} => {
	const {data, loading} = useRequest({
		dataSourceFn: API.lifecycle.fetchChannelLifecycles,
		variables: {channelId, groupId},
	});

	return {lifecycles: data ?? [], loading};
};
