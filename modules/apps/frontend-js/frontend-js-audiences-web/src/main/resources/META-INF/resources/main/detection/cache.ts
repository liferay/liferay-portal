/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UAParser} from 'ua-parser-js';

import {log} from '../log';

declare const Analytics: any;

/**
 * This is the detection cache. It is used to hold cached values for the
 * lifespan of a detection so that all attributes see the same values during the
 * detection process.
 */
export default class Cache {
	private readonly _acSegments: Promise<Set<string>>;
	private readonly _uaParser: UAParser;

	constructor(abortSignal: AbortSignal) {
		this._acSegments = loadACSegments(abortSignal);
		this._uaParser = new UAParser(navigator.userAgent);
	}

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
			throw new Error('Wait for Analytics global object timed out');
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

	if (abortSignal.aborted) {
		throw new Error('Retrieval of Analytics Cloud segments timed out');
	}

	for (const answer of answers) {
		for (const acSegment of answer) {
			acSegments.add(acSegment);
		}
	}

	log(
		`The Analytics Cloud segments have been retrieved in ${performance.now() - start} ms.`
	);

	return acSegments;
}
