/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditableValue} from '../../../types/editables/EditableValue';

export default function isMappedToStructure(editable?: EditableValue | null) {
	return Boolean(editable?.mappedField);
}
