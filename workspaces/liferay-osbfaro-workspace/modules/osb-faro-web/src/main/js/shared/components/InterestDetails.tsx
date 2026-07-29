import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import getMetricsMapper from 'shared/hoc/mappers/metrics';
import React from 'react';
import TouchpointsQuery from 'shared/queries/TouchpointsQuery';
import URLConstants from 'shared/util/url-constants';
import {createOrderIOMap, VISITORS_METRIC} from 'shared/util/pagination';
import {graphql} from '@apollo/client/react/hoc';
import {
	metricsListColumns,
	sitePagesListColumns,
} from 'shared/util/table-columns';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors, Router} from 'shared/types';
import {Routes} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {withBaseResults, withRangeKey} from 'shared/hoc';

const withData = () =>
	graphql(
		TouchpointsQuery,
		getMetricsMapper((result) => ({
			items: result.pages.assetMetrics,
			total: result.pages.total,
		}))
	);

interface ITableWithDataProps {
	accountId?: string | null;
	accountName?: string | null;
	channelId: string;
	delta: number;
	groupId: string;
	interestId: string;
	orderIOMap: OrderedMap<string, OrderParams>;
	page: number;
	query: string;
	rangeSelectors: RangeSelectors;
	router: Router;
	segmentId?: string | null;
	segmentName?: string | null;
}

const {Last7Days, Last30Days, Last90Days, Yesterday} = RangeKeyTimeRanges;

const TableWithData: React.FC<ITableWithDataProps> = withRangeKey(
	withBaseResults(withData, {
		emptyDescription: sub(
			Liferay.Language.get('empty-message-lists'),
			[
				<ClayLink
					href={URLConstants.DocumentationLink}
					key="DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get('documentation').toLowerCase()}
				</ClayLink>,
			],
			false
		),
		emptyTitle: Liferay.Language.get('empty-title-pages'),
		getColumns: ({
			accountId,
			accountName,
			channelId,
			groupId,
			rangeSelectors,
			segmentId,
			segmentName,
		}: ITableWithDataProps) => [
			sitePagesListColumns.getTitleUrl({
				accountId,
				accountName,
				channelId,
				groupId,
				rangeSelectors,
				route: Routes.SITES_TOUCHPOINTS_OVERVIEW,
				segmentId,
				segmentName,
			}),
			metricsListColumns.visitorsMetric,
			metricsListColumns.viewsMetric,
			metricsListColumns.avgTimeOnPageMetric,
			metricsListColumns.bounceRateMetric,
			metricsListColumns.entrancesMetric,
			metricsListColumns.exitRateMetric,
		],
		legacyDropdownRangeKey: false,
		rangeKeys: [Yesterday, Last7Days, Last30Days, Last90Days],
		rowIdentifier: 'assetId',
		showDropdownRangeKey: true,
	})
);

interface IInterestDetailsProps {
	className?: string;
	router: Router;
}

const InterestDetails: React.FC<IInterestDetailsProps> = ({
	className,
	router,
}) => {
	const {channelId, groupId, interestId} = useParams();
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(VISITORS_METRIC),
	});

	const rangeSelectors = useQueryRangeSelectors();

	return (
		<Card className={getCN(className)} pageDisplay>
			<Card.Header className="align-items-center d-flex justify-content-between">
				<Card.Title>
					{sub(
						Liferay.Language.get('pages-containing-x'),
						[
							<span className="interest-title" key="INTEREST_ID">
								{`"${interestId}"`}
							</span>,
						],
						false
					)}
				</Card.Title>
			</Card.Header>

			<TableWithData
				channelId={channelId!}
				delta={delta}
				groupId={groupId!}
				interestId={interestId!}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rangeSelectors={rangeSelectors}
				router={router}
			/>
		</Card>
	);
};

export default InterestDetails;
