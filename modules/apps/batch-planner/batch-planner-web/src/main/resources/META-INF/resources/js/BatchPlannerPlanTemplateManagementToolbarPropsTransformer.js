/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {getCheckedCheckboxes, postForm} from 'frontend-js-web';

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const deleteBatchPlannerPlanTemplates = (itemData) => {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-templates'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					const form = document.getElementById(
						`${portletNamespace}fm`
					);

					const searchContainer = document.getElementById(
						`${portletNamespace}batchPlannerPlanTemplateDisplaySearchContainer`
					);

					if (form && searchContainer) {
						postForm(form, {
							data: {
								batchPlannerPlanIds: getCheckedCheckboxes(
									searchContainer,
									`${portletNamespace}allRowIds`
								),
							},
							url: itemData?.deleteBatchPlannerPlanTemplatesURL,
						});
					}
				}
			},
		});
	};

	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			const data = item?.data;

			const action = data?.action;

			if (action === 'deleteBatchPlannerPlanTemplates') {
				deleteBatchPlannerPlanTemplates(data);
			}
		},
	};
}
