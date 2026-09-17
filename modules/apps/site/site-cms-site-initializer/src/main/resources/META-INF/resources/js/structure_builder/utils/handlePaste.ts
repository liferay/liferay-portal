/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import {Dispatch} from 'react';

import {Action, Clipboard} from '../contexts/StateContext';
import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import exceedsMaxNesting, {MAX_NESTING} from './exceedsMaxNesting';
import findChild from './findChild';
import isReferenced from './isReferenced';

export default function handlePaste({
	clipboard,
	dispatch,
	structure,
	targetUuid,
}: {
	clipboard: Clipboard | null;
	dispatch: Dispatch<Action>;
	structure: Structure;
	targetUuid: Uuid;
}) {
	if (!clipboard?.items.length) {
		return;
	}

	if (!isValidTarget({structure, targetUuid})) {
		openToast({
			message: Liferay.Language.get(
				'items-could-not-be-pasted-because-the-target-is-not-allowed'
			),
			type: 'danger',
		});

		return;
	}

	if (exceedsMaxNesting({items: clipboard.items, structure, targetUuid})) {
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

	dispatch({targetUuid, type: 'paste'});
}

function isValidTarget({
	structure,
	targetUuid,
}: {
	structure: Structure;
	targetUuid: Uuid;
}): boolean {
	if (targetUuid === structure.uuid) {
		return true;
	}

	const target = findChild({root: structure, uuid: targetUuid});

	if (!target || target.type !== 'group') {
		return false;
	}

	return !isReferenced({root: structure, uuid: targetUuid});
}
