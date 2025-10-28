import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/react-hooks';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {Overview} from '../Overview';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router-dom';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

describe('Data Privacy Overview', () => {
	afterEach(cleanup);

	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => true
		}));

		const {container, getByText} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<StaticRouter>
						<Overview groupId='23' />
					</StaticRouter>
				</Provider>
			</ApolloProvider>
		);

		jest.runAllTimers();

		fireEvent.click(getByText('Select an option'));

		expect(getByText('7 Months')).toBeTruthy();
		expect(getByText('13 Months')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});

	it('should render with disabled buttons in the Suppressed Users section if the user is not an AC admin', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => false
		}));

		const {getByTestId} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<StaticRouter>
						<Overview groupId='23' />
					</StaticRouter>
				</Provider>
			</ApolloProvider>
		);

		jest.runAllTimers();

		expect(getByTestId('export-suppressed-user-button').disabled).toBe(
			true
		);
		expect(getByTestId('data-retention-period-select-input').disabled).toBe(
			true
		);
	});
});
