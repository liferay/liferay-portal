/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Hides the filters dropdown and the filters resume, delegating the filter UI
 * to whoever drives this data set through an FDS connection. The filters this
 * data set declares stay in the state, so a consumer can read them and decide
 * whether to obey them.
 */
export default function propsTransformer({...otherProps}: any) {
	return {
		...otherProps,
		showFilters: false,
	};
}
