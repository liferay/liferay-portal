import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import getInterestsQuery from 'contacts/queries/InterestsQuery';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {compositionListColumns} from 'shared/util/table-columns';
import {CompositionTypes} from 'shared/util/constants';
import {COUNT, createOrderIOMap} from 'shared/util/pagination';
import {
	getMapResultToProps,
	mapPropsToOptions
} from 'contacts/hoc/mappers/interests-query';
import {graphql} from '@apollo/react-hoc';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {withBaseResults} from 'shared/hoc';

const withData = () =>
	graphql(getInterestsQuery(CompositionTypes.AccountInterests), {
		options: mapPropsToOptions,
		props: getMapResultToProps(CompositionTypes.AccountInterests)
	});

const TableWithData = withBaseResults(withData, {
	defaultOrderByField: 'count',
	emptyDescription: (
		<>
			<span className='mr-1'>
				{Liferay.Language.get(
					'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
				)}
			</span>

			<ClayLink
				href={URLConstants.AccountIndividualsDocumentationLink}
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
	getColumns: ({channelId, groupId, id, maxCount, totalCount}) => [
		compositionListColumns.getName({
			label: Liferay.Language.get('topic'),
			routeFn: ({data: {name}}) =>
				name &&
				toRoute(Routes.CONTACTS_ACCOUNT_INTEREST_DETAILS, {
					channelId,
					groupId,
					id,
					interestId: name
				}),
			sortable: true
		}),
		compositionListColumns.getRelativeMetricBar({
			label: Liferay.Language.get('account-members'),
			maxCount,
			sortable: true,
			totalCount
		}),
		compositionListColumns.getPercentOf({
			metricName: Liferay.Language.get('active-members'),
			totalCount
		})
	],
	rowIdentifier: 'name',
	showDropdownRangeKey: false
});

const Interests = () => {
	const {channelId, groupId, id} = useParams();
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(COUNT)
	});

	return (
		<Card pageDisplay>
			<Card.Header className='align-items-center d-flex justify-content-between'>
				<Card.Title>
					{Liferay.Language.get('account-interest-topics')}
				</Card.Title>
			</Card.Header>

			<TableWithData
				channelId={channelId}
				delta={delta}
				groupId={groupId}
				id={id}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rowBordered={false}
			/>
		</Card>
	);
};

export default Interests;
