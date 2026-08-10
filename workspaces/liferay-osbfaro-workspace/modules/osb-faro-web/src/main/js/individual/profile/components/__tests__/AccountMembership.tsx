import AccountMembership from '../AccountMembership';
import mockStore from 'test/mock-store';
import React from 'react';
import {fromJS} from 'immutable';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const renderWithStore = (children: React.ReactNode) =>
	render(<Provider store={mockStore()}>{children}</Provider>);

describe('Account Membership', () => {
	const mockData = {
		accountName: 'Acme Corporation',
		accountType: 'Customer',
		annualRevenue: '1000000',
		country: 'United States',
		createdDate: '2020-01-01T00:00:00.000Z',
		currencyCode: 'USD',
		customerSince: 2015,
		id: '001xx000003DGbYAAW',
		industry: 'Manufacturing',
		lastActivityDate: '2021-12-01T00:00:00.000Z',
		numberOfEmployees: '500',
		state: 'California',
	};

	it('should render the snapshot', () => {
		const {container} = renderWithStore(
			<AccountMembership accountData={fromJS(mockData)} />
		);
		expect(container).toMatchSnapshot();
	});

	it('should render the empty state when showEmptyState is true', () => {
		const {getByText, queryByText} = renderWithStore(
			<AccountMembership accountData={fromJS(mockData)} showEmptyState>
				<div>{'empty state rendered'}</div>
			</AccountMembership>
		);

		expect(getByText('empty state rendered')).toBeTruthy();
		expect(queryByText('industry')).toBeNull();
	});

	it('should correctly format the time entries', () => {
		const {getByText} = renderWithStore(
			<AccountMembership accountData={fromJS(mockData)} />
		);

		expect(getByText('2015')).toBeTruthy();
		expect(getByText('Dec 1, 2021')).toBeTruthy();
		expect(getByText('Jan 1, 2020')).toBeTruthy();
	});

	it('should display the fallback dash for missing account values', () => {
		const {getAllByText} = renderWithStore(
			<AccountMembership accountData={fromJS({})} />
		);

		const dashes = getAllByText('-');
		expect(dashes.length).toBeGreaterThan(0);
	});

	it('should render annualRevenue without throwing when currencyCode is null', () => {
		expect(() =>
			renderWithStore(
				<AccountMembership
					accountData={fromJS({
						...mockData,
						currencyCode: null,
					})}
				/>
			)
		).not.toThrow();
	});

	it('links the account name to the account page', () => {
		const {getByRole} = renderWithStore(
			<AccountMembership
				accountData={fromJS(mockData)}
				channelId="420253908131944590"
				groupId="liferay.com"
			/>
		);

		expect(getByRole('link', {name: 'Acme Corporation'})).toHaveAttribute(
			'href',
			'/workspace/liferay.com/420253908131944590/contacts/accounts/001xx000003DGbYAAW'
		);
	});

	it('does not link the account name without a channel and group', () => {
		const {queryByRole} = renderWithStore(
			<AccountMembership accountData={fromJS(mockData)} />
		);

		expect(queryByRole('link', {name: 'Acme Corporation'})).toBeNull();
	});
});
