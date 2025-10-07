/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getSpritemap} from '@liferay/frontend-icons-web';
import {openSelectionModal} from 'frontend-js-components-web';

export function EditAssetCategoryCPDisplayLayout({
	assetCategory,
	categorySelectorUrl,
	itemSelectorUrl,
	portletNamespace,
	title,
}) {
	const displayPageItemRemove = document.getElementById(
		`${portletNamespace}displayPageItemRemove`
	);

	const displayPageNameInput = document.getElementById(
		`${portletNamespace}displayPageNameInput`
	);

	const pagesContainerInput = document.getElementById(
		`${portletNamespace}pagesContainerInput`
	);

	const chooseDisplayPageButton = document.getElementById(
		`${portletNamespace}chooseDisplayPage`
	);

	chooseDisplayPageButton?.addEventListener('click', () => {
		openSelectionModal({
			onSelect: (selectedItem) => {
				if (
					selectedItem &&
					displayPageNameInput &&
					pagesContainerInput
				) {
					pagesContainerInput.value = selectedItem.id;

					displayPageNameInput.innerHTML = selectedItem.name;

					displayPageItemRemove?.classList.remove('hide');
				}
			},
			selectEventName: 'selectDisplayPage',
			title: Liferay.Language.get('select-category-display-page'),
			url: itemSelectorUrl,
		});
	});

	if (displayPageItemRemove && displayPageNameInput && pagesContainerInput) {
		displayPageItemRemove.addEventListener('click', () => {
			displayPageNameInput.innerHTML = Liferay.Language.get('none');

			pagesContainerInput.value = '';

			displayPageItemRemove.classList.add('hide');
		});
	}

	function createLabel(id, title) {
		const labelContainer = document.createElement('span');

		labelContainer.classList.add(
			'label',
			'label-dismissible',
			'label-secondary'
		);
		labelContainer.setAttribute('id', id);

		const linkContainer = document.createElement('span');

		linkContainer.classList.add('label-item', 'label-item-expand');

		const link = document.createElement('a');

		link.setAttribute('href', '#' + id);
		link.innerText = title;
		linkContainer.appendChild(link);

		const buttonContainer = document.createElement('span');

		buttonContainer.classList.add('label-item', 'label-item-after');

		const button = document.createElement('button');

		button.classList.add('close');
		button.setAttribute('aria-label', 'Close');
		button.setAttribute('type', 'button');
		button.addEventListener('click', handleCloseButtonClick);

		const svg = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'svg'
		);

		svg.classList.add('lexicon-icon', 'lexicon-icon-times');
		svg.setAttribute('focusable', false);
		svg.setAttribute('role', 'presentation');

		const use = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'use'
		);

		use.setAttribute('href', `${getSpritemap()}#times`);

		svg.appendChild(use);
		button.appendChild(svg);
		buttonContainer.appendChild(button);

		labelContainer.appendChild(linkContainer);
		labelContainer.appendChild(buttonContainer);

		return labelContainer;
	}

	const categoriesContainer = document.getElementById(
		`${portletNamespace}categoriesContainer`
	);

	const classPK = document.getElementById(`${portletNamespace}classPK`);

	const assetCategoryId = assetCategory?.categoryId;

	if (assetCategoryId) {
		const categoryNode = createLabel(`category-${assetCategoryId}`, title);

		categoriesContainer?.appendChild(categoryNode);
		classPK.value = assetCategoryId;
	}

	const selectCategoriesButton = document.getElementById(
		`${portletNamespace}selectCategories`
	);

	selectCategoriesButton?.addEventListener('click', () => {
		openSelectionModal({
			buttonAddLabel: Liferay.Language.get('done'),
			multiple: true,
			onSelect: (selectedItems) => {
				if (selectedItems) {
					Object.keys(selectedItems).forEach((key) => {
						const item = selectedItems[key];

						if (item.unchecked && !categoriesContainer) {
							return;
						}

						const categoryNodes = categoriesContainer.children;

						if (categoryNodes.length) {
							for (let i = 0; i < categoryNodes.length; i++) {
								categoriesContainer.removeChild(
									categoryNodes.item(i)
								);
							}
						}

						delete selectedItems[key];

						const categoryNode = createLabel(
							`category-${item.categoryId}`,
							item.value
						);

						categoriesContainer.appendChild(categoryNode);
						classPK.value = item.categoryId;

						return;
					});
				}
			},
			selectEventName: `${portletNamespace}selectCategory`,
			title: Liferay.Language.get('select-category-display-page'),
			url: categorySelectorUrl,
		});
	});

	function handleCloseButtonClick(event) {
		const button = event.currentTarget;

		categoriesContainer?.removeChild(button.parentElement.parentElement);

		classPK.value = 0;
	}
}
