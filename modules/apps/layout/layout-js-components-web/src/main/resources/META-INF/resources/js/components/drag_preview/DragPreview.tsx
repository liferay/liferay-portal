/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {useDragLayer} from 'react-dnd';

import './DragPreview.scss';

import {debounce} from 'frontend-js-web';

import isNullOrUndefined from '../../utils/isNullOrUndefined';

type Alignment = {
	element: HTMLElement;
	position: 'bottom' | 'middle' | 'top';
	scrollElement?: HTMLElement;
};

interface DragItem {
	icon?: string;
	name?: string;
}

interface Props<T> {
	alignment?: Alignment;
	focusElement?: boolean;
	getIcon: (item: T) => string;
	getLabel: (item: T) => string;
}

const getItemStyles = ({
	alignment,
	currentOffset,
	previewRef,
	rtl,
}: {
	alignment?: Alignment;
	currentOffset: {x: number; y: number} | null;
	previewRef: React.RefObject<HTMLDivElement>;
	rtl: boolean;
}) => {

	// Do not display if data is not ready

	if ((!currentOffset && !alignment) || !previewRef.current) {
		return {
			display: 'none',
		};
	}

	let x;
	let y;

	const previewRect = previewRef.current.getBoundingClientRect();

	// Align to keyboard target

	if (alignment) {
		const targetRect = alignment.element.getBoundingClientRect();

		x = targetRect.x + targetRect.width / 2 - previewRect.width * 0.5;

		if (alignment.position === 'bottom') {
			y = targetRect.y + targetRect.height - previewRect.height * 0.5;
		}
		else if (alignment.position === 'top') {
			y = targetRect.y - previewRect.height * 0.5;
		}
		else {
			y = targetRect.y + targetRect.height / 2 - previewRect.height * 0.5;
		}
	}

	// Align to mouse

	else if (currentOffset) {
		x = rtl
			? currentOffset.x + previewRect.width * 0.5 - window.innerWidth
			: currentOffset.x - previewRect.width * 0.5;

		y = currentOffset.y - previewRect.height * 0.5;
	}

	const transform = `translate(${x}px, ${y}px)`;

	return {
		WebkitTransform: transform,
		transform,
	};
};

/**
 * Drag preview for both mouse DnD and keyboard movement.
 * It's aligned to the correct element if alignment is passed,
 * otherwise it's aligned to mouse
 */

export default function DragPreview<T extends DragItem>({
	alignment,
	focusElement,
	getIcon = (item) => item?.icon || '',
	getLabel = (item) => item?.name || Liferay.Language.get('element'),
}: Props<T>) {
	const ref = useRef<HTMLDivElement>(null);

	const {currentOffset, isDragging, item} = useDragLayer((monitor) => ({
		currentOffset: monitor.getClientOffset(),
		isDragging: monitor.isDragging(),
		item: monitor.getItem(),
	}));

	const icon = useMemo(() => getIcon(item), [getIcon, item]);
	const label = useMemo(() => getLabel(item), [getLabel, item]);

	const dir =
		Liferay.Language.direction[Liferay.ThemeDisplay.getLanguageId()];

	const [style, setStyle] = useState<React.CSSProperties>();

	// Focus drag preview if indicated

	useEffect(() => {
		if (focusElement) {
			ref.current?.focus();
		}
	}, [focusElement]);

	// Set styles during movement

	useEffect(() => {
		setStyle(
			getItemStyles({
				alignment,
				currentOffset,
				previewRef: ref,
				rtl: dir === 'rtl',
			})
		);
	}, [alignment, currentOffset, dir]);

	// Set styles on scroll when aligning to keyboard target

	useEffect(() => {
		const onScroll = debounce(() => {
			setStyle(
				getItemStyles({
					alignment,
					currentOffset,
					previewRef: ref,
					rtl: dir === 'rtl',
				})
			);
		}, 100);

		if (alignment?.scrollElement) {
			alignment.scrollElement.addEventListener('scroll', onScroll);
		}

		return () => {
			if (alignment?.scrollElement) {
				alignment.scrollElement.removeEventListener('scroll', onScroll);
			}
		};
	});

	// CKEditor allows you to drag text within the editor, so the drag preview
	// should not appear in this case.

	const isCKEditorText = !isNullOrUndefined(item) && 'text' in item;

	// Return if no movement is enabled or the selected element is text

	if ((!isDragging && !alignment) || (!icon && !label) || isCKEditorText) {
		return null;
	}

	return (
		<div className="cadmin">
			<div className="drag-preview position-fixed">
				<div
					aria-label={Liferay.Language.get('movement-preview')}
					className="align-items-center d-flex drag-preview__content p-2 position-absolute text-2"
					dir={dir}
					ref={ref}
					style={style}
					tabIndex={-1}
				>
					{icon && (
						<ClayIcon
							className="flex-shrink-0 mr-3 mt-0"
							symbol={icon}
						/>
					)}

					<span className="text-truncate">{label}</span>
				</div>
			</div>
		</div>
	);
}
