import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import TextTruncate from 'shared/components/TextTruncate';
import URLConstants from 'shared/util/url-constants';
import {
	createOrderIOMap,
	getDefaultSortOrder,
	NAME
} from 'shared/util/pagination';
import {INDIVIDUALS} from 'shared/util/router';
import {interestListColumns} from 'shared/util/table-columns';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

export const TOTAL_DAYS = 90;

interface IContributionsCellProps {
	className?: string;
	data: {relatedPagesCount: number};
}

export const ContributionsCell: React.FC<IContributionsCellProps> = ({
	className,
	data: {relatedPagesCount}
}) => (
	<td className={getCN('table-cell-expand', className)}>
		<TextTruncate
			title={sub(Liferay.Language.get('x-contributing-pages'), [
				relatedPagesCount
			])}
		/>
	</td>
);

interface IInterestsProps {
	channelId: string;
	groupId: string;
	id: string;
}

const Interests: React.FC<IInterestsProps> = ({channelId, groupId, id}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME))
	});

	return (
		<Card pageDisplay>
			<SearchableEntityTable
				className='interest-history-table'
				columns={[
					interestListColumns.getName({
						channelId,
						groupId,
						id,
						maxWidth: null,
						routeFn: ({data: {name}}) =>
							name &&
							toRoute(
								Routes.CONTACTS_INDIVIDUAL_INTEREST_DETAILS,
								{
									channelId,
									groupId,
									id,
									interestId: name
								}
							),
						type: INDIVIDUALS
					}),
					{
						accessor: 'relatedPagesCount',
						cellRenderer: ContributionsCell,
						label: Liferay.Language.get('contributing-pages'),
						sortable: false
					}
				]}
				dataSourceFn={API.interests.search}
				dataSourceParams={{
					channelId,
					contactsEntityId: id,
					groupId,
					interestMax: TOTAL_DAYS
				}}
				delta={delta}
				entityLabel={Liferay.Language.get('interests')}
				noResultsRenderer={() => (
					<NoResultsDisplay
						description={
							<>
								<span className='mr-1'>
									{Liferay.Language.get(
										'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
									)}
								</span>

								<ClayLink
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
				)}
				orderByOptions={[
					{
						label: Liferay.Language.get('interest'),
						value: NAME
					}
				]}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rowIdentifier='name'
			/>
		</Card>
	);
};

export default Interests;
