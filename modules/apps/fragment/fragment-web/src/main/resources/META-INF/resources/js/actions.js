/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openModal,
	openSelectionModal,
	openToast,
} from 'frontend-js-components-web';

import openDeleteFragmentCollectionModal from './openDeleteFragmentCollectionModal';

export const ACTIONS = {
	deleteCollections({
		deleteFragmentCollectionURL,
		portletNamespace,
		viewDeleteFragmentCollectionsURL,
	}) {
		this.openFragmentCollectionsItemSelector(
			Liferay.Language.get('delete'),
			Liferay.Language.get('delete-fragment-set'),
			viewDeleteFragmentCollectionsURL,
			(selectedItems) => {
				if (!selectedItems?.length) {
					return;
				}

				const form = document.getElementById(`${portletNamespace}fm`);

				if (!form) {
					return;
				}

				openDeleteFragmentCollectionModal({
					multiple: true,
					onDelete: () => {
						let input = form.elements[`${portletNamespace}rowIds`];

						if (!input) {
							input = document.createElement('input');
							input.name = `${portletNamespace}rowIds`;
						}

						input.value = selectedItems.map((item) => item.value);

						form.appendChild(input);

						submitForm(form, deleteFragmentCollectionURL);
					},
				});
			},
			null,
			portletNamespace
		);
	},

	exportCollections({
		exportFragmentCollectionsURL,
		portletNamespace,
		viewExportFragmentCollectionsURL,
	}) {
		let processed = false;

		this.openFragmentCollectionsItemSelector(
			Liferay.Language.get('export'),
			Liferay.Language.get('export-fragment-set'),
			viewExportFragmentCollectionsURL,
			(selectedItems) => {
				if (!selectedItems?.length) {
					return;
				}

				const form = document.getElementById(`${portletNamespace}fm`);

				if (!form) {
					return;
				}

				let input = form.elements[`${portletNamespace}rowIds`];

				if (!input) {
					input = document.createElement('input');
					input.name = `${portletNamespace}rowIds`;
				}

				input.value = selectedItems.map((item) => item.value);
				input.setAttribute('type', 'hidden');

				form.appendChild(input);

				submitForm(form, exportFragmentCollectionsURL);

				processed = true;
			},
			() => {
				if (processed) {
					openToast({
						message: Liferay.Language.get(
							'your-request-processed-successfully'
						),
						toastProps: {
							autoClose: 5000,
						},
						type: 'success',
					});
				}
			},
			portletNamespace
		);
	},

	openFragmentCollectionsItemSelector(
		dialogButtonLabel,
		dialogTitle,
		dialogURL,
		callback,
		onClose,
		portletNamespace
	) {
		openSelectionModal({
			buttonAddLabel: dialogButtonLabel,
			multiple: true,
			onClose,
			onSelect: (selectedItem) => {
				if (selectedItem) {
					callback(selectedItem);
				}
			},
			selectEventName: `${portletNamespace}selectCollections`,
			title: dialogTitle,
			url: dialogURL,
		});
	},

	openImportView({portletNamespace, viewImportURL}) {
		openModal({
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					label: Liferay.Language.get('import'),
					type: 'submit',
				},
			],
			id: `${portletNamespace}openImportView`,
			onClose: () => {
				window.location.reload();
			},
			title: Liferay.Language.get('import'),
			url: viewImportURL,
		});
	},
};
