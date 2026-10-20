/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {delegate, sub} from 'frontend-js-web';

export default function ({
	layoutItemSelectorUrl,
	layoutPageTemplateEntryItemSelectorUrl,
	portletNamespace,
	productItemSelectorUrl,
	removeIcon,
	searchContainerId,
}) {
	const initProductSelection = (searchContainer) => {
		const selectProductButton = document.getElementById(
			`${portletNamespace}selectProduct`
		);

		if (!selectProductButton) {
			return;
		}

		selectProductButton.addEventListener('click', (event) => {
			event.preventDefault();

			openSelectionModal({
				onSelect: (selectedItem) => {
					if (!selectedItem) {
						return;
					}

					const searchContainerData = searchContainer.getData();

					const link = document.querySelector(
						`a[data-rowid="${searchContainerData}"]`
					);

					if (link) {
						const row = link.closest('tr');

						if (row) {
							searchContainer.deleteRow(row, link.dataset.rowid);
						}
					}

					const rowColumns = [];

					rowColumns.push(selectedItem.name);
					rowColumns.push(
						`<a class="float-right modify-link" data-rowId="${selectedItem.id}" href="javascript:void(0);">${removeIcon}</a>`
					);

					const classPKInput = document.getElementById(
						`${portletNamespace}classPK`
					);

					if (classPKInput) {
						classPKInput.value = selectedItem.id;
					}

					searchContainer.addRow(rowColumns, selectedItem.id);

					searchContainer.updateDataStore();
				},
				selectEventName: 'productDefinitionsSelectItem',
				title: sub(
					Liferay.Language.get('select-x'),
					Liferay.Language.get('product')
				),
				url: productItemSelectorUrl,
			});
		});

		const searchContainerContentBox = searchContainer.get('contentBox');

		delegate(
			searchContainerContentBox.getDOMNode(),
			'click',
			'.modify-link',
			(event) => {
				const link = event.delegateTarget;

				const row = link.closest('tr');

				searchContainer.deleteRow(row, link.dataset.rowid);

				const siteGroupInput = document.getElementById(
					`${portletNamespace}classPK`
				);

				if (siteGroupInput) {
					siteGroupInput.value = 0;
				}
			}
		);
	};

	Liferay.componentReady(`${portletNamespace}${searchContainerId}`).then(
		(searchContainer) => {
			initProductSelection(searchContainer);
		}
	);

	const initDisplayPageSelection = () => {
		const chooseLayoutButton = document.getElementById(
			`${portletNamespace}chooseLayout`
		);
		const chooseLayoutPageTemplateEntryButton = document.getElementById(
			`${portletNamespace}chooseLayoutPageTemplateEntry`
		);
		const displayPageItemRemoveIcon = document.getElementById(
			`${portletNamespace}displayPageItemRemove`
		);
		const pagesContainerInput = document.getElementById(
			`${portletNamespace}pagesContainerInput`
		);
		const displayPageNameInput = document.getElementById(
			`${portletNamespace}displayPageNameInput`
		);

		if (
			(!chooseLayoutButton && !chooseLayoutPageTemplateEntryButton) ||
			!displayPageItemRemoveIcon ||
			!pagesContainerInput ||
			!displayPageNameInput
		) {
			return;
		}

		if (chooseLayoutButton) {
			chooseLayoutButton.addEventListener('click', () => {
				openSelectionModal({
					buttonAddLabel: Liferay.Language.get('done'),
					multiple: true,
					onSelect: (selectedItem) => {
						if (!selectedItem) {
							return;
						}

						pagesContainerInput.value = selectedItem.id;

						displayPageNameInput.innerHTML = selectedItem.name;

						displayPageItemRemoveIcon.classList.remove('hide');
					},
					selectEventName: 'selectLayout',
					title: Liferay.Language.get('select-product-display-page'),
					url: layoutItemSelectorUrl,
				});
			});
		}

		if (chooseLayoutPageTemplateEntryButton) {
			chooseLayoutPageTemplateEntryButton.addEventListener(
				'click',
				() => {
					openSelectionModal({
						buttonAddLabel: Liferay.Language.get('done'),
						onSelect: (selectedItem) => {
							if (!selectedItem) {
								return;
							}

							const itemValue = JSON.parse(selectedItem.value);

							pagesContainerInput.value = itemValue.uuid;

							displayPageNameInput.innerHTML = itemValue.name;

							displayPageItemRemoveIcon.classList.remove('hide');
						},
						selectEventName: 'selectLayoutPageTemplateEntry',
						title: Liferay.Language.get(
							'select-product-display-page'
						),
						url: layoutPageTemplateEntryItemSelectorUrl,
					});
				}
			);
		}

		displayPageItemRemoveIcon.addEventListener('click', () => {
			displayPageNameInput.innerHTML = Liferay.Language.get('none');

			pagesContainerInput.value = '';

			displayPageItemRemoveIcon.classList.add('hide');
		});
	};

	initDisplayPageSelection();
}
