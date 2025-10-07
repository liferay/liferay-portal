/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AxiosError} from 'axios';

import {DocumentFileItem} from './DocumentFileItem';

import './FileList.scss';
import {ImageFileItem} from './ImageFileItem';

export type ImageCustomField = {
	customValue: {data: string[]};
	dataType: string;
	name: string;
};

export type UploadedImage = {
	uploadedImage: {
		cdnEnabled: boolean;
		cdnURL: string;
		customFields: ImageCustomField[];
		displayDate: string;
		externalReferenceCode: string;
		fileEntryId: number;
		galleryEnabled: boolean;
		id: number;
		options: {};
		priority: number;
		src: string;
		tags: any[];
		title: {en_US: string};
		type: number;
	};
};

export type UploadedFile = {
	changed: boolean;
	error: boolean | AxiosError;
	file: File;
	fileName: string;
	id: string;
	imageDescription?: string;
	preview?: string;
	progress: number;
	readableSize:
		| string
		| number
		| any[]
		| {
				exponent: number;
				symbol: any;
				unit: string;
				value: any;
		  };
	tags?: any[];
	uploaded: boolean;
	uploadedImage?: UploadedImage;
	versionName?: string;
};

type FileListProps = {
	isProcessing: boolean;
	onArrowClick?: (index: number, direction: string) => void;
	onChangeInput?: (newImagesInputs: UploadedFile[]) => void;
	onDelete: (id: string, versionName?: string) => void;
	removable?: boolean;
	type: 'document' | 'image';
	uploadedFiles: UploadedFile[];
	uploadedImages?: UploadedFile[];
	versionName?: string;
};

export function FileList({
	isProcessing,
	onArrowClick = () => {},
	onChangeInput = () => {},
	onDelete,
	removable,
	type,
	uploadedFiles,
	uploadedImages,
	versionName,
}: FileListProps) {
	return (
		<div className="file-list-container">
			{uploadedFiles?.map((uploadedFile, index) => {
				if (type === 'document') {
					return (
						<DocumentFileItem
							isProcessing={isProcessing}
							key={uploadedFile?.id}
							onDelete={onDelete}
							removable={removable}
							uploadedFile={uploadedFile}
							versionName={versionName}
						/>
					);
				}

				if (type === 'image') {
					return (
						<ImageFileItem
							index={index}
							isProcessing={isProcessing}
							key={index}
							onArrowClick={onArrowClick}
							onChangeInput={onChangeInput}
							onDelete={onDelete}
							position={uploadedFiles.length}
							tooltip="Use the image description to provide more context about the screenshot, such as what is the user trying to accomplish, what are the business requirements met by this screen or anything else you feel would be helpful to guide your potential customer.  This content will be provided in the form of a mouse over of the image."
							uploadedFile={uploadedFile}
							uploadedImages={uploadedImages}
							versionName={versionName}
						/>
					);
				}
			})}
		</div>
	);
}
