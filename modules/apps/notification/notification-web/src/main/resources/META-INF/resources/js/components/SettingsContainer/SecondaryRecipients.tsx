/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import {MultiSelectItem} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React from 'react';

import {useRecipient} from '../../hooks/useRecipient';
import {Recipient} from './Recipient';

interface SecondaryRecipientsProps {
	learnResources: ILearnResourceContext;
	recipientOptions: LabelValueObject[];
	roles: MultiSelectItem[];
	selectedLocale: Locale;
	setValues: (values: Partial<NotificationTemplate>) => void;
	userGroups: MultiSelectItem[];
	values: NotificationTemplate;
}

export function SecondaryRecipient({
	learnResources,
	recipientOptions,
	roles,
	selectedLocale,
	setValues,
	userGroups,
	values,
}: SecondaryRecipientsProps) {
	const {handleChange, handleTypeChange} = useRecipient(setValues, values);

	return (
		<>
			<ClayPanel
				displayTitle={Liferay.Language.get('cc')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<Recipient
						disabled={values.system}
						displayType="row"
						id="cc"
						label={Liferay.Language.get('recipients')}
						learnResources={learnResources}
						onChange={handleChange}
						onTypeChange={handleTypeChange}
						recipientOptions={recipientOptions}
						roles={roles}
						selectedLocale={selectedLocale}
						userGroups={userGroups}
						values={values}
					/>
				</ClayPanel.Body>
			</ClayPanel>

			<ClayPanel
				displayTitle={Liferay.Language.get('bcc')}
				displayType="unstyled"
			>
				<ClayPanel.Body>
					<Recipient
						disabled={values.system}
						displayType="row"
						id="bcc"
						label={Liferay.Language.get('recipients')}
						learnResources={learnResources}
						onChange={handleChange}
						onTypeChange={handleTypeChange}
						recipientOptions={recipientOptions}
						roles={roles}
						selectedLocale={selectedLocale}
						userGroups={userGroups}
						values={values}
					/>
				</ClayPanel.Body>
			</ClayPanel>
		</>
	);
}
