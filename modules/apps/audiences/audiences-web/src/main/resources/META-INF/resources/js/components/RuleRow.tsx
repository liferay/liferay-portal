/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useDragAndDrop} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {useRef, useState} from 'react';
import {DropTargetMonitor, useDrop} from 'react-dnd';

import {DRAG_TYPES} from '../constants/dragTypes';
import {getOperatorLabel} from '../constants/operators';
import {AudiencesCriteria, Rule} from '../types';

type DropPosition = 'bottom' | 'top' | null;

interface DragItem {
	icon: string;
	id: string;
	name: string;
}

interface AttributeDragItem {
	audiencesCriteria: AudiencesCriteria;
	type: string;
}

interface IProps {
	audiencesCriteria?: AudiencesCriteria;
	index: number;
	items: DragItem[];
	onAddRule: (audiencesCriteria: AudiencesCriteria, index?: number) => void;
	onChange: (rule: Rule) => void;
	onDelete: () => void;
	onDuplicate: () => void;
	onReorder: (items: DragItem[]) => void;
	rule: Rule;
}

const getDropPosition = (
	ref: React.RefObject<HTMLElement>,
	monitor: DropTargetMonitor
): DropPosition => {
	const clientOffset = monitor.getClientOffset();

	if (!ref.current || !clientOffset) {
		return null;
	}

	const dropItemBoundingRect = ref.current.getBoundingClientRect();
	const hoverClientY = clientOffset.y - dropItemBoundingRect.top;

	return hoverClientY < dropItemBoundingRect.height / 2 ? 'top' : 'bottom';
};

export default function RuleRow({
	audiencesCriteria,
	index,
	items,
	onAddRule,
	onChange,
	onDelete,
	onDuplicate,
	onReorder,
	rule,
}: IProps) {
	const dragHandlerRef = useRef<HTMLSpanElement>(null);
	const dropItemRef = useRef<HTMLDivElement | null>(null);

	const [dropPosition, setDropPosition] = useState<DropPosition>(null);

	const {isDragging, isDropBottomPosition, isDropTopPosition} =
		useDragAndDrop<DragItem>({
			dragHandlerRef,
			dropItemRef,
			item: items[index],
			itemIndex: index,
			items,
			onDrop: onReorder,
		});

	const [{isOver}, attributeDrop] = useDrop<
		AttributeDragItem,
		void,
		{isOver: boolean}
	>({
		accept: DRAG_TYPES.ATTRIBUTE,
		collect: (monitor) => ({isOver: !!monitor.isOver()}),
		drop: (item, monitor) => {
			const dropPosition = getDropPosition(dropItemRef, monitor);

			onAddRule(
				item.audiencesCriteria,
				dropPosition === 'bottom' ? index + 1 : index
			);
		},
		hover: (item, monitor) => {
			let dropPosition: DropPosition = null;

			if (isOver) {
				dropPosition = getDropPosition(dropItemRef, monitor);
			}

			setDropPosition(dropPosition);
		},
	});

	if (!audiencesCriteria) {
		return null;
	}

	const {label, operators, options, type} = audiencesCriteria;

	return (
		<div className="mb-3" ref={attributeDrop}>
			<div
				className={classNames(
					'align-items-center audience-builder-rule d-flex justify-content-between p-3',
					{
						'audience-builder-rule--dragging': isDragging,
						'audience-builder-rule--drop-bottom':
							isDropBottomPosition ||
							(isOver && dropPosition === 'bottom'),
						'audience-builder-rule--drop-top':
							isDropTopPosition ||
							(isOver && dropPosition === 'top'),
					}
				)}
				ref={dropItemRef}
			>
				<div className="align-items-center c-gap-3 d-flex">
					<span
						aria-label={Liferay.Language.get('drag')}
						className="audience-builder-grip text-secondary"
						ref={dragHandlerRef}
						title={Liferay.Language.get('drag')}
					>
						<ClayIcon symbol="drag" />
					</span>

					<span className="font-weight-semi-bold text-4 text-nowrap">
						{label}
					</span>

					<Picker
						aria-label={Liferay.Language.get('operator')}
						className="flex-shrink-0 form-control-sm w-auto"
						items={operators.map((operator) => ({
							label: getOperatorLabel(operator, type),
							value: operator,
						}))}
						onSelectionChange={(key) =>
							onChange({...rule, operator: key as string})
						}
						selectedKey={rule.operator}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>

					{options.length ? (
						<Picker
							aria-label={Liferay.Language.get('value')}
							className="flex-shrink-0 form-control-sm w-auto"
							items={options}
							onSelectionChange={(key) =>
								onChange({...rule, value: key as string})
							}
							selectedKey={rule.value}
						>
							{(item) => (
								<Option key={item.value}>{item.label}</Option>
							)}
						</Picker>
					) : (
						<ClayInput
							aria-label={Liferay.Language.get('value')}
							className="form-control-sm text-3"
							onChange={(event) =>
								onChange({...rule, value: event.target.value})
							}
							placeholder={
								type === 'date' ? 'YYYY-MM-DD' : undefined
							}
							type={type === 'number' ? 'number' : 'text'}
							value={rule.value}
						/>
					)}
				</div>

				<div className="align-items-baseline d-flex">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('duplicate')}
						borderless
						displayType="secondary"
						onClick={onDuplicate}
						size="sm"
						symbol="copy"
						title={Liferay.Language.get('duplicate')}
					/>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('delete')}
						borderless
						displayType="secondary"
						onClick={onDelete}
						size="sm"
						symbol="times-circle"
						title={Liferay.Language.get('delete')}
					/>
				</div>
			</div>
		</div>
	);
}
