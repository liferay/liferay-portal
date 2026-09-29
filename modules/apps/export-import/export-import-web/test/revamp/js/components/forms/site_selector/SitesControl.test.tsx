/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import SitesControl from '../../../../../../src/main/resources/META-INF/resources/revamp/js/components/forms/site_selector/SitesControl';
import {PreviewSite} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';

const PREVIEW_SITES: PreviewSite[] = [
	{
		childSitesCount: 1,
		descriptiveName: 'Marketing',
		existsInInstance: true,
		externalReferenceCode: 'erc-marketing',
		path: 'Global / Marketing',
	},
	{
		childSitesCount: 0,
		descriptiveName: 'Support',
		existsInInstance: false,
		externalReferenceCode: 'erc-support',
		path: 'Global / Marketing / Support',
	},
];

const renderControl = (
	props: Partial<React.ComponentProps<typeof SitesControl>> = {}
) => {
	const onChange = jest.fn();

	const renderResult = render(
		<SitesControl
			onChange={onChange}
			previewSites={PREVIEW_SITES}
			selectedExternalReferenceCodes={[]}
			totalCount={PREVIEW_SITES.length}
			{...props}
		/>
	);

	return {...renderResult, onChange};
};

const openDialog = () =>
	userEvent.click(screen.getByRole('button', {name: 'select-sites'}));

const findRow = async (title: string) =>
	(await screen.findByText(title)).closest('tr') as HTMLElement;

const PREVIEW_SITES_PAGE = JSON.stringify({
	items: PREVIEW_SITES,
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: PREVIEW_SITES.length,
});

describe('SitesControl', () => {
	beforeAll(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) =>
				({
					'selected-x': 'Selected {0}',
					'x-items': '{0} Items',
				})[key] ?? key
		);
	});

	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(PREVIEW_SITES_PAGE);
	});

	it('shows the total number of sites available', () => {
		renderControl();

		expect(screen.getByText('sites')).toBeInTheDocument();

		expect(screen.getByText('2 Items')).toBeInTheDocument();
	});

	it('says nothing is selected when nothing is selected', () => {
		renderControl();

		expect(screen.getByText('no-sites-are-selected')).toBeInTheDocument();
	});

	it('names the selected sites', () => {
		renderControl({selectedExternalReferenceCodes: ['erc-support']});

		expect(screen.getByText('Selected Support')).toBeInTheDocument();
	});

	it('names selected sites that go by the same name rather than counting them', () => {
		renderControl({
			previewSites: [
				PREVIEW_SITES[0],
				{...PREVIEW_SITES[1], descriptiveName: 'Marketing'},
			],
			selectedExternalReferenceCodes: ['erc-marketing', 'erc-support'],
		});

		expect(
			screen.getByText('Selected Marketing, Marketing')
		).toBeInTheDocument();
	});

	it('offers no way to select sites other than the dialog', () => {
		renderControl();

		expect(screen.queryByRole('checkbox')).not.toBeInTheDocument();
	});

	it('opens the dialog from the link', async () => {
		renderControl();

		await openDialog();

		expect(await screen.findByRole('dialog')).toBeInTheDocument();
	});

	it('hands the picked sites back to the row', async () => {
		const {onChange} = renderControl();

		await openDialog();

		const row = await findRow('Support');

		const checkbox = within(row).getByRole('checkbox');

		await userEvent.click(checkbox);

		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		expect(onChange).toHaveBeenCalledWith(['erc-support']);
	});

	it('reopening keeps what was already picked', async () => {
		renderControl({selectedExternalReferenceCodes: ['erc-support']});

		await openDialog();

		const row = await findRow('Support');

		expect(within(row).getByRole('checkbox')).toBeChecked();
	});

	it('reads title, path and child sites when exporting', async () => {
		renderControl();

		await openDialog();

		await screen.findByText('Support');

		const columnHeaders = screen
			.getAllByRole('columnheader')
			.map((columnHeader) => columnHeader.textContent);

		expect(columnHeaders).toEqual([
			expect.anything(),
			'title',
			'path',
			'child-sites',
			'manage-columns-visibility',
		]);
	});

	it('reads title, path and exists in instance when importing', async () => {
		renderControl({process: 'import'});

		await openDialog();

		await screen.findByText('Support');

		const columnHeaders = screen
			.getAllByRole('columnheader')
			.map((columnHeader) => columnHeader.textContent);

		expect(columnHeaders).toEqual([
			expect.anything(),
			'title',
			'path',
			'exists-in-instance',
			'manage-columns-visibility',
		]);
	});

	it('says whether the instance already has each site', async () => {
		renderControl({process: 'import'});

		await openDialog();

		const existingRow = await findRow('Marketing');

		expect(within(existingRow).getByText('yes')).toBeInTheDocument();

		const newRow = await findRow('Support');

		expect(within(newRow).getByText('no')).toBeInTheDocument();
	});

	it('shows where each site sits', async () => {
		renderControl();

		await openDialog();

		const row = await findRow('Support');

		expect(
			within(row).getByText('Global / Marketing / Support')
		).toBeInTheDocument();
	});

	describe('reading the sites from the API', () => {
		const API_URL = '/o/export-import/v1.0/export-preview/preview-sites';

		it('hands the picked sites back to the row', async () => {
			const {onChange} = renderControl({
				apiURL: API_URL,
				previewSites: undefined,
			});

			await openDialog();

			const row = await findRow('Support');

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(onChange).toHaveBeenCalledWith(['erc-support']);
		});

		it('names the sites after they are picked', async () => {
			const {rerender} = renderControl({
				apiURL: API_URL,
				previewSites: undefined,
			});

			await openDialog();

			const row = await findRow('Support');

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			rerender(
				<SitesControl
					apiURL={API_URL}
					onChange={jest.fn()}
					selectedExternalReferenceCodes={['erc-support']}
					totalCount={2}
				/>
			);

			expect(
				await screen.findByText('Selected Support')
			).toBeInTheDocument();
		});

		it('reopening keeps what was already picked', async () => {
			renderControl({
				apiURL: API_URL,
				previewSites: undefined,
				selectedExternalReferenceCodes: ['erc-support'],
			});

			await openDialog();

			const row = await findRow('Support');

			expect(within(row).getByRole('checkbox')).toBeChecked();
		});

		it('shows where each site sits', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await openDialog();

			const row = await findRow('Support');

			expect(
				within(row).getByText('Global / Marketing / Support')
			).toBeInTheDocument();
		});

		it('asks the API for the sites in ascending order', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await openDialog();

			await screen.findByText('Support');

			expect(fetch.mock.calls[0][0]).toContain(
				'sort=descriptiveName%3Aasc'
			);
		});

		it('asks the API for the sites in descending order', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await openDialog();

			await userEvent.click(
				await screen.findByRole('button', {name: /order\[sort\]/})
			);

			await userEvent.click(screen.getByText('descending'));

			await waitFor(() =>
				expect(
					fetch.mock.calls[fetch.mock.calls.length - 1][0]
				).toContain('sort=descriptiveName%3Adesc')
			);
		});

		it('keeps the page URL untouched while browsing the sites', async () => {
			const search = window.location.search;

			renderControl({apiURL: API_URL, previewSites: undefined});

			await openDialog();

			await userEvent.click(
				await screen.findByRole('button', {name: /order\[sort\]/})
			);

			await userEvent.click(screen.getByText('descending'));

			await waitFor(() =>
				expect(
					fetch.mock.calls[fetch.mock.calls.length - 1][0]
				).toContain('sort=descriptiveName%3Adesc')
			);

			expect(window.location.search).toBe(search);
		});
	});

	it('lists the sites from the file by title', async () => {
		renderControl({previewSites: [...PREVIEW_SITES].reverse()});

		await openDialog();

		await screen.findByText('Support');

		expect(
			screen
				.getAllByText(/^(Marketing|Support)$/)
				.map((title) => title.textContent)
		).toEqual(['Marketing', 'Support']);
	});

	it('offers no sort control when the sites come from the file', async () => {
		renderControl();

		await openDialog();

		await screen.findByText('Support');

		expect(
			screen.queryByRole('button', {name: /order\[sort\]/})
		).not.toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderControl();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
