/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FIELD_TYPE_LABEL,
	getDefaultField,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';

describe('getDefaultField', () => {
	let getDefaultLanguageIdSpy: jest.SpyInstance;
	let getLanguageIdSpy: jest.SpyInstance;

	beforeEach(() => {
		getDefaultLanguageIdSpy = jest.spyOn(
			Liferay.ThemeDisplay,
			'getDefaultLanguageId'
		);
		getLanguageIdSpy = jest.spyOn(Liferay.ThemeDisplay, 'getLanguageId');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('uses languageKey to seed both locale slots correctly when they differ', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('es_ES');

		const getLanguageSpy = jest
			.spyOn(Liferay.Language, 'get')
			.mockImplementation((key: string) =>
				key === 'title' ? 'Título' : key
			);

		try {
			const field = getDefaultField({
				defaultLanguageLabels: {
					labels: {title: 'Title'},
					locale: 'en_US',
				},
				languageKey: 'title',
				parent: getUuid(),
				type: 'text',
			});

			expect(field.label).toEqual({
				en_US: 'Title',
				es_ES: 'Título',
			});
		}
		finally {
			getLanguageSpy.mockRestore();
		}
	});

	it('produces a single label key when current and default language match', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('en_US');

		const field = getDefaultField({
			defaultLanguageLabels: {labels: {}, locale: 'en_US'},
			parent: getUuid(),
			type: 'text',
		});

		expect(Object.keys(field.label)).toEqual(['en_US']);
	});

	it('falls back to FIELD_TYPE_LABEL when no label is provided', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('es_ES');

		const field = getDefaultField({
			defaultLanguageLabels: {labels: {}, locale: 'en_US'},
			parent: getUuid(),
			type: 'text',
		});

		expect(field.label).toEqual({
			en_US: FIELD_TYPE_LABEL.text,
			es_ES: FIELD_TYPE_LABEL.text,
		});
	});

	it('seeds the default-language label from the singleton when no label is provided', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('es_ES');

		const defaultLanguageLabels = {
			labels: {
				'date-and-time': 'Date and time',
				'text': 'Text',
			},
			locale: 'en_US',
		};

		expect(
			getDefaultField({
				defaultLanguageLabels,
				parent: getUuid(),
				type: 'text',
			}).label.en_US
		).toBe('Text');

		expect(
			getDefaultField({
				defaultLanguageLabels,
				parent: getUuid(),
				type: 'datetime',
			}).label.en_US
		).toBe('Date and time');
	});
});
