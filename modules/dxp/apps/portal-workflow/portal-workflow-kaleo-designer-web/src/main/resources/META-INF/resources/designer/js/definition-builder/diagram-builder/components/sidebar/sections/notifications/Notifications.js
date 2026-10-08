/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useEffect, useState} from 'react';

import {DefinitionBuilderContext} from '../../../../../DefinitionBuilderContext';
import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import NotificationsInfo from './NotificationsInfo';

const Notifications = (props) => {
	const {accountEntryId} = useContext(DefinitionBuilderContext);
	const {selectedItem} = useContext(DiagramBuilderContext);

	const {notifications} = selectedItem.data;
	const [sections, setSections] = useState([{identifier: `${Date.now()}-0`}]);

	useEffect(() => {
		const sectionsData = [];

		if (notifications) {
			for (let i = 0; i < notifications.name.length; i++) {
				let notificationTypes = notifications.notificationTypes[i];

				if (notificationTypes === undefined) {
					notificationTypes = '';
				}

				sectionsData.push({
					description: notifications.description[i],
					executionType: notifications.executionType[i],
					identifier: `${Date.now()}-${i}`,
					name: notifications.name[i],
					notificationTypes,
					recipients: notifications.recipients[i],
					template: notifications.template[i],
					templateLanguage: notifications.templateLanguage[i],
				});
			}

			setSections(sectionsData);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return sections.map(({identifier}, index) => (
		<NotificationsInfo
			{...props}
			accountEntryId={accountEntryId}
			identifier={identifier}
			index={index}
			key={`section-${identifier}`}
			sectionsLength={sections?.length}
			setSections={setSections}
		/>
	));
};

export default Notifications;
