/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria} from '../../../src/main/resources/META-INF/resources/js/types';
import {getValueOptions} from '../../../src/main/resources/META-INF/resources/js/util/getValueOptions';

const BOOLEAN: AudiencesCriteria = {
	icon: 'check',
	inputType: 'boolean',
	key: 'user_authentication',
	label: 'User Authentication',
	options: [],
	type: 'boolean',
};

const SELECT: AudiencesCriteria = {
	icon: 'text',
	inputType: 'select',
	key: 'language',
	label: 'Language',
	options: [{label: 'English', value: 'en'}],
	type: 'string',
};

describe('getValueOptions', () => {
	it('returns true and false options for a boolean criteria', () => {
		expect(getValueOptions(BOOLEAN).map((option) => option.value)).toEqual([
			'true',
			'false',
		]);
	});

	it('returns the criteria options otherwise', () => {
		expect(getValueOptions(SELECT)).toBe(SELECT.options);
	});
});
