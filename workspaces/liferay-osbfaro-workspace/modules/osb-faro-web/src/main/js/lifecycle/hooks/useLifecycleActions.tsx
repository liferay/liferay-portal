import * as API from 'shared/api';
import ManageLifecycleNotificationsModal from 'shared/components/modals/ManageLifecycleNotificationsModal';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {getLifecycleSaveErrorMessage} from 'lifecycle/utils/lifecycleErrors';
import {ILifecycle} from 'shared/api/lifecycle';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDispatch} from 'react-redux';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useModal} from '@clayui/modal';
import {useParams} from 'react-router-dom';

interface ILifecycleAction {
	className?: string;
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
	onDelete?: (lifecycle: ILifecycle) => void;
	onShowDetails?: () => void;
	onView?: () => void;
}

export const useLifecycleActions = ({
	lifecycle,
	onDefaultChange,
	onDelete,
	onShowDetails,
	onView,
}: IUseLifecycleActionsOptions): IUseLifecycleActionsResult => {
	const currentUser = useCurrentUser();

	const notificationsModal = useModal();

	const dispatch = useDispatch();

	const history = useHistoryAdapter();

	const {channelId, groupId, lifecycleId: openLifecycleId} = useParams();

	if (!lifecycle) {
		return {actions: [], manageNotificationsModal: null};
	}

	const lifecycleId = lifecycle.id;

	const lifecycleERC = lifecycle.externalReferenceCode;

	const lifecycleName = lifecycle.name || lifecycleId;

	const isDefault = lifecycle.defaultAccountLifecycle;

	const authorized = currentUser.isAdmin();

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

	const removeLifecycle = () =>
		API.lifecycle
			.deleteLifecycle({channelId, groupId: groupId!, lifecycleId})
			.then(
				() => {
					dispatch(
						addAlert({
							alertType: Alert.Types.Success,
							message: sub(
								Liferay.Language.get('x-was-deleted'),
								[lifecycleName]
							) as string,
						})
					);

					if (lifecycleERC === openLifecycleId) {
						history.replace(
							toRoute(Routes.LIFECYCLE, {channelId, groupId})
						);
					}
					else {
						onDelete?.(lifecycle);
					}
				},
				() =>
					dispatch(
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get(
								'the-delete-process-failed-to-complete.-try-again-later'
							),
						})
					)
			);

	const confirmDelete = () =>
		dispatch(
			open(modalTypes.CONFIRMATION_MODAL, {
				message: sub(
					Liferay.Language.get(
						'you-are-about-to-delete-the-lifecycle-x,-but-its-underlying-account-data-will-not-be-removed-from-the-workspace.-this-action-is-permanent-and-cannot-be-undone.-are-you-sure-you-want-to-proceed'
					),
					[lifecycleName]
				),
				modalVariant: 'modal-danger',
				onClose: () => dispatch(close()),
				onSubmit: removeLifecycle,
				submitButtonDisplay: 'danger',
				submitMessage: Liferay.Language.get('delete'),
				title: sub(Liferay.Language.get('delete-lifecycle-x?'), [
					lifecycleName,
				]),
				titleIcon: 'exclamation-full',
			})
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
		...(authorized
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
		...(isDefault === false
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
		...(authorized
			? [
					{
						className: 'text-danger',
						icon: {symbol: 'trash'},
						label: Liferay.Language.get('delete'),
						onClick: confirmDelete,
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
