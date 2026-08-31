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
		individualId,
		individualName,
		router: {
			params: {channelId},
		},
	} = useContext(BasePage.Context);

	const {
		data,
		error,
		loading = false,
	} = useQuery<SitesTopPagesQueryData, SitesTopPagesQueryVariables>(
		SitesTopPagesQuery,
		{
			skip: !individualId,
			variables: {
				...getSafeRangeSelectors(rangeSelectors),
				channelId,
				individualId,
				size: 5,
				sort: {
					column: activeTabId,
					type: OrderByDirections.Descending,
				},
				start: 0,
			},
		}
	);

	/**
	 * The Sites Dashboard does not offer an individual filter yet, so the
	 * query values below are carried for symmetry with the account card and
	 * are ignored by the page they land on.
	 */

	const routeQueries = pickBy({individualId, individualName});

	return (
		<TopPagesCardContent
			activeTabId={activeTabId}
			empty={!data?.pages.total}
			emptyState={<TopPagesEmptyState />}
			error={error}
			items={data?.pages.assetMetrics ?? []}
			loading={loading}
			onActiveTabIdChange={setActiveTabId}
			rangeSelectors={rangeSelectors}
			routeQueries={routeQueries}
		/>
	);
};

export default TopPagesCard;
