/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

import {convertToUTC} from '../../utils/convertToUTC';

const localDate = '2025-05-14 17:37';
const utcDate = '2025-05-14T17:37Z';

describe('convertToUTC', () => {
	it('receives a Local date and returns it in UTC format', () => {
		expect(convertToUTC(localDate)).toStrictEqual(utcDate);
	});
});
