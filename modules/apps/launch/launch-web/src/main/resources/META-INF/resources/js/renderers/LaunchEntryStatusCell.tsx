/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {LaunchEntry} from '../api/launches';
import WorkflowStatusLabel from '../components/WorkflowStatusLabel';
import useLaunchEntrySummary from '../hooks/useLaunchEntrySummary';

interface Props {
	getLaunchEntrySummaryURL: string;
	itemData: LaunchEntry;
	portletNamespace: string;
}

export default function LaunchEntryStatusCell({
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

	return <WorkflowStatusLabel workflowStatus={launchEntrySummary.status} />;
}
