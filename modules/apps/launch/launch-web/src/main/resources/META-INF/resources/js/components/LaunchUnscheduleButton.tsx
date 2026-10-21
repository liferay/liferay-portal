/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import confirmAndUnscheduleLaunchAction from '../actions/confirmAndUnscheduleLaunchAction';

interface Props {
	scheduled: boolean;
	unscheduleLaunchURL: string;
}

export default function LaunchUnscheduleButton({
	scheduled,
	unscheduleLaunchURL,
}: Props) {
	if (!scheduled) {
		return null;
	}

	return (
		<ClayButton
			className="text-nowrap"
			displayType="secondary"
			onClick={() =>
				confirmAndUnscheduleLaunchAction({unscheduleLaunchURL})
			}
			small
			type="button"
		>
			{Liferay.Language.get('unschedule')}
		</ClayButton>
	);
}
