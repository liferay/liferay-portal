import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getInterestsQuery from 'contacts/queries/InterestsQuery';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {compositionListColumns} from 'shared/util/table-columns';
import {CompositionTypes} from 'shared/util/constants';
import {
	getMapResultToProps,
	mapCardPropsToOptions
} from 'contacts/hoc/mappers/interests-query';
import {graphql} from '@apollo/client/react/hoc';
import {PAGES, Routes, setUriQueryValue, toRoute} from 'shared/util/router';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withTableData} from 'shared/hoc';

const withData = () =>
	graphql(getInterestsQuery(CompositionTypes.SegmentInterests), {
		options: mapCardPropsToOptions,
		props: getMapResultToProps(CompositionTypes.SegmentInterests)
	});

const TableWithData = withTableData(withData, {
	emptyDescription: (
		<>
			<span className='mr-1'>
				{Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
				)}
			</span>

			<ClayLink
				href={URLConstants.SegmentsTopInterestsDocumentationLink}
				key='DOCUMENTATION'
				target='_blank'
			>
				{Liferay.Language.get('learn-more-about-interests')}
			</ClayLink>
		</>
	),
	emptyTitle: Liferay.Language.get('no-interests-were-found'),
	getColumns: ({channelId, groupId, id, maxCount, totalCount}) => [
		compositionListColumns.getName({
			label: Liferay.Language.get('topic'),
			maxWidth: 200,
			routeFn: ({data: {name}}) =>
				name &&
				setUriQueryValue(
					toRoute(Routes.CONTACTS_SEGMENT_INTEREST_DETAILS, {
						channelId,
						groupId,
						id,
						interestId: name,
						tabId: PAGES
					}),
					'active',
					true
				),
			sortable: false
		}),
		compositionListColumns.getRelativeMetricBar({
			abbreviateCount: true,
			label: Liferay.Language.get('segment-members'),
			maxCount,
			totalCount
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('active-members'),
			totalCount
		})
	],
	rowIdentifier: 'name'
});

const InterestsCard = ({channelId, groupId, id}) => (
	<Card
		className='interests-card-root'
		minHeight={536}
		reportContainer={ReportContainer.TopInterestsCard}
	>
		<Card.Header>
			<Card.Title>{Liferay.Language.get('top-interests')}</Card.Title>
		</Card.Header>

		<TableWithData
			channelId={channelId}
			groupId={groupId}
			id={id}
			rowBordered={false}
		/>

		<Card.Footer>
			<ClayLink
				className='button-root'
				decoration='none'
				displayType='secondary'
				href={toRoute(Routes.CONTACTS_SEGMENT_INTERESTS, {
					channelId,
					groupId,
					id
				})}
				small
			>
				{Liferay.Language.get('view-all-interests')}

				<ClayIcon
					className='icon-root ml-2'
					symbol='angle-right-small'
				/>
			</ClayLink>
		</Card.Footer>
	</Card>
);

export default InterestsCard;
