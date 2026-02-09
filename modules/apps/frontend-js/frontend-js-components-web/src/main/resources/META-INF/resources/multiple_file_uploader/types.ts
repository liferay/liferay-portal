/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface FileData {
	errorMessage?: string;
	failed?: boolean;
	file: File;
	name: string;
	size: number;
}

export interface FaildFile {
	errorMessage: string;
	failed: boolean;
	name: string;
	size?: number;
}

export type UploadRequestCallback = ({
	fileData,
}: {
	fileData: FileData;
}) => Promise<
	| {}
	| {
			error: string;
	  }
	| {
			errors: {
				errorMessage: string;
				name: string;
			}[];
			multipleErrors: boolean;
	  }
>;

export interface UploadMessages {
	anotherFileButton: string;
	filesToUpload: string;
	loadingMessageDescription: string;
	loadingMessageTitle: string;
	xFilesNotUploaded: string;
}
