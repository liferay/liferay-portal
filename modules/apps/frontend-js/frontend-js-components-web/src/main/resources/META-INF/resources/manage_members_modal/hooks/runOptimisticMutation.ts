/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {escapeHTML, sub} from 'frontend-js-web';
import {Dispatch} from 'react';

import openToast from '../../toast/openToast';
import {ServiceResult} from '../types';
import {Action} from './membersReducer';

export interface OptimisticMutation {
	errorMessage: string;
	name: string;
	onSuccess?: () => void;
	optimisticAction: Action;
	performMutation: () => Promise<ServiceResult>;
	rollbackAction: Action;
	successMessage?: string;
}

function showMemberToast(
	message: string,
	name: string,
	type: 'danger' | 'success'
) {
	openToast({
		message: sub(message, [`<strong>${escapeHTML(name)}</strong>`]),
		type,
	});
}

export async function runOptimisticMutation(
	dispatch: Dispatch<Action>,
	{
		errorMessage,
		name,
		onSuccess,
		optimisticAction,
		performMutation,
		rollbackAction,
		successMessage,
	}: OptimisticMutation
) {
	dispatch(optimisticAction);

	const {error} = await performMutation();

	if (error) {
		dispatch(rollbackAction);

		showMemberToast(errorMessage, name, 'danger');
	}
	else {
		onSuccess?.();

		if (successMessage) {
			showMemberToast(successMessage, name, 'success');
		}
	}
}
