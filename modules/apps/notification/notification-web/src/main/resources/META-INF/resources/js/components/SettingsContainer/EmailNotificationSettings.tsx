/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import {FormError, MultiSelectItem} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {NotificationTemplateError} from '../EditNotificationTemplate';
import {PrimaryRecipient} from './PrimaryRecipients';
import {SecondaryRecipient} from './SecondaryRecipients';
import {Sender} from './Sender';
import {getEmailNotificationRoles} from './rolesUtil';
import {getUserGroups} from './userGroupsUtil';

import './EmailNotificationSettings.scss';

interface EmailNotificationSettingsProps {
	baseResourceURL: string;
	errors: FormError<NotificationTemplate & NotificationTemplateError>;
	learnResources: ILearnResourceContext;
	selectedLocale: Locale;
	setValues: (values: Partial<NotificationTemplate>) => void;
	values: NotificationTemplate;
}

const RECIPIENT_OPTIONS = [
	{
		label: Liferay.Language.get('user-email-address'),
		value: 'email',
	},
	{
		label: Liferay.Language.get('roles'),
		value: 'role',
	},
	Liferay.FeatureFlags['LPD-17564'] && {
		label: Liferay.Language.get('subscribers'),
		value: 'subscribers',
	},
	Liferay.FeatureFlags['LPD-6233'] && {
		label: Liferay.Language.get('definition-of-terms'),
		value: 'term',
	},
	{
		label: Liferay.Language.get('user-groups'),
		value: 'user-group',
	},
] as LabelValueObject[];

export function EmailNotificationSettings({
	baseResourceURL,
	errors,
	learnResources,
	selectedLocale,
	setValues,
	values,
}: EmailNotificationSettingsProps) {
	const [roles, setRoles] = useState<MultiSelectItem[]>([]);
	const [userGroups, setUserGroups] = useState<MultiSelectItem[]>([]);

	useEffect(() => {
		const fetchInitialData = async () => {
			const emailNotificationRoles =
				await getEmailNotificationRoles(baseResourceURL);
			const emailNotificationUserGroups = await getUserGroups();

			setRoles(emailNotificationRoles);
			setUserGroups(emailNotificationUserGroups);
		};

		fetchInitialData();
	}, [baseResourceURL]);

	return (
		<div className="lfr__notification-template-email-notification-settings">
			<ClayPanel
				displayTitle={Liferay.Language.get('sender')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<Sender
						errors={errors}
						selectedLocale={selectedLocale}
						setValues={setValues}
						values={values}
					/>
				</ClayPanel.Body>
			</ClayPanel>

			<ClayPanel
				displayTitle={Liferay.Language.get('primary-recipients')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<PrimaryRecipient
						errors={errors}
						learnResources={learnResources}
						recipientOptions={RECIPIENT_OPTIONS}
						roles={roles}
						selectedLocale={selectedLocale}
						setValues={setValues}
						userGroups={userGroups}
						values={values}
					/>
				</ClayPanel.Body>
			</ClayPanel>

			<ClayPanel
				displayTitle={Liferay.Language.get('secondary-recipients')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<SecondaryRecipient
						learnResources={learnResources}
						recipientOptions={RECIPIENT_OPTIONS}
						roles={roles}
						selectedLocale={selectedLocale}
						setValues={setValues}
						userGroups={userGroups}
						values={values}
					/>
				</ClayPanel.Body>
			</ClayPanel>
		</div>
	);
}
