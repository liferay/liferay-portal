/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, getByText, render} from '@testing-library/react';
import {Select} from 'dynamic-data-mapping-form-field-type';
import React from 'react';

import {RuleEditor} from '../../../src/main/resources/META-INF/resources/js/pages/RuleEditor.es';
import {FIELDS, FIELDS_TYPES, OPERATORS_BY_TYPE} from '../__mock__/fields.es';

global.fetch.enableFetchMocks();

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	loadModule: () => {},
}));

const DEFAULT_RULE = {
	actions: [
		{
			target: '',
			type: '',
		},
	],
	conditions: [
		{
			operands: [
				{
					type: '',
					value: '',
				},
			],
			operator: '',
		},
	],
	logicalOperator: 'OR',
};

const pages = [
	{
		label: '1 Page title',
		name: '0',
		value: '0',
	},
	{
		label: '2 Page title',
		name: '1',
		value: '1',
	},
	{
		label: '3 Page title',
		name: '2',
		value: '2',
	},
];

const rule = {
	'actions': [
		{
			action: 'show',
			label: 'radio',
			target: 'radio',
		},
	],
	'conditions': [
		{
			operands: [
				{
					label: 'text',
					repeatable: false,
					source: 0,
					type: 'field',
					value: 'text',
				},
				{
					type: 'string',
					value: 'aaaa',
				},
			],
			operator: 'equals-to',
		},
	],
	'logical-operator': 'OR',
};

describe('RuleEditor', () => {
	const originalLiferayLoader = window.Liferay.Loader;

	afterEach(() => {
		fireEvent.click(getByText(document, 'cancel'));

		cleanup();
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		global.fetch.mockResponse(JSON.stringify(FIELDS_TYPES));
	});

	beforeAll(() => {
		window.Liferay = {
			...window.Liferay,
			Loader: {
				require: ([fieldModule], resolve) => {
					switch (fieldModule) {
						case 'select':
							resolve({default: Select});
							break;
						default:
							break;
					}
				},
			},
		};
	});

	afterAll(() => {
		window.Liferay.Loader = originalLiferayLoader;
	});

	describe('Rule Editor', () => {
		it('disables save button when there is no rule completely set', () => {
			const {container} = render(
				<RuleEditor
					fields={FIELDS}
					onCancel={() => {}}
					onSave={() => {}}
					operatorsByType={OPERATORS_BY_TYPE}
					pages={pages}
					rule={DEFAULT_RULE}
				/>
			);

			expect(getByText(container, 'save')).toBeDisabled();
		});

		it('enabled save button there is a rule completely set', () => {
			const {container} = render(
				<RuleEditor
					fields={FIELDS}
					onCancel={() => {}}
					onSave={() => {}}
					operatorsByType={OPERATORS_BY_TYPE}
					pages={pages}
					rule={rule}
				/>
			);

			expect(getByText(container, 'save')).toBeEnabled();
		});
	});
});
