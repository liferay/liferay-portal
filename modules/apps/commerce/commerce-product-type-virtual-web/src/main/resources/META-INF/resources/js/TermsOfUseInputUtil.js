/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function ({
	portletNamespace,
	termsOfUseJournalArticleBrowserURL,
}) {
	Liferay.Util.toggleBoxes(
		`${portletNamespace}termsOfUseRequired`,
		`${portletNamespace}termsOfUseSettings`
	);

	const journalArticleNameInput = document.getElementById(
		`${portletNamespace}journalArticleNameInput`
	);

	const journalArticleRemove = document.getElementById(
		`${portletNamespace}journalArticleRemove`
	);

	const selectArticle = document.getElementById(
		`${portletNamespace}selectArticle`
	);

	if (journalArticleNameInput && journalArticleRemove && selectArticle) {
		selectArticle.addEventListener('click', (event) => {
			event.preventDefault();

			Liferay.Util.openSelectionModal({
				onSelect: (selectedItem) => {
					const termsOfUseJournalArticleResourcePrimKey =
						document.getElementById(
							`${portletNamespace}termsOfUseJournalArticleResourcePrimKey`
						);

					const itemValue = JSON.parse(selectedItem.value);

					if (termsOfUseJournalArticleResourcePrimKey) {
						termsOfUseJournalArticleResourcePrimKey.value =
							itemValue.classPK;
					}

					journalArticleRemove.classList.remove('hide');

					journalArticleNameInput.innerText = itemValue.title;
				},
				selectEventName: 'selectJournalArticle',
				title: Liferay.Language.get('select-web-content'),
				url: termsOfUseJournalArticleBrowserURL,
			});
		});

		journalArticleRemove.addEventListener('click', (event) => {
			event.preventDefault();

			const termsOfUseJournalArticleResourcePrimKey =
				document.getElementById(
					`${portletNamespace}termsOfUseJournalArticleResourcePrimKey`
				);

			if (termsOfUseJournalArticleResourcePrimKey) {
				termsOfUseJournalArticleResourcePrimKey.value = 0;
			}

			journalArticleNameInput.innerText = Liferay.Language.get('none');

			journalArticleRemove.classList.add('hide');
		});
	}
}
