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

describe('Blog', () => {
	const router = {
		params: {
			assetId: '123',
			channelId: '456',
			groupId: '789',
			title: 'Blog Title',
			touchpoint: 'https://liferay.com/blog',
			type: 'Blog',
		},
		query: {},
	};

	beforeEach(() => {
		(getMatchedRoute as jest.Mock).mockReturnValue(
			Routes.ASSETS_BLOGS_OVERVIEW
		);
	});

	it('shows the visitors tab for LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Blog className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Visitors')).toBeTruthy();
	});

	it('hides the visitors tab for non-LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(false);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Blog className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Visitors')).toBeNull();
	});

	it('shows the known individuals tab for non-LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(false);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Blog className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Known Individuals')).toBeTruthy();
	});

	it('hides the known individuals tab for LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Blog className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Known Individuals')).toBeNull();
	});
});
