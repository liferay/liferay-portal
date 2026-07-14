/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import AIAssistantChat from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistantChat';
import {
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api';
import {postAIIssueReport} from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/api';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api',
	() => ({
		createEventSource: jest.fn(() => Promise.resolve(null)),
		postChatByExternalReferenceCodeMessage: jest.fn(() =>
			Promise.resolve()
		),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/ReportFeedback/api'
);

const mockCreateEventSource = createEventSource as jest.MockedFunction<
	typeof createEventSource
>;
const mockPostChat =
	postChatByExternalReferenceCodeMessage as jest.MockedFunction<
		typeof postChatByExternalReferenceCodeMessage
	>;
const mockPostAIIssueReport = postAIIssueReport as jest.MockedFunction<
	typeof postAIIssueReport
>;

const defaultProps = {
	getContext: () => ({}),
	instructionDefinitionScope: 'test-scope',
};

function createFakeEventSource() {
	const listeners: Record<string, (event: {data: string}) => void> = {};

	return {
		addEventListener: jest.fn(
			(type: string, handler: (event: {data: string}) => void) => {
				listeners[type] = handler;
			}
		),
		close: jest.fn(),
		emit(type: string, data: string) {
			listeners[type]?.({data});
		},
	};
}

async function renderAndOpen() {
	await act(async () => {
		render(<AIAssistantChat {...defaultProps} />);
	});

	await act(async () => {
		screen
			.getByRole('button', {name: 'ai-assistant'})
			.dispatchEvent(new MouseEvent('click', {bubbles: true}));
	});
}

describe('AIAssistantChat', () => {
	beforeEach(() => {
		window.HTMLElement.prototype.scrollTo = jest.fn();

		mockCreateEventSource.mockReset();
		mockCreateEventSource.mockResolvedValue(null);
		mockPostChat.mockReset();
		mockPostChat.mockResolvedValue(undefined);
		mockPostAIIssueReport.mockReset();
		mockPostAIIssueReport.mockResolvedValue({id: 'report-1'});

		global.Liferay = {
			...global.Liferay,
			Util: {
				...global.Liferay?.Util,
				openToast: jest.fn(),
			},
		};
	});

	it('shows the chat input immediately on open', async () => {
		await renderAndOpen();

		expect(
			screen.getByPlaceholderText('Ask me anything...')
		).toBeInTheDocument();
	});

	it('shows the footer disclaimer', async () => {
		await renderAndOpen();

		expect(
			screen.getByText('ai-generated-responses-may-be-inaccurate')
		).toBeInTheDocument();
	});

	it('exposes the feedback row on a successful message and wires the codes', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({
					agentDefinitionExternalReferenceCodes: ['agent-x'],
					data: 'Here is your answer',
				})
			);
		});

		expect(
			screen.getByRole('button', {
				name: 'send-negative-feedback-or-report-legal-concern',
			})
		).toBeInTheDocument();

		await act(async () => {
			fireEvent.click(
				screen.getByRole('button', {name: 'give-positive-feedback'})
			);
		});

		expect(mockPostAIIssueReport).toHaveBeenCalledWith({
			agentDefinitionExternalReferenceCodes: ['agent-x'],
			feedback: 'positive',
			surface: 'aiAssistant',
		});
	});

	it('hides the feedback row on an error message', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Agent Invocation Failed',
				JSON.stringify({data: 'Something went wrong'})
			);
		});

		expect(screen.getByText('Something went wrong')).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'give-positive-feedback'})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {
				name: 'send-negative-feedback-or-report-legal-concern',
			})
		).not.toBeInTheDocument();
	});

	it('merges the static context and the getContext snapshot when sending', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await act(async () => {
			render(
				<AIAssistantChat
					context={{scope: 'static'}}
					getContext={() => ({live: 'value'})}
					instructionDefinitionScope="test-scope"
				/>
			);
		});

		await act(async () => {
			screen
				.getByRole('button', {name: 'ai-assistant'})
				.dispatchEvent(new MouseEvent('click', {bubbles: true}));
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'ref-code');
		});

		const textArea = screen.getByPlaceholderText('Ask me anything...');

		await act(async () => {
			fireEvent.change(textArea, {target: {value: 'Hello'}});
		});

		await act(async () => {
			fireEvent.submit(textArea.closest('form') as HTMLFormElement);
		});

		expect(mockPostChat).toHaveBeenCalledWith(
			expect.objectContaining({
				chatContext: {live: 'value', scope: 'static'},
			})
		);
	});

	it('renders a generated image from an image event', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({
					agentDefinitionExternalReferenceCodes: ['agent-x'],
					data: 'BASE64',
					mimeType: 'image/png',
					type: 'image',
				})
			);
		});

		expect(screen.getByAltText('generated-image')).toHaveAttribute(
			'src',
			'data:image/png;base64,BASE64'
		);
	});

	it('accumulates several image events into a single balloon', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({
					data: 'AAA',
					mimeType: 'image/png',
					type: 'image',
				})
			);
		});

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({
					data: 'BBB',
					mimeType: 'image/png',
					type: 'image',
				})
			);
		});

		const images = screen.getAllByAltText('generated-image');

		expect(images).toHaveLength(2);
		expect(images[0]).toHaveAttribute('src', 'data:image/png;base64,AAA');
		expect(images[1]).toHaveAttribute('src', 'data:image/png;base64,BBB');

		expect(
			screen.getAllByRole('checkbox', {name: 'generated-image'})
		).toHaveLength(2);
	});

	it('defaults the mime type to image/png when the image event omits it', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({data: 'CCC', type: 'image'})
			);
		});

		expect(screen.getByAltText('generated-image')).toHaveAttribute(
			'src',
			'data:image/png;base64,CCC'
		);
	});
});
