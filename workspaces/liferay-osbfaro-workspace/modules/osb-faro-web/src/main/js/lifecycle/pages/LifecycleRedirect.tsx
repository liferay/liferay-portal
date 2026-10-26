import * as API from 'shared/api';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import LifecyclesPage from 'lifecycle/components/LifecyclesPage';
import Loading from 'shared/components/Loading';
import React from 'react';
import {
	ConfigureLifecycleEmptyState,
	LifecycleAccountDataEmptyState,
} from 'lifecycle/components/LifecycleEmptyStates';
import {Navigate, useParams} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleAccountData} from 'lifecycle/hooks/useLifecycleAccountData';
import {useRequest} from 'shared/hooks/useRequest';
import {AccountDataStatus} from 'lifecycle/utils/types';

const LifecycleRedirect = () => {
	const currentUser = useCurrentUser();

	const {channelId, groupId} = useParams();

	const {refetch: refetchAccountData, status: accountDataStatus} =
		useLifecycleAccountData();

	const {
		data: lifecycles,
		error: lifecyclesError,
		loading: lifecyclesLoading,
		refetch: refetchLifecycles,
	} = useRequest({
		dataSourceFn: API.lifecycle.fetchLifecycles,
		variables: {
			channelId,
			defaultAccountLifecycle: true,
			groupId: groupId!,
		},
	});

	const defaultLifecycle =
		accountDataStatus === AccountDataStatus.Ready &&
		!lifecyclesLoading &&
		lifecycles?.[0];

	if (defaultLifecycle) {
		return (
			<Navigate
				replace
				to={toRoute(Routes.LIFECYCLE_DASHBOARD, {
					channelId,
					groupId,
					lifecycleId: defaultLifecycle.externalReferenceCode,
				})}
			/>
		);
	}

	const renderBody = () => {
		if (accountDataStatus !== AccountDataStatus.Ready) {
			return (
				<LifecycleAccountDataEmptyState
					authorized={currentUser.isAdmin()}
					groupId={groupId!}
					onReload={refetchAccountData}
					status={accountDataStatus}
				/>
			);
		}

		if (lifecyclesLoading) {
			return <Loading />;
		}

		if (lifecyclesError) {
			return <ErrorDisplay onReload={refetchLifecycles} spacer />;
		}

		return (
			<ConfigureLifecycleEmptyState
				channelId={channelId!}
				groupId={groupId!}
			/>
		);
	};

	return <LifecyclesPage>{renderBody()}</LifecyclesPage>;
};

export default LifecycleRedirect;
