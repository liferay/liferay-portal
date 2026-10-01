/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML, fetch, getOpener, sub} from 'frontend-js-web';

export default function ({namespace, successMessage}) {
	const form = document.getElementById(`${namespace}fm`);

	const content = document.querySelector('.add-db-partition-company-content');
	const loading = document.querySelector('.add-db-partition-company-loading');

	let submitting = false;

	const showContent = () => {
		content.classList.add('d-block');
		content.classList.remove('d-none');
		loading.classList.add('d-none');
		loading.classList.remove('d-flex');
	};

	const showError = (alertContainer, message) => {
		showContent();

		openToast({
			autoClose: false,
			container: alertContainer,
			message: escapeHTML(message),
			toastProps: {
				onClose: null,
			},
			type: 'danger',
			variant: 'stripe',
		});
	};

	const onSubmit = async (event) => {
		event.preventDefault();

		if (submitting) {
			return;
		}

		submitting = true;

		const formData = new FormData(form);

		content.classList.add('d-none');
		content.classList.remove('d-block');
		loading.classList.add('d-flex');
		loading.classList.remove('d-none');

		const alertContainer = document.querySelector(
			'.add-db-partition-company-alert-container'
		);

		if (alertContainer.hasChildNodes()) {
			alertContainer.firstChild.remove();
		}

		try {
			const response = await fetch(form.action, {
				body: formData,
				method: 'POST',
			});

			if (!response.ok) {
				throw new Error(
					Liferay.Language.get('an-unexpected-error-occurred')
				);
			}

			const {error} = await response.json();

			if (error) {
				throw new Error(error);
			}

			const opener = getOpener();

			opener.Liferay.Util.openToast({
				message: sub(
					successMessage,
					escapeHTML(formData.get(`${namespace}schemaName`))
				),
				type: 'info',
			});

			opener.Liferay.fire('closeModal');
		}
		catch (error) {
			submitting = false;

			showError(alertContainer, error.message);
		}
	};

	form.addEventListener('submit', onSubmit);

	return {
		dispose() {
			form.removeEventListener('submit', onSubmit);
		},
	};
}
