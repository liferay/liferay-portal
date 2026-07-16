/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Detection} from './detection';
import {log} from './log';
import {store} from './store';

import type {AudienceId, AudiencesDefinition, Handler} from './index';

interface HandlersMap {
	[audienceId: AudienceId]: Handler[];
}

const handlers: HandlersMap = {};

let priorities: Map<AudienceId, number> = new Map();

export function clear(): void {
	store.clear();
}

export function clearHandlers(): void {
	for (const audienceId of Object.keys(handlers)) {
		delete handlers[audienceId];
	}
}

export function get(): Set<AudienceId> {
	return store.getAudienceIds();
}

export function getPriority(audienceId: AudienceId): number {
	const priority = priorities.get(audienceId);

	return priority === undefined ? Infinity : priority;
}

export async function runDetection(
	audiencesDefinitionURL: string
): Promise<void> {
	let response;

	try {

		// eslint-disable-next-line @liferay/portal/no-global-fetch
		response = await fetch(audiencesDefinitionURL);
	}
	catch (error) {
		throw new Error(
			`Unable to fetch '${audiencesDefinitionURL}': ${getErrorMessage(error)}`
		);
	}

	if (!response.ok) {
		throw new Error(
			`Request to fetch ${audiencesDefinitionURL} returned an error: ` +
				`${response.status} ${response.statusText}`
		);
	}

	let audiencesDefinition: AudiencesDefinition;

	try {
		audiencesDefinition = await response.json();
	}
	catch (error) {
		throw new Error(
			`Unable to parse '${audiencesDefinitionURL}': ${getErrorMessage(error)}`
		);
	}

	priorities = new Map(
		audiencesDefinition.audiences.map((audience, index) => [
			audience.id,
			index,
		])
	);

	const detection = new Detection(audiencesDefinition);

	let matches;

	try {
		matches = await detection.run();
	}
	catch (error) {
		throw new Error(
			`There was an error running audiences detection: ${getErrorMessage(error)}`
		);
	}

	const audienceIds = store.getAudienceIds();

	for (const match of matches) {
		audienceIds.add(match);
	}

	store.setAudienceIds(audienceIds);
}

export function on(audienceId: AudienceId, handler: Handler): void {
	log(
		`Adding handler '${handler.name ?? 'anonymous'}' for audience '${audienceId}'`
	);

	if (!handlers[audienceId]) {
		handlers[audienceId] = [];
	}

	handlers[audienceId].push(handler);
}

export async function runHandlers(): Promise<void> {
	const audienceIds = get();

	for (const audienceId of audienceIds) {
		if (!handlers[audienceId]) {
			continue;
		}

		for (const handler of handlers[audienceId]) {
			const handlerName = handler.name ?? 'anonymous';

			log(
				`Running handler '${handlerName}' for audience '${audienceId}'`
			);

			try {
				await handler();
			}
			catch (error) {
				throw new Error(
					`There was an error running handler '${handlerName}' of audience ` +
						`'${audienceId}': ${getErrorMessage(error)}`
				);
			}
		}
	}
}

export function setLogEnabled(enabled: boolean) {
	log.enabled = enabled;
}

function getErrorMessage(error: unknown): string {
	if (error instanceof Error) {
		return error.message;
	}

	return String(error);
}
