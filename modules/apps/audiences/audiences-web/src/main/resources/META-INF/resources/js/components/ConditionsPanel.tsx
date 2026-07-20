/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {Dispatch, Fragment, useMemo, useRef, useState} from 'react';
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
import {
	useMovementSource,
	useMovementTarget,
} from '../keyboard_movement/KeyboardMovementContext';
import KeyboardMovementManager from '../keyboard_movement/KeyboardMovementManager';
import {Action} from '../reducer';
import {AudiencesCriteria, AudiencesCriteriaType, Group} from '../types';
import {DropZone, getDropPosition} from '../util/getDropPosition';
import {canGroupNode} from '../util/tree/canGroupNode';
import {flattenRules} from '../util/tree/flattenRules';
import {isGroup} from '../util/tree/isGroup';
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
	ruleIndexById: Map<string, number>;
}

function collectNodeNames(
	group: Group,
	audiencesCriteriasByKey: Record<string, AudiencesCriteria>,
	namesById: Record<string, string> = {}
): Record<string, string> {
	group.items.forEach((node) => {
		if (isGroup(node)) {
			namesById[node.id] = Liferay.Language.get('group');

			collectNodeNames(node, audiencesCriteriasByKey, namesById);
		}
		else {
			namesById[node.id] =
				audiencesCriteriasByKey[node.attribute]?.label ??
				node.attribute;
		}
	});

	return namesById;
}

export default function ConditionsPanel({
	audiencesCriteriaTypes,
	dispatch,
	root,
}: IProps) {
	const audiencesCriteriasByKey: Record<string, AudiencesCriteria> = useMemo(
		() =>
			Object.fromEntries(
				audiencesCriteriaTypes
					.flatMap(
						(audiencesCriteriaType) =>
							audiencesCriteriaType.audiencesCriterias
					)
					.map((audiencesCriteria) => [
						audiencesCriteria.key,
						audiencesCriteria,
					])
			),
		[audiencesCriteriaTypes]
	);

	const iconColorsByKey: Record<string, string> = useMemo(
		() =>
			Object.fromEntries(
				audiencesCriteriaTypes.flatMap((audiencesCriteriaType) =>
					audiencesCriteriaType.audiencesCriterias.map(
						(audiencesCriteria) => [
							audiencesCriteria.key,
							CATEGORY_ICON_COLORS[audiencesCriteriaType.key] ??
								DEFAULT_ICON_COLOR,
						]
					)
				)
			),
		[audiencesCriteriaTypes]
	);

	const announce = useScreenReaderAnnounce();

	const movementSource = useMovementSource();

	const ruleIndexById = useMemo(
		() =>
			new Map(flattenRules(root).map((rule, index) => [rule.id, index])),
		[root]
	);

	const {getItemProps} = useKeyboardNavigation({
		itemCount: ruleIndexById.size,
	});

	const namesById = useMemo(
		() => collectNodeNames(root, audiencesCriteriasByKey),
		[audiencesCriteriasByKey, root]
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
		ruleIndexById,
	};

	return (
		<div className="border mt-4 rounded">
			{movementSource ? (
				<KeyboardMovementManager
					dispatch={dispatch}
					namesById={namesById}
					root={root}
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
		ruleIndexById,
	} = context;

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
								navigationProps={getItemProps(
									ruleIndexById.get(node.id) ?? 0
								)}
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
										type: 'MOVE_RULE_INTO_NEW_GROUP',
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

	const movementTarget = useMovementTarget();

	const groupRef = useRef<HTMLDivElement | null>(null);

	const [dropPosition, setDropPosition] = useState<DropZone | null>(null);

	const isMovementTarget = movementTarget.nodeId === group.id;

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
						(isOver && dropPosition === DROP_POSITIONS.bottom) ||
						(isMovementTarget &&
							movementTarget.position === DROP_POSITIONS.bottom),
					'audience-builder-group--drop-top':
						(isOver && dropPosition === DROP_POSITIONS.top) ||
						(isMovementTarget &&
							movementTarget.position === DROP_POSITIONS.top),
				}
			)}
			data-keyboard-movement-id={group.id}
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
