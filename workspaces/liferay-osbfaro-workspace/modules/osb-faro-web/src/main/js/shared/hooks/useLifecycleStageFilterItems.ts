import * as API from 'shared/api';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {useRequest} from 'shared/hooks/useRequest';

export interface ILifecycleStageFieldValue {
	externalReferenceCode: string;
	id: string;
	stageName?: string;
	stageType?: LifecycleStages;
}

export const useLifecycleStageFilterItems = ({
	accountLifecycleId,
	channelId,
	groupId,
}: {
	accountLifecycleId?: string;
	channelId: string;
	groupId: string;
}): {
	items: {label: string; value: string}[];
	lifecycleStages: ILifecycleStageFieldValue[];
	loading: boolean;
} => {
	const {data, loading} = useRequest({
		dataSourceFn: API.accounts.fetchLifecycleStageFieldValues,
		skipRequest: !accountLifecycleId,
		variables: {
			accountLifecycleId,
			channelId,
			groupId,
		},
	});

	const lifecycleStages: ILifecycleStageFieldValue[] = data?.items ?? [];

	return {
		items: lifecycleStages.map(
			({externalReferenceCode, stageName, stageType}) => ({
				label:
					stageName ??
					(stageType && lifecycleStagesLabelMap[stageType]?.label) ??
					externalReferenceCode,
				value: externalReferenceCode,
			})
		),
		lifecycleStages,
		loading,
	};
};
