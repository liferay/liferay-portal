import DocumentAndMedia from '../index';
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
	default: ({
		assetType,
		initialAccountId,
		initialAccountName,
	}: {
		assetType: string;
		initialAccountId?: string;
		initialAccountName?: string;
	}) => (
		<div
			data-asset-type={assetType}
			data-initial-account-id={initialAccountId}
			data-initial-account-name={initialAccountName}
			data-testid="filter-by-account"
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
		getMatchedRoute: jest.fn(
			() => actual.Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW
		),
	};
});

describe('DocumentAndMedia', () => {
	const router = {
		params: {
			assetId: '123',
			channelId: '456',
			groupId: '789',
			title: 'Document Title',
			touchpoint: 'https://liferay.com/document',
			type: 'Document',
		},
		query: {},
	};

	beforeEach(() => {
		(getMatchedRoute as jest.Mock).mockReturnValue(
			Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW
		);
	});

	it('shows the account filter on the overview route for LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<DocumentAndMedia className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
			'data-asset-type',
			'document'
		);
	});

	it('hides the account filter on the overview route for non-LDP workspaces', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(false);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<DocumentAndMedia className="" router={router as any} />
				</MemoryRouter>
			</Provider>
		);

		expect(screen.queryByTestId('filter-by-account')).toBeNull();
	});

	it('seeds the account filter from the accountId/accountName URL query params', () => {
		(useLDPEnabled as jest.Mock).mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<DocumentAndMedia
						className=""
						router={
							{
								...router,
								query: {
									accountId: '100',
									accountName: 'Account 100',
								},
							} as any
						}
					/>
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
});
