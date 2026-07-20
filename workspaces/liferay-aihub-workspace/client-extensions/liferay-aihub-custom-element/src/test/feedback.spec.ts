/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';

import {postAIIssueReport} from '../api';
import {submitPositiveFeedback} from '../feedback';

vi.mock('../api', () => ({postAIIssueReport: vi.fn()}));

const mockedPost = vi.mocked(postAIIssueReport);

const payload = {
	agentDefinitionExternalReferenceCodes: ['agent-1'],
	chatbotExternalReferenceCode: 'chatbot-1',
	surface: 'clickToChat' as const,
};

describe('submitPositiveFeedback', () => {
	beforeEach(() => {
		mockedPost.mockReset();
	});

	afterEach(() => {
		vi.restoreAllMocks();
	});

	it('posts positive feedback and runs onSuccess once on success', async () => {
		mockedPost.mockResolvedValue({id: 'mock-1'});

		const onSuccess = vi.fn();

		await submitPositiveFeedback(payload, onSuccess);

		expect(mockedPost).toHaveBeenCalledWith({
			...payload,
			feedback: 'positive',
		});
		expect(onSuccess).toHaveBeenCalledTimes(1);
	});

	it('does not run onSuccess when the request fails', async () => {
		mockedPost.mockRejectedValue(new Error('boom'));

		const consoleError = vi
			.spyOn(console, 'error')
			.mockImplementation(() => {});
		const onSuccess = vi.fn();

		await submitPositiveFeedback(payload, onSuccess);

		expect(onSuccess).not.toHaveBeenCalled();
		expect(consoleError).toHaveBeenCalledWith(
			'Failed to submit feedback:',
			expect.any(Error)
		);
	});
});
