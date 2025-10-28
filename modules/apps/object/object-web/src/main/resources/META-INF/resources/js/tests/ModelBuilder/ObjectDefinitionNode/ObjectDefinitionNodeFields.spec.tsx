/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ObjectDefinitionNodeFields} from '../../../components/ModelBuilder/ObjectDefinitionNode/ObjectDefinitionNodeFields';

jest.mock(
	'../../../components/ModelBuilder/ModelBuilderContext/objectFolderContext',
	() => {
		return {
			useObjectFolderContext() {
				return [{}, jest.fn()];
			},
		};
	}
);

jest.mock('react-flow-renderer', () => {
	return {
		useStore() {
			return {
				edges: [],
				nodes: [],
			};
		},
	};
});

const objectFieldsMock: ObjectFieldNodeRow[] = [
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField1',
		label: {
			en_US: 'objectField1',
		},
		name: 'objectField1',
		primaryKey: false,
		required: false,
		selected: false,
	},
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField2',
		label: {
			en_US: 'objectField2',
		},
		name: 'objectField2',
		primaryKey: false,
		required: false,
		selected: false,
	},
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField3',
		label: {
			en_US: 'objectField3',
		},
		name: 'objectField3',
		primaryKey: false,
		required: false,
		selected: false,
	},
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField4',
		label: {
			en_US: 'objectField4',
		},
		name: 'objectField4',
		primaryKey: false,
		required: false,
		selected: false,
	},
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField5',
		label: {
			en_US: 'objectField5',
		},
		name: 'objectField5',
		primaryKey: false,
		required: false,
		selected: false,
	},
	{
		businessType: 'LongInteger',
		externalReferenceCode: 'objectField6',
		label: {
			en_US: 'objectField6',
		},
		name: 'objectField6',
		primaryKey: false,
		required: false,
		selected: false,
	},
];

describe('ObjectDefinitionNodeFields Component', () => {
	it('check if the object field label and business type will be rendered correctly', () => {
		const {queryByText} = render(
			<ObjectDefinitionNodeFields
				defaultLanguageId="en_US"
				objectFields={[
					{
						businessType: 'LongInteger',
						externalReferenceCode: 'idObjectFieldERC',
						label: {
							en_US: 'ID',
						},
						name: 'id',
						primaryKey: true,
						required: false,
						selected: false,
					},
				]}
				selectedObjectDefinitionId={0}
				showAllObjectFields={false}
			/>
		);

		expect(queryByText('ID')).toBeVisible();
		expect(queryByText('long-integer')).toBeVisible();
	});

	it('check if only 5 object fields will be listed if showAllObjectFields property is false', () => {
		const {queryByText} = render(
			<ObjectDefinitionNodeFields
				defaultLanguageId="en_US"
				objectFields={objectFieldsMock}
				selectedObjectDefinitionId={0}
				showAllObjectFields={false}
			/>
		);

		expect(queryByText('objectField1')).toBeVisible();
		expect(queryByText('objectField2')).toBeVisible();
		expect(queryByText('objectField3')).toBeVisible();
		expect(queryByText('objectField4')).toBeVisible();
		expect(queryByText('objectField5')).toBeVisible();
		expect(queryByText('objectField6')).toBeNull();
	});

	it('check if more than 5 object fields will be listed if showAllObjectFields property is true', () => {
		const {queryByText} = render(
			<ObjectDefinitionNodeFields
				defaultLanguageId="en_US"
				objectFields={objectFieldsMock}
				selectedObjectDefinitionId={0}
				showAllObjectFields
			/>
		);

		expect(queryByText('objectField1')).toBeVisible();
		expect(queryByText('objectField2')).toBeVisible();
		expect(queryByText('objectField3')).toBeVisible();
		expect(queryByText('objectField4')).toBeVisible();
		expect(queryByText('objectField5')).toBeVisible();
		expect(queryByText('objectField6')).toBeVisible();
	});
});
