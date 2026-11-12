/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import FormView from '../../../../src/main/resources/META-INF/resources/js/custom/form/FormView.es';

const PAGES = [{rows: [], title: ''}];

const INVALID_PAGES = [
	{
		rows: [
			{
				columns: [
					{
						fields: [
							{
								fieldName: 'text',
								name: 'text',
								type: 'text',
								valid: false,
								visible: true,
							},
						],
					},
				],
			},
		],
	},
];

const getEvaluatedContainerIds = () =>
	fetch.mock.calls
		.filter(([url]) => String(url).includes('form-context-provider'))
		.map(
			([, {body}]) =>
				JSON.parse(body.get('serializedFormContext')).containerId
		);

const renderForms = (props = {portletNamespace: '_portletNamespace_'}) => {
	fetch.mockResponseOnce(JSON.stringify([]));
	fetch.mockResponseOnce(JSON.stringify([]));

	return render(
		<form>
			<FormView
				containerId="_portletNamespace_dataEngineLayoutRenderer1"
				pages={PAGES}
				submittable
				{...props}
			/>

			<FormView
				containerId="_portletNamespace_dataEngineLayoutRenderer2"
				pages={PAGES}
				submittable
				{...props}
			/>

			<button type="submit">publish</button>
		</form>
	);
};

describe('FormView', () => {
	const originalThemeDisplay = themeDisplay;

	beforeAll(() => {
		global.AUI = () => ({use: (module, callback) => callback()});

		themeDisplay = {
			...originalThemeDisplay,
			getSiteGroupId: () => '20126',
		};
	});

	afterAll(() => {
		delete global.AUI;

		themeDisplay = originalThemeDisplay;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(HTMLFormElement.prototype, 'submit').mockImplementation(
			() => {}
		);
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('does not submit when another form in the same page is invalid', async () => {
		renderForms();

		fetch.mockResponseOnce(JSON.stringify(PAGES));
		fetch.mockResponseOnce(JSON.stringify(INVALID_PAGES));

		await userEvent.click(screen.getByText('publish'));

		await waitFor(() =>
			expect(Liferay.fire).toHaveBeenCalledWith(
				'ddmFormError',
				expect.anything()
			)
		);

		expect(getEvaluatedContainerIds()).toEqual([
			'_portletNamespace_dataEngineLayoutRenderer1',
			'_portletNamespace_dataEngineLayoutRenderer2',
		]);
		expect(HTMLFormElement.prototype.submit).not.toHaveBeenCalled();
	});

	it('submits once when every form in the same page is valid', async () => {
		renderForms();

		fetch.mockResponseOnce(JSON.stringify(PAGES));
		fetch.mockResponseOnce(JSON.stringify(PAGES));

		await userEvent.click(screen.getByText('publish'));

		await waitFor(() =>
			expect(HTMLFormElement.prototype.submit).toHaveBeenCalled()
		);

		expect(getEvaluatedContainerIds()).toEqual([
			'_portletNamespace_dataEngineLayoutRenderer1',
			'_portletNamespace_dataEngineLayoutRenderer2',
		]);
		expect(HTMLFormElement.prototype.submit).toHaveBeenCalledTimes(1);
	});

	it('validates the CSRF token once before submitting the forms of the Forms portlet', async () => {
		renderForms({
			portletNamespace:
				'_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_',
			validateCSRFTokenURL: '/validate-csrf-token',
		});

		fetch.mockResponseOnce(JSON.stringify({validCSRFToken: true}));
		fetch.mockResponseOnce(JSON.stringify(PAGES));
		fetch.mockResponseOnce(JSON.stringify(PAGES));

		await userEvent.click(screen.getByText('publish'));

		await waitFor(() =>
			expect(HTMLFormElement.prototype.submit).toHaveBeenCalled()
		);

		expect(
			fetch.mock.calls.filter(([url]) => url === '/validate-csrf-token')
		).toHaveLength(1);
		expect(HTMLFormElement.prototype.submit).toHaveBeenCalledTimes(1);
	});
});
