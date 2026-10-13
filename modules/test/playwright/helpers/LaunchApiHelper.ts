/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

export type LaunchEntry = {
	baseVersionKey: string;
	className: string;
	classPK: number;
	draftId: number;
	id: number;
	r_launchSetToLaunchEntries_c_launchSetId: number;
	versionKey?: string;
};

export type LaunchSet = {
	description: string;
	id: number;
	name: string;
};

export class LaunchApiHelper {
	readonly apiHelpers: ApiHelpers;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
	}

	async deleteLaunchSet(launchSetId: number) {
		await this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}launch-sets/${launchSetId}`
		);
	}

	async getLaunchEntries(launchSetId: number): Promise<LaunchEntry[]> {
		const filter = encodeURIComponent(
			`r_launchSetToLaunchEntries_c_launchSetId eq '${launchSetId}'`
		);

		const {items} = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}launch-entries?filter=${filter}&pageSize=100`
		);

		return items || [];
	}

	async getLaunchEntry(
		launchSetId: number,
		className: string
	): Promise<LaunchEntry> {
		const launchEntries = await this.getLaunchEntries(launchSetId);

		const launchEntry = launchEntries.find(
			(candidate) => candidate.className === className
		);

		if (!launchEntry) {
			throw new Error(
				`Launch ${launchSetId} holds no entry of type ${className}`
			);
		}

		return launchEntry;
	}

	async getLaunchSetByName(name: string): Promise<LaunchSet | undefined> {
		const filter = encodeURIComponent(`name eq '${name}'`);

		const {items} = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}launch-sets?filter=${filter}`
		);

		return items?.[0];
	}
}
