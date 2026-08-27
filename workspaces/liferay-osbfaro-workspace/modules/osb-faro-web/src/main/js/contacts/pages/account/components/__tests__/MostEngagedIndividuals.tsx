import MostEngagedIndividuals from '../MostEngagedIndividuals';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useParams} from 'react-router-dom';

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
	useParams: jest.fn(),
}));

const mockedUseParams = useParams as jest.Mock;

describe('MostEngagedIndividuals', () => {
	afterEach(cleanup);

	beforeEach(() => {
		jest.clearAllMocks();
		lastDropdownProps = undefined;
		mockSearch = '';

		// The handler reads window.location, which jsdom shares across tests.

		window.history.replaceState({}, '', '/');

		mockedUseParams.mockReturnValue({
			channelId: '456',
			groupId: '23',
			id: 'acc-1',
		});
	});

	it('should render the card title', () => {
		render(<MostEngagedIndividuals />);

		expect(
			screen.getByText('MOST ENGAGED INDIVIDUALS')
		).toBeInTheDocument();
	});

	it('should render the individuals data set in preview mode', () => {
		render(<MostEngagedIndividuals />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'most-engaged-individuals-dataset'
		);
	});

	it('should link to the account profile for the full list', () => {
		render(<MostEngagedIndividuals />);

		expect(
			screen.getByRole('link', {name: 'View All'}).getAttribute('href')
		).toContain('/contacts/accounts/acc-1/profile');
	});

	it('should render the range dropdown', () => {
		render(<MostEngagedIndividuals />);

		expect(lastDropdownProps).toBeDefined();
	});

	it('should default the range to the last thirty days', () => {
		render(<MostEngagedIndividuals />);

		expect(lastDropdownProps.rangeSelectors.rangeKey).toBe('30');
	});

	it('should show the range that is on the query string', () => {
		mockSearch = '?rangeKey=7';

		render(<MostEngagedIndividuals />);

		expect(lastDropdownProps.rangeSelectors.rangeKey).toBe('7');
	});

	it('should offer the custom range picker', () => {
		render(<MostEngagedIndividuals />);

		expect(lastDropdownProps.legacy).toBe(false);
	});

	it('should write the selected preset to the query string', () => {
		render(<MostEngagedIndividuals />);

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

		render(<MostEngagedIndividuals />);

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

	it('should render no link while the account id is missing', () => {
		mockedUseParams.mockReturnValue({channelId: '456', groupId: '23'});

		render(<MostEngagedIndividuals />);

		expect(
			screen.queryByRole('link', {name: 'View All'})
		).not.toBeInTheDocument();
	});
});
