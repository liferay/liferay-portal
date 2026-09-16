/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import findChild from './findChild';
import isField from './isField';
import isLocked from './isLocked';
import isReferenced from './isReferenced';
import isRepeatableGroup from './isRepeatableGroup';

export type UndeletableReason =
	| 'is-locked'
	| 'is-referenced'
	| 'causes-invalid-group';

export default function getUndeletableChildren(
	uuids: Uuid[],
	structure: Structure
): Map<Uuid, UndeletableReason> {
	const undeletables = new Map<Uuid, UndeletableReason>();

	const items = uuids.map((uuid) => findChild({root: structure, uuid})!);

	for (const item of items) {
		if (isLocked({root: structure, uuid: item.uuid})) {
			undeletables.set(item.uuid, 'is-locked');
		}

		if (isReferenced({root: structure, uuid: item.uuid})) {
			undeletables.set(item.uuid, 'is-referenced');
		}

		const parent = findChild({
			root: structure,
			uuid: item.parent,
		});

		if (parent && isRepeatableGroup(parent)) {
			const groupFields = Array.from(parent.children.values()).filter(
				(child) => isField(child)
			);

			const fields = items.filter((item) => isField(item));

			if (
				groupFields.every(({uuid}) =>
					fields.some((field) => field.uuid === uuid)
				)
			) {
				groupFields.forEach((field) => {
					undeletables.set(field.uuid, 'causes-invalid-group');
				});
			}
		}
	}

	return undeletables;
}
