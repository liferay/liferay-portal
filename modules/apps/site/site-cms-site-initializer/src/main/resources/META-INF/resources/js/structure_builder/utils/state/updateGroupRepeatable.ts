/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../contexts/StateContext';
import {Group, Structure} from '../../types/Structure';
import getClosestERC from '../getClosestERC';
import getRandomId from '../getRandomId';
import getRandomName from '../getRandomName';
import updateChild from './updateChild';

export default function updateGroupRepeatable({
	group,
	history,
	isRepeatable,
	savedChildren,
	structure,
}: {
	group: Group;
	history: State['history'];
	isRepeatable: boolean;
	savedChildren: State['savedChildren'];
	structure: Structure;
}): {children: Structure['children']; history: State['history']} {
	const nextGroup: Group = isRepeatable
		? {
				...group,
				erc: group.erc ?? getRandomId(),
				isRepeatable: true,
				name: group.name ?? getRandomName({capitalize: true}),
				relationshipERC: group.relationshipERC ?? getRandomId(),
				relationshipName: group.relationshipName ?? getRandomName(),
			}
		: {...group, isRepeatable: false};

	return {
		children: updateChild({child: nextGroup, root: structure}),
		history: getNextHistory({
			group,
			history,
			isRepeatable,
			savedChildren,
			structure,
		}),
	};
}

function getNextHistory({
	group,
	history,
	isRepeatable,
	savedChildren,
	structure,
}: {
	group: Group;
	history: State['history'];
	isRepeatable: boolean;
	savedChildren: State['savedChildren'];
	structure: Structure;
}): State['history'] {
	const {erc, relationshipERC} = group;

	if (!erc || !relationshipERC) {
		return history;
	}

	if (isRepeatable) {
		return {
			...history,
			deletedGroupERCs: history.deletedGroupERCs.filter(
				(deletedERC) => deletedERC !== erc
			),
			deletedRelationships: history.deletedRelationships.filter(
				(deletedRelationship) =>
					deletedRelationship.relationshipERC !== relationshipERC
			),
		};
	}

	if (!savedChildren.has(group.uuid)) {
		return history;
	}

	return {
		...history,
		deletedGroupERCs: [...history.deletedGroupERCs, erc],
		deletedRelationships: [
			...history.deletedRelationships,
			{
				relationshipERC,
				structureERC: getClosestERC({structure, uuid: group.parent}),
			},
		],
	};
}
