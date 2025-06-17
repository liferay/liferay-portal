/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../contexts/StateContext';
import {Uuid} from '../types/Uuid';

export default function selectValidationErrors(uuid: Uuid) {
	return (state: State) => {
		return state.invalids.get(uuid) || new Set();
	};
}
