/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {readFileSync} from 'fs';
import {join} from 'path';

import en from '../../src/main/resources/META-INF/resources/js/i18n/en';
import {
	TranslationKey,
	setMessages,
	t,
} from '../../src/main/resources/META-INF/resources/js/i18n/index';
import {liferayMessages} from '../../src/main/resources/META-INF/resources/js/i18n/liferayMessages';

afterEach(() => setMessages(null));

const SELF_RESOLVING = new Set(['1:1', '16:9', '3:4', '4:3', '9:16']);

describe('the catalogues stay in step', () => {
	it('liferayMessages carries exactly the keys of en.ts', () => {
		expect(Object.keys(liferayMessages).sort()).toEqual(
			Object.keys(en).sort()
		);
	});

	it('every translatable key exists in Language.properties', () => {
		const properties = readFileSync(
			join(
				__dirname,
				'../../../../portal-language/portal-language-lang/src/main/resources/content/Language.properties'
			),
			'utf8'
		);

		const defined = new Set(
			[...properties.matchAll(/^([^#=\n]+)=/gm)].map(([, key]) => key)
		);

		const missing = Object.keys(en).filter(
			(key) => !SELF_RESOLVING.has(key) && !defined.has(key)
		);

		expect(missing).toEqual([]);
	});
});

describe('the i18n seam', () => {
	it('formats Liferay-style placeholders', () => {
		expect(t('editor-loaded', 1200, 800)).toBe(
			'Image editor opened. Image is 1200 by 800 pixels.'
		);
	});

	it('falls back to the key rather than to silence', () => {
		expect(t('not-a-real-key' as TranslationKey)).toBe('not-a-real-key');
	});

	it('lets a host override part of the dictionary, Clay-style', () => {
		setMessages({close: 'Cerrar'});

		expect(t('close')).toBe('Cerrar');

		expect(t('editing-image')).toBe('Editing image');

		setMessages(null);

		expect(t('close')).toBe('Close');
	});
});
