import NotificationSettingsModal from './NotificationSettingsModal';
import React from 'react';
import {Frequency} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

interface IManageSegmentNotificationsModalProps {
	onClose: ModalTypes.close;
}

const CHECKBOXES = [
	{
		label: Liferay.Language.get('new-member-added-to-the-segment'),
		name: 'newMemberAdded',
	},
];

const ManageSegmentNotificationsModal: React.FC<
	IManageSegmentNotificationsModalProps
> = ({onClose}) => (
	<NotificationSettingsModal
		checkboxes={CHECKBOXES}
		description={Liferay.Language.get(
			'choose-your-preferred-notification-types-and-delivery-frequency-for-both-in-product-and-email-channels.-these-settings-are-personal-and-will-not-affect-other-users'
		)}
		initialValues={{
			emailFrequency: Frequency.Daily,
			newMemberAdded: true,
		}}
		onClose={onClose}
		title={Liferay.Language.get('manage-segment-notifications')}
	/>
);

export default ManageSegmentNotificationsModal;
