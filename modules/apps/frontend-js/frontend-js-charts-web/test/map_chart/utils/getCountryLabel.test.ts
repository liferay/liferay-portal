/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getCountryLabel} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/getCountryLabel';

describe('getCountryLabel', () => {
	it('prefers an explicit label over the resolved display name', () => {
		expect(
			getCountryLabel({country: 'FR', label: 'Custom Label', value: 1})
		).toBe('Custom Label');
	});

	it('resolves a localized display name for a country with a baked name', () => {
		expect(getCountryLabel({country: 'FR', value: 1})).toBe(
			'country.france'
		);
	});

	it('falls back to the raw country code for a country without a baked name', () => {
		expect(getCountryLabel({country: 'Kosovo', value: 1})).toBe('Kosovo');
	});
});
