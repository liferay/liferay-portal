import AccountIndividuals from '../AccountIndividuals';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => (
		<div data-testid="fds-component" id={props.id} />
	),
}));

let lastDropdownProps: any;
let mockSearch = '';

const mockPush = jest.fn();

// Mocking the dropdown keeps these off the Apollo query it runs to load the
// range presets; what matters here is the range it is given and the range it
// hands back.

jest.mock('shared/components/dropdown-range-key/DropdownRangeKey', () => ({
	DropdownRangeKey: (props: any) => {
		lastDropdownProps = props;

		return <div data-testid="dropdown-range-key" />;
	},
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useLocation: () => ({search: mockSearch}),
	useParams: () => ({channelId: '456', groupId: '23', id: 'acc-1'}),
}));

describe('AccountIndividuals', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		lastDropdownProps = undefined;
		mockSearch = '';

		// The handler reads window.location, which jsdom shares across tests.

		window.history.replaceState({}, '', '/');
	});

	afterEach(cleanup);

	it('should render the card title', () => {
		render(<AccountIndividuals />);

		expect(screen.getByText('Account Individuals')).toBeInTheDocument();
	});

	it('should render the range dropdown', () => {
		render(<AccountIndividuals />);

		expect(lastDropdownProps).toBeDefined();
	});

	it('should default the range to the last thirty days', () => {
		render(<AccountIndividuals />);

		expect(lastDropdownProps.rangeSelectors.rangeKey).toBe('30');
	});

	it('should show the range that is on the query string', () => {
		mockSearch = '?rangeKey=7';

		render(<AccountIndividuals />);

		expect(lastDropdownProps.rangeSelectors.rangeKey).toBe('7');
	});

	it('should offer the custom range picker', () => {
		render(<AccountIndividuals />);

		expect(lastDropdownProps.legacy).toBe(false);
	});

	it('should write the selected preset to the query string', () => {
		render(<AccountIndividuals />);

		lastDropdownProps.onRangeSelectorChange({
			rangeEnd: null,
			rangeKey: '90',
			rangeStart: null,
		});

		expect(mockPush).toHaveBeenCalledWith('/?rangeKey=90');
	});

	it('should drop the previous bounds when a preset is selected', () => {
		window.history.replaceState(
			{},
			'',
			'/?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10'
		);

		render(<AccountIndividuals />);

		lastDropdownProps.onRangeSelectorChange({
			rangeEnd: null,
			rangeKey: '30',
			rangeStart: null,
		});

		const [pushedURL] = mockPush.mock.calls[0];

		expect(pushedURL).not.toContain('rangeEnd');
		expect(pushedURL).not.toContain('rangeStart');
		expect(pushedURL).toContain('rangeKey=30');
	});

	it('should render the card description', () => {
		render(<AccountIndividuals />);

		expect(
			screen.getByText(
				'Lists all individuals associated with this account.'
			)
		).toBeInTheDocument();
	});

	it('should render the individuals data set', () => {
		render(<AccountIndividuals />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'account-individuals-dataset'
		);
	});
});
