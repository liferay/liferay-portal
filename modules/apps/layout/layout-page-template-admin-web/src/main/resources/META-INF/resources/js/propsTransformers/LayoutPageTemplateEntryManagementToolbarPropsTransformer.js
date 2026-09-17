/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSimpleInputModal} from 'frontend-js-components-web';

import openDeletePageTemplateModal from '../commands/openDeletePageTemplateModal';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const addLayoutPageTemplateEntry = (data) => {
		openSimpleInputModal({
			dialogTitle: Liferay.Language.get('add-page-template'),
			formSubmitURL: data?.addPageTemplateURL,
			mainFieldLabel: Liferay.Language.get('name'),
			mainFieldName: 'name',
			mainFieldPlaceholder: Liferay.Language.get('name'),
			namespace: portletNamespace,
		});
	};

	const deleteLayoutPageTemplateEntries = () => {
		openDeletePageTemplateModal({
			onDelete: () => {
				const form = document.getElementById(`${portletNamespace}fm`);

				if (form) {
					submitForm(form);
				}
			},
			title: Liferay.Language.get('page-template-sets'),
		});
	};

	const exportLayoutPageTemplateEntries = (itemData) => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (form) {
			submitForm(form, itemData?.exportLayoutPageTemplateEntryURL);
		}
	};

	return {
		...otherProps,
		onActionButtonClick(event, {item}) {
			const data = item?.data;

			const action = data?.action;

			if (action === 'deleteLayoutPageTemplateEntries') {
				deleteLayoutPageTemplateEntries();
			}
			else if (action === 'exportLayoutPageTemplateEntries') {
				exportLayoutPageTemplateEntries(data);
			}
		},
		onCreateButtonClick(event, {item}) {
			const data = item?.data;

			if (data?.action === 'addLayoutPageTemplateEntry') {
				addLayoutPageTemplateEntry(data);
			}
		},
		onCreationMenuItemClick(event, {item}) {
			const data = item?.data;

			if (data?.action === 'addLayoutPageTemplateEntry') {
				addLayoutPageTemplateEntry(data);
			}
		},
	};
}
