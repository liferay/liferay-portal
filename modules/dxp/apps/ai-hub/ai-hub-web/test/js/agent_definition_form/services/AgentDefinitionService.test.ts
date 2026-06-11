/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getAgentDefinitions} from '../../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService';

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
});
