/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {useEffect} from 'react';
import {useDrag} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import {DRAG_TYPES} from '../constants/dragTypes';
import {AudiencesCriteria} from '../types';

interface IProps {
	audiencesCriteria: AudiencesCriteria;
}

export default function AttributeListItem({audiencesCriteria}: IProps) {
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

	useEffect(() => {
		previewRef(getEmptyImage(), {captureDraggingState: true});
	}, [previewRef]);

	return (
		<div
			className={classNames(
				'align-items-center audience-builder-attribute d-flex p-2',
				{
					'audience-builder-attribute--dragging': isDragging,
				}
			)}
			ref={handlerRef}
		>
			<ClayIcon
				className="audience-builder-attribute__grip mr-2 text-secondary"
				symbol="drag"
			/>

			<ClayIcon className="mr-2" symbol={audiencesCriteria.icon} />

			<span className="text-3 text-truncate">
				{audiencesCriteria.label}
			</span>
		</div>
	);
}
