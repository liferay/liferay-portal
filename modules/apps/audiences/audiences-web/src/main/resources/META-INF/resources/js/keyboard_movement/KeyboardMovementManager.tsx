/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import {Dispatch, useEffect} from 'react';

import {DROP_POSITIONS} from '../constants/dropPositions';
import {
	ARROW_DOWN_KEY_CODE,
	ARROW_UP_KEY_CODE,
	END_KEY_CODE,
	ENTER_KEY_CODE,
	ESCAPE_KEY_CODE,
	HOME_KEY_CODE,
} from '../constants/keyboardCodes';
import {Action} from '../reducer';
import {CriteriaNode} from '../types';
import {
	MovementSource,
	MovementTarget,
	useDisableKeyboardMovement,
	useMovementTarget,
	useSetMovementTarget,
	useSetMovementText,
} from './KeyboardMovementContext';

const ACTION_TYPES = {
	add: 'add',
	move: 'move',
} as const;

const DIRECTIONS = {
	down: 'down',
	up: 'up',
} as const;

type Direction = keyof typeof DIRECTIONS;

export interface MovementItem {
	icon: string;
	id: string;
	name: string;
}

interface Props {
	dispatch: Dispatch<Action>;
	items: MovementItem[];
	nodes: CriteriaNode[];
	source: MovementSource;
}

export default function KeyboardMovementManager({
	dispatch,
	items,
	nodes,
	source,
}: Props) {
	const disableMovement = useDisableKeyboardMovement();
	const setTarget = useSetMovementTarget();
	const setText = useSetMovementText();
	const target = useMovementTarget();

	useEffect(() => {
		if (target.index !== null) {
			return;
		}

		if (!items.length) {
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

		setTarget(getInitialTarget(source, nodes));

		setText(
			Liferay.Language.get(
				'use-arrows-to-move-it-and-press-enter-to-select-the-new-position-press-esc-to-cancel'
			)
		);
	}, [
		disableMovement,
		dispatch,
		items,
		nodes,
		setTarget,
		setText,
		source,
		target,
	]);

	useEffect(() => {
		const executeAction = () => {
			if (target.index === null || !target.position) {
				return;
			}

			const insertionIndex =
				target.position === DROP_POSITIONS.bottom
					? target.index + 1
					: target.index;

			const actionType = source.ruleId
				? ACTION_TYPES.move
				: ACTION_TYPES.add;

			if (actionType === ACTION_TYPES.add) {
				if (!source.audiencesCriteria) {
					return;
				}

				dispatch({
					audiencesCriteria: source.audiencesCriteria,
					index: insertionIndex,
					type: 'ADD_RULE',
				});
			}
			else {
				const sourceIndex = nodes.findIndex(
					(node) => node.id === source.ruleId
				);

				if (sourceIndex === -1) {
					disableMovement();

					return;
				}

				if (
					insertionIndex === sourceIndex ||
					insertionIndex === sourceIndex + 1
				) {
					setText('');

					disableMovement();

					return;
				}

				const nextNodes = [...nodes];

				const [movedNode] = nextNodes.splice(sourceIndex, 1);

				nextNodes.splice(
					insertionIndex > sourceIndex
						? insertionIndex - 1
						: insertionIndex,
					0,
					movedNode
				);

				dispatch({items: nextNodes, type: 'REORDER_RULES'});
			}

			setText(
				sub(Liferay.Language.get('x-placed-on-x-of-x'), [
					source.name,
					target.position,
					items[target.index].name,
				])
			);

			disableMovement();
		};

		const moveTarget = (nextTarget: MovementTarget | null) => {
			if (!nextTarget || nextTarget.index === null) {
				return;
			}

			setTarget(nextTarget);

			setText(
				sub(Liferay.Language.get('targeting-x-of-x'), [
					nextTarget.position,
					items[nextTarget.index].name,
				])
			);
		};

		const onKeyDown = (event: KeyboardEvent) => {
			event.preventDefault();
			event.stopPropagation();

			if (event.code === ARROW_DOWN_KEY_CODE) {
				moveTarget(
					getNextTarget(target, DIRECTIONS.down, items.length)
				);
			}
			else if (event.code === ARROW_UP_KEY_CODE) {
				moveTarget(getNextTarget(target, DIRECTIONS.up, items.length));
			}
			else if (event.code === END_KEY_CODE) {
				moveTarget({
					index: items.length - 1,
					position: DROP_POSITIONS.bottom,
				});
			}
			else if (event.code === ENTER_KEY_CODE) {
				executeAction();
			}
			else if (event.code === ESCAPE_KEY_CODE) {
				setText('');

				disableMovement();
			}
			else if (event.code === HOME_KEY_CODE) {
				moveTarget({index: 0, position: DROP_POSITIONS.top});
			}
		};

		window.addEventListener('keydown', onKeyDown, true);

		return () => window.removeEventListener('keydown', onKeyDown, true);
	});

	return null;
}

export function getInitialTarget(
	source: MovementSource,
	nodes: CriteriaNode[]
): MovementTarget {
	if (source.ruleId) {
		return {
			index: nodes.findIndex((node) => node.id === source.ruleId),
			position: DROP_POSITIONS.bottom,
		};
	}

	return {index: nodes.length - 1, position: DROP_POSITIONS.bottom};
}

export function getNextTarget(
	target: MovementTarget,
	direction: Direction,
	itemsCount: number
): MovementTarget | null {
	const {index, position} = target;

	if (index === null || !position) {
		return null;
	}

	if (direction === DIRECTIONS.down) {
		if (position === DROP_POSITIONS.top) {
			return {index, position: DROP_POSITIONS.bottom};
		}

		if (index < itemsCount - 1) {
			return {index: index + 1, position: DROP_POSITIONS.bottom};
		}

		return null;
	}

	if (position === DROP_POSITIONS.bottom) {
		if (index === 0) {
			return {index: 0, position: DROP_POSITIONS.top};
		}

		return {index: index - 1, position: DROP_POSITIONS.bottom};
	}

	return null;
}
