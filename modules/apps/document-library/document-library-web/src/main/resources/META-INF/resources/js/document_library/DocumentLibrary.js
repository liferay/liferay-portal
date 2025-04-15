/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {buildFragment, sub} from 'frontend-js-web';

const HTML5_UPLOAD =
	window && window.File && window.FormData && window.XMLHttpRequest;

export default function DocumentLibrary({
	editEntryUrl,
	namespace,
	searchContainerId,
	...config
}) {
	let searchContainer;

	const form = document[`${namespace}fm2`];

	const entriesContainer = document.getElementById(
		`${namespace}entriesContainer`
	);

	let documentLibraryUploadComponent;

	Liferay.componentReady(`${namespace}${searchContainerId}`).then(
		(searchContainerComponent) => {
			searchContainer = searchContainerComponent;

			searchContainer.registerAction('move-to-folder', _moveToFolder);
			searchContainer.registerAction('move-to-trash', _moveToTrash);
			searchContainer.on('rowToggled', _handleSearchContainerRowToggled);
		}
	);

	if (
		config.uploadable &&
		HTML5_UPLOAD &&
		themeDisplay.isSignedIn() &&
		entriesContainer
	) {
		config.appViewEntryTemplates = document.getElementById(
			`${namespace}appViewEntryTemplates`
		);

		document.addEventListener('dragenter', _plugUpload, {once: true});
	}

	function _handleSearchContainerRowToggled() {
		const bulkSelection =
			searchContainer.select &&
			searchContainer.select.get('bulkSelection');

		form.elements[`${namespace}selectAll`].value = bulkSelection;
	}

	function _moveCurrentSelection(newFolderId) {
		form.action = editEntryUrl;
		form.method = 'POST';
		form.enctype = 'multipart/form-data';

		form.elements[`${namespace}cmd`].value = 'move';
		form.elements[`${namespace}newFolderId`].value = newFolderId;

		submitForm(form, editEntryUrl, false);
	}

	function _moveSingleElement(newFolderId, parameterName, parameterValue) {
		const newForm = document.createElement('div');

		newForm.appendChild(
			buildFragment(
				`<form action="${editEntryUrl}" class="hide" method="POST">
					<input name="${namespace}cmd" value="move"/>
					<input name="${namespace}newFolderId" value="${newFolderId}"/>
					<input name="${namespace}${parameterName}" value="${parameterValue}"/>
				</form>`
			)
		);

		const formNode = newForm.firstElementChild;

		form.append(formNode);

		submitForm(formNode, editEntryUrl, false);
	}

	function _moveToFolder(object) {
		const dropTarget = object.targetItem;

		const selectedItems = object.selectedItems;

		const folderId = dropTarget.attr('data-folder-id');

		if (folderId) {
			if (
				!searchContainer.select ||
				selectedItems.indexOf(dropTarget.one('input[type=checkbox]'))
			) {
				_moveCurrentSelection(folderId);
			}
		}
	}

	function _moveToTrash() {
		const action = 'move_to_trash';

		if (form.elements[`${namespace}jakarta-portlet-action`]) {
			form.elements[`${namespace}jakarta-portlet-action`].value = action;
		}
		else {
			form.elements[`${namespace}cmd`].value = action;
		}

		submitForm(form, editEntryUrl, false);
	}

	function _plugUpload() {
		AUI().use('document-library-upload-component', () => {
			documentLibraryUploadComponent =
				new Liferay.DocumentLibraryUploadComponent({
					appViewEntryTemplates: config.appViewEntryTemplates,
					columnNames: config.columnNames,
					displayStyle: config.displayStyle,
					documentLibraryNamespace: namespace,
					entriesContainer,
					folderId: config.defaultParentFolderId,
					maxFileSize: config.maxFileSize,
					redirect: encodeURIComponent(config.redirect),
					scopeGroupId: config.scopeGroupId,
					uploadURL: config.uploadURL,
					viewFileEntryURL: config.viewFileEntryURL,
				});
		});
	}

	window[`${namespace}move`] = function (
		selectedItems,
		parameterName,
		parameterValue,
		selectFolderURL
	) {
		const dialogTitle =
			selectedItems === 1
				? Liferay.Language.get('select-destination-folder-for-x-item')
				: Liferay.Language.get('select-destination-folder-for-x-items');

		openSelectionModal({
			height: '480px',
			id: namespace,
			onSelect: (selectedItem) => {
				if (parameterName && parameterValue) {
					_moveSingleElement(
						selectedItem.resourceid,
						parameterName,
						parameterValue
					);
				}
				else {
					_moveCurrentSelection(selectedItem.resourceid);
				}
			},
			selectEventName: `${namespace}selectFolder`,
			size: 'lg',
			title: sub(dialogTitle, [selectedItems]),
			url: selectFolderURL,
		});
	};

	return {
		dispose() {
			document.removeEventListener('dragenter', _plugUpload);

			documentLibraryUploadComponent?.destroy();
		},
	};
}
