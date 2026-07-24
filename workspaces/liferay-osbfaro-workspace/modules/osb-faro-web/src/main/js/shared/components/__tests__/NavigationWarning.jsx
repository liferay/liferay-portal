import mockStore from 'test/mock-store';
import NavigationWarning from '../NavigationWarning';
import React from 'react';
import {actionTypes, modalTypes} from 'shared/actions/modals';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';
import {useBlocker} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: jest.fn(),
}));

describe('NavigationWarning', () => {
	afterEach(() => {
		cleanup();

		useBlocker.mockReset();
	});

	it('should render', () => {
		useBlocker.mockReturnValue({state: 'unblocked'});

		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<NavigationWarning when />
				</MemoryRouter>
			</Provider>
		);

		expect(container).toBeTruthy();
	});

	it('opens the confirmation modal and wires proceed and reset when blocked', () => {
		const proceed = jest.fn();
		const reset = jest.fn();

		useBlocker.mockReturnValue({proceed, reset, state: 'blocked'});

		const store = mockStore();
		const dispatch = jest.spyOn(store, 'dispatch');

		render(
			<Provider store={store}>
				<MemoryRouter>
					<NavigationWarning when />
				</MemoryRouter>
			</Provider>
		);

		const openModalCall = dispatch.mock.calls.find(
			([action]) =>
				action.type === actionTypes.OPEN_MODAL &&
				action.payload.type === modalTypes.CONFIRMATION_MODAL
		);

		expect(openModalCall).toBeTruthy();

		const {props} = openModalCall[0].payload;

		props.onSubmit();

		expect(proceed).toHaveBeenCalled();

		props.onClose();

		expect(reset).toHaveBeenCalled();
	});
});
