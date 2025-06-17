import * as API from 'shared/api';
import BaseListPage from 'contacts/components/BaseListPage';
import ClayLink from '@clayui/link';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	ACCOUNT_TYPE,
	ACTIVITIES_COUNT,
	createOrderIOMap,
	getDefaultSortOrder,
	INDIVIDUAL_COUNT,
	NAME
} from 'shared/util/pagination';
import {accountsListColumns} from 'shared/util/table-columns';
import {FetchSegmentsParams} from 'segment/pages/List';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {User} from 'shared/util/records';

const getAccountsDataSource = ({
	channelId,
	delta,
	groupId,
	orderIOMap,
	page,
	query
}: FetchSegmentsParams) =>
	API.accounts.search({
		channelId,
		delta,
		groupId,
		orderIOMap,
		page,
		query
	});

interface IListProps {
	channelId: string;
	currentUser: User;
	groupId: string;
}

const List: React.FC<IListProps> = ({channelId, groupId, ...otherProps}) => {
	const currentUser = useCurrentUser();
	const authorized = currentUser.isAdmin();

	const columns = [
		accountsListColumns.getName({channelId, groupId}),
		accountsListColumns.type,
		accountsListColumns.individualCount,
		accountsListColumns.activitiesCount
	];

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME))
	});

	return (
		<BaseListPage
			{...otherProps}
			columns={columns}
			currentUser={currentUser}
			dataSourceFn={getAccountsDataSource}
			delta={delta}
			emptyStateTitle={Liferay.Language.get(
				'no-accounts-synced-from-data-sources'
			)}
			entityLabel={Liferay.Language.get('accounts')}
			noResultsConfig={{
				description: (
					<>
						{Liferay.Language.get(
							'connect-a-data-source-to-get-started'
						)}

						<ClayLink
							className='d-block mb-3'
							href={URLConstants.DataSourceConnection}
							key='DOCUMENTATION'
							target='_blank'
						>
							{Liferay.Language.get(
								'access-our-documentation-to-learn-more'
							)}
						</ClayLink>

						{authorized && (
							<ClayLink
								button
								className='button-root'
								displayType='primary'
								href={toRoute(Routes.SETTINGS_ADD_DATA_SOURCE, {
									groupId
								})}
							>
								{Liferay.Language.get('connect-data-source')}
							</ClayLink>
						)}
					</>
				),
				icon: {
					border: false,
					size: Sizes.XXXLarge,
					symbol: 'ac_satellite'
				},
				title: Liferay.Language.get(
					'no-accounts-synced-from-data-sources'
				)
			}}
			orderByOptions={[
				{
					label: Liferay.Language.get('account-name'),
					value: NAME
				},
				{
					label: Liferay.Language.get('account-type'),
					value: ACCOUNT_TYPE
				},
				{
					label: Liferay.Language.get('individuals'),
					value: INDIVIDUAL_COUNT
				},
				{
					label: Liferay.Language.get('total-activities'),
					value: ACTIVITIES_COUNT
				}
			]}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			rowIdentifier='id'
		/>
	);
};

export default List;
