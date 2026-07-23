import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import InterestsQuery from 'shared/queries/InterestsQuery';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {compositionListColumns} from 'shared/util/table-columns';
import {CompositionTypes, RangeKeyTimeRanges} from 'shared/util/constants';
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
	graphql(InterestsQuery, {
		options: mapCardPropsToOptions,
		props: getMapResultToProps(CompositionTypes.SiteInterests),
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
				{Liferay.Language.get('learn-more-about-interests')}
			</a>
		</>
	),
	emptyTitle: Liferay.Language.get(
		'there-are-no-interests-on-the-selected-period'
	),
	getColumns: ({maxCount, totalCount}) => [
		compositionListColumns.getRelativeMetricBar({
			label: `${Liferay.Language.get(
				'interest-topics'
			)} | ${Liferay.Language.get('sessions')}`,
			maxCount,
			showName: true,
			totalCount,
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('sessions'),
			totalCount,
		}),
	],
	rowIdentifier: 'name',
});

const InterestsCard = () => {
	const {channelId, groupId} = useParams();

	const {Last7Days, Last30Days, Last90Days, Yesterday} = RangeKeyTimeRanges;

	const rangeKeys = [Yesterday, Last7Days, Last30Days, Last90Days];

	return (
		<BaseCard
			className="interests-card-root"
			label={Liferay.Language.get('interests')}
			legacyDropdownRangeKey={false}
			rangeKeys={rangeKeys}
			reportContainer={ReportContainer.InterestsCard}
		>
			{({accountId, rangeSelectors}) => (
				<>
					<TableWithData
						accountId={accountId}
						channelId={channelId}
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
								toRoute(Routes.SITES_INTERESTS, {
									channelId,
									groupId,
								})
							)}
							small
						>
							{Liferay.Language.get('all-interests')}

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

export default InterestsCard;
