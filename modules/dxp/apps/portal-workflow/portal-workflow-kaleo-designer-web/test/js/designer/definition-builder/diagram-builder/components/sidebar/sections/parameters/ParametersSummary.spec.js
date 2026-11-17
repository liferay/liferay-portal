/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ParametersSummary from '../../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/parameters/ParametersSummary';
import MockDiagramBuilderContext from '../../../../../../../../mock/MockDiagramBuilderContext';

const renderParametersSummary = (parameters) =>
	render(
		<MockDiagramBuilderContext
			mockSelectedNode={{
				data: {label: {en_US: 'LLM'}, parameters},
				id: 'llmNode',
				type: 'llm',
			}}
		>
			<ParametersSummary setContentName={() => {}} />
		</MockDiagramBuilderContext>
	);

const fillModal = async (name, value) => {
	const nameInput = await screen.findByLabelText(/^parameter/);

	await userEvent.clear(nameInput);

	if (name) {
		await userEvent.type(nameInput, name);
	}

	const valueInput = screen.getByLabelText(/^value/);

	await userEvent.clear(valueInput);

	if (value) {
		await userEvent.type(valueInput, value);
	}

	await userEvent.click(screen.getByRole('button', {name: 'save'}));
};

const openRowAction = async (rowName, action) => {
	const row = screen.getByRole('row', {name: new RegExp(rowName)});

	await userEvent.click(within(row).getByRole('button', {name: 'actions'}));

	await userEvent.click(await screen.findByRole('menuitem', {name: action}));
};

describe('ParametersSummary', () => {
	it('Shows only the Add Parameter button when the node has no parameters', () => {
		renderParametersSummary([]);

		expect(screen.queryByRole('table')).not.toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'add-parameter'})
		).toBeInTheDocument();
	});

	it('Adds a parameter to the table', async () => {
		renderParametersSummary([]);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-parameter'})
		);

		await fillModal('maxOutputTokens', '512');

		const row = screen.getByRole('row', {name: /maxOutputTokens/});

		expect(within(row).getByText('512')).toBeInTheDocument();
		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
	});

	it('Requires the parameter and the value', async () => {
		renderParametersSummary([]);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-parameter'})
		);

		await fillModal('', '');

		expect(screen.getAllByText('this-field-is-required')).toHaveLength(2);
		expect(screen.getByRole('dialog')).toBeInTheDocument();
	});

	it('Rejects a parameter that is already set', async () => {
		renderParametersSummary([{name: 'topP', value: '0.9'}]);

		await userEvent.click(
			screen.getByRole('button', {name: 'add-parameter'})
		);

		await fillModal('topP', '0.5');

		expect(
			screen.getByText('this-parameter-is-already-set')
		).toBeInTheDocument();
		expect(screen.getAllByRole('row', {hidden: true})).toHaveLength(2);
	});

	it('Edits a parameter and keeps its own name valid', async () => {
		renderParametersSummary([{name: 'topP', value: '0.9'}]);

		await openRowAction('topP', 'edit');

		expect(await screen.findByLabelText(/^parameter/)).toHaveValue('topP');

		await fillModal('topP', '0.5');

		const row = screen.getByRole('row', {name: /topP/});

		expect(within(row).getByText('0.5')).toBeInTheDocument();
		expect(
			screen.queryByText('this-parameter-is-already-set')
		).not.toBeInTheDocument();
	});

	it('Removes a parameter and hides the table when none is left', async () => {
		renderParametersSummary([{name: 'topP', value: '0.9'}]);

		await openRowAction('topP', 'remove');

		expect(screen.queryByRole('table')).not.toBeInTheDocument();
	});
});
