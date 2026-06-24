/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClaySelectWithOption} from '@clayui/form';
import {
	RowBuilder,
	useScreenReaderAnnounce,
} from '@liferay/layout-js-components-web';
import React, {useState} from 'react';
import {v4 as uuidv4} from 'uuid';

import {AudiencesCriteria, AudiencesCriteriaType, Rule} from '../types';
import RuleRow from './RuleRow';

interface IProps {
	audiencesCriteriaTypes: AudiencesCriteriaType[];
}

function createRule(audiencesCriteria: AudiencesCriteria): Rule {
	return {
		attribute: audiencesCriteria.key,
		id: `rule-${uuidv4()}`,
		operator: audiencesCriteria.operators[0] || '',
		value: audiencesCriteria.options[0]?.value || '',
	};
}

export default function ConditionsPanel({audiencesCriteriaTypes}: IProps) {
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

	const [conjunction, setConjunction] = useState('and');
	const [rules, setRules] = useState<Rule[]>(() =>
		(audiencesCriteriaTypes[0]?.audiencesCriterias ?? []).map(createRule)
	);

	const handleDuplicate = (index: number) => {
		const newRules = [...rules];

		newRules.splice(index + 1, 0, {
			...rules[index],
			id: `rule-${uuidv4()}`,
		});

		setRules(newRules);

		announce(Liferay.Language.get('a-condition-was-duplicated'));
	};

	return (
		<div className="card d-flex flex-column flex-grow-1 mt-4 overflow-hidden">
			<div className="px-4 py-3">
				<p className="font-weight-bold mb-0 text-6">
					{Liferay.Language.get('conditions')}
				</p>
			</div>

			<div className="card-body d-flex flex-column overflow-hidden p-4">
				<div className="flex-grow-1 overflow-auto">
					<div className="border overflow-hidden rounded">
						<div className="align-items-center bg-lighter d-flex p-3">
							<ClaySelectWithOption
								aria-label={Liferay.Language.get('conjunction')}
								className="bg-white font-weight-semi-bold form-control-sm mr-3 text-2 text-center text-uppercase w-auto"
								onChange={(event) =>
									setConjunction(event.target.value)
								}
								options={[
									{
										label: Liferay.Language.get('and'),
										value: 'and',
									},
									{
										label: Liferay.Language.get('or'),
										value: 'or',
									},
								]}
								value={conjunction}
							/>

							<span className="text-2 text-secondary">
								{conjunction === 'or'
									? Liferay.Language.get(
											'any-rule-must-match'
										)
									: Liferay.Language.get(
											'all-rule-must-match'
										)}

								{' · '}

								{Liferay.Util.sub(
									Liferay.Language.get('x-criteria'),
									rules.length
								)}
							</span>
						</div>

						<div className="py-2">
							<RowBuilder<Rule>
								createItem={() =>
									createRule(audiencesCriterias[0])
								}
								hideAddButton
								itemClassName="audience-builder-rule pr-4 py-3"
								items={rules}
								labels={{
									add: Liferay.Language.get('add-condition'),
									addedAnnouncement: Liferay.Language.get(
										'a-condition-was-added'
									),
									delete: Liferay.Language.get('delete'),
									deletedAnnouncement: Liferay.Language.get(
										'a-condition-was-removed'
									),
									list: Liferay.Language.get('conditions'),
								}}
								renderItem={({item, onChange}) => (
									<RuleRow
										audiencesCriteria={
											audiencesCriteriasByKey[
												item.attribute
											]
										}
										onChange={onChange}
										rule={item}
									/>
								)}
								renderItemActions={({index}) => (
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'duplicate'
										)}
										borderless
										className="align-self-baseline"
										displayType="secondary"
										onClick={() => handleDuplicate(index)}
										size="sm"
										symbol="copy"
										title={Liferay.Language.get(
											'duplicate'
										)}
									/>
								)}
								renderItemSeparator={() => (
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
								)}
								setItems={setRules}
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
