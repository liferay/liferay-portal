/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DROP_POSITIONS} from '../constants/dropPositions';
import {Group} from '../types';
import {DropZone} from './getDropPosition';
import {canGroupNode} from './tree/canGroupNode';
import {isGroup} from './tree/isGroup';

export interface MoveTarget {
	groupId: string;
	groupPath: number[];
	index: number;
	nodeId: string;
	position: DropZone;
}

export function getMoveTargets(root: Group): MoveTarget[] {
	const targets: MoveTarget[] = [];

	const collectTargets = (group: Group, groupPath: number[]) => {
		group.items.forEach((node, index) => {
			targets.push({
				groupId: group.id,
				groupPath,
				index,
				nodeId: node.id,
				position: DROP_POSITIONS.top,
			});

			if (isGroup(node)) {
				collectTargets(node, [...groupPath, index]);
			}
			else if (canGroupNode([...groupPath, index])) {
				targets.push({
					groupId: group.id,
					groupPath,
					index,
					nodeId: node.id,
					position: 'middle',
				});
			}

			if (index === group.items.length - 1) {
				targets.push({
					groupId: group.id,
					groupPath,
					index: index + 1,
					nodeId: node.id,
					position: DROP_POSITIONS.bottom,
				});
			}
		});
	};

	collectTargets(root, []);

	return targets;
}
