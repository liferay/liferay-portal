/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {filesize} from 'filesize';

import {DropzoneUpload} from '../../../../../../components/DropzoneUpload/DropzoneUpload';
import {
	FileList,
	UploadedFile,
} from '../../../../../../components/FileList/FileList';
import Form from '../../../../../../components/MarketplaceForm';
import VideoThumbnail from '../../../../../../components/VideoThumbnail';
import {
	HeaderContentTypeImages,
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';
import {swapElements} from '../../../../../../utils/array';
import {getRandomID} from '../../../../../../utils/string';
import {ACCEPT_FILE_TYPES} from '../../../Apps/AppCreationFlow/StorefrontPage/CustomizeAppStorefrontPage';
import {MAX_IMAGE_QUANTITY, MAX_SIZE_5MBS} from '../../constants';

enum ContentMediaType {
	EMBED_VIDEO_URL = 'embed-video-url',
	UPLOAD_IMAGES = 'upload-images',
}

const Header = () => {
	const {observer, onOpenChange, open} = useModal();

	const [
		{
			header: {contentType, description, title},
		},
		dispatch,
	] = useSolutionContext();

	const handleUpload = (files: File[]) => {
		if (
			files?.length +
				(contentType as HeaderContentTypeImages).content?.headerImages
					?.length >
			MAX_IMAGE_QUANTITY
		) {
			return onOpenChange(true);
		}
		if (contentType.type === ContentMediaType.UPLOAD_IMAGES) {
			const totalImages =
				(contentType.content?.headerImages?.length || 0) +
				files?.length;

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
					contentType: {
						content: {
							headerImages: contentType.content.headerImages
								? [
										...contentType.content.headerImages,
										...newUploadedFiles,
									]
								: newUploadedFiles,
						},
						type: ContentMediaType.UPLOAD_IMAGES,
					},
				},
				type: SolutionTypes.SET_HEADER,
			});
		}
	};

	const handleDelete = async (id: string) => {
		const files = (
			contentType as HeaderContentTypeImages
		).content.headerImages.filter((uploadedFile) => uploadedFile.id !== id);

		dispatch({
			payload: id,
			type: SolutionTypes.SET_DELETE_IMAGE,
		});

		dispatch({
			payload: {
				contentType: {
					content: {
						headerImages: files,
					},
					type: ContentMediaType.UPLOAD_IMAGES,
				},
			},
			type: SolutionTypes.SET_HEADER,
		});
	};

	const handleArrowClick = (index: number, direction: string) => {
		const newIndex = direction === 'up' ? index - 1 : index + 1;

		const files = swapElements(
			(contentType as HeaderContentTypeImages).content.headerImages,
			index,
			newIndex
		);

		files[index].changed = true;
		files[newIndex].changed = true;

		dispatch({
			payload: {
				contentType: {
					content: {
						headerImages: (contentType as HeaderContentTypeImages)
							.content.headerImages,
					},
					type: ContentMediaType.UPLOAD_IMAGES,
				},
			},
			type: SolutionTypes.SET_HEADER,
		});
	};

	return (
		<div className="mb-4 solutions-form-header">
			<h3>{i18n.translate('solution-header')}</h3>

			<hr />

			<Form.FormControl>
				<Form.Label
					className="mt-2"
					htmlFor="title"
					info="Title"
					required
				>
					{i18n.translate('title')}
				</Form.Label>

				<Form.Input
					maxLength={110}
					name="title"
					onChange={(event) =>
						dispatch({
							payload: {[event.target.name]: event.target.value},
							type: SolutionTypes.SET_HEADER,
						})
					}
					placeholder={i18n.translate('enter-title-header')}
					type="text"
					value={title}
				/>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="description"
					info="Description"
					required
				>
					{i18n.translate('description')}
				</Form.Label>

				<div className="rich-text-editor">
					<Form.RichTextEditor
						maxLength={700}
						onChange={(value) =>
							dispatch({
								payload: {description: value},
								type: SolutionTypes.SET_HEADER,
							})
						}
						placeholder={i18n.translate('insert-text-here')}
						value={description}
					/>
				</div>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label className="mt-5" htmlFor="text" required>
					{i18n.translate('content-media-type')}
				</Form.Label>

				<ClayRadioGroup
					className="d-flex flex-column mt-1"
					onChange={(event: any) =>
						dispatch({
							payload: {
								contentType: {
									content: {
										headerImages: [],
										headerVideoDescription: '',
										headerVideoUrl: '',
									},
									type: event,
								},
							},
							type: SolutionTypes.SET_HEADER,
						})
					}
					value={contentType.type}
				>
					<ClayRadio label="Upload images" value="upload-images" />

					<ClayRadio
						label="Embed video URL"
						value="embed-video-url"
					/>
				</ClayRadioGroup>
			</Form.FormControl>

			{contentType.type === ContentMediaType.EMBED_VIDEO_URL && (
				<>
					<Form.FormControl>
						<Form.Label className="mt-5" htmlFor="url" required>
							{i18n.translate('video-url')}
						</Form.Label>

						<Form.Input
							name="headerVideoUrl"
							onChange={(event) =>
								dispatch({
									payload: {
										contentType: {
											content: {
												...contentType.content,
												headerVideoUrl:
													event.target.value,
											},
											type: ContentMediaType.EMBED_VIDEO_URL,
										},
									},
									type: SolutionTypes.SET_HEADER,
								})
							}
							placeholder="https://"
							type="text"
							value={contentType.content.headerVideoUrl}
						/>

						<Form.HelpMessage>
							{i18n.translate(
								'you-can-paste-links-directly-from-youtube'
							)}
						</Form.HelpMessage>
					</Form.FormControl>

					<Form.FormControl>
						<div className="border d-flex flex-row mt-5 p-4 rounded">
							<VideoThumbnail
								videoURL={contentType.content.headerVideoUrl}
							/>

							<Form.Input
								className="ml-3"
								name="headerVideoDescription"
								onChange={(event) =>
									dispatch({
										payload: {
											contentType: {
												content: {
													...contentType.content,
													headerVideoDescription:
														event.target.value,
												},
												type: ContentMediaType.EMBED_VIDEO_URL,
											},
										},
										type: SolutionTypes.SET_HEADER,
									})
								}
								placeholder={i18n.translate(
									'video-description'
								)}
								type="text"
								value={
									contentType.content.headerVideoDescription
								}
							/>
						</div>
					</Form.FormControl>
				</>
			)}

			{contentType.type === ContentMediaType.UPLOAD_IMAGES && (
				<Form.FormControl>
					<Form.Label className="mb-4 mt-2" htmlFor="description">
						{i18n.sub(
							'add-up-to-x-images',
							MAX_IMAGE_QUANTITY.toString()
						)}
					</Form.Label>

					{!!contentType.content.headerImages?.length && (
						<FileList
							isProcessing={false}
							onArrowClick={handleArrowClick}
							onChangeInput={(newImagesInputs) =>
								dispatch({
									payload: {
										contentType: {
											content: {
												headerImages: newImagesInputs,
											},
											type: ContentMediaType.UPLOAD_IMAGES,
										},
									},
									type: SolutionTypes.SET_HEADER,
								})
							}
							onDelete={handleDelete}
							type="image"
							uploadedFiles={contentType.content.headerImages}
							uploadedImages={contentType.content.headerImages}
						/>
					)}

					<DropzoneUpload
						acceptFileTypes={ACCEPT_FILE_TYPES}
						buttonText={i18n.translate('select-a-file')}
						description={i18n.translate(
							'only-gif-jpg-png-are-allowed-ax-file-size-is-5mb'
						)}
						disabled={
							contentType.content.headerImages?.length ===
							MAX_IMAGE_QUANTITY
						}
						maxFiles={MAX_IMAGE_QUANTITY}
						maxSize={MAX_SIZE_5MBS}
						multiple
						onDropRejected={(fileList) => {
							if (fileList.length > MAX_IMAGE_QUANTITY) {
								onOpenChange(true);
							}
						}}
						onHandleUpload={handleUpload}
						title={i18n.translate('drag-and-drop-to-upload-or')}
					/>
				</Form.FormControl>
			)}

			{open && (
				<ClayModal
					center
					observer={observer}
					size={'md' as any}
					status="info"
				>
					<ClayModal.Header>
						{i18n.translate('maximum-number-of-upload-reached')}
					</ClayModal.Header>
					<ClayModal.Body className="pb-8">
						{i18n.sub(
							'you-cannot-upload-more-than-x-files',
							MAX_IMAGE_QUANTITY.toString()
						)}
					</ClayModal.Body>
				</ClayModal>
			)}
		</div>
	);
};

export default Header;
