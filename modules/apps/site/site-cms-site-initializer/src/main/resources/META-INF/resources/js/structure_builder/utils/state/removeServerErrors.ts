/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../contexts/StateContext';
import {Uuid} from '../../types/Uuid';
import {SERVER_ERRORS} from '../validation';

export default function removeServerErrors({
	invalids,
	uuid,
}: {
	invalids: State['invalids'];
	uuid: Uuid;
}): State['invalids'] {
	const errors = new Map(invalids.get(uuid));

	for (const {error, property} of Object.values(SERVER_ERRORS)) {
		if (property === 'global' && errors.get(property) === error) {
			errors.delete(property);
		}
	}

	const nextInvalids = new Map(invalids);

	if (errors.size) {
		nextInvalids.set(uuid, errors);
	}
	else {
		nextInvalids.delete(uuid);
	}

	return nextInvalids;
}
