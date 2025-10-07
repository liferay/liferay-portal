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

	function search() {
		const searchURL = form.action;

		const filterValueInput = form.querySelector(
			'.custom-filter-value-input'
		);

		const queryString = FacetUtil.updateQueryString(
			filterValueInput.getAttribute('name'),
			[filterValueInput.value],
			window.location.search
		);

		Liferay.Util.navigate(searchURL + queryString);
	}

	function _handleSubmit(event) {
		event.preventDefault();
		event.stopPropagation();

		search();
	}

	Liferay.namespace('Search').FacetUtil = FacetUtil;

	const applyButton = form.querySelector('.custom-filter-apply-button');

	if (applyButton) {
		applyButton.addEventListener('click', search);
	}

	form.addEventListener('submit', _handleSubmit);

	return {
		dispose() {
			form.removeEventListener('submit', _handleSubmit);

			if (applyButton) {
				applyButton.removeEventListener('click', search);
			}
		},
	};
}
