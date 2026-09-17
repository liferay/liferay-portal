/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildLocalizedValue from '../../../common/utils/buildLocalizedValue';
import {
	Group,
	NonRepeatableGroup,
	Structure,
	StructureChild,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import insertGroup from './insertGroup';

export default function addGroup({
	groupChildren,
	groupParent,
	groupUuid,
	root,
}: {
	groupChildren: StructureChild[];
	groupParent: Uuid;
	groupUuid: Uuid;
	root: Structure | Group;
}): Structure['children'] | Group['children'] {
	const group: NonRepeatableGroup = {
		children: new Map(
			groupChildren.map((child) => [
				child.uuid,
				{...child, parent: groupUuid},
			])
		),
		isRepeatable: false,
		label: buildLocalizedValue('group'),
		parent: groupParent,
		type: 'group',
		uuid: groupUuid,
	};

	return insertGroup({group, groupChildren, root});
}
