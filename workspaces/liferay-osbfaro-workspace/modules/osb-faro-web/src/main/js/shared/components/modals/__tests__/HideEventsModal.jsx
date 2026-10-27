import HideEventsModal from '../HideEventsModal';
import React from 'react';
import {Alert} from 'shared/types';
import {cleanup, fireEvent, screen, waitFor} from '@testing-library/react';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const EVENTS = [
	{displayName: 'First', hidden: false, id: '1'},
	{displayName: 'Second', hidden: false, id: '2'},
];

const renderHideEvents = (props) =>
	renderModal((observer) => (
		<HideEventsModal
			addAlert={jest.fn()}
			eventName={({displayName}) => displayName}
			events={EVENTS}
			hideEvents={jest.fn(() => Promise.resolve())}
			observer={observer}
			onClose={jest.fn()}
			{...props}
		/>
	));

describe('HideEventsModal', () => {
	afterEach(cleanup);

	it('renders as a warning with the hide button', async () => {
		await renderHideEvents();

		expect(document.querySelector('.modal-warning')).toBeTruthy();

		expect(
			screen.getByRole('button', {name: 'Hide'})
		).toBeInTheDocument();
	});

	it('hides every event and shows a success alert', async () => {
		const addAlert = jest.fn();
		const hideEvents = jest.fn(() => Promise.resolve());

		await renderHideEvents({addAlert, hideEvents});

		fireEvent.click(screen.getByRole('button', {name: 'Hide'}));

		expect(hideEvents).toHaveBeenCalledWith(['1', '2']);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith(
				expect.objectContaining({alertType: Alert.Types.Success})
			)
		);
	});

	it('names the event in the success alert when only one is visible', async () => {
		const addAlert = jest.fn();
		const singleEventHiddenMessage = jest.fn((name) => `${name} hidden`);

		await renderHideEvents({
			addAlert,
			events: [
				{displayName: 'Already', hidden: true, id: '1'},
				{displayName: 'Second', hidden: false, id: '2'},
			],
			singleEventHiddenMessage,
		});

		fireEvent.click(screen.getByRole('button', {name: 'Hide'}));

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Success,
				message: 'Second hidden',
			})
		);
	});

	it('does not use the single event message when several are visible', async () => {
		const addAlert = jest.fn();
		const singleEventHiddenMessage = jest.fn();

		await renderHideEvents({addAlert, singleEventHiddenMessage});

		fireEvent.click(screen.getByRole('button', {name: 'Hide'}));

		await waitFor(() => expect(addAlert).toHaveBeenCalled());

		expect(singleEventHiddenMessage).not.toHaveBeenCalled();
	});

	it('shows an error alert that stays when hiding fails', async () => {
		const addAlert = jest.fn();

		await renderHideEvents({
			addAlert,
			hideEvents: jest.fn(() => Promise.reject(new Error('failed'))),
		});

		fireEvent.click(screen.getByRole('button', {name: 'Hide'}));

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Error,
					timeout: false,
				})
			)
		);
	});

	it('closes when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderHideEvents({onClose});

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
