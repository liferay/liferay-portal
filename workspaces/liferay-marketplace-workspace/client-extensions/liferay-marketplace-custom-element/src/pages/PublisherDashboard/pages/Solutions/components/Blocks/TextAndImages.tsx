/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import {filesize} from 'filesize';

import {DropzoneUpload} from '../../../../../../components/DropzoneUpload/DropzoneUpload';
import {
	FileList,
	UploadedFile,
} from '../../../../../../components/FileList/FileList';
import Form from '../../../../../../components/MarketplaceForm';
import {TextImageBlock} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';
import {swapElements} from '../../../../../../utils/array';
import {getRandomID} from '../../../../../../utils/string';
import {ACCEPT_FILE_TYPES} from '../../../Apps/AppCreationFlow/StorefrontPage/CustomizeAppStorefrontPage';
import {MAX_IMAGE_QUANTITY, MAX_SIZE_5MBS} from '../../constants';
import {BlockTypeProps} from './BlockPropsType';

const TextAndImages: React.FC<BlockTypeProps<TextImageBlock>> = ({
	block: {content},
	onChange,
	onDeleteImage,
}) => {
	const {observer, onOpenChange, open} = useModal();

	return (
		<div className="p-4">
			<Form.FormControl>
				<Form.Label
					className="mt-2"
					htmlFor="title"
					info="title"
					required
				>
					{i18n.translate('title')}
				</Form.Label>

				<Form.Input
					maxLength={170}
					name="title"
					onChange={(event) => onChange({title: event.target.value})}
					placeholder="Enter title"
					type="text"
					value={content.title || ''}
				/>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label
					className="mt-5"
					htmlFor="description"
					info="description"
					required
				>
					{i18n.translate('description')}
				</Form.Label>

				<div className="rich-text-editor">
					<Form.RichTextEditor
						maxLength={700}
						onChange={(text) => onChange({description: text})}
						placeholder="Insert text here"
						value={content.description || ''}
					/>
				</div>
			</Form.FormControl>

			<Form.FormControl>
				<Form.Label info="images">
					Add up to {MAX_IMAGE_QUANTITY} images
				</Form.Label>

				{!!content.files?.length && (
					<FileList
						isProcessing={false}
						onArrowClick={(index, direction) => {
							const newIndex =
								direction === 'up' ? index - 1 : index + 1;

							const files = swapElements(
								content?.files,
								index,
								newIndex
							);

							files[index].changed = true;
							files[newIndex].changed = true;
							onChange({files});
						}}
						onChangeInput={(files) => onChange({files})}
						onDelete={(id) => {
							const files = content?.files?.filter(
								(uploadedFile) => uploadedFile?.id !== id
							);

							onDeleteImage(id);
							onChange({files});
						}}
						type="image"
						uploadedFiles={content?.files}
						uploadedImages={content?.files}
					/>
				)}

				<DropzoneUpload
					acceptFileTypes={ACCEPT_FILE_TYPES}
					buttonText={i18n.translate('select-a-file')}
					description={i18n.translate(
						'only-gif-jpg-jpeg-png-are-allowed-max-file-size-is-5mb'
					)}
					disabled={content?.files?.length === MAX_IMAGE_QUANTITY}
					maxFiles={MAX_IMAGE_QUANTITY}
					maxSize={MAX_SIZE_5MBS}
					multiple={true}
					onDropRejected={(fileList) => {
						if (fileList.length > MAX_IMAGE_QUANTITY) {
							onOpenChange(true);
						}
					}}
					onHandleUpload={(files: File[]) => {
						const totalImages =
							(content.files?.length || 0) + files?.length;

						if (totalImages > MAX_IMAGE_QUANTITY) {
							return onOpenChange(true);
						}

						const newUploadedFiles: UploadedFile[] = files.map(
							(file, index) => ({
								changed: false,
								error: false,
								file,
								fileName: file.name,
								id: getRandomID(),
								index,
								preview: URL.createObjectURL(file),
								progress: 0,
								readableSize: filesize(file.size),
								uploaded: false,
							})
						);

						onChange({
							files: content.files
								? [...content.files, ...newUploadedFiles]
								: newUploadedFiles,
						});
					}}
					title="Drag and drop to upload or"
				/>
			</Form.FormControl>

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

export default TextAndImages;
