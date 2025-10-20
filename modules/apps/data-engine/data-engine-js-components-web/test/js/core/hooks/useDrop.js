/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook} from '@testing-library/react-hooks';
import {useDrop as useDndDrop} from 'react-dnd';

import {useDrop} from '../../../../src/main/resources/META-INF/resources/js/core/hooks/useDrop.es';

jest.mock('react-dnd');

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/core/hooks/useConfig.es',
	() => ({
		useConfig: jest.fn(() => ({})),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/core/hooks/useForm.es',
	() => ({
		useForm: jest.fn(() => jest.fn()),
		useFormState: jest.fn(() => ({})),
	})
);

describe('useDrop', () => {
	let mockSourceField;

	beforeEach(() => {
		mockSourceField = {
			data: {},
			sourceIndexes: {
				columnIndex: 0,
				pageIndex: 0,
				rowIndex: 0,
			},
			sourceParentField: {},
			type: 'fieldType:move',
		};

		useDndDrop.mockImplementation((options) => [
			{canDrop: options.canDrop(mockSourceField), overTarget: true},
			jest.fn(),
		]);
	});

	describe('isDroppingFieldsetIntoNestedField', () => {
		it('allows dropping a fieldset into a non-child field', () => {
			const sourceField = {
				fieldName: 'Fieldset1',
				label: 'Fieldset1',
				nestedFields: [{fieldName: 'Text1'}, {fieldName: 'Text2'}],
				type: 'fieldset',
			};

			const targetField = {
				fieldName: 'Text3',
				type: 'text',
			};

			const targetParentField = undefined;

			mockSourceField = {...mockSourceField, data: sourceField};

			const {result} = renderHook(() =>
				useDrop({
					field: targetField,
					origin: 'field',
					parentField: targetParentField,
				})
			);

			expect(result.current.canDrop).toBe(true);
		});

		it('does not allow dropping a fieldset into a nested field that belongs to another fieldset', () => {
			const sourceFieldSet = {
				fieldName: 'Fieldset2',
				label: 'Fieldset2',
				nestedFields: [{fieldName: 'Text3'}],
				type: 'fieldset',
			};

			const targetField = {
				fieldName: 'Text1',
				nestedFieldIndex: 0,
				type: 'text',
			};

			const targetParentField = {
				fieldName: 'Fieldset1',
			};

			mockSourceField = {...mockSourceField, data: sourceFieldSet};

			const {result} = renderHook(() =>
				useDrop({
					field: targetField,
					origin: 'field',
					parentField: targetParentField,
				})
			);

			expect(result.current.canDrop).toBe(false);
		});

		it('does not allow dropping a fieldset into a nested field that belongs to itself', () => {
			const sourceFieldSet = {
				fieldName: 'Fieldset1',
				label: 'Fieldset1',
				nestedFields: [{fieldName: 'Text1'}, {fieldName: 'Text2'}],
				type: 'fieldset',
			};

			const targetField = {
				fieldName: 'Text1',
				nestedFieldIndex: 0,
				type: 'text',
			};

			const targetParentField = {
				fieldName: 'Fieldset1',
			};

			mockSourceField = {...mockSourceField, data: sourceFieldSet};

			const {result} = renderHook(() =>
				useDrop({
					field: targetField,
					origin: 'field',
					parentField: targetParentField,
				})
			);

			expect(result.current.canDrop).toBe(false);
		});
	});

	describe('isDroppingFieldsetIntoNestedPlaceholder', () => {
		it('allows dropping a fieldset into a nested placeholder that belongs to another fieldset', () => {
			const sourceFieldSet = {
				fieldName: 'Fieldset1',
				label: 'Fieldset1',
				nestedFields: [
					{fieldName: 'Text1', type: 'text'},
					{fieldName: 'Text2', type: 'text'},
					{
						fieldName: 'Fieldset2',
						nestedFields: [{fieldName: 'Text3', type: 'text'}],
						type: 'fieldset',
					},
				],
				type: 'fieldset',
			};

			const targetParentField = {
				fieldName: 'Fieldset3',
			};

			mockSourceField = {...mockSourceField, data: sourceFieldSet};

			const {result} = renderHook(() =>
				useDrop({
					field: undefined,
					origin: 'empty',
					parentField: targetParentField,
				})
			);

			expect(result.current.canDrop).toBe(true);
		});

		it('does not allow dropping a fieldset into a nested placeholder that belongs to itself', () => {
			const sourceFieldSet = {
				fieldName: 'Fieldset1',
				label: 'Fieldset1',
				nestedFields: [
					{fieldName: 'Text1', type: 'text'},
					{fieldName: 'Text2', type: 'text'},
					{
						fieldName: 'Fieldset2',
						nestedFields: [{fieldName: 'Text3', type: 'text'}],
						type: 'fieldset',
					},
				],
				type: 'fieldset',
			};

			const targetParentField = {
				fieldName: 'Fieldset2',
			};

			mockSourceField = {...mockSourceField, data: sourceFieldSet};

			const {result} = renderHook(() =>
				useDrop({
					field: undefined,
					origin: 'empty',
					parentField: targetParentField,
				})
			);

			expect(result.current.canDrop).toBe(false);
		});
	});
});
