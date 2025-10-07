/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	MultiSelectItem,
	MultipleSelect,
} from '@liferay/object-js-components-web';
import React from 'react';

import {handleMultiSelectItemsChange} from './multiSelectUtil';

interface RecipientMultipleSelectProps {
	disabled: boolean;
	error?: string;
	id: string;
	label: string;
	name: 'roleName' | 'userGroupName';
	onRecipientsChange: (recipients: EmailNotificationRecipients[]) => void;
	options: MultiSelectItem[];
	placeholder: string;
	required?: boolean;
	searchPlaceholder: string;
	setOptions: (options: MultiSelectItem[]) => void;
}

export function RecipientMultipleSelect({
	disabled,
	error,
	id,
	label,
	name,
	onRecipientsChange,
	options,
	placeholder,
	required,
	searchPlaceholder,
	setOptions,
}: RecipientMultipleSelectProps) {
	return (
		<div className="lfr__notification-template-email-notification-settings-multiple-select">
			<MultipleSelect
				disabled={disabled}
				error={error}
				id={id}
				label={label}
				options={options}
				placeholder={placeholder}
				required={required}
				search
				searchPlaceholder={searchPlaceholder}
				selectAllOption
				setOptions={(items) => {
					const newRecipients = handleMultiSelectItemsChange(
						items,
						name
					);

					onRecipientsChange(newRecipients);

					setOptions(items);
				}}
			/>
		</div>
	);
}
