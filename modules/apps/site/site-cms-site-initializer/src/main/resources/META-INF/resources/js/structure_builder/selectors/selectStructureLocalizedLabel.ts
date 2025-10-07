/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {State} from '../contexts/StateContext';

export default function selectStructureLocalizedLabel(state: State) {
	return getLocalizedValue(state.structure.label);
}
