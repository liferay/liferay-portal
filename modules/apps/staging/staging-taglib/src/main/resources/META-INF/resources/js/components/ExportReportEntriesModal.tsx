/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLabel from '@clayui/label';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import ClayProgressBar from '@clayui/progress-bar';
import classNames from 'classnames';
import React from 'react';

import {
	Status,
	useBatchEngineExportTask,
} from '../hooks/useBatchEngineExportTask';
import {downloadFile} from '../utils/downloadFile';

type StatusInfo = {
	displayType: 'success' | 'info' | 'danger';
	label: string;
	message: string;
};

const STATUS_MAP: Record<Status, StatusInfo> = {
	[Status.COMPLETED]: {
		displayType: 'success',
		label: Liferay.Language.get('completed'),
		message: Liferay.Language.get(
			'your-file-has-been-generated-and-is-ready-to-download'
		),
	},
	[Status.FAILED]: {
		displayType: 'danger',
		label: Liferay.Language.get('failed'),
		message: Liferay.Language.get(
			'please-try-again-an-unexpected-error-occurred-while-creating-the-file'
		),
	},
	[Status.STARTED]: {
		displayType: 'info',
		label: Liferay.Language.get('running'),
		message: Liferay.Language.get('your-file-is-being-created'),
	},
};

export function ExportReportEntriesModal({
	closeModal,
	filename,
	importProcessId,
}: {
	closeModal: () => void;
	filename: string;
	importProcessId: string;
}) {
	const {downloadURL, errorMessage, progress, status} =
		useBatchEngineExportTask(importProcessId);

	const currentMessage =
		status === Status.FAILED && errorMessage
			? errorMessage
			: STATUS_MAP[status].message;

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('export-report-entries')}
			</ClayModal.Header>

			<ClayModal.Body className="text-3">
				<div id={`${importProcessId}status`} role="status">
					<p className="mb-0">{currentMessage}</p>

					<p>
						<strong>{filename}</strong>
					</p>

					<ClayLabel displayType={STATUS_MAP[status].displayType}>
						{STATUS_MAP[status].label}
					</ClayLabel>
				</div>

				<ClayProgressBar
					aria-labelledby={`${importProcessId}status`}
					messages={{
						ariaLabelAttention: Liferay.Language.get(
							'attention-value-is-at-x'
						),
						ariaLabelComplete: Liferay.Language.get('complete'),
						ariaLabelInProgress: Liferay.Language.get('progress-x'),
					}}
					value={progress}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className={classNames(
								`btn-${STATUS_MAP[status].displayType}`
							)}
							disabled={status !== Status.COMPLETED}
							onClick={() => {
								if (!downloadURL) {
									return;
								}

								downloadFile(downloadURL, filename);
							}}
						>
							{status === Status.STARTED && (
								<span className="inline-item inline-item-before">
									<ClayLoadingIndicator size="xs" />
								</span>
							)}

							{Liferay.Language.get('download')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
