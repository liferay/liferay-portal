/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {StructureServiceError} from '../../common/services/StructureService';
import {Action} from '../contexts/StateContext';
import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';

export default function buildStructureErrorAction({
	error,
	previousStatus,
	uuid,
}: {
	error: StructureServiceError;
	previousStatus: Structure['status'];
	uuid: Uuid;
}): Action {
	if (error === 'slug-in-use') {
		return {
			error: 'in-use',
			property: 'slug',
			status: previousStatus,
			type: 'add-error',
			uuid,
		};
	}

	if (error === 'in-use') {
		return {
			error: 'in-use',
			property: 'name',
			status: previousStatus,
			type: 'add-error',
			uuid,
		};
	}

	return {
		error: 'unexpected',
		property: 'global',
		status: previousStatus,
		type: 'add-error',
		uuid,
	};
}
