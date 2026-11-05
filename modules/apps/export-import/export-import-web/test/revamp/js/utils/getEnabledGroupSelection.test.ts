/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getEnabledGroupSelection} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/getEnabledGroupSelection';

describe('getEnabledGroupSelection', () => {
	it.each([
		[false, false, undefined],
		[true, false, 'sites'],
		[false, true, 'spaces'],
		[true, true, 'sitesAndSpaces'],
	])(
		'with sites %s and spaces %s names the selection %s',
		(siteSelectionEnabled, spaceSelectionEnabled, expected) => {
			expect(
				getEnabledGroupSelection({
					siteSelectionEnabled,
					spaceSelectionEnabled,
				})
			).toBe(expected);
		}
	);

	it('names no selection when nothing is enabled', () => {
		expect(getEnabledGroupSelection()).toBeUndefined();
	});
});
