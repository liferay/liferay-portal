/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UAParser} from 'ua-parser-js';

import {log} from './log';
import {TimeoutError} from './timeout_error';

declare const Analytics: any;

/**
 * This is the detection cache. It is used to hold cached values for the
 * lifespan of a detection so that all attributes see the same values during the
 * detection process.
 */
export class Cache {
	private readonly _acSegments: Promise<Set<string>>;
	private readonly _uaParser: UAParser;

	/**
	 * Start retrieving external dependencies eagerly to store them in the
	 * cache.
	 *
	 * This method never throws (timeouts throw in the getter methods, not
	 * here).
	 */
	constructor(abortSignal: AbortSignal) {
		this._acSegments = loadACSegments(abortSignal);

		// The segments are requested as soon as the cache is created, so when a
		// detection is aborted before any audience asks for them, the rejection
		// has no one waiting for it. Mark it as handled here to keep it from
		// surfacing as an unhandled rejection. Callers of getACSegments() still
		// get the rejection.

		this._acSegments.catch(() => {});

		this._uaParser = new UAParser(navigator.userAgent);
	}

	/**
	 * @throws TimeoutError if detection timed out
	 * @throws Error if anything went wrong
	 */
	async getACSegments(): Promise<Set<string>> {
		return this._acSegments;
	}

	getUAParser(): UAParser {
		return this._uaParser;
	}
}

async function loadACSegments(abortSignal: AbortSignal): Promise<Set<string>> {
	let start;

	// Wait for Analytics global object to be ready

	log(`Waiting for 'Analytics' global object...`);

	start = performance.now();

	while (typeof Analytics === 'undefined') {
		await new Promise((resolve) => setTimeout(resolve, 100));

		if (abortSignal.aborted) {
			throw new TimeoutError('Waiting for Analytics global object');
		}
	}

	log(
		`The 'Analytics' global object showed up in ${performance.now() - start} ms`
	);

	// Request AC segments

	const acSegments: Set<string> = new Set();

	log(`Getting Analytics Cloud segments...`);

	start = performance.now();

	const answers = await Promise.all([
		Analytics.segment.getBatchSegmentExternalReferenceCodes(),
		Analytics.segment.getRealTimeSegmentExternalReferenceCodes(),
	]);

	log(
		`The Analytics Cloud segments have been retrieved in ${performance.now() - start} ms.`
	);

	if (abortSignal.aborted) {
		throw new TimeoutError('Retrieval of Analytics Cloud segments');
	}

	for (const answer of answers) {
		for (const acSegment of answer) {
			acSegments.add(acSegment);
		}
	}

	return acSegments;
}
