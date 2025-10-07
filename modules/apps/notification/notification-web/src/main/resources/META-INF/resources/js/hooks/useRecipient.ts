/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function useRecipient(
	setValues: (values: Partial<NotificationTemplate>) => void,
	values: NotificationTemplate
) {
	const [recipient] = values.recipients as EmailRecipients[];

	const handleChange = (
		key: string,
		value:
			| Partial<Liferay.Language.FullyLocalizedValue<string>>
			| string
			| EmailNotificationRecipients[]
	) => {
		setValues({
			...values,
			recipients: [
				{
					...recipient,
					[key]: value,
				},
			],
		});
	};

	const handleTypeChange = (key: string, type: string) => {
		setValues({
			...values,
			recipients: [
				{
					...recipient,
					[`${key}Type`]: type,
					[key]:
						type === 'email' || type === 'term'
							? ''
							: type === 'subscribers'
								? '[%EMAIL_RECIPIENT_ADDRESS%]'
								: [],
				},
			],
		});
	};

	return {handleChange, handleTypeChange};
}
