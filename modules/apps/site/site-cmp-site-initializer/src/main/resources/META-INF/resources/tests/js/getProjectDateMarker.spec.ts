/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getProjectDateMarker from '../../js/components/props_transformer/views/calendar_view/utils/getProjectDateMarker';

const projectDates = {
	dueDate: '2026-07-31',
	startDate: '2026-07-01',
};

describe('getProjectDateMarker', () => {
	it('returns "dueDate" when the cell matches the due date', () => {
		expect(getProjectDateMarker('2026-07-31', projectDates)).toBe('dueDate');
	});

	it('returns "startDate" when the cell matches the start date', () => {
		expect(getProjectDateMarker('2026-07-01', projectDates)).toBe('startDate');
	});

	it('returns null when project dates are missing', () => {
		expect(getProjectDateMarker('2026-07-01', null)).toBeNull();
	});

	it('returns null when the cell matches neither date', () => {
		expect(getProjectDateMarker('2026-07-15', projectDates)).toBeNull();
	});
});
