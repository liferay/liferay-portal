/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import AIAssistantHost from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistantHost';
import AIAssistantTriggerButton from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/AIAssistantTriggerButton';
import {
	createEventSource,
	postChatByExternalReferenceCodeMessage,
} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api';
import {getSpaces} from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/services/getSpaces';
import {CATEGORIZE_EVENT} from '../../../src/main/resources/META-INF/resources/js/Categorization/events';
import {classifyCategorizationIntent} from '../../../src/main/resources/META-INF/resources/js/Categorization/services/classifyCategorizationIntent';
import {ECategorizationAgent} from '../../../src/main/resources/META-INF/resources/js/Categorization/types';
import {postAIIssueReport} from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/api';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api',
	() => ({
		createEventSource: jest.fn(() => Promise.resolve(null)),
		executeHttpRequestAction: jest.fn(() => Promise.resolve()),
		postChatByExternalReferenceCodeMessage: jest.fn(() =>
			Promise.resolve()
		),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/CategorizationMessageBalloon',
	() => ({
		__esModule: true,
		default: () => 'categorization-balloon',
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/services/getObjectFields',
	() => ({getObjectFields: jest.fn(() => Promise.resolve({items: []}))})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/services/getSpaces',
	() => ({getSpaces: jest.fn(() => Promise.resolve([]))})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/Categorization/services/classifyCategorizationIntent'
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/ReportFeedback/api'
);

const mockClassify = classifyCategorizationIntent as jest.MockedFunction<
	typeof classifyCategorizationIntent
>;
const mockCreateEventSource = createEventSource as jest.MockedFunction<
	typeof createEventSource
>;
const mockGetSpaces = getSpaces as jest.MockedFunction<typeof getSpaces>;
const mockPostChat =
	postChatByExternalReferenceCodeMessage as jest.MockedFunction<
		typeof postChatByExternalReferenceCodeMessage
	>;
const mockPostAIIssueReport = postAIIssueReport as jest.MockedFunction<
	typeof postAIIssueReport
>;

const HOST_CONTAINER_ID = 'ai-assistant-host-root';

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

function renderHost(
	triggerProps?: Partial<
		React.ComponentProps<typeof AIAssistantTriggerButton>
	>
) {
	return render(
		<>
			<AIAssistantHost />

			{triggerProps && (
				<AIAssistantTriggerButton {...defaultProps} {...triggerProps} />
			)}
		</>
	);
}

async function clickTrigger() {
	await act(async () => {
		fireEvent.click(screen.getByRole('button', {name: 'ai-assistant'}));
	});
}

async function renderAndOpen(
	triggerProps: Partial<
		React.ComponentProps<typeof AIAssistantTriggerButton>
	> = {presentation: 'dropdown'}
) {
	await act(async () => {
		renderHost(triggerProps);
	});

	await clickTrigger();
}

function getLiferayHandler(eventName: string) {
	return (Liferay.on as jest.Mock).mock.calls
		.filter(([name]) => name === eventName)
		.at(-1)?.[1];
}

function fireCategorizeEvent(payload: unknown) {
	getLiferayHandler(CATEGORIZE_EVENT)?.(payload);
}

async function openWithContentTypes() {
	await act(async () => {
		renderHost();
	});

	await act(async () => {
		getLiferayHandler('openAIAssistantChat')?.({
			contentTypes: [
				{
					externalReferenceCode: 'L_CMS_BLOG',
					label: 'Blog',
					name: 'C_Blog',
				},
			],
		});
	});
}

function getSidebar() {
	return screen.getByRole('complementary', {name: 'ai-assistant'});
}

async function waitForSidebarOpen() {
	const sidebar = getSidebar();

	await waitFor(() => expect(sidebar).not.toHaveAttribute('inert'));

	return sidebar;
}

describe('AIAssistantHost', () => {
	let hostContainerStub: HTMLDivElement;

	beforeEach(() => {
		(window as unknown as {[key: string]: unknown})[
			'__LIFERAY_AI_ASSISTANT_SINGLETON__'
		] = {
			eventBound: true,
			hostMounted: true,
			listeners: new Set(),
			state: {command: null},
		};

		hostContainerStub = document.createElement('div');
		hostContainerStub.id = HOST_CONTAINER_ID;

		document.body.appendChild(hostContainerStub);

		Object.defineProperty(document.body, 'clientWidth', {
			configurable: true,
			value: 1440,
		});

		mockCreateEventSource.mockReset();
		mockCreateEventSource.mockResolvedValue(null);
		mockGetSpaces.mockReset();
		mockGetSpaces.mockResolvedValue([]);
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

	afterEach(() => {
		hostContainerStub.remove();
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

	it('closes the dropdown when a trigger anchored elsewhere is clicked again', async () => {
		const anchor = document.createElement('button');

		anchor.id = 'toolbar-anchor';

		document.body.appendChild(anchor);

		await renderAndOpen({
			anchorId: 'toolbar-anchor',
			presentation: 'dropdown',
			triggerId: 'tags-trigger',
		});

		await act(async () => {
			fireCategorizeEvent({
				agent: ECategorizationAgent.GENERATE_TAGS,
				content: 'Body',
			});
		});

		// While the dropdown is open, Clay's Overlay hides everything outside
		// the menu with aria-hidden, so the trigger is only reachable by id.

		const trigger = document.getElementById('tags-trigger') as HTMLElement;

		expect(trigger).toHaveAttribute('aria-expanded', 'true');

		await act(async () => {
			fireEvent.pointerDown(trigger);
			fireEvent.mouseDown(trigger);
			fireEvent.pointerUp(trigger);
			fireEvent.mouseUp(trigger);
			fireEvent.click(trigger);
		});

		expect(trigger).toHaveAttribute('aria-expanded', 'false');
		expect(
			screen.queryByRole('button', {name: 'maximize'})
		).not.toBeInTheDocument();

		anchor.remove();
	});

	it('closes the sidebar on Escape', async () => {
		await renderAndOpen({presentation: 'sidebar'});

		const sidebar = await waitForSidebarOpen();

		await act(async () => {
			fireEvent.keyDown(document, {key: 'Escape'});
		});

		await waitFor(() => expect(sidebar).toHaveAttribute('inert'));

		expect(
			screen.getByRole('button', {name: 'ai-assistant'})
		).toHaveAttribute('aria-expanded', 'false');
	});

	it('releases the pushed page content when the sidebar closes', async () => {
		const wrapper = document.createElement('div');

		wrapper.id = 'wrapper';

		document.body.appendChild(wrapper);

		await renderAndOpen({presentation: 'sidebar'});

		const sidebar = await waitForSidebarOpen();

		expect(wrapper).toHaveClass('c-slideout-push-end');
		expect(wrapper).toHaveClass('ai-assistant-sidebar-push');

		await act(async () => {
			fireEvent.keyDown(document, {key: 'Escape'});
		});

		await waitFor(() => expect(sidebar).toHaveAttribute('inert'));

		await waitFor(() =>
			expect(wrapper).not.toHaveClass('c-slideout-push-end')
		);

		expect(wrapper).not.toHaveClass('ai-assistant-sidebar-push');

		wrapper.remove();
	});

	it('injects the image into a select file field found on the page when no field context is provided', async () => {
		const originalDataTransfer = (global as {DataTransfer?: unknown})
			.DataTransfer;

		(global as {DataTransfer?: unknown}).DataTransfer = class {
			items = {
				_files: [] as File[],
				add(file: File) {
					this._files.push(file);
				},
			};

			get files() {
				return this.items._files;
			}
		};

		const field = document.createElement('div');

		field.setAttribute('data-ai-assistant-field-id', '');
		field.innerHTML = '<input class="file-upload-input" type="file" />';

		document.body.appendChild(field);

		const fileInput = field.querySelector(
			'.file-upload-input'
		) as HTMLInputElement;

		let files: File[] = [];

		Object.defineProperty(fileInput, 'files', {
			configurable: true,
			get: () => files,
			set: (value) => {
				files = value;
			},
		});

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

		fireEvent.click(screen.getByRole('button', {name: 'save-image'}));

		expect(fileInput.files).toHaveLength(1);

		field.remove();

		(global as {DataTransfer?: unknown}).DataTransfer =
			originalDataTransfer;
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

	describe('free-form categorization', () => {
		beforeEach(() => {
			mockClassify.mockReset();
			mockPostChat.mockClear();
			(Liferay.fire as jest.Mock).mockClear();
		});

		it('does not classify when the feature is disabled', async () => {
			const fakeEventSource = createFakeEventSource();

			mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

			await renderAndOpen({initialMessage: 'tag this article'});

			await act(async () => {
				fakeEventSource.emit('Subscribe', 'ref-1');
			});

			expect(mockClassify).not.toHaveBeenCalled();
			expect(mockPostChat).toHaveBeenCalled();
		});

		it('fires a single request event for a categorization message', async () => {
			const fakeEventSource = createFakeEventSource();

			mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
			mockClassify.mockResolvedValue({
				actions: [{agent: 'tag', count: 3, targets: []}],
				passthrough: false,
			});

			await renderAndOpen({
				enableFreeFormCategorization: true,
				initialMessage: 'tag this article',
			});

			await act(async () => {
				fakeEventSource.emit('Subscribe', 'ref-1');
			});

			expect(mockClassify).toHaveBeenCalledWith('tag this article');
			expect(Liferay.fire).toHaveBeenCalledWith(
				'cms:aiAssistant:requestCategorize',
				{actions: [{agent: 'tag', count: 3, targets: []}]}
			);
			expect(mockPostChat).not.toHaveBeenCalled();
		});

		it('posts a passthrough message to the chat', async () => {
			const fakeEventSource = createFakeEventSource();

			mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
			mockClassify.mockResolvedValue({actions: [], passthrough: true});

			await renderAndOpen({
				enableFreeFormCategorization: true,
				initialMessage: 'what can you do?',
			});

			await act(async () => {
				fakeEventSource.emit('Subscribe', 'ref-1');
			});

			expect(mockClassify).toHaveBeenCalledWith('what can you do?');
			expect(mockPostChat).toHaveBeenCalled();
			expect(Liferay.fire).not.toHaveBeenCalledWith(
				'cms:aiAssistant:requestCategorize',
				expect.anything()
			);
		});

		it('renders only the balloon when the categorization event suppresses the user message', async () => {
			await act(async () => {
				renderHost();
			});

			await act(async () => {
				fireCategorizeEvent({
					agent: 'L_GENERATE_TAGS',
					cmsGroupId: 1,
					content: 'x',
					scopeId: 1,
					suppressUserMessage: true,
					targets: ['kayaking'],
				});
			});

			expect(
				screen.getByText('categorization-balloon')
			).toBeInTheDocument();
			expect(screen.queryByText('generate-tags')).not.toBeInTheDocument();
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

	it('keeps the live connection across shell switches', async () => {
		await renderAndOpen();

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'maximize'}));
		});

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'minimize'}));
		});

		expect(mockCreateEventSource).toHaveBeenCalledTimes(1);
	});

	it('keeps the message draft when switching shells', async () => {
		await renderAndOpen();

		fireEvent.change(screen.getByPlaceholderText('ask-me-anything'), {
			target: {value: 'Draft in progress'},
		});

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'maximize'}));
		});

		expect(
			within(getSidebar()).getByPlaceholderText('ask-me-anything')
		).toHaveValue('Draft in progress');
	});

	it('merges the static context and the getContext snapshot when sending', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen({
			context: {scope: 'static'},
			getContext: () => ({live: 'value'}),
			presentation: 'dropdown',
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'ref-code');
		});

		const textArea = screen.getByPlaceholderText('ask-me-anything');

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

	it('moves the conversation into the sidebar when maximized', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			fakeEventSource.emit(
				'Chat Message Sent',
				JSON.stringify({data: 'Here is your answer'})
			);
		});

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'maximize'}));
		});

		expect(
			within(getSidebar()).getByText('Here is your answer')
		).toBeInTheDocument();

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'minimize'}));
		});

		expect(
			screen.getByRole('button', {name: 'maximize'})
		).toBeInTheDocument();
		expect(screen.getByText('Here is your answer')).toBeInTheDocument();
	});

	it('offers no expand toggle for a sidebar command', async () => {
		await renderAndOpen({presentation: 'sidebar'});

		await waitForSidebarOpen();

		expect(
			screen.queryByRole('button', {name: 'maximize'})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'minimize'})
		).not.toBeInTheDocument();
	});

	it('offers the expand toggle for a dropdown command', async () => {
		await renderAndOpen();

		expect(
			screen.getByRole('button', {name: 'maximize'})
		).toBeInTheDocument();
	});

	it('renders the panel header actions as small buttons', async () => {
		await renderAndOpen();

		expect(screen.getByRole('button', {name: 'maximize'})).toHaveClass(
			'btn-sm'
		);
		expect(screen.getByRole('button', {name: 'close'})).toHaveClass(
			'btn-sm'
		);
	});

	it('reopens with the last presentation for an open event', async () => {
		await renderAndOpen();

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'close'}));
		});

		await act(async () => {
			getLiferayHandler('openAIAssistantChat')?.({
				message: 'Generate content',
			});
		});

		expect(
			screen.getByRole('button', {name: 'maximize'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'minimize'})
		).not.toBeInTheDocument();
	});

	it('opens the sidebar for an open event with no prior command', async () => {
		await act(async () => {
			renderHost();
		});

		await act(async () => {
			getLiferayHandler('openAIAssistantChat')?.({
				message: 'Translate Content',
			});
		});

		const sidebar = await waitForSidebarOpen();

		expect(
			within(sidebar).getByPlaceholderText('ask-me-anything')
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'maximize'})
		).not.toBeInTheDocument();
	});

	it('opens the sidebar from the trigger in the default presentation', async () => {
		await renderAndOpen({});

		expect(
			screen.getByRole('button', {name: 'ai-assistant'})
		).toHaveAttribute('aria-expanded', 'true');

		const sidebar = await waitForSidebarOpen();

		expect(
			within(sidebar).getByPlaceholderText('ask-me-anything')
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'minimize'})
		).not.toBeInTheDocument();
	});

	it('opens the sidebar when a categorize event fires with no prior command', async () => {
		await act(async () => {
			renderHost();
		});

		await act(async () => {
			fireCategorizeEvent({
				agent: ECategorizationAgent.GENERATE_TAGS,
				content: 'Body',
			});
		});

		const sidebar = await waitForSidebarOpen();

		expect(within(sidebar).getByText('generate-tags')).toBeInTheDocument();
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

	it('reopens as a dropdown after the expanded chat is closed', async () => {
		await renderAndOpen();

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'maximize'}));
		});

		const sidebar = await waitForSidebarOpen();

		await act(async () => {
			fireEvent.click(
				within(sidebar).getByRole('button', {name: 'close'})
			);
		});

		await clickTrigger();

		expect(
			screen.getByRole('button', {name: 'maximize'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'minimize'})
		).not.toBeInTheDocument();
	});

	it('reuses the same body DOM across shell switches', async () => {
		await renderAndOpen();

		const inputBeforeExpand =
			screen.getByPlaceholderText('ask-me-anything');

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'maximize'}));
		});

		expect(
			within(getSidebar()).getByPlaceholderText('ask-me-anything')
		).toBe(inputBeforeExpand);
	});

	describe('scrolling', () => {
		const SCROLL_HEIGHT = 900;

		let scrollTo: jest.Mock;

		beforeEach(() => {
			scrollTo = jest.fn();

			Object.defineProperty(
				window.HTMLElement.prototype,
				'scrollHeight',
				{configurable: true, value: SCROLL_HEIGHT}
			);

			window.HTMLElement.prototype.scrollTo = scrollTo;
		});

		afterEach(() => {
			Reflect.deleteProperty(
				window.HTMLElement.prototype,
				'scrollHeight'
			);
			Reflect.deleteProperty(window.HTMLElement.prototype, 'scrollTo');
		});

		it('scrolls the conversation to the bottom when a message arrives', async () => {
			const fakeEventSource = createFakeEventSource();

			mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

			await renderAndOpen();

			scrollTo.mockClear();

			await act(async () => {
				fakeEventSource.emit(
					'Chat Message Sent',
					JSON.stringify({data: 'Here are your tags'})
				);
			});

			expect(scrollTo).toHaveBeenCalledWith({
				behavior: 'smooth',
				top: SCROLL_HEIGHT,
			});
		});

		it('scrolls the conversation to the bottom when the generating indicator appears without a new message', async () => {
			const fakeEventSource = createFakeEventSource();

			mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

			await renderAndOpen();

			await act(async () => {
				fakeEventSource.emit(
					'Chat Message Sent',
					JSON.stringify({
						component: {
							options: [
								{
									action: {
										'http-request': {
											href: '/o/tags',
											method: 'POST',
										},
									},
									label: 'generate-tags',
								},
							],
							title: 'what-do-you-want-to-do',
							type: 'quick-replies',
						},
						type: 'component',
					})
				);
			});

			scrollTo.mockClear();

			await act(async () => {
				fireEvent.click(
					screen.getByRole('button', {name: 'generate-tags'})
				);
			});

			expect(scrollTo).toHaveBeenCalledWith({
				behavior: 'smooth',
				top: SCROLL_HEIGHT,
			});
		});
	});

	it('keeps the composer usable when a content type is selected before the connection subscribes', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await renderAndOpen();

		await act(async () => {
			getLiferayHandler('openAIAssistantChat')?.({
				contentTypes: [
					{
						externalReferenceCode: 'L_CMS_BLOG',
						label: 'Blog',
						name: 'C_Blog',
					},
				],
			});
		});

		await act(async () => {
			fireEvent.change(screen.getByLabelText('content-type'), {
				target: {value: 'L_CMS_BLOG'},
			});
		});

		await waitFor(() =>
			expect(
				screen.getByPlaceholderText('ask-me-anything')
			).not.toHaveAttribute('readonly')
		);

		expect(screen.queryByText('generating')).toBeNull();
	});

	it('sends the command initial message when the connection is already subscribed', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);

		await act(async () => {
			renderHost({initialMessage: 'tag this article'});
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'ref-1');
		});

		expect(mockPostChat).not.toHaveBeenCalled();

		await clickTrigger();

		expect(mockPostChat).toHaveBeenCalledWith(
			expect.objectContaining({message: 'tag this article'})
		);
	});

	it('shows the chat input immediately on open', async () => {
		await renderAndOpen();

		expect(
			screen.getByPlaceholderText('ask-me-anything')
		).toBeInTheDocument();
	});

	it('shows the footer disclaimer', async () => {
		await renderAndOpen();

		expect(
			screen.getByText('ai-generated-responses-may-be-inaccurate')
		).toBeInTheDocument();
	});

	it('asks for the space before the content type when there are several spaces', async () => {
		mockGetSpaces.mockResolvedValue([
			{
				externalReferenceCode: 'MARKETING',
				id: 1,
				name: 'Marketing',
				siteId: 1,
			},
			{
				externalReferenceCode: 'SALES',
				id: 2,
				name: 'Sales',
				siteId: 2,
			},
		]);

		await openWithContentTypes();

		expect(
			screen.getByText(
				'in-which-space-do-you-want-to-generate-the-content'
			)
		).toBeInTheDocument();
		expect(screen.queryByLabelText('content-type')).toBeNull();

		await act(async () => {
			fireEvent.change(screen.getByLabelText('space'), {
				target: {value: '2'},
			});
		});

		expect(
			screen.getByText('Sales', {selector: 'span'})
		).toBeInTheDocument();
		expect(
			screen.getByText('what-type-of-content-do-you-want-to-generate')
		).toBeInTheDocument();
		expect(screen.getByLabelText('content-type')).toBeInTheDocument();
	});

	it('skips the space question when there is a single space', async () => {
		mockGetSpaces.mockResolvedValue([
			{
				externalReferenceCode: 'MARKETING',
				id: 1,
				name: 'Marketing',
				siteId: 7,
			},
		]);

		await openWithContentTypes();

		expect(screen.queryByLabelText('space')).toBeNull();
		expect(screen.getByLabelText('content-type')).toBeInTheDocument();
	});

	it('skips the space question when there are no spaces', async () => {
		mockGetSpaces.mockResolvedValue([]);

		await openWithContentTypes();

		expect(screen.queryByLabelText('space')).toBeNull();
		expect(screen.getByLabelText('content-type')).toBeInTheDocument();
	});

	it('warns and falls back to the content type when the spaces fail to load', async () => {
		mockGetSpaces.mockRejectedValue(new Error('Request failed'));

		await openWithContentTypes();

		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({
				message: 'the-spaces-could-not-be-loaded',
				type: 'danger',
			})
		);
		expect(screen.queryByLabelText('space')).toBeNull();
		expect(screen.getByLabelText('content-type')).toBeInTheDocument();
	});
});
