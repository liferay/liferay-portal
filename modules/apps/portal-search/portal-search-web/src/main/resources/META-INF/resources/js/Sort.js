/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FacetUtil} from './FacetUtil';

export default function ({namespace: portletNamespace}) {
	const form = document.getElementById(`${portletNamespace}fm`);

	if (!form) {
		return;
	}

	function _handleChangeSort(event) {
		const sortSelect = event.target.value;

		const key = document.getElementById(
			`${portletNamespace}sort-parameter-name`
		).value;

		if (key) {
			const url = new URL(window.location.href);

			url.search = FacetUtil.updateQueryString(
				key,
				[sortSelect],
				window.location.search
			);

			Liferay.Util.navigate(url.toString());
		}
	}

	Liferay.namespace('Search').FacetUtil = FacetUtil;

	const sortSelection = document.getElementById(
		`${portletNamespace}sortSelection`
	);

	if (sortSelection) {
		sortSelection.addEventListener('change', _handleChangeSort);
	}

	return {
		dispose() {
			if (sortSelection) {
				sortSelection.removeEventListener('change', _handleChangeSort);
			}
		},
	};
}
