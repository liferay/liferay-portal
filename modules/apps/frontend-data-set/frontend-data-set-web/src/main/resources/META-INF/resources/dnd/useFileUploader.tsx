/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {getObjectValueFromPath} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import {THandleFileDrop} from '../DnDContext';
import isFileDropEnabled from '../utils/isFileDropEnabled';
import {IFileDropSettings, TOnFileDrop} from '../utils/types';

/* This hook connects FDS with state about dropped files, allowing integration
	with a file uploader component in the future. To implement an uploader,
	please provide the the onFileDrop function via fileDropSettings.
	Default implementation is used if none provided, showing a modal with the
	list of dropped files.
 */
const useFileUploader = ({
	fileDropSettings,
	selectedItemsKey,
}: {
	fileDropSettings: IFileDropSettings;
	selectedItemsKey: string | undefined;
}): {
	handleFileDrop: THandleFileDrop;
} => {
	const [droppedFiles, setDroppedFiles] = useState<File[]>([]);
	const [dropTarget, setDropTarget] = useState(null);

	const defaultUploader: TOnFileDrop = useCallback(
		(droppedFiles: File[], dropTarget: any) => {
			const ModalBody = () => {
				const label = (file: File) =>
					`'${file.name}' of size '${file.size}' and type '${file.type}'`;

				return (
					<div>
						{droppedFiles.map((file: File) => (
							<li key={file.name}>{label(file)}</li>
						))}

						{dropTarget ? (
							<span>
								Dropped on item{' '}

								{getObjectValueFromPath({
									object: dropTarget,
									path: selectedItemsKey,
								})}
							</span>
						) : (
							<span>
								Dropped on the FDS, no specific drop target
							</span>
						)}
					</div>
				);
			};

			openModal({
				bodyComponent: ModalBody,
				size: 'lg',
				title: Liferay.Language.get('files'),
			});
		},
		[selectedItemsKey]
	);

	const handleFileDrop: THandleFileDrop = (
		droppedItem: any,
		dropTarget?: any
	) => {
		if (droppedItem) {
			const files: File[] = droppedItem.files;
			setDroppedFiles(files);
			setDropTarget(dropTarget ? dropTarget : null);
		}
	};

	useEffect(() => {
		if (!isFileDropEnabled(fileDropSettings) || !droppedFiles?.length) {
			return;
		}

		if (
			fileDropSettings.onFileDrop &&
			typeof fileDropSettings.onFileDrop === 'function'
		) {
			fileDropSettings.onFileDrop(droppedFiles, dropTarget);
		}
		else {
			defaultUploader(droppedFiles, dropTarget);
		}
	}, [
		defaultUploader,
		droppedFiles,
		dropTarget,
		fileDropSettings,
		selectedItemsKey,
	]);

	return {handleFileDrop};
};

export default useFileUploader;
