/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Action} from '../contexts/StateContext';
import {Uuid} from '../types/Uuid';
import {SERVER_ERRORS, ServerError} from './validation';

export default function buildStructureErrorAction({
	error,
	uuid,
}: {
	error: ServerError;
	uuid: Uuid;
}): Action {
	return {
		...SERVER_ERRORS[error],
		type: 'add-error',
		uuid,
	};
}
