import mockStore from 'test/mock-store';
import React from 'react';
import {Dashboard} from '../index';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/components/download-report/DownloadCSVReport', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('shared/components/download-report/DownloadPDFReport', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('shared/components/Loading', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('shared/components/RouteNotFound', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('route-middleware/BundleRouter', () => ({
	__esModule: true,
	default: () => null,
}));

jest.mock('shared/components/AccountDropdown', () => ({
	__esModule: true,
	default: ({
		initialAccountId,
		initialAccountName,
	}: {
		initialAccountId?: string;
		initialAccountName?: string;
	}) => (
		<div
			data-initial-account-id={initialAccountId}
			data-initial-account-name={initialAccountName}
			data-testid="filter-by-account"
		/>
	),
}));

jest.mock('shared/components/SegmentDropdown', () => ({
	__esModule: true,
	default: ({
		initialSegmentId,
		initialSegmentName,
	}: {
		initialSegmentId?: string;
		initialSegmentName?: string;
	}) => (
		<div
			data-initial-segment-id={initialSegmentId}
			data-initial-segment-name={initialSegmentName}
			data-testid="filter-by-segment"
		/>
	),
}));

jest.mock('shared/context/channel', () => ({
	useChannelContext: () => ({selectedChannel: {name: 'test channel'}}),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: () => ({empty: false, error: false, loading: false}),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => false}),
}));

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '789'}),
}));

describe('Dashboard', () => {
	const router = {
		params: {
			channelId: '456',
			groupId: '789',
		},
		query: {},
	};

	it('shows the account filter for LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('filter-by-account')).toBeTruthy();
	});

	it('hides the account filter for non-LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(false);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('filter-by-account')).toBeNull();
	});

	it('seeds the account filter from the accountId/accountName URL query params', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter
					initialEntries={['/?accountId=100&accountName=Account+100']}
				>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
			'data-initial-account-id',
			'100'
		);
		expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
			'data-initial-account-name',
			'Account 100'
		);
	});

	it('shows the segment filter for LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('filter-by-segment')).toBeTruthy();
	});

	it('hides the segment filter for non-LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(false);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('filter-by-segment')).toBeNull();
	});

	it('seeds the segment filter from the segmentId/segmentName URL query params', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter
					initialEntries={['/?segmentId=100&segmentName=Segment+100']}
				>
					<Dashboard router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('filter-by-segment')).toHaveAttribute(
			'data-initial-segment-id',
			'100'
		);
		expect(screen.getByTestId('filter-by-segment')).toHaveAttribute(
			'data-initial-segment-name',
			'Segment 100'
		);
	});
});
