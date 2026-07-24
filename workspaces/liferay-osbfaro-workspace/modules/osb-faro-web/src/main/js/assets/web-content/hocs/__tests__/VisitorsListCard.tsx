import client from 'shared/apollo/client';
import VisitorsListCard from '../VisitorsListCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {fireEvent, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const MOCK_ROUTER = {
	params: {
		channelId: '456',
		groupId: '789',
	},
	query: {},
};

describe('VisitorsListCard', () => {
	it('shows an Accounts tab active by default and switches to the Known Individuals tab on click', () => {
		const {getByTestId, getByText} = render(
			<Provider store={mockStore()}>
				<ApolloProvider client={client}>
					<MemoryRouter>
						<VisitorsListCard router={MOCK_ROUTER} />
					</MemoryRouter>
				</ApolloProvider>
			</Provider>
		);

		expect(getByText('Accounts')).toBeTruthy();
		expect(getByText('Known Individuals')).toBeTruthy();

		expect(getByTestId('accounts').className).toContain('active');
		expect(getByTestId('individuals').className).not.toContain('active');

		fireEvent.click(getByText('Known Individuals'));

		expect(getByTestId('individuals').className).toContain('active');
		expect(getByTestId('accounts').className).not.toContain('active');
	});
});
