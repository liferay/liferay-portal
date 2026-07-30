import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import React, {useContext, useState} from 'react';
import TopPagesCardContent, {
	ITopPagesCardItem,
	TOP_PAGES_TABS,
} from 'shared/components/TopPagesCardContent';
import {RangeSelectors} from 'shared/types';
import {Routes, toRoute} from 'shared/util/router';

/**
 * TODO LPD-100208: replace with the page metrics of the account in focus.
 */

const MOCK_ITEMS: ITopPagesCardItem[] = [
	{
		assetId: '/group/guest/excavator-maintenance',
		assetTitle: 'Excavator Maintenance',
		entrancesMetric: {value: 12},
		exitRateMetric: {value: 0.24},
		visitorsMetric: {value: 18},
	},
	{
		assetId: '/group/guest/hydraulic-systems',
		assetTitle: 'Hydraulic Systems',
		entrancesMetric: {value: 9},
		exitRateMetric: {value: 0.18},
		visitorsMetric: {value: 14},
	},
	{
		assetId: '/group/guest/crane-safety-tips',
		assetTitle: 'Crane Safety Tips',
		entrancesMetric: {value: 8},
		exitRateMetric: {value: 0.15},
		visitorsMetric: {value: 14},
	},
	{
		assetId: '/group/guest/welding-techniques',
		assetTitle: 'Welding Techniques',
		entrancesMetric: {value: 5},
		exitRateMetric: {value: 0.11},
		visitorsMetric: {value: 9},
	},
	{
		assetId: '/group/guest/engine-overhauls',
		assetTitle: 'Engine Overhauls',
		entrancesMetric: {value: 4},
		exitRateMetric: {value: 0.09},
		visitorsMetric: {value: 9},
	},
];

const TopPagesCard: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	className,
}) => (
	<BaseCard
		className={className}
		label={Liferay.Language.get('top-pages').toUpperCase()}
		legacyDropdownRangeKey={false}
	>
		{({rangeSelectors}) => (
			<TopPagesCardWithData rangeSelectors={rangeSelectors} />
		)}
	</BaseCard>
);

interface ITopPagesCardWithDataProps {
	rangeSelectors: RangeSelectors;
}

const TopPagesCardWithData: React.FC<ITopPagesCardWithDataProps> = ({
	rangeSelectors,
}) => {
	const [activeTabId, setActiveTabId] = useState(TOP_PAGES_TABS[0].tabId);
	const {
		router: {
			params: {channelId, groupId},
		},
	} = useContext(BasePage.Context);

	return (
		<TopPagesCardContent
			activeTabId={activeTabId}
			footer={{
				href: toRoute(Routes.SITES_TOUCHPOINTS, {channelId, groupId}),
				label: Liferay.Language.get('view-all'),
			}}
			items={MOCK_ITEMS}
			onActiveTabIdChange={setActiveTabId}
			rangeSelectors={rangeSelectors}
		/>
	);
};

export default TopPagesCard;
