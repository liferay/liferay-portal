import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import Nav from 'shared/components/Nav';
import React from 'react';
import RoleRenderer from '../../components/user-list/RoleRenderer';
import StatusRenderer from '../../components/user-list/StatusRenderer';
import UserActionsRenderer from '../../components/user-list/UserActionsRenderer';
import {
	ActionTypes,
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {
	createOrderIOMap,
	EMAIL_ADDRESS,
	NAME,
	ROLE_NAME,
	STATUS,
} from 'shared/util/pagination';
import {getDisplayRole, getPluralMessage, sub} from 'shared/util/lang';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {User} from 'shared/util/records';
import {useRequest} from 'shared/hooks/useRequest';
import {UserRoleNames, UserStatuses} from 'shared/util/constants';

const userRoleOptions = [UserRoleNames.Member, UserRoleNames.Administrator].map(
	(role) => ({
		label: getDisplayRole(role),
		value: role,
	})
);

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IUserListProps extends PropsFromRedux {
	currentUser: User;
	groupId: string;
}

const UserList: React.FC<IUserListProps> = ({
	addAlert,
	close,
	currentUser,
	groupId,
	open,
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, filterBy, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.user.fetchMany,
		variables: {
			delta,
			groupId,
			orderIOMap,
			page,
			query,
			status: [UserStatuses.Approved, UserStatuses.Pending],
		},
	});

	const handleActions = () => {
		open(modalTypes.BATCH_ACTION_MODAL, {
			actionOptions: {
				actionCountString: Liferay.Language.get(
					'changing-permissions-for-x-users'
				),
				options: userRoleOptions,
				optionsLabel: Liferay.Language.get('select-permission'),
			},
			columns: [
				{
					accessor: 'name',
					className: 'table-cell-expand',
					label: Liferay.Language.get('name'),
					sortable: false,
					title: true,
				},
				{
					accessor: 'emailAddress',
					label: Liferay.Language.get('email'),
					sortable: false,
				},
				{
					accessor: 'status',
					cellRenderer: StatusRenderer,
					label: Liferay.Language.get('status'),
					sortable: false,
				},
				{
					accessor: 'roleName',
					cellRenderer: RoleRenderer,
					label: Liferay.Language.get('permission'),
					sortable: false,
				},
			],
			editableAttr: 'roleName',
			fitContent: true,
			items: selectedItems.toArray(),
			onClose: close,
			onSave: handleUserSave,
			title: Liferay.Language.get('edit-permissions-for-selected-users'),
		});
	};

	const handleInviteModal = () => {
		open(modalTypes.INVITE_USERS_MODAL, {
			onClose: close,
			onSubmit: handleUserInvite,
		});
	};

	const handleUserDelete = (ids: string[]) => {
		open(modalTypes.CONFIRMATION_MODAL, {
			message: getPluralMessage(
				Liferay.Language.get('are-you-sure-you-want-to-delete-x-user'),
				Liferay.Language.get('are-you-sure-you-want-to-delete-x-users'),
				ids.length,
				false,
				[<b key="confirmDeleteCount">{ids.length}</b>]
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: () =>
				API.user
					.delete({
						groupId,
						ids,
					})
					.then((data) => {
						addAlert({
							alertType: Alert.Types.Success,
							message: getPluralMessage(
								Liferay.Language.get('x-user-has-been-deleted'),
								Liferay.Language.get(
									'x-users-have-been-deleted'
								),
								data?.length,
								false,
								[<b key="deleteCount">{data?.length}</b>]
							),
						});

						selectionDispatch?.({type: ActionTypes.ClearAll});

						refetch?.();
					})
					.catch((err) =>
						addAlert({
							alertType: Alert.Types.Error,
							message:
								err.message === UNAUTHORIZED_ACCESS
									? Liferay.Language.get(
											'unauthorized-access'
										)
									: Liferay.Language.get('error'),
							timeout: false,
						})
					),
			submitButtonDisplay: 'warning',
			title: Liferay.Language.get('delete-user'),
			titleIcon: 'warning-full',
		});
	};

	const handleUserInvite = (emailAddresses: string[]) =>
		API.user
			.inviteMany({emailAddresses, groupId, roleName: 'Site Member'})
			.then((response) => {
				addAlert({
					alertType: Alert.Types.Success,
					message: Liferay.Language.get('invitations-have-been-sent'),
				});

				refetch?.();

				close();

				return response;
			})
			.catch(() => {
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get('error'),
				});
			});

	const handleUserSave = ({
		edits,
		ids,
	}: {
		edits: {[key: string]: any};
		ids: string[];
	}) =>
		API.user
			.updateMany({...edits, groupId, ids} as {
				groupId: string;
				ids: string[];
				roleName: string;
			})
			.then((data) => {
				addAlert({
					alertType: Alert.Types.Success,
					message: sub(
						Liferay.Language.get(
							'permissions-have-been-changed-for-x-users'
						),
						[<b key="changedCount">{data.length}</b>],
						false
					),
				});

				selectionDispatch?.({type: ActionTypes.ClearAll});

				refetch?.();
			})
			.catch((err) =>
				addAlert({
					alertType: Alert.Types.Error,
					message:
						err.message === UNAUTHORIZED_ACCESS
							? Liferay.Language.get('unauthorized-access')
							: Liferay.Language.get('error'),
				})
			);

	const isUserDisabled = (user?: object) => {
		const typedUser = user as {id: string};
		const userRow = new User(typedUser);

		return (
			!currentUser.isAdmin() ||
			userRow.isOwner() ||
			typedUser?.id === currentUser.id
		);
	};

	const renderInlineRowActions = ({
		data,
		editing,
		edits,
		itemsSelected,
		rowEvents,
	}: {
		data: any;
		editing: boolean;
		edits: {[key: string]: any};
		itemsSelected: boolean;
		rowEvents: {[key: string]: any};
	}) => (

		/* eslint-disable react/jsx-handler-names */
		<UserActionsRenderer
			currentUserId={currentUser.id}
			data={new User(data)}
			editing={editing}
			edits={edits}
			itemsSelected={itemsSelected}
			{...rowEvents}
			onUserDelete={handleUserDelete}
			onUserSave={handleUserSave}
		/>

		/* eslint-enable react/jsx-handler-names */
	);

	const renderNav = () => {
		if (selectedItems.isEmpty()) {
			return (
				<Nav>
					<Nav.Item>
						<ClayButton
							className="button-root nav-btn p-2"
							displayType="primary"
							onClick={handleInviteModal}
						>
							{Liferay.Language.get('invite-users')}
						</ClayButton>
					</Nav.Item>
				</Nav>
			);
		}

		return (
			<Nav>
				<ClayButton
					borderless
					className="button-root"
					displayType="secondary"
					onClick={handleActions}
					outline
				>
					{Liferay.Language.get('change-permissions')}
				</ClayButton>

				<ClayButton
					aria-label={Liferay.Language.get('delete')}
					borderless
					className="button-root"
					displayType="secondary"
					onClick={() =>
						handleUserDelete(selectedItems.keySeq().toArray())
					}
					outline
				>
					<ClayIcon className="icon-root" symbol="trash" />
				</ClayButton>
			</Nav>
		);
	};

	const authorized = currentUser.isAdmin();

	return (
		<CrossPageSelect
			checkDisabled={isUserDisabled}
			columns={[
				{
					accessor: 'name',
					className: 'table-cell-expand',
					label: Liferay.Language.get('name'),
					title: true,
				},
				{
					accessor: 'emailAddress',
					label: Liferay.Language.get('email'),
				},
				{
					accessor: 'status',
					cellRenderer: StatusRenderer,
					label: Liferay.Language.get('status'),
				},
				{
					accessor: 'roleName',
					cellRenderer: RoleRenderer,
					cellRendererProps: {
						options: userRoleOptions,
					},
					editable: true,
					label: Liferay.Language.get('permission'),
				},
			]}
			delta={delta}
			emptyTitle={sub(Liferay.Language.get('no-x-were-found'), [
				Liferay.Language.get('users'),
			])}
			entityLabel={Liferay.Language.get('users')}
			error={error}
			filterBy={filterBy}
			items={data?.items}
			loading={loading}
			orderByOptions={[
				{
					label: Liferay.Language.get('name'),
					value: NAME,
				},
				{
					label: Liferay.Language.get('email'),
					value: EMAIL_ADDRESS,
				},
				{
					label: Liferay.Language.get('permission'),
					value: ROLE_NAME,
				},
				{
					label: Liferay.Language.get('status'),
					value: STATUS,
				},
			]}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			renderInlineRowActions={authorized ? renderInlineRowActions : null}
			renderNav={authorized ? renderNav : null}
			showCheckbox={authorized}
			total={data?.total}
		/>
	);
};

export default compose<React.ComponentType<any>>(
	connector,
	withSelectionProvider
)(UserList);
