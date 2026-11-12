/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	evaluate,
	mergePages,
} from '../../../src/main/resources/META-INF/resources/js/utils/evaluation.es';
import {PagesVisitor} from '../../../src/main/resources/META-INF/resources/js/utils/visitors.es';

// The "select" field is localizable. The value the server validates comes from
// the field's `value` property, so these tests assert on `value`.

const buildPages = (fieldProps) => [
	{
		rows: [
			{
				columns: [
					{
						fields: [
							{
								fieldName: 'select',
								localizable: true,
								name: 'select',
								type: 'select',
								...fieldProps,
							},
						],
					},
				],
			},
		],
	},
];

const mergeField = ({sourceVisible, viewMode = true, ...fieldProps}) => {
	const mergedPages = mergePages(
		'en_US',
		'en_US',
		'select',
		buildPages({...fieldProps, visible: true}),
		buildPages({...fieldProps, visible: sourceVisible}),
		viewMode
	);

	return new PagesVisitor(mergedPages).findField(
		(field) => field.fieldName === 'select'
	);
};

describe('evaluation', () => {
	describe('evaluate', () => {
		const originalThemeDisplay = themeDisplay;

		beforeEach(() => {
			themeDisplay = {
				...originalThemeDisplay,
				getSiteGroupId: () => '20126',
			};
		});

		afterEach(() => {
			jest.useRealTimers();

			themeDisplay = originalThemeDisplay;
		});

		it('evaluates a configuration in the default language', async () => {
			jest.useFakeTimers();

			fetch.mockResponseOnce(JSON.stringify([]));

			const evaluation = evaluate('title', {
				containerId: '_portletNamespace_editConfiguration',
				defaultLanguageId: 'en_US',
				editingLanguageId: 'es_ES',
				pages: [],
				portletNamespace: '_portletNamespace_',
			});

			jest.runAllTimers();

			await evaluation;

			const [, {body}] = fetch.mock.calls[0];

			expect(body.get('languageId')).toBe('en_US');
		});

		it('evaluates every form when several forms evaluate at the same time', async () => {
			jest.useFakeTimers();

			fetch.mockResponseOnce(JSON.stringify([]));
			fetch.mockResponseOnce(JSON.stringify([]));

			const containerIds = [
				'_portletNamespace_dataEngineLayoutRenderer1',
				'_portletNamespace_dataEngineLayoutRenderer2',
			];

			const evaluations = containerIds.map((containerId) =>
				evaluate(null, {
					containerId,
					defaultLanguageId: 'en_US',
					editingLanguageId: 'en_US',
					pages: [],
					portletNamespace: '_portletNamespace_',
				})
			);

			jest.runAllTimers();

			expect(
				fetch.mock.calls.map(
					([, {body}]) =>
						JSON.parse(body.get('serializedFormContext'))
							.containerId
				)
			).toEqual(containerIds);

			await Promise.all(evaluations);
		});
	});

	describe('mergePages', () => {
		it('clears the value when a field without a predefined value becomes visible in view mode', () => {
			const field = mergeField({
				predefinedValue: '',
				sourceVisible: false,
				value: 'stale',
			});

			expect(field.value).toEqual('');
		});

		it('does not reset the value when not in view mode', () => {
			const field = mergeField({
				predefinedValue: ['option2'],
				sourceVisible: false,
				value: ['option1'],
				viewMode: false,
			});

			expect(field.value).toEqual(['option1']);
		});

		it('keeps the value when the field visibility does not change', () => {
			const field = mergeField({
				predefinedValue: ['option2'],
				sourceVisible: true,
				value: ['option1'],
			});

			expect(field.value).toEqual(['option1']);
		});

		it('restores the predefined value when a field becomes visible in view mode', () => {
			const field = mergeField({
				predefinedValue: ['option2'],
				sourceVisible: false,
				value: '',
			});

			expect(field.value).toEqual(['option2']);
		});
	});
});
