import * as API from 'shared/api';
import NotificationSettingsModal, {
	NotificationSettings,
} from './NotificationSettingsModal';
import React from 'react';
import {Frequency} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

interface IManageSegmentNotificationsModalProps {
	groupId: string;
	onClose: ModalTypes.close;
	segmentId: string;
}

const CHECKBOXES = [
	{
		label: Liferay.Language.get('new-member-added-to-the-segment'),
		name: 'newMemberAdded',
	},
];

const DEFAULT_VALUES: NotificationSettings = {
	emailFrequency: Frequency.Monthly,
	newMemberAdded: false,
};

const ManageSegmentNotificationsModal: React.FC<
	IManageSegmentNotificationsModalProps
> = ({groupId, onClose, segmentId}) => (
	<NotificationSettingsModal
		checkboxes={CHECKBOXES}
		defaultValues={DEFAULT_VALUES}
		description={Liferay.Language.get(
			'choose-your-preferred-notification-types-and-delivery-frequency-for-both-in-product-and-email-channels.-these-settings-are-personal-and-will-not-affect-other-users'
		)}
		fetchSavedValues={() =>
			API.preferences
				.fetchSegmentNotifications({groupId, segmentId})
				.then((preferences) => preferences[segmentId])
		}
		onClose={onClose}
		onSave={(values: NotificationSettings) =>
			API.preferences.updateSegmentNotification({
				...values,
				groupId,
				segmentId,
			})
		}
		title={Liferay.Language.get('manage-segment-notifications')}
	/>
);

export default ManageSegmentNotificationsModal;
