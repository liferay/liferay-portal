/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group, SerializedGroup} from '../../types';
import {createGroup} from './createGroup';
import {parseGroup} from './parseGroup';

export function parseRootGroup(serialized?: SerializedGroup): Group {
	return serialized ? parseGroup(serialized) : createGroup();
}
