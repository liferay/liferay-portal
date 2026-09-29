/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LastViewedDataRenderer} from '../../../../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/data_renderers/LastViewedDataRenderer';

describe('LastViewedDataRenderer', () => {
	it('formats a valid date', () => {
		expect(
			LastViewedDataRenderer({
				itemData: {lastViewed: '2026-08-27T10:00:00Z'} as any,
			})
		).toBe('Aug 27, 2026');
	});

	it('renders a dash when the date is absent or invalid', () => {
		expect(LastViewedDataRenderer({itemData: {} as any})).toBe('-');
		expect(
			LastViewedDataRenderer({itemData: {lastViewed: ''} as any})
		).toBe('-');
		expect(
			LastViewedDataRenderer({
				itemData: {lastViewed: 'not-a-date'} as any,
			})
		).toBe('-');
	});
});
