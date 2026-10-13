/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import React from 'react';

import {LaunchEntry} from '../api/launches';
import useLaunchEntrySummary from '../hooks/useLaunchEntrySummary';

interface Props {
	getLaunchEntrySummaryURL: string;
	itemData: LaunchEntry;
	portletNamespace: string;
}

export default function LaunchEntryTitleCell({
	getLaunchEntrySummaryURL,
	itemData,
	portletNamespace,
}: Props) {
	const launchEntrySummary = useLaunchEntrySummary(
		itemData,
		portletNamespace,
		getLaunchEntrySummaryURL
	);

	if (!launchEntrySummary) {
		return <span className="text-secondary">&mdash;</span>;
	}

	if (!launchEntrySummary.editURL) {
		return (
			<div className="table-list-title">{launchEntrySummary.title}</div>
		);
	}

	return (
		<div className="table-list-title">
			<ClayLink data-senna-off href={launchEntrySummary.editURL}>
				{launchEntrySummary.title}
			</ClayLink>
		</div>
	);
}
