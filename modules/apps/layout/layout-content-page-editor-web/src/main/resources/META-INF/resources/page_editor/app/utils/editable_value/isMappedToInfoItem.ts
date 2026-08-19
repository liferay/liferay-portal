/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditableValue} from '../../../types/editables/EditableValue';

interface BaseMapped {
	classNameId: string;
	fieldId: string;
}

interface MappedWithClassPK extends BaseMapped {
	classPK: string;
}

interface MappedWithERC extends BaseMapped {
	externalReferenceCode: string;
}

type MappedEditable = EditableValue & (MappedWithClassPK | MappedWithERC);

export default function isMappedToInfoItem(
	editable: EditableValue | null
): editable is MappedEditable {
	if (!editable) {
		return false;
	}

	if (typeof editable !== 'object') {
		return false;
	}

	return (
		'classNameId' in editable &&
		editable.classNameId !== '' &&
		'fieldId' in editable &&
		editable.fieldId !== '' &&
		(('classPK' in editable && editable.classPK !== '') ||
			('externalReferenceCode' in editable &&
				editable.externalReferenceCode !== ''))
	);
}
