/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';
import * as fs from 'fs';

import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TBatchEngineTask = {
	className?: string;
	contentType?: string;
	errorMessage?: string;
	executeStatus?: string;
	externalReferenceCode?: string;
	id?: number;
	processedItemsCount?: number;
	startTime?: string;
	totalItemsCount?: number;
};

export class HeadlessBatchEngineApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-batch-engine/v1.0/';
	}

	async getExportTask(exportTaskId: number): Promise<TBatchEngineTask> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/export-task/${exportTaskId}`
		);
	}

	async getExportTaskContent(exportTaskId: number) {
		const body: Buffer = await (
			await this.apiHelpers.getResponse(
				`${this.apiHelpers.baseUrl}${this.basePath}/export-task/${exportTaskId}/content`
			)
		).body();
		const fileName = '/tmp/' + getRandomString();
		fs.writeFileSync(fileName, body);

		return fileName;
	}

	async getImportTask(importTaskId: number): Promise<TBatchEngineTask> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}import-task/${importTaskId}`
		);
	}

	async waitForImportTask(importTaskId: number): Promise<TBatchEngineTask> {
		let importTask: TBatchEngineTask;

		await expect
			.poll(
				async () => {
					importTask = await this.getImportTask(importTaskId);

					return importTask.executeStatus;
				},
				{intervals: [1000], timeout: 180 * 1000}
			)
			.toMatch(/^(COMPLETED|FAILED)$/);

		return importTask!;
	}
}
