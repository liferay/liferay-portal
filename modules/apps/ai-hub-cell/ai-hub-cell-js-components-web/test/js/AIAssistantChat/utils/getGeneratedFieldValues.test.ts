/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getGeneratedFieldValues from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/getGeneratedFieldValues';

describe('getGeneratedFieldValues', () => {
	it('parses a JSON object of field values', () => {
		expect(getGeneratedFieldValues('{"title": "Hello"}')).toEqual({
			title: 'Hello',
		});
	});

	it('returns an empty object for invalid JSON', () => {
		expect(getGeneratedFieldValues('not json')).toEqual({});
	});

	it('returns an empty object for a JSON array', () => {
		expect(getGeneratedFieldValues('["a", "b"]')).toEqual({});
	});

	it('returns an empty object for a JSON primitive', () => {
		expect(getGeneratedFieldValues('"just a string"')).toEqual({});
	});
});
