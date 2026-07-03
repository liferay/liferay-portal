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
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';
import {DropTargetMonitor, useDrop} from 'react-dnd';

import {DRAG_TYPES} from '../constants/dragTypes';
import {DROP_POSITIONS, DropPosition} from '../constants/dropPositions';
import {getOperatorLabel, getOperators} from '../constants/operators';
import {NavigationItemProps} from '../hooks/useKeyboardNavigation';
import {
	useMovementSource,
	useMovementTarget,
	useSetMovementSource,
} from '../keyboard_movement/KeyboardMovementContext';
import {AudiencesCriteria, Rule} from '../types';

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
	iconColor: string;
	index: number;
	items: DragItem[];
	navigationProps: NavigationItemProps;
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
): DropPosition | null => {
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
	iconColor,
	index,
	items,
	navigationProps,
	onAddRule,
	onChange,
	onDelete,
	onDuplicate,
	onReorder,
	rule,
}: IProps) {
	const dragHandlerRef = useRef<HTMLButtonElement>(null);
	const dropItemRef = useRef<HTMLDivElement | null>(null);

	const setRowRef = (element: HTMLDivElement | null) => {
		dropItemRef.current = element;

		navigationProps.ref(element);
	};

	const [dropPosition, setDropPosition] = useState<DropPosition | null>(null);

	const movementSource = useMovementSource();
	const movementTarget = useMovementTarget();
	const setMovementSource = useSetMovementSource();

	const isMovementSource = movementSource?.ruleId === rule.id;

	const isMovementTarget =
		Boolean(movementSource) && movementTarget.index === index;

	const isMovementTargetBottomPosition =
		isMovementTarget && movementTarget.position === DROP_POSITIONS.bottom;

	const isMovementTargetTopPosition =
		isMovementTarget && movementTarget.position === DROP_POSITIONS.top;

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
			let dropPosition: DropPosition | null = null;

			if (isOver) {
				dropPosition = getDropPosition(dropItemRef, monitor);
			}

			setDropPosition(dropPosition);
		},
	});

	if (!audiencesCriteria) {
		return (
			<ErrorRuleRow
				dropBottom={
					isDropBottomPosition || isMovementTargetBottomPosition
				}
				dropTop={isDropTopPosition || isMovementTargetTopPosition}
				onDelete={onDelete}
				onFocus={navigationProps.onFocus}
				onKeyDown={navigationProps.onKeyDown}
				rowRef={setRowRef}
				tabIndex={navigationProps.tabIndex}
			/>
		);
	}

	const {inputType, label, options, type} = audiencesCriteria;

	const operators = getOperators(inputType, type);

	return (
		<div ref={attributeDrop} role="none">
			<div
				aria-label={label}
				className={classNames(
					'align-items-center audience-builder-rule d-flex justify-content-between p-3',
					`audience-builder-rule--${iconColor}`,
					{
						'audience-builder-rule--dragging':
							isDragging || isMovementSource,
						'audience-builder-rule--drop-bottom':
							isDropBottomPosition ||
							(isOver && dropPosition === 'bottom') ||
							isMovementTargetBottomPosition,
						'audience-builder-rule--drop-top':
							isDropTopPosition ||
							(isOver && dropPosition === 'top') ||
							isMovementTargetTopPosition,
					}
				)}
				onFocus={navigationProps.onFocus}
				onKeyDown={navigationProps.onKeyDown}
				ref={setRowRef}
				role="menuitem"
				tabIndex={navigationProps.tabIndex}
			>
				<div className="align-items-center c-gap-3 d-flex">
					<ClayButtonWithIcon
						aria-label={sub(Liferay.Language.get('move-x'), label)}
						borderless
						className="audience-builder-grip text-secondary"
						displayType="secondary"
						onClick={(event) => {
							if (event.detail === 0) {
								setMovementSource({
									icon: audiencesCriteria.icon,
									name: label,
									ruleId: rule.id,
								});
							}
						}}
						ref={dragHandlerRef}
						size="sm"
						symbol="drag"
						tabIndex={navigationProps.tabIndex}
						title={sub(Liferay.Language.get('move-x'), label)}
					/>

					<span className="font-weight-semi-bold text-4 text-nowrap">
						{label}
					</span>

					<Picker
						aria-label={Liferay.Language.get('operator')}
						className="flex-shrink-0 form-control-sm w-auto"
						items={operators.map((operator) => ({
							label: getOperatorLabel(operator, inputType),
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

					<RuleValueField
						inputType={inputType}
						onChange={(value) => onChange({...rule, value})}
						options={options}
						type={type}
						value={rule.value}
					/>
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

interface RuleValueFieldProps {
	inputType: AudiencesCriteria['inputType'];
	onChange: (value: string) => void;
	options: AudiencesCriteria['options'];
	type: AudiencesCriteria['type'];
	value: string;
}

function RuleValueField({
	inputType,
	onChange,
	options,
	type,
	value,
}: RuleValueFieldProps) {
	if (options.length) {
		return (
			<Picker
				aria-label={Liferay.Language.get('value')}
				className="flex-shrink-0 form-control-sm w-auto"
				items={options}
				onSelectionChange={(key) => onChange(key as string)}
				selectedKey={value}
			>
				{(item) => <Option key={item.value}>{item.label}</Option>}
			</Picker>
		);
	}

	return (
		<ClayInput
			aria-label={Liferay.Language.get('value')}
			className="form-control-sm text-3"
			onChange={(event) => onChange(event.target.value)}
			placeholder={inputType === 'date' ? 'YYYY-MM-DD' : undefined}
			type={type === 'number' ? 'number' : 'text'}
			value={value}
		/>
	);
}

interface ErrorRuleRowProps {
	dropBottom: boolean;
	dropTop: boolean;
	onDelete: () => void;
	onFocus: (event: React.FocusEvent<HTMLDivElement>) => void;
	onKeyDown: (event: React.KeyboardEvent<HTMLDivElement>) => void;
	rowRef: (node: HTMLDivElement | null) => void;
	tabIndex: number;
}

function ErrorRuleRow({
	dropBottom,
	dropTop,
	onDelete,
	onFocus,
	onKeyDown,
	rowRef,
	tabIndex,
}: ErrorRuleRowProps) {
	return (
		<div
			aria-label={Liferay.Language.get(
				'the-criteria-is-no-longer-available'
			)}
			className={classNames(
				'align-items-center audience-builder-rule audience-builder-rule--error d-flex justify-content-between p-3',
				{
					'audience-builder-rule--drop-bottom': dropBottom,
					'audience-builder-rule--drop-top': dropTop,
				}
			)}
			onFocus={onFocus}
			onKeyDown={onKeyDown}
			ref={rowRef}
			role="menuitem"
			tabIndex={tabIndex}
		>
			<div className="align-items-center c-gap-3 d-flex">
				<ClayIcon className="text-danger" symbol="exclamation-full" />

				<span className="text-3">
					{Liferay.Language.get(
						'the-criteria-is-no-longer-available'
					)}
				</span>
			</div>

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
	);
}
