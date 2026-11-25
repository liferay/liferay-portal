import CriteriaSidebarDataSourceEmptyState from '../CriteriaSidebarDataSourceEmptyState';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

jest.mock('shared/hooks/useCurrentUser', () => ({useCurrentUser: jest.fn()}));

jest.mock('react-router-dom', () => ({
	useParams: () => ({groupId: '12345'})
}));

const EmptyState = () => (
	<CriteriaSidebarDataSourceEmptyState
		description='Connect a data source.'
		title='No Data Has Been Synced'
	/>
);

describe('CriteriaSidebarDataSourceEmptyState', () => {
	afterEach(cleanup);

	it('should offer an administrator to connect a data source', () => {
		useCurrentUser.mockReturnValue({isAdmin: () => true});

		render(<EmptyState />);

		expect(
			screen.getByRole('link', {name: /learn more about data sources/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('link', {name: /^connect data source$/i})
		).toHaveAttribute('href', '/workspace/12345/settings/data-source');
	});

	it('should not offer to connect a data source to a user who cannot', () => {
		useCurrentUser.mockReturnValue({isAdmin: () => false});

		render(<EmptyState />);

		expect(
			screen.getByRole('link', {name: /learn more about data sources/i})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('link', {name: /^connect data source$/i})
		).not.toBeInTheDocument();
	});
});
