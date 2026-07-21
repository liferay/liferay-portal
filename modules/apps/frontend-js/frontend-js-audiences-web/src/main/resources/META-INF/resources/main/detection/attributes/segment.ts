/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {log} from '../../log';

declare const Analytics: any;

const AC_SEGMENTS_SESSION_STORAGE_KEY = 'liferay.audiences.acSegments';

const ANALYTICS_WAIT_INTERVAL = 100;

const ANALYTICS_WAIT_TIMEOUT = 10000;

let acSegmentsRequest: Promise<void> | undefined;

export function getSegment(): Set<string> {
	const cachedAcSegments = readAcSegmentsCache();

	if (cachedAcSegments === undefined) {
		requestAcSegments();

		return new Set();
	}

	return cachedAcSegments;
}

function getCurrentUserId(): string {
	return Liferay.ThemeDisplay.getUserId();
}

function readAcSegmentsCache(): Set<string> | undefined {
	try {
		const value = sessionStorage.getItem(AC_SEGMENTS_SESSION_STORAGE_KEY);

		if (value === null) {
			return undefined;
		}

		const {segments, userId} = JSON.parse(value);

		if (userId !== getCurrentUserId()) {
			return undefined;
		}

		return new Set(segments);
	}
	catch (error: any) {
		log(
			`Unable to read the cached Analytics Cloud segments: ${
				error.message || error
			}`
		);

		return undefined;
	}
}

function requestAcSegments(): void {
	if (acSegmentsRequest !== undefined) {
		return;
	}

	acSegmentsRequest = (async () => {
		await waitForAnalytics();

		const acSegments: Set<string> = new Set();

		for (const segment of await Analytics.segment.getBatchSegmentExternalReferenceCodes()) {
			acSegments.add(segment);
		}

		for (const segment of await Analytics.segment.getRealTimeSegmentExternalReferenceCodes()) {
			acSegments.add(segment);
		}

		writeAcSegmentsCache(acSegments);
	})()
		.catch((error: any) => {
			log(
				`Unable to fetch the Analytics Cloud segments: ${
					error.message || error
				}`
			);
		})
		.finally(() => {
			acSegmentsRequest = undefined;
		});
}

async function waitForAnalytics(): Promise<void> {
	let elapsed = 0;

	while (typeof Analytics === 'undefined') {
		if (elapsed >= ANALYTICS_WAIT_TIMEOUT) {
			throw new Error(
				`Unable to get Analytics Cloud segments because 'Analytics' global object is missing`
			);
		}

		await new Promise((resolve) =>
			setTimeout(resolve, ANALYTICS_WAIT_INTERVAL)
		);

		elapsed += ANALYTICS_WAIT_INTERVAL;
	}
}

function writeAcSegmentsCache(acSegments: Set<string>): void {
	try {
		sessionStorage.setItem(
			AC_SEGMENTS_SESSION_STORAGE_KEY,
			JSON.stringify({
				segments: [...acSegments],
				userId: getCurrentUserId(),
			})
		);
	}
	catch (error: any) {
		log(
			`Unable to cache the Analytics Cloud segments: ${
				error.message || error
			}`
		);
	}
}
