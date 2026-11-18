import HelpWidget from '../HelpWidget';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {fireEvent, render, screen} from '@testing-library/react';
import {fromJS} from 'immutable';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {PLANS} from 'shared/util/subscriptions';

jest.unmock('react-dom');

describe('HelpWidget', () => {
	it('should render', () => {
		const {getByRole} = render(
			<Provider store={mockStore()}>
				<HelpWidget />
			</Provider>
		);

		expect(getByRole('button', {name: /^help$/i})).toBeInTheDocument();
	});

	it('should render a dropdown', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<HelpWidget />
			</Provider>
		);

		expect(getByText(/^report an issue$/i)).toBeTruthy();
	});

	it('should open the report an issue modal on the basic plan', async () => {
		const store = mockStore(
			fromJS({
				projects: {
					'23': {data: {faroSubscription: {name: PLANS.basic.name}}}
				}
			})
		);

		render(
			<Provider store={store}>
				<MemoryRouter>
					<MockedProvider>
						<HelpWidget groupId='23' />
					</MockedProvider>
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByRole('dialog')).toBeNull();

		fireEvent.click(screen.getByRole('button', {name: /^help$/i}));
		fireEvent.click(screen.getByText(/^report an issue$/i));

		expect(await screen.findByRole('dialog')).toBeInTheDocument();
	});
});
