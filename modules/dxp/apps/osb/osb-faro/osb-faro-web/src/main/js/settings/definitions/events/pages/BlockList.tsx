import BasePage from 'settings/components/BasePage';
import BlockListCard from '../components/BlockListCard';
import React from 'react';
import {getDefinitions} from 'shared/util/breadcrumbs';
import {Routes, toRoute} from 'shared/util/router';

interface IBlockListProps {
	groupId: string;
}

const BlockList: React.FC<IBlockListProps> = ({groupId, ...otherProps}) => (
	<BasePage
		breadcrumbItems={[
			getDefinitions({groupId}),
			{
				href: toRoute(Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM, {
					groupId
				}),
				label: Liferay.Language.get('events')
			},
			{active: true, label: Liferay.Language.get('block-list')}
		]}
		pageDescription={Liferay.Language.get(
			'blocked-events-are-not-processed-by-analytics-cloud'
		)}
		pageTitle={Liferay.Language.get('block-list')}
	>
		<BlockListCard {...otherProps} groupId={groupId} />
	</BasePage>
);

export default BlockList;
