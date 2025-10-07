/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {filesize} from 'filesize';

import {DropzoneUpload} from '../../../../../../../components/DropzoneUpload/DropzoneUpload';
import {FileList} from '../../../../../../../components/FileList/FileList';
import {ProductType} from '../../../../../../../enums/Product';
import i18n from '../../../../../../../i18n';
import {getRandomID} from '../../../../../../../utils/string';
import {useAppContext} from '../../AppContext/AppManageState';
import {ActionTypes} from '../../AppContext/actionTypes';

type UploadAppPackagesComponentProps = {
	isProcessing: boolean;
	versionName: string;
};

export const acceptFileTypes = {
	[ProductType.CLIENT_EXTENSION]: {
		'application/java-archive': ['.zip'],
	},
	[ProductType.CLOUD]: {
		'application/java-archive': ['.zip'],
	},
	[ProductType.COMPOSITE_APP]: {
		'application/java-archive': ['.zip'],
	},
	[ProductType.DXP]: {
		'application/java-archive': ['.jar'],
		'application/octet-stream': ['.war'],
	},
	[ProductType.LOW_CODE_CONFIGURATION]: {
		'application/java-archive': ['.zip'],
	},
	[ProductType.OTHER]: {
		'application/java-archive': ['.zip'],
	},
};

export const UPLOAD_MAX_SIZE = 500_000_000;

export function UploadAppPackagesComponent({
	isProcessing,
	versionName,
}: UploadAppPackagesComponentProps) {
	const [{appType, buildAppPackages}, dispatch] = useAppContext();

	const enableUploadFiles =
		!isProcessing &&
		(!buildAppPackages[versionName]?.length ||
			buildAppPackages[versionName]?.length < 10);

	const handleUploadAppPackages = (files: File[], versionName?: string) => {
		const newUploadedPackage = files.map((file) => ({
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

		const currentVersionFiles =
			buildAppPackages[versionName as string] ?? [];

		dispatch({
			payload: {
				files: currentVersionFiles.length
					? [
							...buildAppPackages[versionName as string],
							...newUploadedPackage,
						]
					: newUploadedPackage,
				versionName,
			},
			type: ActionTypes.UPLOAD_BUILD_PACKAGE_FILES,
		});
	};

	const handleRemoveAppPackages = (fileId: string, versionName?: string) =>
		dispatch({
			payload: {
				files: buildAppPackages[versionName as string]?.filter(
					(value) => value.id !== fileId
				),
				versionName,
			},
			type: ActionTypes.UPLOAD_BUILD_PACKAGE_FILES,
		});

	return (
		<>
			<FileList
				isProcessing={isProcessing}
				onDelete={handleRemoveAppPackages}
				type="document"
				uploadedFiles={
					buildAppPackages ? buildAppPackages[versionName] : []
				}
				versionName={versionName}
			/>

			{enableUploadFiles && (
				<DropzoneUpload
					acceptFileTypes={
						acceptFileTypes[
							appType.value as keyof typeof acceptFileTypes
						]
					}
					buttonText={i18n.translate('select-a-file')}
					description={
						appType.value === ProductType.DXP
							? i18n.translate(
									'only-jar-war-files-are-allowed-max-file-size-is-500mb'
								)
							: i18n.translate(
									'only-zip-files-are-allowed-max-file-size-is-500mb'
								)
					}
					maxFiles={1}
					maxSize={UPLOAD_MAX_SIZE}
					multiple={true}
					onHandleUpload={handleUploadAppPackages}
					title={i18n.translate('drag-and-drop-to-upload-or')}
					versionName={versionName}
				/>
			)}
		</>
	);
}

export default UploadAppPackagesComponent;
