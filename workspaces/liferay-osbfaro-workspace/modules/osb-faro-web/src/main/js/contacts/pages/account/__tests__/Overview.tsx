import Overview from '../Overview';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const mockAccount = {
	accountName: 'IQVIA',
	accountType: 'Prospect',
	annualRevenue: 11359000000,
	country: 'United States',
	id: 'acc-1',
	industry: 'Business Services',
	lifecycleStage: 'ENGAGED',
};

describe('Overview', () => {
	afterEach(cleanup);

	it('should render the account firmographics from the account', () => {
		render(<Overview account={mockAccount} />);

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.getByText('United States')).toBeInTheDocument();
		expect(screen.getByText('11.36B Revenue')).toBeInTheDocument();
		expect(screen.getByText('Business Services')).toBeInTheDocument();
		expect(screen.getByText('Lifecycle: Engaged')).toBeInTheDocument();
		expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
	});

	it('should render no lifecycle label when the account has none', () => {
		render(<Overview account={{...mockAccount, lifecycleStage: null}} />);

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Lifecycle/)).not.toBeInTheDocument();
	});

	it('should render no account type label when the account has none', () => {
		render(<Overview account={{...mockAccount, accountType: ''}} />);

		expect(screen.getByText('IQVIA')).toBeInTheDocument();
		expect(screen.queryByText(/Type:/)).not.toBeInTheDocument();
	});

	it('should render the card without an account', () => {
		const {container} = render(<Overview />);

		expect(screen.getByText('ACCOUNT INFO')).toBeInTheDocument();
		expect(container.querySelectorAll('.label')).toHaveLength(0);
	});
});
