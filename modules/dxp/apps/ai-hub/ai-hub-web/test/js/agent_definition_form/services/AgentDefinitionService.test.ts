/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getAgentDefinitions,
	putAgentDefinition,
} from '../../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService';
import {AgentDefinition} from '../../../../src/main/resources/META-INF/resources/js/agent_definition_form/types/AgentDefinition';

const mockFetch = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: any[]) => mockFetch(...args),
}));

const BASE_URI = '/o/ai-hub/v1.0/agent-definitions';

describe('AgentDefinitionService', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	describe('getAgentDefinitions', () => {
		it('appends sort and page size params as a query string', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			});

			await getAgentDefinitions({
				pageSize: '4',
				sort: 'dateModified:desc',
			});

			expect(mockFetch).toHaveBeenCalledWith(
				`${BASE_URI}?pageSize=4&sort=dateModified%3Adesc`,
				expect.objectContaining({method: 'GET'})
			);
		});

		it('does not append a trailing question mark for empty params', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			});

			await getAgentDefinitions({});

			expect(mockFetch).toHaveBeenCalledWith(
				BASE_URI,
				expect.objectContaining({method: 'GET'})
			);
		});

		it('targets the base endpoint when no params are given', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			});

			await getAgentDefinitions();

			expect(mockFetch).toHaveBeenCalledWith(
				BASE_URI,
				expect.objectContaining({method: 'GET'})
			);
		});
	});

	describe('putAgentDefinition', () => {
		it('uses the ERC argument as the URL path, not the one in the body', async () => {
			mockFetch.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
			});

			const agentDefinition = {
				active: true,
				description: 'Description',
				externalReferenceCode: 'NEW-AGENT-DEFINITION-ERC',
				inputVariables: 'input',
				outputVariable: 'output',
				r_accountToAIHubAgentDefinitions_accountEntryERC: 'ACCOUNT-ERC',
				title_i18n: {en_US: 'Title'},
				workflowDefinitionName: 'workflow',
			} as AgentDefinition;

			await putAgentDefinition(agentDefinition, 'AGENT-DEFINITION-ERC');

			expect(mockFetch).toHaveBeenCalledWith(
				`/o/ai-hub/agent-definitions/by-external-reference-code/AGENT-DEFINITION-ERC`,
				expect.objectContaining({
					body: JSON.stringify(agentDefinition),
					method: 'PUT',
				})
			);
		});
	});
});
