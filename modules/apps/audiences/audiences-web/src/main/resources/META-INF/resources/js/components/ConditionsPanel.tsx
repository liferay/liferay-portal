/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import React, {Fragment, useState} from 'react';
import {useDrop} from 'react-dnd';
import {v4 as uuidv4} from 'uuid';

import {DRAG_TYPES} from '../constants/dragTypes';
import {AudiencesCriteria, AudiencesCriteriaType, Rule} from '../types';
import RuleRow from './RuleRow';

interface IProps {
	audiencesCriteriaTypes: AudiencesCriteriaType[];
	json?: string;
	namespace: string;
}

interface AttributeDragItem {
	audiencesCriteria: AudiencesCriteria;
	type: string;
}

function createRule(audiencesCriteria: AudiencesCriteria): Rule {
	return {
		attribute: audiencesCriteria.key,
		id: `rule-${uuidv4()}`,
		operator: audiencesCriteria.operators[0] || '',
		value: audiencesCriteria.options[0]?.value || '',
	};
}

function parseCriteria(json?: string): {conjunction: string; rules: Rule[]} {
	if (!json) {
		return {conjunction: 'and', rules: []};
	}

	try {
		const parsed: {
			conjunction?: string;
			rules?: Array<{
				attribute: string;
				operator: string;
				value: string;
			}>;
		} = JSON.parse(json);

		return {
			conjunction: (parsed.conjunction ?? 'AND').toLowerCase(),
			rules: (parsed.rules ?? [])
				.filter((rule) => Boolean(rule.attribute))
				.map((rule) => ({
					attribute: rule.attribute,
					id: `rule-${uuidv4()}`,
					operator: rule.operator,
					value: rule.value,
				})),
		};
	}
	catch {
		return {conjunction: 'and', rules: []};
	}
}

export default function ConditionsPanel({
	audiencesCriteriaTypes,
	json,
	namespace,
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

	const [initialCriteria] = useState(() => parseCriteria(json));
	const [conjunction, setConjunction] = useState(initialCriteria.conjunction);
	const [rules, setRules] = useState<Rule[]>(initialCriteria.rules);

	const serializedJSON = JSON.stringify({
		conjunction: conjunction.toUpperCase(),
		rules: rules.map((rule) => ({
			attribute: rule.attribute,
			operator: rule.operator,
			value: rule.value,
		})),
	});

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
		setRules((currentRules) => {
			const newRule = createRule(audiencesCriteria);

			if (index === undefined) {
				return [...currentRules, newRule];
			}

			const newRules = [...currentRules];

			newRules.splice(index, 0, newRule);

			return newRules;
		});

		announce(Liferay.Language.get('a-condition-was-added'));
	}

	const handleChange = (index: number, newRule: Rule) => {
		setRules((currentRules) =>
			currentRules.map((rule, currentIndex) =>
				currentIndex === index ? newRule : rule
			)
		);
	};

	const handleDelete = (index: number) => {
		setRules((currentRules) =>
			currentRules.filter((rule, currentIndex) => currentIndex !== index)
		);

		announce(Liferay.Language.get('a-condition-was-removed'));
	};

	const handleDuplicate = (index: number) => {
		setRules((currentRules) => {
			const newRules = [...currentRules];

			newRules.splice(index + 1, 0, {
				...currentRules[index],
				id: `rule-${uuidv4()}`,
			});

			return newRules;
		});

		announce(Liferay.Language.get('a-condition-was-duplicated'));
	};

	const handleReorder = (newItems: Array<{id: string}>) => {
		const rulesById = new Map(rules.map((rule) => [rule.id, rule]));

		setRules(
			newItems
				.map((item) => rulesById.get(item.id))
				.filter((rule): rule is Rule => Boolean(rule))
		);
	};

	return (
		<div className="border mt-4 rounded">
			<input
				name={`${namespace}json`}
				type="hidden"
				value={serializedJSON}
			/>

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
										value: 'and',
									},
									{
										label: Liferay.Language.get('or'),
										value: 'or',
									},
								]}
								onSelectionChange={(key) =>
									setConjunction(key as string)
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
							{conjunction === 'or'
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
											{conjunction === 'or'
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
										handleChange(index, newRule)
									}
									onDelete={() => handleDelete(index)}
									onDuplicate={() => handleDuplicate(index)}
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
