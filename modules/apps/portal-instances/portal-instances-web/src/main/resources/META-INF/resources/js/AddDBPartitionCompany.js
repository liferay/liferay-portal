/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch, getOpener} from 'frontend-js-web';

export default function ({namespace}) {
	const form = document.getElementById(`${namespace}fm`);

	const content = document.querySelector('.add-db-partition-company-content');
	const loading = document.querySelector('.add-db-partition-company-loading');

	let isSubmitting = false;

	const onSubmit = (event) => {
		event.preventDefault();

		if (isSubmitting) {
			return;
		}

		isSubmitting = true;

		const formData = new FormData(form);

		content.classList.add('d-none');
		content.classList.remove('d-block');
		loading.classList.add('d-flex');

		const alertContainer = document.querySelector(
			'.add-db-partition-company-alert-container'
		);

		if (alertContainer.hasChildNodes()) {
			alertContainer.firstChild.remove();
		}

		const showError = (message) => {
			isSubmitting = false;

			content.classList.add('d-block');
			content.classList.remove('d-none');
			loading.classList.add('d-none');
			loading.classList.remove('d-flex');

			openToast({
				autoClose: false,
				container: alertContainer,
				message,
				toastProps: {
					onClose: null,
				},
				type: 'danger',
				variant: 'stripe',
			});
		};

		fetch(form.action, {
			body: formData,
			method: 'POST',
		})
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.status);
				}

				return response.json();
			})
			.then((responseJSON) => {
				if (responseJSON.companyId) {
					const opener = getOpener();

					opener.Liferay.fire('closeModal', {
						redirect: opener.location.href,
					});
				}
				else if (responseJSON.error) {
					showError(responseJSON.error);
				}
				else {
					showError(
						Liferay.Language.get('an-unexpected-error-occurred')
					);
				}
			})
			.catch(() => {
				showError(Liferay.Language.get('an-unexpected-error-occurred'));
			});
	};

	form.addEventListener('submit', onSubmit);

	return {
		dispose() {
			form.removeEventListener('submit', onSubmit);
		},
	};
}
