/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {filesize} from 'filesize';

import {DropzoneUpload} from '../../../../../components/DropzoneUpload/DropzoneUpload';
import {FileList} from '../../../../../components/FileList/FileList';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import {
	ALLOWED_MIME_TYPES,
	PUBLISH_APP_UPLOAD_MAX_FILES,
	PUBLISH_APP_UPLOAD_MAX_SIZE,
} from '../../../../../enums/File';
import {ProductType} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {getRandomID} from '../../../../../utils/string';

type NewAppUploadAppPackagesComponentProps = {
	isProcessing: boolean;
	versionName: string;
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
};

export function NewAppUploadAppPackagesComponent({
	isProcessing,
	versionName,
}: NewAppUploadAppPackagesComponentProps) {
	const [
		{
			build: {appType, liferayPackages},
		},
		dispatch,
	] = useNewAppContext();

	const enableUploadFiles =
		!isProcessing &&
		(!liferayPackages?.length ||
			liferayPackages?.length < PUBLISH_APP_UPLOAD_MAX_FILES);

	const handleRemoveAppPackages = (fileId: string) => {
		const _liferayPackages = liferayPackages.map((liferayPackage) => {
			if (liferayPackage.version === versionName) {
				return {
					...liferayPackage,
					file: liferayPackage.file.filter(({id}) => id !== fileId),
				};
			}

			return liferayPackage;
		});

		dispatch({
			payload: {
				liferayPackages: _liferayPackages,
			},
			type: NewAppTypes.SET_BUILD,
		});
	};

	const handleUploadAppPackages = (files: File[]) => {
		const newUploadedPackages = files.map((file) => ({
			error: false,
			file,
			fileName: file.name,
			id: getRandomID(),
			preview: URL.createObjectURL(file),
			progress: 0,
			readableSize: filesize(file.size),
			uploaded: false,
			versionName,
		}));

		const _liferayPackages = liferayPackages.map((liferayPackage) => {
			if (liferayPackage.version === versionName) {
				return {
					...liferayPackage,
					file: liferayPackage.file.length
						? [...liferayPackage.file, ...newUploadedPackages]
						: newUploadedPackages,
				};
			}

			return liferayPackage;
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
				uploadedFiles={
					liferayPackages.find(
						(liferayPackage) =>
							liferayPackage.version === versionName
					)?.file ?? []
				}
				versionName={versionName}
			/>

			{enableUploadFiles && (
				<DropzoneUpload
					acceptFileTypes={
						acceptFileTypes[appType as keyof typeof acceptFileTypes]
					}
					buttonText={i18n.translate('select-a-file')}
					description={
						appType === ProductType.CLOUD
							? i18n.translate(
									'only-zip-files-are-allowed-max-file-size-is-500-mb'
								)
							: i18n.translate(
									'only-jar-war-files-are-allowed-max-file-size-is-500mb'
								)
					}
					maxFiles={PUBLISH_APP_UPLOAD_MAX_FILES}
					maxSize={PUBLISH_APP_UPLOAD_MAX_SIZE}
					multiple={true}
					onHandleUpload={handleUploadAppPackages}
					title={i18n.translate('drag-and-drop-to-upload-or')}
					versionName={versionName}
				/>
			)}
		</>
	);
}

export default NewAppUploadAppPackagesComponent;
