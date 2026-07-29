/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CONTENT_GAP_ANALYSIS_ERC} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/constants';
import buildAssistantMessage from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/buildAssistantMessage';

const GAP_ANSWER = JSON.stringify({
	gaps: [
		{
			funnelStageName: 'Awareness',
			personaName: 'Decision Maker',
			reason: 'No content yet.',
			severity: 'high',
		},
	],
	summary: {overview: 'One gap to address.'},
});

describe('buildAssistantMessage', () => {
	it('builds a component message carrying the agent component', () => {
		const component = {
			options: [
				{
					action: {
						'http-request': {
							body: {transitionName: 'yes'},
							href: '/o/ai-hub/v1.0/agent-instances/123/resume',
							method: 'PUT',
						},
					},
					label: 'Yes',
				},
			],
			title: 'Would you like me to add all suggested assets?',
			type: 'select' as const,
		};

		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: ['L_GAP_ANALISYS'],
				component,
				type: 'component',
			})
		).toEqual({
			agentDefinitionExternalReferenceCodes: ['L_GAP_ANALISYS'],
			component,
			sender: 'assistant',
			text: '',
		});
	});

	it('builds an image message with a base64 data URI', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: 'iVBORw0KGgo=',
				mimeType: 'image/jpeg',
				type: 'image',
			})
		).toEqual({
			agentDefinitionExternalReferenceCodes: [CONTENT_GAP_ANALYSIS_ERC],
			images: ['data:image/jpeg;base64,iVBORw0KGgo='],
			sender: 'assistant',
			text: '',
		});
	});

	it('defaults a missing data field to an empty string', () => {
		expect(buildAssistantMessage({}).text).toBe('');
	});

	it('defaults missing agent reference codes to an empty array', () => {
		expect(
			buildAssistantMessage({data: 'hi'})
				.agentDefinitionExternalReferenceCodes
		).toEqual([]);
	});

	it('defaults the image mime type to image/png when missing', () => {
		expect(
			buildAssistantMessage({data: 'iVBORw0KGgo=', type: 'image'}).images
		).toEqual(['data:image/png;base64,iVBORw0KGgo=']);
	});

	it('formats a Content Gap Analysis text answer into bulleted markdown', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: GAP_ANSWER,
				type: 'text',
			})
		).toEqual({
			agentDefinitionExternalReferenceCodes: [CONTENT_GAP_ANALYSIS_ERC],
			sender: 'assistant',
			text:
				'One gap to address.\n\n' +
				'- **Decision Maker / Awareness** (high) — No content yet.',
		});
	});

	it('formats when the answer omits the type (defaults to text)', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: [
					CONTENT_GAP_ANALYSIS_ERC,
				],
				data: GAP_ANSWER,
			}).text
		).toContain('- **Decision Maker / Awareness** (high)');
	});

	it('passes through text answers from agents without a formatter', () => {
		expect(
			buildAssistantMessage({
				agentDefinitionExternalReferenceCodes: ['L_SOMETHING_ELSE'],
				data: 'Plain answer.',
			}).text
		).toBe('Plain answer.');
	});
});
