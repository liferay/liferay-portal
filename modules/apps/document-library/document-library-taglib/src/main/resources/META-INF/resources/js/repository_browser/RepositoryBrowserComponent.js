/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

export default function RepositoryBrowserComponent({
	includeExtension,
	namespace,
	parentFolderId,
	repositoryBrowserURL,
	repositoryId,
	viewableByGuest,
}) {
	const fileInput = document.getElementById(`${namespace}file`);

	const onInputChange = () => {
		const formData = new FormData();

		formData.append('file', fileInput.files[0]);

		const uploadFileURL = `${repositoryBrowserURL}?includeExtension=${includeExtension}&repositoryId=${repositoryId}&parentFolderId=${parentFolderId}&viewableByGuest=${viewableByGuest}`;

		fetch(uploadFileURL, {
			body: formData,
			method: 'PUT',
		})
			.then(() => window.location.reload())
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	fileInput.addEventListener('change', onInputChange);

	return {
		dispose() {
			fileInput.removeEventListener('change', onInputChange);
		},
	};
}
