/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {Resizer} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useSessionState} from 'frontend-js-components-web';
import {sub, throttle} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';
import {useDrop} from 'react-dnd';

import MillerColumnsItem from './MillerColumnsItem';
import {ACCEPTING_TYPES} from './constants/acceptingTypes';

const AUTOSCROLL_DELAY = 20;
const AUTOSCROLL_DISTANCE = 20;
const AUTOSCROLL_RANGE_LENGTH = 20;

const COLUMN_MAX_WIDTH = 672;
const COLUMN_MIN_WIDTH = 286;
const COLUMN_WIDTH_RESIZE_STEP = 20;

const scroll = (columnsContainer, monitor) => {
	const clientOffset = monitor.getClientOffset();
	const containerRect = columnsContainer.current.getBoundingClientRect();

	const hoverClientX = containerRect.right - clientOffset?.x;

	if (hoverClientX < AUTOSCROLL_RANGE_LENGTH) {
		columnsContainer.current.scrollLeft += AUTOSCROLL_DISTANCE;
	}
	else if (hoverClientX > containerRect.width - AUTOSCROLL_RANGE_LENGTH) {
		columnsContainer.current.scrollLeft -= AUTOSCROLL_DISTANCE;
	}
};

const throttledScroll = throttle(scroll, AUTOSCROLL_DELAY);

const isValidTarget = (sources, parent) =>
	!sources.some(
		(source) =>
			!(
				parent &&
				(source.columnIndex > parent.columnIndex + 1 ||
					(source.columnIndex === parent.columnIndex + 1 &&
						source.parentId !== parent.id) ||
					(parent.parentable &&
						source.columnIndex <= parent.columnIndex &&
						!source.active))
			)
	);

const MillerColumnsColumn = ({
	createPageTemplateURL,
	getPageTemplateCollectionsURL,
	getItemActionsURL,
	columnItems = [],
	columnsContainer,
	isLayoutSetPrototype,
	items,
	namespace,
	onItemDrop,
	getItemChildren,
	index,
	rtl,
}) => {
	const ref = useRef();
	const lastColumnItem = columnItems[columnItems.length - 1];

	const [{canDrop}, drop] = useDrop({
		accept: ACCEPTING_TYPES.ITEM,
		canDrop(source, monitor) {
			return (
				monitor.isOver({shallow: true}) &&
				isValidTarget(source.items, lastColumnItem)
			);
		},
		collect: (monitor) => ({
			canDrop: !!monitor.canDrop(),
		}),
		drop(source) {
			if (canDrop) {
				onItemDrop(source.items, lastColumnItem);
			}
		},
		hover(source, monitor) {
			if (Liferay.Browser.isSafari() && !Liferay.Browser.isChrome()) {
				throttledScroll(columnsContainer, monitor);
			}
		},
	});

	useEffect(() => {
		drop(ref);
	}, [drop]);

	const [columnWidth, setColumnWidth] = useSessionState(
		`${namespace}_column-width-${index}`,
		0
	);

	const sizeProps = columnWidth
		? {
				style: {
					maxWidth: `${columnWidth}px`,
					minWidth: `${columnWidth}px`,
					width: `${columnWidth}px`,
				},
			}
		: {lg: '4', md: '6', size: '11'};

	return (
		<>
			<ClayLayout.Col
				className={classNames(
					'miller-columns-col show-quick-actions-on-hover',
					{
						'drop-target': canDrop,
					}
				)}
				containerElement="ul"
				id={`miller-columns-list-${columnItems[0]?.parentId}`}
				ref={ref}
				role="menu"
				{...sizeProps}
			>
				{columnItems.map((item) => (
					<MillerColumnsItem
						createPageTemplateURL={createPageTemplateURL}
						getItemActionsURL={getItemActionsURL}
						getItemChildren={getItemChildren}
						getPageTemplateCollectionsURL={
							getPageTemplateCollectionsURL
						}
						isLayoutSetPrototype={isLayoutSetPrototype}
						item={item}
						items={items}
						key={item.key}
						namespace={namespace}
						onItemDrop={onItemDrop}
						rtl={rtl}
					/>
				))}
			</ClayLayout.Col>

			<Resizer
				ariaLabel={sub(
					Liferay.Language.get('resize-column-x'),
					index + 1
				)}
				id={`resize-${index}`}
				maxWidth={COLUMN_MAX_WIDTH}
				minWidth={COLUMN_MIN_WIDTH}
				resizeStep={COLUMN_WIDTH_RESIZE_STEP}
				setWidth={setColumnWidth}
				tabIndex={-1}
				targetRef={ref}
				width={columnWidth}
			/>
		</>
	);
};

export default MillerColumnsColumn;
