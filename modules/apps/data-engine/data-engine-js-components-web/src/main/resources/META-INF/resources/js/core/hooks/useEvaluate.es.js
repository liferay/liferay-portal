/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback} from 'react';

import {useConfig} from './useConfig.es';
import {useFormState} from './useForm.es';

/**
 * This hook is a partial function that removes the need to pass the same
 * properties every time they are called, this is only for thunks that use
 * the `evaluate` function.
 */
export function useEvaluate(thunk) {
	const {containerId, groupId, portletNamespace} = useConfig();
	const {
		defaultLanguageId,
		editingLanguageId,
		objectFields,
		pages,
		rules,
		title,
	} = useFormState();

	return useCallback(
		(args) =>
			thunk({
				containerId,
				defaultLanguageId,
				editingLanguageId,
				groupId,
				objectFields,
				pages,
				portletNamespace,
				rules,
				title,
				...args,
			}),
		[
			containerId,
			defaultLanguageId,
			editingLanguageId,
			groupId,
			objectFields,
			pages,
			portletNamespace,
			rules,
			thunk,
			title,
		]
	);
}
