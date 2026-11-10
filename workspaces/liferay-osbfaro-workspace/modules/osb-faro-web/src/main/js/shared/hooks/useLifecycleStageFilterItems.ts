import * as API from 'shared/api';
import {
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {useRequest} from 'shared/hooks/useRequest';

export interface ILifecycleStageFieldValue {
	externalReferenceCode: string;
	id: string;
	lifecycleName?: string;
	name?: string;
	stageName?: string;
	stageType?: LifecycleStages;
}

const getLifecycleStageLabel = (
	{
		externalReferenceCode,
		lifecycleName,
		name,
		stageName,
		stageType,
	}: ILifecycleStageFieldValue,
	allLifecycles: boolean
) => {
	if (allLifecycles) {
		return `${lifecycleName}: ${name}`;
	}

	return (
		stageName ??
		(stageType && lifecycleStagesLabelMap[stageType]?.label) ??
		externalReferenceCode
	);
};

export const useLifecycleStageFilterItems = ({
	accountLifecycleId,
	allLifecycles = false,
	channelId,
	groupId,
}: {
	accountLifecycleId?: string;
	allLifecycles?: boolean;
	channelId: string;
	groupId: string;
}): {
	items: {label: string; value: string}[];
	lifecycleStages: ILifecycleStageFieldValue[];
	loading: boolean;
} => {
	const {data, loading} = useRequest({
		dataSourceFn: API.accounts.fetchLifecycleStageFieldValues,
		skipRequest: !accountLifecycleId && !allLifecycles,
		variables: {
			accountLifecycleId,
			channelId,
			groupId,
		},
	});

	const lifecycleStages: ILifecycleStageFieldValue[] = data?.items ?? [];

	const items = lifecycleStages.map((lifecycleStage) => ({
		label: getLifecycleStageLabel(lifecycleStage, allLifecycles),
		value: lifecycleStage.externalReferenceCode,
	}));

	return {items, lifecycleStages, loading};
};
