/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {filesize} from 'filesize';

import {DropzoneUpload} from '../../../../../components/DropzoneUpload/DropzoneUpload';
import {
	FileList,
	UploadedFile,
} from '../../../../../components/FileList/FileList';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import {
	ALLOWED_MIME_TYPES,
	PUBLISH_APP_UPLOAD_MAX_SIZE,
} from '../../../../../enums/File';
import {ProductType} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {getRandomID} from '../../../../../utils/string';

type NewAppUploadAppPackagesComponentProps = {
	isProcessing: boolean;
	liferayPackage: {
		file: UploadedFile | null;
		id: string;
		versions: string[];
	};
};

export const acceptFileTypes = {
	[ProductType.CLIENT_EXTENSION]: ALLOWED_MIME_TYPES.ZIP,
	[ProductType.CLOUD]: ALLOWED_MIME_TYPES.ZIP,
	[ProductType.COMPOSITE_APP]: ALLOWED_MIME_TYPES.ZIP,
	[ProductType.DXP]: {
		...ALLOWED_MIME_TYPES.JAR,
		...ALLOWED_MIME_TYPES.WAR,
	},
	[ProductType.LOW_CODE_CONFIGURATION]: ALLOWED_MIME_TYPES.ZIP,
	[ProductType.OTHER]: ALLOWED_MIME_TYPES.ZIP,
};

export function NewAppUploadAppPackagesComponent({
	isProcessing,
	liferayPackage,
}: NewAppUploadAppPackagesComponentProps) {
	const [
		{
			build: {appType, liferayPackages},
		},
		dispatch,
	] = useNewAppContext();

	const enableUploadFiles = !isProcessing && !liferayPackage.file?.id;

	const handleRemoveAppPackages = (liferayPackageId: string) => {
		const _liferayPackage = liferayPackages.find(
			(liferayPackage) => liferayPackage.file.id === liferayPackageId
		);

		if (_liferayPackage) {
			_liferayPackage.file = null;
		}

		dispatch({
			payload: {
				liferayPackages,
			},
			type: NewAppTypes.SET_BUILD,
		});
	};

	const handleUploadAppPackages = (files: File[]) => {
		const newUploadedPackage = files.map((file) => ({
			changed: true,
			error: false,
			file,
			fileName: file.name,
			id: getRandomID(),
			preview: URL.createObjectURL(file),
			progress: 0,
			readableSize: filesize(file.size),
			uploaded: false,
		}));

		const _liferayPackages = liferayPackages.map((_liferayPackage) => {
			if (liferayPackage.id === _liferayPackage.id) {
				return {
					..._liferayPackage,
					file: newUploadedPackage[0],
				};
			}

			return _liferayPackage;
		});

		dispatch({
			payload: {
				liferayPackages: _liferayPackages,
			},
			type: NewAppTypes.SET_BUILD,
		});
	};

	return (
		<>
			<FileList
				isProcessing={isProcessing}
				onDelete={handleRemoveAppPackages}
				type="document"
				uploadedFiles={liferayPackage.file ? [liferayPackage.file] : []}
			/>

			{enableUploadFiles && (
				<DropzoneUpload
					acceptFileTypes={
						acceptFileTypes[appType as keyof typeof acceptFileTypes]
					}
					buttonText={i18n.translate('select-a-file')}
					description={
						appType === ProductType.DXP
							? i18n.translate(
									'only-jar-war-files-are-allowed-max-file-size-is-500mb'
								)
							: i18n.translate(
									'only-zip-files-are-allowed-max-file-size-is-500-mb'
								)
					}
					maxFiles={1}
					maxSize={PUBLISH_APP_UPLOAD_MAX_SIZE}
					multiple={false}
					onHandleUpload={handleUploadAppPackages}
					title={i18n.translate('drag-and-drop-to-upload-or')}
				/>
			)}
		</>
	);
}

export default NewAppUploadAppPackagesComponent;
