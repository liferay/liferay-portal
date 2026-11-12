/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ModelSummary from '../../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/model/ModelSummary';
import MockDiagramBuilderContext from '../../../../../../../../mock/MockDiagramBuilderContext';

const MODEL_PROVIDERS = {
	items: [
		{externalReferenceCode: 'bedrockERC', title: 'Bedrock'},
		{externalReferenceCode: 'otherERC', title: 'Other'},
	],
};

const MODELS = {
	items: [
		{label: 'Claude Sonnet 4.5', name: 'anthropic.claude-sonnet-4-5'},
		{label: 'Nova Pro', name: 'amazon.nova-pro-v1:0'},
	],
};

const renderModelSummary = (data = {}) =>
	render(
		<MockDiagramBuilderContext
			mockSelectedNode={{
				data: {label: {en_US: 'LLM'}, ...data},
				id: 'llmNode',
				type: 'llm',
			}}
		>
			<ModelSummary setContentName={() => {}} />
		</MockDiagramBuilderContext>
	);

describe('ModelSummary', () => {
	it('Keeps the model disabled until a provider is selected', async () => {
		fetch.mockResponseOnce(JSON.stringify(MODEL_PROVIDERS));

		renderModelSummary();

		expect(
			await screen.findByRole('option', {name: 'Bedrock'})
		).toBeInTheDocument();
		expect(screen.getByLabelText('model')).toBeDisabled();
	});

	it('Lists the models of the selected provider', async () => {
		fetch
			.mockResponseOnce(JSON.stringify(MODEL_PROVIDERS))
			.mockResponseOnce(JSON.stringify(MODELS));

		renderModelSummary();

		await screen.findByRole('option', {name: 'Bedrock'});

		await userEvent.selectOptions(
			screen.getByLabelText('provider'),
			'bedrockERC'
		);

		await screen.findByRole('option', {name: 'Claude Sonnet 4.5'});

		expect(fetch).toHaveBeenLastCalledWith(
			expect.stringContaining(
				'/o/ai-hub/v1.0/model-providers/by-external-reference-code/bedrockERC/models'
			),
			expect.any(Object)
		);

		const modelSelect = screen.getByLabelText('model');

		expect(modelSelect).toBeEnabled();

		await userEvent.selectOptions(
			modelSelect,
			'anthropic.claude-sonnet-4-5'
		);

		expect(modelSelect).toHaveValue('anthropic.claude-sonnet-4-5');
	});

	it('Shows the stored provider and model', async () => {
		fetch
			.mockResponseOnce(JSON.stringify(MODEL_PROVIDERS))
			.mockResponseOnce(JSON.stringify(MODELS));

		renderModelSummary({
			modelName: 'amazon.nova-pro-v1:0',
			modelProvider: 'bedrockERC',
		});

		await screen.findByRole('option', {name: 'Nova Pro'});

		expect(screen.getByLabelText('provider')).toHaveValue('bedrockERC');
		expect(screen.getByLabelText('model')).toHaveValue(
			'amazon.nova-pro-v1:0'
		);
	});

	it('Clears the model when the provider changes', async () => {
		fetch
			.mockResponseOnce(JSON.stringify(MODEL_PROVIDERS))
			.mockResponseOnce(JSON.stringify(MODELS))
			.mockResponseOnce(JSON.stringify({items: []}));

		renderModelSummary({
			modelName: 'amazon.nova-pro-v1:0',
			modelProvider: 'bedrockERC',
		});

		await screen.findByRole('option', {name: 'Nova Pro'});

		await userEvent.selectOptions(
			screen.getByLabelText('provider'),
			'otherERC'
		);

		expect(
			screen.queryByRole('option', {name: 'Nova Pro'})
		).not.toBeInTheDocument();
		expect(screen.getByLabelText('model')).toHaveValue('');
	});

	it('Shows an error when the models cannot be listed', async () => {
		fetch
			.mockResponseOnce(JSON.stringify(MODEL_PROVIDERS))
			.mockResponseOnce(JSON.stringify({}), {status: 400});

		renderModelSummary({modelProvider: 'bedrockERC'});

		expect(
			await screen.findByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
		expect(screen.getByLabelText('model')).toBeDisabled();
		expect(screen.getByLabelText('provider')).toBeEnabled();
	});
});
