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

it('Return null when the variables have the expected shape', () => {
	const elements = [
		{
			data: {
				inputVariables: [{name: 'request', type: 'string'}],
				label: {en_US: 'LLM'},
				outputVariables: [],
				rag: {},
				tools: [],
			},
			id: 'llmNode',
		},
	];

	expect(getInvalidVariables(elements, 'en_US')).toBeNull();
});

it('Return the input variables message and the node label', () => {
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
		label: 'HTTP Request',
		message: 'input-variables-must-be-a-valid-json-array-in-the-x-node',
	});
});

it('Return the output variables message and the node label', () => {
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
		label: 'LLM',
		message: 'output-variables-must-be-a-valid-json-array-in-the-x-node',
	});
});

it('Return the tools message when the tools are not an array', () => {
	const elements = [
		{data: {label: {en_US: 'LLM'}, tools: 'not json'}, id: 'llmNode'},
	];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		label: 'LLM',
		message: 'tools-must-be-a-valid-json-array-in-the-x-node',
	});
});

it('Return the RAG message when the RAG configuration is not an object', () => {
	const elements = [{data: {label: {en_US: 'LLM'}, rag: []}, id: 'llmNode'}];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		label: 'LLM',
		message:
			'retrieval-augmented-generation-must-be-a-valid-json-object-in-the-x-node',
	});
});

it('Return the node id when the node has no label for the language', () => {
	const elements = [
		{data: {inputVariables: 'request', label: {}}, id: 'httpNode'},
	];

	expect(getInvalidVariables(elements, 'en_US')).toStrictEqual({
		label: 'httpNode',
		message: 'input-variables-must-be-a-valid-json-array-in-the-x-node',
	});
});

it('Ignore transitions', () => {
	const elements = [
		{data: {label: {en_US: 'T1'}}, id: 't1', source: 'a', target: 'b'},
	];

	expect(getInvalidVariables(elements, 'en_US')).toBeNull();
});
