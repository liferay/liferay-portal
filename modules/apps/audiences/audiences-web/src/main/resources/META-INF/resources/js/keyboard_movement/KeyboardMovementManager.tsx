/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {Dispatch, useEffect, useMemo} from 'react';

import {DROP_POSITIONS} from '../constants/dropPositions';
import {
	ARROW_DOWN_KEY_CODE,
	ARROW_UP_KEY_CODE,
	END_KEY_CODE,
	ENTER_KEY_CODE,
	ESCAPE_KEY_CODE,
	HOME_KEY_CODE,
	SPACE_KEY_CODE,
} from '../constants/keyboardCodes';
import {Action} from '../reducer';
import {Group} from '../types';
import {DropZone} from '../util/getDropPosition';
import {MoveTarget, getMoveTargets} from '../util/getMoveTargets';
import {
	MovementSource,
	MovementTarget,
	useDisableKeyboardMovement,
	useMovementTarget,
	useSetMovementTarget,
	useSetMovementText,
} from './KeyboardMovementContext';

interface Props {
	dispatch: Dispatch<Action>;
	namesById: Record<string, string>;
	root: Group;
	source: MovementSource;
}

export default function KeyboardMovementManager({
	dispatch,
	namesById,
	root,
	source,
}: Props) {
	const disableMovement = useDisableKeyboardMovement();
	const setTarget = useSetMovementTarget();
	const setText = useSetMovementText();
	const target = useMovementTarget();

	const targets = useMemo(
		() =>
			getMoveTargets(root).filter(
				(moveTarget) =>
					moveTarget.position !== 'middle' ||
					moveTarget.nodeId !== source.ruleId
			),
		[root, source.ruleId]
	);

	const currentIndex = targets.findIndex(
		(moveTarget) =>
			moveTarget.nodeId === target.nodeId &&
			moveTarget.position === target.position
	);

	const sourceIndex = source.ruleId
		? targets.findIndex(
				(moveTarget) =>
					moveTarget.nodeId === source.ruleId &&
					moveTarget.position === DROP_POSITIONS.top
			)
		: -1;

	useEffect(() => {
		if (target.nodeId !== null) {
			return;
		}

		if (!targets.length) {
			if (!source.ruleId && source.audiencesCriteria) {
				dispatch({
					audiencesCriteria: source.audiencesCriteria,
					index: 0,
					type: 'ADD_RULE',
				});

				setText(Liferay.Language.get('a-condition-was-added'));
			}

			disableMovement();

			return;
		}

		setTarget(toMovementTarget(getInitialTarget(targets, sourceIndex)));

		setText(
			Liferay.Language.get(
				'use-arrows-to-move-it-and-press-enter-to-select-the-new-position-press-esc-to-cancel'
			)
		);
	}, [
		disableMovement,
		dispatch,
		setTarget,
		setText,
		source,
		sourceIndex,
		target,
		targets,
	]);

	useEffect(() => {
		const confirmPlacement = () => {
			const moveTarget = targets[currentIndex];

			if (!moveTarget) {
				return;
			}

			if (source.ruleId) {
				if (moveTarget.position === 'middle') {
					dispatch({
						nodeId: source.ruleId,
						targetId: moveTarget.nodeId,
						type: 'MOVE_RULE_INTO_NEW_GROUP',
					});
				}
				else {
					const sourceTarget = targets[sourceIndex];

					if (
						sourceTarget &&
						moveTarget.groupId === sourceTarget.groupId &&
						(moveTarget.index === sourceTarget.index ||
							moveTarget.index === sourceTarget.index + 1)
					) {
						setText('');

						disableMovement();

						return;
					}

					dispatch({
						nodeId: source.ruleId,
						targetGroupId: moveTarget.groupId,
						targetIndex: moveTarget.index,
						type: 'MOVE_RULE',
					});
				}
			}
			else if (source.audiencesCriteria) {
				if (moveTarget.position === 'middle') {
					dispatch({
						audiencesCriteria: source.audiencesCriteria,
						targetId: moveTarget.nodeId,
						type: 'ADD_GROUP',
					});
				}
				else {
					dispatch({
						audiencesCriteria: source.audiencesCriteria,
						groupPath: moveTarget.groupPath,
						index: moveTarget.index,
						type: 'ADD_RULE',
					});
				}
			}

			setText(
				sub(Liferay.Language.get('x-placed-on-x-of-x'), [
					source.name,
					getPositionLabel(moveTarget.position),
					namesById[moveTarget.nodeId] ?? '',
				])
			);

			disableMovement();
		};

		const moveTo = (nextIndex: number) => {
			const moveTarget = targets[nextIndex];

			if (!moveTarget) {
				return;
			}

			setTarget(toMovementTarget(moveTarget));

			setText(
				sub(Liferay.Language.get('targeting-x-of-x'), [
					getPositionLabel(moveTarget.position),
					namesById[moveTarget.nodeId] ?? '',
				])
			);
		};

		const onKeyDown = (event: KeyboardEvent) => {
			event.preventDefault();
			event.stopPropagation();

			if (event.code === ARROW_DOWN_KEY_CODE) {
				moveTo(currentIndex + 1);
			}
			else if (event.code === ARROW_UP_KEY_CODE) {
				moveTo(currentIndex - 1);
			}
			else if (event.code === END_KEY_CODE) {
				moveTo(targets.length - 1);
			}
			else if (
				event.code === ENTER_KEY_CODE ||
				event.code === SPACE_KEY_CODE
			) {
				confirmPlacement();
			}
			else if (event.code === ESCAPE_KEY_CODE) {
				setText('');

				disableMovement();
			}
			else if (event.code === HOME_KEY_CODE) {
				moveTo(0);
			}
		};

		window.addEventListener('keydown', onKeyDown, true);

		return () => window.removeEventListener('keydown', onKeyDown, true);
	});

	return null;
}

function getPositionLabel(position: DropZone): string {
	if (position === 'middle') {
		return Liferay.Language.get('group');
	}

	if (position === DROP_POSITIONS.top) {
		return Liferay.Language.get('top');
	}

	return Liferay.Language.get('bottom');
}

function getInitialTarget(
	targets: MoveTarget[],
	sourceIndex: number
): MoveTarget {
	if (sourceIndex !== -1) {
		return targets[sourceIndex + 1] ?? targets[sourceIndex];
	}

	return targets[targets.length - 1];
}

function toMovementTarget(moveTarget: MoveTarget): MovementTarget {
	return {nodeId: moveTarget.nodeId, position: moveTarget.position};
}
