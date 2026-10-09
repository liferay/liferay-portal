import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import EventAnalysisListCard from '../hocs/EventAnalysisListCard';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {FeatureName, useLimitReached} from 'shared/hooks/useLimitReached';
import {Routes, toRoute} from 'shared/util/router';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const List = () => {
	const {selectedChannel} = useChannelContext();
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();
	const currentUser = useCurrentUser();

	const {empty, error, loading} = useDataSources();

	const {
		data: usageData,
		error: usageError,
		loading: usageLoading,
		refetch,
	} = useRequest({
		dataSourceFn: API.projects.fetchFeatureUsages,
		variables: {groupId},
	});

	const hasLimitReached = useLimitReached({
		data: usageData,
		featureName: FeatureName.EventAnalysis,
	});

	const pageAction = [
		{
			button: true,
			disabled:
				empty || error || usageLoading || usageError || hasLimitReached,
			displayType: 'primary',
			href: toRoute(Routes.EVENT_ANALYSIS_CREATE, {channelId, groupId}),
			label: Liferay.Language.get('create-analysis'),
		},
	];

	const authorized = currentUser.isAdmin();

	return (
		<BasePage documentTitle={Liferay.Language.get('event-analysis')}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel?.name,
					}),
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						title={Liferay.Language.get('event-analysis')}
					/>

					<BasePage.Header.Section>
						<BasePage.Header.PageActions actions={pageAction} />

						{hasLimitReached && (
							<ClayButton
								borderless
								className="ml-2"
								data-tooltip-align="right"
								displayType="unstyled"
								size="sm"
								title={Liferay.Language.get(
									'a-maximum-number-of-event-analyses-has-been-reached-delete-an-existing-analysis-to-create-a-new-one'
								)}
							>
								<ClayIcon
									className="text-secondary"
									symbol="exclamation-full"
								/>
							</ClayButton>
						)}
					</BasePage.Header.Section>
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.Body>
				<StatesRenderer empty={empty} error={error} loading={loading}>
					<StatesRenderer.Loading />

					<StatesRenderer.Empty
						description={
							<>
								{authorized
									? Liferay.Language.get(
											'connect-a-data-source-to-get-started'
										)
									: Liferay.Language.get(
											'please-contact-your-workspace-administrator-to-add-data-sources'
										)}

								<ClayLink
									className="d-block mb-3"
									href={URLConstants.DataSourceConnection}
									key="DOCUMENTATION"
									target="_blank"
								>
									{Liferay.Language.get(
										'access-our-documentation-to-learn-more'
									)}
								</ClayLink>

								{authorized && (
									<ClayLink
										button
										className="button-root"
										displayType="primary"
										href={toRoute(
											Routes.SETTINGS_DATA_SOURCE_LIST,
											{
												groupId,
											}
										)}
									>
										{Liferay.Language.get(
											'connect-data-source'
										)}
									</ClayLink>
								)}
							</>
						}
						displayCard
						title={Liferay.Language.get(
							'no-data-sources-connected'
						)}
					/>

					<StatesRenderer.Success>
						<EventAnalysisListCard onItemsChange={refetch} />
					</StatesRenderer.Success>
				</StatesRenderer>
			</BasePage.Body>
		</BasePage>
	);
};

export default List;
