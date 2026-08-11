/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import HelpTooltipIcon from '../../common/components/forms/HelpTooltipIcon';

export default function SlugInput({
	disabled,
	error,
	onValueChange,
	value: initialValue,
}: {
	disabled?: boolean;
	error?: string;
	onValueChange: (value: string) => void;
	value: string;
}) {
	const id = useId();

	const [value, setValue] = useState(initialValue);

	useEffect(() => {
		setValue(initialValue);
	}, [initialValue]);

	return (
		<ClayForm.Group className={classNames({'has-error': error})}>
			<label htmlFor={id}>{Liferay.Language.get('slug')}</label>

			<HelpTooltipIcon
				message={Liferay.Language.get(
					"the-unique-path-for-this-category.-it-will-be-appended-to-the-channel's-friendly-url"
				)}
			/>

			<ClayInput
				disabled={disabled}
				id={id}
				onBlur={() => onValueChange(value)}
				onChange={(event) => setValue(event.target.value)}
				type="text"
				value={value}
			/>

			{error ? <FieldFeedback errorMessage={error} /> : null}
		</ClayForm.Group>
	);
}
