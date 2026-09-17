/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {useDragLayer} from 'react-dnd';

const layerStyles = {
	cursor: 'grabbing',
	height: '100%',
	left: 0,
	pointerEvents: 'none',
	position: 'fixed',
	top: 0,
	width: '100%',
	zIndex: 150,
};

function getItemStyles(initialOffset, currentOffset) {
	if (!initialOffset || !currentOffset) {
		return {
			display: 'none',
		};
	}

	const {x, y} = currentOffset;
	const transform = `translate(${x}px, ${y}px)`;

	return {
		WebkitTransform: transform,
		left: 0,
		position: 'absolute',
		top: 0,
		transform,
	};
}

const DragLayer = () => {
	const {currentOffset, initialOffset, isDragging, item} = useDragLayer(
		(monitor) => ({
			currentOffset: monitor.getSourceClientOffset(),
			initialOffset: monitor.getInitialSourceClientOffset(),
			isDragging: monitor.isDragging(),
			item: monitor.getItem(),
			itemType: monitor.getItemType(),
		})
	);

	if (!isDragging) {
		return null;
	}

	const {preview} = item;

	return (
		<div style={{...layerStyles, zIndex: 200}}>
			<div
				style={getItemStyles(initialOffset, currentOffset, isDragging)}
			>
				{preview && preview()}
			</div>
		</div>
	);
};

export default DragLayer;
