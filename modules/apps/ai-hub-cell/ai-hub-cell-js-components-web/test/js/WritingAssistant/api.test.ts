/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {postAgentInstance} from '../../../src/main/resources/META-INF/resources/js/WritingAssistant/api';
import {EActionType} from '../../../src/main/resources/META-INF/resources/js/WritingAssistant/types';
import {RequestTooLargeError} from '../../../src/main/resources/META-INF/resources/js/utils/throwIfRequestTooLarge';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const authorizationToken = {
	accessToken: 'access-token',
	serviceURL: 'https://ai-hub.liferay.com',
	userToken: 'user-token',
};

function jsonResponse(body: unknown, ok = true, status = ok ? 200 : 500) {
	return {
		json: () => Promise.resolve(body),
		ok,
		status,
		statusText: ok ? 'OK' : 'Internal Server Error',
		text: () => Promise.resolve(JSON.stringify(body)),
	};
}

describe('postAgentInstance', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('throws a too-long error when the request is too large', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false, 413) as never);

		const error = await postAgentInstance(
			'selected content',
			'sink-1',
			EActionType.IMPROVE_WRITING
		).catch((caught) => caught);

		expect(error).toBeInstanceOf(RequestTooLargeError);
		expect(error.message).toBe(
			'the-selected-content-is-too-long-shorten-it-and-try-again'
		);
	});

	it('throws the generic error on any other failure', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false) as never);

		const error = await postAgentInstance(
			'selected content',
			'sink-1',
			EActionType.IMPROVE_WRITING
		).catch((caught) => caught);

		expect(error).not.toBeInstanceOf(RequestTooLargeError);
		expect(error.message).toContain('Unable to invoke agent');
	});
});
