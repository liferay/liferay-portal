/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import HomeDashboard from '../../../src/main/resources/META-INF/resources/js/home_dashboard/HomeDashboard';

const mockGetAgentDefinitions = jest.fn();
const mockGetChatbots = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService',
	() => ({
		getAgentDefinitions: (...args: any[]) =>
			mockGetAgentDefinitions(...args),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/chatbot_form/services/ChatbotService',
	() => ({
		getChatbots: (...args: any[]) => mockGetChatbots(...args),
	})
);

(global as any).Liferay = {
	Icons: {spritemap: 'icons.svg'},
	Language: {
		get: (key: string) => key,
	},
};

const defaultProps = {
	agentBuilderURL: 'http://localhost:8080/web/ai-hub/agent-builder',
	agentURL: 'http://localhost:8080/web/ai-hub/agent',
	backURL: 'http://localhost:8080/web/ai-hub',
	chatbotURL: 'http://localhost:8080/web/ai-hub/chatbot',
	chatbotsURL: 'http://localhost:8080/web/ai-hub/chatbots',
};

function buildItems(prefix: string, count: number) {
	return Array.from({length: count}, (_, index) => ({
		active: true,
		externalReferenceCode: `${prefix}_${index}`,
		title: `${prefix} ${index}`,
	}));
}

function getCardHref(title: string): string {
	const link = screen.getByText(title).closest('a');

	return link?.getAttribute('href') ?? '';
}

describe('HomeDashboard', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('passes the workflow definition name in the agent link', async () => {
		mockGetAgentDefinitions.mockResolvedValue({
			items: [
				{
					active: true,
					externalReferenceCode: 'L_IMPROVE_WRITING',
					title: 'Improve Writing',
					workflowDefinitionName: 'Improve Writing',
				},
			],
		});
		mockGetChatbots.mockResolvedValue({items: []});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => screen.getByText('Improve Writing'));

		const href = getCardHref('Improve Writing');

		expect(href).toContain('externalReferenceCode=L_IMPROVE_WRITING');
		expect(href).toContain('workflowDefinitionName=Improve+Writing');
	});

	it('omits the workflow definition name when the agent has none', async () => {
		mockGetAgentDefinitions.mockResolvedValue({
			items: [
				{
					active: true,
					externalReferenceCode: 'L_NO_WORKFLOW',
					title: 'No Workflow Agent',
				},
			],
		});
		mockGetChatbots.mockResolvedValue({items: []});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => screen.getByText('No Workflow Agent'));

		const href = getCardHref('No Workflow Agent');

		expect(href).toContain('externalReferenceCode=L_NO_WORKFLOW');
		expect(href).not.toContain('workflowDefinitionName');
	});

	it('never adds the workflow definition name to chatbot links', async () => {
		mockGetAgentDefinitions.mockResolvedValue({items: []});
		mockGetChatbots.mockResolvedValue({
			items: [
				{
					active: true,
					externalReferenceCode: 'L_SUPPORT_BOT',
					title: 'Support Bot',
				},
			],
		});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => screen.getByText('Support Bot'));

		const href = getCardHref('Support Bot');

		expect(href).toContain('externalReferenceCode=L_SUPPORT_BOT');
		expect(href).not.toContain('workflowDefinitionName');
	});

	it('requests the latest agents and chatbots sorted by modification date', async () => {
		mockGetAgentDefinitions.mockResolvedValue({items: []});
		mockGetChatbots.mockResolvedValue({items: []});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => {
			expect(mockGetAgentDefinitions).toHaveBeenCalledWith({
				pageSize: '4',
				sort: 'dateModified:desc',
			});
		});

		expect(mockGetChatbots).toHaveBeenCalledWith({
			pageSize: '4',
			sort: 'dateModified:desc',
		});
	});

	it('renders up to four chatbots in the order returned by the server', async () => {
		mockGetAgentDefinitions.mockResolvedValue({items: []});
		mockGetChatbots.mockResolvedValue({items: buildItems('Chatbot', 5)});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => {
			expect(screen.getByText('Chatbot 0')).toBeInTheDocument();
		});

		expect(screen.getByText('Chatbot 1')).toBeInTheDocument();
		expect(screen.getByText('Chatbot 2')).toBeInTheDocument();
		expect(screen.getByText('Chatbot 3')).toBeInTheDocument();
		expect(screen.queryByText('Chatbot 4')).not.toBeInTheDocument();
	});

	it('renders up to four agents', async () => {
		mockGetAgentDefinitions.mockResolvedValue({
			items: buildItems('Agent', 5),
		});
		mockGetChatbots.mockResolvedValue({items: []});

		render(<HomeDashboard {...defaultProps} />);

		await waitFor(() => {
			expect(screen.getByText('Agent 0')).toBeInTheDocument();
		});

		expect(screen.getByText('Agent 3')).toBeInTheDocument();
		expect(screen.queryByText('Agent 4')).not.toBeInTheDocument();
	});
});
