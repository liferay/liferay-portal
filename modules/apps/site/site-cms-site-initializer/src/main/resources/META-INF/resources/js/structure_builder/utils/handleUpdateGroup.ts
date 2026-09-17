/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {Dispatch} from 'react';

import {Action, State} from '../contexts/StateContext';
import {Group, Structure} from '../types/Structure';
import {getChildrenUuids} from './getChildrenUuids';
import isLocked from './isLocked';

export default async function handleUpdateGroup({
	dispatch,
	group,
	isRepeatable,
	label,
	publishedChildren,
	structure,
}: {
	dispatch: Dispatch<Action>;
	group: Group;
	isRepeatable?: boolean;
	label?: Liferay.Language.LocalizedValue<string>;
	publishedChildren: State['publishedChildren'];
	structure: Structure;
}) {
	if (isRepeatable !== undefined && group.isRepeatable !== isRepeatable) {
		const uuids = Array.from(getChildrenUuids({root: group}));

		if (
			isRepeatable &&
			uuids.some((uuid) => isLocked({root: structure, uuid}))
		) {
			openToast({
				message: Liferay.Language.get(
					'a-group-that-contains-system-fields-cannot-be-repeatable'
				),
				type: 'danger',
			});

			return;
		}

		if (uuids.some((uuid) => publishedChildren.has(uuid))) {
			const confirmed = await openConfirmModal({
				buttonLabel: Liferay.Language.get('continue'),
				center: true,
				status: 'warning',
				text: isRepeatable
					? Liferay.Language.get(
							'creating-a-repeatable-group-with-published-fields-will-permanently-delete-existing-field-data-after-publishing-the-structure'
						)
					: Liferay.Language.get(
							'making-a-group-with-published-fields-not-repeatable-will-permanently-delete-existing-field-data-after-publishing-the-structure'
						),
				title: Liferay.Language.get('repeatable'),
			});

			if (!confirmed) {
				return;
			}
		}
	}

	dispatch({isRepeatable, label, type: 'update-group', uuid: group.uuid});
}
