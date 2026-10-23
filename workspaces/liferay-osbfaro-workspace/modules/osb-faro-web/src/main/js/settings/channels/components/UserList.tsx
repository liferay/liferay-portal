import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import ConfirmationModal from 'shared/components/ConfirmationModal';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useState} from 'react';
import RowActions from 'shared/components/RowActions';
import SearchableTableModal from 'shared/components/modals/SearchableTableModal';
import Table from 'shared/components/table';
import {ActionTypes} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
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
import {useModal} from '@clayui/modal';
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
					className="button-root nav-btn"
					displayType="secondary"
					onClick={() => onRemoveUser(selectedItems)}
				>
					{
						<>
							<ClayIcon
								className="icon-root mr-2 remove-users"
								symbol="times"
							/>
							{Liferay.Language.get('remove')}
						</>
					}
				</ClayButton>
			) : (
				<ClayButton
					className="button-root nav-btn px-3"
					displayType="primary"
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
			'timeZoneId',
		]),
	}),
	{addAlert}
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
	groupId,
	id,
	propertyName,
	timeZoneId,
	...otherProps
}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.channels.fetchUsers,
		variables: {
			channelId: id,
			delta,
			groupId,
			orderIOMap,
			page,
			query,
		},
	});

	const addUsersModal = useModal();
	const noUsersModal = useModal();
	const removeUsersModal = useModal();

	const [usersToRemove, setUsersToRemove] = useState<{
		clearSelection?: (params: {type: string}) => void;
		users: User[];
	}>();

	const clearSelection = usersToRemove?.clearSelection;
	const removedUsers = usersToRemove?.users ?? [];
	const removedUserIds = removedUsers.map(({userId}) => userId);

	const getEmailOrCount = (users: User[]): number => {
		const count = users.length;

		return count === 1 ? users[0].emailAddress : count;
	};

	const getRemoveUserModalFn =
		(clearAll?: (params: {type: string}) => void) =>
		(selectedIOMap: any) => {
			setUsersToRemove({
				clearSelection: clearAll,
				users: selectedIOMap.valueSeq().toArray(),
			});

			removeUsersModal.onOpenChange(true);
		};

	const getAddUserModalProps = () => ({
		columns: [
			usersListColumns.nameEmailAddress,
			usersListColumns.getLastLoginDate(timeZoneId),
		],
		dataSourceFn: ({
			delta,
			orderIOMap,
			page,
			query,
		}: {
			[key: string]: any;
		}) =>
			API.channels.fetchUsers({
				available: true,
				channelId: id,
				delta,
				groupId,
				orderIOMap,
				page,
				query,
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
			addUsersModal.onOpenChange(false);

			if (!get(data, 'total')) {
				handleNoUsersInPropertyModal();
			}
		},
		onSubmit: (selectedIOMap: any) => {
			const users = selectedIOMap.valueSeq().toArray();

			const userIds = users.map(({userId}: {userId: string}) => userId);

			API.channels
				.addUsers({
					channelId: id,
					groupId,
					userIds,
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
						) as string,
					});

					addUsersModal.onOpenChange(false);
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
						timeout: false,
					});
				});
		},
		requireSelection: true,
		title: propertyName,
	});

	const handleAddUserModal = () => {
		addUsersModal.onOpenChange(true);
	};

	const handleNoUsersInPropertyModal = () => {
		noUsersModal.onOpenChange(true);
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
						<span className="text-secondary">
							{Liferay.Language.get(
								'add-users-to-give-them-access-to-this-property'
							)}
						</span>
					}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite',
					}}
					primary
					spacer
					title={Liferay.Language.get('no-users-added')}
				/>
			),
			orderIOMap,
			pageDisplay: true,
			rowIdentifier: 'id',
			total: get(data, 'total'),
		};

		if (loading) {
			return <Loading />;
		}
		else if (error) {
			return <ErrorDisplay onReload={refetch} spacer />;
		}
		else if (authorized) {
			return (
				<SelectionProvider>
					<CrossPageSelect
						{...sharedProps}
						renderRowActions={({data}: {data: User}) => {
							const rowAction = {
								'data-testid': 'delete-user',
								label: Liferay.Language.get('delete'),
								onClick: () =>
									getRemoveUserModalFn()(
										OrderedMap({[data.userId]: data})
									),
							};

							return authorized ? (
								<RowActions
									actions={[rowAction]}
									quickActions={[
										{
											iconSymbol: 'times',
											...rowAction,
										},
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
						}: {
							selectedItems: any;
							selectionDispatch: any;
							[key: string]: any;
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
		}
		else {

			// TODO: Can we just use shoCheckbox isntead of this?

			return <ListComponent {...sharedProps} />;
		}
	};

	return (
		<>
			{renderContent()}

			{addUsersModal.open && (
				<SearchableTableModal
					{...getAddUserModalProps()}
					observer={addUsersModal.observer}
				/>
			)}

			{removeUsersModal.open && (
				<ConfirmationModal
					closeAfterSubmit={false}
					message={
						<div className="text-secondary">
							{
								getPluralMessage(
									Liferay.Language.get(
										'removing-x-from-this-property.-they-will-need-to-be-added-again-to-regain-access-to-this-property'
									),
									Liferay.Language.get(
										'removing-x-users-from-this-property.-they-will-need-to-be-added-again-to-regain-access-to-this-property'
									),
									removedUserIds.length,
									true,
									[
										getEmailOrCount(removedUsers),
										propertyName,
									]
								) as string
							}
						</div>
					}
					observer={removeUsersModal.observer}
					onClose={() => removeUsersModal.onOpenChange(false)}
					onSubmit={() => {
						API.channels
							.deleteUsers({
								channelId: id,
								groupId,
								userIds: removedUserIds,
							})
							.then(() => {
								if (clearSelection) {
									clearSelection({
										type: ActionTypes.ClearAll,
									});
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
										removedUserIds.length,
										true,
										[
											getEmailOrCount(removedUsers),
											propertyName,
										]
									) as string,
								});

								removeUsersModal.onOpenChange(false);
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
										removedUserIds.length,
										true,
										[
											getEmailOrCount(removedUsers),
											propertyName,
										]
									) as string,
									timeout: false,
								});
							});
					}}
					submitButtonDisplay="warning"
					submitMessage={Liferay.Language.get('remove')}
					title={Liferay.Language.get('remove-user')}
					type="warning"
				/>
			)}

			{noUsersModal.open && (
				<ConfirmationModal
					closeAfterSubmit={false}
					message={
						<div className="text-secondary">
							{Liferay.Language.get(
								'property-permissions-have-changed-without-any-users-selected.-do-you-want-to-close-the-modal-without-adding-users'
							)}
						</div>
					}
					observer={noUsersModal.observer}
					onClose={() => {
						noUsersModal.onOpenChange(false);

						handleAddUserModal();
					}}
					onSubmit={() => noUsersModal.onOpenChange(false)}
					submitButtonDisplay="warning"
					submitMessage={Liferay.Language.get('close')}
					title={Liferay.Language.get('no-users-in-property')}
					type="warning"
				/>
			)}
		</>
	);
};

export default connector(UserList);
