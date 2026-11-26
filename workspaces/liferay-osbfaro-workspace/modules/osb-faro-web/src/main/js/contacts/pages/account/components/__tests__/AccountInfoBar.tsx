import AccountInfoBar from '../AccountInfoBar';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useLifecycleSelection} from 'shared/hooks/useLifecycleSelection';

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

jest.mock('shared/hooks/useLifecycleSelection', () => ({
	useLifecycleSelection: jest.fn(),
}));

const mockedUseLifecycleSelection = useLifecycleSelection as jest.Mock;

const buildLifecycleStage = (lifecycleId: string, name: string) => ({
	lifecycleId,
	lifecycleName: `Lifecycle ${lifecycleId}`,
	name,
	stageExternalReferenceCode: `erc-${lifecycleId}-${name}`,
});

const mockAccount = {
	accountName: 'Hydrofield',
	accountType: 'Prospect',
	annualRevenue: 120000000,
	country: 'Australia',
	industry: 'Health Sector',
	lifecycleStages: [
		buildLifecycleStage('al-1', 'Engaged'),
		buildLifecycleStage('al-2', 'At Risk'),
	],
};

const lifecycleSelection = {
	lifecycles: [
		{id: 'al-1', name: 'Customer'},
		{id: 'al-2', name: 'Partner'},
	],
	loading: false,
	selectedLifecycleId: 'al-1',
	setSelectedLifecycleId: jest.fn(),
};

describe('AccountInfoBar', () => {
	afterEach(cleanup);

	beforeEach(() => {
		mockedUseLifecycleSelection.mockReturnValue(lifecycleSelection);
	});

	describe('rendering', () => {
		it('should render the account name', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('Hydrofield')).toBeInTheDocument();
		});

		it('should render every firmographic value', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText(/^australia$/i)).toBeInTheDocument();
			expect(screen.getByText('Health Sector')).toBeInTheDocument();
		});

		it('should abbreviate the annual revenue', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText('120M Revenue')).toBeInTheDocument();
		});

		it('should render the stage of the account in the selected lifecycle and the account type as labels', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText(/^engaged$/i)).toBeInTheDocument();
			expect(screen.queryByText(/^at risk$/i)).not.toBeInTheDocument();
			expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
		});

		it('should render the stage of the account when another lifecycle is selected', () => {
			mockedUseLifecycleSelection.mockReturnValue({
				...lifecycleSelection,
				selectedLifecycleId: 'al-2',
			});

			render(<AccountInfoBar {...mockAccount} />);

			expect(screen.getByText(/^at risk$/i)).toBeInTheDocument();
			expect(screen.queryByText(/^engaged$/i)).not.toBeInTheDocument();
		});

		it('should render the Lifecycle label before the lifecycle selector', () => {
			render(<AccountInfoBar {...mockAccount} />);

			const label = screen.getByText(/^lifecycle$/i);

			expect(
				label.compareDocumentPosition(
					screen.getByRole('combobox', {name: /^lifecycle$/i})
				) & Node.DOCUMENT_POSITION_FOLLOWING
			).toBeTruthy();
		});

		it('should render the lifecycle selector with the selected lifecycle', () => {
			render(<AccountInfoBar {...mockAccount} />);

			expect(
				screen.getByRole('combobox', {name: /^lifecycle$/i})
			).toHaveTextContent(/customer/i);
		});
	});

	describe('missing values', () => {
		it('should render a field blank rather than a placeholder', () => {
			render(<AccountInfoBar accountName="Hydrofield" />);

			expect(screen.getByText('Hydrofield')).toBeInTheDocument();
			expect(screen.queryByText(/^australia$/i)).not.toBeInTheDocument();
			expect(screen.queryByText(/Revenue/)).not.toBeInTheDocument();
			expect(screen.queryByText(/Type:/)).not.toBeInTheDocument();
		});

		it('should not render the stage label when the account has no stage in the selected lifecycle', () => {
			const {container} = render(
				<AccountInfoBar
					accountName="Hydrofield"
					accountType="Prospect"
					lifecycleStages={[buildLifecycleStage('al-2', 'Engaged')]}
				/>
			);

			expect(screen.queryByText(/^engaged$/i)).not.toBeInTheDocument();
			expect(container.querySelectorAll('.label')).toHaveLength(1);
			expect(screen.getByText('Type: Prospect')).toBeInTheDocument();
		});

		it('should render a zero annual revenue', () => {
			render(
				<AccountInfoBar accountName="Hydrofield" annualRevenue={0} />
			);

			expect(screen.getByText(/Revenue/)).toHaveTextContent('0');
		});

		it('should render nothing for an empty account', () => {
			const {container} = render(<AccountInfoBar />);

			expect(container.querySelectorAll('.label')).toHaveLength(0);
			expect(container.querySelectorAll('.lexicon-icon')).toHaveLength(0);
		});
	});
});
