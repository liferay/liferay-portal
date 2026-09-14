import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import getMetricsMapper from 'shared/hoc/mappers/metrics';
import React, {useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	compose,
	withBaseResults,
	withQueryPagination,
	withQueryRangeSelectors,
} from 'shared/hoc';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {getAssetListQueries} from './assetListQueries';
import {graphql} from '@apollo/client/react/hoc';
import {memoize} from 'lodash';
import {metricsListColumns} from 'shared/util/table-columns';
import {RangeSelectors} from 'shared/types';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

/**
 * Built once per asset type and cached: the Apollo and pagination HOCs below
 * have to keep a stable component identity across renders.
 */
export const getKnownIndividualsListCard = memoize((graphQLType: string) => {
	const {individualsMetric, individualsQuery} =
		getAssetListQueries(graphQLType);

	const withData = () =>
		graphql(
			individualsQuery,
			getMetricsMapper((result: any) => ({
				items: result[graphQLType][individualsMetric].individuals
					.individuals,
				total: result[graphQLType][individualsMetric].individuals.total,
			}))
		);

	const TableWithData = withBaseResults(withData, {
		emptyDescription: (
			<>
				<span className="mr-1">
					{Liferay.Language.get(
						'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
					)}
				</span>

				<ClayLink
					href={URLConstants.IndividualsDashboardDocumentation}
					key="DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get('learn-more-about-individuals')}
				</ClayLink>
			</>
		),
		emptyIcon: {
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_satellite',
		},
		emptyTitle: Liferay.Language.get('no-individuals-were-found'),
		getColumns: ({
			router: {
				params: {channelId, groupId},
			},
		}: {
			router: {params: {channelId: string; groupId: string}};
		}) => [
			metricsListColumns.getNameEmail({
				channelId,
				groupId,
				route: Routes.CONTACTS_INDIVIDUAL,
			}),
		],
		legacyDropdownRangeKey: false,
		rowIdentifier: 'id',
	});

	const KnownIndividualsListCard = ({
		rangeSelectors: initialRangeSelectors,
		...otherProps
	}: {
		rangeSelectors: RangeSelectors;
		[key: string]: unknown;
	}) => {
		const [rangeSelectors, setRangeSelectors] = useState<RangeSelectors>(
			initialRangeSelectors
		);

		return (
			<Card className="known-individuals-root" pageDisplay>
				<TableWithData
					{...otherProps}
					onRangeSelectorsChange={setRangeSelectors}
					rangeSelectors={rangeSelectors}
				/>
			</Card>
		);
	};

	return compose<React.ComponentType<any>>(
		withQueryPagination({initialOrderIOMap: createOrderIOMap(NAME)}),
		withQueryRangeSelectors()
	)(KnownIndividualsListCard);
});
