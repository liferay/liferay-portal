/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Cache} from './cache';
import {Detection} from './detection';
import {log} from './log';
import {TimeoutError} from './timeout_error';
import {formatError, indent, waitForAbort} from './util';

import type {
	AudienceId,
	AudiencesDefinition,
	Handler,
	RunDetectionOptions,
} from './index';

// Global state variables for Audiences module

const audiencePriorities: Map<AudienceId, number> = new Map();
const detectedAudiences = new Set<AudienceId>();
const handlers: Map<AudienceId, Handler[]> = new Map();

export function clear(): void {
	audiencePriorities.clear();
	detectedAudiences.clear();
}

export function clearHandlers(): void {
	handlers.clear();
}

export function get(): Set<AudienceId> {
	return new Set(detectedAudiences);
}

export function getPriority(audienceId: AudienceId): number {
	return audiencePriorities.get(audienceId) ?? Infinity;
}

export async function runDetection(
	audiencesDefinitionURL: string,
	options?: RunDetectionOptions
): Promise<void> {
	try {

		// Clear global state

		clear();

		// Start the timeout

		const signal = createRunDetectionTimeoutAbortSignal(options?.timeout);

		// Start retrieving external dependencies eagerly

		const cache = new Cache(signal);

		// Download audiences definition and update priorities

		const audiencesDefinition = await downloadAudiencesDefinition(
			audiencesDefinitionURL,
			signal
		);

		for (let i = 0; i < audiencesDefinition.audiences.length; i++) {
			audiencePriorities.set(audiencesDefinition.audiences[i].id, i);
		}

		// Run the detection and update detected audiences

		const detection = new Detection(audiencesDefinition);

		const matches: AudienceId[] = [];

		try {
			await detection.run(cache, signal, matches);
		}
		catch (error: any) {
			if (error instanceof TimeoutError) {
				log(
					`Detection timeout of ${options?.timeout} milliseconds reached`
				);
			}
			else {
				throw error;
			}
		}

		for (const match of matches) {
			detectedAudiences.add(match);
		}
	}
	catch (error: any) {
		log(
			`Audiences detection failed with error:\n${indent(2, true, formatError(error))}`
		);
	}
}

export function on(audienceId: AudienceId, handler: Handler): void {
	const handlerName = handler.name ? handler.name : '<anonymous>';

	log(`Adding handler '${handlerName}' for audience '${audienceId}'`);

	let audienceHandlers = handlers.get(audienceId);

	if (!audienceHandlers) {
		audienceHandlers = [];
		handlers.set(audienceId, audienceHandlers);
	}

	audienceHandlers.push(handler);
}

export async function runHandlers(): Promise<void> {
	try {
		const audienceIds = get();

		await Promise.allSettled(
			[...audienceIds]
				.filter((audienceId) => handlers.has(audienceId))
				.map(async (audienceId) => {
					await Promise.allSettled(

						// @ts-ignore

						handlers.get(audienceId).map(async (handler) => {
							const handlerName = handler.name ?? 'anonymous';

							log(
								`Running handler '${handlerName}' for audience '${audienceId}'`
							);

							try {
								await handler();
							}
							catch (error: any) {
								log(
									`Handler '${handlerName}' of audience '${audienceId}' failed with error:\n${formatError(error)}`
								);
							}
						})
					);
				})
		);
	}
	finally {
		clearHandlers();
	}
}

export function setLogEnabled(enabled: boolean) {
	log.enabled = enabled;
}

function createRunDetectionTimeoutAbortSignal(timeout?: number): AbortSignal {
	const abortController = new AbortController();

	if (timeout) {
		setTimeout(() => abortController.abort(), timeout);
	}

	return abortController.signal;
}

/**
 * @throws TimeoutError if download timed out
 * @throws Error if anything (e.g: download or parsing) went wrong
 */
async function downloadAudiencesDefinition(
	audiencesDefinitionURL: string,
	signal: AbortSignal
): Promise<AudiencesDefinition> {
	const response: void | Response = await Promise.race([
		waitForAbort(signal),

		// eslint-disable-next-line @liferay/portal/no-global-fetch
		fetch(audiencesDefinitionURL, {signal}),
	]);

	if (!response) {
		throw new TimeoutError('Download of audiences definition');
	}

	if (!response.ok) {
		throw new Error(
			`Server returned: ${response.status} ${response.statusText}`
		);
	}

	return await response.json();
}
