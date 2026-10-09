/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayList from '@clayui/list';
import {useEventListener} from '@liferay/frontend-js-react-web';
import {useDragAndDrop} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {LIST_ITEM_TYPES} from '../../../app/config/constants/listItemTypes';
import {useRulesModal} from '../../../app/contexts/RulesModalContext';
import {useDispatch, useSelector} from '../../../app/contexts/StoreContext';
import {useHighlightItems, useKeyboardNavigation} from '../../../app/js-index';
import selectLayoutDataItemLabel from '../../../app/selectors/selectLayoutDataItemLabel';
import deleteRule from '../../../app/thunks/deleteRule';
import updateRule from '../../../app/thunks/updateRule';
import updateRules from '../../../app/thunks/updateRules';
import {isAdvancedRule} from '../../../app/utils/isAdvancedRule';
import {isLayoutDataItemDeleted} from '../../../app/utils/isLayoutDataItemDeleted';
import useActionValues, {
	ActionValues,
} from '../../../app/utils/useActionValues';
import useConditionValues, {
	ConditionValues,
} from '../../../app/utils/useConditionValues';
import {
	Action as ActionType,
	Condition as ConditionType,
	Rule,
} from '../../../types/Rule';
import useMappingFieldItems from '../utils/useMappingFieldItems';

const MAX_RULES = 20;

export default function RulesList({
	isSearching,
	rules,
}: {
	isSearching: boolean;
	rules: Rule[];
}) {
	const dispatch = useDispatch();
	const highlightItems = useHighlightItems();

	const onUnhighlightItems = (event: Event) => {
		const target = event.target as HTMLElement;

		if (!target.classList.contains('page-editor__rule')) {
			highlightItems([]);
		}
	};

	useEventListener(
		'keydown',
		(event) => {
			const {key} = event as KeyboardEvent;

			if (key === 'Enter') {
				onUnhighlightItems(event);
			}
		},
		false,
		document
	);

	const {openRulesModal} = useRulesModal();

	const onDeleteRule = (rule: Rule) => {
		dispatch(
			deleteRule({
				ruleId: rule.id!,
			})
		).then(() =>
			openToast({
				message: Liferay.Language.get(
					'the-rule-was-deleted-successfully'
				),
				type: 'success',
			})
		);
	};

	const onEditRule = (rule: Rule, trigger: HTMLButtonElement | null) =>
		openRulesModal({rule, trigger});

	return (
		<>
			{!isSearching ? (
				<ClayButton
					className="mb-3 mx-3"
					displayType="secondary"
					onClick={() => openRulesModal()}
					size="sm"
				>
					<ClayIcon className="mr-2" symbol="plus" />

					{Liferay.Language.get('new-rule')}
				</ClayButton>
			) : null}

			<div className="border-top overflow-auto p-3">
				{!isSearching && rules.length >= MAX_RULES ? (
					<ClayAlert
						className="mb-4 mt-2"
						displayType="warning"
						title="Warning"
					>
						{Liferay.Language.get(
							'excessive-rules-may-affect-page-performance'
						)}
					</ClayAlert>
				) : null}

				<ClayList data-menu role="menu">
					{rules.map((rule, index) => (
						<RuleItem
							index={index}
							key={rule.id}
							onDelete={onDeleteRule}
							onEdit={onEditRule}
							rule={rule}
							rules={rules}
						/>
					))}
				</ClayList>
			</div>
		</>
	);
}

function RuleItem({
	index,
	onDelete,
	onEdit,
	rule,
	rules,
}: {
	index: number;
	onDelete: (rule: Rule) => void;
	onEdit: (rule: Rule, trigger: HTMLButtonElement | null) => void;
	rule: Rule;
	rules: Rule[];
}) {
	const mappingFieldItems = useMappingFieldItems();

	const highlightItems = useHighlightItems();
	const {isTarget: isNavigationTarget, setElement} = useKeyboardNavigation({
		type: LIST_ITEM_TYPES.listItem,
	});
	const layoutData = useSelector((state) => state.layoutData);
	const [triggerElement, setTriggerElement] =
		useState<HTMLButtonElement | null>(null);

	const [editing, setEditing] = useState(false);
	const [name, setName] = useState(rule.name);

	const dragHandlerRef = useRef<HTMLButtonElement | null>(null);
	const dropItemRef = useRef<HTMLLIElement | null>(null);
	const inputRef = useRef<HTMLInputElement>(null);

	const dispatch = useDispatch();

	const {
		handleKeyboardDragAndDrop,
		isDragging,
		isDropBottomPosition,
		isDropTopPosition,
		isKeyboardDragging,
	} = useDragAndDrop({
		dragHandlerRef,
		dropItemRef,
		item: rule,
		itemIndex: index,
		items: rules,
		onDrop: (rules) => {
			dispatch(updateRules(rules));
		},
	});

	useEffect(() => {
		if (editing && inputRef.current) {
			inputRef.current.focus();
		}
	}, [editing]);

	const onFinishEditing = useCallback(() => {
		if (!name) {
			setName(rule.name);

			openToast({
				message: Liferay.Language.get('rule-name-cannot-be-empty'),
				type: 'info',
			});

			return;
		}

		dispatch(
			updateRule({
				...rule,
				name,
				ruleId: rule.id!,
			})
		);
	}, [dispatch, name, rule]);

	const items = useSelector((state) =>
		Object.values(state.layoutData.items)
			.filter(
				(item) =>
					!isLayoutDataItemDeleted(state.layoutData, item.itemId)
			)
			.map((item) => ({
				label: selectLayoutDataItemLabel(state, item),
				value: item.itemId,
			}))
	);

	const conditions = useConditionValues({
		...rule,
		conditions: rule.conditions || [],
		items,
		mappingFieldItems,
	});
	const actions = useActionValues({...rule, items});

	const ruleItemIds = useMemo(
		() => getRuleItemIds(rule.actions, rule.conditions || []),
		[rule.actions, rule.conditions]
	);

	const onHighlightItems = async () => {
		highlightItems(ruleItemIds);
	};

	const onScroll = () => {
		const fragment = document.querySelector('.highlighted-from-rule');

		fragment?.scrollIntoView({
			behavior: 'instant' as ScrollBehavior,
			block: 'center',
			inline: 'nearest',
		});
	};

	const isRuleDisabled = ruleItemIds.some((id) =>
		isLayoutDataItemDeleted(layoutData, id)
	);

	const setListItemRef = useCallback(
		(node: HTMLLIElement) => {
			dropItemRef.current = node;

			if (!isKeyboardDragging) {
				setElement(node);
			}
		},
		[setElement, isKeyboardDragging]
	);

	const tabIndex = useMemo(
		() => (isNavigationTarget ? 0 : -1),
		[isNavigationTarget]
	);

	return (
		<ClayList.Item
			aria-description={Liferay.Language.get(
				'press-enter-or-space-to-scroll-to-the-first-fragment-under-this-rule'
			)}
			aria-label={getRuleAriaLabel(
				rule.name,
				conditions,
				actions,
				isRuleDisabled
			)}
			className={classNames('drag-and-drop p-2 page-editor__rule', {
				'dragging': isDragging,
				'drop-bottom': isDropBottomPosition,
				'drop-top': isDropTopPosition,
			})}
			key={rule.id}
			onClick={async () => {
				await onHighlightItems();

				onScroll();
			}}
			onKeyDown={async ({key}) => {
				if (key === 'Enter' || key === ' ') {
					await onHighlightItems();

					onScroll();
				}
			}}
			ref={setListItemRef}
			role="menuitem"
			tabIndex={tabIndex}
		>
			<ClayList.ItemField expand>
				<div className="align-items-center d-flex">
					<ClayButtonWithIcon
						aria-label={sub(Liferay.Language.get('move-x'), name)}
						borderless
						className="ml-n2 text-secondary"
						onClick={(event) => {
							event.stopPropagation();
						}}
						onKeyDown={handleKeyboardDragAndDrop}
						ref={dragHandlerRef}
						size="sm"
						symbol="drag"
						tabIndex={tabIndex}
						title={sub(Liferay.Language.get('move-x'), name)}
					/>

					{editing ? (
						<input
							onBlur={() => {
								setEditing(false);

								onFinishEditing();
							}}
							onChange={(event) => {
								setName(event.target.value);
							}}
							onFocus={() => {
								if (!inputRef.current) {
									return;
								}

								inputRef.current.setSelectionRange(
									0,
									name.length
								);
							}}
							onKeyDown={(event) => {
								if (
									event.key === 'Enter' ||
									event.key === 'Escape' ||
									event.key === 'Tab'
								) {
									setEditing(false);

									onFinishEditing();
								}

								if (!event.key.match(/[a-z0-9-_ ]/gi)) {
									event.preventDefault();
								}

								event.stopPropagation();
							}}
							ref={inputRef}
							type="text"
							value={name}
						/>
					) : (
						<span
							className="flex-grow-1 font-weight-semi-bold"
							onDoubleClick={() => setEditing(true)}
						>
							<span aria-hidden="true">
								{name}

								{isRuleDisabled ? (
									<ClayIcon
										className="lfr-tooltip-scope ml-2 text-warning"
										data-title={Liferay.Language.get(
											'disabled-rule'
										)}
										symbol="warning-full"
									/>
								) : null}
							</span>
						</span>
					)}

					<ClayDropDown
						hasLeftSymbols={true}
						menuElementAttrs={{
							containerProps: {
								className: 'cadmin',
							},
						}}
						onMouseOver={(event) => event.stopPropagation()}
						trigger={
							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('view-x-options'),
									rule.name
								)}
								borderless
								displayType="secondary"
								onClick={(event) => {
									event.stopPropagation();
									highlightItems([]);
								}}
								ref={setTriggerElement}
								size="sm"
								symbol="ellipsis-v"
								tabIndex={tabIndex}
								title={sub(
									Liferay.Language.get('view-x-options'),
									rule.name
								)}
							/>
						}
					>
						<ClayDropDown.ItemList>
							<ClayDropDown.Item
								onClick={() => onEdit(rule, triggerElement)}
								symbolLeft="pencil"
							>
								{Liferay.Language.get('edit')}
							</ClayDropDown.Item>

							<ClayDropDown.Item onClick={() => setEditing(true)}>
								{Liferay.Language.get('rename')}
							</ClayDropDown.Item>

							<ClayDropDown.Divider />

							<ClayDropDown.Item
								onClick={() => onDelete(rule)}
								symbolLeft="trash"
							>
								{Liferay.Language.get('delete')}
							</ClayDropDown.Item>
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</div>
			</ClayList.ItemField>

			<ClayList.ItemField
				aria-disabled={isRuleDisabled || undefined}
				className={classNames('mt-3', {'text-muted': isRuleDisabled})}
				expand
			>
				{isAdvancedRule(rule) ? (
					<p className="mb-0">
						<ClayLabel className="m-0" displayType="info">
							{Liferay.Language.get('advanced-rule')}
						</ClayLabel>
					</p>
				) : (
					<p
						aria-hidden="true"
						className="align-items-center c-gap-2 d-flex flex-wrap"
					>
						{conditions.map((condition, index) => (
							<Condition
								condition={condition}
								index={index}
								key={condition.id}
							/>
						))}

						{actions.map((action) => (
							<Action action={action} key={action.id} />
						))}
					</p>
				)}
			</ClayList.ItemField>
		</ClayList.Item>
	);
}

function Condition({
	condition,
	index,
}: {
	condition: ConditionValues;
	index: number;
}) {
	return (
		<>
			<span
				className={classNames('font-weight-semi-bold', {
					'text-uppercase': index > 0,
				})}
			>
				{condition.prefix}
			</span>

			{condition.type ? (
				<ClayLabel className="m-0" displayType="secondary">
					{condition.type}
				</ClayLabel>
			) : null}

			{condition.condition}

			{condition.value ? (
				<ClayLabel className="m-0" displayType="secondary">
					{condition.value}
				</ClayLabel>
			) : null}
		</>
	);
}

function Action({action}: {action: ActionValues}) {
	return (
		<>
			{action.prefix ? (
				<span className="font-weight-semi-bold text-uppercase">
					{action.prefix}
				</span>
			) : null}

			<span className="font-weight-semi-bold">{action.type}</span>

			{action.item ? (
				<ClayLabel className="m-0" displayType="secondary">
					{action.item}
				</ClayLabel>
			) : null}
		</>
	);
}

function getRuleAriaLabel(
	name: string,
	conditions: ConditionValues[],
	actions: ActionValues[],
	disabled: boolean
) {
	const conditionsDescription = conditions
		.map((condition) => condition.description)
		.join(' ');

	const actionsDescription = actions
		.map((action) => action.description)
		.join(' ');

	return `${name}${disabled ? ` ${Liferay.Language.get('disabled-rule')}` : ''}: ${conditionsDescription} ${actionsDescription}`;
}

function getRuleItemIds(actions: ActionType[], conditions: ConditionType[]) {
	const ruleItemIds = new Set<string>();

	for (const {itemId} of actions) {
		if (itemId) {
			ruleItemIds.add(itemId);
		}
	}

	if (conditions) {
		for (const {field, type} of conditions) {
			if (field && type === 'form') {
				ruleItemIds.add(field);
			}
		}
	}

	return [...ruleItemIds];
}
