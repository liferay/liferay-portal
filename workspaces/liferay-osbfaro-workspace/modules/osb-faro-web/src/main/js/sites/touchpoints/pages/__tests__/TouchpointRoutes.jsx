import mockStore from 'test/mock-store';
import React from 'react';
import TouchpointRoutes from '../TouchpointRoutes';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {getMatchedRoute, Routes} from 'shared/util/router';
import {render, screen} from '@testing-library/react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/components/download-report/DownloadCSVReport', () => ({
	__esModule: true,
	default: ({assetId}) => (
		<div data-asset-id={assetId} data-testid='download-csv-report' />
	)
}));

jest.mock('shared/components/download-report/DownloadPDFReport', () => ({
	__esModule: true,
	default: () => null
}));

jest.mock('shared/components/dropdown-range-key/DropdownRangeKey', () => ({
	DropdownRangeKey: () => null
}));

jest.mock('shared/components/Loading', () => ({
	__esModule: true,
	default: () => null
}));

jest.mock('shared/components/RouteNotFound', () => ({
	__esModule: true,
	default: () => null
}));

jest.mock('route-middleware/BundleRouter', () => ({
	__esModule: true,
	default: () => null
}));

jest.mock('../../components/ExperienceDropdown', () => ({
	__esModule: true,
	default: () => <div data-testid='experience-dropdown' />
}));

jest.mock('shared/components/AccountDropdown', () => ({
	__esModule: true,
	default: ({initialAccountId, initialAccountName}) => (
		<div
			data-initial-account-id={initialAccountId}
			data-initial-account-name={initialAccountName}
			data-testid='account-dropdown'
		/>
	)
}));

jest.mock('../../components/SegmentDropdown', () => ({
	__esModule: true,
	default: () => null
}));

jest.mock('shared/context/channel', () => ({
	useChannelContext: () => ({selectedChannel: {name: 'test channel'}})
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: () => ({empty: false})
}));

jest.mock('shared/hooks/useQueryRangeSelectors', () => ({
	useQueryRangeSelectors: () => ({rangeKey: '30'})
}));

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn()
}));

jest.mock('shared/util/router', () => {
	const actual = jest.requireActual('shared/util/router');

	return {
		...actual,
		getMatchedRoute: jest.fn(
			() => actual.Routes.SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS
		)
	};
});

describe('TouchpointRoutes', () => {
	beforeEach(() => {
		getMatchedRoute.mockReturnValue(
			Routes.SITES_TOUCHPOINTS_KNOWN_INDIVIDUALS
		);
		useLDPEnabled.mockReturnValue(false);
	});

	it('forwards a percent-encoded touchpoint as assetId for the Known Individuals CSV download', () => {
		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/人事発告'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(
			screen
				.getByTestId('download-csv-report')
				.getAttribute('data-asset-id')
		).toBe(
			'http://example.com/web/site/%E4%BA%BA%E4%BA%8B%E7%99%BA%E5%91%8A'
		);
	});

	it('shows the experience filter dropdown on the overview route for LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('experience-dropdown')).toBeTruthy();
	});

	it('hides the experience filter dropdown on the overview route for non-LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(false);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('experience-dropdown')).toBeNull();
	});

	it('shows the visitors tab for LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Visitors')).toBeTruthy();
	});

	it('hides the visitors tab for non-LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(false);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Visitors')).toBeNull();
	});

	it('shows the known individuals tab for non-LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(false);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Known Individuals')).toBeTruthy();
	});

	it('hides the known individuals tab for LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByText('Known Individuals')).toBeNull();
	});

	it('shows the account filter on the overview route for LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('account-dropdown')).toBeTruthy();
	});

	it('hides the account filter on the overview route for non-LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(false);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('account-dropdown')).toBeNull();
	});

	it('shows the account filter on the path route for LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_PATH);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('account-dropdown')).toBeTruthy();
	});

	it('hides the account filter on the path route for non-LDP workspaces', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_PATH);
		useLDPEnabled.mockReturnValue(false);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('account-dropdown')).toBeNull();
	});

	it('seeds the account filter from the accountId/accountName URL query params on the overview route', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_OVERVIEW);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter
					initialEntries={['/?accountId=100&accountName=Account+100']}
				>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('account-dropdown')).toHaveAttribute(
			'data-initial-account-id',
			'100'
		);
		expect(screen.getByTestId('account-dropdown')).toHaveAttribute(
			'data-initial-account-name',
			'Account 100'
		);
	});

	it('seeds the account filter from the accountId/accountName URL query params on the path route', () => {
		getMatchedRoute.mockReturnValue(Routes.SITES_TOUCHPOINTS_PATH);
		useLDPEnabled.mockReturnValue(true);

		const router = {
			params: {
				channelId: '1',
				experienceId: '',
				groupId: '2',
				title: 'page',
				touchpoint: 'http://example.com/web/site/home'
			},
			query: {}
		};

		render(
			<Provider store={mockStore()}>
				<MemoryRouter
					initialEntries={['/?accountId=100&accountName=Account+100']}
				>
					<TouchpointRoutes router={router} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('account-dropdown')).toHaveAttribute(
			'data-initial-account-id',
			'100'
		);
	});
});
