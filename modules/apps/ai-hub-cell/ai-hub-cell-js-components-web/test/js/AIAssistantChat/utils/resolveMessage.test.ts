/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/events';
import {AgentComponent} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/types';
import resolveMessage from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/resolveMessage';

const SELECT_COMPONENT: AgentComponent = {
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
	title: 'What would you like to do next?',
	type: 'select',
};

describe('resolveMessage', () => {
	it('resolves a categorization message', () => {
		const categorization = {assetTags: ['news']} as never;

		expect(
			resolveMessage({categorization, sender: 'assistant', text: ''})
		).toEqual({categorization, type: 'categorization'});
	});

	it('resolves a content drafts message', () => {
		expect(
			resolveMessage({
				sender: 'assistant',
				text: '[Draft](https://localhost:8080/cms/edit_content_item?id=1)',
			})
		).toEqual({type: 'content-drafts'});
	});

	it('resolves a content types message', () => {
		const contentTypes = [
			{
				externalReferenceCode: 'L_CMS_BLOG',
				label: 'Blog',
				name: 'C_Blog',
			},
		];

		expect(
			resolveMessage({
				contentTypes,
				sender: 'assistant',
				text: 'What type of content do you want to generate?',
			})
		).toEqual({contentTypes, type: 'content-types'});
	});

	it('resolves a field values message from the field value agent', () => {
		expect(
			resolveMessage({
				agentDefinitionExternalReferenceCodes: [
					GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE,
				],
				sender: 'assistant',
				text: JSON.stringify({headline: 'A headline'}),
			})
		).toEqual({
			fieldValues: {headline: 'A headline'},
			type: 'field-values',
		});
	});

	it('resolves a message carrying a select component', () => {
		expect(
			resolveMessage({
				component: SELECT_COMPONENT,
				sender: 'assistant',
				text: '',
			})
		).toEqual({component: SELECT_COMPONENT, type: 'select-component'});
	});

	it('resolves a plain text message as an assistant message', () => {
		expect(
			resolveMessage({sender: 'assistant', text: 'Plain answer.'})
		).toEqual({type: 'assistant'});
	});

	it('resolves a select component before any other message trait', () => {
		expect(
			resolveMessage({
				component: SELECT_COMPONENT,
				images: ['data:image/png;base64,abc'],
				sender: 'user',
				text: '',
			})
		).toEqual({component: SELECT_COMPONENT, type: 'select-component'});
	});

	it('resolves a translate message from fenced JSON text', () => {
		const translate = {
			action: 'translate',
			agentInstanceId: 42,
			availableLanguageIds: ['en_US', 'pt_BR'],
			targetLanguageIds: ['pt_BR'],
		};

		expect(
			resolveMessage({
				sender: 'assistant',
				text: '```json\n' + JSON.stringify(translate) + '\n```',
			})
		).toEqual({translate, type: 'translate'});
	});

	it('resolves a user message', () => {
		expect(resolveMessage({sender: 'user', text: 'Hello'})).toEqual({
			type: 'user',
		});
	});

	it('resolves an error message as an assistant message', () => {
		expect(
			resolveMessage({error: true, sender: 'assistant', text: ''})
		).toEqual({type: 'assistant'});
	});

	it('resolves an images message', () => {
		expect(
			resolveMessage({
				images: ['data:image/png;base64,abc'],
				sender: 'assistant',
				text: '',
			})
		).toEqual({images: ['data:image/png;base64,abc'], type: 'images'});
	});

	it('resolves field values from other agents as a plain assistant message', () => {
		expect(
			resolveMessage({
				agentDefinitionExternalReferenceCodes: ['L_SOMETHING_ELSE'],
				sender: 'assistant',
				text: JSON.stringify({headline: 'A headline'}),
			})
		).toEqual({type: 'assistant'});
	});
});
