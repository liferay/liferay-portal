/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {Dispatch, Fragment, useRef, useState} from 'react';
import {ConnectDropTarget, useDrop} from 'react-dnd';

import {
	CATEGORY_ICON_COLORS,
	DEFAULT_ICON_COLOR,
} from '../constants/categoryIconColors';
import {
	AttributeDragItem,
	DRAG_TYPES,
	RowDragItem,
} from '../constants/dragTypes';
import {DROP_POSITIONS} from '../constants/dropPositions';
import useKeyboardNavigation, {
	NavigationItemProps,
} from '../hooks/useKeyboardNavigation';
import {useMovementSource} from '../keyboard_movement/KeyboardMovementContext';
import KeyboardMovementManager, {
	MovementItem,
} from '../keyboard_movement/KeyboardMovementManager';
import {Action} from '../reducer';
import {
	AudiencesCriteria,
	AudiencesCriteriaType,
	CriteriaNode,
	Group,
} from '../types';
import {DropZone, getDropPosition} from '../util/dropPosition';
import {canGroupNode, isGroup} from '../util/tree';
import RuleRow from './RuleRow';

interface IProps {
	audiencesCriteriaTypes: AudiencesCriteriaType[];
	dispatch: Dispatch<Action>;
	root: Group;
}

interface RenderContext {
	announce: (message: string) => void;
	audiencesCriteriasByKey: Record<string, AudiencesCriteria>;
	dispatch: Dispatch<Action>;
	getItemProps: (index: number) => NavigationItemProps;
	iconColorsByKey: Record<string, string>;
}

function toMovementItems(
	items: CriteriaNode[],
	audiencesCriteriasByKey: Record<string, AudiencesCriteria>
): MovementItem[] {
	return items.map((node) => {
		if (isGroup(node)) {
			return {
				icon: 'folder',
				id: node.id,
				name: Liferay.Language.get('group'),
			};
		}

		const audiencesCriteria = audiencesCriteriasByKey[node.attribute];

		return {
			icon: audiencesCriteria?.icon ?? '',
			id: node.id,
			name: audiencesCriteria?.label ?? node.attribute,
		};
	});
}

export default function ConditionsPanel({
	audiencesCriteriaTypes,
	dispatch,
	root,
}: IProps) {
	const audiencesCriterias = audiencesCriteriaTypes.flatMap(
		(audiencesCriteriaType) => audiencesCriteriaType.audiencesCriterias
	);

	const audiencesCriteriasByKey: Record<string, AudiencesCriteria> =
		Object.fromEntries(
			audiencesCriterias.map((audiencesCriteria) => [
				audiencesCriteria.key,
				audiencesCriteria,
			])
		);

	const iconColorsByKey: Record<string, string> = Object.fromEntries(
		audiencesCriteriaTypes.flatMap((audiencesCriteriaType) =>
			audiencesCriteriaType.audiencesCriterias.map(
				(audiencesCriteria) => [
					audiencesCriteria.key,
					CATEGORY_ICON_COLORS[audiencesCriteriaType.key] ??
						DEFAULT_ICON_COLOR,
				]
			)
		)
	);

	const announce = useScreenReaderAnnounce();

	const movementSource = useMovementSource();

	const {getItemProps} = useKeyboardNavigation({
		itemCount: root.items.filter((node) => !isGroup(node)).length,
	});

	const keyboardMovementItems = toMovementItems(
		root.items,
		audiencesCriteriasByKey
	);

	const [{canDrop, isOver}, drop] = useDrop<
		AttributeDragItem,
		void,
		{canDrop: boolean; isOver: boolean}
	>({
		accept: DRAG_TYPES.ATTRIBUTE,
		collect: (monitor) => ({
			canDrop: monitor.canDrop(),
			isOver: monitor.isOver(),
		}),
		drop: (item) => {
			dispatch({
				audiencesCriteria: item.audiencesCriteria,
				type: 'ADD_RULE',
			});

			announce(Liferay.Language.get('a-condition-was-added'));
		},
	});

	const context: RenderContext = {
		announce,
		audiencesCriteriasByKey,
		dispatch,
		getItemProps,
		iconColorsByKey,
	};

	return (
		<div className="border mt-4 rounded">
			{movementSource ? (
				<KeyboardMovementManager
					dispatch={dispatch}
					items={keyboardMovementItems}
					nodes={root.items}
					source={movementSource}
				/>
			) : null}

			<div className="px-4 py-3">
				<p className="font-weight-bold mb-0 text-6">
					{Liferay.Language.get('conditions')}
				</p>
			</div>

			{root.items.length ? (
				<>
					<ConjunctionBar
						conjunction={root.conjunction}
						onConjunctionChange={(value) =>
							dispatch({
								conjunction: value,
								type: 'SET_CONJUNCTION',
							})
						}
					/>

					<div
						aria-label={Liferay.Language.get('conditions')}
						aria-orientation="vertical"
						className="px-3 py-2"
						role="menu"
					>
						<GroupItems context={context} group={root} path={[]} />
					</div>
				</>
			) : (
				<ConditionsEmptyState
					canDrop={canDrop}
					dropRef={drop}
					isOver={isOver}
				/>
			)}
		</div>
	);
}

interface GroupItemsProps {
	context: RenderContext;
	group: Group;
	path: number[];
}

function GroupItems({context, group, path}: GroupItemsProps) {
	const {
		announce,
		audiencesCriteriasByKey,
		dispatch,
		getItemProps,
		iconColorsByKey,
	} = context;

	const topLevel = !path.length;

	const handleAddRule = (
		audiencesCriteria: AudiencesCriteria,
		insertIndex?: number
	) => {
		dispatch({
			audiencesCriteria,
			groupPath: path,
			index: insertIndex,
			type: 'ADD_RULE',
		});

		announce(Liferay.Language.get('a-condition-was-added'));
	};

	const handleMoveRule = (nodeId: string, targetIndex: number) =>
		dispatch({
			nodeId,
			targetGroupId: group.id,
			targetIndex,
			type: 'MOVE_RULE',
		});

	return (
		<>
			{group.items.map((node, index) => {
				const nodePath = [...path, index];

				const ruleIndex = group.items
					.slice(0, index)
					.filter((item) => !isGroup(item)).length;

				return (
					<Fragment key={node.id}>
						{index > 0 ? (
							<div
								aria-hidden="true"
								className="font-weight-semi-bold my-3 text-3 text-secondary text-uppercase"
							>
								{group.conjunction === 'OR'
									? Liferay.Language.get('or')
									: Liferay.Language.get('and')}
							</div>
						) : null}

						{isGroup(node) ? (
							<GroupRow
								context={context}
								group={node}
								index={index}
								onAddRule={handleAddRule}
								onMoveRule={handleMoveRule}
								path={nodePath}
							/>
						) : (
							<RuleRow
								audiencesCriteria={
									audiencesCriteriasByKey[node.attribute]
								}
								canGroup={canGroupNode(nodePath)}
								iconColor={iconColorsByKey[node.attribute]}
								index={index}
								movable={topLevel}
								navigationProps={
									topLevel
										? getItemProps(ruleIndex)
										: undefined
								}
								onAddRule={handleAddRule}
								onChange={(rule) =>
									dispatch({
										path: nodePath,
										rule,
										type: 'UPDATE_RULE',
									})
								}
								onDelete={() => {
									dispatch({
										path: nodePath,
										type: 'DELETE_RULE',
									});

									announce(
										Liferay.Language.get(
											'a-condition-was-removed'
										)
									);
								}}
								onDuplicate={() => {
									dispatch({
										path: nodePath,
										type: 'DUPLICATE_RULE',
									});

									announce(
										Liferay.Language.get(
											'a-condition-was-duplicated'
										)
									);
								}}
								onGroup={(audiencesCriteria) =>
									dispatch({
										audiencesCriteria,
										targetId: node.id,
										type: 'ADD_GROUP',
									})
								}
								onMoveGroup={(nodeId) =>
									dispatch({
										nodeId,
										targetId: node.id,
										type: 'MOVE_GROUP',
									})
								}
								onMoveRule={handleMoveRule}
								rule={node}
							/>
						)}
					</Fragment>
				);
			})}
		</>
	);
}

interface GroupRowProps {
	context: RenderContext;
	group: Group;
	index: number;
	onAddRule: (audiencesCriteria: AudiencesCriteria, index?: number) => void;
	onMoveRule: (nodeId: string, index: number) => void;
	path: number[];
}

function GroupRow({
	context,
	group,
	index,
	onAddRule,
	onMoveRule,
	path,
}: GroupRowProps) {
	const {dispatch} = context;

	const groupRef = useRef<HTMLDivElement | null>(null);

	const [dropPosition, setDropPosition] = useState<DropZone | null>(null);

	const [{isOver}, dropRef] = useDrop<RowDragItem, void, {isOver: boolean}>({
		accept: [DRAG_TYPES.ATTRIBUTE, DRAG_TYPES.RULE],
		collect: (monitor) => ({
			isOver: monitor.isOver({shallow: true}) && monitor.canDrop(),
		}),
		drop: (item, monitor) => {
			if (monitor.didDrop()) {
				return;
			}

			const targetIndex =
				getDropPosition(groupRef, monitor, {canGroup: false}) ===
				DROP_POSITIONS.top
					? index
					: index + 1;

			if ('audiencesCriteria' in item) {
				onAddRule(item.audiencesCriteria, targetIndex);
			}
			else {
				onMoveRule(item.id, targetIndex);
			}
		},
		hover: (_item, monitor) => {
			setDropPosition(
				monitor.isOver({shallow: true})
					? getDropPosition(groupRef, monitor, {canGroup: false})
					: null
			);
		},
	});

	const setGroupRef = (element: HTMLDivElement | null) => {
		groupRef.current = element;

		dropRef(element);
	};

	return (
		<div
			aria-label={Liferay.Language.get('group')}
			className={classNames(
				'audience-builder-group border overflow-hidden rounded',
				{
					'audience-builder-group--drop-bottom':
						isOver && dropPosition === DROP_POSITIONS.bottom,
					'audience-builder-group--drop-top':
						isOver && dropPosition === DROP_POSITIONS.top,
				}
			)}
			ref={setGroupRef}
			role="group"
		>
			<ConjunctionBar
				conjunction={group.conjunction}
				onConjunctionChange={(value) =>
					dispatch({
						conjunction: value,
						groupPath: path,
						type: 'SET_CONJUNCTION',
					})
				}
			/>

			<div className="px-3 py-2">
				<GroupItems context={context} group={group} path={path} />
			</div>
		</div>
	);
}

interface ConjunctionBarProps {
	conjunction: string;
	onConjunctionChange: (conjunction: string) => void;
}

function ConjunctionBar({
	conjunction,
	onConjunctionChange,
}: ConjunctionBarProps) {
	return (
		<div className="align-items-center bg-lighter border-top c-gap-2 d-flex p-3 text-3 text-secondary">
			<Picker
				aria-label={Liferay.Language.get('conjunction')}
				className="form-control-sm w-auto"
				items={[
					{label: Liferay.Language.get('all'), value: 'AND'},
					{label: Liferay.Language.get('any'), value: 'OR'},
				]}
				onSelectionChange={(key) => onConjunctionChange(key as string)}
				selectedKey={conjunction}
			>
				{(item) => <Option key={item.value}>{item.label}</Option>}
			</Picker>

			{Liferay.Language.get('of-these-criteria-are-met')}
		</div>
	);
}

interface ConditionsEmptyStateProps {
	canDrop: boolean;
	dropRef: ConnectDropTarget;
	isOver: boolean;
}

function ConditionsEmptyState({
	canDrop,
	dropRef,
	isOver,
}: ConditionsEmptyStateProps) {
	return (
		<div
			className={classNames('audience-builder-drop-zone m-4 p-4', {
				'audience-builder-drop-zone--active': canDrop,
				'audience-builder-drop-zone--over': isOver,
			})}
			ref={dropRef}
		>
			{!canDrop && (
				<ClayEmptyState
					description={Liferay.Language.get(
						'to-create-a-new-audience-drag-items-from-the-sidebar-and-drop-them-here'
					)}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
					title={Liferay.Language.get('no-criteria-yet')}
				/>
			)}
		</div>
	);
}
