/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';
import {useDrag, useDrop} from 'react-dnd';
import {getEmptyImage} from 'react-dnd-html5-backend';

import {DRAG_TYPES, RowDragItem, RuleDragItem} from '../constants/dragTypes';
import {DROP_POSITIONS} from '../constants/dropPositions';
import {getOperatorLabel, getOperators} from '../constants/operators';
import {NavigationItemProps} from '../hooks/useKeyboardNavigation';
import {
	useMovementSource,
	useMovementTarget,
	useSetMovementSource,
} from '../keyboard_movement/KeyboardMovementContext';
import {AudiencesCriteria, Rule} from '../types';
import {DropZone, getDropPosition} from '../util/getDropPosition';

interface IProps {
	audiencesCriteria?: AudiencesCriteria;
	canGroup: boolean;
	iconColor: string;
	index: number;
	navigationProps?: NavigationItemProps;
	onAddRule: (audiencesCriteria: AudiencesCriteria, index?: number) => void;
	onChange: (rule: Rule) => void;
	onDelete: () => void;
	onDuplicate: () => void;
	onGroup: (audiencesCriteria: AudiencesCriteria) => void;
	onMoveGroup: (nodeId: string) => void;
	onMoveRule: (nodeId: string, index: number) => void;
	rule: Rule;
}

export default function RuleRow({
	audiencesCriteria,
	canGroup,
	iconColor,
	index,
	navigationProps,
	onAddRule,
	onChange,
	onDelete,
	onDuplicate,
	onGroup,
	onMoveGroup,
	onMoveRule,
	rule,
}: IProps) {
	const dropItemRef = useRef<HTMLDivElement | null>(null);

	const [dropPosition, setDropPosition] = useState<DropZone | null>(null);

	const movementSource = useMovementSource();
	const movementTarget = useMovementTarget();
	const setMovementSource = useSetMovementSource();

	const isMovementSource = movementSource?.ruleId === rule.id;

	const isMovementTarget =
		Boolean(movementSource) && movementTarget.nodeId === rule.id;

	const isMovementTargetBottom =
		isMovementTarget && movementTarget.position === DROP_POSITIONS.bottom;

	const isMovementTargetGroup =
		isMovementTarget && movementTarget.position === 'middle';

	const isMovementTargetTop =
		isMovementTarget && movementTarget.position === DROP_POSITIONS.top;

	const [{isDragging}, dragRef, dragPreviewRef] = useDrag<
		RuleDragItem,
		void,
		{isDragging: boolean}
	>({
		collect: (monitor) => ({isDragging: monitor.isDragging()}),
		item: {id: rule.id, type: DRAG_TYPES.RULE},
	});

	useEffect(() => {
		dragPreviewRef(getEmptyImage(), {captureDraggingState: true});
	}, [dragPreviewRef]);

	useEffect(() => {
		dropItemRef.current
			?.querySelectorAll<HTMLElement>('[role="combobox"]')
			.forEach((element) =>
				element.setAttribute(
					'tabindex',
					String(navigationProps?.tabIndex ?? 0)
				)
			);
	});

	const [{isOver}, dropRef] = useDrop<RowDragItem, void, {isOver: boolean}>({
		accept: [DRAG_TYPES.ATTRIBUTE, DRAG_TYPES.RULE],
		canDrop: (item) => !('id' in item) || item.id !== rule.id,
		collect: (monitor) => ({
			isOver: monitor.isOver() && monitor.canDrop(),
		}),
		drop: (item, monitor) => {
			const dropZone = getDropPosition(dropItemRef, monitor, {canGroup});

			const insertIndex =
				dropZone === DROP_POSITIONS.bottom ? index + 1 : index;

			if ('audiencesCriteria' in item) {
				if (dropZone === 'middle') {
					onGroup(item.audiencesCriteria);
				}
				else {
					onAddRule(item.audiencesCriteria, insertIndex);
				}
			}
			else if (dropZone === 'middle') {
				onMoveGroup(item.id);
			}
			else {
				onMoveRule(item.id, insertIndex);
			}
		},
		hover: (_item, monitor) => {
			setDropPosition(
				monitor.canDrop()
					? getDropPosition(dropItemRef, monitor, {canGroup})
					: null
			);
		},
	});

	const setRowRef = (element: HTMLDivElement | null) => {
		dropItemRef.current = element;

		dropRef(element);

		navigationProps?.ref(element);
	};

	if (!audiencesCriteria) {
		return (
			<ErrorRuleRow
				dropBottom={
					(isOver && dropPosition === DROP_POSITIONS.bottom) ||
					isMovementTargetBottom
				}
				dropTop={
					(isOver && dropPosition === DROP_POSITIONS.top) ||
					isMovementTargetTop
				}
				navigationProps={navigationProps}
				nodeId={rule.id}
				onDelete={onDelete}
				rowRef={setRowRef}
			/>
		);
	}

	const {inputType, label, options, type} = audiencesCriteria;

	const operators = getOperators(inputType, type);

	return (
		<div
			aria-label={label}
			className={classNames(
				'align-items-center audience-builder-rule d-flex justify-content-between p-3',
				`audience-builder-rule--${iconColor}`,
				{
					'audience-builder-rule--dragging':
						isDragging || isMovementSource,
					'audience-builder-rule--drop-bottom':
						(isOver && dropPosition === DROP_POSITIONS.bottom) ||
						isMovementTargetBottom,
					'audience-builder-rule--drop-group':
						(isOver && dropPosition === 'middle') ||
						isMovementTargetGroup,
					'audience-builder-rule--drop-top':
						(isOver && dropPosition === DROP_POSITIONS.top) ||
						isMovementTargetTop,
				}
			)}
			data-keyboard-movement-id={rule.id}
			onFocus={navigationProps?.onFocus}
			onKeyDown={navigationProps?.onKeyDown}
			ref={setRowRef}
			role="menuitem"
			tabIndex={navigationProps?.tabIndex ?? 0}
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
					ref={(element) => {
						dragRef(element);
					}}
					size="sm"
					symbol="drag"
					tabIndex={navigationProps?.tabIndex ?? 0}
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
					{(item) => <Option key={item.value}>{item.label}</Option>}
				</Picker>

				<RuleValueField
					inputType={inputType}
					onChange={(value) => onChange({...rule, value})}
					options={options}
					tabIndex={navigationProps?.tabIndex ?? 0}
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
					tabIndex={navigationProps?.tabIndex ?? 0}
					title={Liferay.Language.get('duplicate')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('delete')}
					borderless
					displayType="secondary"
					onClick={onDelete}
					size="sm"
					symbol="times-circle"
					tabIndex={navigationProps?.tabIndex ?? 0}
					title={Liferay.Language.get('delete')}
				/>
			</div>
		</div>
	);
}

interface RuleValueFieldProps {
	inputType: AudiencesCriteria['inputType'];
	onChange: (value: string) => void;
	options: AudiencesCriteria['options'];
	tabIndex: number;
	type: AudiencesCriteria['type'];
	value: string;
}

function RuleValueField({
	inputType,
	onChange,
	options,
	tabIndex,
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
			tabIndex={tabIndex}
			type={type === 'number' ? 'number' : 'text'}
			value={value}
		/>
	);
}

interface ErrorRuleRowProps {
	dropBottom: boolean;
	dropTop: boolean;
	navigationProps?: NavigationItemProps;
	nodeId: string;
	onDelete: () => void;
	rowRef: (node: HTMLDivElement | null) => void;
}

function ErrorRuleRow({
	dropBottom,
	dropTop,
	navigationProps,
	nodeId,
	onDelete,
	rowRef,
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
			data-keyboard-movement-id={nodeId}
			onFocus={navigationProps?.onFocus}
			onKeyDown={navigationProps?.onKeyDown}
			ref={rowRef}
			role="menuitem"
			tabIndex={navigationProps?.tabIndex ?? 0}
		>
			<div className="align-items-center c-gap-3 d-flex">
				<ClayIcon className="text-danger" symbol="times-circle-full" />

				<span className="font-weight-semi-bold text-4 text-danger">
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
				tabIndex={navigationProps?.tabIndex ?? 0}
				title={Liferay.Language.get('delete')}
			/>
		</div>
	);
}
