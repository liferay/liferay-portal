/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export type RequestResult =
	| {data: {redirect?: string}; error: null}
	| {data: null; error: string | null};

async function postLaunchAction(
	url: string,
	body: FormData
): Promise<RequestResult> {
	try {
		const response = await fetch(url, {
			body,
			method: 'POST',
		});

		if (response.status === 401) {
			window.location.reload();

			return {data: null, error: null};
		}

		const {error, redirect, validationError} = await response.json();

		if (error || validationError) {
			return {data: null, error: error || validationError};
		}

		return {data: {redirect}, error: null};
	}
	catch (error) {
		return {
			data: null,
			error: Liferay.Language.get('an-unexpected-error-occurred'),
		};
	}
}

function scheduleLaunch(scheduleLaunchURL: string, body: FormData) {
	return postLaunchAction(scheduleLaunchURL, body);
}

function unscheduleLaunch(unscheduleLaunchURL: string) {
	return postLaunchAction(unscheduleLaunchURL, new FormData());
}

export default {
	scheduleLaunch,
	unscheduleLaunch,
};
