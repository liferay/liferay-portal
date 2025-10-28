/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DateRangeFilter from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/implementation/DateRangeFilter';
import {EEntityFieldType} from '../../../../../src/main/resources/META-INF/resources/management_bar/controls/filters/utils/types';

const {getOdataString} = DateRangeFilter;

const fromDate = {day: 1, month: 1, year: 2025};
const toDate = {day: 31, month: 1, year: 2025};

describe('DateRangeFilter.getOdataString', () => {
	it('returns an empty string if no dates are selected', () => {
		const result = getOdataString({
			entityFieldType: EEntityFieldType.DATE,
			id: 'testField',
			selectedData: {},
		} as any);

		expect(result).toBe('');
	});

	describe('with entityFieldType="date"', () => {
		it('generates a "ge" filter when only "from" is provided', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.DATE,
				id: 'testField',
				selectedData: {from: fromDate},
			} as any);

			expect(result).toBe('testField ge 2025-01-01');
		});

		it('generates an "le" filter when only "to" is provided', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.DATE,
				id: 'testField',
				selectedData: {to: toDate},
			} as any);

			expect(result).toBe('testField le 2025-01-31');
		});

		it('generates a "ge" and "le" filter when both dates are provided', () => {
			const result = getOdataString({
				entityFieldType: EEntityFieldType.DATE,
				id: 'testField',
				selectedData: {from: fromDate, to: toDate},
			} as any);

			expect(result).toBe(
				'testField ge 2025-01-01) and (testField le 2025-01-31'
			);
		});
	});
});
