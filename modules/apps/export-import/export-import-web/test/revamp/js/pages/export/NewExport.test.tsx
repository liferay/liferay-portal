/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {NewExport} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/export/NewExport';
import {Preview} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import {
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {toMockPortletDataHandlers} from '../../mocks/mockPortletDataHandlerSections';
import {mockPreview} from '../../mocks/mockPreview';

const expandSection = async (label: string) => {
	const sectionLabel = screen.getByText(label, {selector: 'label'});
	const sheet = sectionLabel.closest('.sheet');

	const button = within(sheet as HTMLElement).getByRole('button', {
		name: /expand-x/,
	});

	await userEvent.click(button);
};

const getExportCall = () =>
	fetch.mock.calls.find(
		([url, init]) =>
			String(url).includes('export-processes') && init?.method === 'POST'
	);

const getFilteredPreviewCall = () =>
	fetch.mock.calls.find(([url]) => String(url).includes('export-preview?'));

const DEFAULT_PROPS = {
	backURL: '/some/back/url',
	exportPreviewAPIURL: '/o/export-import/v1.0/export-preview',
	exportProcessAPIURL: '/o/export-import/v1.0/export-processes',
	pageTreeModalConfiguration: {
		groupId: 20121,
		pageSize: 20,
		privateLayoutsAvailable: false,
	},
	timeZoneId: 'UTC',
};

const INSTANCE_PROPS = {
	exportPreviewSitesAPIURL:
		'/o/export-import/v1.0/export-preview/preview-sites',
	siteSelectionEnabled: true,
};

const renderComponent = (
	props: Partial<React.ComponentProps<typeof NewExport>> = {}
) => render(<NewExport {...DEFAULT_PROPS} {...props} />);

describe('NewExport', () => {
	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(JSON.stringify(mockPreview));
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('preloads the preview without fetching when it is provided', async () => {
		renderComponent({exportPreview: mockPreview});

		await screen.findByText('loaded');

		expect(screen.getByRole('checkbox', {name: 'Design'})).toBeChecked();
		expect(fetch).not.toHaveBeenCalled();
	});

	it('fetches the preview on mount when it is not provided', async () => {
		renderComponent();

		await screen.findByText('loaded');

		expect(fetch).toHaveBeenCalledTimes(1);
		expect(fetch.mock.calls[0][0]).toBe(DEFAULT_PROPS.exportPreviewAPIURL);
	});

	it('renders the export form', async () => {
		const {container} = renderComponent();

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		expect(nameInput).toBeInTheDocument();

		expect(screen.getByText('filter-content-by')).toBeInTheDocument();

		await screen.findByText('loaded');

		await checkAccessibility({
			context: {

				// TODO Drop exclude once content_selector checkboxes expose labels

				exclude: ['[data-testid="data-selection-section"]'],
				include: [container],
			},
		});
	});

	it('checks every section by default', async () => {
		renderComponent();

		await screen.findByText('loaded');

		expect(screen.getByRole('checkbox', {name: 'Design'})).toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		).toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		).toBeChecked();
	});

	it('renders the root model handler tag and description', async () => {
		renderComponent({
			exportPreview: {
				...mockPreview,
				portletDataHandlers: toMockPortletDataHandlers([
					{
						key: 'category.site_administration.content',
						label: 'Content & Data',
						portletDataHandlers: [
							{
								description: 'Child 1, Child 2',
								label: 'Root Handler',
								name: 'handler',
								tag: 'root-object',
							},
						],
					},
				]),
			},
		});

		await screen.findByText('loaded');

		await expandSection('Content & Data');

		expect(screen.getByText('Child 1, Child 2')).toBeInTheDocument();
		expect(screen.getByText('root-object')).toBeInTheDocument();
	});

	it('renders the error alert when the API fails', async () => {
		fetch.resetMocks();
		fetch.mockResponseOnce(JSON.stringify({title: 'boom'}), {status: 500});

		renderComponent();

		expect(await screen.findByText('boom')).toBeInTheDocument();
	});

	it('shows a required error on name when blurred empty', async () => {
		renderComponent();

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});

		await userEvent.click(nameInput);
		nameInput.blur();

		await screen.findByText('this-field-is-required');
	});

	it('keeps the export button disabled while the form is invalid', async () => {
		renderComponent();

		const exportButton = await screen.findByRole('button', {
			name: /^export$/i,
		});

		await waitFor(() => {
			expect(exportButton).toBeDisabled();
		});
	});

	it('shows and clears the selection error as contentSelection toggles', async () => {
		renderComponent();

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		const dataSelectionGroup = screen.getByRole('group', {
			name: 'data-selection',
		});

		await userEvent.click(screen.getByRole('checkbox', {name: 'Design'}));
		await userEvent.click(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		);
		await userEvent.click(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		);

		await screen.findByText(
			'please-select-at-least-one-entity-type-to-continue'
		);
		expect(dataSelectionGroup).toHaveAttribute('aria-invalid', 'true');

		await userEvent.click(screen.getByRole('checkbox', {name: 'Design'}));

		await waitFor(() => {
			expect(
				screen.queryByText(
					'please-select-at-least-one-entity-type-to-continue'
				)
			).not.toBeInTheDocument();
		});
		expect(dataSelectionGroup).not.toHaveAttribute('aria-invalid');
	});

	it('asks for an entity type or a site below both, where sites are on offer', async () => {
		renderComponent(INSTANCE_PROPS);

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		await userEvent.click(screen.getByRole('checkbox', {name: 'Design'}));
		await userEvent.click(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		);
		await userEvent.click(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		);

		const alert = await screen.findByText(
			'please-select-at-least-one-entity-type-or-site-to-continue'
		);

		const dataSelectionGroup = screen.getByRole('group', {
			name: 'data-selection',
		});

		expect(dataSelectionGroup).not.toHaveAttribute('aria-invalid');

		for (const group of [
			screen.getByRole('group', {name: 'scope'}),
			dataSelectionGroup,
		]) {
			expect(
				group.compareDocumentPosition(alert) &
					Node.DOCUMENT_POSITION_FOLLOWING
			).toBeTruthy();
		}
	});

	it('keeps the entity types deselected after exporting only sites', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('preview-sites')) {
				return JSON.stringify({
					items: [
						{
							childSitesCount: 0,
							descriptiveName: 'Support',
							externalReferenceCode: 'erc-support',
							path: 'Global / Support',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 20,
					totalCount: 1,
				});
			}

			if (request.method === 'POST') {
				return JSON.stringify({exportImportConfigurationId: 1});
			}

			return JSON.stringify(mockPreview);
		});

		renderComponent(INSTANCE_PROPS);

		await userEvent.type(
			await screen.findByRole('textbox', {name: /^name/i}),
			'test-file'
		);

		for (const name of ['Design', 'Site Builder', 'Content & Data']) {
			await userEvent.click(screen.getByRole('checkbox', {name}));
		}

		await userEvent.click(
			screen.getByRole('button', {name: 'select-sites'})
		);

		const row = await screen.findByText('Support');

		await userEvent.click(
			within(row.closest('tr') as HTMLElement).getByRole('checkbox')
		);
		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		const exportButton = screen.getByRole('button', {name: /^export$/i});

		await waitFor(() => expect(exportButton).toBeEnabled());

		await userEvent.click(exportButton);

		await waitFor(() => expect(getExportCall()).toBeDefined());

		expect(
			screen.getByRole('checkbox', {name: 'Design'})
		).not.toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		).not.toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		).not.toBeChecked();

		expect(JSON.parse(String(getExportCall()?.[1]?.body))).toMatchObject({
			portletDataHandlers: [],
			siteExternalReferenceCodes: ['erc-support'],
		});
	});

	it('keeps form values after applying a filter', async () => {
		renderComponent();

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		const filterSelect = screen.getByRole('combobox', {
			name: 'filter-content-by',
		});
		await userEvent.selectOptions(filterSelect, 'last');

		await userEvent.click(
			screen.getByRole('button', {name: /show-results/i})
		);

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledTimes(2);
		});

		expect(screen.getByRole('textbox', {name: /^name/i})).toHaveValue(
			'test-file'
		);
	});

	it('keeps the entity types deselected after applying a filter', async () => {
		renderComponent();

		await screen.findByText('loaded');

		for (const name of ['Design', 'Site Builder', 'Content & Data']) {
			await userEvent.click(screen.getByRole('checkbox', {name}));
		}

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'filter-content-by'}),
			'last'
		);

		await userEvent.click(
			screen.getByRole('button', {name: /show-results/i})
		);

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledTimes(2);
		});

		expect(
			await screen.findByRole('checkbox', {name: 'Design'})
		).not.toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		).not.toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		).not.toBeChecked();
	});

	it('ignores a stale filtered preview after the filter is cleared', async () => {
		renderComponent();

		await screen.findByText('loaded');

		let resolveFilteredPreview = () => {};

		fetch.mockResponseOnce(
			() =>
				new Promise((resolve) => {
					resolveFilteredPreview = () =>
						resolve(
							JSON.stringify({
								...mockPreview,
								portletDataHandlers: [],
							})
						);
				})
		);

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'filter-content-by'}),
			'last'
		);
		await userEvent.click(
			screen.getByRole('button', {name: /show-results/i})
		);

		await userEvent.click(screen.getByText('clear-filters'));

		expect(screen.getByText('loaded')).toBeInTheDocument();

		await act(async () => {
			resolveFilteredPreview();

			await new Promise((resolve) => setTimeout(resolve, 0));
		});

		expect(screen.getByRole('checkbox', {name: 'Design'})).toBeChecked();
		expect(screen.getByText('loaded')).toBeInTheDocument();
	});

	it('exports the modified last range as its hours', async () => {
		renderComponent();

		await screen.findByText('loaded');

		await userEvent.type(
			await screen.findByRole('textbox', {name: /^name/i}),
			'test-file'
		);
		await userEvent.click(screen.getByRole('checkbox', {name: 'Design'}));

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'filter-content-by'}),
			'last'
		);
		await userEvent.click(
			screen.getByRole('button', {name: /show-results/i})
		);

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = getExportCall();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.dateFilter).toEqual({hours: 12, type: 'LastHours'});
		});
	});

	it('resolves the date range bounds in the portal time zone', async () => {
		renderComponent({timeZoneId: 'Europe/Madrid'});

		await screen.findByText('loaded');

		await userEvent.click(
			await screen.findByRole('textbox', {name: /^name/i})
		);

		await userEvent.paste('test-file');

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'filter-content-by'}),
			'dateRange'
		);

		await userEvent.click(screen.getByLabelText('from'));

		await userEvent.paste('01/01/2026 08:00 AM');

		await userEvent.click(
			screen.getByRole('button', {name: /show-results/i})
		);

		await waitFor(() => {
			expect(getFilteredPreviewCall()).toBeDefined();
		});

		expect(
			new URL(
				String(getFilteredPreviewCall()![0]),
				'http://localhost'
			).searchParams.get('startDate')
		).toBe('2026-01-01T07:00:00.000Z');

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const body = JSON.parse(getExportCall()![1]!.body as string);

			expect(body.dateFilter).toEqual({
				startDate: '2026-01-01T07:00:00.000Z',
				type: 'DateRange',
			});
		});
	});

	it('enables the export button once the name is set since entities are checked by default', async () => {
		renderComponent();

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});

		const exportButton = screen.getByRole('button', {name: /^export$/i});

		await waitFor(() => {
			expect(exportButton).toBeDisabled();
		});

		await userEvent.type(nameInput, 'test-file');

		await waitFor(() => {
			expect(exportButton).toBeEnabled();
		});
	});

	it('hides the deletions checkbox when the preview has no deletions', async () => {
		fetch.resetMocks();
		fetch.mockResponse(
			JSON.stringify({
				...mockPreview,
				deletionCount: 0,
			})
		);

		renderComponent();

		await screen.findByText('loaded');

		expect(
			screen.queryByLabelText(/^export-individual-deletions/)
		).not.toBeInTheDocument();
	});

	it('shows the deletions checkbox unchecked and toggles it when the preview has deletions', async () => {
		renderComponent();

		const deletionsCheckbox = await screen.findByLabelText(
			/^export-individual-deletions/
		);

		expect(deletionsCheckbox).not.toBeChecked();

		await userEvent.click(deletionsCheckbox);

		expect(deletionsCheckbox).toBeChecked();

		await userEvent.click(deletionsCheckbox);

		expect(deletionsCheckbox).not.toBeChecked();
	});

	describe('page selection', () => {
		const toPreviewWithPages = (choice: string): Preview => ({
			additionCount: 1,
			deletionCount: 0,
			portletDataHandlers: toMockPortletDataHandlers([
				{
					key: 'category.site_administration.build',
					label: 'Site Builder',
					portletDataHandlers: [
						{
							label: 'Pages',
							name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
							portletDataHandlerControls: [
								{
									choice,
									choices: [
										{
											label: 'Public Pages',
											name: 'public-pages',
										},
										{
											label: 'Private Pages',
											name: 'private-pages',
										},
									],
									label: 'Visibility',
									name: CONTROL_NAME_VISIBILITY,
									portletDataHandlerControls: [
										{
											label: 'Pages',
											name: CONTROL_NAME_PAGES,
											selected: true,
											type: 'TreeSelection',
										},
									],
									type: 'Choice',
								},
							],
						},
					],
				},
			]),
		});

		const previewWithPages = toPreviewWithPages('public-pages');

		const toLayoutSetPortletDataHandlers = (choice: string) => [
			{
				name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
				portletDataHandlerControls: [
					{
						choice,
						name: CONTROL_NAME_VISIBILITY,
						portletDataHandlerControls: [
							{
								name: CONTROL_NAME_PAGES,
								selected: true,
								type: 'TreeSelection',
							},
						],
						type: 'Choice',
					},
				],
			},
		];

		const getExportRequest = () =>
			JSON.parse(String(getExportCall()?.[1]?.body));

		const typeFileName = () =>
			userEvent.type(
				screen.getByRole('textbox', {name: /^name/i}),
				'test-file'
			);

		const expandSection = () =>
			userEvent.click(screen.getByRole('button', {name: 'expand-x'}));

		const submitExport = async () => {
			const exportButton = screen.getByRole('button', {
				name: /^export$/i,
			});

			await waitFor(() => expect(exportButton).toBeEnabled());

			await userEvent.click(exportButton);
		};

		beforeEach(() => {
			jest.clearAllMocks();

			fetch.resetMocks();
			fetch.mockResponse(
				JSON.stringify({exportImportConfigurationId: 1})
			);
		});

		it('exports all public pages selected by default', async () => {
			renderComponent({exportPreview: previewWithPages});

			await typeFileName();

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().portletDataHandlers).toEqual(
					toLayoutSetPortletDataHandlers('public-pages')
				)
			);

			await waitFor(() =>
				expect(Liferay.Util.navigate).toHaveBeenCalledWith(
					'/some/back/url'
				)
			);
		});

		it('exports all private pages when private pages are the default', async () => {
			renderComponent({
				exportPreview: toPreviewWithPages('private-pages'),
				pageTreeModalConfiguration: {
					...DEFAULT_PROPS.pageTreeModalConfiguration,
					privateLayoutsAvailable: true,
				},
			});

			await typeFileName();

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().portletDataHandlers).toEqual(
					toLayoutSetPortletDataHandlers('private-pages')
				)
			);
		});

		it('hides the visibility radios when private pages are not available', async () => {
			renderComponent({exportPreview: previewWithPages});

			await expandSection();

			expect(screen.queryAllByRole('radio')).toHaveLength(0);
		});

		it('shows the visibility radios and exports all private pages', async () => {
			renderComponent({
				exportPreview: previewWithPages,
				pageTreeModalConfiguration: {
					...DEFAULT_PROPS.pageTreeModalConfiguration,
					privateLayoutsAvailable: true,
				},
			});

			await typeFileName();
			await expandSection();

			const radios = screen.getAllByRole('radio');

			expect(radios).toHaveLength(2);
			expect(radios[0]).toBeChecked();

			await userEvent.click(radios[1]);

			expect(screen.getAllByRole('radio')[1]).toBeChecked();
			expect(screen.getByRole('checkbox', {name: 'Pages'})).toBeChecked();

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().portletDataHandlers).toEqual(
					toLayoutSetPortletDataHandlers('private-pages')
				)
			);
		});

		it('resets the selection to public when the public radio is selected', async () => {
			renderComponent({
				exportPreview: previewWithPages,
				pageTreeModalConfiguration: {
					...DEFAULT_PROPS.pageTreeModalConfiguration,
					privateLayoutsAvailable: true,
				},
			});

			await expandSection();

			await userEvent.click(screen.getAllByRole('radio')[1]);

			expect(screen.getAllByRole('radio')[1]).toBeChecked();

			await userEvent.click(screen.getAllByRole('radio')[0]);

			expect(screen.getAllByRole('radio')[0]).toBeChecked();
			expect(screen.getAllByRole('radio')[1]).not.toBeChecked();
		});

		it('clears the page selection when the main checkbox is toggled off', async () => {
			renderComponent({
				exportPreview: previewWithPages,
				pageTreeModalConfiguration: {
					...DEFAULT_PROPS.pageTreeModalConfiguration,
					privateLayoutsAvailable: true,
				},
			});

			await expandSection();

			await userEvent.click(screen.getAllByRole('radio')[1]);

			expect(screen.getByRole('checkbox', {name: 'Pages'})).toBeChecked();

			await userEvent.click(
				screen.getByRole('checkbox', {name: 'Pages'})
			);

			expect(
				screen.getByRole('checkbox', {name: 'Pages'})
			).not.toBeChecked();
		});
	});

	describe('look and feel', () => {
		const previewWithLookAndFeel: Preview = {
			additionCount: 0,
			deletionCount: 0,
			portletDataHandlers: toMockPortletDataHandlers([
				{
					key: 'category.site_administration.build',
					label: 'Site Builder',
					portletDataHandlers: [
						{
							label: 'Look and Feel',
							name: 'LOOK_AND_FEEL',
							portletDataHandlerControls: [
								{
									label: 'Theme Settings',
									name: 'THEME_REFERENCE',
									selected: true,
									type: 'Boolean',
								},
								{
									label: 'Logo',
									name: 'LOGO',
									selected: true,
									type: 'Boolean',
								},
								{
									label: 'Site Pages Settings',
									name: 'LAYOUT_SET_SETTINGS',
									selected: true,
									type: 'Boolean',
								},
								{
									label: 'Site Template Settings',
									name: 'LAYOUT_SET_PROTOTYPE_SETTINGS',
									selected: true,
									type: 'Boolean',
								},
							],
						},
					],
				},
			]),
		};

		const toLookAndFeelPortletDataHandler = (
			logo: boolean,
			siteTemplateSettings: boolean
		) => ({
			name: 'LOOK_AND_FEEL',
			portletDataHandlerControls: [
				{
					name: 'THEME_REFERENCE',
					selected: true,
					type: 'Boolean',
				},
				{name: 'LOGO', selected: logo, type: 'Boolean'},
				{
					name: 'LAYOUT_SET_SETTINGS',
					selected: true,
					type: 'Boolean',
				},
				{
					name: 'LAYOUT_SET_PROTOTYPE_SETTINGS',
					selected: siteTemplateSettings,
					type: 'Boolean',
				},
			],
		});

		const submitExport = async () => {
			await userEvent.type(
				screen.getByRole('textbox', {name: /^name/i}),
				'test-file'
			);

			fetch.mockResponseOnce(JSON.stringify({}));

			await userEvent.click(
				screen.getByRole('button', {name: /^export$/i})
			);

			await waitFor(() => expect(getExportCall()).toBeDefined());

			return JSON.parse(String(getExportCall()?.[1]?.body));
		};

		it('sends the look and feel settings as handler controls', async () => {
			renderComponent({exportPreview: previewWithLookAndFeel});

			await expandSection('Site Builder');

			await userEvent.click(screen.getByLabelText('Logo'));
			await userEvent.click(
				screen.getByLabelText('Site Template Settings')
			);

			const body = await submitExport();

			expect(body.portletDataHandlers).toEqual([
				toLookAndFeelPortletDataHandler(false, false),
			]);
			expect(body).not.toHaveProperty('logo');
			expect(body).not.toHaveProperty('themeSettings');
		});

		it('selects every look and feel setting by default', async () => {
			renderComponent({exportPreview: previewWithLookAndFeel});

			const body = await submitExport();

			expect(body.portletDataHandlers).toEqual([
				toLookAndFeelPortletDataHandler(true, true),
			]);
		});
	});

	it('does not submit the permissions flag by default', async () => {
		renderComponent();

		await screen.findByText('loaded');

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});

		await userEvent.type(nameInput, 'test-file');

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = getExportCall();

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.permissions).toBe(false);
		});
	});

	it('submits the permissions flag when the checkbox is checked', async () => {
		renderComponent();

		await screen.findByText('loaded');

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});

		await userEvent.type(nameInput, 'test-file');

		await userEvent.click(screen.getByLabelText(/^export-permissions/));

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = getExportCall();

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.permissions).toBe(true);
		});
	});

	it('submits the checked Comments and Ratings entries as request flags', async () => {
		renderComponent({commentsAndRatingsEnabled: true});

		await screen.findByText('loaded');

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		await expandSection('Content & Data');

		await userEvent.click(screen.getByLabelText('ratings'));

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = getExportCall();

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.comments).toBe(true);
			expect(body.ratings).toBe(false);

			const portletDataHandlerNames = body.portletDataHandlers.map(
				(portletDataHandler: {name: string}) => portletDataHandler.name
			);

			expect(portletDataHandlerNames).not.toContain('commentsAndRatings');
			expect(portletDataHandlerNames).not.toContain('comments');
			expect(portletDataHandlerNames).not.toContain('COMMENTS');
		});
	});

	it('hides a nested control when its parent control is unchecked', async () => {
		renderComponent();

		await screen.findByText('loaded');

		await expandSection('Content & Data');

		await userEvent.click(
			screen.getAllByRole('button', {name: 'show-all-x'})[0]
		);

		const referencedContentCheckbox = screen.getByRole('checkbox', {
			name: 'Referenced Content',
		}) as HTMLInputElement;

		if (!referencedContentCheckbox.checked) {
			await userEvent.click(referencedContentCheckbox);
		}

		expect(
			screen.getByText('Referenced Content Behavior', {
				selector: 'label[for="_journal_referenced-content-behavior"]',
			})
		).toBeInTheDocument();

		await userEvent.click(referencedContentCheckbox);

		expect(referencedContentCheckbox).not.toBeChecked();
		expect(
			screen.queryByText('Referenced Content Behavior', {
				selector: 'label[for="_journal_referenced-content-behavior"]',
			})
		).not.toBeInTheDocument();
	});
});
