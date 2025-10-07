/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	MutableRefObject,
	RefObject,
	useCallback,
	useContext,
	useEffect,
	useRef,
} from 'react';
import {type DropTargetMonitor, useDrop} from 'react-dnd';
import {NativeTypes} from 'react-dnd-html5-backend';

import DnDContext from '../DnDContext';
import isFileDropEnabled from '../utils/isFileDropEnabled';

const dropTargetClass: string = 'drop-target';

const useFDSDrop = ({
	item,
	targetDropRef,
	targetDropRefQuerySelector,
}: {
	item?: any;
	targetDropRef?: RefObject<HTMLElement>;
	targetDropRefQuerySelector?: string;
}) => {
	const {fileDropSettings, handleFileDrop} = useContext(DnDContext);

	const targetDropElementRef: MutableRefObject<HTMLElement | null> =
		useRef<HTMLElement>(null);

	const nonDroppableRef: MutableRefObject<null> = useRef(null);

	const isDropTarget = useCallback(
		(item?: any) => {
			if (!item) {
				return true;
			}

			return fileDropSettings?.isDropTarget
				? fileDropSettings.isDropTarget({item})
				: true;
		},
		[fileDropSettings]
	);

	const [{isOverCurrent}, dropRef] = useDrop({
		accept: isFileDropEnabled(fileDropSettings) ? [NativeTypes.FILE] : [],
		canDrop() {
			return isFileDropEnabled(fileDropSettings) && isDropTarget(item);
		},
		collect: (monitor: DropTargetMonitor) => {
			return {
				isOverCurrent:
					isFileDropEnabled(fileDropSettings) &&
					isDropTarget(item) &&
					monitor.isOver({shallow: true}),
			};
		},
		drop(fileItem: any, monitor) {
			if (monitor.isOver({shallow: true})) {
				if (targetDropRefQuerySelector && targetDropElementRef) {
					targetDropElementRef.current?.classList.remove(
						dropTargetClass
					);
				}

				handleFileDrop?.(fileItem, item);
			}
		},
	});

	useEffect(() => {
		if (
			targetDropRef &&
			targetDropRef.current &&
			isDropTarget(item) &&
			isFileDropEnabled(fileDropSettings)
		) {
			dropRef(targetDropRef);

			if (targetDropRefQuerySelector) {
				targetDropElementRef.current =
					targetDropRef.current?.querySelector(
						targetDropRefQuerySelector
					);
			}
		}
	}, [
		isDropTarget,
		dropRef,
		item,
		fileDropSettings,
		targetDropRef,
		targetDropRefQuerySelector,
	]);

	useEffect(() => {
		if (!targetDropRefQuerySelector) {
			return;
		}

		if (isOverCurrent) {
			targetDropElementRef?.current?.classList.add(dropTargetClass);
		}
		else {
			targetDropElementRef?.current?.classList.remove(dropTargetClass);
		}
	}, [isOverCurrent, fileDropSettings, targetDropRefQuerySelector]);

	return {
		className: isOverCurrent ? dropTargetClass : '',
		dropRef: isDropTarget(item) ? dropRef : nonDroppableRef,
		isOverCurrent,
	};
};

export default useFDSDrop;
