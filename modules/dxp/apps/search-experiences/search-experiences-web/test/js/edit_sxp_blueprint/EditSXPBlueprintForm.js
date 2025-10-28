/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {configure} from '@testing-library/dom';
import {fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import EditSXPBlueprintForm from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/edit_sxp_blueprint/EditSXPBlueprintForm';
import fetchPreviewSearch from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/fetch/fetch_preview_search';
import getUIConfigurationValues from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/sxp_element/get_ui_configuration_values';
const Toasts = require('../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/toasts');
import {ENTITY_JSON, INITIAL_CONFIGURATION} from '../mocks/data';
import {QUERY_SXP_ELEMENTS} from '../mocks/sxpElements';

import '@testing-library/jest-dom';

import {TEST_IDS} from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/testIds';

jest.mock(
	'../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/CodeMirrorEditor',
	() =>
		({onChange, value}) => (
			<textarea
				aria-label="text-area"
				onChange={onChange}
				value={value}
			/>
		)
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/fetch/fetch_preview_search'
);

// Prevents "TypeError: Liferay.component is not a function" error on openToast

Toasts.openSuccessToast = jest.fn();

// Suppress act warning until @testing-library/react is updated past 9
// to use screen. See https://javascript.plainenglish.io/
// you-probably-dont-need-act-in-your-react-tests-2a0bcd2ad65c

const originalError = console.error;

beforeAll(() => {
	console.error = (...args) => {
		if (/Warning.*not wrapped in act/.test(args[0])) {
			return;
		}
		originalError.call(console, ...args);
	};
});

afterAll(() => {
	console.error = originalError;
});

Liferay.ThemeDisplay.getDefaultLanguageId = () => 'en_US';
Liferay.ThemeDisplay.getPathContext = () => '';

configure({testIdAttribute: 'data-qa-id'});

function renderEditSXPBlueprintForm(props) {
	return render(
		<EditSXPBlueprintForm
			entityJSON={ENTITY_JSON}
			initialConfiguration={INITIAL_CONFIGURATION}
			initialDescription=""
			initialDescriptionI18n={{}}
			initialSXPElementInstances={[]}
			initialTitle="Test Title"
			initialTitleI18n={{
				'en-US': 'Test Title',
			}}
			sxpBlueprintId="0"
			{...props}
		/>
	);
}

describe('EditSXPBlueprintForm', () => {
	global.URL.createObjectURL = jest.fn();

	it('renders the edit blueprint form without errors', async () => {
		const {findByText} = renderEditSXPBlueprintForm();

		const titleElement = await findByText('Test Title');

		expect(titleElement).not.toBeNull();
	});

	it('renders the query elements', async () => {
		const {container, findByText} = renderEditSXPBlueprintForm({
			initialSXPElementInstances: QUERY_SXP_ELEMENTS.map(
				(sxpElement) => ({
					sxpElement,
					type: 10,
					uiConfigurationValues: getUIConfigurationValues(sxpElement),
				})
			),
		});

		await findByText('query-settings');

		const {getByText} = within(
			container.querySelector('.layout-section-main')
		);

		QUERY_SXP_ELEMENTS.map((sxpElement) =>
			getByText(sxpElement.title_i18n['en_US'])
		);
	});

	it('adds additional query element from sidebar', async () => {
		const {container, findByText, queryAllByLabelText} =
			renderEditSXPBlueprintForm();

		await findByText('query-settings');

		const sxpElementCountBefore =
			container.querySelectorAll('.sxp-element').length;

		container
			.querySelectorAll('.lexicon-icon-angle-right')
			.forEach((header) => {
				fireEvent.click(header);
			});

		fireEvent.mouseOver(queryAllByLabelText('add')[0]);

		fireEvent.click(queryAllByLabelText('add')[0]);

		const sxpElementCountAfter =
			container.querySelectorAll('.sxp-element').length;

		expect(sxpElementCountAfter).toBe(sxpElementCountBefore + 1);
	});

	it('enables removal of additional query elements', async () => {
		const {container, findByText, getAllByLabelText, getAllByText} =
			renderEditSXPBlueprintForm({
				initialSXPElementInstances: QUERY_SXP_ELEMENTS.map(
					(sxpElement) => ({
						sxpElement,
						type: 10,
						uiConfigurationValues:
							getUIConfigurationValues(sxpElement),
					})
				),
			});

		await findByText('query-settings');

		const sxpElementCountBefore =
			container.querySelectorAll('.sxp-element').length;

		fireEvent.click(getAllByLabelText('dropdown')[0]);

		fireEvent.click(getAllByText('remove')[0]);

		const sxpElementCountAfter =
			container.querySelectorAll('.sxp-element').length;

		expect(sxpElementCountAfter).toBe(sxpElementCountBefore - 1);
	});

	describe('fetchPreviewSearch responses', () => {
		async function setupAndGetErrorItems() {
			const {findAllByTestId, findByTestId, getByTestId} =
				renderEditSXPBlueprintForm();

			await findByTestId(TEST_IDS.PREVIEW_SIDEBAR_BUTTON);

			//

			fireEvent.click(getByTestId(TEST_IDS.PREVIEW_SIDEBAR_BUTTON));

			const previewSidebar = await findByTestId(TEST_IDS.PREVIEW_SIDEBAR);

			// Make search request.

			fireEvent.keyDown(
				within(previewSidebar).getByPlaceholderText('search'),
				{
					charCode: 13,
					code: 'Enter',
					key: 'Enter',
				}
			);

			await findAllByTestId(TEST_IDS.ERROR_LIST_ITEM);

			const errorItems = within(previewSidebar).queryAllByTestId(
				TEST_IDS.ERROR_LIST_ITEM
			);

			// Expand all error items.

			errorItems.forEach((errorItem) => {
				if (within(errorItem).queryByLabelText('expand')) {
					fireEvent.click(within(errorItem).getByLabelText('expand'));
				}
			});

			return errorItems;
		}

		it('displays an error for JSON validation where a property is not defined in the schema', async () => {
			fetchPreviewSearch.mockImplementation(async function mockFetch() {
				return {
					json: async () => ({
						status: 'BAD_REQUEST',
						title: 'The property "elementInstances" is not defined in SXPBlueprint. The property "null" is not defined in Object[]. The property "sxpElement" is not defined in ElementInstance. The property "elementDefinitionn" is not defined in SXPElement.',
					}),
					ok: false,
					status: 400,
				};
			});

			const errorItems = await setupAndGetErrorItems();

			expect(errorItems.length).toBe(1);

			// Check that the styling is correct.

			expect(errorItems[0]).toHaveClass('alert-danger');

			// Check that the title and description is correct.

			expect(errorItems[0]).toHaveTextContent(/error/i); // Case insensitive match to 'error'.
			expect(errorItems[0]).toHaveTextContent(
				'The property "elementInstances" is not defined in SXPBlueprint. The property "null" is not defined in Object[]. The property "sxpElement" is not defined in ElementInstance. The property "elementDefinitionn" is not defined in SXPElement.'
			);
		});

		it('displays an error for ElasticSearch validation with Log Exceptions Only set to true', async () => {
			fetchPreviewSearch.mockImplementation(async function mockFetch() {
				return {
					json: async () => ({
						page: 0,
						pageSize: 10,
						request: {},
						requestString: '',
						responseString:
							'java.lang.RuntimeException: org.elasticsearch.ElasticsearchStatusException: ElasticsearchStatusException[Elasticsearch exception [type=parsing_exception, reason=unknown query [termm] did you mean any of [term, terms]?]]; nested: ElasticsearchException[Elasticsearch exception [type=named_object_not_found_exception, reason=[2:11] unknown field [termm]]];\nSuppressed: method [POST], host [http://127.0.0.1:9201], URI [/liferay-38901/_search?typed_keys=true&max_concurrent_shard_requests=5&ignore_unavailable=false&expand_wildcards=open&allow_no_indices=true&ignore_throttled=true&search_type=query_then_fetch&batched_reduce_size=512], status line [HTTP/1.1 400 Bad Request]\n{"error":{"root_cause":[{"type":"parsing_exception","reason":"unknown query [termm] did you mean any of [term, terms]?","line":2,"col":11}],"type":"parsing_exception","reason":"unknown query [termm] did you mean any of [term, terms]?","line":2,"col":11,"caused_by":{"type":"named_object_not_found_exception","reason":"[2:11] unknown field [termm]"}},"status":400}',
						searchHits: {
							hits: [],
							maxScore: 0,
							totalHits: 0,
						},
					}),
					ok: true,
					status: 200,
				};
			});

			const errorItems = await setupAndGetErrorItems();

			expect(errorItems.length).toBe(1);

			// Check that the styling is correct.

			expect(errorItems[0]).toHaveClass('alert-danger');

			// Check that the title and description is correct.

			expect(errorItems[0]).toHaveTextContent(/error/i); // Case insensitive match to 'error'.
			expect(errorItems[0]).toHaveTextContent(
				'unknown query [termm] did you mean any of [term, terms]?'
			);

			// Check for properties that should be present.

			expect(errorItems[0]).toHaveTextContent('exceptionClass');
			expect(errorItems[0]).toHaveTextContent('exceptionTrace');
		});

		it('displays an error for ElasticSearch validation with Log Exceptions Only set to false', async () => {
			fetchPreviewSearch.mockImplementation(async () => {
				return {
					json: async () => ({
						errors: [
							{
								exceptionClass: 'java.lang.RuntimeException',
								exceptionTrace:
									'java.lang.RuntimeException: ...',
								localizedMessage: 'Error',
								msg: 'com.liferay.portal.kernel.search.SearchException: java.lang.RuntimeException: org.elasticsearch.ElasticsearchStatusException: ElasticsearchStatusException[Elasticsearch exception [type=parsing_exception, reason=unknown query [termmm] did you mean any of [term, terms]?]]; nested: ElasticsearchException[Elasticsearch exception [type=named_object_not_found_exception, reason=[2:12] unknown field [termmm]]];',
								severity: 'ERROR',
							},
						],
					}),
					ok: true,
					status: 200,
				};
			});

			const errorItems = await setupAndGetErrorItems();

			expect(errorItems.length).toBe(1);

			// Check that the styling is correct.

			expect(errorItems[0]).toHaveClass('alert-danger');

			// Check that the title and description is correct.

			expect(errorItems[0]).toHaveTextContent(/error/i); // Case insensitive match to 'error'.
			expect(errorItems[0]).toHaveTextContent(
				'com.liferay.portal.kernel.search.SearchException: java.lang.RuntimeException: org.elasticsearch.ElasticsearchStatusException: ElasticsearchStatusException[Elasticsearch exception [type=parsing_exception, reason=unknown query [termmm] did you mean any of [term, terms]?]]; nested: ElasticsearchException[Elasticsearch exception [type=named_object_not_found_exception, reason=[2:12] unknown field [termmm]]];'
			);

			// Check for properties that should be present.

			expect(errorItems[0]).toHaveTextContent('exceptionClass');
			expect(errorItems[0]).toHaveTextContent('exceptionTrace');
		});

		it('displays multiple errors', async () => {
			fetchPreviewSearch.mockImplementation(async () => {
				return {
					json: async () => ({
						errors: [
							{
								exceptionClass:
									'com.liferay.search.experiences.blueprint.exception.InvalidElementInstanceException',
								exceptionTrace:
									'com.liferay.search.experiences.blueprint.exception.InvalidElementInstanceException: ...',
								localizedMessage: 'Element skipped',
								msg: 'Invalid element instance at: 0',
								severity: 'WARN',
								sxpElementId: 'querySXPElement-0',
							},
							{
								exceptionClass:
									'com.liferay.search.experiences.blueprint.exception.InvalidQueryEntryException',
								exceptionTrace:
									'com.liferay.search.experiences.blueprint.exception.InvalidQueryEntryException: ...',
								localizedMessage: 'Error',
								msg: 'Invalid query entry at: 0',
								severity: 'ERROR',
								sxpElementId: 'querySXPElement-0',
							},
							{
								exceptionClass:
									'com.liferay.search.experiences.blueprint.exception.InvalidParameterException',
								exceptionTrace:
									'com.liferay.search.experiences.blueprint.exception.InvalidParameterException: ...',
								localizedMessage: 'Error',
								msg: 'Invalid parameter name: openweathermap.temp',
								severity: 'ERROR',
								sxpElementId: 'querySXPElement-0',
							},
						],
					}),
					ok: true,
					status: 200,
				};
			});

			const errorItems = await setupAndGetErrorItems();

			expect(errorItems.length).toBe(3);

			// Check that the styling is correct.

			expect(errorItems[0]).toHaveClass('alert-warning');
			expect(errorItems[1]).toHaveClass('alert-danger');
			expect(errorItems[2]).toHaveClass('alert-danger');

			// Check that the displayed data is correct.

			expect(errorItems[0]).toHaveTextContent('Element skipped');
			expect(errorItems[0]).toHaveTextContent(
				'Invalid element instance at: 0'
			);
			expect(errorItems[0]).toHaveTextContent('exceptionClass');
			expect(errorItems[0]).toHaveTextContent('exceptionTrace');
			expect(errorItems[0]).not.toHaveTextContent('sxpElementId');

			expect(errorItems[1]).toHaveTextContent(/error/i); // Case insensitive match to 'error'.
			expect(errorItems[1]).toHaveTextContent(
				'Invalid query entry at: 0'
			);
			expect(errorItems[1]).toHaveTextContent('exceptionClass');
			expect(errorItems[1]).toHaveTextContent('exceptionTrace');
			expect(errorItems[1]).not.toHaveTextContent('sxpElementId');

			expect(errorItems[2]).toHaveTextContent(/error/i); // Case insensitive match to 'error'.
			expect(errorItems[2]).toHaveTextContent(
				'Invalid parameter name: openweathermap.temp'
			);
			expect(errorItems[2]).toHaveTextContent('exceptionClass');
			expect(errorItems[2]).toHaveTextContent('exceptionTrace');
			expect(errorItems[2]).not.toHaveTextContent('sxpElementId');
		});
	});
});
