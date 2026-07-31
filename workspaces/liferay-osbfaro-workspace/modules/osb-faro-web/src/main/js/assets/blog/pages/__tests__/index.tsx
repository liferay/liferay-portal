import Blog from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {getMatchedRoute, Routes} from 'shared/util/router';
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

jest.mock('shared/util/router', () => {
	const actual = jest.requireActual('shared/util/router');

	return {
		...actual,
		getMatchedRoute: jest.fn(() => actual.Routes.ASSETS_BLOGS_OVERVIEW),
	};
});

const ROUTER = {
	className: '',
	params: {
		assetId: 'asset-1',
		channelId: '1',
		groupId: '2',
		title: 'my blog',
		touchpoint: 'http://example.com/web/site/blog',
		type: 'Blog',
	},
	query: {},
};

describe('Blog', () => {
	beforeEach(() => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);
	});

	it('shows an unscoped segment filter on the accounts route', () => {
		(getMatchedRoute as jest.Mock).mockReturnValue(
			Routes.ASSETS_BLOGS_ACCOUNTS
		);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Blog className="" router={ROUTER as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('segment-dropdown')).not.toHaveAttribute(
			'data-asset-id'
		);
		expect(screen.getByTestId('segment-dropdown')).not.toHaveAttribute(
			'data-asset-type'
		);
	});
});
