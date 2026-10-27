import mockStore from 'test/mock-store';
import NavigationWarning from '../NavigationWarning';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';
import {useBlocker} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: jest.fn(),
}));

// Like the router, the blocker leaves the `blocked` state once it is reset or
// allowed to proceed.

const mockBlocker = () => {
	let state = 'blocked';

	const proceed = jest.fn(() => {
		state = 'proceeding';
	});

	const reset = jest.fn(() => {
		state = 'unblocked';
	});

	useBlocker.mockImplementation(() => ({proceed, reset, state}));

	return {proceed, reset};
};

const renderComponent = () =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<NavigationWarning when />
			</MemoryRouter>
		</Provider>
	);

describe('NavigationWarning', () => {
	afterEach(() => {
		cleanup();

		useBlocker.mockReset();
	});

	it('should render nothing while the navigation is not blocked', () => {
		useBlocker.mockReturnValue({state: 'unblocked'});

		renderComponent();

		expect(screen.queryByRole('dialog')).toBeNull();
	});

	it('opens the confirmation modal when the navigation is blocked', async () => {
		useBlocker.mockReturnValue({
			proceed: jest.fn(),
			reset: jest.fn(),
			state: 'blocked',
		});

		renderComponent();

		expect(await screen.findByRole('dialog')).toBeInTheDocument();
		expect(
			await screen.findByRole('heading', {name: /unsaved changes/i})
		).toBeInTheDocument();
	});

	it('proceeds with the navigation when the user leaves the page', async () => {
		const {proceed, reset} = mockBlocker();

		renderComponent();

		fireEvent.click(await screen.findByText(/leave page/i));

		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());

		expect(proceed).toHaveBeenCalledTimes(1);
		expect(reset).not.toHaveBeenCalled();
	});

	it('resets the navigation when the user stays on the page', async () => {
		const {proceed, reset} = mockBlocker();

		renderComponent();

		fireEvent.click(await screen.findByText(/stay on page/i));

		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());

		expect(reset).toHaveBeenCalledTimes(1);
		expect(proceed).not.toHaveBeenCalled();
	});

	it('resets the navigation when the modal is dismissed with Esc', async () => {
		const {proceed, reset} = mockBlocker();

		renderComponent();

		await screen.findByText(/leave page/i);

		fireEvent.keyDown(document, {key: 'Escape'});

		await waitFor(() => expect(reset).toHaveBeenCalledTimes(1));

		expect(proceed).not.toHaveBeenCalled();
	});
});
