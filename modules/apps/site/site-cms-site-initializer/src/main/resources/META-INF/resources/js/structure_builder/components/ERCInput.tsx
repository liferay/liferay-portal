/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

export default function ERCInput({
	disabled,
	error,
	onValueChange,
	value: initialValue,
	helpText = Liferay.Language.get(
		'unique-key-for-referencing-the-object-definition'
	),
}: {
	disabled?: boolean;
	error?: string;
	helpText?: string;
	onValueChange: (value: string) => void;
	value: string;
}) {
	const id = useId();

	const [value, setValue] = useState(initialValue);

	return (
		<ClayForm.Group className={classNames({'has-error': error})}>
			<label htmlFor={id}>
				{Liferay.Language.get('erc')}

				<ClayIcon
					className="ml-1 reference-mark"
					focusable="false"
					role="presentation"
					symbol="asterisk"
				/>
			</label>

			<ClayIcon
				className="lfr-portal-tooltip ml-1 text-secondary"
				data-title={helpText}
				focusable="false"
				role="dialog"
				symbol="question-circle"
				tabIndex={0}
			/>

			<ClayInput
				disabled={disabled}
				id={id}
				onBlur={() => onValueChange(value)}
				onChange={(event) => setValue(event.target.value)}
				required
				type="text"
				value={value}
			/>

			{error ? <FieldFeedback errorMessage={error} /> : null}
		</ClayForm.Group>
	);
}
