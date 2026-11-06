/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Structure, StructureChild} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {canPlaceRepeatableGroups} from './canContainRepeatableGroup';
import isRelationship from './isRelationship';
import isRepeatableGroup from './isRepeatableGroup';

export default function getPlacementErrorMessage({
	items,
	structure,
	targetUuid,
}: {
	items: StructureChild[];
	structure: Structure;
	targetUuid: Uuid;
}): string | null {
	if (
		Liferay.FeatureFlags['LPD-96666'] &&
		targetUuid !== structure.uuid &&
		items.some((item) => isRelationship(item) && !isRepeatableGroup(item))
	) {
		return Liferay.Language.get(
			'referenced-structures-and-related-content-can-only-be-placed-at-the-first-level'
		);
	}

	if (!canPlaceRepeatableGroups({items, structure, targetUuid})) {
		return Liferay.Language.get(
			'repeatable-groups-can-only-be-placed-at-the-first-level-or-inside-a-tab'
		);
	}

	return null;
}
