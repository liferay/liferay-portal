/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import openCodeEditorModal from './openCodeEditorModal';

interface Props {
	defaultLanguageValue?: string;
	description?: string;
	footer?: React.ReactNode;
	initialValue: string;
	label: string;
	mode: 'text/html' | 'text/javascript';
	onChange: (value: string) => void;
}

export default function CodeEditorField({
	defaultLanguageValue,
	description,
	footer,
	initialValue,
	label,
	mode,
	onChange,
}: Props) {
	const fieldId = useId();

	const [value, setValue] = useState(initialValue);

	return (
		<ClayForm.Group small>
			<div className="align-items-center d-flex justify-content-between">
				<label className="mb-0" htmlFor={fieldId}>
					{label}
				</label>

				<ClayButtonWithIcon
					aria-label={sub(Liferay.Language.get('edit-x'), label)}
					borderless
					displayType="secondary"
					onClick={() =>
						openCodeEditorModal({
							initialValue: value,
							mode,
							onSave: (newValue) => {
								setValue(newValue);

								onChange(newValue);
							},
							title: label,
						})
					}
					size="xs"
					symbol="expand"
					title={sub(Liferay.Language.get('edit-x'), label)}
				/>
			</div>

			{description ? (
				<p className="mb-1 text-2 text-secondary">{description}</p>
			) : null}

			<ClayInput
				component="textarea"
				id={fieldId}
				onBlur={() => onChange(value)}
				onChange={(event) => setValue(event.target.value)}
				value={value}
			/>

			{defaultLanguageValue !== undefined ? (
				<p
					className={classNames(
						'element-variations__default-language-value mt-2 pl-2 text-2 text-break',
						{
							'element-variations__default-language-value--empty':
								!defaultLanguageValue,
							'font-italic': defaultLanguageValue,
							'text-secondary': Boolean(defaultLanguageValue),
						}
					)}
				>
					{defaultLanguageValue ||
						Liferay.Language.get(
							'there-is-no-default-value-to-localize'
						)}
				</p>
			) : null}

			{footer}
		</ClayForm.Group>
	);
}
