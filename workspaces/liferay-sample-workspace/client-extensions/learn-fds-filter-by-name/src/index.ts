/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {
	FDSFilter,
	FDSFilterHTMLElementBuilderArgs,
} from '@liferay/js-api/data-set';

type FilterData = string;

function descriptionBuilder(selectedData: FilterData): string {
	return selectedData;
}

function htmlElementBuilder({
	fieldName,
	filter,
	setFilter,
}: FDSFilterHTMLElementBuilderArgs<FilterData>): HTMLElement {
	const input = document.createElement('input');

	if (filter.selectedData) {
		input.value = filter.selectedData;
	}
	else {
        input.value = "contains(creator,'')";

	}

	input.className = 'form-control';
	input.placeholder = 'Search by Name';

	const button = document.createElement('button');

	button.className = 'btn btn-block btn-secondary btn-sm mt-2';
	button.innerText = 'Submit';
	button.onclick = () =>
		setFilter({
			selectedData: input.value,
		});

	const div = document.createElement('div');

	div.className = 'dropdown-item';

	div.appendChild(input);
	div.appendChild(button);

	return div;
}

function oDataQueryBuilder(selectedData: FilterData): string {
	return selectedData;
}

const fdsFilter: FDSFilter<FilterData> = {
	descriptionBuilder,
	htmlElementBuilder,
	oDataQueryBuilder,
};

export default fdsFilter;