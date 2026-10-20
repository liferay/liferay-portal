import React from 'react';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {fromJS} from 'immutable';
import {SyncSalesforceDataStep} from '../SyncSalesforceDataStep';
import {updateSalesforce} from 'shared/api/data-source';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('settings/components/base-page/WizardPageContext', () => ({
	useWizardPage: () => ({
		dataSource: {
			id: '42',
			provider: fromJS({
				accountsConfiguration: {enableAllAccounts: true},
				contactsConfiguration: {
					enableAllContacts: true,
					enableAllLeads: true,
				},
			}),
		},
	}),
}));

jest.mock('shared/api/data-source', () => ({
	updateSalesforce: jest.fn(() => Promise.resolve()),
}));

const getCheckboxes = () =>
	document.querySelectorAll<HTMLInputElement>('input[type="checkbox"]');

describe('SyncSalesforceDataStep', () => {
	it('renders the campaigns item checked and disabled without a synced count', () => {
		render(
			<SyncSalesforceDataStep onNext={jest.fn()} onPrev={jest.fn()} />
		);

		expect(screen.getByText('Campaigns')).toBeTruthy();
		expect(
			screen.getByText(
				'Represents fields from the Campaign object within Salesforce.'
			)
		).toBeTruthy();
		expect(screen.queryByText(/Items Synced/i)).toBeNull();

		const checkboxes = getCheckboxes();

		expect(checkboxes).toHaveLength(3);
		expect(checkboxes[2].checked).toBe(true);
		expect(checkboxes[2].disabled).toBe(true);
	});

	it('keeps saving the accounts and individuals selection', async () => {
		const onNext = jest.fn();

		render(<SyncSalesforceDataStep onNext={onNext} onPrev={jest.fn()} />);

		const checkboxes = getCheckboxes();

		expect(checkboxes[0].checked).toBe(true);
		expect(checkboxes[0].disabled).toBe(false);

		fireEvent.click(checkboxes[0]);

		expect(checkboxes[0].checked).toBe(false);
		expect(checkboxes[2].checked).toBe(true);

		fireEvent.click(screen.getByText('Continue'));

		await waitFor(() => expect(onNext).toHaveBeenCalled());

		expect(updateSalesforce).toHaveBeenCalledWith({
			accountsConfiguration: {enableAllAccounts: false},
			contactsConfiguration: {
				enableAllContacts: true,
				enableAllLeads: true,
			},
			groupId: '23',
			id: '42',
		});
	});
});
