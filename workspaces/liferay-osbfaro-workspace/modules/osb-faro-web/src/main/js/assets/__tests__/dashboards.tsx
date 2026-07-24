import Blog from 'assets/blog/pages';
import DocumentAndMedia from 'assets/document-and-media/pages';
import Form from 'assets/form/pages';
import mockStore from 'test/mock-store';
import ObjectEntry from 'assets/object-entry/pages';
import React from 'react';
import WebContent from 'assets/web-content/pages';
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
		getMatchedRoute: jest.fn(),
	};
});

/**
 * Every asset dashboard renders the account filter the same way, so the suite
 * below runs once per dashboard.
 */

const DASHBOARDS = [
	{
		assetType: 'blog',
		Component: Blog,
		label: 'Blog',
		name: 'Blog',
		overviewRoute: Routes.ASSETS_BLOGS_OVERVIEW,
		slug: 'blog',
	},
	{
		assetType: 'document',
		Component: DocumentAndMedia,
		label: 'Document',
		name: 'DocumentAndMedia',
		overviewRoute: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
		slug: 'document',
	},
	{
		assetType: 'form',
		Component: Form,
		label: 'Form',
		name: 'Form',
		overviewRoute: Routes.ASSETS_FORMS_OVERVIEW,
		slug: 'form',
	},
	{
		assetType: 'objectEntry',
		Component: ObjectEntry,
		label: 'Object Entry',
		name: 'ObjectEntry',
		overviewRoute: Routes.ASSETS_OBJECT_ENTRY_OVERVIEW,
		slug: 'object-entry',
	},
	{
		assetType: 'journal',
		Component: WebContent,
		label: 'Web Content',
		name: 'WebContent',
		overviewRoute: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
		slug: 'web-content',
	},
];

describe.each(DASHBOARDS)(
	'$name',
	({Component, assetType, label, overviewRoute, slug}) => {
		const router = {
			params: {
				assetId: '123',
				channelId: '456',
				groupId: '789',
				title: `${label} Title`,
				touchpoint: `https://liferay.com/${slug}`,
				type: label,
			},
			query: {},
		};

		const renderDashboard = (initialEntries = ['/']) =>
			render(
				<Provider store={mockStore()}>
					<MemoryRouter initialEntries={initialEntries}>
						<Component className="" router={router as any} />
					</MemoryRouter>
				</Provider>
			);

		beforeEach(() => {
			(getMatchedRoute as jest.Mock).mockReturnValue(overviewRoute);
		});

		it('shows the account filter on the overview route for LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(true);

			renderDashboard();

			expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
				'data-asset-type',
				assetType
			);
		});

		it('hides the account filter on the overview route for non-LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(false);

			renderDashboard();

			expect(screen.queryByTestId('filter-by-account')).toBeNull();
		});

		it('seeds the account filter from the accountId/accountName URL query params', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(true);

			renderDashboard(['/?accountId=100&accountName=Account+100']);

			expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
				'data-initial-account-id',
				'100'
			);
			expect(screen.getByTestId('filter-by-account')).toHaveAttribute(
				'data-initial-account-name',
				'Account 100'
			);
		});

		it('shows the visitors tab for LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(true);

			renderDashboard();

			expect(screen.queryByText('Visitors')).toBeTruthy();
		});

		it('hides the visitors tab for non-LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(false);

			renderDashboard();

			expect(screen.queryByText('Visitors')).toBeNull();
		});

		it('shows the known individuals tab for non-LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(false);

			renderDashboard();

			expect(screen.queryByText('Known Individuals')).toBeTruthy();
		});

		it('hides the known individuals tab for LDP workspaces', () => {
			(useLDPEnabled as jest.Mock).mockReturnValue(true);

			renderDashboard();

			expect(screen.queryByText('Known Individuals')).toBeNull();
		});
	}
);
