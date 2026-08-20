/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues} from 'formik';

export function getProcessFormErrors(values: FormikValues): {
	[key: string]: string;
} {
	const errors: {[key: string]: string} = {};

	if (!values.name) {
		errors.name = Liferay.Language.get('this-field-is-required');
	}

	if (!values.contentSelection) {
		errors.contentSelection = Liferay.Language.get(
			'please-select-at-least-one-entity-type-to-continue'
		);
	}

	return errors;
}
