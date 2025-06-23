/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isEdge} from 'react-flow-renderer';

import type {Edge, Elements} from 'react-flow-renderer';

export function isIdDuplicated(elements: Elements, id: string) {
	let duplicated = false;

	elements.map((element) => {
		if (element.id === id) {
			duplicated = true;
		}
	});

	return duplicated;
}

export function isTransitionNameDuplicated(
	elements: Elements,
	selectedTransition: Edge,
	transitionName: string
) {
	const sameSourceTransitions = elements.filter(
		(element) =>
			isEdge(element) &&
			element.source === selectedTransition.source &&
			element.id !== selectedTransition.id
	);

	return sameSourceTransitions.some(
		(transition) => transition.data.name === transitionName
	);
}

export function getModalInfo(itemType: string) {
	if (itemType === 'actions') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-all-actions-and-their-settings'
			),
			title: Liferay.Language.get('delete-actions'),
		};
	}
	if (itemType === 'assignments') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-all-assignments-and-their-settings'
			),
			title: Liferay.Language.get('delete-assignments'),
		};
	}
	if (itemType === 'condition') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-condition-node'
			),
			title: Liferay.Language.get('delete-condition-node'),
		};
	}
	if (itemType === 'end') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-end-node'
			),
			title: Liferay.Language.get('delete-end-node'),
		};
	}
	if (itemType === 'fork') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-fork-node'
			),
			title: Liferay.Language.get('delete-fork-node'),
		};
	}
	if (itemType === 'join') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-join-node'
			),
			title: Liferay.Language.get('delete-join-node'),
		};
	}
	if (itemType === 'join-xor') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-join-xor-node'
			),
			title: Liferay.Language.get('delete-join-xor-node'),
		};
	}
	if (itemType === 'notifications') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-all-notifications-and-their-settings'
			),
			title: Liferay.Language.get('delete-notifications'),
		};
	}
	if (itemType === 'start') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-start-node'
			),
			title: Liferay.Language.get('delete-start-node'),
		};
	}
	if (itemType === 'state') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-state-node'
			),
			title: Liferay.Language.get('delete-state-node'),
		};
	}
	if (itemType === 'task') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-task-node'
			),
			title: Liferay.Language.get('delete-task-node'),
		};
	}
	if (itemType === 'timers') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-all-timers-and-their-settings'
			),
			title: Liferay.Language.get('delete-timers'),
		};
	}
	if (itemType === 'transition') {
		return {
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-the-selected-transition'
			),
			title: Liferay.Language.get('delete-transition'),
		};
	}

	return {};
}
