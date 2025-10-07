/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {FormError, MultiSelectItem} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React from 'react';

import {useRecipient} from '../../hooks/useRecipient';
import {NotificationTemplateError} from '../EditNotificationTemplate';
import {Recipient} from './Recipient';

interface PrimaryRecipientProps {
	errors: FormError<NotificationTemplate & NotificationTemplateError>;
	learnResources: ILearnResourceContext;
	recipientOptions: LabelValueObject[];
	roles: MultiSelectItem[];
	selectedLocale: Locale;
	setValues: (values: Partial<NotificationTemplate>) => void;
	userGroups: MultiSelectItem[];
	values: NotificationTemplate;
}

export function PrimaryRecipient({
	errors,
	learnResources,
	recipientOptions,
	roles,
	selectedLocale,
	setValues,
	userGroups,
	values,
}: PrimaryRecipientProps) {
	const [recipient] = values.recipients as EmailRecipients[];

	const {handleChange, handleTypeChange} = useRecipient(setValues, values);

	return (
		<>
			<Recipient
				disabled={values.system}
				displayType="column"
				error={errors.to}
				id="to"
				label={Liferay.Language.get('recipients')}
				learnResources={learnResources}
				onChange={handleChange}
				onTypeChange={handleTypeChange}
				recipientOptions={recipientOptions}
				required
				roles={roles}
				selectedLocale={selectedLocale}
				userEmailAddressLocalized
				userGroups={userGroups}
				values={values}
			/>

			<>
				<ClayForm.Group className="ml-1 row">
					<div className="mr-2">
						<ClayCheckbox
							checked={recipient.singleRecipient}
							disabled={values.system}
							label={Liferay.Language.get(
								'send-emails-separately'
							)}
							onChange={({target: {checked}}) => {
								setValues({
									...values,
									recipients: [
										{
											...recipient,
											singleRecipient: checked,
										},
									],
								});
							}}
						/>
					</div>

					<ClayTooltipProvider>
						<span
							title={Liferay.Language.get(
								'each-to-recipient-will-receive-separate-emails'
							)}
						>
							<ClayIcon
								className="lfr__notification-template-email-notification-settings-tooltip-icon"
								symbol="question-circle-full"
							/>
						</span>
					</ClayTooltipProvider>
				</ClayForm.Group>
			</>
		</>
	);
}
