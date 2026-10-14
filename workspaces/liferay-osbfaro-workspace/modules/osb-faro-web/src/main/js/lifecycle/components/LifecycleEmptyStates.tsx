import ClayLink from '@clayui/link';
import DataSourceEmptyState from 'shared/components/DataSourceEmptyState';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

export const ConfigureLifecycleEmptyState = ({
	channelId,
	groupId,
}: {
	channelId: string;
	groupId: string;
}) => (
	<NoResultsDisplay
		description={Liferay.Language.get(
			'complete-the-configuration-to-start-seeing-insights'
		)}
		displayCard
		icon={{
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_lifecycle_empty',
		}}
		spacer
		title={Liferay.Language.get('configure-a-new-lifecycle')}
	>
		<ClayLink
			button
			className="button-root mt-1"
			displayType="primary"
			href={toRoute(Routes.LIFECYCLE_CREATE, {channelId, groupId})}
		>
			{Liferay.Language.get('new-lifecycle')}
		</ClayLink>
	</NoResultsDisplay>
);

export const NoDataSourcesEmptyState = ({
	authorized,
	groupId,
}: {
	authorized: boolean;
	groupId: string;
}) => (
	<DataSourceEmptyState
		authorized={authorized}
		description={
			authorized
				? Liferay.Language.get(
						'connect-a-data-source-to-sync-lifecycle-stages'
					)
				: Liferay.Language.get(
						'please-contact-your-workspace-administrator-to-add-data-sources'
					)
		}
		groupId={groupId}
		title={Liferay.Language.get('no-data-sources-connected')}
	/>
);
