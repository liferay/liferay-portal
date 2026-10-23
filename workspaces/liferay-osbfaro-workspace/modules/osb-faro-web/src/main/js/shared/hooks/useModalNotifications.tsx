import * as API from 'shared/api';
import Notification, {
	NotificationSubtypes,
	NotificationTypes,
} from 'shared/util/records/Notification';
import React, {useEffect, useRef, useState} from 'react';
import TimeZoneSelectionModal from 'shared/components/modals/TimeZoneSelectionModal';
import {useModal} from '@clayui/modal';

/**
 * Shows the modal notifications of a workspace one after the other and
 * returns the modal that is currently showing. The caller renders it.
 */
export function useModalNotifications(groupId: string): React.ReactNode {
	const modal = useModal();

	const pendingRef = useRef<Array<Notification>>([]);

	const [current, setCurrent] = useState<Notification | null>(null);

	const showNext = (): void => {
		let next: Notification | undefined;

		do {
			next = pendingRef.current.pop();
		} while (next && next.subtype !== NotificationSubtypes.TimeZoneAdmin);

		setCurrent(next ?? null);

		modal.onOpenChange(!!next);
	};

	useEffect(() => {
		API.notifications
			.fetchNotifications({
				groupId,
				type: NotificationTypes.Modal,
			})
			.then((notificationList: Array<Notification>) => {
				pendingRef.current = notificationList;

				showNext();
			});
	}, []);

	if (!modal.open || !current) {
		return null;
	}

	return (
		<TimeZoneSelectionModal
			groupId={groupId}
			key={current.id}
			notificationId={current.id}
			observer={modal.observer}
			onClose={showNext}
		/>
	);
}
