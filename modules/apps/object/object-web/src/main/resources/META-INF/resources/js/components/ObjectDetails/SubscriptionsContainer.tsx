/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {Toggle} from '@liferay/object-js-components-web';
import {createPortletURL} from 'frontend-js-web';
import React from 'react';

import './SubscriptionsContainer.scss';

interface SubscriptionsContainerProps {
	hasUpdateObjectDefinitionPermission: boolean;
	isLinkedObjectDefinition?: boolean;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

const notificationsTemplatesURL = createPortletURL(
	Liferay.ThemeDisplay.getLayoutRelativeControlPanelURL(),
	{
		p_p_id: 'com_liferay_notification_web_internal_portlet_NotificationTemplatesPortlet',
		p_p_lifecycle: 0,
		p_p_state: 'maximized',
	}
);

export function SubscriptionsContainer({
	hasUpdateObjectDefinitionPermission,
	isLinkedObjectDefinition,
	onSubmit,
	setValues,
	values,
}: SubscriptionsContainerProps) {
	const disabled =
		!hasUpdateObjectDefinitionPermission || isLinkedObjectDefinition;

	return (
		<div className="lfr-objects__subscriptions-container">
			<ClayForm.Group>
				<Toggle
					disabled={disabled}
					label={Liferay.Language.get('enable-entry-subscriptions')}
					name="enableObjectEntrySubscription"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							enableObjectEntrySubscription:
								!values.enableObjectEntrySubscription,
						})
					}
					toggled={values.enableObjectEntrySubscription}
				/>

				{values.enableObjectEntrySubscription && (
					<>
						<div className="lfr-objects__subscriptions-container-description">
							<Text as="p" color="secondary" size={3}>
								{Liferay.Language.get(
									'allow-users-to-subscribe-to-folders-and-entries'
								)}
							</Text>
						</div>

						<ClayButton
							aria-label={Liferay.Language.get(
								'go-to-notification-templates'
							)}
							className="lfr-objects__subscriptions-container-notifications-button"
							displayType="secondary"
							onClick={() => {
								window.open(
									notificationsTemplatesURL,
									'_blank'
								);
							}}
							size="sm"
						>
							<span className="icon">
								{Liferay.Language.get(
									'go-to-notification-templates'
								)}
							</span>

							<ClayIcon
								className="lfr-objects__subscriptions-container-notifications-button-icon"
								symbol="shortcut"
							/>
						</ClayButton>
					</>
				)}
			</ClayForm.Group>
		</div>
	);
}
