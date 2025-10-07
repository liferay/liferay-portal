/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {formatPublishedDate} from '../../../../js/components/content-dashboard/visitors-behavior/utils';

describe('utils', () => {
	it.each([
		{
			input: '2025-01-28',
			output: '2025-01-28T00:00',
		},
		{
			input: '2025-01-28T00:00:00.000Z',
			output: '2025-01-28T00:00',
		},
		{
			input: '2025-01-28T01:10:00.000Z',
			output: '2025-01-28T01:00',
		},
		{
			input: '2025-01-28T10:20:00.000Z',
			output: '2025-01-28T10:00',
		},
		{
			input: '2025-01-28T20:30:00.000Z',
			output: '2025-01-28T20:00',
		},
	])('format published date', (date) => {
		const formattedDate = formatPublishedDate(date.input);

		expect(formattedDate).toEqual(date.output);
	});
});
