/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {validate} from '../../../../../src/main/resources/META-INF/resources/js/common/components/forms/validations'; // adjust import path

const required = (value: any) => (!value ? 'Required' : undefined);

describe('validate()', () => {
	it('Deletes previous error if field has no validations', () => {
		const fields = {
			fieldName: [],
		};

		const values = {fieldName: 'name'};
		const errors = {fieldName: 'Old error'};

		const result = validate(fields, values, errors);

		expect(result.fieldName).toBeUndefined();
	});

	it('Set error if validation fails', () => {
		const fields = {
			fieldName: [required],
		};

		const values = {fieldName: ''};
		const result = validate(fields, values, {});

		expect(result.fieldName).toBe('Required');
	});

	it('Clears error if validation passes', () => {
		const fields = {
			fieldName: [required],
		};

		const values = {fieldName: 'ok'};
		const errors = {fieldName: 'Old error'};

		const result = validate(fields, values, errors);

		expect(result.fieldName).toBeUndefined();
	});
});
