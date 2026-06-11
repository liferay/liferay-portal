/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ChatbotForm from '../../../src/main/resources/META-INF/resources/js/chatbot_form/ChatbotForm';

const mockGetChatbotDefinition = jest.fn();
const mockPostChatbotDefinition = jest.fn();
const mockPutChatbotDefinition = jest.fn();
const mockOpenToast = jest.fn();

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/agent_definition_form/services/AgentDefinitionService',
	() => ({
		getAgentDefinitions: jest.fn().mockResolvedValue({items: []}),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/chatbot_form/services/ChatbotService',
	() => ({
		disassociateChatbotFromAgentDefinition: jest.fn().mockResolvedValue({}),
		getChatbotDefinition: (...args: any[]) =>
			mockGetChatbotDefinition(...args),
		postChatbotDefinition: (...args: any[]) =>
			mockPostChatbotDefinition(...args),
		putChatbotAgentDefinitionRelationship: jest.fn().mockResolvedValue({}),
		putChatbotDefinition: (...args: any[]) =>
			mockPutChatbotDefinition(...args),
	})
);

jest.mock('@liferay/object-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

jest.mock('frontend-js-components-web', () => ({
	InputLocalized: ({
		error,
		label,
		name,
		onChange,
		translations,
	}: {
		error?: string;
		label: string;
		name: string;
		onChange: (value: Record<string, string>) => void;
		translations?: Record<string, string>;
	}) => (
		<div>
			<label htmlFor={name}>{label}</label>

			<input
				data-testid={name}
				id={name}
				onChange={(event) =>
					onChange({
						...(translations ?? {}),
						en_US: event.target.value,
					})
				}
				value={translations?.en_US ?? ''}
			/>

			{error && <span data-testid={`${name}-error`}>{error}</span>}
		</div>
	),
}));

jest.mock('frontend-js-web', () => ({
	sub: (template: string, ...args: string[]) =>
		template.replace(/\{\d+\}/g, () => args.shift() ?? ''),
}));

(global as any).Liferay = {
	Icons: {spritemap: 'icons.svg'},
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getLanguageId: () => 'en_US',
	},
};

const defaultProps = {
	accountEntryExternalReferenceCode: 'ACCOUNT',
	avatarAcceptedFileExtensions: 'jpeg, jpg, png',
	avatarMaximumFileSize: 1024,
	avatarMaximumFileSizeLabel: '1 KB',
	avatarUploadTip: 'Upload a jpeg, jpg, png no larger than 1 KB',
	backURL: '/back',
	externalReferenceCode: '',
	portalURL: 'http://localhost:8080',
	readOnly: false,
};

function getHiddenFileInput(): HTMLInputElement {
	return document.getElementById('avatar') as HTMLInputElement;
}

function makeFile(name: string, sizeInBytes: number, type = 'image/png'): File {
	const blob = new Blob([new Uint8Array(sizeInBytes)], {type});

	return new File([blob], name, {type});
}

describe('ChatbotForm company logo upload', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockPostChatbotDefinition.mockReset();
		mockPutChatbotDefinition.mockReset();
	});

	afterEach(() => {
		cleanup();
	});

	it('rejects files larger than avatarMaximumFileSize when limit is greater than zero', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('big-logo.png', 4096);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			);
		});

		expect(screen.queryByText('big-logo.png')).not.toBeInTheDocument();
	});

	it('accepts files of any size when avatarMaximumFileSize is zero (unlimited)', async () => {
		render(<ChatbotForm {...defaultProps} avatarMaximumFileSize={0} />);

		const file = makeFile('huge-logo.png', 5_000_000);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		await screen.findByText('huge-logo.png');

		expect(mockOpenToast).not.toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
	});

	it('hides the Clear button when no company logo is set', () => {
		render(<ChatbotForm {...defaultProps} />);

		expect(
			screen.queryByRole('button', {name: 'clear'})
		).not.toBeInTheDocument();
	});

	it('renders the Clear button after a valid file is selected', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('logo.png', 512);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		expect(
			await screen.findByRole('button', {name: 'clear'})
		).toBeInTheDocument();
	});

	it('disables the select button while reading the file', async () => {
		render(<ChatbotForm {...defaultProps} />);

		const file = makeFile('logo.png', 512);

		fireEvent.change(getHiddenFileInput(), {target: {files: [file]}});

		const selectButton = screen.getByLabelText('select-x');

		expect(selectButton).toBeDisabled();

		await waitFor(() => expect(selectButton).not.toBeDisabled());
	});
});

describe('ChatbotForm disclaimer message', () => {
	beforeEach(() => {
		mockOpenToast.mockClear();
		mockGetChatbotDefinition.mockReset();
		mockPostChatbotDefinition.mockReset();
		mockPutChatbotDefinition.mockReset();
	});

	afterEach(() => {
		cleanup();
	});

	it('submits with an empty disclaimer when the admin never fills it', async () => {
		mockPostChatbotDefinition.mockResolvedValue({
			externalReferenceCode: 'CHATBOT-ERC',
		});

		render(<ChatbotForm {...defaultProps} />);

		await screen.findByTestId('disclaimerMessage_i18n');

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPostChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPostChatbotDefinition.mock.calls[0][0].disclaimerMessage_i18n
		).toEqual({});
	});

	it('sends the typed disclaimer through to the API payload', async () => {
		mockPostChatbotDefinition.mockResolvedValue({
			externalReferenceCode: 'CHATBOT-ERC',
		});

		render(<ChatbotForm {...defaultProps} />);

		fireEvent.change(await screen.findByTestId('disclaimerMessage_i18n'), {
			target: {value: 'Custom disclaimer'},
		});

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(mockPostChatbotDefinition).toHaveBeenCalled()
		);

		expect(
			mockPostChatbotDefinition.mock.calls[0][0].disclaimerMessage_i18n
		).toEqual({en_US: 'Custom disclaimer'});
	});
});
