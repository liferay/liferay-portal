/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const openInstanceModal = (item) => {
		const importURL = item?.data?.importURL;

		openModal({
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					formId: `${portletNamespace}fm`,
					label: importURL
						? Liferay.Language.get('import')
						: Liferay.Language.get('add'),
					type: 'submit',
				},
			],
			height: '60vh',
			iframeBodyCssClass: '',
			size: 'md',
			title: importURL
				? Liferay.Language.get('import-instance')
				: Liferay.Language.get('add-instance'),
			url: importURL || item?.data?.addInstanceURL,
		});
	};

	return {
		...otherProps,
		onCreateButtonClick(event, {item}) {
			event.preventDefault();

			openInstanceModal(item);
		},
		onCreationMenuItemClick(event, {item}) {
			event.preventDefault();

			openInstanceModal(item);
		},
	};
}
