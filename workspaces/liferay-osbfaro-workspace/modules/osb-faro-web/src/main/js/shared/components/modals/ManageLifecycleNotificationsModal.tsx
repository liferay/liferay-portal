import * as API from 'shared/api';
import NotificationSettingsModal, {
	NotificationSettings,
} from './NotificationSettingsModal';
import React from 'react';
import {Frequency} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

interface IManageLifecycleNotificationsModalProps {
	groupId: string;
	lifecycleId: string;
	onClose: ModalTypes.close;
}

const CHECKBOXES = [
	{
		label: Liferay.Language.get('new-accounts'),
		name: 'newAccounts',
	},
	{
		label: Liferay.Language.get('account-stage-changes'),
		name: 'accountStageChanges',
	},
	{
		label: Liferay.Language.get('net-new-pipeline-accounts'),
		name: 'netNewPipelineAccounts',
	},
	{
		label: Liferay.Language.get('new-stalled-accounts'),
		name: 'newStalledAccounts',
	},
	{
		label: Liferay.Language.get('new-at-risk-accounts'),
		name: 'newAtRiskAccounts',
	},
];

const DEFAULT_VALUES: NotificationSettings = {
	accountStageChanges: false,
	emailFrequency: Frequency.Monthly,
	netNewPipelineAccounts: false,
	newAccounts: false,
	newAtRiskAccounts: false,
	newStalledAccounts: false,
};

const ManageLifecycleNotificationsModal: React.FC<
	IManageLifecycleNotificationsModalProps
> = ({groupId, lifecycleId, onClose}) => (
	<NotificationSettingsModal
		checkboxes={CHECKBOXES}
		defaultValues={DEFAULT_VALUES}
		description={Liferay.Language.get(
			'choose-your-preferred-notification-types-and-delivery-frequency-for-both-in-product-and-email-channels.-these-settings-are-personal-and-will-not-affect-other-users'
		)}
		fetchSavedValues={() =>
			API.preferences
				.fetchLifecycleNotifications({groupId, lifecycleId})
				.then((preferences) => preferences[lifecycleId])
		}
		onClose={onClose}
		onSave={(values: NotificationSettings) =>
			API.preferences.updateLifecycleNotification({
				...values,
				groupId,
				lifecycleId,
			})
		}
		title={Liferay.Language.get('manage-lifecycle-notifications')}
	/>
);

export default ManageLifecycleNotificationsModal;
