import CampaignsEmptyState from '../CampaignsEmptyState';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';

jest.mock('shared/context/dataSources', () => ({useDataSources: jest.fn()}));

jest.mock('shared/hooks/useCurrentUser', () => ({useCurrentUser: jest.fn()}));

jest.mock('react-router-dom', () => ({
	useParams: () => ({groupId: '12345'})
}));

describe('CampaignsEmptyState', () => {
	afterEach(cleanup);

	beforeEach(() => {
		useCurrentUser.mockReturnValue({isAdmin: () => true});
		useDataSources.mockReturnValue({empty: false, loading: false});
	});

	it('should point to connecting a data source when none is connected', () => {
		useDataSources.mockReturnValue({empty: true, loading: false});

		render(<CampaignsEmptyState />);

		expect(
			screen.getByText('No Campaign Data Has Been Synced')
		).toBeInTheDocument();
		expect(
			screen.getByText('Connect a data source containing campaign data.')
		).toBeInTheDocument();
		expect(
			screen.getByRole('link', {name: /Learn More about Data Sources/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('link', {name: 'Connect Data Source'})
		).toHaveAttribute('href', '/workspace/12345/settings/data-source');
	});

	it('should not offer to connect a data source to a user who cannot', () => {
		useCurrentUser.mockReturnValue({isAdmin: () => false});
		useDataSources.mockReturnValue({empty: true, loading: false});

		render(<CampaignsEmptyState />);

		expect(
			screen.getByText('No Campaign Data Has Been Synced')
		).toBeInTheDocument();
		expect(
			screen.queryByRole('link', {name: 'Connect Data Source'})
		).not.toBeInTheDocument();
	});

	it('should tell that no campaign was synced when a data source is connected', () => {
		render(<CampaignsEmptyState />);

		expect(screen.getByText('No Campaigns Were Found')).toBeInTheDocument();
		expect(
			screen.getByText(
				'No campaigns were synced from the connected data sources.'
			)
		).toBeInTheDocument();
		expect(
			screen.queryByRole('link', {name: 'Connect Data Source'})
		).not.toBeInTheDocument();
	});

	it('should wait for the data sources before telling why it is empty', () => {
		useDataSources.mockReturnValue({empty: false, loading: true});

		render(<CampaignsEmptyState />);

		expect(
			screen.queryByText('No Campaigns Were Found')
		).not.toBeInTheDocument();
		expect(
			screen.queryByText('No Campaign Data Has Been Synced')
		).not.toBeInTheDocument();
	});
});
