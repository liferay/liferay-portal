/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Group,
	ReferencedStructure,
	RelatedContent,
	Structure,
	StructureChild,
} from '../../types/Structure';
import {Uuid} from '../../types/Uuid';
import {Field} from '../field';
import findAvailableFieldName from '../findAvailableFieldName';
import getRandomId from '../getRandomId';
import getRandomName from '../getRandomName';
import getUuid from '../getUuid';

export default function cloneChild({
	child,
	deletedChildren,
	parent,
	siblings,
}: {
	child: StructureChild;
	deletedChildren: StructureChild[];
	parent: Uuid;
	siblings: Structure['children'];
}): StructureChild {
	const uuid = getUuid();

	if (child.type === 'group') {
		const group: Group = {
			...child,
			children: new Map(),
			erc: getRandomId(),
			name: getRandomName({capitalize: true}),
			parent,
			relationshipERC: getRandomId(),
			relationshipName: getRandomName(),
			uuid,
		};

		for (const grandchild of child.children.values()) {
			const cloned = cloneChild({
				child: grandchild,
				deletedChildren,
				parent: uuid,
				siblings: group.children,
			});

			group.children.set(cloned.uuid, cloned);
		}

		return group;
	}

	if (child.type === 'referenced-structure') {
		const referenced: ReferencedStructure = {
			...child,
			children: new Map(),
			parent,
			relationshipName: getRandomName(),
			uuid,
		};

		for (const grandchild of child.children.values()) {
			const cloned = cloneChild({
				child: grandchild,
				deletedChildren,
				parent: uuid,
				siblings: referenced.children,
			});

			referenced.children.set(cloned.uuid, cloned);
		}

		return referenced;
	}

	if (child.type === 'related-content') {
		const relatedContent: RelatedContent = {
			...child,
			erc: getRandomId(),
			name: getRandomName(),
			parent,
			uuid,
		};

		return relatedContent;
	}

	const field: Field = {
		...child,
		erc: getRandomId(),
		name: findAvailableFieldName(siblings, deletedChildren, child.name),
		parent,
		uuid,
	};

	return field;
}
