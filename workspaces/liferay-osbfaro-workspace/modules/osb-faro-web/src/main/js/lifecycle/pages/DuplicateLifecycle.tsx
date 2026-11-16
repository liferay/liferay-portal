import * as API from 'shared/api';
import CreateLifecycleForm from 'lifecycle/components/CreateLifecycleForm';
import LifecycleNotFound from 'lifecycle/components/LifecycleNotFound';
import LifecyclesPage from 'lifecycle/components/LifecyclesPage';
import Loading from 'shared/components/Loading';
import React from 'react';
import {
	buildDuplicateLifecycleName,
	duplicateStageConfigsFromLifecycle,
} from 'lifecycle/utils/lifecyclePayload';
import {LifecycleAccountDataEmptyState} from 'lifecycle/components/LifecycleEmptyStates';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleAccountData} from 'lifecycle/hooks/useLifecycleAccountData';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {AccountDataStatus} from 'lifecycle/utils/types';

const DuplicateLifecycle = () => {
	const {groupId, sourceLifecycleId} = useParams();

	const currentUser = useCurrentUser();

	const {refetch: refetchAccountData, status: accountDataStatus} =
		useLifecycleAccountData();

	const {
		data: lifecycle,
		error: lifecycleError,
		loading: lifecycleLoading,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycle,
		variables: {groupId: groupId!, lifecycleId: sourceLifecycleId!},
	});

	if (accountDataStatus === AccountDataStatus.Loading || lifecycleLoading) {
		return (
			<LifecyclesPage showTitle={false}>
				<Loading />
			</LifecyclesPage>
		);
	}

	if (accountDataStatus !== AccountDataStatus.Ready) {
		return (
			<LifecyclesPage>
				<LifecycleAccountDataEmptyState
					authorized={currentUser.isAdmin()}
					groupId={groupId!}
					onReload={refetchAccountData}
					status={accountDataStatus}
				/>
			</LifecyclesPage>
		);
	}

	if (lifecycleError || !lifecycle) {
		return <LifecycleNotFound />;
	}

	return (
		<CreateLifecycleForm
			description={lifecycle.description ?? ''}
			name={buildDuplicateLifecycleName(lifecycle.name ?? '')}
			segmentId={lifecycle.segmentId}
			stageConfigs={() =>
				duplicateStageConfigsFromLifecycle(
					lifecycle.accountLifecycleStages
				)
			}
		/>
	);
};

export default DuplicateLifecycle;
