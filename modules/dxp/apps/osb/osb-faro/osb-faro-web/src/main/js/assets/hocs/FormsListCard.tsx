import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import FormsListQuery from 'shared/queries/FormsListQuery';
import ListComponent from 'shared/hoc/ListComponent';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	createOrderIOMap,
	getGraphQLVariablesFromPagination,
	SUBMISSIONS_METRIC
} from 'shared/util/pagination';
import {getSafeRangeSelectors} from 'shared/util/util';
import {mapListResultsToProps} from 'shared/util/mappers';
import {metricsListColumns} from 'shared/util/table-columns';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/react-hooks';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const FormsListCard: React.FC = () => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(SUBMISSIONS_METRIC)
	});

	const {channelId, groupId} = useParams();
	const rangeSelectors = useQueryRangeSelectors();

	const response = useQuery(FormsListQuery, {
		variables: {
			channelId,
			...getGraphQLVariablesFromPagination({
				delta,
				orderIOMap,
				page,
				query
			}),
			...getSafeRangeSelectors(rangeSelectors)
		}
	});
	return (
		<Card className='forms-root' pageDisplay>
			<ListComponent
				{...mapListResultsToProps(response, result => ({
					items: result.forms.assetMetrics,
					total: result.forms.total
				}))}
				columns={[
					metricsListColumns.getTitleId({
						channelId,
						groupId,
						label: `${Liferay.Language.get(
							'form-name'
						)} | ${Liferay.Language.get('id').toUpperCase()}`,
						rangeSelectors,
						route: Routes.ASSETS_FORMS_OVERVIEW
					}),
					metricsListColumns.submissionsMetric,
					metricsListColumns.viewsMetric,
					metricsListColumns.abandonmentsMetric,
					metricsListColumns.completionTimeMetric
				]}
				delta={delta}
				entityLabel={Liferay.Language.get('forms')}
				legacyDropdownRangeKey={false}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								<span className='mr-1'>
									{Liferay.Language.get(
										'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
									)}
								</span>

								<ClayLink
									href={
										URLConstants.AssetsFormsListDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-forms'
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
							'there-are-no-visitors-data-found'
						)}
					/>
				}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rangeSelectors={rangeSelectors}
				rowIdentifier={['assetId', 'assetTitle']}
				showDropdownRangeKey
				showFilterAndOrder={false}
			/>
		</Card>
	);
};

export default FormsListCard;
