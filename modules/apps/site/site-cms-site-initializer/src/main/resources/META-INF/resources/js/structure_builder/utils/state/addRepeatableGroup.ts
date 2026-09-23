/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import buildLocalizedValue from '../../../common/utils/buildLocalizedValue';
import {DefaultLanguageLabels} from '../../../common/utils/defaultLanguageLabels';
import {
	Group,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import getRandomId from '../getRandomId';
import getRandomName from '../getRandomName';
import insertGroup from './insertGroup';

export default function addRepeatableGroup({
	defaultLanguageLabels,
	groupChildren,
	groupParent,
	groupUuid,
	root,
}: {
	defaultLanguageLabels: DefaultLanguageLabels;
	groupChildren: StructureChild[];
	groupParent: Uuid;
	groupUuid: Uuid;
	root: Structure | Group;
}): Structure['children'] | RepeatableGroup['children'] {
	const group: RepeatableGroup = {
		children: new Map(
			groupChildren.map((child) => [
				child.uuid,
				{...child, parent: groupUuid},
			])
		),
		erc: getRandomId(),
		isRepeatable: true,
		label: buildLocalizedValue({
			defaultLanguageLabels,
			key: 'repeatable-group',
		}),
		name: getRandomName({capitalize: true}),
		parent: groupParent,
		relationshipERC: getRandomId(),
		relationshipName: getRandomName(),
		type: 'group',
		uuid: groupUuid,
	};

	return insertGroup({group, groupChildren, root});
}
