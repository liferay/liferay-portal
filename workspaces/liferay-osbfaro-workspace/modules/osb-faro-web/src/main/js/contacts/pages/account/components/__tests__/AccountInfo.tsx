import AccountInfo from '../AccountInfo';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({id: 'acc-123'}),
}));

jest.mock('../AccountDetailsModal', () => ({
	__esModule: true,
	default: ({onClose}: {onClose: () => void}) => (
		<div data-testid="account-details-modal">
			<button onClick={onClose} type="button">
				{'close'}
			</button>
		</div>
	),
}));

const mockAccount = {
	accountType: 'Customer',
	annualRevenue: 5000000,
	id: 'acc-123',
	industry: 'Technology',
	numberOfEmployees: 250,
	website: 'https://acme.com',
};

describe('AccountInfo', () => {
	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the card title', () => {
			render(<AccountInfo account={mockAccount} />);

			expect(
				screen.getByText('General Account Information')
			).toBeInTheDocument();
		});

		it('should render every info label', () => {
			render(<AccountInfo account={mockAccount} />);

			expect(screen.getByText('Account Type')).toBeInTheDocument();
			expect(screen.getByText('Industry')).toBeInTheDocument();
			expect(screen.getByText('Company Size')).toBeInTheDocument();
			expect(screen.getByText('Annual Revenue')).toBeInTheDocument();
			expect(screen.getByText('Website')).toBeInTheDocument();
		});

		it('should render the values from the mock', () => {
			render(<AccountInfo account={mockAccount} />);

			expect(screen.getByText('Customer')).toBeInTheDocument();
			expect(screen.getByText('Technology')).toBeInTheDocument();
			expect(screen.getByText('250')).toBeInTheDocument();
			expect(screen.getByText('5M')).toBeInTheDocument();
			expect(screen.getByText('https://acme.com')).toBeInTheDocument();
		});

		it('should render a zero annual revenue', () => {
			render(
				<AccountInfo account={{...mockAccount, annualRevenue: 0}} />
			);

			expect(screen.getByText('0')).toBeInTheDocument();
		});

		it('should render a zero company size', () => {
			render(
				<AccountInfo account={{...mockAccount, numberOfEmployees: 0}} />
			);

			expect(screen.getByText('0')).toBeInTheDocument();
		});

		it.each([
			['accountType', undefined],
			['accountType', ''],
			['annualRevenue', null],
			['annualRevenue', undefined],
			['industry', undefined],
			['numberOfEmployees', null],
			['numberOfEmployees', undefined],
			['website', undefined],
			['website', ''],
		])('should render a %s of %p as no data', (key, value) => {
			render(<AccountInfo account={{...mockAccount, [key]: value}} />);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		});

		it('should render the website value as an external link', () => {
			render(<AccountInfo account={mockAccount} />);

			const link = screen.getByRole('link', {
				name: /https:\/\/acme\.com/,
			});

			expect(link).toHaveAttribute('href', 'https://acme.com');
			expect(link).toHaveAttribute('target', '_blank');
			expect(link).toHaveAttribute('rel', 'noreferrer noopener');
		});

		it('should render the View All button', () => {
			render(<AccountInfo account={mockAccount} />);

			expect(
				screen.getByRole('button', {name: 'View All'})
			).toBeInTheDocument();
		});
	});

	describe('loading', () => {
		it('should render the loading indicator and hide the content when loading is true', () => {
			const {container} = render(
				<AccountInfo account={mockAccount} loading />
			);

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(
				screen.queryByRole('button', {name: 'View All'})
			).not.toBeInTheDocument();
		});
	});

	describe('empty state', () => {
		it('should render the empty title and description when no account is provided', () => {
			render(<AccountInfo />);

			expect(
				screen.getByText('No Account Attributes Available')
			).toBeInTheDocument();
			expect(
				screen.getByText(
					'Account attributes will appear here when available.'
				)
			).toBeInTheDocument();
		});

		it('should not render the View All button when there are no attributes', () => {
			render(<AccountInfo />);

			expect(
				screen.queryByRole('button', {name: 'View All'})
			).not.toBeInTheDocument();
		});

		it('should render the empty state when the account has no resolvable attributes', () => {
			render(<AccountInfo account={{}} />);

			expect(
				screen.getByText('No Account Attributes Available')
			).toBeInTheDocument();
		});

		it('should render the empty state when every attribute is null or empty', () => {
			render(
				<AccountInfo
					account={{
						accountType: '',
						annualRevenue: null as unknown as number,
						industry: '',
						numberOfEmployees: null as unknown as number,
						website: '',
					}}
				/>
			);

			expect(
				screen.getByText('No Account Attributes Available')
			).toBeInTheDocument();
			expect(screen.queryByText('–')).not.toBeInTheDocument();
		});

		it('should not render the empty state when the only attribute is a zero', () => {
			render(<AccountInfo account={{annualRevenue: 0}} />);

			expect(
				screen.queryByText('No Account Attributes Available')
			).not.toBeInTheDocument();
			expect(screen.getByText('0')).toBeInTheDocument();
		});
	});

	describe('details modal', () => {
		it('should not render the modal until the View All button is clicked', () => {
			render(<AccountInfo account={mockAccount} />);

			expect(
				screen.queryByTestId('account-details-modal')
			).not.toBeInTheDocument();
		});

		it('should open the modal when the View All button is clicked', () => {
			render(<AccountInfo account={mockAccount} />);

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));

			expect(
				screen.getByTestId('account-details-modal')
			).toBeInTheDocument();
		});

		it('should close the modal when onClose is called', () => {
			render(<AccountInfo account={mockAccount} />);

			fireEvent.click(screen.getByRole('button', {name: 'View All'}));
			fireEvent.click(screen.getByRole('button', {name: 'close'}));

			expect(
				screen.queryByTestId('account-details-modal')
			).not.toBeInTheDocument();
		});
	});
});
