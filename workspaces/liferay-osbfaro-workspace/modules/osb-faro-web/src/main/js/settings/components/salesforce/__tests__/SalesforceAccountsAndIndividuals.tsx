jest.unmock('react-dom');

import React from 'react';
import SalesforceAccountsAndIndividuals from '../SalesforceAccountsAndIndividuals';
import {render} from '@testing-library/react';

describe('SalesforceAccountsAndIndividuals', () => {
	it('renders the synced counts with comma separators when they are large', () => {
		const {getByText} = render(
			<SalesforceAccountsAndIndividuals
				accountsSyncedCount={73085}
				enabledAccounts
				enabledIndividuals
				individualsSyncedCount={279089}
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('73,085 Items Synced')).toBeTruthy();
		expect(getByText('279,089 Items Synced')).toBeTruthy();
	});

	it('renders the synced counts when they are zero', () => {
		const {getAllByText} = render(
			<SalesforceAccountsAndIndividuals
				accountsSyncedCount={0}
				enabledAccounts
				enabledIndividuals
				individualsSyncedCount={0}
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getAllByText('0 Items Synced')).toHaveLength(2);
	});

	it('hides the synced counts when no values are provided', () => {
		const {queryByText} = render(
			<SalesforceAccountsAndIndividuals
				enabledAccounts
				enabledIndividuals
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(queryByText(/Items Synced/i)).toBeNull();
	});
});
