/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const ACTIONS = {
	move: 'move',
	moveEntries: 'moveEntries',
	moveToTrash: '/journal/move_articles_and_folders_to_trash',
};

export default function ({
	editEntryURL,
	moveEntryURL,
	namespace: portletNamespace,
	searchContainerId,
}) {
	const editEntry = (event) => {
		const action = event.action;

		const actionForm = getActionForm(action);

		const url =
			action === ACTIONS.move || action === ACTIONS.moveEntries
				? moveEntryURL
				: editEntryURL;

		submitForm(actionForm, url);
	};

	const getActionForm = (action, folderId) => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (folderId) {
			const newFolderId = document.getElementById(
				`${portletNamespace}newFolderId`
			);

			newFolderId.setAttribute('value', folderId);
		}

		form.setAttribute('method', 'POST');

		const redirectUrl = window.location.href;

		form.setAttribute(`${portletNamespace}redirect`, redirectUrl);

		const inputId = document.getElementById(
			`${portletNamespace}jakarta-portlet-action`
		);

		if (inputId) {
			inputId.setAttribute('value', action);
		}
		else {
			inputId.setAttribute(`${portletNamespace}cmd`, action);
		}

		return form;
	};

	const moveToFolder = (object) => {
		const dropTarget = object.targetItem;

		const selectedItems = object.selectedItems;

		const folderId = dropTarget.getAttribute('data-folder-id');

		if (folderId) {
			if (
				!searchContainer.select ||
				selectedItems.indexOf(dropTarget.one('input[type=checkbox]'))
			) {
				const actionForm = getActionForm(ACTIONS.move, folderId);

				submitForm(actionForm, moveEntryURL);
			}
		}
	};

	const moveToTrash = () => {
		const actionForm = getActionForm(ACTIONS.moveToTrash);

		submitForm(actionForm, editEntryURL);
	};

	const searchContainer = Liferay.SearchContainer.get(
		`${portletNamespace}${searchContainerId}`
	);

	searchContainer.registerAction('move-to-folder', moveToFolder);

	searchContainer.registerAction('move-to-trash', moveToTrash);

	Liferay.on(`${portletNamespace}editEntry`, editEntry);

	return {
		dispose() {
			Liferay.detach(`${portletNamespace}editEntry`, editEntry);
		},
	};
}
