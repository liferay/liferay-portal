/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {filesize} from 'filesize';
import {useState} from 'react';

import {DropzoneUpload} from '../../../../../../components/DropzoneUpload/DropzoneUpload';
import {
	FileList,
	UploadedFile,
	UploadedImage,
} from '../../../../../../components/FileList/FileList';
import {Header} from '../../../../../../components/Header/Header';
import {NewAppPageFooterButtons} from '../../../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {Section} from '../../../../../../components/Section/Section';
import i18n from '../../../../../../i18n';
import {Liferay} from '../../../../../../liferay/liferay';
import fetcher from '../../../../../../services/fetcher';
import HeadlessCommerceAdminCatalog from '../../../../../../services/rest/HeadlessCommerceAdminCatalog';
import {
	baseURL,
	createImageAxios,
	deleteAttachment,
} from '../../../../../../utils/api';
import {swapElements} from '../../../../../../utils/array';
import {getRandomID} from '../../../../../../utils/string';
import {submitBase64EncodedFile} from '../../../../../../utils/util';
import {useAppContext} from '../AppContext/AppManageState';
import {ActionTypes} from '../AppContext/actionTypes';

import './CustomizeAppStorefrontPage.scss';

export const ACCEPT_FILE_TYPES = {
	'image/gif': ['.gif'],
	'image/jpeg': ['.jpeg'],
	'image/jpg': ['.jpg'],
	'image/png': ['.png'],
};
const MAX_IMAGE_QUANTITY = 10;

type CustomizeAppStorefrontPageProps = {
	onClickBack: () => void;
	onClickContinue: () => void;
};

export function CustomizeAppStorefrontPage({
	onClickBack,
	onClickContinue,
}: CustomizeAppStorefrontPageProps) {
	const [{appERC, appStorefrontImages}, dispatch] = useAppContext();

	const [isLoading, setIsLoading] = useState<boolean>(false);

	const handleUpload = (files: File[]) => {
		const totalImages = (appStorefrontImages?.length || 0) + files.length;

		if (totalImages > MAX_IMAGE_QUANTITY) {
			return;
		}

		const newUploadedFiles: UploadedFile[] = files.map((file) => ({
			changed: false,
			error: false,
			file,
			fileName: file.name,
			id: getRandomID(),
			index: 0,
			preview: URL.createObjectURL(file),
			progress: 0,
			readableSize: filesize(file.size),
			uploaded: false,
		}));

		dispatch({
			payload: {
				files: appStorefrontImages?.length
					? [...appStorefrontImages, ...newUploadedFiles]
					: newUploadedFiles,
			},
			type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
		});
	};

	const handleDelete = async (id: string) => {
		const currentFiles = appStorefrontImages.findIndex(
			(uploadedFile) => uploadedFile.id === id
		);

		const files = appStorefrontImages.filter(
			(uploadedFile) => uploadedFile.id !== id
		);

		if (appStorefrontImages[currentFiles]?.uploaded) {
			await fetcher.delete(
				`${baseURL}/o/headless-commerce-admin-catalog/v1.0/attachment/${id}`
			);
		}

		dispatch({
			payload: {
				files,
			},
			type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
		});
	};

	const handleArrowClick = (index: number, direction: string) => {
		const newIndex = direction === 'up' ? index - 1 : index + 1;

		const files = swapElements(appStorefrontImages, index, newIndex);

		files[index].changed = true;
		files[newIndex].changed = true;

		dispatch({
			payload: {
				files,
			},
			type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
		});
	};

	return (
		<div className="storefront-page-container">
			<Header
				description="Design the storefront for your app.  This will set the information displayed on the app page in the Marketplace."
				title="Customize app storefront"
			/>

			<Section
				label="App Storefront"
				required
				tooltip={`Screenshots for your app must not exceed ${MAX_IMAGE_QUANTITY} 80 pixels in width and 678 pixels in height and must be in JPG or PNG format.  The file site of each screenshot must not exceed 384KB.  Each screenshot should preferrably be the same size, but each will be automatically scaled to match the aspect ratio of the above dimensions. It is preferrable if they are named sequentially, but you can reorder them as needed.`}
				tooltipText="More Info"
			>
				<div className="storefront-page-info-container">
					<span className="storefront-page-info-text">
						{`Add up to ${MAX_IMAGE_QUANTITY} images`}
					</span>

					{!isLoading && appStorefrontImages?.length > 0 && (
						<ClayButton
							className="font-weight-bold"
							displayType="link"
							onClick={async () => {
								try {
									for (const image of appStorefrontImages) {
										if (image.uploaded) {
											deleteAttachment(image.id);
										}
									}

									Liferay.Util.openToast({
										message: i18n.translate(
											'request-sent-successfully'
										),
										type: 'success',
									});

									dispatch({
										payload: {
											files: [],
										},
										type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
									});
								}
								catch (error) {
									console.error(error);

									Liferay.Util.openToast({
										message: i18n.translate(
											'an-unexpected-error-occurred'
										),
										type: 'danger',
									});
								}
							}}
						>
							Remove all
						</ClayButton>
					)}
				</div>

				{appStorefrontImages?.length > 0 && (
					<FileList
						isProcessing={isLoading}
						onArrowClick={handleArrowClick}
						onChangeInput={(newImagesInputs) =>
							dispatch({
								payload: {
									files: newImagesInputs,
								},
								type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
							})
						}
						onDelete={handleDelete}
						type="image"
						uploadedFiles={appStorefrontImages}
						uploadedImages={appStorefrontImages}
					/>
				)}

				{!isLoading && (
					<DropzoneUpload
						acceptFileTypes={ACCEPT_FILE_TYPES}
						buttonText="Select a file"
						description="Only gif, jpg, png are allowed. Max file size is 5MB "
						maxFiles={MAX_IMAGE_QUANTITY}
						maxSize={5000000}
						multiple={true}
						onHandleUpload={handleUpload}
						title="Drag and drop to upload or"
					/>
				)}
			</Section>

			<NewAppPageFooterButtons
				disableContinueButton={
					isLoading ||
					!appStorefrontImages ||
					!appStorefrontImages.length
				}
				isLoading={isLoading}
				onClickBack={() => onClickBack()}
				onClickContinue={async () => {
					setIsLoading(true);
					for (const [
						index,
						image,
					] of appStorefrontImages.entries()) {
						if (image.uploaded && image.changed) {
							const {uploadedImage} = appStorefrontImages[
								index
							] as unknown as UploadedImage;

							uploadedImage.priority = index + 1;

							uploadedImage.title.en_US =
								image.imageDescription as string;

							await HeadlessCommerceAdminCatalog.addOrUpdateProductImageByExternalReferenceCode(
								appERC,
								uploadedImage as unknown as UploadedImage
							);

							appStorefrontImages[index].changed = false;

							dispatch({
								payload: {
									files: appStorefrontImages,
								},
								type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
							});
						}

						if (!image.uploaded) {
							const uploadedFile = await submitBase64EncodedFile({
								appERC,
								callback: (progress) => {
									appStorefrontImages[index].progress =
										progress;
									appStorefrontImages[index].uploaded =
										progress === 100;

									dispatch({
										payload: {
											files: appStorefrontImages,
										},
										type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
									});
									appStorefrontImages;
								},
								file: image.file,
								index: index + 1,
								isAppIcon: false,
								requestFunction: createImageAxios,
								title: image.imageDescription ?? image.fileName,
							});

							appStorefrontImages[index].uploadedImage =
								uploadedFile as UploadedImage;
							appStorefrontImages[index].changed = false;

							dispatch({
								payload: {
									files: appStorefrontImages,
								},
								type: ActionTypes.UPLOAD_APP_STOREFRONT_IMAGES,
							});
						}
					}

					onClickContinue();
					setIsLoading(false);
				}}
			/>
		</div>
	);
}
