/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {
	addParams,
	debounce,
	delegate,
	getFormElement,
	toggleDisabled,
	toggleSelectBox,
} from 'frontend-js-web';

export function NavigationMenuConfiguration({
	itemSelectorNamespace,
	namespace,
	portletResource,
	rootMenuItemEventName,
	rootMenuItemSelectorURL,
	siteNavigationMenuEventName,
	siteNavigationMenuItemSelectorURL,
}) {
	const form = document.getElementById(`${namespace}fm`);

	const resetPreview = (option) => {
		const displayDepthSelect = getFormElement(form, 'displayDepth');
		const displayStyle = document.getElementById(
			`${namespace}preferences--displayStyle--`
		);
		const expandedLevelsSelect = getFormElement(form, 'expandedLevels');
		const rootMenuItemExternalReferenceCodeInput = getFormElement(
			form,
			'rootMenuItemExternalReferenceCode'
		);
		const rootMenuItemLevelSelect = getFormElement(
			form,
			'rootMenuItemLevel'
		);
		const rootMenuItemTypeSelect = getFormElement(form, 'rootMenuItemType');
		const siteNavigationMenuExternalReferenceCodeInput = getFormElement(
			form,
			'siteNavigationMenuExternalReferenceCode'
		);
		const siteNavigationMenuGroupExternalReferenceCodeInput =
			getFormElement(
				form,
				'siteNavigationMenuGroupExternalReferenceCode'
			);
		const siteNavigationMenuTypeInput = getFormElement(
			form,
			'siteNavigationMenuType'
		);

		let displayStyleValue = null;

		if (typeof option === 'string' || option instanceof String) {
			displayStyleValue = option;
		}
		else {
			displayStyleValue = displayStyle.value;
		}

		let data = {
			preview: true,
		};

		if (
			displayDepthSelect &&
			displayStyle &&
			expandedLevelsSelect &&
			rootMenuItemExternalReferenceCodeInput &&
			rootMenuItemLevelSelect &&
			rootMenuItemTypeSelect &&
			siteNavigationMenuExternalReferenceCodeInput &&
			siteNavigationMenuGroupExternalReferenceCodeInput &&
			siteNavigationMenuTypeInput
		) {
			data.displayDepth = displayDepthSelect.value;
			data.displayStyle = displayStyleValue;
			data.expandedLevels = expandedLevelsSelect.value;
			data.rootMenuItemExternalReferenceCode =
				rootMenuItemExternalReferenceCodeInput.value;
			data.rootMenuItemLevel = rootMenuItemLevelSelect.value;
			data.rootMenuItemType = rootMenuItemTypeSelect.value;
			data.siteNavigationMenuExternalReferenceCode =
				siteNavigationMenuExternalReferenceCodeInput.value;
			data.siteNavigationMenuGroupExternalReferenceCode =
				siteNavigationMenuGroupExternalReferenceCodeInput.value;
			data.siteNavigationMenuType = siteNavigationMenuTypeInput.value;
		}

		data = Liferay.Util.ns(`_${portletResource}_`, data);

		Liferay.Portlet.refresh(`#p_p_id_${portletResource}_`, data);
	};

	const debouncedResetPreview = debounce(resetPreview, 200);

	form.addEventListener('change', debouncedResetPreview);
	form.addEventListener('select', debouncedResetPreview);

	const chooseRootMenuItemButton = document.getElementById(
		`${namespace}chooseRootMenuItem`
	);
	const rootMenuItemExternalReferenceCodeInput = document.getElementById(
		`${namespace}rootMenuItemExternalReferenceCode`
	);
	const rootMenuItemNameSpan = document.getElementById(
		`${namespace}rootMenuItemName`
	);
	const selectSiteNavigationMenuTypeSelect = document.getElementById(
		`${namespace}selectSiteNavigationMenuType`
	);
	const siteNavigationMenuExternalReferenceCodeInput =
		document.getElementById(
			`${namespace}siteNavigationMenuExternalReferenceCode`
		);
	const siteNavigationMenuGroupExternalReferenceCodeInput =
		document.getElementById(
			`${namespace}siteNavigationMenuGroupExternalReferenceCode`
		);

	if (
		chooseRootMenuItemButton &&
		rootMenuItemExternalReferenceCodeInput &&
		rootMenuItemNameSpan &&
		selectSiteNavigationMenuTypeSelect &&
		siteNavigationMenuExternalReferenceCodeInput &&
		siteNavigationMenuGroupExternalReferenceCodeInput
	) {
		chooseRootMenuItemButton.addEventListener('click', (event) => {
			event.preventDefault();

			let uri = rootMenuItemSelectorURL;

			uri = addParams(
				`${itemSelectorNamespace}siteNavigationMenuType=${selectSiteNavigationMenuTypeSelect.value}`,
				uri
			);
			uri = addParams(
				`${itemSelectorNamespace}siteNavigationMenuExternalReferenceCode=${encodeURIComponent(siteNavigationMenuExternalReferenceCodeInput.value)}`,
				uri
			);
			uri = addParams(
				`${itemSelectorNamespace}siteNavigationMenuGroupExternalReferenceCode=${encodeURIComponent(siteNavigationMenuGroupExternalReferenceCodeInput.value)}`,
				uri
			);

			openSelectionModal({
				height: '70vh',
				onSelect(selectedItem) {
					if (selectedItem) {
						rootMenuItemExternalReferenceCodeInput.value =
							selectedItem.selectSiteNavigationMenuItemExternalReferenceCode;
						rootMenuItemNameSpan.innerText =
							selectedItem.selectSiteNavigationMenuItemName;

						debouncedResetPreview();
					}
				},
				selectEventName: rootMenuItemEventName,
				size: 'md',
				title: Liferay.Language.get('select-site-navigation-menu-item'),
				url: uri,
			});
		});

		const chooseSiteNavigationMenuButton = document.getElementById(
			`${namespace}chooseSiteNavigationMenu`
		);
		const navigationMenuName = document.getElementById(
			`${namespace}navigationMenuName`
		);
		const removeSiteNavigationMenu = document.getElementById(
			`${namespace}removeSiteNavigationMenu`
		);

		if (
			chooseSiteNavigationMenuButton &&
			navigationMenuName &&
			removeSiteNavigationMenu &&
			rootMenuItemExternalReferenceCodeInput &&
			rootMenuItemNameSpan &&
			siteNavigationMenuExternalReferenceCodeInput &&
			siteNavigationMenuGroupExternalReferenceCodeInput
		) {
			chooseSiteNavigationMenuButton.addEventListener('click', () => {
				openSelectionModal({
					id: `${namespace}selectSiteNavigationMenu`,
					onSelect(selectedItem) {
						const itemValue = JSON.parse(selectedItem.value);

						if (itemValue) {
							navigationMenuName.innerText = itemValue.name;
							rootMenuItemExternalReferenceCodeInput.value = '';
							rootMenuItemNameSpan.innerText = itemValue.name;
							siteNavigationMenuExternalReferenceCodeInput.value =
								itemValue.externalReferenceCode;
							siteNavigationMenuGroupExternalReferenceCodeInput.value =
								itemValue.groupExternalReferenceCode;

							removeSiteNavigationMenu.classList.toggle('hide');

							debouncedResetPreview();
						}
					},
					selectEventName: siteNavigationMenuEventName,
					title: Liferay.Language.get('select-site-navigation-menu'),
					url: siteNavigationMenuItemSelectorURL,
				});
			});
		}

		const removeSiteNavigationMenuButton = document.getElementById(
			`${namespace}removeSiteNavigationMenu`
		);

		if (
			navigationMenuName &&
			removeSiteNavigationMenu &&
			removeSiteNavigationMenuButton &&
			rootMenuItemExternalReferenceCodeInput &&
			rootMenuItemNameSpan &&
			siteNavigationMenuExternalReferenceCodeInput &&
			siteNavigationMenuGroupExternalReferenceCodeInput
		) {
			removeSiteNavigationMenuButton.addEventListener('click', () => {
				navigationMenuName.innerText = '';
				rootMenuItemExternalReferenceCodeInput.value = '';
				rootMenuItemNameSpan.innerText = '';
				siteNavigationMenuExternalReferenceCodeInput.value = '';
				siteNavigationMenuGroupExternalReferenceCodeInput.value = '';

				removeSiteNavigationMenu.classList.toggle('hide');

				debouncedResetPreview();
			});
		}

		toggleSelectBox(
			`${namespace}rootMenuItemType`,
			'select',
			`${namespace}rootMenuItemExternalReferenceCodePanel`
		);

		toggleSelectBox(
			`${namespace}rootMenuItemType`,
			(currentValue) => {
				return (
					currentValue === 'absolute' || currentValue === 'relative'
				);
			},
			`${namespace}rootMenuItemLevel`
		);

		const siteNavigationMenuType = document.getElementById(
			`${namespace}siteNavigationMenuType`
		);

		if (
			rootMenuItemNameSpan &&
			selectSiteNavigationMenuTypeSelect &&
			siteNavigationMenuType
		) {
			selectSiteNavigationMenuTypeSelect.addEventListener(
				'change',
				() => {
					const selectedSelectSiteNavigationMenuType =
						document.querySelector(
							`${namespace}selectSiteNavigationMenuType option:checked`
						);

					if (selectedSelectSiteNavigationMenuType) {
						rootMenuItemNameSpan.innerText =
							selectedSelectSiteNavigationMenuType.innerText;
					}

					siteNavigationMenuType.value =
						selectSiteNavigationMenuTypeSelect.value;
				}
			);
		}

		const chooseSiteNavigationMenu = document.getElementById(
			`${namespace}chooseSiteNavigationMenu`
		);

		if (
			chooseSiteNavigationMenu &&
			navigationMenuName &&
			removeSiteNavigationMenu &&
			siteNavigationMenuExternalReferenceCodeInput &&
			siteNavigationMenuGroupExternalReferenceCodeInput &&
			siteNavigationMenuType
		) {
			delegate(
				document.getElementById(`${namespace}fm`),
				'change',
				'.select-navigation',
				() => {
					const siteNavigationDisabled =
						selectSiteNavigationMenuTypeSelect.disabled;

					toggleDisabled(
						chooseSiteNavigationMenu,
						siteNavigationDisabled
					);
					toggleDisabled(
						selectSiteNavigationMenuTypeSelect,
						!siteNavigationDisabled
					);

					navigationMenuName.innerText = '';
					siteNavigationMenuExternalReferenceCodeInput.value = '';
					siteNavigationMenuGroupExternalReferenceCodeInput.value =
						'';
					siteNavigationMenuType.value = -1;

					removeSiteNavigationMenu.classList.add('hide');

					debouncedResetPreview();
				}
			);
		}
	}

	Liferay.on('templateSelector:changedTemplate', (event) => {
		debouncedResetPreview(event.value);
	});
}
