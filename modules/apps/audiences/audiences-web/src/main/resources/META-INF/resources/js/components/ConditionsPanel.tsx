/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import {useScreenReaderAnnounce} from '@liferay/layout-js-components-web';
import React, {Fragment, useState} from 'react';
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

	return (
		<div className="border mt-4 rounded">
			<div className="px-4 py-3">
				<p className="font-weight-bold mb-0 text-6">
					{Liferay.Language.get('conditions')}
				</p>
			</div>

			<div className="align-items-center bg-lighter border-top d-flex p-3">
				<div className="mr-3">
					<Picker
						aria-label={Liferay.Language.get('conjunction')}
						className="form-control-sm w-auto"
						items={[
							{label: Liferay.Language.get('and'), value: 'and'},
							{label: Liferay.Language.get('or'), value: 'or'},
						]}
						onSelectionChange={(key) =>
							setConjunction(key as string)
						}
						selectedKey={conjunction}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
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
							onChange={(newRule) => handleChange(index, newRule)}
							onDelete={() => handleDelete(index)}
							onDuplicate={() => handleDuplicate(index)}
							rule={rule}
						/>
					</Fragment>
				))}
			</div>
		</div>
	);
}
