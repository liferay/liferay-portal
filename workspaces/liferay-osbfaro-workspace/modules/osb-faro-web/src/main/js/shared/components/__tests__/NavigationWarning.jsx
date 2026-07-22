import mockStore from 'test/mock-store';
import NavigationWarning from '../NavigationWarning';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
}));

describe('NavigationWarning', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<NavigationWarning when />
				</MemoryRouter>
			</Provider>
		);

		expect(container).toBeTruthy();
	});
});
