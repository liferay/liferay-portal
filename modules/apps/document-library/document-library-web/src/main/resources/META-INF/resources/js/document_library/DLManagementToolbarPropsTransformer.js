/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openCategorySelectionModal,
	openConfirmModal,
	openSelectionModal,
	openTagSelectionModal,
	openToast,
} from 'frontend-js-components-web';
import {addParams, createPortletURL, navigate, sub} from 'frontend-js-web';

import {collectDigitalSignature} from './digital-signature/DigitalSignatureUtil';

export default function propsTransformer({
	additionalProps: {
		addFileEntryURL,
		bulkCopyURL,
		bulkPermissionsConfiguration: {defaultModelClassName, permissionsURLs},
		collectDigitalSignaturePortlet,
		downloadEntryURL,
		editEntryURL,
		folderConfiguration,
		openViewMoreFileEntryTypesURL,
		redirect,
		selectAssetCategoriesURL,
		selectAssetTagsURL,
		selectExtensionURL,
		selectFileEntryTypeURL,
		selectFolderURL,
		trashEnabled,
		viewFileEntryTypeURL,
	},
	portletNamespace,
	...otherProps
}) {
	const getAllSelectedElements = () => {
		const searchContainer = Liferay.SearchContainer.get(
			otherProps.searchContainerId
		);

		return searchContainer.select.getAllSelectedElements();
	};

	const processAction = (action, url) => {
		if (!action) {
			return;
		}

		const form = document.getElementById(`${portletNamespace}fm2`);

		if (!form) {
			return;
		}

		form.setAttribute('method', 'post');

		const actionInputElement = form.querySelector(
			`#${portletNamespace}jakarta-portlet-action`
		);

		if (actionInputElement) {
			actionInputElement.setAttribute('value', action);
		}

		const commandInputElement = form.querySelector(
			`#${portletNamespace}cmd`
		);

		if (commandInputElement) {
			commandInputElement.setAttribute('value', action);
		}

		submitForm(form, url, false);
	};

	const checkIn = () => {
		Liferay.componentReady(
			`${portletNamespace}DocumentLibraryCheckinModal`
		).then((documentLibraryCheckinModal) => {
			documentLibraryCheckinModal.open((versionIncrease, changeLog) => {
				const form = document.getElementById(`${portletNamespace}fm2`);

				if (!form) {
					return;
				}

				const changeLogInput = form.querySelector(
					`#${portletNamespace}changeLog`
				);

				if (changeLogInput) {
					changeLogInput.setAttribute('value', changeLog);
				}

				const versionIncreaseInput = form.querySelector(
					`#${portletNamespace}versionIncrease`
				);

				if (versionIncreaseInput) {
					versionIncreaseInput.setAttribute('value', versionIncrease);
				}

				processAction('checkin', editEntryURL);
			});
		});
	};

	const copy = () => {
		const dlObjectIds = getAllSelectedElements().get('value');

		const url = addParams(
			`${portletNamespace}dlObjectIds=${dlObjectIds.join(',')}`,
			bulkCopyURL
		);

		navigate(url);
	};

	const deleteEntries = () => {
		if (trashEnabled) {
			processAction('move_to_trash', editEntryURL);
		}
		else {
			openConfirmModal({
				message: Liferay.Language.get(
					'are-you-sure-you-want-to-delete-the-selected-entries'
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						processAction('delete', editEntryURL);
					}
				},
			});
		}
	};

	const editCategories = () => {
		const searchContainer = Liferay.SearchContainer.get(
			otherProps.searchContainerId
		);

		Liferay.componentReady(
			`${portletNamespace}EditCategoriesComponent`
		).then((editCategoriesComponent) => {
			const bulkSelection = searchContainer.select?.get('bulkSelection');

			const selectedFileEntries = searchContainer.select
				.getAllSelectedElements()
				.get('value');

			editCategoriesComponent.open(
				selectedFileEntries,
				bulkSelection,
				folderConfiguration.defaultParentFolderId
			);
		});
	};

	const editTags = () => {
		const searchContainer = Liferay.SearchContainer.get(
			otherProps.searchContainerId
		);

		Liferay.componentReady(`${portletNamespace}EditTagsComponent`).then(
			(editTagsComponent) => {
				const bulkSelection =
					searchContainer.select?.get('bulkSelection');

				const selectedFileEntries = searchContainer.select
					.getAllSelectedElements()
					.get('value');

				editTagsComponent.open(
					selectedFileEntries,
					bulkSelection,
					folderConfiguration.defaultParentFolderId
				);
			}
		);
	};

	const filterByCategory = (categoriesFilterURL) => {
		openCategorySelectionModal({
			portletNamespace,
			redirectURL: selectAssetCategoriesURL,
			selectCategoryURL: categoriesFilterURL,
		});
	};

	const filterByDocumentType = () => {
		openSelectionModal({
			onSelect(selectedItem) {
				if (selectedItem) {
					const destinationURL = new URL(viewFileEntryTypeURL);

					const resetCurParam = `_${destinationURL.searchParams.get(
						'p_p_id'
					)}_resetCur`;

					destinationURL.searchParams.set(resetCurParam, 'true');

					const url = addParams(
						`${portletNamespace}fileEntryTypeId=${selectedItem.value}`,
						destinationURL.href
					);
					navigate(url);
				}
			},
			selectEventName: `${portletNamespace}selectFileEntryType`,
			title: Liferay.Language.get('filter-by-type'),
			url: selectFileEntryTypeURL,
		});
	};

	const filterByExtension = (extensionsFilterURL) => {
		openSelectionModal({
			buttonAddLabel: Liferay.Language.get('apply'),
			height: '70vh',
			multiple: true,
			onSelect(selectedItem) {
				if (selectedItem) {
					const url = selectedItem.reduce(
						(acc, item) =>
							addParams(
								`${portletNamespace}extension=${item}`,
								acc
							),
						selectExtensionURL
					);

					const destinationUrl = new URL(url);

					const resetCurParam = `_${destinationUrl.searchParams.get(
						'p_p_id'
					)}_resetCur`;

					destinationUrl.searchParams.set(resetCurParam, 'true');

					navigate(destinationUrl.href);
				}
			},
			selectEventName: `${portletNamespace}selectedFileExtension`,
			size: 'md',
			title: Liferay.Language.get('filter-by-extension'),
			url: extensionsFilterURL,
		});
	};

	const filterByTag = (tagsFilterURL) => {
		openTagSelectionModal({
			portletNamespace,
			redirectURL: selectAssetTagsURL,
			selectTagURL: tagsFilterURL,
		});
	};

	const move = () => {
		const searchContainer = Liferay.SearchContainer.get(
			otherProps.searchContainerId
		);

		let selectedItems = 0;

		if (searchContainer.select) {
			selectedItems = searchContainer.select
				.getAllSelectedElements()
				.filter(':enabled')
				.size();
		}

		const dialogTitle =
			selectedItems === 1
				? Liferay.Language.get('select-destination-folder-for-x-item')
				: Liferay.Language.get('select-destination-folder-for-x-items');

		openSelectionModal({
			height: '480px',
			id: `${portletNamespace}selectFolder`,
			onSelect(selectedItem) {
				const newFolderId = selectedItem.resourceid;

				const form = document.getElementById(`${portletNamespace}fm2`);

				if (!form) {
					return;
				}

				form.setAttribute('action', editEntryURL);
				form.setAttribute('enctype', 'multipart/form-data');
				form.setAttribute('method', 'post');

				const cmdInput = form.querySelector(`#${portletNamespace}cmd`);

				if (cmdInput) {
					cmdInput.setAttribute('value', 'move');
				}

				const newFolderIdInput = form.querySelector(
					`#${portletNamespace}newFolderId`
				);

				if (newFolderIdInput) {
					newFolderIdInput.setAttribute('value', newFolderId);
				}

				submitForm(form, editEntryURL, false);
			},
			selectEventName: `${portletNamespace}folderSelected`,
			size: 'lg',
			title: sub(dialogTitle, [selectedItems]),
			url: selectFolderURL,
		});
	};

	const openCreateAIImage = (aiImageCreatorURL, isAICreatorOpenAIAPIKey) => {
		if (!isAICreatorOpenAIAPIKey) {
			Liferay.componentReady(`${portletNamespace}ConfigueAIModal`).then(
				(configureAIModal) => {
					configureAIModal.open();
				}
			);
		}
		else {
			openSelectionModal({
				height: '70vh',
				onSelect: ({selectedItems}) => {
					if (selectedItems) {
						openToast({
							message: sub(
								Liferay.Language.get(
									'x-files-were-successfully-added'
								),
								[`<strong>${selectedItems.length}</strong>`]
							),
							title: Liferay.Language.get('success'),
							type: 'success',
						});

						navigate(redirect);
					}
				},
				selectEventName: `${portletNamespace}selectAIImages`,
				size: 'lg',
				title: Liferay.Language.get('create-ai-image'),
				url: createPortletURL(aiImageCreatorURL, {
					selectEventName: `${portletNamespace}selectAIImages`,
				}).toString(),
			});
		}
	};

	const permissions = () => {
		const map = new Map();

		getAllSelectedElements().each((element) => {
			const modelClassName =
				element.getData('modelclassname') ?? defaultModelClassName;

			map.set(modelClassName, [
				...(map.get(modelClassName) ?? []),
				element.get('value'),
			]);
		});

		if (map.size > 1) {
			openToast({
				message: Liferay.Language.get(
					'it-is-not-possible-to-simultaneously-change-the-permissions-of-different-asset-types'
				),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});

			return;
		}

		const [selectedModelClassName, selectedFileEntries] = map
			.entries()
			?.next().value;

		const permissionsURL = permissionsURLs[selectedModelClassName];

		const url = new URL(permissionsURL);

		openSelectionModal({
			title: Liferay.Language.get('permissions'),
			url: addParams(
				{
					[`_${url.searchParams.get('p_p_id')}_resourcePrimKey`]:
						selectedFileEntries.join(','),
				},
				permissionsURL
			),
		});
	};

	return {
		...otherProps,
		onActionButtonClick(event, {item}) {
			const action = item?.data?.action;

			if (action === 'checkin') {
				checkIn();
			}
			else if (action === 'checkout') {
				processAction('checkout', editEntryURL);
			}
			else if (action === 'collectDigitalSignature') {
				collectDigitalSignature(
					getAllSelectedElements().get('value'),
					collectDigitalSignaturePortlet
				);
			}
			else if (action === 'copy') {
				copy();
			}
			else if (action === 'deleteEntries') {
				deleteEntries();
			}
			else if (action === 'download') {
				processAction('download', downloadEntryURL);
			}
			else if (action === 'editCategories') {
				editCategories();
			}
			else if (action === 'editTags') {
				editTags();
			}
			else if (action === 'move') {
				move();
			}
			else if (action === 'permissions') {
				permissions();
			}
		},
		onCreationMenuItemClick: (event, {item}) => {
			if (item?.data?.action === 'openAICreateImage') {
				openCreateAIImage(
					item?.data?.aiCreatorURL,
					item?.data?.isAICreatorOpenAIAPIKey
				);
			}
		},
		onFilterDropdownItemClick(event, {item}) {
			if (item?.data?.action === 'openCategoriesSelector') {
				filterByCategory(item?.data?.categoriesFilterURL);
			}
			else if (item?.data?.action === 'openDocumentTypesSelector') {
				filterByDocumentType();
			}
			else if (item?.data?.action === 'openExtensionSelector') {
				filterByExtension(item?.data?.extensionsFilterURL);
			}
			else if (item?.data?.action === 'openTagsSelector') {
				filterByTag(item?.data?.tagsFilterURL);
			}
		},
		onShowMoreButtonClick() {
			openSelectionModal({
				onSelect(selectedItem) {
					if (selectedItem) {
						const url = addParams(
							`${portletNamespace}fileEntryTypeId=${selectedItem.fileentrytypeid}`,
							addFileEntryURL
						);
						navigate(url);
					}
				},
				selectEventName: `${portletNamespace}selectFileEntryType`,
				title: Liferay.Language.get('more'),
				url: openViewMoreFileEntryTypesURL,
			});
		},
	};
}
