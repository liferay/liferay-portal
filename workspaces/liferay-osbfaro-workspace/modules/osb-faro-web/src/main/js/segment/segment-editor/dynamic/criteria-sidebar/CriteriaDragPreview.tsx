import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';
import {DragTypes} from '../utils/drag-types';
import {getStickerStyle} from './stickerColors';
import {getTypeIcon} from './CriteriaSidebarItem';
import {Property} from 'shared/util/records';
import {useDragLayer} from 'react-dnd';

export default function CriteriaDragPreview() {
	const {currentOffset, property} = useDragLayer((monitor) => {
		const draggingProperty =
			monitor.isDragging() &&
			monitor.getItemType() === DragTypes.Property;

		return {
			currentOffset: draggingProperty ? monitor.getClientOffset() : null,
			property: draggingProperty
				? (monitor.getItem().property as Property)
				: null,
		};
	});

	if (!currentOffset || !property) {
		return null;
	}

	const {label, propertyKey, type} = property;

	return (
		<div className="criteria-drag-preview position-fixed">
			<div
				className="align-items-center bg-white c-gap-3 criteria-drag-preview-content d-flex pl-2 pr-3 py-2 text-3"
				data-testid="criteria-drag-preview"
				style={{
					transform: `translate(${currentOffset.x}px, ${currentOffset.y}px) translate(-16px, -50%)`,
				}}
			>
				<ClaySticker
					className="criteria-sidebar-item-sticker flex-shrink-0"
					size="sm"
					style={getStickerStyle(propertyKey)}
				>
					<ClayIcon symbol={getTypeIcon(type)} />
				</ClaySticker>

				<span className="text-truncate">{label}</span>
			</div>
		</div>
	);
}
