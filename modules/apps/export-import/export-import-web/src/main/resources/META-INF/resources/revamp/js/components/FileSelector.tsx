/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ProgressBar from '@clayui/progress-bar';
import React from 'react';

import '../../css/FileSelector.scss';

import ClayAlert from '@clayui/alert';
import {ButtonWithIcon} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import {formatStorage} from 'frontend-js-web';

import DragZone from './forms/DragZone';

export type FileSelectorStatus =
	| 'IDLE'
	| 'UPLOADING'
	| 'VALIDATING'
	| 'SUCCESS'
	| 'ERROR'
	| 'STABLE_SUCCESS';

const STATUS_CONFIG: Record<
	FileSelectorStatus,
	{
		showDragZone: boolean;
		showFileInfo: boolean;
		showProgressBar: boolean;
		statusText?: string;
	}
> = {
	ERROR: {
		showDragZone: true,
		showFileInfo: false,
		showProgressBar: false,
	},
	IDLE: {
		showDragZone: true,
		showFileInfo: false,
		showProgressBar: false,
	},
	STABLE_SUCCESS: {
		showDragZone: false,
		showFileInfo: true,
		showProgressBar: false,
	},
	SUCCESS: {
		showDragZone: false,
		showFileInfo: true,
		showProgressBar: true,
		statusText: Liferay.Language.get('completed'),
	},
	UPLOADING: {
		showDragZone: false,
		showFileInfo: true,
		showProgressBar: true,
		statusText: Liferay.Language.get('uploading'),
	},
	VALIDATING: {
		showDragZone: false,
		showFileInfo: true,
		showProgressBar: true,
		statusText: Liferay.Language.get('validating'),
	},
};

export default function FileSelector({
	'aria-describedby': ariaDescribedby,
	'aria-labelledby': ariaLabelledby,
	error,
	file,
	handleRejection,
	handleUpload,
	name,
	onRemove,
	progress,
	status,
	validExtensions = '*',
}: {
	'aria-describedby'?: string;
	'aria-labelledby'?: string;
	'error'?: string;
	'file'?: File;
	'handleRejection': (error: string) => void;
	'handleUpload': (file: File) => Promise<void>;
	'name': string;
	'onRemove': () => void;
	'progress'?: number;
	'status': FileSelectorStatus;
	'validExtensions'?: string;
}) {
	const config = STATUS_CONFIG[status];

	const showErrorMessage = error && (status === 'IDLE' || status === 'ERROR');

	return (
		<div
			aria-describedby={ariaDescribedby}
			aria-labelledby={ariaLabelledby}
		>
			{config.showFileInfo && file && (
				<div className="border flex-fill p-3 rounded">
					<ClayLayout.ContentRow padded verticalAlign="center">
						<ClayLayout.ContentCol>
							<ClaySticker
								className="sticker-border-secondary"
								displayType="secondary"
							>
								<ClayIcon symbol="document" />
							</ClaySticker>
						</ClayLayout.ContentCol>

						<ClayLayout.ContentCol expand>
							<ClayLayout.ContentRow verticalAlign="center">
								<ClayLayout.ContentCol expand>
									<div className="font-weight-semi-bold">
										{file.name}
									</div>

									<div className="small text-secondary">
										{formatStorage(file.size, {
											addSpaceBeforeSuffix: true,
										})}
									</div>
								</ClayLayout.ContentCol>

								<ClayLayout.ContentCol>
									<ButtonWithIcon
										aria-label={Liferay.Language.get(
											'remove-file'
										)}
										borderless
										displayType="secondary"
										onClick={onRemove}
										symbol="times"
									/>
								</ClayLayout.ContentCol>
							</ClayLayout.ContentRow>

							{config.showProgressBar && (
								<div className="mt-2">
									<ProgressBar
										fillBarClassName={
											status === 'VALIDATING'
												? 'progress-bar-animated progress-bar-striped'
												: ''
										}
										value={
											status === 'SUCCESS' ||
											status === 'VALIDATING'
												? 100
												: progress ?? 0
										}
									/>

									<div
										aria-live="polite"
										className="mt-1 small text-secondary"
									>
										{config.statusText}...
									</div>
								</div>
							)}
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>
				</div>
			)}

			{config.showDragZone && (
				<DragZone
					handleRejection={handleRejection}
					handleUpload={handleUpload}
					maxFiles={1}
					maxSize={
						Liferay.PropsValues.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE
					}
					name={name}
					validExtensions={validExtensions}
				/>
			)}

			{showErrorMessage && (
				<div className="mt-2" id={`${name}-error-message`}>
					<ClayAlert displayType="danger" role="alert">
						{error}
					</ClayAlert>
				</div>
			)}
		</div>
	);
}
