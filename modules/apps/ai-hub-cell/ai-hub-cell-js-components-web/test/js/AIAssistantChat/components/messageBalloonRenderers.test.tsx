/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom';

import {renderMessageBalloon} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/messageBalloonRenderers';
import {Message} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/types';
import {AIChat} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/useAIChat';

const MESSAGE: Message = {
	agentDefinitionExternalReferenceCodes: ['CMS_AGENT'],
	sender: 'assistant',
	text:
		'I created these contents for you:\n\n' +
		'- [Travelling around Japan](/cms/edit_content_item/1)',
};

function renderBalloon(type: 'assistant' | 'content-drafts', item: Message) {
	const chat = {
		feedbackGiven: {},
		giveThumbsUp: jest.fn(),
		setReportContext: jest.fn(),
	} as unknown as AIChat;

	render(renderMessageBalloon({chat, index: 3, item}, {type}));

	return chat;
}

describe('renderMessageBalloon', () => {
	it.each(['assistant', 'content-drafts'] as const)(
		'does not render the feedback actions on an error %s reply',
		(type) => {
			renderBalloon(type, {...MESSAGE, error: true});

			expect(
				screen.queryByRole('button', {name: 'give-positive-feedback'})
			).toBeNull();
		}
	);

	it.each(['assistant', 'content-drafts'] as const)(
		'reports a %s reply with its index and agent definitions',
		async (type) => {
			const chat = renderBalloon(type, MESSAGE);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'send-negative-feedback-or-report-legal-concern',
				})
			);

			expect(chat.setReportContext).toHaveBeenCalledWith({
				agentDefinitionExternalReferenceCodes: ['CMS_AGENT'],
				index: 3,
			});
		}
	);
});
