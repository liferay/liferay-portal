/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openConfirmModal,
	openOptionsModal,
} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import {Dispatch} from 'react';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {Action, State} from '../contexts/StateContext';
import {Group, Structure, StructureChild} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import exceedsMaxNesting, {MAX_NESTING} from './exceedsMaxNesting';
import findAvailableFieldName from './findAvailableFieldName';
import findChild from './findChild';
import getUndeletableChildren, {
	UndeletableReason,
} from './getUndeletableChildren';
import isReferenced from './isReferenced';

export default async function handleMoveChildren({
	deletedChildren,
	dispatch,
	publishedChildren,
	structure,
	targetUuid,
	uuids,
}: {
	deletedChildren: State['history']['deletedChildren'];
	dispatch: Dispatch<Action>;
	publishedChildren: State['publishedChildren'];
	structure: Structure;
	targetUuid: Uuid;
	uuids: Uuid[];
}) {
	if (
		exceedsMaxNesting({
			items: uuids.map((uuid) => findChild({root: structure, uuid})!),
			structure,
			targetUuid,
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

	const movingPublished = uuids.some(
		(uuid) =>
			!isReferenced({root: structure, uuid}) &&
			publishedChildren.has(uuid)
	);

	if (movingPublished) {
		const confirm = await openConfirmModal({
			buttonLabel: Liferay.Language.get('move'),
			center: true,
			optOutConfig: {
				sessionKey: 'disableChildrenMoveModal',
			},
			status: 'warning',
			text: Liferay.Language.get(
				'moving-fields-may-impact-existing-stored-data-after-publishing-the-structure.-are-you-sure-you-want-to-continue'
			),
			title: Liferay.Language.get('move-field'),
		});

		if (!confirm) {
			return;
		}
	}

	const undeletables = getUndeletableChildren(uuids, structure);
	const reasons = [...undeletables.values()];

	const items = uuids.map((uuid) => findChild({root: structure, uuid})!);

	let movableItems = items
		.filter(
			({parent, uuid}) => !undeletables.has(uuid) && parent !== targetUuid
		)
		.map((item) => ({...item, parent: targetUuid}));

	if (!movableItems.length) {
		showWarnings(reasons);

		return;
	}

	const target =
		targetUuid === structure.uuid
			? structure
			: (findChild({
					root: structure,
					uuid: targetUuid,
				}) as Group);

	if (hasNameConflict(movableItems, target)) {
		const onNameConflict = await openOptionsModal({
			defaultValue: 'rename',
			options: [
				{
					label: Liferay.Language.get('do-not-move'),
					value: 'do-not-move',
				},
				{
					label: Liferay.Language.get('keep-both-and-rename'),
					value: 'rename',
				},
			],
			text: sub(
				Liferay.Language.get(
					'one-or-more-fields-have-field-names-that-already-exist-in-the-location-x.-what-action-do-you-want-to-take'
				),
				getLocalizedValue(target.label)
			),
			title: Liferay.Language.get('move-options'),
		});

		if (!onNameConflict) {
			showWarnings(reasons);

			return;
		}

		if (onNameConflict === 'rename') {
			movableItems = movableItems.map((item) => {
				if (!item.name) {
					return item;
				}

				return {
					...item,
					name: findAvailableFieldName(
						target.children,
						deletedChildren,
						item.name
					),
				};
			});
		}
		else if (onNameConflict === 'do-not-move') {
			movableItems = movableItems.filter(
				(item) =>
					!Array.from(target.children.values()).some(
						(child) => child.name === item.name
					)
			);
		}

		if (!movableItems.length) {
			showWarnings(reasons);

			return;
		}
	}

	showWarnings(reasons);

	dispatch({
		items: movableItems,
		targetUuid,
		type: 'move-children',
	});
}

function hasNameConflict(
	movableItems: StructureChild[],
	target: Structure | Group
): boolean {
	return movableItems.some((item) =>
		Array.from(target.children.values()).some(
			(child) => Boolean(item.name) && child.name === item.name
		)
	);
}

function showWarnings(reasons: UndeletableReason[]) {
	if (reasons.includes('causes-invalid-group')) {
		openToast({
			message: Liferay.Language.get(
				'some-fields-could-not-be-moved-because-at-least-one-field-is-required-in-a-repeatable-group'
			),
			type: 'danger',
		});
	}
	else if (
		reasons.includes('is-locked') ||
		reasons.includes('is-referenced')
	) {
		openToast({
			message: Liferay.Language.get(
				'some-items-could-not-be-moved-because-they-are-system-fields-or-they-belong-to-a-referenced-content-structure'
			),
			type: 'danger',
		});
	}
}
