/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DESCRIPTION_AUTOFIX_DEFINITION} from '../../../../js/components/insights_view/autofix_definitions/DescriptionAutofixDefinition';
import {TITLE_AUTOFIX_DEFINITION} from '../../../../js/components/insights_view/autofix_definitions/TitleAutofixDefinition';
import {
	WORKFLOW_STATUS_APPROVED,
	generateCandidates,
	patchScanInsight,
	postAutofix,
} from '../../../../js/components/insights_view/services/AutofixService';

const mockFetch = jest.fn();
const mockInvokeAgent = jest.fn();

jest.mock('../../../../js/agent/invokeAgent', () => ({
	invokeAgent: (...args: unknown[]) => mockInvokeAgent(...args),
}));

jest.mock('frontend-js-web', () => ({
	fetch: (...args: unknown[]) => mockFetch(...args),
}));

describe('generateCandidates', () => {
	beforeEach(() => {
		mockInvokeAgent.mockReset();
	});

	it('parses candidates with their rationale', async () => {
		mockInvokeAgent.mockResolvedValue(
			JSON.stringify({
				candidates: [
					{rationale: 'r1', title: 'A'},
					{rationale: 'r2', title: 'B'},
				],
			})
		);

		const candidates = await generateCandidates(
			TITLE_AUTOFIX_DEFINITION,
			'content'
		);

		expect(candidates).toHaveLength(2);
		expect(candidates[0]).toEqual({rationale: 'r1', value: 'A'});
	});

	it('parses candidates wrapped in a markdown code fence', async () => {
		mockInvokeAgent.mockResolvedValue(
			'```json\n' +
				JSON.stringify({
					candidates: [{rationale: 'r1', title: 'A'}],
				}) +
				'\n```'
		);

		const candidates = await generateCandidates(
			TITLE_AUTOFIX_DEFINITION,
			'content'
		);

		expect(candidates).toHaveLength(1);
		expect(candidates[0]).toEqual({rationale: 'r1', value: 'A'});
	});

	it('parses candidates wrapped in a code fence with trailing prose', async () => {
		mockInvokeAgent.mockResolvedValue(
			'```json\n' +
				JSON.stringify({
					candidates: [{rationale: 'r1', title: 'A'}],
				}) +
				'\n```\nLet me know if you need more options!'
		);

		const candidates = await generateCandidates(
			TITLE_AUTOFIX_DEFINITION,
			'content'
		);

		expect(candidates).toHaveLength(1);
		expect(candidates[0]).toEqual({rationale: 'r1', value: 'A'});
	});

	it('throws the agent reported reason instead of rendering it as a candidate', async () => {
		mockInvokeAgent.mockResolvedValue(
			JSON.stringify({
				error: 'The page content is too sparse to analyze.',
			})
		);

		await expect(
			generateCandidates(TITLE_AUTOFIX_DEFINITION, 'content')
		).rejects.toThrow('The page content is too sparse to analyze.');
	});

	it('throws with the raw response text when the response is not JSON', async () => {
		mockInvokeAgent.mockResolvedValue(
			'You have exceeded your monthly token quota.'
		);

		await expect(
			generateCandidates(TITLE_AUTOFIX_DEFINITION, 'content')
		).rejects.toThrow('You have exceeded your monthly token quota.');
	});

	it('filters out candidates without a title', async () => {
		mockInvokeAgent.mockResolvedValue(
			JSON.stringify({
				candidates: [{title: 'A'}, {rationale: 'no title'}],
			})
		);

		const candidates = await generateCandidates(
			TITLE_AUTOFIX_DEFINITION,
			'content'
		);

		expect(candidates).toHaveLength(1);
	});

	it('throws when the response has no candidates array', async () => {
		mockInvokeAgent.mockResolvedValue(JSON.stringify({foo: 'bar'}));

		await expect(
			generateCandidates(TITLE_AUTOFIX_DEFINITION, 'content')
		).rejects.toThrow();
	});

	it('invokes the title generator agent with the page content', async () => {
		mockInvokeAgent.mockResolvedValue(
			JSON.stringify({candidates: [{title: 'A'}]})
		);

		await generateCandidates(TITLE_AUTOFIX_DEFINITION, 'my page content');

		expect(mockInvokeAgent).toHaveBeenCalledWith({
			agentExternalReferenceCode: 'L_SEO_STUDIO_TITLE_GENERATOR',
			context: {pageContent: 'my page content'},
		});
	});

	it('invokes the description generator agent with the page content', async () => {
		mockInvokeAgent.mockResolvedValue(
			JSON.stringify({candidates: [{title: 'A'}]})
		);

		await generateCandidates(
			DESCRIPTION_AUTOFIX_DEFINITION,
			'my page content'
		);

		expect(mockInvokeAgent).toHaveBeenCalledWith({
			agentExternalReferenceCode: 'L_SEO_STUDIO_DESCRIPTION_GENERATOR',
			context: {pageContent: 'my page content'},
		});
	});
});

describe('postAutofix', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('posts the insight type, value, and page URL to the SEO Studio Autofix backend', async () => {
		mockFetch.mockResolvedValue({ok: true});

		await postAutofix({
			insightType: TITLE_AUTOFIX_DEFINITION.insightTypeName,
			pageURL: 'http://example.com/web/customer/products',
			value: 'New Title',
		});

		const [url, options] = mockFetch.mock.calls[0];

		expect(url).toBe('/o/seo-studio/v1.0/autofix');
		expect(options.method).toBe('POST');

		const body = JSON.parse(options.body);

		expect(body.insightType).toBe(TITLE_AUTOFIX_DEFINITION.insightTypeName);
		expect(body.pageURL).toBe('http://example.com/web/customer/products');
		expect(body.value).toBe('New Title');
	});

	it('throws when the response is not ok', async () => {
		mockFetch.mockResolvedValue({ok: false});

		await expect(
			postAutofix({
				insightType: TITLE_AUTOFIX_DEFINITION.insightTypeName,
				pageURL: 'http://example.com/web/c/p',
				value: 't',
			})
		).rejects.toThrow();
	});
});

describe('patchScanInsight', () => {
	beforeEach(() => {
		mockFetch.mockReset();
	});

	it('marks the scan insight approved through the SEO Studio REST API', async () => {
		mockFetch.mockResolvedValue({ok: true});

		await patchScanInsight(123);

		const [url, options] = mockFetch.mock.calls[0];

		expect(url).toBe('/o/seo-studio/scan-insights/123');
		expect(options.method).toBe('PATCH');

		const body = JSON.parse(options.body);

		expect(body.state).toBe(WORKFLOW_STATUS_APPROVED);
		expect(body.resolvedDate).toEqual(expect.any(String));
	});

	it('throws when the response is not ok', async () => {
		mockFetch.mockResolvedValue({ok: false});

		await expect(patchScanInsight(1)).rejects.toThrow();
	});
});
