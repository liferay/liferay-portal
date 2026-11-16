/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {toggleDisabled} from 'frontend-js-web';

import {previewSeoFireChange} from './PreviewSeoEvents';

export default function ({namespace, uploadOpenGraphImageURL}) {
	const openGraphImageButton = document.getElementById(
		`${namespace}openGraphImageButton`
	);
	const openGraphClearImageButton = document.getElementById(
		`${namespace}openGraphClearImageButton`
	);
	const openGraphImageInput = document.getElementById(
		`${namespace}openGraphImageInput`
	);
	const openGraphImageFileEntryERC = document.getElementById(
		`${namespace}openGraphImageFileEntryERC`
	);
	const openGraphImageFileEntryScopeERC = document.getElementById(
		`${namespace}openGraphImageFileEntryScopeERC`
	);
	const openGraphImageAltField = document.getElementById(
		`${namespace}openGraphImageAlt`
	);
	const openGraphImageAltFieldDefaultLocale = document.getElementById(
		`${namespace}openGraphImageAlt_${Liferay.ThemeDisplay.getDefaultLanguageId()}`
	);
	const openGraphImageAltLabel = document.querySelector(
		`[for="${namespace}openGraphImageAlt"]`
	);

	const openImageSelector = () => {
		openSelectionModal({
			onSelect: (selectedItem) => {
				if (selectedItem) {
					const itemValue = JSON.parse(selectedItem.value);

					openGraphImageFileEntryERC.value =
						itemValue.externalReferenceCode;

					let fileEntryScopeERC = '';

					if (
						String(itemValue.groupId) !==
						String(Liferay.ThemeDisplay.getScopeGroupId())
					) {
						fileEntryScopeERC =
							itemValue.groupExternalReferenceCode || '';
					}

					openGraphImageFileEntryScopeERC.value = fileEntryScopeERC;

					openGraphImageInput.value = itemValue.title;

					previewSeoFireChange(namespace, {
						type: 'imgUrl',
						value: itemValue.url,
					});

					toggleDisabled(
						[
							openGraphClearImageButton,
							openGraphImageAltField,
							openGraphImageAltFieldDefaultLocale,
							openGraphImageAltLabel,
						],
						false
					);
				}
			},
			selectEventName: `${namespace}openGraphImageSelectedItem`,
			title: Liferay.Language.get('select-image'),
			url: uploadOpenGraphImageURL,
		});
	};

	openGraphImageButton.addEventListener('click', openImageSelector);
	openGraphImageInput.addEventListener('click', openImageSelector);

	openGraphClearImageButton.addEventListener('click', () => {
		openGraphImageFileEntryERC.value = '';
		openGraphImageFileEntryScopeERC.value = '';

		openGraphImageInput.value = '';

		toggleDisabled(
			[
				openGraphClearImageButton,
				openGraphImageAltField,
				openGraphImageAltFieldDefaultLocale,
				openGraphImageAltLabel,
			],
			true
		);

		previewSeoFireChange(namespace, {
			type: 'imgUrl',
			value: '',
		});
	});

	const openGraphTitleEnabledCheck = document.getElementById(
		`${namespace}openGraphTitleEnabled`
	);
	const openGraphTitleField = document.getElementById(
		`${namespace}openGraphTitle`
	);
	const openGraphTitleFieldDefaultLocale = document.getElementById(
		`${namespace}openGraphTitle_${Liferay.ThemeDisplay.getLanguageId()}`
	);
	const openGraphTitleWrapper = document.getElementById(
		`${namespace}openGraphTitleWrapper`
	);

	openGraphTitleEnabledCheck.addEventListener('click', (event) => {
		const disabled = !event.target.checked;

		const label = openGraphTitleWrapper.querySelector('label');

		toggleDisabled(
			[openGraphTitleField, openGraphTitleFieldDefaultLocale, label],
			disabled
		);

		previewSeoFireChange(namespace, {
			disabled,
			type: 'title',
			value: openGraphTitleField.value,
		});
	});

	const openGraphDescriptionEnabledCheck = document.getElementById(
		`${namespace}openGraphDescriptionEnabled`
	);
	const openGraphDescriptionField = document.getElementById(
		`${namespace}openGraphDescription`
	);
	const openGraphDescriptionFieldDefaultLocale = document.getElementById(
		`${namespace}openGraphDescription_${Liferay.ThemeDisplay.getLanguageId()}`
	);
	const openGraphDescriptionWrapper = document.getElementById(
		`${namespace}openGraphDescriptionWrapper`
	);

	openGraphDescriptionEnabledCheck.addEventListener('click', (event) => {
		const disabled = !event.target.checked;

		const label = openGraphDescriptionWrapper.querySelector('label');

		toggleDisabled(
			[
				openGraphDescriptionField,
				openGraphDescriptionFieldDefaultLocale,
				label,
			],
			disabled
		);

		previewSeoFireChange(namespace, {
			disabled,
			type: 'description',
			value: openGraphDescriptionField.value,
		});
	});
}
