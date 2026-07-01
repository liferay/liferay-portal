/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {fetch, objectToFormData, setCSPNonce} from 'frontend-js-web';

export default function ({
	changeThemeButtonId,
	initialSelectedThemeId,
	lookAndFeelDetailURL,
	namespace,
	selectThemeURL,
	themeContainerId,
}) {
	let selectedThemeId = initialSelectedThemeId;

	const themeContainer = document.getElementById(themeContainerId);

	const onChangeThemeButtonClick = (event) => {
		event.preventDefault();

		const url = new URL(selectThemeURL);

		url.searchParams.set(`${namespace}themeId`, selectedThemeId);

		openSelectionModal({
			onSelect: (selectedItem) => {
				const itemValue = JSON.parse(selectedItem.value);

				if (
					itemValue.themeId &&
					selectedThemeId !== itemValue.themeId
				) {
					themeContainer.innerHTML = '';

					const loadingIndicator = document.createElement('div');

					loadingIndicator.classList.add('loading-animation');

					themeContainer.appendChild(loadingIndicator);

					fetch(lookAndFeelDetailURL, {
						body: objectToFormData({
							[`${namespace}themeId`]: itemValue.themeId,
						}),
						method: 'POST',
					})
						.then((response) => response.text())
						.then((response) => {
							response = setCSPNonce(response);

							const range = document.createRange();
							const fragment =
								range.createContextualFragment(response);

							themeContainer.removeChild(loadingIndicator);
							themeContainer.appendChild(fragment);

							selectedThemeId = itemValue.themeId;

							updateCheckboxNames(namespace, themeContainer);
						});
				}
			},
			selectEventName: `${namespace}selectTheme`,
			title: Liferay.Language.get('available-themes'),
			url: url.href,
		});
	};

	const changeThemeButton = document.getElementById(changeThemeButtonId);

	changeThemeButton.addEventListener('click', onChangeThemeButtonClick);

	return {
		dispose() {
			changeThemeButton.removeEventListener(
				'click',
				onChangeThemeButtonClick
			);
		},
	};
}

/**
 * Update checkboxNames input to reflect the actual checkbox in the page,
 * this value is used by PortletRequestImpl to set the parameters in the request
 */
function updateCheckboxNames(namespace, themeContainer) {
	const nextCheckboxNames = Array.from(
		themeContainer.querySelectorAll('input[type=checkbox]')
	)
		.map((input) => input.name.replace(namespace, ''))
		.join(',');

	const checkboxNamesInput = document.getElementById(
		`${namespace}checkboxNames`
	);

	if (checkboxNamesInput) {
		checkboxNamesInput.value = nextCheckboxNames;
	}
}
