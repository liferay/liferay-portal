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

import GroupsControl from '../../../../../../src/main/resources/META-INF/resources/revamp/js/components/forms/group_selector/GroupsControl';
import {
	AssetLibrary,
	Site,
} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';

const SITES: Site[] = [
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

const SPACES: AssetLibrary[] = [
	{
		descriptiveName: 'Book',
		existsInInstance: true,
		externalReferenceCode: 'erc-book',
		type: 'Space',
	},
	{
		descriptiveName: 'Catalog',
		existsInInstance: false,
		externalReferenceCode: 'erc-catalog',
		type: 'Space',
	},
];

const GROUPS = {
	site: {
		groups: SITES,
		labels: {
			empty: 'no-sites-are-selected',
			select: 'select-sites',
			title: 'sites',
		},
		picked: 'Support',
		pickedCode: 'erc-support',
	},
	space: {
		groups: SPACES,
		labels: {
			empty: 'no-spaces-are-selected',
			select: 'select-spaces',
			title: 'spaces',
		},
		picked: 'Catalog',
		pickedCode: 'erc-catalog',
	},
};

const renderControl = (
	props: Partial<React.ComponentProps<typeof GroupsControl>> = {}
) => {
	const onChange = jest.fn();

	const renderResult = render(
		<GroupsControl
			groups={SITES}
			onChange={onChange}
			selectedExternalReferenceCodes={[]}
			totalCount={SITES.length}
			type="site"
			{...props}
		/>
	);

	return {...renderResult, onChange};
};

const openDialog = (label = 'select-sites') =>
	userEvent.click(screen.getByRole('button', {name: label}));

const findRow = async (title: string) =>
	(await screen.findByText(title)).closest('tr') as HTMLElement;

const getColumnHeaders = () =>
	screen
		.getAllByRole('columnheader')
		.map((columnHeader) => columnHeader.textContent);

const toPage = (items: Site[] | AssetLibrary[]) =>
	JSON.stringify({
		items,
		lastPage: 1,
		page: 1,
		pageSize: 20,
		totalCount: items.length,
	});

const SITES_PAGE = toPage(SITES);

describe('GroupsControl', () => {
	beforeAll(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) =>
				({
					'selected-x': 'Selected {0}',
					'x-items': '{0} Items',
				})[key] ?? key
		);
	});

	afterAll(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) => key
		);
	});

	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(SITES_PAGE);
	});

	describe.each(['site', 'space'] as const)('selecting %ss', (type) => {
		const {groups, labels, picked, pickedCode} = GROUPS[type];

		const renderGroups = (
			props: Partial<React.ComponentProps<typeof GroupsControl>> = {}
		) => renderControl({groups, totalCount: groups.length, type, ...props});

		it('names the section and its empty state', () => {
			renderGroups();

			expect(screen.getByText(labels.title)).toBeInTheDocument();
			expect(screen.getByText(labels.empty)).toBeInTheDocument();
		});

		it('shows how many are available', () => {
			renderGroups();

			expect(screen.getByText('2 Items')).toBeInTheDocument();
		});

		it('names the selected ones', () => {
			renderGroups({selectedExternalReferenceCodes: [pickedCode]});

			expect(screen.getByText(`Selected ${picked}`)).toBeInTheDocument();
		});

		it('opens a dialog named after the type', async () => {
			renderGroups();

			await openDialog(labels.select);

			expect(
				await screen.findByRole('dialog', {name: labels.select})
			).toBeInTheDocument();
		});

		it('hands the picked groups back to the row', async () => {
			const {onChange} = renderGroups();

			await openDialog(labels.select);

			const row = await findRow(picked);

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(onChange).toHaveBeenCalledWith([pickedCode]);
		});

		it('reopening keeps what was already picked', async () => {
			renderGroups({selectedExternalReferenceCodes: [pickedCode]});

			await openDialog(labels.select);

			const row = await findRow(picked);

			expect(within(row).getByRole('checkbox')).toBeChecked();
		});
	});

	it('names selected sites that go by the same name rather than counting them', () => {
		renderControl({
			groups: [SITES[0], {...SITES[1], descriptiveName: 'Marketing'}],
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

	it.each([
		['site', 'export', ['title', 'path', 'child-sites']],
		['site', 'import', ['title', 'path', 'exists-in-instance']],
		['space', 'export', ['title']],
		['space', 'import', ['title', 'exists-in-instance']],
	] as const)(
		'reads the %s columns of an %s as %j',
		async (type, process, columns) => {
			const {groups, labels, picked} = GROUPS[type];

			renderControl({groups, process, totalCount: groups.length, type});

			await openDialog(labels.select);

			await screen.findByText(picked);

			expect(getColumnHeaders()).toEqual([
				expect.anything(),
				...columns,
				'manage-columns-visibility',
			]);
		}
	);

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

	describe('reading the groups from the API', () => {
		const API_URL = '/o/export-import/v1.0/export-preview/sites';

		it('hands the picked sites back to the row', async () => {
			const {onChange} = renderControl({
				apiURL: API_URL,
				groups: undefined,
			});

			await openDialog();

			const row = await findRow('Support');

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(onChange).toHaveBeenCalledWith(['erc-support']);
		});

		it('hands the picked spaces back to the row', async () => {
			fetch.mockResponse(toPage(SPACES));

			const {onChange} = renderControl({
				apiURL: '/o/export-import/v1.0/export-preview/asset-libraries',
				groups: undefined,
				type: 'space',
			});

			await openDialog('select-spaces');

			const row = await findRow('Catalog');

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(onChange).toHaveBeenCalledWith(['erc-catalog']);
		});

		it('names the sites after they are picked', async () => {
			const {rerender} = renderControl({
				apiURL: API_URL,
				groups: undefined,
			});

			await openDialog();

			const row = await findRow('Support');

			await userEvent.click(within(row).getByRole('checkbox'));

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			rerender(
				<GroupsControl
					apiURL={API_URL}
					onChange={jest.fn()}
					selectedExternalReferenceCodes={['erc-support']}
					totalCount={2}
					type="site"
				/>
			);

			expect(
				await screen.findByText('Selected Support')
			).toBeInTheDocument();
		});

		it('reopening keeps what was already picked', async () => {
			renderControl({
				apiURL: API_URL,
				groups: undefined,
				selectedExternalReferenceCodes: ['erc-support'],
			});

			await openDialog();

			const row = await findRow('Support');

			expect(within(row).getByRole('checkbox')).toBeChecked();
		});

		it('shows where each site sits', async () => {
			renderControl({apiURL: API_URL, groups: undefined});

			await openDialog();

			const row = await findRow('Support');

			expect(
				within(row).getByText('Global / Marketing / Support')
			).toBeInTheDocument();
		});

		it('asks the API for the sites in ascending order', async () => {
			renderControl({apiURL: API_URL, groups: undefined});

			await openDialog();

			await screen.findByText('Support');

			expect(fetch.mock.calls[0][0]).toContain(
				'sort=descriptiveName%3Aasc'
			);
		});

		it('asks the API for the sites in descending order', async () => {
			renderControl({apiURL: API_URL, groups: undefined});

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

			renderControl({apiURL: API_URL, groups: undefined});

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
		renderControl({groups: [...SITES].reverse()});

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
