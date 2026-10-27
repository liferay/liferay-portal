import * as API from 'shared/api';
import {useRequest} from 'shared/hooks/useRequest';

interface IUseLifecyclesDetailsProps {
	channelId: string;
	defaultAccountLifecycle?: boolean;
	groupId: string;
}

export const useLifecycleDetails = ({
	channelId,
	defaultAccountLifecycle = true,
	groupId,
}: IUseLifecyclesDetailsProps) => {
	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {channelId, defaultAccountLifecycle, groupId},
	});

	return {
		error,
		hasError: !!error,
		lifecycles: data,
		loading,
		refetch,
	};
};
