/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FieldValidator} from 'formik';

const required: FieldValidator = (value) =>
	!String(value ?? '').trim()
		? Liferay.Language.get('this-field-is-required')
		: undefined;

export {required};
