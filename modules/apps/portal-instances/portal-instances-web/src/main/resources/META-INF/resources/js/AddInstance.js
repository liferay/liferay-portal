/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch, getOpener} from 'frontend-js-web';

export default function ({namespace}) {
	const form = document.getElementById(`${namespace}fm`);

	const content = document.querySelector('.add-instance-content');
	const loading = document.querySelector('.add-instance-loading');

	const onSubmit = (event) => {
		event.preventDefault();

		const formData = new FormData(form);

		content.classList.add('d-none');
		content.classList.remove('d-block');
		loading.classList.add('d-flex');

		const alertContainer = document.querySelector(
			'.add-instance-alert-container'
		);

		if (alertContainer.hasChildNodes()) {
			alertContainer.firstChild.remove();
		}

		fetch(form.action, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((response) => {
				const opener = getOpener();

				if (!response.error) {
					opener.Liferay.fire('closeModal', {
						id: `${namespace}addSiteDialog`,
						redirect: opener.location.href,
					});
				}
				else {
					content.classList.add('d-block');
					loading.classList.add('d-none');
					loading.classList.remove('d-flex');

					openToast({
						autoClose: false,
						container: alertContainer,
						message: response.error,
						toastProps: {
							onClose: null,
						},
						type: 'danger',
						variant: 'stripe',
					});
				}
			});
	};

	form.addEventListener('submit', onSubmit);

	return {
		dispose() {
			form.removeEventListener('submit', onSubmit);
		},
	};
}
