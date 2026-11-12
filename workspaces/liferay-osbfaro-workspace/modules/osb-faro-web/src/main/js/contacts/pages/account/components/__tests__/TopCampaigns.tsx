import React from 'react';
import TopCampaigns from '../TopCampaigns';
import {render, screen} from '@testing-library/react';
import {useParams} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: () => <div data-testid="fds-component" />,
}));

jest.mock('shared/components/dropdown-range-key/DropdownRangeKey', () => ({
	DropdownRangeKey: () => <div data-testid="dropdown-range-key" />,
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: jest.fn()}),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useLocation: () => ({search: ''}),
	useParams: jest.fn(),
}));

const mockedUseParams = useParams as jest.Mock;

const getViewAllHref = () =>
	screen.getByRole('link', {name: 'View All'}).getAttribute('href')!;

describe('TopCampaigns', () => {
	beforeEach(() => {
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

	it('should link to the campaigns filtered by the account', () => {
		render(<TopCampaigns accountName="IQVIA" />);

		const href = getViewAllHref();

		expect(href).toContain('/campaigns');
		expect(href).not.toContain('/campaigns/');
		expect(href).toContain('accountId=acc-1');
		expect(href).toContain('accountName=IQVIA');
	});

	it('should leave the account name off while it is loading', () => {
		render(<TopCampaigns />);

		expect(getViewAllHref()).not.toContain('accountName');
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
