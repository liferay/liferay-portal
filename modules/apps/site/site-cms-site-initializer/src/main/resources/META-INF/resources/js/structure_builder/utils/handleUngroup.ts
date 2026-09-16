/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {Dispatch} from 'react';

import {Action, State} from '../contexts/StateContext';
import {Uuid} from '../types/Uuid';

export default async function handleUngroup({
	dispatch,
	publishedChildren,
	uuid,
}: {
	dispatch: Dispatch<Action>;
	publishedChildren: State['publishedChildren'];
	uuid: Uuid;
}) {
	if (publishedChildren.has(uuid)) {
		openToast({
			message: Liferay.Language.get(
				'the-ungroup-action-cannot-be-done-because-this-repeatable-group-is-already-published'
			),
			type: 'danger',
		});

		return;
	}

	dispatch({type: 'ungroup', uuid});
}
