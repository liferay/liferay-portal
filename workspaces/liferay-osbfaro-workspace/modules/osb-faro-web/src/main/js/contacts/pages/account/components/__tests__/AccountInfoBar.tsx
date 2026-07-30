import AccountInfoBar from '../AccountInfoBar';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const mockAccount = {
	accountName: 'Hydrofield',
	accountType: 'Prospect',
	annualRevenue: 120000000,
	country: 'Australia',
	industry: 'Health Sector',
	lifecycleStage: 'ENGAGED',
};

describe('AccountInfoBar', () => {
	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the section title', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('ACCOUNT INFO')).toBeInTheDocument();
		});

		it('should render the account name', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('Hydrofield')).toBeInTheDocument();
		});

		it('should render every firmographic value', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('Australia')).toBeInTheDocument();
			expect(screen.getByText('Health Sector')).toBeInTheDocument();
		});

		it('should abbreviate the annual revenue', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('120M Revenue')).toBeInTheDocument();
		});

		it('should render the lifecycle stage and the account type as labels', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('Lifecycle: Engaged')).toBeInTheDocument();
			expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
		});

		it('should color the lifecycle stage from the stage label map', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(
				screen.getByText('Lifecycle: Engaged').closest('.label')
			).toHaveClass('label-inverse-warning');
		});

		it('should color every mapped lifecycle stage', () => {
			render(<AccountInfoBar lifecycleStage="AT_RISK" />);

			expect(
				screen.getByText('Lifecycle: At Risk').closest('.label')
			).toHaveClass('label-inverse-danger');
		});

		it('should fall back to the raw stage when it is not mapped', () => {
			render(<AccountInfoBar lifecycleStage="NOT_A_STAGE" />);

			expect(
				screen.getByText('Lifecycle: NOT_A_STAGE').closest('.label')
			).toHaveClass('label-inverse-secondary');
		});
	});

	describe('missing values', () => {
		it('should render a field blank rather than a placeholder', () => {
			render(<AccountInfoBar accountName="Hydrofield" />);

			expect(screen.getByText('Hydrofield')).toBeInTheDocument();
			expect(screen.queryByText('Australia')).not.toBeInTheDocument();
			expect(screen.queryByText(/Revenue/)).not.toBeInTheDocument();
			expect(screen.queryByText(/Lifecycle:/)).not.toBeInTheDocument();
			expect(screen.queryByText(/Type:/)).not.toBeInTheDocument();
		});

		it('should not render the lifecycle label when the backend returns null', () => {
			const {container} = render(
				<AccountInfoBar
					accountName="Hydrofield"
					accountType="Prospect"
					lifecycleStage={null}
				/>
			);

			expect(screen.queryByText(/Lifecycle/)).not.toBeInTheDocument();
			expect(container.querySelectorAll('.label')).toHaveLength(1);
			expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
		});

		it('should render a zeroed annual revenue blank', () => {
			render(
				<AccountInfoBar accountName="Hydrofield" annualRevenue={0} />
			);

			expect(screen.queryByText(/Revenue/)).not.toBeInTheDocument();
			expect(screen.queryByText('0')).not.toBeInTheDocument();
		});

		it('should render nothing but the section title for an empty account', () => {
			const {container} = render(<AccountInfoBar />);

			expect(screen.getByText('ACCOUNT INFO')).toBeInTheDocument();
			expect(container.querySelectorAll('.label')).toHaveLength(0);
			expect(container.querySelectorAll('.lexicon-icon')).toHaveLength(1);
		});
	});
});
