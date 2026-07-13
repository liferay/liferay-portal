/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {Dispatch, Fragment} from 'react';
import {ConnectDropTarget, useDrop} from 'react-dnd';

import {
	CATEGORY_ICON_COLORS,
	DEFAULT_ICON_COLOR,
} from '../constants/categoryIconColors';
import {DRAG_TYPES} from '../constants/dragTypes';
import useKeyboardNavigation from '../hooks/useKeyboardNavigation';
import {useMovementSource} from '../keyboard_movement/KeyboardMovementContext';
import KeyboardMovementManager from '../keyboard_movement/KeyboardMovementManager';
import {Action} from '../reducer';
import {AudiencesCriteria, AudiencesCriteriaType, Rule} from '../types';
import RuleRow from './RuleRow';

interface IProps {
	audiencesCriteriaTypes: AudiencesCriteriaType[];
	conjunction: string;
	dispatch: Dispatch<Action>;
	rules: Rule[];
}

interface AttributeDragItem {
	audiencesCriteria: AudiencesCriteria;
	type: string;
}

export default function ConditionsPanel({
	audiencesCriteriaTypes,
	conjunction,
	dispatch,
	rules,
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

	const {getItemProps} = useKeyboardNavigation({itemCount: rules.length});

	const movementItems = rules.map((rule) => {
		const audiencesCriteria = audiencesCriteriasByKey[rule.attribute];

		return {
			icon: audiencesCriteria?.icon ?? '',
			id: rule.id,
			name: audiencesCriteria?.label ?? rule.attribute,
		};
	});

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
		drop: (item) => handleAddRule(item.audiencesCriteria),
	});

	function handleAddRule(
		audiencesCriteria: AudiencesCriteria,
		index?: number
	) {
		dispatch({audiencesCriteria, index, type: 'ADD_RULE'});

		announce(Liferay.Language.get('a-condition-was-added'));
	}

	const handleReorder = (newItems: Array<{id: string}>) => {
		const rulesById = new Map(rules.map((rule) => [rule.id, rule]));

		dispatch({
			rules: newItems
				.map((item) => rulesById.get(item.id))
				.filter((rule): rule is Rule => Boolean(rule)),
			type: 'REORDER_RULES',
		});
	};

	return (
		<div className="border mt-4 rounded">
			{movementSource ? (
				<KeyboardMovementManager
					dispatch={dispatch}
					items={movementItems}
					rules={rules}
					source={movementSource}
				/>
			) : null}

			<div className="px-4 py-3">
				<p className="font-weight-bold mb-0 text-6">
					{Liferay.Language.get('conditions')}
				</p>
			</div>

			{rules.length ? (
				<>
					<ConjunctionBar
						conjunction={conjunction}
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
						{rules.map((rule, index) => (
							<Fragment key={rule.id}>
								{index > 0 ? (
									<div
										aria-hidden="true"
										className="font-weight-semi-bold my-3 text-3 text-secondary text-uppercase"
									>
										{conjunction === 'OR'
											? Liferay.Language.get('or')
											: Liferay.Language.get('and')}
									</div>
								) : null}

								<RuleRow
									audiencesCriteria={
										audiencesCriteriasByKey[rule.attribute]
									}
									iconColor={iconColorsByKey[rule.attribute]}
									index={index}
									items={movementItems}
									navigationProps={getItemProps(index)}
									onAddRule={handleAddRule}
									onChange={(newRule) =>
										dispatch({
											index,
											rule: newRule,
											type: 'UPDATE_RULE',
										})
									}
									onDelete={() => {
										dispatch({index, type: 'DELETE_RULE'});

										announce(
											Liferay.Language.get(
												'a-condition-was-removed'
											)
										);
									}}
									onDuplicate={() => {
										dispatch({
											index,
											type: 'DUPLICATE_RULE',
										});

										announce(
											Liferay.Language.get(
												'a-condition-was-duplicated'
											)
										);
									}}
									onReorder={handleReorder}
									rule={rule}
								/>
							</Fragment>
						))}
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
					{
						label: Liferay.Language.get('all'),
						value: 'AND',
					},
					{
						label: Liferay.Language.get('any'),
						value: 'OR',
					},
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
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
					title={Liferay.Language.get('no-criteria-yet')}
				/>
			)}
		</div>
	);
}
