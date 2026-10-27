import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {
	ConfigureLifecycleEmptyState,
	NoDataSourcesEmptyState,
} from 'lifecycle/components/LifecycleEmptyStates';
import {Navigate, useParams} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useRequest} from 'shared/hooks/useRequest';

const LifecycleRedirect = () => {
	const currentUser = useCurrentUser();

	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

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

	const defaultLifecycle = !lifecyclesLoading && lifecycles?.[0];

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
		if (dataSourcesLoading || lifecyclesLoading) {
			return <Loading />;
		}

		if (noDataSources) {
			return (
				<NoDataSourcesEmptyState
					authorized={currentUser.isAdmin()}
					groupId={groupId!}
				/>
			);
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

	return (
		<BasePage documentTitle={Liferay.Language.get('lifecycles')}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
					{
						...breadcrumbs.getLifecycles({
							channelId: channelId!,
							groupId: groupId!,
						}),
						active: true,
					},
				]}
				fluid
				groupId={groupId!}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						className="mb-3"
						title={Liferay.Language.get('lifecycles')}
					/>
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.Body fluid>{renderBody()}</BasePage.Body>
		</BasePage>
	);
};

export default LifecycleRedirect;
