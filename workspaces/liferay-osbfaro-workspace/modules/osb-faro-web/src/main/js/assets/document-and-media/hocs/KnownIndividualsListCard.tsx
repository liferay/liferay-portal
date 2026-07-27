import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import getMetricsMapper from 'shared/hoc/mappers/metrics';
import knownIndividualsListAssetQuery from 'shared/queries/knownIndividualsListAssetQuery';
import React, {useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	compose,
	withBaseResults,
	withQueryPagination,
	withQueryRangeSelectors,
} from 'shared/hoc';
import {createOrderIOMap, DOWNLOADS_METRIC, NAME} from 'shared/util/pagination';
import {graphql} from '@apollo/client/react/hoc';
import {metricsListColumns} from 'shared/util/table-columns';
import {RangeSelectors} from 'shared/types';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

const withData = () =>
	graphql(
		knownIndividualsListAssetQuery('document', DOWNLOADS_METRIC),
		getMetricsMapper((result) => ({
			items: result.document.downloadsMetric.individuals.individuals,
			total: result.document.downloadsMetric.individuals.total,
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

interface IKnownIndividualsListCardProps {
	rangeSelectors: RangeSelectors;
	[key: string]: unknown;
}

const KnownIndividualsListCard = ({
	rangeSelectors: initialRangeSelectors,
	...otherProps
}: IKnownIndividualsListCardProps) => {
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

export default compose<React.ComponentType<any>>(
	withQueryPagination({initialOrderIOMap: createOrderIOMap(NAME)}),
	withQueryRangeSelectors()
)(KnownIndividualsListCard);
