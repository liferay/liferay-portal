import BaseCard from 'shared/components/base-card';
import BasePage from 'shared/components/base-page';
import React, {useContext, useState} from 'react';
import SitesTopPagesQuery, {
	SitesTopPagesQueryData,
	SitesTopPagesQueryVariables,
} from 'shared/queries/SitesTopPagesQuery';
import TopPagesCardContent, {
	TOP_PAGES_TABS,
} from 'shared/components/TopPagesCardContent';
import {getSafeRangeSelectors} from 'shared/util/util';
import {OrderByDirections} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {useQuery} from '@apollo/client';

interface ITopPagesCardProps extends React.HTMLAttributes<HTMLElement> {
	footer: {
		label: string;
		href: string;
	};
	label: string;
	legacyDropdownRangeKey?: boolean;
	minHeight?: number;
}

const TopPagesCard: React.FC<ITopPagesCardProps> = ({
	className,
	footer,
	label,
	legacyDropdownRangeKey,
	minHeight,
}) => (
	<BaseCard
		className={className}
		label={label}
		legacyDropdownRangeKey={legacyDropdownRangeKey ?? true}
		minHeight={minHeight}
		reportContainer={ReportContainer.TopPagesCard}
	>
		{({rangeSelectors}) => (
			<TopPagesCardWithData
				footer={footer}
				rangeSelectors={rangeSelectors}
			/>
		)}
	</BaseCard>
);

interface ITopPageCardWithData extends Partial<ITopPagesCardProps> {
	rangeSelectors: RangeSelectors;
}

const TopPagesCardWithData: React.FC<ITopPageCardWithData> = ({
	footer,
	rangeSelectors,
}) => {
	const [activeTabId, setActiveTabId] = useState(TOP_PAGES_TABS[0].tabId);
	const {
		accountId,
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

	return (
		<TopPagesCardContent
			activeTabId={activeTabId}
			empty={!data?.pages.total}
			error={error}
			footer={footer}
			items={data?.pages.assetMetrics ?? []}
			loading={loading}
			onActiveTabIdChange={setActiveTabId}
			rangeSelectors={rangeSelectors}
		/>
	);
};

export default TopPagesCard;
