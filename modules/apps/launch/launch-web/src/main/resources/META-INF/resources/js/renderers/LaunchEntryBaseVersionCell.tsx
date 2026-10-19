/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React from 'react';

import {
	LaunchEntry,
	getLaunchEntryVersions,
	rebaseLaunchEntry,
} from '../api/launches';
import useLaunchEntrySummary from '../hooks/useLaunchEntrySummary';
import openSelectLaunchEntryVersionModal from '../modals/openSelectLaunchEntryVersionModal';

interface Props {
	getLaunchEntrySummaryURL: string;
	getLaunchEntryVersionsURL: string;
	itemData: LaunchEntry;
	loadData: () => void;
	portletNamespace: string;
	published: boolean;
}

export default function LaunchEntryBaseVersionCell({
	getLaunchEntrySummaryURL,
	getLaunchEntryVersionsURL,
	itemData,
	loadData,
	portletNamespace,
	published,
}: Props) {
	const launchEntrySummary = useLaunchEntrySummary(
		{...itemData, draftId: 0, versionKey: itemData.baseVersionKey},
		portletNamespace,
		getLaunchEntrySummaryURL
	);

	function handleClick(event: React.MouseEvent) {
		event.preventDefault();

		getLaunchEntryVersions({
			className: itemData.className,
			classPK: itemData.classPK,
			portletNamespace,
			resourceURL: getLaunchEntryVersionsURL,
		})
			.then((launchEntryVersions) =>
				openSelectLaunchEntryVersionModal({
					baseVersionKey: itemData.baseVersionKey,
					launchEntryVersions,
					onSelect: (baseVersionKey) =>
						rebaseLaunchEntry({
							baseVersionKey,
							className: itemData.className,
							classPK: itemData.classPK,
							id: itemData.id,
						})
							.then(() => loadData())
							.catch((exception: Error) =>
								openToast({
									message: escapeHTML(exception.message),
									type: 'danger',
								})
							),
				})
			)
			.catch((exception: Error) =>
				openToast({
					message: escapeHTML(exception.message),
					type: 'danger',
				})
			);
	}

	if (!launchEntrySummary) {
		return <span className="text-secondary">&mdash;</span>;
	}

	if (published) {
		return <span>{launchEntrySummary.version}</span>;
	}

	return (
		<ClayLink href="#" onClick={handleClick}>
			{launchEntrySummary.version}
		</ClayLink>
	);
}
