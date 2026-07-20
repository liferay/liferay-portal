/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';

import {getChatbotConfiguration} from '../api';

const configuration = {title: 'My Chatbot'};

const mockFetch = vi.fn();

function getRequestHeaders(): Headers {
	const [, init] = mockFetch.mock.calls[0];

	return init.headers as Headers;
}

describe('getChatbotConfiguration', () => {
	beforeEach(() => {
		mockFetch.mockResolvedValue({
			json: async () => configuration,
			ok: true,
		});

		vi.stubGlobal('fetch', mockFetch);
	});

	afterEach(() => {
		delete (window as any).Liferay;

		mockFetch.mockReset();

		vi.unstubAllGlobals();
	});

	it('omits the Accept-Language header on standalone embeds', async () => {
		await getChatbotConfiguration('chatbot-1');

		expect(getRequestHeaders().get('Accept-Language')).toBeNull();
		expect(getRequestHeaders().get('Accept')).toBe('application/json');
	});

	it('omits the Accept-Language header when the page locale is unavailable', async () => {
		(window as any).Liferay = {ThemeDisplay: {}};

		await getChatbotConfiguration('chatbot-1');

		expect(getRequestHeaders().get('Accept-Language')).toBeNull();
	});

	it('sends the page locale in the Accept-Language header when running inside Liferay', async () => {
		(window as any).Liferay = {
			ThemeDisplay: {getBCP47LanguageId: () => 'pt-BR'},
		};

		await expect(getChatbotConfiguration('chatbot-1')).resolves.toEqual(
			configuration
		);

		expect(getRequestHeaders().get('Accept-Language')).toBe('pt-BR');
	});
});
