/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import React from 'react';

import {getDateValue} from '../js/index.js';

describe('getDateValue', () => {
	it('returns the date in the correct format', () => {
		const parameterDate = new Date('06/27/2025');

		const day = parameterDate.getDate();
		const month = parameterDate.getMonth();
		const year = parameterDate.getFullYear();

		render(
			<>
				<input id="parameterDateDay" value={day} />
				<input id="parameterDateMonth" value={month} />
				<input id="parameterDateYear" value={year} />
			</>
		);

		const dateFormat = getDateValue('');

		expect(dateFormat).toBe("2025-06-27");
	});
});
