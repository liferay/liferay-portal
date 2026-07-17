/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {postAIIssueReport} from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/api';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const randomString = () => Math.random().toString(36).slice(2, 10);

const authorizationToken = {
	accessToken: randomString(),
	serviceURL: 'https://ai-hub.liferay.com',
	userToken: randomString(),
};

const payload = {
	agentDefinitionExternalReferenceCodes: ['agent-1'],
	feedback: 'positive' as const,
	surface: 'clickToChat' as const,
};

function jsonResponse(body: unknown, ok = true) {
	return {
		json: () => Promise.resolve(body),
		ok,
		status: ok ? 200 : 500,
		statusText: ok ? 'OK' : 'Internal Server Error',
		text: () => Promise.resolve(JSON.stringify(body)),
	};
}

describe('postAIIssueReport', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('authorizes the report with the access token and the user token', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(
			jsonResponse({id: 'report-1'}) as never
		);

		await postAIIssueReport(payload);

		const [, reportInit] = mockFetch.mock.calls[1];

		const headers = reportInit?.headers as Headers;

		expect(headers.get('Authorization')).toBe(
			`Bearer ${authorizationToken.accessToken}`
		);
		expect(headers.get('Liferay-AI-Hub-Cell-On-Behalf-Of')).toBe(
			authorizationToken.userToken
		);
	});

	it('posts the report to the reports endpoint of the service URL', async () => {
		const responseId = randomString();

		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(
			jsonResponse({id: responseId}) as never
		);

		const result = await postAIIssueReport(payload);

		expect(result).toEqual({id: responseId});
		expect(mockFetch).toHaveBeenCalledTimes(2);

		const [reportURL] = mockFetch.mock.calls[1];

		expect(reportURL).toBe(
			'https://ai-hub.liferay.com/o/ai-hub/v1.0/reports'
		);
	});

	it('throws error when the request fails', async () => {
		mockFetch.mockResolvedValueOnce(
			jsonResponse(authorizationToken) as never
		);
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false) as never);

		await expect(postAIIssueReport(payload)).rejects.toThrow(
			'Unable to send feedback'
		);
	});

	it('throws error without posting the report when no authorization token is generated', async () => {
		mockFetch.mockResolvedValueOnce(jsonResponse({}, false) as never);

		await expect(postAIIssueReport(payload)).rejects.toThrow(
			'Unable to generate authorization token.'
		);
	});
});
