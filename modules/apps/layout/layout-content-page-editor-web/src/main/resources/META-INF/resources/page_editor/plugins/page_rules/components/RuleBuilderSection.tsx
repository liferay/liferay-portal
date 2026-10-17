/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {
	PopoverTooltip,
	RowBuilder,
	isNullOrUndefined,
} from '@liferay/layout-js-components-web';
import {sub, uuidv4} from 'frontend-js-web';
import React, {useId, useMemo} from 'react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../app/config/constants/layoutDataItemTypes';
import {useSelector} from '../../../app/contexts/StoreContext';
import selectLayoutDataItemLabel from '../../../app/selectors/selectLayoutDataItemLabel';
import {isAllowedInRules} from '../../../app/utils/isAllowedInRules';
import {isLayoutDataItemDeleted} from '../../../app/utils/isLayoutDataItemDeleted';
import {translateConditionsToScript} from '../../../app/utils/translateConditionsToScript';
import useActionValues from '../../../app/utils/useActionValues';
import useConditionValues from '../../../app/utils/useConditionValues';
import {Action, Condition} from '../../../types/Rule';
import useMappingFieldItems from '../utils/useMappingFieldItems';
import ActionComponent from './Action';
import AdvancedRuleEditor from './AdvancedRuleEditor';
import ConditionComponent from './Condition';

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
	setActions: (actions: Action[]) => void;
};

export function RuleBuilderActionSection({
	actions,
	setActions,
}: RuleBuilderActionProps) {
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);

	const [layoutDataItems, inputFragmentItems] = useMemo(() => {
		const layoutItems: {label: string; value: string}[] = [];
		const inputFragments: {label: string; value: string}[] = [];

		Object.values(layoutData.items).forEach((item) => {
			if (isLayoutDataItemDeleted(layoutData, item.itemId)) {
				return;
			}

			if (isAllowedInRules(item, layoutData)) {
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

	const actionValues = useActionValues({
		actions,
		items: [...layoutDataItems, ...inputFragmentItems],
	});

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
				<RowBuilder<Action>
					canDelete={(action, _index, currentActions) =>
						!action.readOnly &&
						(currentActions.length > 1 || !!action.type)
					}
					createItem={() => ({id: uuidv4()}) as Action}
					itemClassName="page-editor__rule-builder-item--action"
					items={actions}
					labels={{
						add: Liferay.Language.get('add-action'),
						addedAnnouncement: Liferay.Language.get('action-added'),
						delete: Liferay.Language.get('delete-action'),
						deleteAriaLabel: (_action, index) =>
							sub(
								Liferay.Language.get('delete-action-x'),
								actionValues[index]?.description ?? ''
							),
						deletedAnnouncement:
							Liferay.Language.get('action-deleted'),
						itemAriaLabel: (action, index) =>
							action.itemId
								? actionValues[index]?.description ?? ''
								: Liferay.Language.get('incomplete-action'),
						list: Liferay.Language.get(
							'execute-the-following-actions'
						),
					}}
					renderItem={({item, onChange}) => (
						<ActionComponent
							action={item}
							inputFragmentItems={inputFragmentItems}
							layoutDataItems={layoutDataItems}
							onActionChange={onChange}
						/>
					)}
					setItems={setActions}
				/>
			</ClayPanel.Body>
		</ClayPanel>
	);
}

export type ConditionType = 'all' | 'any';

type RuleBuilderConditionProps = {
	conditionType: ConditionType;
	conditions: Condition[];
	script: string | undefined;
	setRuleConditions: ({
		conditionType,
		conditions,
		script,
	}: {
		conditionType?: ConditionType;
		conditions?: Condition[];
		script?: string;
	}) => void;
};

export function RuleBuilderConditionSection({
	conditionType,
	conditions,
	script,
	setRuleConditions,
}: RuleBuilderConditionProps) {
	const mappingFieldItems = useMappingFieldItems();

	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const layoutData = useSelector((state) => state.layoutData);

	const inputFragmentItems = useMemo(() => {
		const inputFragments: {label: string; value: string}[] = [];

		Object.values(layoutData.items).forEach((item) => {
			if (isLayoutDataItemDeleted(layoutData, item.itemId)) {
				return;
			}

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

	const conditionValues = useConditionValues({
		conditionType,
		conditions,
		items: inputFragmentItems,
		mappingFieldItems,
	});

	const fieldTypes = useMemo(() => {
		const types: Record<string, string> = {};

		for (const field of mappingFieldItems) {
			types[field.value] = field.type;
		}

		return types;
	}, [mappingFieldItems]);

	const tooltipId = useId();

	return (
		<ClayPanel
			className="page-editor__rule-builder-section"
			displayTitle={
				<ClayPanel.Title className="d-flex justify-content-between p-3 page-editor__rule-builder-section-title">
					<div className="align-items-center d-flex">
						<ClayIcon
							className="arrow-icon mr-3 mt-0"
							symbol="arrow-split"
						/>

						<span className="font-weight-semi-bold text-4">
							{Liferay.Language.get('conditions')}
						</span>
					</div>

					<div>
						<PopoverTooltip
							content={Liferay.Language.get(
								'the-basic-builder-does-not-support-advanced-expressions'
							)}
							id={tooltipId}
							trigger={
								<ClayIcon
									aria-label={Liferay.Language.get(
										'show-more'
									)}
									className="mr-2"
									symbol="question-circle-full"
								/>
							}
						/>

						<ClayButton
							disabled={Boolean(script)}
							displayType="secondary"
							onClick={() => {
								setRuleConditions(
									isNullOrUndefined(script)
										? {
												script: conditions?.length
													? translateConditionsToScript(
															conditions,
															conditionType,
															fieldTypes
														)
													: '',
											}
										: {
												conditionType: 'all',
												conditions: [
													{
														id: uuidv4(),
														type: undefined,
													},
												],
												script: undefined,
											}
								);
							}}
							size="sm"
						>
							{isNullOrUndefined(script)
								? Liferay.Language.get('advanced')
								: Liferay.Language.get('basic')}

							<ClayIcon className="ml-3" symbol="code" />
						</ClayButton>
					</div>
				</ClayPanel.Title>
			}
			displayType="secondary"
			showCollapseIcon
		>
			<ClayPanel.Body className="px-3">
				{isNullOrUndefined(script) ? (
					<>
						<div className="align-items-center d-flex mb-3 text-4">
							<span className="mr-3">
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
											Liferay.Language.get(
												'scroll-to-top'
											),
									}}
									onSelectionChange={(key: any) =>
										setRuleConditions({
											conditionType: key,
										})
									}
									selectedKey={conditionType}
								>
									{(item) => (
										<Option key={item.value}>
											{item.label}
										</Option>
									)}
								</Picker>

								<span className="ml-3 mr-3">
									{Liferay.Language.get(
										'of-the-following-conditions-are-met'
									)}
								</span>
							</div>
						</div>

						<RowBuilder<Condition>
							canDelete={(condition, _index, currentConditions) =>
								currentConditions.length > 1 || !!condition.type
							}
							createItem={() => ({id: uuidv4()}) as Condition}
							itemClassName="page-editor__rule-builder-item--condition"
							items={conditions}
							labels={{
								add: Liferay.Language.get('add-condition'),
								addedAnnouncement:
									Liferay.Language.get('condition-added'),
								delete: Liferay.Language.get(
									'delete-condition'
								),
								deleteAriaLabel: (_condition, index) =>
									sub(
										Liferay.Language.get(
											'delete-condition-x'
										),
										conditionValues[index]?.description ??
											''
									),
								deletedAnnouncement:
									Liferay.Language.get('condition-deleted'),
								itemAriaLabel: (condition, index) =>
									condition.options?.value
										? conditionValues[index]?.description ??
											''
										: Liferay.Language.get(
												'incomplete-condition'
											),
								list:
									conditionType === 'all'
										? Liferay.Language.get(
												'if-all-of-the-following-conditions-are-met'
											)
										: Liferay.Language.get(
												'if-any-of-the-following-conditions-are-met'
											),
							}}
							renderItem={({item, onChange}) => (
								<ConditionComponent
									condition={item}
									inputFragmentItems={inputFragmentItems}
									mappingFieldItems={mappingFieldItems}
									onConditionChange={onChange}
								/>
							)}
							setItems={(newConditions) =>
								setRuleConditions({
									conditions: newConditions,
								})
							}
						/>
					</>
				) : (
					<div>
						<AdvancedRuleEditor
							mappingFieldItems={mappingFieldItems}
							onChange={(value: string | undefined) => {
								setRuleConditions({script: value || ''});
							}}
							value={script}
						/>
					</div>
				)}
			</ClayPanel.Body>
		</ClayPanel>
	);
}
