/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from 'frontend-js-components-web';
import {getCheckedCheckboxes, postForm} from 'frontend-js-web';

export function ExportImportManagementToolbarPropsTransformer({
	portletNamespace,
	...otherProps
}) {
	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			if (item?.data?.action === 'deleteEntries') {
				openConfirmModal({
					message: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-the-selected-entries'
					),
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							const form = document.getElementById(
								`${portletNamespace}fm`
							);

							if (form) {
								postForm(form, {
									data: {
										cmd: 'delete',
										deleteBackgroundTaskIds:
											getCheckedCheckboxes(
												form,
												`${portletNamespace}allRowIds`
											),
									},
								});
							}
						}
					},
				});
			}
		},
	};
}

export {EditExport} from '../revamp/js/pages/EditExport';
export {default as ImportButton} from './legacy/components/buttons/ImportButton';
export {ViewImportErrorDetail} from './pages/ViewImportErrorDetail';
export {default as formatDate} from './utils/formatDate';
