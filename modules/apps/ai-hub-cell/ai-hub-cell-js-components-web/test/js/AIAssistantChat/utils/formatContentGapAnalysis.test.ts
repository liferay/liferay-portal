/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import formatContentGapAnalysis from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/formatContentGapAnalysis';

describe('formatContentGapAnalysis', () => {
	it('parses a payload wrapped in markdown code fences', () => {
		const gaps = [
			{
				funnelStageName: 'Awareness',
				personaName: 'Decision Maker',
				reason: 'No content yet.',
				severity: 'high',
			},
		];

		const data = '```json\n' + JSON.stringify({gaps}) + '\n```';

		expect(formatContentGapAnalysis(data)).toBe(
			'- **Decision Maker / Awareness** (high) — No content yet.'
		);
	});

	it('renders the gaps list when the summary or its overview is absent', () => {
		const gaps = [
			{
				funnelStageName: 'Awareness',
				personaName: 'Decision Maker',
				reason: 'No content yet.',
				severity: 'high',
			},
		];

		const expected =
			'- **Decision Maker / Awareness** (high) — No content yet.';

		expect(formatContentGapAnalysis(JSON.stringify({gaps}))).toBe(expected);

		expect(
			formatContentGapAnalysis(JSON.stringify({gaps, summary: {}}))
		).toBe(expected);
	});

	it('renders the summary overview followed by a bulleted gaps list', () => {
		const data = JSON.stringify({
			gaps: [
				{
					currentCount: 0,
					funnelStageId: 'f1',
					funnelStageName: 'Awareness',
					personaId: 'p1',
					personaName: 'Decision Maker',
					reason: 'No content yet.',
					severity: 'high',
				},
				{
					currentCount: 0,
					funnelStageId: 'f2',
					funnelStageName: 'Retention',
					personaId: 'p2',
					personaName: 'End User',
					reason: 'Only one asset.',
					severity: 'medium',
				},
			],
			summary: {
				funnelStageCount: 2,
				gapCount: 2,
				overview: 'You have two gaps to address.',
				personaCount: 2,
			},
		});

		expect(formatContentGapAnalysis(data)).toBe(
			'You have two gaps to address.\n\n' +
				'- **Decision Maker / Awareness** (high) — No content yet.\n' +
				'- **End User / Retention** (medium) — Only one asset.'
		);
	});

	it('returns null for non-JSON payloads', () => {
		expect(formatContentGapAnalysis('I cannot fulfill this request')).toBe(
			null
		);
	});

	it('returns null when the payload is not a gap-analysis object', () => {
		expect(formatContentGapAnalysis(JSON.stringify({foo: 'bar'}))).toBe(
			null
		);
	});

	it('returns null when there is nothing to show', () => {
		expect(
			formatContentGapAnalysis(JSON.stringify({gaps: [], summary: {}}))
		).toBe(null);
	});
});
