import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import RowActions from 'shared/components/RowActions';
import Table from 'shared/components/table';
import {ActionTypes} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose, withPaginationBar, withToolbar} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {get} from 'lodash';
import {getPluralMessage} from 'shared/util/lang';
import {IPaginationUnsorted} from 'shared/types';
import {OrderedMap} from 'immutable';
import {RootState} from 'shared/store';
import {SelectionProvider} from 'shared/context/selection';
import {Sizes} from 'shared/util/constants';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {User} from 'shared/util/records';
import {useRequest} from 'shared/hooks/useRequest';
import {usersListColumns} from 'shared/util/table-columns';
import {withEmpty} from 'cerebro-shared/hocs/utils';

const ListComponent = compose<any>(
	withToolbar(),
	withPaginationBar(),
	withEmpty()
)(Table);

const UserListNav: React.FC<{
	onAddUser: () => void;
	onRemoveUser: (selectedItems: OrderedMap<string, Object>) => void;
	selectedItems: OrderedMap<string, Object>;
}> = ({onAddUser, onRemoveUser, selectedItems}) => (
	<Nav>
		<Nav.Item>
			{selectedItems.size ? (
				<ClayButton
					className='button-root nav-btn'
					displayType='secondary'
					onClick={() => onRemoveUser(selectedItems)}
				>
					{
						<>
							<ClayIcon
								className='icon-root mr-2 remove-users'
								symbol='times'
							/>
							{Liferay.Language.get('remove')}
						</>
					}
				</ClayButton>
			) : (
				<ClayButton
					className='button-root nav-btn px-3'
					displayType='primary'
					onClick={onAddUser}
				>
					{Liferay.Language.get('add-user')}
				</ClayButton>
			)}
		</Nav.Item>
	</Nav>
);
const connector = connect(
	(store: RootState, {groupId}: {groupId: string}) => ({
		timeZoneId: store.getIn([
			'projects',
			groupId,
			'data',
			'timeZone',
			'timeZoneId'
		])
	}),
	{addAlert, close, open}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IUserListProps extends PropsFromRedux, IPaginationUnsorted {
	authorized: boolean;
	groupId: string;
	id: string;
	propertyName: string;
}

const UserList: React.FC<IUserListProps> = ({
	addAlert,
	authorized,
	close,
	groupId,
	id,
	open,
	propertyName,
	timeZoneId,
	...otherProps
}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.channels.fetchUsers,
		variables: {
			channelId: id,
			cur: page,
			delta,
			groupId,
			orderIOMap,
			query
		}
	});

	const getEmailOrCount = (users: User[]): number => {
		const count = users.length;

		return count === 1 ? users[0].emailAddress : count;
	};

	const getRemoveUserModalFn = (
		clearAll?: (object) => void
	) => selectedIOMap => {
		const users = selectedIOMap.valueSeq().toArray();

		const userIds = users.map(({userId}) => userId);

		open(modalTypes.CONFIRMATION_MODAL, {
			closeAfterSubmit: false,
			message: (
				<div className='text-secondary'>
					{
						getPluralMessage(
							Liferay.Language.get(
								'removing-x-from-this-property.-they-will-need-to-be-added-again-to-regain-access-to-this-property'
							),
							Liferay.Language.get(
								'removing-x-users-from-this-property.-they-will-need-to-be-added-again-to-regain-access-to-this-property'
							),
							userIds.length,
							true,
							[getEmailOrCount(users), propertyName]
						) as string
					}
				</div>
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: () => {
				API.channels
					.deleteUsers({
						channelId: id,
						groupId,
						userIds
					})
					.then(() => {
						if (clearAll) {
							clearAll({type: ActionTypes.ClearAll});
						}

						refetch();

						addAlert({
							alertType: Alert.Types.Success,
							message: getPluralMessage(
								Liferay.Language.get(
									'x-has-been-removed-from-x'
								),
								Liferay.Language.get(
									'x-users-have-been-removed-from-x'
								),
								userIds.length,
								true,
								[getEmailOrCount(users), propertyName]
							) as string
						});

						close();
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: getPluralMessage(
								Liferay.Language.get(
									'there-was-an-error-removing-x-from-x'
								),
								Liferay.Language.get(
									'there-was-an-error-removing-x-users-from-x'
								),
								userIds.length,
								true,
								[getEmailOrCount(users), propertyName]
							) as string,
							timeout: false
						});
					});
			},
			showFilterAndOrder: false,
			submitButtonDisplay: 'warning',
			submitMessage: Liferay.Language.get('remove'),
			title: Liferay.Language.get('remove-user'),
			titleIcon: 'warning-full'
		});
	};

	const handleAddUserModal = () => {
		open(modalTypes.SEARCHABLE_TABLE_MODAL, {
			columns: [
				usersListColumns.nameEmailAddress,
				usersListColumns.getLastLoginDate(timeZoneId)
			],
			dataSourceFn: ({delta, orderIOMap, page, query}) =>
				API.channels.fetchUsers({
					available: true,
					channelId: id,
					delta,
					groupId,
					orderIOMap,
					page,
					query
				}),
			entityLabel: Liferay.Language.get('users'),
			initialOrderIOMap: createOrderIOMap(NAME),
			instruction: Liferay.Language.get(
				'select-users-to-add-to-this-property.-users-must-be-previously-invited-to-workspace-to-add-to-a-property'
			),
			noResultsDescription: Liferay.Language.get(
				'all-users-in-this-workspace-have-already-been-added-to-this-property'
			),
			noResultsIcon: null,
			noResultsTitle: Liferay.Language.get('no-users-to-add'),
			onClose: () => {
				close();

				if (!get(data, 'total')) {
					handleNoUsersInPropertyModal();
				}
			},
			onSubmit: selectedIOMap => {
				const users = selectedIOMap.valueSeq().toArray();

				const userIds = users.map(({userId}) => userId);

				API.channels
					.addUsers({
						channelId: id,
						groupId,
						userIds
					})
					.then(() => {
						refetch();

						addAlert({
							alertType: Alert.Types.Success,
							message: getPluralMessage(
								Liferay.Language.get(
									'x-has-been-added-as-a-user-to-x'
								),
								Liferay.Language.get(
									'x-users-have-been-added-to-x'
								),
								userIds.length,
								true,
								[getEmailOrCount(users), propertyName]
							) as string
						});

						close();
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: getPluralMessage(
								Liferay.Language.get(
									'there-was-an-error-adding-x-as-a-user-to-x'
								),
								Liferay.Language.get(
									'there-was-an-error-adding-x-users-to-x'
								),
								userIds.length,
								true,
								[getEmailOrCount(users), propertyName]
							) as string,
							timeout: false
						});
					});
			},
			requireSelection: true,
			title: propertyName
		});
	};

	const handleNoUsersInPropertyModal = () => {
		open(modalTypes.CONFIRMATION_MODAL, {
			closeAfterSubmit: false,
			message: (
				<div className='text-secondary'>
					{Liferay.Language.get(
						'property-permissions-have-changed-without-any-users-selected.-do-you-want-to-close-the-modal-without-adding-users'
					)}
				</div>
			),
			modalVariant: 'modal-warning',
			onClose: () => {
				close();

				handleAddUserModal();
			},
			onSubmit: close,
			submitButtonDisplay: 'warning',
			submitMessage: Liferay.Language.get('close'),
			title: Liferay.Language.get('no-users-in-property'),
			titleIcon: 'warning-full'
		});
	};

	const renderContent = () => {
		const sharedProps = {
			columns: [usersListColumns.name, usersListColumns.emailAddress],
			delta,
			disableSearch: data?.disableSearch,
			empty: data?.total,
			items: data?.items,
			loading,
			noResultsRenderer: (
				<NoResultsDisplay
					description={
						<span className='text-secondary'>
							{Liferay.Language.get(
								'add-users-to-give-them-access-to-this-property'
							)}
						</span>
					}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite'
					}}
					primary
					spacer
					title={Liferay.Language.get('no-users-added')}
				/>
			),
			orderIOMap,
			pageDisplay: true,
			rowIdentifier: 'id',
			total: get(data, 'total')
		};

		if (loading) {
			return <Loading />;
		} else if (error) {
			return <ErrorDisplay onReload={refetch} spacer />;
		} else if (authorized) {
			return (
				<SelectionProvider>
					<CrossPageSelect
						{...sharedProps}
						renderRowActions={({data}) => {
							const rowAction = {
								'data-testid': 'delete-user',
								label: Liferay.Language.get('delete'),
								onClick: () =>
									getRemoveUserModalFn()(
										OrderedMap({[data.userId]: data})
									)
							};

							return authorized ? (
								<RowActions
									actions={[rowAction]}
									quickActions={[
										{
											iconSymbol: 'times',
											...rowAction
										}
									]}
								/>
							) : null;
						}}
						{...otherProps}
					>
						{({
							selectedItems,
							selectionDispatch,
							...otherProps
						}) => (
							<ListComponent
								renderNav={() => (
									<UserListNav
										onAddUser={handleAddUserModal}
										onRemoveUser={getRemoveUserModalFn(
											selectionDispatch
										)}
										selectedItems={selectedItems}
									/>
								)}
								{...otherProps}
							/>
						)}
					</CrossPageSelect>
				</SelectionProvider>
			);
		} else {
			// TODO: Can we just use shoCheckbox isntead of this?
			return <ListComponent {...sharedProps} />;
		}
	};

	return renderContent();
};

export default connector(UserList);
