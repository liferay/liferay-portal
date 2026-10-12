/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Autocomplete} from 'commerce-frontend-js';
import {openSelectionModal} from 'frontend-js-components-web';

export default function ({
	autocompleteInitialLabel,
	autocompleteInitialValue,
	itemSelectorURL,
	namespace,
}) {
	Autocomplete('autocomplete', 'autocomplete-root', {
		apiUrl: '/o/headless-commerce-admin-channel/v1.0/tax-categories',
		initialLabel: autocompleteInitialLabel,
		initialValue: autocompleteInitialValue,
		inputId: 'shippingTaxCategoryId',
		inputName: `${namespace}shippingTaxSettings--taxCategoryId--`,
		itemsKey: 'id',
		itemsLabel: ['name', 'LANG'],
		onValueUpdated(value, shippingTaxData) {
			if (value) {
				window.document.querySelector('#shippingTaxCategoryId').value =
					shippingTaxData.id;
			}
			else {
				window.document.querySelector('#shippingTaxCategoryId').value =
					0;
			}
		},
	});

	const fileEntryIdInput = document.getElementById(`${namespace}fileEntryId`);
	const fileEntryNameInput = document.getElementById(
		`${namespace}fileEntryNameInput`
	);
	const fileEntryRemoveIcon = document.getElementById(
		`${namespace}fileEntryRemoveIcon`
	);
	const selectFileButton = document.getElementById(
		`${namespace}selectFileButton`
	);

	if (fileEntryNameInput && fileEntryRemoveIcon && selectFileButton) {
		selectFileButton.addEventListener('click', (event) => {
			event.preventDefault();

			openSelectionModal({
				onSelect: (selectedItem) => {
					if (!selectedItem) {
						return;
					}

					const value = JSON.parse(selectedItem.value);

					if (fileEntryIdInput) {
						fileEntryIdInput.value = value.fileEntryId;
					}

					fileEntryRemoveIcon.classList.remove('hide');

					fileEntryNameInput.innerHTML =
						'<a>' + Liferay.Util.escape(value.title) + '</a>';
				},
				selectEventName: 'addFileEntry',
				title: Liferay.Language.get('select-file'),
				url: itemSelectorURL,
			});
		});

		fileEntryRemoveIcon.addEventListener('click', (event) => {
			event.preventDefault();

			if (fileEntryIdInput) {
				fileEntryIdInput.value = 0;
			}

			fileEntryNameInput.innerText = '';

			fileEntryRemoveIcon.classList.add('hide');
		});
	}

	const guestBrowsingEnabledInput = document.getElementById(
		`${namespace}guestBrowsingEnabled`
	);
	const guestCheckoutEnabledInput = document.getElementById(
		`${namespace}guestCheckoutEnabled`
	);

	if (guestBrowsingEnabledInput && guestCheckoutEnabledInput) {
		const updateGuestCheckoutEnabledInput = () => {
			if (!guestBrowsingEnabledInput.checked) {
				guestCheckoutEnabledInput.checked = false;
			}

			guestCheckoutEnabledInput.disabled =
				!guestBrowsingEnabledInput.checked;
		};

		updateGuestCheckoutEnabledInput();

		guestBrowsingEnabledInput.addEventListener(
			'change',
			updateGuestCheckoutEnabledInput
		);
	}
}
