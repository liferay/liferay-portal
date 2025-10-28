/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {getLabel} from '../js/ObjectRelationship/ObjectRelationship';

describe('getLabel', () => {
	it('returns as a string the same boolean value passed in booleanField', () => {
		let label = getLabel(
			{booleanField: true},
			'booleanField',
			'en_US',
			'Boolean'
		);

		expect(label).toBe('true');

		label = getLabel(
			{booleanField: false},
			'booleanField',
			'en_US',
			'Boolean'
		);

		expect(label).toBe('false');
	});
});
