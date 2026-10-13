/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import {
	LaunchEntry,
	LaunchEntrySummary,
	getLaunchEntrySummary,
} from '../api/launches';

const cache = new Map<string, Promise<LaunchEntrySummary>>();

export default function useLaunchEntrySummary(
	launchEntry: LaunchEntry,
	portletNamespace: string,
	resourceURL: string
): LaunchEntrySummary | null {
	const [launchEntrySummary, setLaunchEntrySummary] =
		useState<LaunchEntrySummary | null>(null);

	const {className, classPK, draftId, versionKey} = launchEntry;

	useEffect(() => {
		const key = `${className}|${classPK}|${draftId}|${versionKey}`;

		setLaunchEntrySummary(null);

		if (!cache.has(key)) {
			cache.set(
				key,
				getLaunchEntrySummary({
					className,
					classPK,
					draftId,
					portletNamespace,
					resourceURL,
					versionKey,
				})
			);
		}

		let current = true;

		cache
			.get(key)!
			.then((summary) => {
				if (current) {
					setLaunchEntrySummary(summary);
				}
			})
			.catch(() => {
				cache.delete(key);
			});

		return () => {
			current = false;
		};
	}, [
		className,
		classPK,
		draftId,
		portletNamespace,
		resourceURL,
		versionKey,
	]);

	return launchEntrySummary;
}
