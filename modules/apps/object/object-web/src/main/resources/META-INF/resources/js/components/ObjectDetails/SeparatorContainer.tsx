/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SeparatorFields} from '@liferay/friendly-url-web';
import {FormError} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {Error} from '../../utils/errors';

interface SeparatorContainerProps {
	disabled?: boolean;
	errors: FormError<ObjectDefinition>;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setErrors?: (errors: Error) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

const SEPARATOR_TEXT = {
	helpText: Liferay.Language.get(
		'modifying-these-values-can-impact-existing-urls-and-seo'
	),
	label: Liferay.Language.get('object-entry-url-separator'),
	url: 'http://www.sitename.com',
	warningMessage: Liferay.Language.get(
		"using-the-l-separator-disables-the-ability-to-override-an-object-entry's-friendly-url"
	),
};

export function hasLegacySeparator(value: any) {
	return value === 'l';
}

export function SeparatorContainer({
	disabled,
	errors,
	onSubmit,
	setErrors,
	setValues,
	values,
}: SeparatorContainerProps) {
	const {helpText, label, url, warningMessage} = SEPARATOR_TEXT;

	const [warnings, setWarnings] = useState({friendlyURLSeparator: ''});

	const handleChange = (value: string) => {
		setValues({friendlyURLSeparator: value});

		if (hasLegacySeparator(value)) {
			setValues({
				enableFriendlyURLCustomization: false,
			});

			setWarnings({friendlyURLSeparator: warningMessage});
		}
		else {
			setWarnings({friendlyURLSeparator: ''});
		}
	};

	const handleOnBlur = (
		event: React.FocusEvent<HTMLInputElement, Element>
	) => {
		event.stopPropagation();

		if (setErrors) {
			setErrors({});
		}

		if (onSubmit) {
			onSubmit();
		}
	};

	return (
		<>
			<SeparatorFields
				disabled={disabled}
				errors={{fields: errors}}
				fields={[
					{
						defaultValue: values.name as string,
						handleChange,
						handleOnBlur,
						helpText,
						label,
						name: 'friendlyURLSeparator',
						value: values.friendlyURLSeparator as string,
					},
				]}
				hideReset={true}
				url={url}
				warnings={{fields: warnings}}
			/>
		</>
	);
}
