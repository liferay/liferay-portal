/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm, {ClayCheckbox, ClaySelect} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import React from 'react';

import {IMappingRule} from './types';

const TEXT_CASES = [
	{label: Liferay.Language.get('lowercase'), value: 'lower'},
	{label: Liferay.Language.get('uppercase'), value: 'upper'},
];

const TRUNCATE_LENGTHS = [40, 80, 200, 1000];

interface IProps {
	channelFieldMultiple: boolean;
	disabled?: boolean;
	mappingRule: IMappingRule;
	onChange: (mappingRule: IMappingRule) => void;
}

export default function MappingRulesSection({
	channelFieldMultiple,
	disabled,
	mappingRule,
	onChange,
}: IProps) {
	const handleChange = (values: Partial<IMappingRule>) =>
		onChange({...mappingRule, ...values});

	return (
		<div className="container-fluid container-fluid-max-md p-0 p-md-4">
			<ClayPanel
				aria-label={Liferay.Language.get('mapping-rules')}
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('mapping-rules')}
					</h2>

					{disabled && (
						<ClayAlert
							className="mb-0"
							displayType="warning"
							title={Liferay.Language.get('warning')}
						>
							{Liferay.Language.get(
								'mapping-rules-are-not-supported-for-this-field-type'
							)}
						</ClayAlert>
					)}

					<div>
						<h3 className="font-weight-semi-bold mb-2 text-2 text-secondary text-uppercase">
							{Liferay.Language.get('cleanup')}
						</h3>

						<ClayCheckbox
							checked={mappingRule.stripHTMLTags}
							disabled={disabled}
							label={Liferay.Language.get('strip-html-tags')}
							onChange={(event) =>
								handleChange({
									stripHTMLTags: event.target.checked,
								})
							}
						/>

						<ClayCheckbox
							checked={mappingRule.trimWhitespace}
							disabled={disabled}
							label={Liferay.Language.get('trim-whitespace')}
							onChange={(event) =>
								handleChange({
									trimWhitespace: event.target.checked,
								})
							}
						/>
					</div>

					<div>
						<h3 className="font-weight-semi-bold mb-2 text-2 text-secondary text-uppercase">
							{Liferay.Language.get('encoding-format')}
						</h3>

						<ClayCheckbox
							checked={mappingRule.urlEncode}
							disabled={disabled}
							label={Liferay.Language.get('url-encode')}
							onChange={(event) =>
								handleChange({
									urlEncode: event.target.checked,
								})
							}
						/>
					</div>

					<div className="c-gap-1 d-flex flex-column">
						<label>{Liferay.Language.get('case')}</label>

						<ClaySelect
							aria-label={Liferay.Language.get('case')}
							disabled={disabled}
							onChange={(event) =>
								handleChange({
									textCase: event.target.value,
								})
							}
							value={mappingRule.textCase}
						>
							<ClaySelect.Option
								label={Liferay.Language.get('none')}
								value=""
							/>

							{TEXT_CASES.map(({label, value}) => (
								<ClaySelect.Option
									key={value}
									label={label}
									value={value}
								/>
							))}
						</ClaySelect>
					</div>

					<div className="c-gap-1 d-flex flex-column">
						<label>{Liferay.Language.get('truncate')}</label>

						<ClaySelect
							aria-label={Liferay.Language.get('truncate')}
							disabled={disabled}
							onChange={(event) =>
								handleChange({
									truncateLength: Number(event.target.value),
								})
							}
							value={mappingRule.truncateLength}
						>
							<ClaySelect.Option
								label={Liferay.Language.get('none')}
								value={0}
							/>

							{TRUNCATE_LENGTHS.map((truncateLength) => (
								<ClaySelect.Option
									key={truncateLength}
									label={String(truncateLength)}
									value={truncateLength}
								/>
							))}
						</ClaySelect>
					</div>

					{!channelFieldMultiple && (
						<div className="c-gap-1 d-flex flex-column">
							<label>
								{Liferay.Language.get('fallback-logic')}
							</label>

							<ClaySelect
								aria-label={Liferay.Language.get(
									'fallback-logic'
								)}
								disabled={disabled}
								onChange={(event) =>
									handleChange({
										useFirstNonemptySource:
											event.target.value === 'true',
									})
								}
								value={String(
									mappingRule.useFirstNonemptySource
								)}
							>
								<ClaySelect.Option
									label={Liferay.Language.get('no-fallback')}
									value="false"
								/>

								<ClaySelect.Option
									label={Liferay.Language.get(
										'first-nonempty-source'
									)}
									value="true"
								/>
							</ClaySelect>
						</div>
					)}
				</ClayForm.Group>
			</ClayPanel>
		</div>
	);
}
