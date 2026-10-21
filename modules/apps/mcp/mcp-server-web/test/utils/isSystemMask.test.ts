/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import isSystemMask from '../../src/main/resources/META-INF/resources/js/utils/isSystemMask';

import type {DataMask} from '../../src/main/resources/META-INF/resources/js/types/DataMask';

describe('isSystemMask', () => {
	const dataMask = (key: string): DataMask => ({
		detectionRegex: '\\d+',
		maskType: {key: key as DataMask['maskType']['key'], name: key},
		name: 'mask',
		replacementValue: '[X]',
	});

	it('returns true for a system mask', () => {
		expect(isSystemMask(dataMask('system'))).toBe(true);
	});

	it('returns false for a custom mask', () => {
		expect(isSystemMask(dataMask('custom'))).toBe(false);
	});

	it('returns false when there is no mask', () => {
		expect(isSystemMask(null)).toBe(false);
	});
});
