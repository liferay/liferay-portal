import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React from 'react';
import SearchTermsQuery from 'shared/queries/SearchTermsQuery';
import URLConstants from 'shared/util/url-constants';
import {compositionListColumns} from 'shared/util/table-columns';
import {CompositionTypes} from 'shared/util/constants';
import {
	getMapResultToProps,
	mapCardPropsToOptions,
} from './mappers/composition-query';
import {graphql} from '@apollo/client/react/hoc';
import {pickBy} from 'lodash';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {useParams} from 'react-router-dom';
import {withTableData} from 'shared/hoc';

const withData = () =>
	graphql(SearchTermsQuery, {
		options: mapCardPropsToOptions,
		props: getMapResultToProps(CompositionTypes.SearchTerms),
	});

const TableWithData = withTableData(withData, {
	emptyDescription: (
		<>
			<span className="mr-1">
				{Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
				)}
			</span>

			<a
				href={URLConstants.SitesDashboardSearchTermsAndInterests}
				key="DOCUMENTATION"
				target="_blank"
			>
				{Liferay.Language.get('learn-more-about-search-terms')}
			</a>
		</>
	),
	emptyTitle: Liferay.Language.get(
		'there-are-no-search-terms-on-the-selected-period'
	),
	getColumns: ({maxCount, totalCount}) => [
		compositionListColumns.getRelativeMetricBar({
			label: `${Liferay.Language.get(
				'search-query'
			)} | ${Liferay.Language.get('searches')}`,
			maxCount,
			showName: true,
			totalCount,
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('searches'),
			totalCount,
		}),
	],
	rowIdentifier: 'name',
});

const SearchTermsCard = (props) => {
	const {channelId, groupId, id} = useParams();

	return (
		<BaseCard
			className="search-terms-card-root"
			label={Liferay.Language.get('search-terms')}
			legacyDropdownRangeKey={false}
			reportContainer={ReportContainer.SearchTermsCard}
		>
			{({accountId, rangeSelectors}) => (
				<>
					<TableWithData
						{...props}
						accountId={accountId}
						channelId={channelId}
						id={id}
						rangeSelectors={rangeSelectors}
						rowBordered={false}
					/>

					<Card.Footer>
						<ClayLink
							borderless
							button
							className="button-root"
							displayType="secondary"
							href={setUriQueryValues(
								pickBy({accountId, ...rangeSelectors}),
								toRoute(Routes.SITES_SEARCH_TERMS, {
									channelId,
									groupId,
								})
							)}
							small
						>
							{Liferay.Language.get('all-search-terms')}

							<ClayIcon
								className="icon-root ml-2"
								symbol="angle-right-small"
							/>
						</ClayLink>
					</Card.Footer>
				</>
			)}
		</BaseCard>
	);
};

export default SearchTermsCard;
