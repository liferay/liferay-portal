import * as API from 'shared/api';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import ListComponent from 'shared/hoc/ListComponent';
import ConfirmationModal from 'shared/components/ConfirmationModal';
import React, {useState} from 'react';
import RequestActionsRenderer from 'settings/components/user-list/RequestActionsRenderer';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {compose, withAdminPermission} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {createOrderIOMap, EMAIL_ADDRESS, NAME} from 'shared/util/pagination';
import {sub} from 'shared/util/lang';
import {useModal} from '@clayui/modal';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {UserStatuses} from 'shared/util/constants';

interface IRequest {
	emailAddress: string;
	id: string;
}

const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IUserRequestProps extends PropsFromRedux {
	groupId: string;
	onSetUserRequest: (userRequest: number) => void;
}

const UserRequest: React.FC<IUserRequestProps> = ({
	groupId,
	onSetUserRequest,
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

	const acceptModal = useModal();
	const declineModal = useModal();

	const [acceptTarget, setAcceptTarget] = useState<IRequest>();
	const [declineTarget, setDeclineTarget] = useState<IRequest>();

	const onAccept = (request: IRequest) => {
		setAcceptTarget(request);

		acceptModal.onOpenChange(true);
	};

	const onDecline = (request: IRequest) => {
		setDeclineTarget(request);

		declineModal.onOpenChange(true);
	};

	return (
		<>
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

			{acceptModal.open && acceptTarget && (
				<ConfirmationModal
					message={sub(
						Liferay.Language.get(
							'are-you-sure-you-want-to-accept-x'
						),
						[<b key="acceptCount">{acceptTarget.emailAddress}</b>],
						false
					)}
					observer={acceptModal.observer}
					onClose={() => acceptModal.onOpenChange(false)}
					onSubmit={() =>
						API.user
							.accept({groupId, id: acceptTarget.id})
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
							})
					}
					title={Liferay.Language.get('accept')}
					type="info"
				/>
			)}

			{declineModal.open && declineTarget && (
				<ConfirmationModal
					message={sub(
						Liferay.Language.get(
							'are-you-sure-you-want-to-decline-x'
						),
						[
							<b key="declineCount">
								{declineTarget.emailAddress}
							</b>,
						],
						false
					)}
					observer={declineModal.observer}
					onClose={() => declineModal.onOpenChange(false)}
					onSubmit={() =>
						API.user
							.delete({groupId, ids: [declineTarget.id]})
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
							})
					}
					title={Liferay.Language.get('decline')}
					type="info"
				/>
			)}
		</>
	);
};

export default compose<any>(withAdminPermission, connector)(UserRequest);
