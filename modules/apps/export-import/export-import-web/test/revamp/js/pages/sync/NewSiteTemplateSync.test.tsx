/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {NewSiteTemplateSync} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/sync/NewSiteTemplateSync';
import {mockPreview} from '../../mocks/mockPreview';
import {
	toMockLayoutSetPortletDataHandler,
	toMockLayoutSetPreviewPortletDataHandler,
	toMockLookAndFeelPortletDataHandler,
	toMockLookAndFeelPreviewPortletDataHandler,
	toMockSiteBuilderPreview,
} from '../../mocks/mockSiteBuilderPreview';

const DEFAULT_PROPS = {
	backURL: '/site-templates',
	pageTreeModalConfiguration: {
		groupId: 20121,
		pageSize: 20,
		privateLayoutsAvailable: false,
		siteExternalReferenceCode: 'site-template-erc',
	},
	siteTemplateName: 'Intranet',
	syncPreviewAPIURL: '/o/export-import/v1.0/site-templates/1/sync-preview',
	syncProcessAPIURL: '/o/export-import/v1.0/site-templates/1/sync-processes',
	timeZoneId: 'UTC',
};

const lookAndFeelControlNames = [
	'THEME_REFERENCE',
	'LOGO',
	'LAYOUT_SET_SETTINGS',
];

const lookAndFeelPortletDataHandler = toMockLookAndFeelPortletDataHandler(
	lookAndFeelControlNames
);

const previewWithPages = toMockSiteBuilderPreview([
	toMockLayoutSetPreviewPortletDataHandler(['private-pages']),
	toMockLookAndFeelPreviewPortletDataHandler(lookAndFeelControlNames),
]);

const getSyncCall = () =>
	fetch.mock.calls.find(
		([url, init]) =>
			String(url).includes('sync-processes') && init?.method === 'POST'
	);

const getSyncRequest = () => JSON.parse(String(getSyncCall()?.[1]?.body));

const renderComponent = (
	props: Partial<React.ComponentProps<typeof NewSiteTemplateSync>> = {}
) => render(<NewSiteTemplateSync {...DEFAULT_PROPS} {...props} />);

const openConfirmation = async () => {
	const syncButton = screen.getByRole('button', {name: /^sync$/i});

	await waitFor(() => expect(syncButton).toBeEnabled());

	await userEvent.click(syncButton);

	await screen.findByText('sync-linked-sites');

	return screen.getByRole('dialog');
};

const submitSync = async () => {
	const dialog = await openConfirmation();

	await userEvent.click(
		within(dialog).getByRole('button', {name: 'sync-sites'})
	);
};

describe('NewSiteTemplateSync', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		fetch.resetMocks();
		fetch.mockResponse(async (request) => {
			const url = new URL(request.url, window.location.origin);

			if (/\/site-pages\/[^/]+\/site-pages$/.test(url.pathname)) {
				return JSON.stringify({items: [], totalCount: 0});
			}

			if (url.pathname.endsWith('/site-pages')) {
				return JSON.stringify({
					items: [
						{
							externalReferenceCode: 'page-1-erc',
							name_i18n: {'en-US': 'page-1'},
						},
						{
							externalReferenceCode: 'page-2-erc',
							name_i18n: {'en-US': 'page-2'},
						},
					],
					lastPage: 1,
					totalCount: 2,
				});
			}

			if (request.url.includes('sync-processes')) {
				return {body: '', init: {status: 204}};
			}

			return JSON.stringify(mockPreview);
		});
	});

	it('fetches the preview on mount when it is not provided', async () => {
		renderComponent();

		await screen.findByText('loaded');

		expect(fetch).toHaveBeenCalledTimes(1);
		expect(fetch.mock.calls[0][0]).toBe(DEFAULT_PROPS.syncPreviewAPIURL);
	});

	it('renders only the data selection step', async () => {
		const {container} = renderComponent({syncPreview: mockPreview});

		await screen.findByText('loaded');

		expect(
			screen.getByText('site-template-sync-description')
		).toBeInTheDocument();
		expect(screen.getByText('select-data-to-sync')).toBeInTheDocument();
		expect(
			screen.getByText('choose-what-to-sync-from-x')
		).toBeInTheDocument();
		expect(screen.queryByRole('textbox')).not.toBeInTheDocument();
		expect(
			screen.queryByRole('checkbox', {name: /export-permissions/})
		).not.toBeInTheDocument();
		expect(screen.queryByText('filter-content-by')).not.toBeInTheDocument();

		await checkAccessibility({
			context: {
				exclude: ['[data-testid="data-selection-section"]'],
				include: [container],
			},
		});
	});

	it('renders the error alert when the API fails', async () => {
		fetch.mockResponse(JSON.stringify({title: 'Boom'}), {status: 500});

		renderComponent();

		expect(await screen.findByText('Boom')).toBeInTheDocument();
	});

	it('asks for confirmation before syncing', async () => {
		renderComponent({syncPreview: mockPreview});

		const dialog = await openConfirmation();

		expect(getSyncCall()).toBeUndefined();

		await userEvent.click(
			within(dialog).getByRole('button', {name: 'sync-sites'})
		);

		await waitFor(() => expect(getSyncCall()).toBeDefined());
	});

	it('syncs once when the confirmation is clicked twice', async () => {
		renderComponent({syncPreview: mockPreview});

		const dialog = await openConfirmation();

		const syncSitesButton = within(dialog).getByRole('button', {
			name: 'sync-sites',
		});

		fireEvent.click(syncSitesButton);
		fireEvent.click(syncSitesButton);

		await waitFor(() => expect(Liferay.Util.navigate).toHaveBeenCalled());

		expect(
			fetch.mock.calls.filter(
				([url, init]) =>
					String(url).includes('sync-processes') &&
					init?.method === 'POST'
			)
		).toHaveLength(1);
	});

	it('syncs all private pages and the look and feel by default and goes back', async () => {
		renderComponent({syncPreview: previewWithPages});

		await submitSync();

		await waitFor(() =>
			expect(getSyncRequest().portletDataHandlers).toEqual([
				toMockLayoutSetPortletDataHandler('private-pages'),
				lookAndFeelPortletDataHandler,
			])
		);

		await waitFor(() =>
			expect(Liferay.Util.navigate).toHaveBeenCalledWith(
				'/site-templates'
			)
		);

		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'success'})
		);
	});

	it('syncs a private page subset through the modal', async () => {
		renderComponent({syncPreview: previewWithPages});

		await userEvent.click(screen.getByRole('button', {name: 'expand-x'}));

		await userEvent.click(screen.getByRole('button', {name: 'select-x'}));

		expect(await screen.findByText('pages-to-sync')).toBeInTheDocument();
		expect(
			await screen.findByRole('checkbox', {name: 'page-1'})
		).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'page-2'})).toBeChecked();

		await userEvent.click(screen.getByRole('checkbox', {name: 'page-2'}));
		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		await submitSync();

		await waitFor(() =>
			expect(getSyncRequest().portletDataHandlers).toEqual([
				toMockLayoutSetPortletDataHandler('private-pages', {
					excludedItemExternalReferenceCodes: ['page-2-erc'],
					selectAll: true,
				}),
				lookAndFeelPortletDataHandler,
			])
		);
	});

	it('shows the escaped error toast and stays on the page when the sync fails', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('sync-processes')) {
				return {
					body: JSON.stringify({title: 'No page <x>'}),
					init: {status: 400},
				};
			}

			return JSON.stringify(mockPreview);
		});

		renderComponent({syncPreview: mockPreview});

		await screen.findByText('loaded');

		await submitSync();

		await waitFor(() =>
			expect(Liferay.Util.openToast).toHaveBeenCalledWith({
				message: 'No page &lt;x&gt;',
				type: 'danger',
			})
		);

		expect(Liferay.Util.navigate).not.toHaveBeenCalled();
	});

	it('disables the sync button and shows the error when nothing is selected', async () => {
		renderComponent({syncPreview: previewWithPages});

		await screen.findByText('loaded');

		await userEvent.click(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		);

		expect(
			await screen.findByText(
				'please-select-at-least-one-entity-type-to-continue'
			)
		).toBeInTheDocument();
		expect(screen.getByRole('button', {name: /^sync$/i})).toBeDisabled();
	});
});
