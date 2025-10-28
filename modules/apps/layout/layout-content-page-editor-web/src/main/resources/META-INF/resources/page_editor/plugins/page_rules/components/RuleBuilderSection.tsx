/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {ScreenReaderAnnouncerContext} from '@liferay/layout-js-components-web';
import React, {Dispatch, SetStateAction, useContext, useMemo} from 'react';
import {flushSync} from 'react-dom';
import {v4 as uuidv4} from 'uuid';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../app/config/constants/layoutDataItemTypes';
import {useSelector} from '../../../app/contexts/StoreContext';
import selectLayoutDataItemLabel from '../../../app/selectors/selectLayoutDataItemLabel';
import ActionComponent, {Action} from './Action';
import ConditionComponent, {Condition} from './Condition';

const TriggerLabel = React.forwardRef<HTMLButtonElement, any>(
	({children, className: _className, onClick, ...otherProps}, ref) => (
		<ClayButton
			className="form-control-select"
			displayType="secondary"
			onClick={onClick}
			ref={ref}
			size="sm"
			{...otherProps}
		>
			{children}
		</ClayButton>
	)
);

type RuleBuilderActionProps = {
	actions: Action[];
	setActions: Dispatch<SetStateAction<Action[]>>;
};

export function RuleBuilderActionSection({
	actions,
	setActions,
}: RuleBuilderActionProps) {
	const {sendMessage} = useContext(ScreenReaderAnnouncerContext);

	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);

	const [layoutDataItems, inputFragmentItems] = useMemo(() => {
		const layoutItems: {label: string; value: string}[] = [];
		const inputFragments: {label: string; value: string}[] = [];

		Object.values(layoutData.items).forEach((item) => {
			if (
				item.type !== LAYOUT_DATA_ITEM_TYPES.collectionItem &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.column &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.dropZone &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.fragmentDropZone &&
				item.type !== LAYOUT_DATA_ITEM_TYPES.root
			) {
				layoutItems.push({
					label: selectLayoutDataItemLabel(
						{fragmentEntryLinks, layoutData},
						item
					),
					value: item.itemId,
				});
			}

			if (item.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
				const fragment =
					fragmentEntryLinks[item.config.fragmentEntryLinkId];

				if (
					fragment &&
					fragment.fragmentEntryType === 'input' &&
					!fragment.fieldTypes?.includes('categorization') &&
					!fragment.fieldTypes?.includes('localizationSelect') &&
					!fragment.fieldTypes?.includes('stepper')
				) {
					inputFragments.push({
						label: selectLayoutDataItemLabel(
							{fragmentEntryLinks, layoutData},
							item
						),
						value: item.itemId,
					});
				}
			}
		});

		return [layoutItems, inputFragments];
	}, [layoutData, fragmentEntryLinks]);

	const actionsRefMap = useMemo(() => new Map(), []);

	const onAddAction = () => {
		const actionId = uuidv4();

		flushSync(() => {
			setActions((previousActions) => [
				...previousActions,
				{id: actionId} as Action,
			]);
		});

		const actionElement = actionsRefMap.get(actionId);

		actionElement?.focus();
		sendMessage(Liferay.Language.get('action-added'));
	};

	const onDeleteAction = (action: Action, index: number) => {
		if (actions.length === 1) {
			setActions([{id: uuidv4()} as Action]);
		}
		else {
			const nextCondition = actions[index - 1] || actions[index + 1];

			actionsRefMap.get(nextCondition.id)?.focus();

			setActions((previousActions) =>
				previousActions.filter(
					(_action, currentIndex) => currentIndex !== index
				)
			);
		}

		sendMessage(Liferay.Language.get('action-deleted'));
	};

	const setActionRef = (
		condition: Action,
		element: HTMLDivElement | null
	) => {
		actionsRefMap.set(condition.id, element);
	};

	return (
		<ClayPanel
			className="page-editor__rule-builder-section"
			displayTitle={
				<ClayPanel.Title className="align-items-center d-flex p-3 page-editor__rule-builder-section-title text-3">
					<div className="align-items-center d-flex">
						<ClayIcon
							className="mr-3 text-purple"
							symbol="arrow-start"
						/>

						<span className="font-weight-bold mr-3">
							{Liferay.Language.get(
								'execute-the-following-actions'
							)}
						</span>
					</div>
				</ClayPanel.Title>
			}
			displayType="secondary"
		>
			<ClayPanel.Body className="px-3">
				<div role="menu">
					{actions.map((action, index) => (
						<ActionComponent
							action={action}
							inputFragmentItems={inputFragmentItems}
							key={action.id}
							layoutDataItems={layoutDataItems}
							onActionChange={(action) =>
								setActions((previousActions) => {
									const newActions = [...previousActions];

									newActions[index] = action;

									return newActions;
								})
							}
							onDeleteAction={() => {
								onDeleteAction(action, index);
							}}
							showDeleteButton={
								actions.length > 1 || !!action.type
							}
							wrapperRef={(element) =>
								setActionRef(action, element)
							}
						/>
					))}
				</div>

				<ClayButton
					className="mt-2"
					displayType="secondary"
					onClick={onAddAction}
					size="sm"
				>
					{Liferay.Language.get('add-action')}
				</ClayButton>
			</ClayPanel.Body>
		</ClayPanel>
	);
}

export type ConditionType = 'all' | 'any';

type RuleBuilderConditionProps = {
	conditionType: ConditionType;
	conditions: Condition[];
	setConditionType: Dispatch<SetStateAction<ConditionType>>;
	setConditions: Dispatch<SetStateAction<Condition[]>>;
};

export function RuleBuilderConditionSection({
	conditionType,
	conditions,
	setConditionType,
	setConditions,
}: RuleBuilderConditionProps) {
	const {sendMessage} = useContext(ScreenReaderAnnouncerContext);

	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);

	const inputFragmentItems = useMemo(() => {
		const inputFragments: {label: string; value: string}[] = [];

		Object.values(layoutData.items).forEach((item) => {
			if (item.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
				const fragment =
					fragmentEntryLinks[item.config.fragmentEntryLinkId];

				if (
					fragment &&
					fragment.fragmentEntryType === 'input' &&
					fragment.fieldTypes?.includes('boolean')
				) {
					inputFragments.push({
						label: selectLayoutDataItemLabel(
							{fragmentEntryLinks, layoutData},
							item
						),
						value: item.itemId,
					});
				}
			}
		});

		return inputFragments;
	}, [layoutData, fragmentEntryLinks]);

	const conditionRefMap = useMemo(() => new Map(), []);

	const onAddCondition = () => {
		const conditionId = uuidv4();

		flushSync(() => {
			setConditions((previousConditions) => [
				...previousConditions,
				{id: conditionId} as Condition,
			]);
		});

		const conditionElement = conditionRefMap.get(conditionId);

		conditionElement?.focus();
		sendMessage(Liferay.Language.get('condition-added'));
	};

	const onDeleteCondition = (condition: Condition, index: number) => {
		if (conditions.length === 1) {
			setConditions([{id: uuidv4()} as Condition]);
		}
		else {
			const nextCondition =
				conditions[index - 1] || conditions[index + 1];

			conditionRefMap.get(nextCondition.id)?.focus();

			setConditions((previousConditions) =>
				previousConditions.filter(
					(_condition, currentIndex) => currentIndex !== index
				)
			);
		}

		sendMessage(Liferay.Language.get('condition-deleted'));
	};

	const setConditionRef = (
		condition: Condition,
		element: HTMLDivElement | null
	) => {
		conditionRefMap.set(condition.id, element);
	};

	return (
		<ClayPanel
			className="page-editor__rule-builder-section"
			displayTitle={
				<ClayPanel.Title
					aria-label={
						conditionType === 'all'
							? Liferay.Language.get(
									'if-all-of-the-following-conditions-are-met'
								)
							: Liferay.Language.get(
									'if-any-of-the-following-conditions-are-met'
								)
					}
					className="p-3 page-editor__rule-builder-section-title text-3"
				>
					<div className="align-items-center d-flex">
						<ClayIcon
							className="arrow-icon mr-3"
							symbol="arrow-split"
						/>

						<span className="font-weight-bold mr-3">
							{Liferay.Language.get('if')}
						</span>

						<div className="align-items-center d-flex">
							<Picker
								aria-label={
									conditionType === 'all'
										? Liferay.Language.get('all')
										: Liferay.Language.get('any')
								}
								as={TriggerLabel}
								items={[
									{
										label: Liferay.Language.get('any'),
										value: 'any',
									},
									{
										label: Liferay.Language.get('all'),
										value: 'all',
									},
								]}
								messages={{
									itemDescribedby: Liferay.Language.get(
										'you-are-currently-on-a-text-element,-inside-of-a-list-box'
									),
									itemSelected:
										Liferay.Language.get('x-selected'),
									scrollToBottomAriaLabel:
										Liferay.Language.get(
											'scroll-to-bottom'
										),
									scrollToTopAriaLabel:
										Liferay.Language.get('scroll-to-top'),
								}}
								onSelectionChange={(key: any) =>
									setConditionType(key)
								}
								selectedKey={conditionType}
							>
								{(item) => (
									<Option key={item.value}>
										{item.label}
									</Option>
								)}
							</Picker>

							<span className="font-weight-bold ml-3 mr-3">
								{Liferay.Language.get(
									'of-the-following-conditions-are-met'
								)}
							</span>
						</div>
					</div>
				</ClayPanel.Title>
			}
			displayType="secondary"
			showCollapseIcon
		>
			<ClayPanel.Body className="px-3">
				<div role="menu">
					{conditions.map((condition, index, conditions) => (
						<ConditionComponent
							condition={condition}
							inputFragmentItems={inputFragmentItems}
							key={condition.id}
							onConditionChange={(condition) =>
								setConditions((previousConditions) => {
									const newConditions = [
										...previousConditions,
									];

									newConditions[index] = condition;

									return newConditions;
								})
							}
							onDeleteCondition={() =>
								onDeleteCondition(condition, index)
							}
							showDeleteButton={
								conditions.length > 1 || !!condition.type
							}
							wrapperRef={(element) =>
								setConditionRef(condition, element)
							}
						/>
					))}
				</div>

				<ClayButton
					className="mt-2"
					displayType="secondary"
					onClick={onAddCondition}
					size="sm"
				>
					{Liferay.Language.get('add-condition')}
				</ClayButton>
			</ClayPanel.Body>
		</ClayPanel>
	);
}
