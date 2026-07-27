import * as API from 'shared/api';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import ListComponent from 'shared/hoc/ListComponent';
import React from 'react';
import RequestActionsRenderer from 'settings/components/user-list/RequestActionsRenderer';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose, withAdminPermission} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {createOrderIOMap, EMAIL_ADDRESS, NAME} from 'shared/util/pagination';
import {sub} from 'shared/util/lang';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {UserStatuses} from 'shared/util/constants';

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IUserRequestProps extends PropsFromRedux {
	groupId: string;
	onSetUserRequest: (userRequest: number) => void;
}

const UserRequest: React.FC<IUserRequestProps> = ({
	close,
	groupId,
	onSetUserRequest,
	open,
}) => {
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: ({delta, groupId, orderIOMap, page, query, statuses}) =>
			API.user
				.fetchMany({delta, groupId, orderIOMap, page, query, statuses})
				.then((data) => {
					onSetUserRequest(data.total);

					return data;
				}),
		variables: {
			delta,
			groupId,
			orderIOMap,
			page,
			query,
			statuses: [UserStatuses.Requested],
		},
	});

	const onAccept = ({
		emailAddress,
		id,
	}: {
		emailAddress: string;
		id: string;
	}) => {
		open(modalTypes.CONFIRMATION_MODAL, {
			message: sub(
				Liferay.Language.get('are-you-sure-you-want-to-accept-x'),
				[<b key="acceptCount">{emailAddress}</b>],
				false
			),
			modalVariant: 'modal-info',
			onClose: close,
			onSubmit: () =>
				API.user
					.accept({groupId, id})
					.then(() => {
						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get('user-added'),
						});

						refetch?.();
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get('error'),
						});
					}),
			title: Liferay.Language.get('accept'),
		});
	};

	const onDecline = ({
		emailAddress,
		id,
	}: {
		emailAddress: string;
		id: string;
	}) => {
		open(modalTypes.CONFIRMATION_MODAL, {
			message: sub(
				Liferay.Language.get('are-you-sure-you-want-to-decline-x'),
				[<b key="declineCount">{emailAddress}</b>],
				false
			),
			modalVariant: 'modal-info',
			onClose: close,
			onSubmit: () =>
				API.user
					.delete({groupId, ids: [id]})
					.then(() => {
						addAlert({
							alertType: Alert.Types.Default,
							message: Liferay.Language.get(
								'user-request-to-join-denied'
							),
						});

						refetch?.();
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get('error'),
						});
					}),
			title: Liferay.Language.get('decline'),
		});
	};

	return (
		<CrossPageSelect
			columns={[
				{
					accessor: 'name',
					label: Liferay.Language.get('name'),
					title: true,
				},
				{
					accessor: 'emailAddress',
					label: Liferay.Language.get('email'),
				},
				{
					accessor: 'status',
					cellRenderer: RequestActionsRenderer,
					cellRendererProps: {
						onAccept,
						onDecline,
					},
					className: 'text-right',
				},
			]}
			delta={delta}
			emptyMessage={sub(Liferay.Language.get('no-x-were-found'), [
				Liferay.Language.get('users'),
			])}
			entityLabel={Liferay.Language.get('users')}
			error={error}
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
			]}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			showCheckbox={false}
			total={data?.total}
		>
			{(props: any) => <ListComponent {...props} />}
		</CrossPageSelect>
	);
};

export default compose<any>(withAdminPermission, connector)(UserRequest);
