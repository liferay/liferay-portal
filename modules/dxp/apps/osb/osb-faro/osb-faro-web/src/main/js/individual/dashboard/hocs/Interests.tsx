import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import IndividualInterestsQuery from 'shared/queries/IndividualInterestsQuery';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {compositionListColumns} from 'shared/util/table-columns';
import {CompositionTypes, Sizes} from 'shared/util/constants';
import {COUNT, createOrderIOMap} from 'shared/util/pagination';
import {
	getMapResultToProps,
	mapPropsToOptions
} from 'contacts/hoc/mappers/interests-query';
import {graphql} from '@apollo/react-hoc';
import {Routes, toRoute} from 'shared/util/router';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {withBaseResults} from 'shared/hoc';

const withData = () =>
	graphql(IndividualInterestsQuery, {
		options: mapPropsToOptions,
		props: getMapResultToProps(CompositionTypes.IndividualInterests)
	});

const TableWithData = withBaseResults(withData, {
	getColumns: ({channelId, groupId, maxCount, totalCount}) => [
		compositionListColumns.getName({
			label: Liferay.Language.get('topic'),
			maxWidth: 200,
			routeFn: ({data: {name}}) =>
				name &&
				toRoute(Routes.CONTACTS_INDIVIDUALS_INTEREST_DETAILS, {
					channelId,
					groupId,
					interestId: name
				}),
			sortable: true
		}),
		compositionListColumns.getRelativeMetricBar({
			label: Liferay.Language.get('total-individuals'),
			maxCount,
			sortable: true,
			totalCount
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('total-individuals'),
			totalCount
		})
	],
	rowIdentifier: 'name',
	showDropdownRangeKey: false
});

const Interests: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const {channelId, groupId} = useParams();
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(COUNT)
	});

	return (
		<Card pageDisplay>
			<Card.Header className='align-items-center d-flex justify-content-between'>
				<Card.Title>
					{Liferay.Language.get('interest-topics')}
				</Card.Title>
			</Card.Header>

			<TableWithData
				channelId={channelId}
				delta={delta}
				groupId={groupId}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								{Liferay.Language.get(
									'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
								)}

								<ClayLink
									className='d-block mb-3'
									href={
										URLConstants.IndividualsDashboardInterestsDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-interests'
									)}
								</ClayLink>
							</>
						}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_satellite'
						}}
						title={Liferay.Language.get(
							'there-are-no-interests-found'
						)}
					/>
				}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rowBordered={false}
			/>
		</Card>
	);
};

export default Interests;
