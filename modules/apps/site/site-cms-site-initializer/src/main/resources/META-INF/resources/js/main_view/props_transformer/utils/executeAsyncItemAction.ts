/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

// copy from FDS
// https://github.com/liferay/liferay-portal/blob/6ea264bb6965323430e035919247bf671c3adf99/modules/apps/frontend-data-set/frontend-data-set-web/src/main/resources/META-INF/resources/FrontendDataSet.js#L708-L770

export function executeAsyncItemAction({
	errorMessage,
	method = 'GET',
	refreshData,
	requestBody,
	setActionItemLoading,
	showToast = true,
	successMessage,
	url,
}: {
	errorMessage?: string;
	method?: string;
	refreshData?: (responseData: any) => void;
	requestBody?: string;
	setActionItemLoading?: (loading: boolean) => void;
	showToast?: boolean;
	successMessage?: string;
	url: string;
}): Promise<void> {
	const DEFAULT_ERROR = Liferay.Language.get('an-unexpected-error-occurred');

	const requestOptions: RequestInit = {
		headers: {
			'Accept': 'application/json',
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
			'Content-Type': 'application/json',
		},
		method,
	};

	if (method.toUpperCase() !== 'GET') {
		requestOptions.body = requestBody ? requestBody : '{}';
	}

	return fetch(url, requestOptions)
		.then((response) => {
			if (!response.ok) {
				throw new Error(DEFAULT_ERROR);
			}

			return response.status === 204 ? '' : response.json();
		})
		.then((responseData) => {
			if (showToast) {
				openToast({
					message:
						successMessage ||
						Liferay.Language.get(
							'your-request-completed-successfully'
						),
					type: 'success',
				});
			}

			refreshData?.(responseData);
		})
		.catch(() => {
			openToast({
				message: errorMessage || DEFAULT_ERROR,
				type: 'danger',
			});

			setActionItemLoading?.(false);
		});
}
