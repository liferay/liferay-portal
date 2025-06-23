/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';
import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom/extend-expect';
import {
	act,
	fireEvent,
	getByLabelText,
	getByText,
	queryByText,
	render,
	screen,
} from '@testing-library/react';
import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {LAYOUT_TYPES} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutTypes';
import {config} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index';
import {useCollectionConfig} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/CollectionItemContext';
import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CollectionService from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService';
import getSelectedField from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getSelectedField';
import useCache from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useCache';
import {pageContentsAtom} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import MappingSelector from '../../../../../../src/main/resources/META-INF/resources/page_editor/common/components/MappingSelector';

const DEFAULT_MAPPING_FIELDS = {
	'InfoItemClassNameId-infoItemClassTypeId': [
		{
			fields: [
				{key: 'unmapped', label: 'unmapped'},
				{
					key: 'text-field-1',
					label: 'Text Field 1',
					type: 'text',
				},
			],
		},
	],
	'mappingType-mappingSubtype': [
		{
			fields: [
				{
					key: 'structure-field-1',
					label: 'Structure Field 1',
					type: 'text',
				},
			],
		},
	],
};

const INFO_ITEM = {
	className: 'InfoItemClassName',
	classNameId: 'InfoItemClassNameId',
	classPK: 'infoItemClassPK',
	classTypeId: 'infoItemClassTypeId',
	title: 'Info Item',
};

const EMPTY_COLLECTION_CONFIG = {
	collection: {
		classNameId: 'collectionClassNameId',
		classPK: 'collectionClassPK',
		itemSubtype: 'collectionItemSubtype',
		itemType: 'collectionItemType',
	},
};

const onMappingSelect = jest.fn();

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			layoutType: '0',
			selectedMappingTypes: {
				subtype: {
					id: 'mappingSubtype',
					label: 'mappingSubtype',
				},
				type: {
					id: 'mappingType',
					label: 'mappingType',
				},
			},
		},
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/CollectionItemContext',
	() => ({
		useCollectionConfig: jest.fn(),
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/CollectionService',
	() => ({
		getCollectionMappingFields: jest.fn(),
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/InfoItemService',
	() => ({
		getAvailableStructureMappingFields: jest.fn(() =>
			Promise.resolve([
				{
					fields: [
						{
							key: 'structure-field-1',
							label: 'Structure Field 1',
							type: 'text',
						},
					],
				},
			])
		),
		getInfoItemRelationships: jest.fn(),
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve())
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getSelectedField',
	() => jest.fn(() => null)
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useCache',
	() => jest.fn()
);

function renderMappingSelector({
	mappedItem = {},
	mappingFields = DEFAULT_MAPPING_FIELDS,
	onMappingSelect,
}) {
	const state = {
		fragmentEntryLinks: {
			0: {
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
						'editable-id-0': {
							config: {},
						},
					},
				},
			},
		},
		mappingFields,
		segmentsExperienceId: 0,
	};

	return render(
		<StoreAPIContextProvider dispatch={() => {}} getState={() => state}>
			<MappingSelector
				fieldType="text"
				mappedItem={mappedItem}
				onMappingSelect={onMappingSelect}
			/>
		</StoreAPIContextProvider>,
		{
			baseElement: document.body,
		}
	);
}

describe('MappingSelector', () => {
	beforeAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [
				{
					classNameId: 'mappedItemClassNameId',
					classPK: 'mappedItemClassPK',
					classTypeId: 'mappedItemClassTypeId',
					itemSubtype: 'Mapped Item Subtype',
					itemType: 'Mapped Item Type',
					title: 'mappedItemTitle',
				},
			],
			status: 'saved',
		});
	});

	afterAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'idle',
		});
	});

	describe('Content Pages', () => {
		it('renders correct selects in content pages', async () => {
			renderMappingSelector({});

			await act(async () => {
				expect(getByText(document.body, 'item')).toBeInTheDocument();
				expect(getByText(document.body, 'field')).toBeInTheDocument();
				expect(
					queryByText(document.body, 'source')
				).not.toBeInTheDocument();
			});
		});

		it('calls onMappingSelect with correct params when mapping to content', async () => {
			renderMappingSelector({
				mappedItem: INFO_ITEM,
				onMappingSelect,
			});

			const fieldSelect = getByLabelText(document.body, 'field');

			await act(async () => {
				fireEvent.change(fieldSelect, {
					target: {value: 'text-field-1'},
				});
			});

			expect(onMappingSelect).toBeCalledWith({
				className: 'InfoItemClassName',
				classNameId: 'InfoItemClassNameId',
				classPK: 'infoItemClassPK',
				classTypeId: 'infoItemClassTypeId',
				fieldId: 'text-field-1',
				title: 'Info Item',
			});
		});

		it('calls onMappingSelect with empty object when unmapping', async () => {
			renderMappingSelector({
				mappedItem: INFO_ITEM,
				onMappingSelect,
			});

			const fieldSelect = getByLabelText(document.body, 'field');

			await act(async () => {
				fireEvent.change(fieldSelect, {
					target: {value: 'unmapped'},
				});
			});

			expect(onMappingSelect).toBeCalledWith({});
		});

		it('renders correct selects when using Collection context', async () => {
			const collectionFields = [
				{key: 'field-1', label: 'Field 1', type: 'text'},
				{key: 'field-2', label: 'Field 2', type: 'text'},
			];

			useCollectionConfig.mockImplementation(
				() => EMPTY_COLLECTION_CONFIG
			);

			CollectionService.getCollectionMappingFields.mockImplementation(
				() =>
					Promise.resolve({
						mappingFields: [
							{
								fields: collectionFields,
							},
						],
					})
			);

			await act(async () => {
				renderMappingSelector({
					mappingFields: {
						'collectionClassNameId-collectionClassPK': [
							{
								fields: collectionFields,
							},
						],
					},
				});
			});

			useCollectionConfig.mockReset();

			CollectionService.getCollectionMappingFields.mockReset();

			expect(
				queryByText(document.body, 'source')
			).not.toBeInTheDocument();
			expect(queryByText(document.body, 'item')).not.toBeInTheDocument();

			expect(getByText(document.body, 'field')).toBeInTheDocument();

			collectionFields.forEach((field) =>
				expect(
					getByText(document.body, field.label)
				).toBeInTheDocument()
			);
		});

		it('shows a warning and disables the selector if the fields array is empty', async () => {
			renderMappingSelector({
				mappedItem: INFO_ITEM,
				mappingFields: {
					'InfoItemClassNameId-infoItemClassTypeId': [],
				},
			});

			const fieldSelect = getByLabelText(document.body, 'field');

			await act(async () => {
				expect(fieldSelect).toBeInTheDocument();
				expect(
					getByText(
						document.body,
						'no-fields-are-available-for-text-editable'
					)
				).toBeInTheDocument();
			});
		});

		it('shows type and subtype label when some item is mapped', async () => {
			renderMappingSelector({
				mappedItem: {
					classNameId: 'mappedItemClassNameId',
					classPK: 'mappedItemClassPK',
					fieldId: 'mappedFieldId',
				},
			});

			await act(async () => {
				expect(
					getByText(document.body, 'Mapped Item Type')
				).toBeInTheDocument();

				expect(
					getByText(document.body, 'Mapped Item Subtype')
				).toBeInTheDocument();
			});
		});

		it('shows field type when an item is mapped and a field is selected', async () => {
			getSelectedField.mockImplementation(() => ({
				typeLabel: 'text',
			}));

			renderMappingSelector({});

			await act(async () => {
				expect(
					screen.getByText('field-type:').parentElement
				).toHaveTextContent('text');
			});
		});
	});

	describe('Display Pages', () => {
		beforeAll(() => {
			config.layoutType = LAYOUT_TYPES.display;
		});

		afterAll(() => {
			config.layoutType = LAYOUT_TYPES.content;
		});

		const mockRelationships = () => {
			const MOCK_CACHE = {
				'relationships-mappingType-mappingSubtype': [
					{classNameId: 'relationship-1', label: 'Relationship 1'},
					{classNameId: 'relationship-2', label: 'Relationship 2'},
				],
			};

			useCache.mockImplementation(({key}) => MOCK_CACHE[key.join('-')]);
		};

		it('renders correct selects in display pages', async () => {
			renderMappingSelector({});

			await act(async () => {
				expect(getByText(document.body, 'field')).toBeInTheDocument();
				expect(getByText(document.body, 'source')).toBeInTheDocument();
			});
		});

		it('does not render content select when selecting structure as source', async () => {
			const {getByLabelText, getByText, queryByText} =
				renderMappingSelector({});

			const sourceTypeSelect = getByLabelText('source');

			await act(async () => {
				fireEvent.change(sourceTypeSelect, {
					target: {value: 'structure'},
				});
			});

			expect(getByText('field')).toBeInTheDocument();
			expect(getByText('source')).toBeInTheDocument();
			expect(queryByText('item')).not.toBeInTheDocument();
		});

		it('calls onMappingSelect with correct params when mapping to structure', async () => {
			renderMappingSelector({
				onMappingSelect,
			});

			const sourceTypeSelect = getByLabelText(document.body, 'source');

			await act(async () => {
				fireEvent.change(sourceTypeSelect, {
					target: {value: 'structure'},
				});

				const fieldSelect = getByLabelText(document.body, 'field');

				fireEvent.change(fieldSelect, {
					target: {value: 'structure-field-1'},
				});
			});

			expect(onMappingSelect).toBeCalledWith({
				mappedField: 'structure-field-1',
			});
		});

		it('allows selecting relationship in display pages', async () => {
			mockRelationships();

			renderMappingSelector({});

			const sourceSelect = screen.getByLabelText('source');

			await userEvent.selectOptions(sourceSelect, 'relationship');
			fireEvent.change(sourceSelect);

			expect(
				screen.getByRole('option', {name: 'relationship'}).selected
			).toBe(true);
		});

		it('shows a new select for relationships when selecting that source', async () => {
			mockRelationships();

			renderMappingSelector({});

			const sourceSelect = screen.getByLabelText('source');

			await userEvent.selectOptions(sourceSelect, 'relationship');
			fireEvent.change(sourceSelect);

			expect(screen.getByLabelText('relationship')).toBeInTheDocument();
		});
	});
});
