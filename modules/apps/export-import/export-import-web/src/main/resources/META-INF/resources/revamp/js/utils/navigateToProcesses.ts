/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikHelpers, FormikValues} from 'formik';

export function navigateToProcesses(
	setStatus: FormikHelpers<FormikValues>['setStatus'],
	message: string,
	url: string
) {
	setStatus({submitted: true});

	Liferay.Util.openToast({message, type: 'success'});

	Liferay.Util.navigate(url);
}
