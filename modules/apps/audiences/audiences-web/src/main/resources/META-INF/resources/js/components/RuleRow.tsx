/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput, ClaySelectWithOption} from '@clayui/form';
import React from 'react';

import {getOperatorLabel} from '../constants/operators';
import {AudiencesCriteria, Rule} from '../types';

interface IProps {
	audiencesCriteria?: AudiencesCriteria;
	onChange: (rule: Rule) => void;
	rule: Rule;
}

export default function RuleRow({audiencesCriteria, onChange, rule}: IProps) {
	if (!audiencesCriteria) {
		return null;
	}

	const {label, operators, options, type} = audiencesCriteria;

	return (
		<div className="align-items-center d-flex">
			<span className="font-weight-semi-bold mr-3 text-4 text-nowrap">
				{label}
			</span>

			<ClaySelectWithOption
				aria-label={Liferay.Language.get('operator')}
				className="bg-white font-weight-semi-bold form-control-sm mr-3 text-3 text-center w-auto"
				onChange={(event) =>
					onChange({...rule, operator: event.target.value})
				}
				options={operators.map((operator) => ({
					label: getOperatorLabel(operator, type),
					value: operator,
				}))}
				value={rule.operator}
			/>

			{options.length ? (
				<ClaySelectWithOption
					aria-label={Liferay.Language.get('value')}
					className="text-3 w-auto"
					onChange={(event) =>
						onChange({...rule, value: event.target.value})
					}
					options={options}
					value={rule.value}
				/>
			) : (
				<ClayInput
					aria-label={Liferay.Language.get('value')}
					className="text-3"
					onChange={(event) =>
						onChange({...rule, value: event.target.value})
					}
					placeholder={type === 'date' ? 'YYYY-MM-DD' : undefined}
					type={type === 'number' ? 'number' : 'text'}
					value={rule.value}
				/>
			)}
		</div>
	);
}
