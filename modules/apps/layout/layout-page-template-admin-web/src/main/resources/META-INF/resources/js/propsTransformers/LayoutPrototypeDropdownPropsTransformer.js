/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import openDeletePageTemplateModal from '../commands/openDeletePageTemplateModal';

const ACTIONS = {
	deleteLayoutPrototype({deleteLayoutPrototypeURL}) {
		openDeletePageTemplateModal({
			onDelete: () => {
				submitForm(document.hrefFm, deleteLayoutPrototypeURL);
			},
			title: Liferay.Language.get('page-template'),
		});
	},

	exportLayoutPrototype({exportLayoutPrototypeURL}) {
		openModal({
			title: Liferay.Language.get('export'),
			url: exportLayoutPrototypeURL,
		});
	},

	importLayoutPrototype({importLayoutPrototypeURL}) {
		openModal({
			title: Liferay.Language.get('import'),
			url: importLayoutPrototypeURL,
		});
	},

	permissionsLayoutPrototype({permissionsLayoutPrototypeURL}) {
		openModal({
			title: Liferay.Language.get('permissions'),
			url: permissionsLayoutPrototypeURL,
		});
	},
};

export default function LayoutPrototypeDropdownPropsTransformer({
	actions,
	...otherProps
}) {
	return {
		...otherProps,
		actions: actions?.map((item) => {
			return {
				...item,
				items: item.items?.map((child) => {
					return {
						...child,
						onClick(event) {
							const action = child.data?.action;

							if (action) {
								event.preventDefault();

								ACTIONS[action](child.data);
							}
						},
					};
				}),
			};
		}),
	};
}
