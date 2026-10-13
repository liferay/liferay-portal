/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {LaunchEntry} from '../api/launches';
import useLaunchEntrySummary from '../hooks/useLaunchEntrySummary';

export type LaunchEntrySummaryField =
	| 'author'
	| 'modified'
	| 'space'
	| 'type'
	| 'version';

interface Props {
	field: LaunchEntrySummaryField;
	getLaunchEntrySummaryURL: string;
	itemData: LaunchEntry;
	portletNamespace: string;
}

export default function LaunchEntrySummaryCell({
	field,
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
		return null;
	}

	if (field === 'modified') {
		return (
			<span>
				{new Date(launchEntrySummary.modified).toLocaleString()}
			</span>
		);
	}

	return <span>{launchEntrySummary[field]}</span>;
}
