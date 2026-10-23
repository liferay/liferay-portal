import * as API from 'shared/api';
import ManageLifecycleNotificationsModal from 'shared/components/modals/ManageLifecycleNotificationsModal';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {getLifecycleSaveErrorMessage} from 'lifecycle/utils/lifecycleErrors';
import {ILifecycle} from 'shared/api/lifecycle';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDispatch} from 'react-redux';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useModal} from '@clayui/modal';
import {useParams} from 'react-router-dom';

interface ILifecycleAction {
	icon: {symbol: string};
	label: string;
	onClick: () => void;
}

interface IUseLifecycleActionsResult {
	actions: ILifecycleAction[];
	manageNotificationsModal: React.ReactNode;
}

interface IUseLifecycleActionsOptions {
	lifecycle?: ILifecycle | null;
	onDefaultChange?: () => void;
	onShowDetails?: () => void;
	onView?: () => void;
}

export const useLifecycleActions = ({
	lifecycle,
	onDefaultChange,
	onShowDetails,
	onView,
}: IUseLifecycleActionsOptions): IUseLifecycleActionsResult => {
	const currentUser = useCurrentUser();

	const notificationsModal = useModal();

	const dispatch = useDispatch();

	const history = useHistoryAdapter();

	const {channelId, groupId} = useParams();

	if (!lifecycle) {
		return {actions: [], manageNotificationsModal: null};
	}

	const lifecycleId = lifecycle.id;

	const lifecycleERC = lifecycle.externalReferenceCode;

	const setDefault = () =>
		API.lifecycle
			.setDefaultLifecycle({groupId: groupId!, lifecycleId})
			.then(() => {
				dispatch(
					addAlert({
						alertType: Alert.Types.Success,
						message: Liferay.Language.get(
							'the-default-lifecycle-was-updated-successfully'
						),
					})
				);

				onDefaultChange?.();
			})
			.catch((error) =>
				dispatch(
					addAlert({
						alertType: Alert.Types.Error,
						message: getLifecycleSaveErrorMessage(error),
					})
				)
			);

	const actions = [
		...(onView
			? [
					{
						icon: {symbol: 'view'},
						label: Liferay.Language.get('view'),
						onClick: () => {
							history.push(
								toRoute(Routes.LIFECYCLE_DASHBOARD, {
									channelId,
									groupId,
									lifecycleId: lifecycleERC,
								})
							);

							onView();
						},
					},
				]
			: []),
		...(currentUser.isAdmin()
			? [
					{
						icon: {symbol: 'pencil'},
						label: Liferay.Language.get('edit'),
						onClick: () =>
							history.push(
								toRoute(Routes.LIFECYCLE_EDIT, {
									channelId,
									groupId,
									lifecycleId: lifecycleERC,
								})
							),
					},
				]
			: []),
		{
			icon: {symbol: 'bell-on'},
			label: Liferay.Language.get('manage-notifications'),
			onClick: () => notificationsModal.onOpenChange(true),
		},
		...(lifecycle.defaultAccountLifecycle === false
			? [
					{
						icon: {symbol: 'pin'},
						label: Liferay.Language.get('set-default'),
						onClick: setDefault,
					},
				]
			: []),
		...(onShowDetails
			? [
					{
						icon: {symbol: 'info-circle'},
						label: Liferay.Language.get('show-details'),
						onClick: onShowDetails,
					},
				]
			: []),
	];

	return {
		actions,
		manageNotificationsModal: notificationsModal.open ? (
			<ManageLifecycleNotificationsModal
				groupId={groupId!}
				lifecycleId={lifecycleId}
				observer={notificationsModal.observer}
				onClose={() => notificationsModal.onOpenChange(false)}
			/>
		) : null,
	};
};
