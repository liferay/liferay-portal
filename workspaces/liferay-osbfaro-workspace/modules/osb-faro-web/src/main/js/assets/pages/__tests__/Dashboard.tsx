import AssetDashboard from '../Dashboard';
import mockStore from 'test/mock-store';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/components/download-report/DownloadCSVReport', () => ({
	__esModule: true,
	default: ({assetType}: {assetType?: string}) => (
		<div data-asset-type={assetType} data-testid="download-csv" />
	),
}));

jest.mock('shared/components/download-report/DownloadPDFReport', () => ({
	__esModule: true,
	default: () => <div data-testid="download-pdf" />,
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
	default: () => <div data-testid="account-dropdown" />,
}));

jest.mock('shared/components/SegmentDropdown', () => ({
	__esModule: true,
	default: ({
		assetId,
		assetType,
		initialSegmentId,
		initialSegmentName,
	}: {
		assetId?: string;
		assetType?: string;
		initialSegmentId?: string;
		initialSegmentName?: string;
	}) => (
		<div
			data-asset-id={assetId}
			data-asset-type={assetType}
			data-initial-segment-id={initialSegmentId}
			data-initial-segment-name={initialSegmentName}
			data-testid="segment-dropdown"
		/>
	),
}));

jest.mock('shared/context/channel', () => ({
	useChannelContext: () => ({selectedChannel: {name: 'test channel'}}),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: () => ({empty: false}),
}));

jest.mock('shared/hooks/useQueryRangeSelectors', () => ({
	useQueryRangeSelectors: () => ({rangeKey: '30'}),
}));

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn(),
}));

const renderDashboard = (tabId?: string, slug = 'blogs') =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<AssetDashboard
					className=""
					router={
						{
							params: {
								assetId: 'asset-1',
								channelId: '1',
								groupId: '2',
								tabId,
								title: 'my blog',
								touchpoint: 'http://example.com/web/site/blog',
								type: 'Blog',
							},
							query: {},
						} as any
					}
					slug={slug}
				/>
			</MemoryRouter>
		</Provider>
	);

describe('AssetDashboard', () => {
	beforeEach(() => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);
	});

	// The sub headers used to be chosen by comparing the matched route against
	// a per asset type constant. They key off the tab instead, so that one
	// dynamic route can serve every asset type.

	it('shows the overview sub header when no tab is selected', () => {
		renderDashboard();

		expect(screen.getByTestId('account-dropdown')).toBeInTheDocument();
		expect(screen.getByTestId('download-pdf')).toBeInTheDocument();
		expect(screen.queryByTestId('download-csv')).toBeNull();
	});

	it('shows an unscoped segment filter on the accounts tab', () => {
		renderDashboard('accounts');

		expect(screen.queryByTestId('account-dropdown')).toBeNull();
		expect(screen.queryByTestId('download-pdf')).toBeNull();

		expect(screen.getByTestId('segment-dropdown')).not.toHaveAttribute(
			'data-asset-id'
		);
		expect(screen.getByTestId('segment-dropdown')).not.toHaveAttribute(
			'data-asset-type'
		);
	});

	it('offers the CSV export on the known individuals tab', () => {
		renderDashboard('known-individuals');

		expect(screen.getByTestId('download-csv')).toHaveAttribute(
			'data-asset-type',
			'blog'
		);
		expect(screen.queryByTestId('download-pdf')).toBeNull();
	});

	// Object entries are the one asset type with no known individuals export.

	it('offers no CSV export for object entries', () => {
		renderDashboard('known-individuals', 'object-entry');

		expect(screen.queryByTestId('download-csv')).toBeNull();
	});
});
