import * as API from 'shared/api';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {useRequest} from 'shared/hooks/useRequest';

export const useLifecycleStageOptions = ({
	groupId,
}: {
	groupId: string;
}): {loading: boolean; options: {label: string; value: string}[]} => {
	const {data: lifecycles, loading: lifecyclesLoading} = useRequest({
		dataSourceFn: API.lifecycle.fetchAccountLifecycles,
		variables: {groupId},
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
		options: stages
			.slice()
			.sort((a, b) => a.displayOrder - b.displayOrder)
			.map(({id, stageType}) => ({
				label:
					lifecycleStagesLabelMap[stageType as LifecycleStages]
						?.label ?? stageType,
				value: id,
			})),
	};
};
