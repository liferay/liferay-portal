/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function ({portletNamespace}) {
	document
		.getElementById(`${portletNamespace}commerceCatalogGroupId`)
		.addEventListener('change', (event) => {
			const languageId = event.target.querySelector(
				'[value="' + event.target.value + '"]'
			).dataset.languageid;

			const nameInput = document.getElementById(
				`${portletNamespace}nameMapAsXML`
			);
			const shortDescriptionInput = document.getElementById(
				`${portletNamespace}shortDescriptionMapAsXML`
			);

			const descriptionInput =
				window[`${portletNamespace}descriptionMapAsXMLEditor`];

			const urlInput = document.getElementById(
				`${portletNamespace}urlTitleMapAsXML`
			);
			const metaTitleInput = document.getElementById(
				`${portletNamespace}metaTitleMapAsXML`
			);
			const metaDescriptionInput = document.getElementById(
				`${portletNamespace}metaDescriptionMapAsXML`
			);
			const metaKeywordsInput = document.getElementById(
				`${portletNamespace}metaKeywordsMapAsXML`
			);

			const nameInputLocalized = Liferay.component(
				`${portletNamespace}nameMapAsXML`
			);
			const shortDescriptionInputLocalized = Liferay.component(
				`${portletNamespace}shortDescriptionMapAsXML`
			);
			const descriptionInputLocalized = Liferay.component(
				`${portletNamespace}descriptionMapAsXML`
			);
			const urlTitleInputLocalized = Liferay.component(
				`${portletNamespace}urlTitleMapAsXML`
			);
			const metaTitleInputLocalized = Liferay.component(
				`${portletNamespace}metaTitleMapAsXML`
			);
			const metaDescriptionInputLocalized = Liferay.component(
				`${portletNamespace}metaDescriptionMapAsXML`
			);
			const metaKeywordsInputLocalized = Liferay.component(
				`${portletNamespace}metaKeywordsMapAsXML`
			);

			nameInputLocalized.updateInputLanguage(nameInput.value, languageId);
			shortDescriptionInputLocalized.updateInputLanguage(
				shortDescriptionInput.value,
				languageId
			);
			descriptionInputLocalized.updateInputLanguage(
				descriptionInput.getHTML(),
				languageId
			);
			urlTitleInputLocalized.updateInputLanguage(
				urlInput.value,
				languageId
			);
			metaTitleInputLocalized.updateInputLanguage(
				metaTitleInput.value,
				languageId
			);
			metaDescriptionInputLocalized.updateInputLanguage(
				metaDescriptionInput.value,
				languageId
			);
			metaKeywordsInputLocalized.updateInputLanguage(
				metaKeywordsInput.value,
				languageId
			);

			nameInputLocalized.selectFlag(languageId, false);
			shortDescriptionInputLocalized.selectFlag(languageId, false);
			descriptionInputLocalized.selectFlag(languageId, false);
			urlTitleInputLocalized.selectFlag(languageId, false);
			metaTitleInputLocalized.selectFlag(languageId, false);
			metaDescriptionInputLocalized.selectFlag(languageId, false);
			metaKeywordsInputLocalized.selectFlag(languageId, false);
		});
}
