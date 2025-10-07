/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {getCheckedCheckboxes, postForm} from 'frontend-js-web';

export default function propsTransformer({
	additionalProps: {
		allSelectedLocalizedMessage,
		ddmFormInstanceRecordIds,
		deleteFormInstanceRecordURL,
	},
	portletNamespace,
	...otherProps
}) {
	return {
		...otherProps,
		onActionButtonClick(event, {item}) {
			if (item?.data?.action === 'deleteRecords') {
				openConfirmModal({
					message: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-this'
					),
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							const form = document.getElementById(
								`${portletNamespace}searchContainerForm`
							);

							const searchContainer = document.getElementById(
								`${portletNamespace}ddmFormInstanceRecord`
							);

							const managementBarSelection =
								document.querySelector(
									'.management-bar'
								)?.textContent;

							if (form && searchContainer) {
								if (
									managementBarSelection &&
									managementBarSelection.includes(
										allSelectedLocalizedMessage
									) &&
									!otherProps.searchValue
								) {
									postForm(form, {
										data: {
											deleteFormInstanceRecordIds:
												ddmFormInstanceRecordIds,
										},
										url: deleteFormInstanceRecordURL,
									});
								}
								else {
									postForm(form, {
										data: {
											deleteFormInstanceRecordIds:
												getCheckedCheckboxes(
													searchContainer,
													`${portletNamespace}allRowIds`
												),
										},
										url: deleteFormInstanceRecordURL,
									});
								}
							}
						}
					},
				});
			}
		},
	};
}
