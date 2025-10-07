/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const conditionContainerElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentEntryLinkNamespace}Container`
);
const imageConditionElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentEntryLinkNamespace}ImageCondition`
);

if (imageConditionElement.src !== '') {
	conditionContainerElement.classList.remove('d-none');
}
