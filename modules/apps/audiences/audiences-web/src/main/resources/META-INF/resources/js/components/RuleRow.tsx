/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import React from 'react';

import {getOperatorLabel} from '../constants/operators';
import {AudiencesCriteria, Rule} from '../types';

interface IProps {
	audiencesCriteria?: AudiencesCriteria;
	onChange: (rule: Rule) => void;
	onDelete: () => void;
	onDuplicate: () => void;
	rule: Rule;
}

export default function RuleRow({
	audiencesCriteria,
	onChange,
	onDelete,
	onDuplicate,
	rule,
}: IProps) {
	if (!audiencesCriteria) {
		return null;
	}

	const {label, operators, options, type} = audiencesCriteria;

	return (
		<div className="align-items-center audience-builder-rule d-flex justify-content-between mb-3 p-3">
			<div className="align-items-center c-gap-3 d-flex">
				<span className="font-weight-semi-bold text-4 text-nowrap">
					{label}
				</span>

				<Picker
					aria-label={Liferay.Language.get('operator')}
					className="flex-shrink-0 form-control-sm w-auto"
					items={operators.map((operator) => ({
						label: getOperatorLabel(operator, type),
						value: operator,
					}))}
					onSelectionChange={(key) =>
						onChange({...rule, operator: key as string})
					}
					selectedKey={rule.operator}
				>
					{(item) => <Option key={item.value}>{item.label}</Option>}
				</Picker>

				{options.length ? (
					<Picker
						aria-label={Liferay.Language.get('value')}
						className="flex-shrink-0 form-control-sm w-auto"
						items={options}
						onSelectionChange={(key) =>
							onChange({...rule, value: key as string})
						}
						selectedKey={rule.value}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>
				) : (
					<ClayInput
						aria-label={Liferay.Language.get('value')}
						className="form-control-sm text-3"
						onChange={(event) =>
							onChange({...rule, value: event.target.value})
						}
						placeholder={type === 'date' ? 'YYYY-MM-DD' : undefined}
						type={type === 'number' ? 'number' : 'text'}
						value={rule.value}
					/>
				)}
			</div>

			<div className="align-items-baseline d-flex">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('duplicate')}
					borderless
					displayType="secondary"
					onClick={onDuplicate}
					size="sm"
					symbol="copy"
					title={Liferay.Language.get('duplicate')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('delete')}
					borderless
					displayType="secondary"
					onClick={onDelete}
					size="sm"
					symbol="times-circle"
					title={Liferay.Language.get('delete')}
				/>
			</div>
		</div>
	);
}
