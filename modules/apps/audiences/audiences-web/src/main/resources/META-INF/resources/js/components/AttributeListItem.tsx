/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect} from 'react';
import {useDrag} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import {DRAG_TYPES} from '../constants/dragTypes';
import {NavigationItemProps} from '../hooks/useKeyboardNavigation';
import {
	useMovementSource,
	useSetMovementSource,
} from '../keyboard_movement/KeyboardMovementContext';
import {AudiencesCriteria} from '../types';

interface IProps {
	audiencesCriteria: AudiencesCriteria;
	iconColor: string;
	navigationProps: NavigationItemProps;
}

export default function AttributeListItem({
	audiencesCriteria,
	iconColor,
	navigationProps,
}: IProps) {
	const [{isDragging}, handlerRef, previewRef] = useDrag({
		collect: (monitor) => ({
			isDragging: monitor.isDragging(),
		}),
		item: {
			audiencesCriteria,
			icon: audiencesCriteria.icon,
			name: audiencesCriteria.label,
			type: DRAG_TYPES.ATTRIBUTE,
		},
	});

	const movementSource = useMovementSource();
	const setMovementSource = useSetMovementSource();

	const isMovementSource =
		!movementSource?.ruleId &&
		movementSource?.audiencesCriteria?.key === audiencesCriteria.key;

	useEffect(() => {
		previewRef(getEmptyImage(), {captureDraggingState: true});
	}, [previewRef]);

	const setRefs = (element: HTMLDivElement | null) => {
		handlerRef(element);

		navigationProps.ref(element);
	};

	return (
		<div
			aria-label={sub(
				Liferay.Language.get('add-x'),
				audiencesCriteria.label
			)}
			className={classNames(
				'align-items-center audience-builder-attribute c-gap-3 d-flex px-2 rounded',
				{
					'audience-builder-attribute--dragging':
						isDragging || isMovementSource,
				}
			)}
			onFocus={navigationProps.onFocus}
			onKeyDown={(event) => {
				if (event.key === 'Enter' || event.key === ' ') {
					event.preventDefault();

					setMovementSource({
						audiencesCriteria,
						icon: audiencesCriteria.icon,
						name: audiencesCriteria.label,
					});

					return;
				}

				navigationProps.onKeyDown(event);
			}}
			ref={setRefs}
			role="menuitem"
			tabIndex={navigationProps.tabIndex}
		>
			<span
				className={classNames(
					'align-items-center audience-builder-attribute__icon d-inline-flex justify-content-center rounded',
					`audience-builder-attribute__icon--${iconColor}`
				)}
			>
				<ClayIcon symbol={audiencesCriteria.icon} />
			</span>

			<span className="text-3 text-truncate">
				{audiencesCriteria.label}
			</span>
		</div>
	);
}
