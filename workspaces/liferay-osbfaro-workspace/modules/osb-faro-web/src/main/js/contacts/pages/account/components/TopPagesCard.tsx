import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import React, {useContext, useState} from 'react';
import SitesTopPagesQuery, {
	SitesTopPagesQueryData,
	SitesTopPagesQueryVariables,
} from 'shared/queries/SitesTopPagesQuery';
import TopPagesCardContent, {
	TOP_PAGES_TABS,
	TopPagesEmptyState,
} from 'shared/components/TopPagesCardContent';
import {getSafeRangeSelectors} from 'shared/util/util';
import {OrderByDirections} from 'shared/util/constants';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {useQuery} from '@apollo/client';

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
		accountId,
		accountName,
		router: {
			params: {channelId, groupId},
		},
	} = useContext(BasePage.Context);

	const {
		data,
		error,
		loading = false,
	} = useQuery<SitesTopPagesQueryData, SitesTopPagesQueryVariables>(
		SitesTopPagesQuery,
		{
			variables: {
				...getSafeRangeSelectors(rangeSelectors),
				accountId,
				channelId,
				size: 5,
				sort: {
					column: activeTabId,
					type: OrderByDirections.Descending,
				},
				start: 0,
			},
		}
	);

	const routeQueries = pickBy({accountId, accountName});

	return (
		<TopPagesCardContent
			activeTabId={activeTabId}
			empty={!data?.pages.total}
			emptyState={<TopPagesEmptyState />}
			error={error}
			footer={{
				href: setUriQueryValues(
					routeQueries,
					toRoute(Routes.SITES_TOUCHPOINTS, {channelId, groupId})
				),
				label: Liferay.Language.get('view-all'),
			}}
			items={data?.pages.assetMetrics ?? []}
			loading={loading}
			onActiveTabIdChange={setActiveTabId}
			rangeSelectors={rangeSelectors}
			routeQueries={routeQueries}
		/>
	);
};

export default TopPagesCard;
