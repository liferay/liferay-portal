/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import {Dispatch} from 'react';

import {Action, State} from '../contexts/StateContext';
import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import exceedsMaxNesting, {MAX_NESTING} from './exceedsMaxNesting';
import findChild from './findChild';
import handleAddRepeatableGroup from './handleAddRepeatableGroup';

export default async function handleAddGroup({
	dispatch,
	parent,
	publishedChildren,
	structure,
	uuids,
}: {
	dispatch: Dispatch<Action>;
	parent?: Uuid;
	publishedChildren: State['publishedChildren'];
	structure: Structure;
	uuids: Uuid[];
}) {
	if (!Liferay.FeatureFlags['LPD-96666']) {
		return handleAddRepeatableGroup({
			dispatch,
			publishedChildren,
			structure,
			uuids,
		});
	}

	const items = uuids.map((uuid) => findChild({root: structure, uuid})!);

	if (new Set(items.map((item) => item.parent)).size > 1) {
		openToast({
			message: Liferay.Language.get(
				'selected-items-must-be-at-the-same-hierarchy-level'
			),
			type: 'danger',
		});

		return;
	}

	const groupParent = parent ?? items[0].parent;

	if (
		exceedsMaxNesting({
			items,
			newGroup: true,
			structure,
			targetUuid: groupParent,
		})
	) {
		openToast({
			message: sub(
				Liferay.Language.get(
					'groups-cannot-be-nested-more-than-x-levels-deep'
				),
				MAX_NESTING
			),
			type: 'danger',
		});

		return;
	}

	dispatch({parent: groupParent, type: 'add-group', uuids});
}
