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
		childSiteCount: 1,
		descriptiveName: 'Marketing',
		existsInInstance: true,
		externalReferenceCode: 'erc-marketing',
		path: 'Global / Marketing',
	},
	{
		childSiteCount: 0,
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

const PREVIEW_SITES_PAGE = JSON.stringify({
	items: PREVIEW_SITES,
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: PREVIEW_SITES.length,
});

describe('SitesControl', () => {

	// The data set can still have a request in flight when a test ends. The
	// shared teardown takes the mock away, so the answer would arrive at
	// whichever test runs next and fail it. Every test keeps one installed.

	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(PREVIEW_SITES_PAGE);
	});

	it('shows the total number of sites available', () => {
		renderControl();

		expect(screen.getByText('sites')).toBeInTheDocument();

		// The shared mock substitutes into keys shaped like "x-"

		expect(screen.getByText('2-items')).toBeInTheDocument();
	});

	it('says nothing is selected when nothing is selected', () => {
		renderControl();

		expect(screen.getByText('no-sites-are-selected')).toBeInTheDocument();
	});

	it('names the selected sites', () => {
		renderControl({selectedExternalReferenceCodes: ['erc-support']});

		expect(screen.getByText('selected-Support')).toBeInTheDocument();
	});

	it('names both selected sites that go by the same name', () => {

		// The path is what tells two sites of the same name apart, so naming
		// one of them would be naming the wrong number of sites

		renderControl({
			previewSites: [
				PREVIEW_SITES[0],
				{...PREVIEW_SITES[1], descriptiveName: 'Marketing'},
			],
			selectedExternalReferenceCodes: ['erc-marketing', 'erc-support'],
		});

		expect(
			screen.getByText('selected-Marketing, Marketing')
		).toBeInTheDocument();
	});

	it('counts the selected sites when they cannot be named', () => {
		renderControl({
			previewSites: undefined,
			selectedExternalReferenceCodes: ['erc-marketing', 'erc-support'],
		});

		expect(screen.getByText('2-sites-are-selected')).toBeInTheDocument();
	});

	it('offers no way to select sites other than the dialog', () => {
		renderControl();

		expect(screen.queryByRole('checkbox')).not.toBeInTheDocument();
	});

	it('opens the dialog from the link', async () => {
		renderControl();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		expect(await screen.findByRole('dialog')).toBeInTheDocument();
	});

	it('hands the picked sites back to the row', async () => {
		const {onChange} = renderControl();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		const row = await screen.findByText('Support');

		const checkbox = within(row.closest('tr') as HTMLElement).getByRole(
			'checkbox'
		);

		await userEvent.click(checkbox);

		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		expect(onChange).toHaveBeenCalledWith(['erc-support']);
	});

	it('reopening keeps what was already picked', async () => {
		renderControl({selectedExternalReferenceCodes: ['erc-support']});

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		const row = await screen.findByText('Support');

		expect(
			within(row.closest('tr') as HTMLElement).getByRole('checkbox')
		).toBeChecked();
	});

	it('reads title, path and child sites when exporting', async () => {
		renderControl();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		await screen.findByText('Support');

		const columnHeaders = screen
			.getAllByRole('columnheader')
			.map((columnHeader) => columnHeader.textContent);

		// Exporting asks how many sites sit below the one being picked, and has
		// nothing to say about an instance to import into. The first and last
		// headers belong to the data set: the select-all box and the control
		// that hides columns.

		expect(columnHeaders).toEqual([
			expect.anything(),
			'title',
			'path',
			'child-sites',
			'manage-columns-visibility',
		]);
	});

	it('reads title, path and exists in instance when importing', async () => {
		renderControl({showExistsInInstance: true});

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

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
		renderControl({showExistsInInstance: true});

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		const existingRow = (await screen.findByText('Marketing')).closest(
			'tr'
		) as HTMLElement;

		expect(within(existingRow).getByText('yes')).toBeInTheDocument();

		const newRow = screen.getByText('Support').closest('tr') as HTMLElement;

		expect(within(newRow).getByText('no')).toBeInTheDocument();
	});

	it('shows where each site sits', async () => {
		renderControl();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		const row = (await screen.findByText('Support')).closest(
			'tr'
		) as HTMLElement;

		// A child and its parent are listed side by side, so the path is what
		// tells them apart

		expect(
			within(row).getByText('Global / Marketing / Support')
		).toBeInTheDocument();
	});

	describe('reading the sites from the API', () => {
		const API_URL = '/o/export-import/v1.0/export-preview/sites';

		beforeEach(() => {
			fetch.resetMocks();
			fetch.mockResponse(PREVIEW_SITES_PAGE);
		});

		it('hands the picked sites back to the row', async () => {
			const {onChange} = renderControl({
				apiURL: API_URL,
				previewSites: undefined,
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			const row = await screen.findByText('Support');

			await userEvent.click(
				within(row.closest('tr') as HTMLElement).getByRole('checkbox')
			);

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(onChange).toHaveBeenCalledWith(['erc-support']);
		});

		it('names the sites after they are picked', async () => {
			const {rerender} = renderControl({
				apiURL: API_URL,
				previewSites: undefined,
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			const row = await screen.findByText('Support');

			await userEvent.click(
				within(row.closest('tr') as HTMLElement).getByRole('checkbox')
			);

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
				await screen.findByText('selected-Support')
			).toBeInTheDocument();
		});

		it('reopening keeps what was already picked', async () => {
			renderControl({
				apiURL: API_URL,
				previewSites: undefined,
				selectedExternalReferenceCodes: ['erc-support'],
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			const row = await screen.findByText('Support');

			expect(
				within(row.closest('tr') as HTMLElement).getByRole('checkbox')
			).toBeChecked();
		});

		it('shows where each site sits', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			const row = (await screen.findByText('Support')).closest(
				'tr'
			) as HTMLElement;

			// The path is read straight off the site the API answers with, the
			// same as on the side that works from the file

			expect(
				within(row).getByText('Global / Marketing / Support')
			).toBeInTheDocument();
		});

		it('asks the API for the sites in ascending order', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			await screen.findByText('Support');

			expect(fetch.mock.calls[0][0]).toContain(
				'sort=descriptiveName%3Aasc'
			);
		});

		it('offers the order of the sites to be changed', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

			expect(
				await screen.findByRole('button', {name: /order\[sort\]/})
			).toBeInTheDocument();
		});

		it('asks the API for the sites in descending order', async () => {
			renderControl({apiURL: API_URL, previewSites: undefined});

			await userEvent.click(
				screen.getByRole('button', {name: 'select-sites'})
			);

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
	});

	it('leaves the order alone when the sites come from the file', async () => {
		renderControl();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		await screen.findByText('Support');

		// The data set cannot sort a list handed to it, so offering the control
		// would be offering something that does nothing

		expect(
			screen.queryByRole('button', {name: /order\[sort\]/})
		).not.toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderControl();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
