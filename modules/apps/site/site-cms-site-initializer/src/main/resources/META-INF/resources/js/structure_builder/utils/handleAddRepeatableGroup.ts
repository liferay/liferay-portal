/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import {Dispatch} from 'react';

import {Action, State} from '../contexts/StateContext';
import {Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import exceedsMaxNesting, {MAX_NESTING} from './exceedsMaxNesting';
import findChild from './findChild';
import getUndeletableChildren from './getUndeletableChildren';

export default async function handleAddRepeatableGroup({
	dispatch,
	publishedChildren,
	structure,
	uuids,
}: {
	dispatch: Dispatch<Action>;
	publishedChildren: State['publishedChildren'];
	structure: Structure;
	uuids: Uuid[];
}) {
	const undeletables = getUndeletableChildren(uuids, structure);

	const reasons = [...undeletables.values()];

	if (reasons.includes('is-locked')) {
		openToast({
			message: Liferay.Language.get(
				'the-repeatable-group-cannot-be-created-because-one-or-more-fields-of-the-selection-are-system-fields'
			),
			type: 'danger',
		});

		return;
	}

	if (reasons.includes('is-referenced')) {
		openToast({
			message: Liferay.Language.get(
				'the-repeatable-group-cannot-be-created-because-referenced-structure-fields-are-not-allowed-in-repeatable-groups'
			),
			type: 'danger',
		});

		return;
	}

	if (reasons.includes('causes-invalid-group')) {
		openToast({
			message: Liferay.Language.get(
				'the-repeatable-group-cannot-be-created-because-at-least-one-field-is-required'
			),
			type: 'danger',
		});

		return;
	}

	const parents = uuids.map((uuid) => {
		const item = findChild({
			root: structure,
			uuid,
		})!;

		return (
			findChild({
				root: structure,
				uuid: item.parent,
			}) || structure
		);
	});

	const isSameParent = new Set(parents).size === 1;

	if (!isSameParent) {
		openToast({
			message: Liferay.Language.get(
				'a-repeatable-group-requires-all-selected-items-to-be-at-the-same-hierarchy-level'
			),
			type: 'danger',
		});

		return;
	}

	const [parent] = parents;

	if (
		exceedsMaxNesting({
			items: uuids.map((uuid) => findChild({root: structure, uuid})!),
			newGroup: true,
			structure,
			targetUuid: parent.uuid,
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

	if (uuids.some((uuid) => publishedChildren.has(uuid))) {
		const confirm = await openConfirmModal({
			buttonLabel: Liferay.Language.get('create-repeatable-group'),
			center: true,
			optOutConfig: {
				sessionKey: 'disableRepeatableGroupCreationModal',
			},
			status: 'warning',
			text: Liferay.Language.get(
				'creating-a-repeatable-group-with-published-fields-will-permanently-delete-existing-field-data-after-publishing-the-structure'
			),
			title: Liferay.Language.get('create-repeatable-group'),
		});

		if (!confirm) {
			return;
		}
	}

	dispatch({type: 'add-repeatable-group', uuids});
}
