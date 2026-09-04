/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayModalProvider} from '@clayui/modal';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {
	ConfigProvider,
	FormProvider,
	KeyboardDNDContextProvider,
} from 'data-engine-js-components-web';
import {openToast} from 'frontend-js-components-web';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import FieldSetList from '../../../../../src/main/resources/META-INF/resources/js/components/field-sets/FieldSetList';
import fieldSetReducer from '../../../../../src/main/resources/META-INF/resources/js/reducers/fieldSetReducer';
import * as DataConverter from '../../../../../src/main/resources/META-INF/resources/js/utils/dataConverter.es';
import * as toast from '../../../../../src/main/resources/META-INF/resources/js/utils/toast.es';
import {
	DATA_DEFINITION_FIELDSET,
	DATA_DEFINITION_RESPONSES,
	ENTRY,
	FORM_VIEW,
} from '../../../../utils/constants.es';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

const {getDataLayoutBuilderProps} = FORM_VIEW;

const defaultState = {
	appProps: {
		config: {},
		contentTypeConfig: {},
		dataDefinitionId: 1,
		dataLayoutId: 1,
		fieldTypesModules: '',
		groupId: 1,
		sidebarPanels: {},
	},
	dataDefinition: DATA_DEFINITION_RESPONSES.ONE_ITEM,
	dataLayout: ENTRY.DATA_LAYOUT,
	fieldSets: [],
};

let dataLayoutBuilder;
let spySuccessToast;
let spyErrorToast;
let ddmFormSpy;

export function FieldSetWrapper({children}) {
	return (
		<DndProvider backend={HTML5Backend}>
			<ClayModalProvider>{children}</ClayModalProvider>
		</DndProvider>
	);
}

describe('FieldSets', () => {
	beforeEach(() => {
		dataLayoutBuilder = getDataLayoutBuilderProps();

		jest.spyOn(DataConverter, 'getFieldSetDDMForm').mockReturnValue({
			name: 'Field53354166',
			pages: FORM_VIEW.pages,
		});

		spySuccessToast = jest
			.spyOn(toast, 'successToast')
			.mockImplementation(() => {});
		spyErrorToast = jest
			.spyOn(toast, 'errorToast')
			.mockImplementation(() => {});

		window.Liferay = {
			...window.Liferay,
			Language: {
				...window.Liferay.Language,
				direction: {
					pt_BR: 'ltr',
				},
			},
			Loader: {
				require: () => jest.fn(),
			},
			SideNavigation: {
				instance: () => {},
			},
		};
	});

	afterEach(() => {
		jest.clearAllTimers();
		jest.restoreAllMocks();

		cleanup();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	xit('renders', () => {
		const {asFragment} = render(
			<FieldSetWrapper>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(asFragment()).toMatchSnapshot();
	});

	xit('renders fieldset list with empty state', () => {
		const {queryByText} = render(
			<FieldSetWrapper>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(queryByText('create-new-fieldset')).toBeTruthy();
		expect(queryByText('there-are-no-fieldsets')).toBeTruthy();
		expect(queryByText('there-are-no-fieldsets-description')).toBeTruthy();
	});

	xit('renders fieldset list with 1 fieldset', () => {
		const {label, nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					name: label,
				},
			],
		};

		const {container, queryByText} = render(
			<DndProvider backend={HTML5Backend}>
				<FieldSetWrapper state={state}>
					<FieldSetList />
				</FieldSetWrapper>
			</DndProvider>
		);

		const fields = container.querySelectorAll('.field-type');

		expect(queryByText('create-new-fieldset')).toBeTruthy();
		expect(fields.length).toBe(1);

		expect(fields[0].querySelector('.list-group-title').textContent).toBe(
			'Address'
		);
		expect(
			fields[0].querySelector('.list-group-subtitle').textContent
		).toBe('x-field');
	});

	xit('renders fieldset list with more than 1 fieldset', () => {
		const {nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					dataDefinitionKey: '110',
					defaultLanguageId: 'en_US',
					name: {
						en_US: 'Address',
						pt_BR: 'Endereço',
					},
				},
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: [
						...nestedDataDefinitionFields,
						nestedDataDefinitionFields,
					],
					dataDefinitionKey: '220',
					defaultLanguageId: 'pt_BR',
					name: {
						en_US: 'House',
						pt_BR: 'Casa',
					},
				},
			],
		};

		const {container, queryByText} = render(
			<FieldSetWrapper state={state}>
				<FieldSetList />
			</FieldSetWrapper>
		);

		const fields = container.querySelectorAll('.field-type');

		expect(queryByText('create-new-fieldset')).toBeTruthy();
		expect(fields.length).toBe(2);

		expect(fields[0].querySelector('.list-group-title').textContent).toBe(
			'Address'
		);
		expect(
			fields[0].querySelector('.list-group-subtitle').textContent
		).toBe('x-field');

		expect(fields[1].querySelector('.list-group-title').textContent).toBe(
			'Casa'
		);
		expect(
			fields[1].querySelector('.list-group-subtitle').textContent
		).toBe('x-fields');
	});

	xit('renders modal when click to create a new fieldset by using empty state', async () => {
		const {queryByText} = render(
			<FieldSetWrapper>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(document.querySelector('.fieldset-modal')).toBeFalsy();

		await act(async () => {
			await fireEvent.click(queryByText('create-new-fieldset'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(document.querySelector('.fieldset-modal')).toBeTruthy();
		expect(document.querySelector('.modal-title').textContent).toBe(
			'create-new-fieldset'
		);

		expect(queryByText('cancel')).toBeTruthy();
		expect(queryByText('save')).toBeTruthy();
	});

	xit('renders modal when click to add a new fieldset with fieldsets in the fieldset list', async () => {
		const {label, nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					name: label,
				},
			],
		};

		const {queryByText} = render(
			<FieldSetWrapper state={state}>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(document.querySelector('.fieldset-modal')).toBeFalsy();

		await act(async () => {
			await fireEvent.click(queryByText('create-new-fieldset'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(document.querySelector('.fieldset-modal')).toBeTruthy();
		expect(document.querySelector('.modal-title').textContent).toBe(
			'create-new-fieldset'
		);

		// Make sure the localization button is shown in the Fielset
		// builder when the user is editing a Fieldset

		expect(
			document.querySelector('.dropdown.localizable-dropdown')
		).toBeTruthy();

		expect(queryByText('cancel')).toBeTruthy();
		expect(queryByText('save')).toBeTruthy();
	});

	xit('renders modal when click to create a new fieldset and close it after click to cancel', async () => {
		const {queryByText} = render(
			<FieldSetWrapper>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(document.querySelector('.fieldset-modal')).toBeFalsy();

		await act(async () => {
			await fireEvent.click(queryByText('create-new-fieldset'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(queryByText('cancel')).toBeTruthy();
		expect(queryByText('save')).toBeTruthy();

		await act(async () => {
			await fireEvent.click(queryByText('cancel'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(document.querySelector('.fieldset-modal')).toBeFalsy();
		expect(queryByText('cancel')).toBeFalsy();
		expect(queryByText('save')).toBeFalsy();
	});

	xit('renders modal when click to edit a fieldset in the fieldset list', async () => {
		fetch.mockResponseOnce(JSON.stringify({}));

		const {label, nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					defaultDataLayout: {id: 1},
					name: label,
				},
			],
		};

		const {queryByText} = render(
			<FieldSetWrapper state={state}>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(document.querySelector('.fieldset-modal')).toBeFalsy();

		await act(async () => {
			await fireEvent.click(queryByText('edit'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(ddmFormSpy.mock.calls.length).toBe(1);
		expect(document.querySelector('.fieldset-modal')).toBeTruthy();
		expect(document.querySelector('.modal-title').textContent).toBe(
			'edit-fieldset'
		);

		// Make sure the localization button is shown in the Fielset
		// builder when the user is editing a Fieldset

		expect(
			document.querySelector('.dropdown.localizable-dropdown')
		).toBeTruthy();
		expect(queryByText('cancel')).toBeTruthy();
		expect(queryByText('save')).toBeTruthy();
	});

	xit('renders fieldset list with one fieldset and create it on form builder', () => {
		const {nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const fieldSet = {
			...DATA_DEFINITION_FIELDSET,
			dataDefinitionFields: nestedDataDefinitionFields,
		};
		const state = {
			...defaultState,
			fieldSets: [fieldSet],
		};

		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<FieldSetWrapper state={state}>
					<FieldSetList />
				</FieldSetWrapper>
			</DndProvider>
		);

		fireEvent.doubleClick(container.querySelector('.field-type'));

		const [
			action,
			{
				fieldSet: {name},
				indexes,
			},
		] =
			dataLayoutBuilder.formBuilderWithLayoutProvider.refs.layoutProvider
				.dispatch.mock.calls[0];

		expect(action).toBe('fieldSetAdded');
		expect(name).toStrictEqual('Field53354166');
		expect(indexes).toStrictEqual({
			columnIndex: 0,
			pageIndex: 0,
			rowIndex: 1,
		});
	});

	xit('renders fieldset list with more than one fieldset and filter it', async () => {
		const {nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					dataDefinitionKey: '110',
					defaultLanguageId: 'en_US',
					name: {
						en_US: 'Address',
						pt_BR: 'Endereço',
					},
				},
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: [
						...nestedDataDefinitionFields,
						nestedDataDefinitionFields,
					],
					dataDefinitionKey: '220',
					defaultLanguageId: 'en_US',
					name: {
						en_US: 'House',
						pt_BR: 'Casa',
					},
				},
			],
		};

		const {container, queryByText, rerender} = render(
			<FieldSetWrapper state={state}>
				<FieldSetList />
			</FieldSetWrapper>
		);

		expect(queryByText('Address')).toBeTruthy();
		expect(queryByText('House')).toBeTruthy();
		expect(container.querySelectorAll('.field-type').length).toBe(2);

		rerender(
			<FieldSetWrapper state={state}>
				<FieldSetList keywords="Address" />
			</FieldSetWrapper>
		);

		expect(queryByText('Address')).toBeTruthy();
		expect(queryByText('House')).toBeFalsy();
		expect(container.querySelectorAll('.field-type').length).toBe(1);
	});

	xit('renders fieldset list with one fieldset and delete it', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				actions: {},
				facets: [],
				items: [],
				lastPage: 1,
				page: 1,
				pageSize: 0,
				totalCount: 0,
			})
		);
		fetch.mockResponseOnce(JSON.stringify({}));

		const {label, nestedDataDefinitionFields} = DATA_DEFINITION_FIELDSET;
		const state = {
			...defaultState,
			fieldSets: [
				{
					...DATA_DEFINITION_FIELDSET,
					dataDefinitionFields: nestedDataDefinitionFields,
					name: label,
				},
			],
		};

		const {queryByText} = render(
			<DndProvider backend={HTML5Backend}>
				<FieldSetWrapper state={state}>
					<FieldSetList />
				</FieldSetWrapper>
			</DndProvider>
		);

		expect(document.querySelector('.modal-dialog')).toBeFalsy();

		await act(async () => {
			await fireEvent.click(queryByText('delete'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		const modal = document.querySelector('.modal-dialog.modal-danger');

		expect(modal).toBeTruthy();

		const [, deleteButton] = modal.querySelectorAll('.modal-footer button');

		await act(async () => {
			await fireEvent.click(deleteButton);
		});

		const {
			dispatch: {
				mock: {calls: dispatchCalls},
			},
		} = dataLayoutBuilder.formBuilderWithLayoutProvider.refs.layoutProvider;
		const [action, payload] = dispatchCalls[0];

		expect(action).toEqual('fieldDeleted');

		expect(spyErrorToast.mock.calls.length).toBe(0);
		expect(spySuccessToast.mock.calls.length).toBe(1);
		expect(dispatchCalls.length).toBe(1);
		expect(payload).toStrictEqual({activePage: 0, fieldName: 'Text'});
	});

	xit('renders the modal fieldset and shows the default language of the object being created', async () => {
		const state = {
			...defaultState,
			dataDefinition: {
				...defaultState.dataDefinition,
				defaultLanguageId: 'pt_BR',
			},
		};

		const {queryByText} = render(
			<FieldSetWrapper state={state}>
				<FieldSetList />
			</FieldSetWrapper>
		);

		await act(async () => {
			await fireEvent.click(queryByText('create-new-fieldset'));
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(
			document.querySelector('.localizable-item-default .autofit-section')
				.textContent
		).toBe('pt-BR');
	});
});

const COMPANY_GROUP_ID = '99';

const CONFIG = {
	contentType: 'journal',
	dataDefinitionId: '1',
	groupId: '20',
};

const createFieldSet = (id, name) => ({
	dataDefinitionFields: [{name: `Text${id}`}],
	dataDefinitionKey: String(id),
	defaultLanguageId: 'en_US',
	id,
	name: {en_US: name},
});

const createSettingsField = (fieldName, value) => ({fieldName, value});

const createPageWithFieldSet = (ddmStructureId) => ({
	rows: [
		{
			columns: [
				{
					fields: [
						{
							nestedFields: [],
							settingsContext: {
								pages: [
									{
										rows: [
											{
												columns: [
													{
														fields: [
															createSettingsField(
																'type',
																'fieldset'
															),
															createSettingsField(
																'ddmStructureId',
																ddmStructureId
															),
														],
													},
												],
											},
										],
									},
								],
							},
						},
					],
				},
			],
		},
	],
});

const createState = (fieldSets, pages) => ({
	availableLanguageIds: ['en_US'],
	dataDefinition: {},
	defaultLanguageId: 'en_US',
	fieldSets,
	pages,
});

const FieldSetListWrapper = ({
	config = CONFIG,
	fieldSets = [],
	pages = [],
	searchTerm = '',
}) => (
	<DndProvider backend={HTML5Backend}>
		<ClayModalProvider>
			<ConfigProvider config={config} initialConfig={{}}>
				<FormProvider
					reducers={[fieldSetReducer]}
					value={createState(fieldSets, pages)}
				>
					<KeyboardDNDContextProvider>
						<FieldSetList searchTerm={searchTerm} />
					</KeyboardDNDContextProvider>
				</FormProvider>
			</ConfigProvider>
		</ClayModalProvider>
	</DndProvider>
);

const getFetchedURLs = () =>
	fetch.mock.calls.map(([resource]) =>
		typeof resource === 'string' ? resource : resource.url
	);

describe('FieldSetList server-side search', () => {
	let originalGetCompanyGroupId;

	beforeEach(() => {
		jest.useFakeTimers();

		openToast.mockClear();

		originalGetCompanyGroupId = themeDisplay.getCompanyGroupId;

		themeDisplay.getCompanyGroupId = () => COMPANY_GROUP_ID;
	});

	afterEach(() => {
		themeDisplay.getCompanyGroupId = originalGetCompanyGroupId;

		jest.clearAllTimers();
		jest.useRealTimers();
	});

	it('debounces the search until the user stops typing', async () => {
		fetch.mockResponse(async () => JSON.stringify({items: []}));

		const {rerender} = render(<FieldSetListWrapper />);

		rerender(<FieldSetListWrapper searchTerm="z" />);

		await act(async () => {
			jest.advanceTimersByTime(200);
		});

		rerender(<FieldSetListWrapper searchTerm="ze" />);

		await act(async () => {
			jest.advanceTimersByTime(200);
		});

		rerender(<FieldSetListWrapper searchTerm="zebra" />);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		const urls = getFetchedURLs();

		expect(urls.length).toBe(2);

		urls.forEach((url) => {
			expect(url).toContain('keywords=zebra');
		});
	});

	it('disables search results that are already in use', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [
						createFieldSet(42, 'Zebra in Use'),
						createFieldSet(300, 'Zebra Unused'),
					],
				});
			}

			return JSON.stringify({items: []});
		});

		const {container} = render(
			<FieldSetListWrapper
				pages={[createPageWithFieldSet('42')]}
				searchTerm="zebra"
			/>
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		const fields = container.querySelectorAll('.field-type');

		expect(fields.length).toBe(2);

		expect(fields[0].classList.contains('disabled')).toBe(true);
		expect(fields[1].classList.contains('disabled')).toBe(false);
	});

	it('encodes the search term in the keywords parameter', async () => {
		fetch.mockResponse(async () => JSON.stringify({items: []}));

		render(<FieldSetListWrapper searchTerm="a b&c" />);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		getFetchedURLs().forEach((url) => {
			expect(new URL(url).searchParams.get('keywords')).toBe('a b&c');
		});
	});

	it('falls back to filtering the store when contentType is missing', () => {
		const {container, queryByText} = render(
			<FieldSetListWrapper
				config={{
					dataDefinitionId: CONFIG.dataDefinitionId,
					groupId: CONFIG.groupId,
				}}
				fieldSets={[
					createFieldSet(2, 'Banana'),
					createFieldSet(3, 'Zebra Local'),
				]}
				searchTerm="zebra"
			/>
		);

		expect(container.querySelector('.loading-animation')).toBeFalsy();
		expect(queryByText('Zebra Local')).toBeTruthy();
		expect(queryByText('Banana')).toBeFalsy();

		expect(fetch).not.toHaveBeenCalled();
	});

	it('hides settled results as soon as the search term changes', async () => {
		const resolvers = {first: [], second: []};

		fetch.mockResponse(
			(request) =>
				new Promise((resolve) => {
					const keywords = new URL(request.url).searchParams.get(
						'keywords'
					);

					const items = request.url.includes('/sites/')
						? [createFieldSet(500, `Result ${keywords}`)]
						: [];

					resolvers[keywords].push(() =>
						resolve(JSON.stringify({items}))
					);
				})
		);

		const {queryByText, rerender} = render(
			<FieldSetListWrapper searchTerm="first" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		await act(async () => {
			resolvers.first.forEach((resolver) => resolver());
		});

		expect(queryByText('Result first')).toBeTruthy();

		rerender(<FieldSetListWrapper searchTerm="second" />);

		expect(queryByText('Result first')).toBeFalsy();

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		await act(async () => {
			resolvers.second.forEach((resolver) => resolver());
		});

		expect(queryByText('Result first')).toBeFalsy();
		expect(queryByText('Result second')).toBeTruthy();
	});

	it('keeps matching store field sets visible next to the loading indicator', () => {
		fetch.mockResponse(async () => JSON.stringify({items: []}));

		const {container, queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[
					createFieldSet(2, 'Banana'),
					createFieldSet(3, 'Zebra Local'),
				]}
				searchTerm="zebra"
			/>
		);

		expect(container.querySelector('.loading-animation')).toBeTruthy();
		expect(queryByText('create-new-fieldset')).toBeTruthy();
		expect(queryByText('Zebra Local')).toBeTruthy();
		expect(queryByText('Banana')).toBeFalsy();
	});

	it('merges store field sets the search index does not return', async () => {
		fetch.mockResponse(async () => JSON.stringify({items: []}));

		const {queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[
					createFieldSet(2, 'Banana'),
					createFieldSet(3, 'Zebra Local'),
				]}
				searchTerm="ebr"
			/>
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('Zebra Local')).toBeTruthy();
		expect(queryByText('Banana')).toBeFalsy();
		expect(queryByText('no-results-found')).toBeFalsy();
	});

	it('prefers the fresher store version of a search result', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [
						createFieldSet(5, 'Zebra'),
						createFieldSet(6, 'Zebra Kept'),
					],
				});
			}

			return JSON.stringify({items: []});
		});

		const {queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[createFieldSet(5, 'Lion')]}
				searchTerm="zebra"
			/>
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('Zebra')).toBeFalsy();
		expect(queryByText('Lion')).toBeTruthy();
		expect(queryByText('Zebra Kept')).toBeTruthy();
	});

	it('renders a search result whose name lacks the display locale without crashing', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [
						{
							...createFieldSet(7, 'Zebra'),
							defaultLanguageId: 'en_US',
							name: {de_DE: 'Zebra Deutsch'},
						},
						createFieldSet(8, 'Zebra Named'),
					],
				});
			}

			return JSON.stringify({items: []});
		});

		const {container, queryByText} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		const fields = container.querySelectorAll('.field-type');

		expect(fields.length).toBe(2);

		expect(fields[0].querySelector('.list-group-title').textContent).toBe(
			''
		);
		expect(queryByText('Zebra Named')).toBeTruthy();
		expect(queryByText('undefined')).toBeFalsy();
	});

	it('renders the field sets from the store without fetching when the search is empty', () => {
		const {queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[
					createFieldSet(2, 'Banana'),
					createFieldSet(3, 'Apple'),
				]}
			/>
		);

		expect(queryByText('Apple')).toBeTruthy();
		expect(queryByText('Banana')).toBeTruthy();

		expect(fetch).not.toHaveBeenCalled();
	});

	it('renders the surviving scope when only one scope request fails', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				throw new Error('boom');
			}

			return JSON.stringify({
				items: [createFieldSet(400, 'Zebra Company')],
			});
		});

		const {queryByText} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('Zebra Company')).toBeTruthy();
		expect(queryByText('unable-to-load-content')).toBeFalsy();

		expect(openToast).not.toHaveBeenCalled();
	});

	it('restores the empty state instead of the error state when a failed search is cleared', async () => {
		fetch.mockReject(new Error('network error'));

		const {queryByText, rerender} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('unable-to-load-content')).toBeTruthy();

		rerender(<FieldSetListWrapper searchTerm="" />);

		expect(queryByText('unable-to-load-content')).toBeFalsy();
		expect(queryByText('there-are-no-fieldsets')).toBeTruthy();
		expect(queryByText('create-new-fieldset')).toBeTruthy();
	});

	it('retries the search when the same term is typed again after a failure', async () => {
		fetch.mockReject(new Error('network error'));

		const {queryByText, rerender} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('unable-to-load-content')).toBeTruthy();

		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [createFieldSet(500, 'Zebra Recovered')],
				});
			}

			return JSON.stringify({items: []});
		});

		rerender(<FieldSetListWrapper searchTerm="" />);
		rerender(<FieldSetListWrapper searchTerm="zebra" />);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('Zebra Recovered')).toBeTruthy();
		expect(queryByText('unable-to-load-content')).toBeFalsy();
	});

	it('searches on the server with the keywords parameter in both scopes', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [
						createFieldSet(1, 'Current Definition'),
						createFieldSet(300, 'Zebra Site'),
					],
				});
			}

			return JSON.stringify({
				items: [createFieldSet(400, 'Zebra Company')],
			});
		});

		const {queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[createFieldSet(2, 'Banana')]}
				searchTerm="zebra"
			/>
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		const urls = getFetchedURLs();

		expect(urls.length).toBe(2);

		expect(urls[0]).toContain(
			'/o/data-engine/v2.0/sites/20/data-definitions/by-content-type/journal'
		);
		expect(urls[1]).toContain(
			'/o/data-engine/v2.0/data-definitions/by-content-type/journal'
		);

		urls.forEach((url) => {
			expect(url).toContain('keywords=zebra');
			expect(url).toContain('page=1');
			expect(url).toContain('pageSize=250');
		});

		expect(queryByText('Banana')).toBeFalsy();
		expect(queryByText('Current Definition')).toBeFalsy();
		expect(queryByText('Zebra Company')).toBeTruthy();
		expect(queryByText('Zebra Site')).toBeTruthy();
	});

	it('shows a refine hint when the results are truncated', async () => {
		fetch.mockResponse(async (request) => {
			if (request.url.includes('/sites/')) {
				return JSON.stringify({
					items: [createFieldSet(300, 'Zebra Site')],
					totalCount: 300,
				});
			}

			return JSON.stringify({items: [], totalCount: 0});
		});

		const {queryByText} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('Zebra Site')).toBeTruthy();
		expect(
			queryByText('refine-the-search-criteria-to-reduce-results')
		).toBeTruthy();
	});

	it('shows an error state and a single toast when every scope fails', async () => {
		fetch.mockReject(new Error('network error'));

		const {queryByText, rerender} = render(
			<FieldSetListWrapper searchTerm="zebra" />
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('unable-to-load-content')).toBeTruthy();
		expect(queryByText('no-results-found')).toBeFalsy();

		rerender(<FieldSetListWrapper searchTerm="lion" />);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('unable-to-load-content')).toBeTruthy();

		expect(openToast).toHaveBeenCalledTimes(1);
		expect(openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
	});

	it('shows the empty search state when the server returns no results', async () => {
		fetch.mockResponse(async () => JSON.stringify({items: []}));

		const {queryByText} = render(
			<FieldSetListWrapper
				fieldSets={[createFieldSet(2, 'Banana')]}
				searchTerm="  zebra  "
			/>
		);

		await act(async () => {
			jest.advanceTimersByTime(500);
		});

		expect(queryByText('no-results-found')).toBeTruthy();

		getFetchedURLs().forEach((url) => {
			expect(new URL(url).searchParams.get('keywords')).toBe('zebra');
		});
	});
});
