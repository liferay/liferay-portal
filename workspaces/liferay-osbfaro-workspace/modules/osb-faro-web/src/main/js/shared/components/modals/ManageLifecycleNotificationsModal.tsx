import NotificationSettingsModal from './NotificationSettingsModal';
import React from 'react';
import {Frequency} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

interface IManageLifecycleNotificationsModalProps {
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

const ManageLifecycleNotificationsModal: React.FC<
	IManageLifecycleNotificationsModalProps
> = ({onClose}) => (
	<NotificationSettingsModal
		checkboxes={CHECKBOXES}
		description={Liferay.Language.get(
			'choose-your-preferred-notification-types-and-delivery-frequency-for-both-in-product-and-email-channels.-these-settings-are-personal-and-will-not-affect-other-users'
		)}
		initialValues={{
			accountStageChanges: true,
			emailFrequency: Frequency.Daily,
			netNewPipelineAccounts: true,
			newAccounts: true,
			newAtRiskAccounts: true,
			newStalledAccounts: true,
		}}
		onClose={onClose}
		title={Liferay.Language.get('manage-lifecycle-notifications')}
	/>
);

export default ManageLifecycleNotificationsModal;
