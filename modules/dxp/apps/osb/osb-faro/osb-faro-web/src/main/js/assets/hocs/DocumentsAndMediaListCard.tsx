import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import DocumentsAndMediaListQuery from 'shared/queries/DocumentsAndMediaListQuery';
import ListComponent from 'shared/hoc/ListComponent';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	createOrderIOMap,
	DOWNLOADS_METRIC,
	getGraphQLVariablesFromPagination
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

const DocumentsAndMediaListCard: React.FC = () => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(DOWNLOADS_METRIC)
	});

	const {channelId, groupId} = useParams();
	const rangeSelectors = useQueryRangeSelectors();

	const response = useQuery(DocumentsAndMediaListQuery, {
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
		<Card className='documents-and-media-root' pageDisplay>
			<ListComponent
				{...mapListResultsToProps(response, result => ({
					items: result.documents.assetMetrics,
					total: result.documents.total
				}))}
				columns={[
					metricsListColumns.getTitleId({
						channelId,
						groupId,
						label: `${Liferay.Language.get(
							'document-name'
						)} | ${Liferay.Language.get('id').toUpperCase()}`,
						rangeSelectors,
						route: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW
					}),
					metricsListColumns.downloadsMetric,
					metricsListColumns.impressionMadeMetric,
					metricsListColumns.commentsMetric,
					metricsListColumns.ratingsMetric
				]}
				delta={delta}
				entityLabel={Liferay.Language.get('documents-and-media')}
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
										URLConstants.AssetsDocumentsAndMediaListDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-more-about-documents-and-media'
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

export default DocumentsAndMediaListCard;
