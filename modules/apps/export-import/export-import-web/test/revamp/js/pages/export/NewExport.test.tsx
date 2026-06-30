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

import {NewExport} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/export/NewExport';
import {ExportPreview} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import {LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY} from '../../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {mockExportPreview} from '../../mocks/mockExportPreview';
import {mockPageTreeItems} from '../../mocks/mockPageTreeItems';

jest.mock('staging-taglib', () => ({
	PagesTree: require('../../mocks/MockPagesTree').MockPagesTree,
}));

const expandSection = async (label: string) => {
	const sectionLabel = screen.getByText(label, {selector: 'label'});
	const sheet = sectionLabel.closest('.sheet');

	const button = within(sheet as HTMLElement).getByRole('button', {
		name: /expand-x/,
	});

	await userEvent.click(button);
};

const DEFAULT_PROPS = {
	backURL: '/some/back/url',
	exportPreviewAPIURL: '/o/export-import/v1.0/export-preview',
	exportProcessAPIURL: '/o/export-import/v1.0/export-processes',
	pageTreeModalConfiguration: {
		liveGroupId: 20121,
		pageSize: 20,
		privateLayoutsEnabled: false,
	},
};

const renderComponent = (
	props: Partial<React.ComponentProps<typeof NewExport>> = {}
) => render(<NewExport {...DEFAULT_PROPS} {...props} />);

describe('NewExport', () => {
	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(JSON.stringify(mockExportPreview));
	});

	it('preloads the preview without fetching when it is provided', async () => {
		renderComponent({exportPreview: mockExportPreview});

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

	it('exports the last range window resolved at apply, not at submit', async () => {
		const HOUR = 60 * 60 * 1000;
		const applyTime = Date.UTC(2026, 0, 1, 12, 0, 0);

		let now = applyTime;

		const dateNowSpy = jest
			.spyOn(Date, 'now')
			.mockImplementation(() => now);

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

		now = applyTime + 5 * HOUR;

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = fetch.mock.calls.find(
				([, init]) => init?.method === 'POST'
			);

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.startDate).toBe(
				new Date(applyTime - 12 * HOUR).toISOString()
			);
			expect(body.endDate).toBe(new Date(applyTime).toISOString());
		});

		dateNowSpy.mockRestore();
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
				...mockExportPreview,
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
		const previewWithPagePicker: ExportPreview = {
			additionCount: 1,
			deletionCount: 0,
			previewPortletDataHandlerSections: [
				{
					label: 'Site Builder',
					name: 'category.site_administration.build',
					previewPortletDataHandlers: [
						{
							label: 'Pages',
							name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
						},
					],
				},
			],
		};

		const privatePageTreeModalConfiguration = {
			...DEFAULT_PROPS.pageTreeModalConfiguration,
			privateLayoutsEnabled: true,
		};

		const getExportRequest = () => {
			const exportProcessCall = fetch.mock.calls.find(
				([url, init]) =>
					String(url).includes('export-processes') &&
					init?.method === 'POST'
			);

			return JSON.parse(String(exportProcessCall?.[1]?.body));
		};

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
			fetch.mockResponse(async (request) => {
				if (request.url.includes('get_layouts_tree')) {
					return JSON.stringify({
						hasMoreElements: false,
						items: mockPageTreeItems,
					});
				}

				if (request.url.includes('session_tree_js_click')) {
					return (await request.text()).includes('cmd=layoutCheck')
						? JSON.stringify([1, 2])
						: '[]';
				}

				return JSON.stringify({exportImportConfigurationId: 1});
			});
		});

		it('exports all public pages selected by default', async () => {
			renderComponent({exportPreview: previewWithPagePicker});

			await typeFileName();

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().requestPortletDataHandlers).toEqual([
					{
						name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
						requestPortletDataHandlerControls: [
							{name: 'publicLayoutPages'},
						],
					},
				])
			);

			await waitFor(() =>
				expect(Liferay.Util.navigate).toHaveBeenCalledWith(
					'/some/back/url'
				)
			);
		});

		it('hides the visibility radios and exports a public page subset through the modal', async () => {
			renderComponent({exportPreview: previewWithPagePicker});

			await typeFileName();
			await expandSection();

			expect(screen.queryAllByRole('radio')).toHaveLength(0);

			await userEvent.click(
				screen.getByRole('button', {name: 'select-x'})
			);

			expect(await screen.findByLabelText('page-1')).toBeChecked();
			expect(screen.getByLabelText('page-2')).toBeChecked();

			await userEvent.click(screen.getByLabelText('page-2'));
			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().requestPortletDataHandlers).toEqual([
					{
						name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
						requestPortletDataHandlerControls: [
							{name: 'publicLayoutPages', values: ['1']},
						],
					},
				])
			);
		});

		it('shows the visibility radios and exports all private pages', async () => {
			renderComponent({
				exportPreview: previewWithPagePicker,
				pageTreeModalConfiguration: privatePageTreeModalConfiguration,
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
				expect(getExportRequest().requestPortletDataHandlers).toEqual([
					{
						name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
						requestPortletDataHandlerControls: [
							{name: 'privateLayoutPages'},
						],
					},
				])
			);
		});

		it('resets the selection to public when the public radio is selected', async () => {
			renderComponent({
				exportPreview: previewWithPagePicker,
				pageTreeModalConfiguration: privatePageTreeModalConfiguration,
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
				exportPreview: previewWithPagePicker,
				pageTreeModalConfiguration: privatePageTreeModalConfiguration,
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

		it('exports a private page subset selected through the modal', async () => {
			renderComponent({
				exportPreview: previewWithPagePicker,
				pageTreeModalConfiguration: privatePageTreeModalConfiguration,
			});

			await typeFileName();
			await expandSection();

			await userEvent.click(screen.getAllByRole('radio')[1]);
			await userEvent.click(
				screen.getByRole('button', {name: 'select-x'})
			);

			expect(await screen.findByLabelText('page-1')).toBeChecked();
			expect(screen.getByLabelText('page-2')).toBeChecked();

			await userEvent.click(screen.getByLabelText('page-2'));
			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			await submitExport();

			await waitFor(() =>
				expect(getExportRequest().requestPortletDataHandlers).toEqual([
					{
						name: LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
						requestPortletDataHandlerControls: [
							{name: 'privateLayoutPages', values: ['1']},
						],
					},
				])
			);
		});
	});

	it('submits the checked Look and Feel entries as request flags', async () => {
		renderComponent({lookAndFeelEnabled: true});

		await screen.findByText('loaded');

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		await expandSection('Site Builder');

		await userEvent.click(screen.getByLabelText('logo'));
		await userEvent.click(screen.getByLabelText('site-template-settings'));

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = fetch.mock.calls.find(([, init]) => {
				const body = init?.body;

				return (
					typeof body === 'string' &&
					body.includes('"requestPortletDataHandlers"')
				);
			});

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.themeSettings).toBe(true);
			expect(body.sitePagesSettings).toBe(true);
			expect(body.logo).toBe(false);
			expect(body.siteTemplateSettings).toBe(false);

			const handlerNames = body.requestPortletDataHandlers.map(
				(handler: {name: string}) => handler.name
			);

			expect(handlerNames).not.toContain('lookAndFeel');
			expect(handlerNames).not.toContain('logo');
		});
	});

	it('checks every look and feel option by default', async () => {
		renderComponent({lookAndFeelEnabled: true});

		await screen.findByText('loaded');

		const nameInput = await screen.findByRole('textbox', {
			name: /^name/i,
		});
		await userEvent.type(nameInput, 'test-file');

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: /^export$/i}));

		await waitFor(() => {
			const exportCall = fetch.mock.calls.find(([, init]) => {
				const body = init?.body;

				return (
					typeof body === 'string' &&
					body.includes('"requestPortletDataHandlers"')
				);
			});

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.themeSettings).toBe(true);
			expect(body.logo).toBe(true);
			expect(body.sitePagesSettings).toBe(true);
			expect(body.siteTemplateSettings).toBe(true);
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
			const exportCall = fetch.mock.calls.find(([, init]) => {
				const body = init?.body;

				return (
					typeof body === 'string' &&
					body.includes('"requestPortletDataHandlers"')
				);
			});

			expect(exportCall).toBeDefined();

			const body = JSON.parse(exportCall![1]!.body as string);

			expect(body.comments).toBe(true);
			expect(body.ratings).toBe(false);

			const handlerNames = body.requestPortletDataHandlers.map(
				(handler: {name: string}) => handler.name
			);

			expect(handlerNames).not.toContain('commentsAndRatings');
			expect(handlerNames).not.toContain('comments');
			expect(handlerNames).not.toContain('COMMENTS');
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
