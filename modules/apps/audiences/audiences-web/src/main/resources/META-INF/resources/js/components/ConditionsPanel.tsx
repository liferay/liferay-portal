/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {Dispatch, Fragment} from 'react';
import {useDrop} from 'react-dnd';

import {DRAG_TYPES} from '../constants/dragTypes';
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

	const announce = useScreenReaderAnnounce();

	const dndItems = rules.map((rule) => {
		const audiencesCriteria = audiencesCriteriasByKey[rule.attribute];

		return {
			icon: audiencesCriteria?.icon ?? '',
			id: rule.id,
			name: audiencesCriteria?.label ?? rule.attribute,
		};
	});

	const [{isOver}, drop] = useDrop<
		AttributeDragItem,
		void,
		{isOver: boolean}
	>({
		accept: DRAG_TYPES.ATTRIBUTE,
		collect: (monitor) => ({isOver: monitor.isOver()}),
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
			<div className="px-4 py-3">
				<p className="font-weight-bold mb-0 text-6">
					{Liferay.Language.get('conditions')}
				</p>
			</div>

			{rules.length ? (
				<>
					<div className="align-items-center bg-lighter border-top d-flex p-3">
						<div className="mr-3">
							<Picker
								aria-label={Liferay.Language.get('conjunction')}
								className="form-control-sm w-auto"
								items={[
									{
										label: Liferay.Language.get('and'),
										value: 'AND',
									},
									{
										label: Liferay.Language.get('or'),
										value: 'OR',
									},
								]}
								onSelectionChange={(key) =>
									dispatch({
										conjunction: key as string,
										type: 'SET_CONJUNCTION',
									})
								}
								selectedKey={conjunction}
							>
								{(item) => (
									<Option key={item.value}>
										{item.label}
									</Option>
								)}
							</Picker>
						</div>

						<span className="text-2 text-secondary">
							{conjunction === 'OR'
								? Liferay.Language.get('any-rule-must-match')
								: Liferay.Language.get('all-rule-must-match')}

							{' · '}

							{Liferay.Util.sub(
								Liferay.Language.get('x-criteria'),
								rules.length
							)}
						</span>
					</div>

					<div className="px-3 py-2">
						{rules.map((rule, index) => (
							<Fragment key={rule.id}>
								{index > 0 ? (
									<div
										aria-hidden="true"
										className="align-items-center d-flex mb-3"
									>
										<span className="audience-builder-conjunction-line border-top" />

										<span className="font-weight-semi-bold mx-3 text-3 text-secondary text-uppercase">
											{conjunction === 'OR'
												? Liferay.Language.get('or')
												: Liferay.Language.get('and')}
										</span>

										<span className="border-top flex-grow-1" />
									</div>
								) : null}

								<RuleRow
									audiencesCriteria={
										audiencesCriteriasByKey[rule.attribute]
									}
									index={index}
									items={dndItems}
									onAddRule={handleAddRule}
									onChange={(newRule) =>
										dispatch({
											index,
											rule: newRule,
											type: 'CHANGE_RULE',
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
				<div
					className={classNames(
						'audience-builder-drop-zone border-top p-4',
						{
							'audience-builder-drop-zone--over': isOver,
						}
					)}
					ref={drop}
				>
					<ClayEmptyState
						description={Liferay.Language.get(
							'to-create-a-new-audience-drag-items-from-the-sidebar-and-drop-them-here'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.svg`}
						title={Liferay.Language.get('no-criteria-yet')}
					/>
				</div>
			)}
		</div>
	);
}
