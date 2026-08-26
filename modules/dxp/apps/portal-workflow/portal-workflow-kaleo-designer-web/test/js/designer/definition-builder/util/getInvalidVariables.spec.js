/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import getInvalidVariables from '../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/util/getInvalidVariables';

it('Return null when a node has no variables', () => {
	const elements = [{data: {label: {en_US: 'Start'}}, id: 'startNode'}];

	expect(getInvalidVariables(elements, 'en_US')).toBeNull();
});

it('Return null when the variables are arrays', () => {
	const elements = [
		{
			data: {
				inputVariables: [{name: 'request', type: 'string'}],
				label: {en_US: 'HTTP Request'},
				outputVariables: [],
			},
			id: 'httpNode',
		},
	];

	expect(getInvalidVariables(elements, 'en_US')).toBeNull();
});

it('Return the input variables title and the node label', () => {
	const elements = [
		{
			data: {
				inputVariables: 'request',
				label: {en_US: 'HTTP Request'},
			},
			id: 'httpNode',
		},
	];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		fieldTitle: 'input-variables',
		label: 'HTTP Request',
	});
});

it('Return the output variables title and the node label', () => {
	const elements = [
		{
			data: {
				label: {en_US: 'LLM'},
				outputVariables: {name: 'response'},
			},
			id: 'llmNode',
		},
	];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		fieldTitle: 'output-variables',
		label: 'LLM',
	});
});

it('Return the node id when the node has no label for the language', () => {
	const elements = [
		{data: {inputVariables: 'request', label: {}}, id: 'httpNode'},
	];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		fieldTitle: 'input-variables',
		label: 'httpNode',
	});
});

it('Ignore transitions', () => {
	const elements = [
		{data: {label: {en_US: 'T1'}}, id: 't1', source: 'a', target: 'b'},
	];

	expect(getInvalidVariables(elements, 'en_US')).toBeNull();
});
