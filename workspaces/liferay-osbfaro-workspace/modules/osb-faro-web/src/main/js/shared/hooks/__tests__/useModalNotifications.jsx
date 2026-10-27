import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import {
	NotificationSubtypes,
	NotificationTypes
} from 'shared/util/records/Notification';
import {act, renderHook, waitFor} from '@testing-library/react';
import {range} from 'lodash';
import {useModalNotifications} from 'shared/hooks/useModalNotifications';

jest.unmock('react-dom');

jest.mock(
	'shared/components/modals/TimeZoneSelectionModal',
	() =>
		({notificationId, onClose}) => (
			<button data-testid={`modal-${notificationId}`} onClick={onClose} />
		)
);

const mockNotifications = count =>
	Promise.resolve(
		range(count).map(i =>
			data.mockNotification(i, {
				subtype: NotificationSubtypes.TimeZoneAdmin,
				type: NotificationTypes.Modal
			})
		)
	);

describe('useModalNotifications', () => {
	it('should return a notification modal', async () => {
		API.notifications.fetchNotifications.mockReturnValueOnce(
			mockNotifications(1)
		);

		const {result} = renderHook(() => useModalNotifications('23'));

		await waitFor(() => expect(result.current).not.toBeNull());

		expect(result.current.props.groupId).toBe('23');
	});

	it('should return another notification modal after closing one when having multiple modals', async () => {
		API.notifications.fetchNotifications.mockReturnValue(
			mockNotifications(2)
		);

		const {result} = renderHook(() => useModalNotifications('23'));

		await waitFor(() => expect(result.current).not.toBeNull());

		const firstId = result.current.props.notificationId;

		act(() => result.current.props.onClose());

		await waitFor(() =>
			expect(result.current?.props.notificationId).not.toBe(firstId)
		);

		expect(result.current).not.toBeNull();

		act(() => result.current.props.onClose());

		await waitFor(() => expect(result.current).toBeNull());
	});

	it('should return nothing when there are no notifications', async () => {
		API.notifications.fetchNotifications.mockReturnValueOnce(
			mockNotifications(0)
		);

		const {result} = renderHook(() => useModalNotifications('23'));

		await waitFor(() =>
			expect(API.notifications.fetchNotifications).toHaveBeenCalled()
		);

		expect(result.current).toBeNull();
	});
});
