import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import Constants, {
	CompositionTypes,
	RangeKeyTimeRanges,
	Sizes
} from 'shared/util/constants';
import InterestsQuery from 'shared/queries/InterestsQuery';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {compose} from 'redux';
import {compositionListColumns} from 'shared/util/table-columns';
import {COUNT, createOrderIOMap} from 'shared/util/pagination';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {
	getMapResultToProps,
	mapPropsToOptions
} from './mappers/composition-query';
import {graphql} from '@apollo/react-hoc';
import {pickBy} from 'lodash';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useChannelContext} from 'shared/context/channel';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {withHistory, withPaginationBar, withTableData} from 'shared/hoc';

const {
	pagination: {cur: defaultPage, delta: defaultDelta}
} = Constants;

const withData = () =>
	compose(
		graphql(InterestsQuery, {
			options: mapPropsToOptions,
			props: getMapResultToProps(CompositionTypes.SiteInterests)
		}),
		withPaginationBar({defaultDelta})
	);

const TableWithData = withTableData(withData, {
	emptyDescription: (
		<>
			<span className='mr-1'>
				{Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
				)}
			</span>

			<ClayLink
				href={URLConstants.SitesDashboardSearchTermsAndInterests}
				key='DOCUMENTATION'
				target='_blank'
			>
				{Liferay.Language.get('learn-more-about-interests')}
			</ClayLink>
		</>
	),
	emptyIcon: {
		border: false,
		size: Sizes.XXXLarge,
		symbol: 'ac_satellite'
	},
	emptyTitle: Liferay.Language.get('there-are-no-interests-found'),
	getColumns: ({
		channelId,
		groupId,
		maxCount,
		rangeSelectors,
		totalCount
	}) => [
		compositionListColumns.getName({
			label: Liferay.Language.get('topic'),
			maxWidth: 200,
			routeFn: ({data: {name}}) =>
				name &&
				setUriQueryValues(
					pickBy({...rangeSelectors}),
					toRoute(Routes.SITES_INTEREST_DETAILS, {
						channelId,
						groupId,
						interestId: name
					})
				),
			sortable: false
		}),
		compositionListColumns.getRelativeMetricBar({
			label: Liferay.Language.get('sessions'),
			maxCount,
			totalCount
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('sessions'),
			totalCount
		})
	],
	rowIdentifier: 'name'
});

const Interests = ({history}) => {
	const {selectedChannel} = useChannelContext();
	const {channelId, groupId} = useParams();
	const {delta, orderIOMap, page} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(COUNT)
	});

	const rangeSelectors = useQueryRangeSelectors();

	const {Last7Days, Last30Days, Last90Days, Yesterday} = RangeKeyTimeRanges;

	const rangeKeys = [Yesterday, Last7Days, Last30Days, Last90Days];

	const handleRangeKeyValueChange = ({rangeEnd, rangeKey, rangeStart}) => {
		history.push(
			setUriQueryValues(
				pickBy({
					page: defaultPage,
					rangeEnd,
					rangeKey,
					rangeStart
				})
			)
		);
	};

	return (
		<Card className='sites-interests-root' pageDisplay>
			<Card.Header className='align-items-center d-flex justify-content-between'>
				{selectedChannel && (
					<Card.Title>
						{sub(Liferay.Language.get('interest-topics-on-x'), [
							selectedChannel.name
						])}
					</Card.Title>
				)}

				<DropdownRangeKey
					legacy={false}
					onRangeSelectorChange={handleRangeKeyValueChange}
					rangeKeys={rangeKeys}
					rangeSelectors={rangeSelectors}
				/>
			</Card.Header>

			<TableWithData
				channelId={channelId}
				delta={delta}
				groupId={groupId}
				orderIOMap={orderIOMap}
				page={page}
				rangeSelectors={rangeSelectors}
				rowBordered={false}
			/>
		</Card>
	);
};

export default withHistory(Interests);
