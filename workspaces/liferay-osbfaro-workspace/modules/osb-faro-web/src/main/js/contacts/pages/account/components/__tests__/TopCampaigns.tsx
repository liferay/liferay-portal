import React from 'react';
import TopCampaigns from '../TopCampaigns';
import {cleanup, render, screen} from '@testing-library/react';
import {useParams} from 'react-router-dom';

jest.unmock('react-dom');

let lastFDSProps: any;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => {
		lastFDSProps = props;

		return <div data-testid="fds-component" id={props.id} />;
	},
}));

let lastDropdownProps: any;
let mockSearch = '';

const mockPush = jest.fn();

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

const getViewAllHref = () =>
	screen.getByRole('link', {name: 'View All'}).getAttribute('href')!;

const renderNameCell = () =>
	render(
		lastFDSProps.customDataRenderers.campaignNameRenderer({
			itemData: {
				campaignName: 'Spring Launch',
				id: 'campaign-1',
				individualsTouched: 12,
				lastTouchDate: '2026-03-12',
			},
			value: 'Spring Launch',
		})
	);

describe('TopCampaigns', () => {
	afterEach(cleanup);

	beforeEach(() => {
		jest.clearAllMocks();
		lastDropdownProps = undefined;
		lastFDSProps = undefined;
		mockSearch = '';

		window.history.replaceState({}, '', '/');

		mockedUseParams.mockReturnValue({
			channelId: '456',
			groupId: '23',
			id: 'acc-1',
		});
	});

	it('should render the card title', () => {
		render(<TopCampaigns accountName="IQVIA" />);

		expect(screen.getByText('TOP CAMPAIGNS')).toBeInTheDocument();
	});

	it('should request the campaigns of the account', async () => {
		mockSearch = '?rangeKey=7';

		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(lastFDSProps.apiURL).toContain(
			'/o/faro/contacts/23/account/acc-1/campaigns'
		);
		expect(lastFDSProps.apiURL).toContain('channelId=456');
		expect(lastFDSProps.apiURL).toContain('rangeKey=7');
	});

	it('should send a custom range as its bounds alone', async () => {
		mockSearch =
			'?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10';

		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(lastFDSProps.apiURL).toContain('rangeEnd=2026-02-20');
		expect(lastFDSProps.apiURL).toContain('rangeStart=2026-02-10');
		expect(lastFDSProps.apiURL).not.toContain('rangeKey');
	});

	it('should rank the campaigns by the individuals touched', async () => {
		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(lastFDSProps.sorts).toEqual([
			expect.objectContaining({
				active: true,
				direction: 'desc',
				key: 'individualsTouched',
			}),
		]);
	});

	it('should show the top five campaigns with no management bar', async () => {
		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(lastFDSProps.views[0].initialPaginationDelta).toBe(5);
		expect(lastFDSProps.showManagementBar).toBe(false);
		expect(lastFDSProps.showPagination).toBe(false);
		expect(lastFDSProps.showSearch).toBe(false);
	});

	it('should render the name, individuals touched and last touch columns', async () => {
		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(
			lastFDSProps.views[0].schema.fields.map(
				({fieldName}: {fieldName: string}) => fieldName
			)
		).toEqual(['campaignName', 'individualsTouched', 'lastTouchDate']);
	});

	it('should render the text only empty state', async () => {
		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		expect(lastFDSProps.emptyState).toEqual({
			description:
				'This account has not been touched by any campaign yet.',
			title: 'No Campaigns Found',
		});
	});

	it('should link each campaign to its detail page', async () => {
		mockSearch = '?rangeKey=7';

		render(<TopCampaigns accountName="IQVIA" />);

		await screen.findByTestId('fds-component');

		cleanup();

		renderNameCell();

		const href = screen
			.getByRole('link', {name: 'Spring Launch'})
			.getAttribute('href')!;

		expect(href).toContain('/campaigns/campaign-1');
		expect(href).toContain('rangeKey=7');
		expect(href).toContain('accountId=acc-1');
	});

	it('should link to the campaigns filtered by the account', () => {
		render(<TopCampaigns accountName="IQVIA" />);

		const href = getViewAllHref();

		expect(href).toContain('/campaigns');
		expect(href).not.toContain('/campaigns/');
		expect(href).toContain('accountId=acc-1');
		expect(href).toContain('accountName=IQVIA');
	});

	it('should carry the selected range to the campaigns', () => {
		mockSearch =
			'?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10';

		render(<TopCampaigns accountName="IQVIA" />);

		const href = getViewAllHref();

		expect(href).toContain('rangeEnd=2026-02-20');
		expect(href).toContain('rangeKey=CUSTOM');
		expect(href).toContain('rangeStart=2026-02-10');
	});

	it('should leave the account name off while it is loading', () => {
		render(<TopCampaigns />);

		expect(getViewAllHref()).not.toContain('accountName');
	});

	it('should write the selected preset to the query string', () => {
		render(<TopCampaigns accountName="IQVIA" />);

		lastDropdownProps.onRangeSelectorChange({
			rangeEnd: null,
			rangeKey: '90',
			rangeStart: null,
		});

		expect(mockPush).toHaveBeenCalledWith('/?rangeKey=90');
	});

	it('should render neither the list nor the link while the account id is missing', () => {
		mockedUseParams.mockReturnValue({channelId: '456', groupId: '23'});

		render(<TopCampaigns accountName="IQVIA" />);

		expect(screen.queryByTestId('fds-component')).not.toBeInTheDocument();
		expect(
			screen.queryByRole('link', {name: 'View All'})
		).not.toBeInTheDocument();
	});
});
