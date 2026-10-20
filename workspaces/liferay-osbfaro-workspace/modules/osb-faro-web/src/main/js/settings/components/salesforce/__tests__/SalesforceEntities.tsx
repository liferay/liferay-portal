jest.unmock('react-dom');

import React from 'react';
import SalesforceEntities from '../SalesforceEntities';
import {render} from '@testing-library/react';

describe('SalesforceEntities', () => {
	it('renders the campaigns item checked and disabled', () => {
		const {getByText} = render(
			<SalesforceEntities
				enabledAccounts={false}
				enabledIndividuals={false}
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('Campaigns')).toBeTruthy();
		expect(
			getByText(
				'Represents fields from the Campaign object within Salesforce.'
			)
		).toBeTruthy();

		const checkboxes = document.querySelectorAll<HTMLInputElement>(
			'input[type="checkbox"]'
		);

		expect(checkboxes).toHaveLength(3);
		expect(checkboxes[0].checked).toBe(false);
		expect(checkboxes[0].disabled).toBe(false);
		expect(checkboxes[1].checked).toBe(false);
		expect(checkboxes[1].disabled).toBe(false);
		expect(checkboxes[2].checked).toBe(true);
		expect(checkboxes[2].disabled).toBe(true);
	});

	it('renders the synced counts with comma separators when they are large', () => {
		const {getByText} = render(
			<SalesforceEntities
				accountsSyncedCount={73085}
				campaignsSyncedCount={1204}
				enabledAccounts
				enabledIndividuals
				individualsSyncedCount={279089}
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('73,085 Items Synced')).toBeTruthy();
		expect(getByText('279,089 Items Synced')).toBeTruthy();
		expect(getByText('1,204 Items Synced')).toBeTruthy();
	});

	it('renders the synced counts when they are zero', () => {
		const {getAllByText} = render(
			<SalesforceEntities
				accountsSyncedCount={0}
				campaignsSyncedCount={0}
				enabledAccounts
				enabledIndividuals
				individualsSyncedCount={0}
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getAllByText('0 Items Synced')).toHaveLength(3);
	});

	it('hides the synced counts when no values are provided', () => {
		const {getByText, queryByText} = render(
			<SalesforceEntities
				enabledAccounts
				enabledIndividuals
				onAccountsChange={jest.fn()}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('Campaigns')).toBeTruthy();
		expect(queryByText(/Items Synced/i)).toBeNull();
	});
});
