/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AgentDefinitionForm from '../../../src/main/resources/META-INF/resources/js/agent_definition_form/AgentDefinitionForm';

const mockDisassociateAgentDefinitionFromContentRetriever = jest.fn();
const mockDisassociateAgentDefinitionFromGuardrail = jest.fn();
const mockGetAgentDefinition = jest.fn();
const mockGetContentRetrievers = jest.fn();
const mockGetGuardrails = jest.fn();
const mockOpenToast = jest.fn();
const mockPostAgentDefinition = jest.fn();
const mockPostAgentDefinitionDraft = jest.fn();
const mockPutAgentDefinition = jest.fn();
const mockPutAgentDefinitionToContentRetrievers = jest.fn();
const mockPutAgentDefinitionToGuardrails = jest.fn();
const mockPutAgentDefinitionDraft = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService',
	() => ({
		disassociateAgentDefinitionFromContentRetriever: (...args: any[]) =>
			mockDisassociateAgentDefinitionFromContentRetriever(...args),
		disassociateAgentDefinitionFromGuardrail: (...args: any[]) =>
			mockDisassociateAgentDefinitionFromGuardrail(...args),
		getAgentDefinition: (...args: any[]) => mockGetAgentDefinition(...args),
		postAgentDefinition: (...args: any[]) =>
			mockPostAgentDefinition(...args),
		postAgentDefinitionDraft: (...args: any[]) =>
			mockPostAgentDefinitionDraft(...args),
		putAgentDefinition: (...args: any[]) => mockPutAgentDefinition(...args),
		putAgentDefinitionDraft: (...args: any[]) =>
			mockPutAgentDefinitionDraft(...args),
		putAgentDefinitionToContentRetrievers: (...args: any[]) =>
			mockPutAgentDefinitionToContentRetrievers(...args),
		putAgentDefinitionToGuardrails: (...args: any[]) =>
			mockPutAgentDefinitionToGuardrails(...args),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/ContentRetrieverService',
	() => ({
		getContentRetrievers: (...args: any[]) =>
			mockGetContentRetrievers(...args),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/GuardrailService',
	() => ({
		getGuardrails: (...args: any[]) => mockGetGuardrails(...args),
	})
);

jest.mock('@liferay/object-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

jest.mock('uuid', () => ({v4: () => 'GENERATED_UUID'}));

jest.mock('@clayui/multi-select', () => {
	const React = require('react');

	return {
		__esModule: true,
		default: ({inputName}: any) =>
			React.createElement('div', {
				'data-testid': `multi-select-${inputName}`,
			}),
	};
});

jest.mock('frontend-js-components-web', () => {
	const React = require('react');

	return {
		FieldBase: ({children, errorMessage, id, label, required}: any) =>
			React.createElement(
				'div',
				null,
				label &&
					React.createElement(
						'label',
						{htmlFor: id},
						label,
						required && '*'
					),
				children,
				errorMessage && React.createElement('div', null, errorMessage)
			),
		InputLocalized: ({error, id, label, onChange, translations}: any) =>
			React.createElement(
				React.Fragment,
				null,
				React.createElement('label', {htmlFor: id}, label),
				React.createElement('input', {
					id,
					onChange: (event: any) =>
						onChange({en_US: event.target.value}),
					value: translations?.en_US || '',
				}),
				error && React.createElement('div', null, error)
			),
	};
});

(global as any).Liferay = {
	Icons: {spritemap: 'icons.svg'},
	Language: {
		get: (key: string) => key,
	},
};

const defaultProps = {
	accountEntryExternalReferenceCode: 'ACCOUNT',
	backURL: '/back',
	editAgentDefinitionURL: '/agent',
	externalReferenceCode: '',
	kaleoDesignerNamespace: '_NS_',
	readOnly: false,
	workflowDefinitionURL: 'http://localhost/workflow-url',
};

const draftAgentDefinition = {
	active: false,
	agentDefinitionsToContentRetrievers: [],
	aiHubAgentDefinitionsToAIHubGuardrails: [],
	description: 'A description',
	externalReferenceCode: 'AGENT_X',
	inputVariables: 'a,b',
	outputVariable: 'out',
	r_accountToAIHubAgentDefinitions_accountEntryERC: 'ACCOUNT',
	title_i18n: {en_US: 'My Agent'},
	workflowDefinitionName: 'wf-1',
};

describe('AgentDefinitionForm', () => {
	beforeEach(() => {
		mockDisassociateAgentDefinitionFromContentRetriever.mockReset();
		mockDisassociateAgentDefinitionFromGuardrail.mockReset();
		mockGetAgentDefinition.mockReset();
		mockGetContentRetrievers.mockReset();
		mockGetGuardrails.mockReset();
		mockOpenToast.mockReset();
		mockPostAgentDefinition.mockReset();
		mockPostAgentDefinitionDraft.mockReset();
		mockPutAgentDefinition.mockReset();
		mockPutAgentDefinitionToContentRetrievers.mockReset();
		mockPutAgentDefinitionToGuardrails.mockReset();
		mockPutAgentDefinitionDraft.mockReset();

		mockGetContentRetrievers.mockResolvedValue({items: []});
		mockGetGuardrails.mockResolvedValue({items: []});
	});

	afterEach(() => {
		cleanup();
	});

	describe('draft creation', () => {
		it('creates a draft and redirects to its edit URL on mount', async () => {
			const originalLocation = window.location;
			const replace = jest.fn();

			Object.defineProperty(window, 'location', {
				configurable: true,
				value: {origin: 'http://localhost', replace},
				writable: true,
			});

			mockPostAgentDefinitionDraft.mockResolvedValueOnce({
				externalReferenceCode: 'AGENT_X',
				workflowDefinitionName: 'wf-1',
			});

			render(<AgentDefinitionForm {...defaultProps} />);

			await waitFor(() =>
				expect(mockPostAgentDefinitionDraft).toHaveBeenCalled()
			);

			await waitFor(() =>
				expect(replace).toHaveBeenCalledWith(
					expect.stringContaining('externalReferenceCode=AGENT_X')
				)
			);

			expect(replace).toHaveBeenCalledWith(
				expect.stringContaining('workflowDefinitionName=wf-1')
			);

			Object.defineProperty(window, 'location', {
				configurable: true,
				value: originalLocation,
				writable: true,
			});
		});
	});

	describe('panels', () => {
		it('hydrates panel inputs from the loaded draft', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce(draftAgentDefinition);

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			expect(screen.getByText('details')).toBeInTheDocument();
			expect(screen.getByText('workflow')).toBeInTheDocument();
			expect(screen.getByText('variables')).toBeInTheDocument();
			expect(screen.getByText('data-sources')).toBeInTheDocument();
			expect(screen.getByText('guardrails')).toBeInTheDocument();
		});
	});

	describe('save', () => {
		it('blocks publishing and surfaces required-field errors when fields are empty', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce({
				...draftAgentDefinition,
				description: '',
				inputVariables: '',
				outputVariable: '',
				title_i18n: {},
			});

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() =>
				expect(mockGetAgentDefinition).toHaveBeenCalled()
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'publish'})
			);

			await waitFor(() => {
				expect(screen.getAllByText('required').length).toBeGreaterThan(
					0
				);
			});

			expect(mockPutAgentDefinition).not.toHaveBeenCalled();
		});

		it('saves a draft without requiring fields to be filled', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce({
				...draftAgentDefinition,
				description: '',
				inputVariables: '',
				outputVariable: '',
			});
			mockPutAgentDefinitionDraft.mockResolvedValueOnce({
				externalReferenceCode: 'AGENT_X',
				status: {label: 'draft'},
			});

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() =>
				expect(screen.getByDisplayValue('My Agent')).toBeInTheDocument()
			);

			await userEvent.click(
				screen.getByRole('button', {name: 'save-as-draft'})
			);

			await waitFor(() => {
				expect(mockPutAgentDefinitionDraft).toHaveBeenCalledWith(
					expect.objectContaining({externalReferenceCode: 'AGENT_X'})
				);
			});

			expect(screen.queryByText('required')).not.toBeInTheDocument();
			expect(mockPutAgentDefinition).not.toHaveBeenCalled();
		});

		it('publishes an edited draft and shows the success toast', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce(draftAgentDefinition);
			mockPutAgentDefinition.mockResolvedValueOnce({
				externalReferenceCode: 'AGENT_X',
				status: {label: 'approved'},
			});

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'publish'})
			);

			await waitFor(() => {
				expect(mockPutAgentDefinition).toHaveBeenCalledWith(
					expect.objectContaining({
						externalReferenceCode: 'AGENT_X',
						workflowDefinitionName: 'wf-1',
					}),
					'AGENT_X'
				);
			});

			await waitFor(() => {
				expect(mockOpenToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'success'})
				);
			});
		});
	});

	describe('toolbar', () => {
		it('disables the save-as-draft and publish buttons when readOnly is true', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce(draftAgentDefinition);

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
					readOnly={true}
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			expect(
				screen.getByRole('button', {name: 'save-as-draft'})
			).toBeDisabled();
			expect(
				screen.getByRole('button', {name: 'publish'})
			).toBeDisabled();
		});

		it('exposes a Cancel link that points at backURL', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce(draftAgentDefinition);

			render(
				<AgentDefinitionForm
					{...defaultProps}
					backURL="/back-here"
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			const cancel = screen.getByRole('link', {name: 'cancel'});

			expect(cancel).toHaveAttribute('href', '/back-here');
		});

		it('shows a single Save action when the agent is already published', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce({
				...draftAgentDefinition,
				status: {label: 'approved'},
			});

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			expect(
				screen.getByRole('button', {name: 'save'})
			).toBeInTheDocument();
			expect(
				screen.queryByRole('button', {name: 'save-as-draft'})
			).not.toBeInTheDocument();
			expect(
				screen.queryByRole('button', {name: 'publish'})
			).not.toBeInTheDocument();
		});

		it('keeps the save-as-draft button for a draft agent', async () => {
			mockGetAgentDefinition.mockResolvedValueOnce({
				...draftAgentDefinition,
				status: {label: 'draft'},
			});

			render(
				<AgentDefinitionForm
					{...defaultProps}
					externalReferenceCode="AGENT_X"
				/>
			);

			await waitFor(() => {
				expect(
					screen.getByDisplayValue('My Agent')
				).toBeInTheDocument();
			});

			expect(
				screen.getByRole('button', {name: 'save-as-draft'})
			).toBeInTheDocument();
		});
	});
});
