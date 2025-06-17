import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import CustomAssetsListQuery from 'shared/queries/CustomAssetsListQuery';
import ListComponent from 'shared/hoc/ListComponent';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	createOrderIOMap,
	getGraphQLVariablesFromPagination,
	MODIFIED_DATE
} from 'shared/util/pagination';
import {mapListResultsToProps} from 'shared/util/mappers';
import {metricsListColumns} from 'shared/util/table-columns';
import {Routes} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/react-hooks';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const CustomAssetsListCard: React.FC<{timeZoneId: string}> = ({timeZoneId}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(MODIFIED_DATE)
	});

	const {channelId, groupId} = useParams();
	const rangeSelectors = useQueryRangeSelectors();

	const response = useQuery(CustomAssetsListQuery, {
		fetchPolicy: 'network-only',
		variables: {
			channelId,
			...getGraphQLVariablesFromPagination({
				delta,
				orderIOMap,
				page,
				query
			})
		}
	});

	return (
		<Card className='custom-assets-root' pageDisplay>
			<ListComponent
				{...mapListResultsToProps(response, result => ({
					items: result.dashboards.dashboards,
					total: result.dashboards.total
				}))}
				columns={[
					metricsListColumns.getTitleId({
						channelId,
						groupId,
						label: `${Liferay.Language.get(
							'asset'
						)} | ${Liferay.Language.get('id').toUpperCase()}`,
						rangeSelectors,
						route: Routes.ASSETS_CUSTOM_DASHBOARD
					}),
					metricsListColumns.modifiedDate,
					metricsListColumns.getCreateDate(timeZoneId)
				]}
				delta={delta}
				entityLabel={Liferay.Language.get('custom-assets')}
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
										URLConstants.AssetsCustomAssetsListDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-custom-assets'
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
				rowIdentifier='id'
				showFilterAndOrder={false}
			/>
		</Card>
	);
};

export default CustomAssetsListCard;
