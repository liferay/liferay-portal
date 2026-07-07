/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getViewBoxWidth} from '../../../src/main/resources/META-INF/resources/js/map_chart/utils/getViewBoxWidth';

describe('getViewBoxWidth', () => {
	it('extracts the width segment from a viewBox string', () => {
		expect(getViewBoxWidth('0 0 558 282')).toBe(558);
	});

	it('extracts the width segment from a cropped viewBox string', () => {
		expect(getViewBoxWidth('178.5 25 200 117')).toBe(200);
	});
});
