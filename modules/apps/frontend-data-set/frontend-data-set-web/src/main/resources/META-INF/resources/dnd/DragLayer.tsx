/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Badge from '@clayui/badge';
import React, {useContext} from 'react';
import {XYCoord, useDragLayer} from 'react-dnd';
import {NativeTypes} from 'react-dnd-html5-backend';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../FrontendDataSetContext';

const DragLayer = ({
	dataSetWrapperRef,
}: {
	dataSetWrapperRef: React.RefObject<HTMLDivElement>;
}) => {
	const {id}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const {currentOffset, isDragging, itemType} = useDragLayer((monitor) => ({
		currentOffset: monitor.getClientOffset(),
		isDragging: monitor.isDragging(),
		itemType: monitor.getItemType(),
	}));

	const isWithinView = (clientOffset: XYCoord | null) => {
		if (!dataSetWrapperRef || !dataSetWrapperRef.current || !clientOffset) {
			return false;
		}

		const viewRect = dataSetWrapperRef.current.getBoundingClientRect();

		return (
			clientOffset.x >= viewRect.left &&
			clientOffset.x <= viewRect.right &&
			clientOffset.y >= viewRect.top &&
			clientOffset.y <= viewRect.bottom
		);
	};

	function getItemStyles(currentOffset: XYCoord | null) {
		if (!currentOffset) {
			return {
				display: 'none',
			};
		}

		const {x, y} = currentOffset;

		const transform = `translate(${x + 20}px, ${y + 20}px)`;

		return {
			WebkitTransform: transform,
			transform,
		};
	}

	return isDragging &&
		itemType === NativeTypes.FILE &&
		isWithinView(currentOffset) ? (
		<div className="fds-file-drag-layer" id={`${id}-fds-file-drag-layer`}>
			<div style={getItemStyles(currentOffset)}>
				<Badge
					displayType="primary"
					label={Liferay.Language.get('drop-files-here-to-upload')}
				></Badge>
			</div>
		</div>
	) : null;
};

export default DragLayer;
