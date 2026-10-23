import * as API from 'shared/api';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {useRequest} from 'shared/hooks/useRequest';
import {sortBy} from 'lodash';

export const useLifecycleStageOptions = ({
	channelId,
	groupId,
}: {
	channelId?: string;
	groupId: string;
}): {loading: boolean; options: {label: string; value: string}[]} => {
	const {data: lifecycles, loading: lifecyclesLoading} = useRequest({
		dataSourceFn: API.lifecycle.fetchAccountLifecycles,
		variables: {channelId, groupId},
	});

	const lifecycle = lifecycles?.[0];

	const {data: lifecycleDetail, loading: lifecycleDetailLoading} = useRequest(
		{
			dataSourceFn: API.lifecycle.fetchLifecycle,
			skipRequest: !lifecycle,
			variables: {groupId, lifecycleId: lifecycle?.id ?? ''},
		}
	);

	const stages: ILifecycleStage[] = lifecycleDetail?.stages ?? [];

	return {
		loading:
			lifecyclesLoading || (Boolean(lifecycle) && lifecycleDetailLoading),
		options: sortBy(stages, 'priority').map(
			({externalReferenceCode, stageName}) => ({
				label: stageName,
				value: externalReferenceCode,
			})
		),
	};
};
