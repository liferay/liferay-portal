/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormError, Input} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import React from 'react';

import {NotificationTemplateError} from '../EditNotificationTemplate';

interface SenderProps {
	errors: FormError<NotificationTemplate & NotificationTemplateError>;
	selectedLocale: Locale;
	setValues: (values: Partial<NotificationTemplate>) => void;
	values: NotificationTemplate;
}

export function Sender({
	errors,
	selectedLocale,
	setValues,
	values,
}: SenderProps) {
	const [recipient] = values.recipients as EmailRecipients[];

	return (
		<div className="row">
			<div className="col-lg-6">
				<Input
					error={errors.from}
					id="fromAddress"
					label={Liferay.Language.get('email-address')}
					name="fromAddress"
					onChange={({target}) =>
						setValues({
							...values,
							recipients: [
								{
									...recipient,
									from: target.value,
								},
							],
						})
					}
					required
					value={recipient.from}
				/>
			</div>

			<div className="col-lg-6">
				<InputLocalized
					error={errors.fromName}
					id="fromName"
					label={Liferay.Language.get('name')}
					name="fromName"
					onChange={(translation) => {
						setValues({
							...values,
							recipients: [
								{
									...recipient,
									fromName: translation,
								},
							],
						});
					}}
					placeholder=""
					required
					selectedLocale={selectedLocale}
					translations={recipient.fromName}
				/>
			</div>
		</div>
	);
}
