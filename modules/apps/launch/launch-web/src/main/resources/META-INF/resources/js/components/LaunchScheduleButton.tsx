/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {navigate} from 'frontend-js-web';
import React from 'react';

import openScheduleLaunchModal from '../modals/openScheduleLaunchModal';
import {formatDateTime} from '../util/dateTime';

import type {TimeZoneOption} from '../util/dateTime';

interface Props {
	launchName: string;
	portletNamespace: string;
	published: boolean;
	scheduleLaunchURL: string;
	scheduledDate: string | null;
	timeZoneId: string;
	timeZones: TimeZoneOption[];
}

export default function LaunchScheduleButton({
	launchName,
	portletNamespace,
	published,
	scheduleLaunchURL,
	scheduledDate,
	timeZoneId,
	timeZones,
}: Props) {
	if (scheduledDate) {
		return (
			<ClayLabel displayType="info">
				{`${Liferay.Language.get('scheduled')}: ${formatDateTime(
					new Date(scheduledDate).getTime(),
					Liferay.ThemeDisplay.getBCP47LanguageId(),
					timeZoneId
				)}`}
			</ClayLabel>
		);
	}

	if (published) {
		return null;
	}

	return (
		<ClayButton
			className="text-nowrap"
			displayType="secondary"
			onClick={() =>
				openScheduleLaunchModal({
					launchName,
					onScheduled: (redirect) => {
						if (redirect) {
							navigate(redirect);
						}
						else {
							window.location.reload();
						}
					},
					portletNamespace,
					scheduleLaunchURL,
					timeZoneId,
					timeZones,
				})
			}
			small
			type="button"
		>
			<ClayIcon
				className="inline-item inline-item-before"
				symbol="calendar"
			/>

			{Liferay.Language.get('schedule')}
		</ClayButton>
	);
}
