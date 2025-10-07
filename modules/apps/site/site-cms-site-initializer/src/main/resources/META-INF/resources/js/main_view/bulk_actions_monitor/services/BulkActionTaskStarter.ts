/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {RequestResult} from '../../../common/services/ApiHelper';
import {
	IBulkActionFDSData,
	IBulkActionTaskPage,
	IBulkActionTaskStarter,
	IBulkActionTaskStarterDTO,
	IBulkActionTaskType,
	TBulkActionTaskDTO,
} from '../../../common/types/BulkActionTask';
import {
	displayCreateTaskErrorToast,
	displayCreateTaskSuccessToast,
} from '../../../common/utils/toastUtil';
import {
	composeCreateTaskDTO,
	composeCreateTaskURL,
	getTaskReportLink,
} from '../util';
import {getBulkActionTaskMessage} from '../util/notifications';

export class BulkActionTaskStarter implements IBulkActionTaskStarter {
	private readonly bulkActionClassNameId: number;
	private readonly onCreateTaskError:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	private readonly onCreateTaskSuccess:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	private readonly selectedData: IBulkActionFDSData;

	public readonly overrideDefaultErrorToast: boolean;
	public readonly overrideDefaultSuccessToast: boolean;
	public readonly payload: TBulkActionTaskDTO;
	public readonly postURL: string;
	public readonly type: keyof IBulkActionTaskType;

	constructor({
		apiURL,
		classNameId,
		keyValues,
		onCreateError = null,
		onCreateSuccess = null,
		overrideDefaultErrorToast = false,
		overrideDefaultSuccessToast = false,
		selectedData,
		type,
	}: IBulkActionTaskStarterDTO<keyof IBulkActionTaskType> & {
		classNameId: number;
	}) {
		if (!apiURL) {
			throw new Error('Cannot POST bulk action task.');
		}

		this.bulkActionClassNameId = classNameId;
		this.onCreateTaskError = onCreateError;
		this.onCreateTaskSuccess = onCreateSuccess;
		this.overrideDefaultErrorToast = overrideDefaultErrorToast;
		this.overrideDefaultSuccessToast = overrideDefaultSuccessToast;
		this.payload = composeCreateTaskDTO(
			type,
			keyValues,
			selectedData
		) as TBulkActionTaskDTO;
		this.postURL = composeCreateTaskURL(
			apiURL,
			selectedData,
			type === 'DownloadBulkAction'
		);
		this.selectedData = selectedData as IBulkActionFDSData;
		this.type = type;
	}

	public onCreateSuccess(response: RequestResult<IBulkActionTaskPage>): void {
		if (this.onCreateTaskSuccess && this.overrideDefaultSuccessToast) {
			this.onCreateTaskSuccess(response);
		}
		else {
			const message = getBulkActionTaskMessage(
				this.type,
				'info',
				this.selectedData
			);

			displayCreateTaskSuccessToast(
				sub(
					message,
					this.selectedData.selectAll
						? [
								getTaskReportLink(
									this.bulkActionClassNameId,
									response?.data?.id
								),
							]
						: [
								this.selectedData?.items?.length || 0,
								getTaskReportLink(
									this.bulkActionClassNameId,
									response?.data?.id
								),
							]
				)
			);

			if (this.onCreateTaskSuccess) {
				this.onCreateTaskSuccess(response);
			}
		}
	}

	public onCreateError(response: RequestResult<IBulkActionTaskPage>): void {
		if (this.onCreateTaskError && this.overrideDefaultErrorToast) {
			this.onCreateTaskError(response);
		}
		else {
			displayCreateTaskErrorToast(response?.error);

			if (this.onCreateTaskError) {
				this.onCreateTaskError(response);
			}
		}
	}
}
