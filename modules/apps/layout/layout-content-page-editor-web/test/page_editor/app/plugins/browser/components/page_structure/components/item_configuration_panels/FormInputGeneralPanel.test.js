/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';

import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {FormInputGeneralPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/FormInputGeneralPanel';

const FORM_ITEM = {
	children: ['input-item-1', 'input-item-2'],
	config: {
		classNameId: 'classNameId',
		classTypeId: 'classTypeId',
	},
	itemId: 'form-item',
	parentId: '',
	type: LAYOUT_DATA_ITEM_TYPES.form,
};

const FRAGMENT_ENTRY_LINK_ID_1 = '1';
const FRAGMENT_ENTRY_LINK_ID_2 = '2';

const INPUT_ITEM_1 = {
	children: [],
	config: {
		fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID_1,
	},
	itemId: 'input-item-1',
	parentId: 'form-item',
	type: LAYOUT_DATA_ITEM_TYPES.fragment,
};

const INPUT_ITEM_2 = {
	children: [],
	config: {
		fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID_2,
	},
	itemId: 'input-item-2',
	parentId: 'form-item',
	type: LAYOUT_DATA_ITEM_TYPES.fragment,
};

let MOCK_CACHE = {
	'allowedInputTypes-dateFragment': ['date'],
	'allowedInputTypes-numericFragment': ['numeric'],
	'allowedInputTypes-textFragment': ['text'],
	'formFields-classNameId-classTypeId': [
		{
			fields: [
				{
					key: 'requiredField',
					label: 'Required Field',
					name: 'requiredField',
					required: true,
					type: 'text',
				},
				{
					key: 'notRequiredField',
					label: 'Not Required Field',
					name: 'notRequiredField',
					required: false,
					type: 'text',
				},
				{
					key: 'numericField',
					label: 'Numeric Field',
					name: 'numericField',
					required: false,
					type: 'numeric',
				},
			],
			label: 'Fieldset',
		},
	],
};

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableLanguages: {
				en_US: {
					default: false,
					displayName: 'English (United States)',
					languageIcon: 'en-us',
					languageId: 'en_US',
					w3cLanguageId: 'en-US',
				},
			},
			commonStyles: [],
			formTypes: [
				{
					label: 'Type',
					subtypes: [],
					value: 'classNameId',
				},
			],
		},
	})
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useCache',
	() => jest.fn(({key}) => MOCK_CACHE[key.join('-')])
);

const mockRelationships = () => {
	MOCK_CACHE = {
		...MOCK_CACHE,
		'relationships-classNameId-classTypeId': [
			{classNameId: 'relationship-1', label: 'Relationship 1'},
			{classNameId: 'relationship-2', label: 'Relationship 2'},
		],
	};
};

const clearRelationships = () => {
	delete MOCK_CACHE['relationships-classNameId-classTypeId'];
};

const renderComponent = ({
	fragmentEntryKey = 'textFragment',
	mappedFieldId,
	item = INPUT_ITEM_1,
} = {}) => {
	const mockDispatch = jest.fn((a) => {
		if (typeof a === 'function') {
			return a(mockDispatch, () => state);
		}
	});

	const state = {
		fragmentEntryLinks: {
			[FRAGMENT_ENTRY_LINK_ID_1]: {
				comments: [],
				configuration: {},
				editableValues: {
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						inputFieldId: mappedFieldId,
					},
				},
				fragmentEntryKey,
				fragmentEntryType: 'input',
				name: 'Fragment',
			},
			[FRAGMENT_ENTRY_LINK_ID_2]: {
				comments: [],
				configuration: {},
				editableValues: {
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {},
				},
				fragmentEntryKey,
				fragmentEntryType: 'input',
				name: 'Fragment',
			},
		},
		fragments: [],
		languageId: 'en_US',
		layoutData: {
			items: {
				[FORM_ITEM.itemId]: FORM_ITEM,
				[INPUT_ITEM_1.itemId]: INPUT_ITEM_1,
				[INPUT_ITEM_2.itemId]: INPUT_ITEM_2,
			},
		},
		permissions: {UPDATE: true},
		restrictedItemIds: new Set(),
		selectedViewportSize: VIEWPORT_SIZES.desktop,
	};

	return render(
		<StoreAPIContextProvider dispatch={mockDispatch} getState={() => state}>
			<FormInputGeneralPanel item={item} />
		</StoreAPIContextProvider>
	);
};

describe('FormInputGeneralPanel', () => {
	it('shows an alert instead of field selector when there are no suitable fields for fragment allowed types', () => {
		renderComponent({fragmentEntryKey: 'dateFragment'});

		expect(
			screen.getByText(
				'there-are-no-suitable-fields-in-the-item-to-be-mapped-to-the-fragment'
			)
		).toBeInTheDocument();

		expect(screen.queryByLabelText('field')).not.toBeInTheDocument();
	});

	it('shows field selector when there are available fields', () => {
		renderComponent();

		expect(screen.getByLabelText('field')).toBeInTheDocument();
	});

	it('shows in the field selector only unmapped options', () => {
		renderComponent({
			item: INPUT_ITEM_2,
			mappedFieldId: 'notRequiredField',
		});

		expect(screen.queryByText('Required Field*')).toBeInTheDocument();
		expect(
			screen.queryByText('Not Required Field')
		).not.toBeInTheDocument();
	});

	it('shows in the field selector only the options whose type is supported by the fragment', () => {
		renderComponent({
			fragmentEntryKey: 'numericFragment',
		});

		expect(screen.queryByText('Numeric Field')).toBeInTheDocument();
		expect(screen.queryByText('Required Field*')).not.toBeInTheDocument();
		expect(
			screen.queryByText('Not Required Field')
		).not.toBeInTheDocument();
	});

	it('does not show configuration fieldset when fragment is not mapped', () => {
		renderComponent();

		expect(
			screen.queryByText('input-fragment-configuration')
		).not.toBeInTheDocument();
	});

	it('shows configuration fieldset when fragment is mapped', () => {
		renderComponent({mappedFieldId: 'requiredField'});

		expect(screen.getByText('fragment-configuration')).toBeInTheDocument();
	});

	it('shows checkbox to set field as required disabled when fragment is mapped to a required field', () => {
		renderComponent({mappedFieldId: 'requiredField'});

		expect(screen.getByLabelText('mark-as-required')).toBeDisabled();
	});

	it('shows checkbox to set field as required enabled when fragment is mapped to a not required field', () => {
		renderComponent({mappedFieldId: 'notRequiredField'});

		expect(screen.getByLabelText('mark-as-required')).not.toBeDisabled();
	});

	it('shows source selector when there is any relationship', () => {
		mockRelationships();

		renderComponent();

		expect(screen.getByLabelText('source')).toBeInTheDocument();

		clearRelationships();
	});
});
