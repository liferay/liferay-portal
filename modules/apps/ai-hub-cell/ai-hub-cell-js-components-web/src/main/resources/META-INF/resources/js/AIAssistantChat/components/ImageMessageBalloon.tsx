/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useRef, useState} from 'react';

import {saveGeneratedImages} from '../services/saveGeneratedImages';

import '../chat.scss';
import injectImageIntoFileUploadField from '../utils/injectImageIntoFileUploadField';
import SpaceSelectionModalContent from './SpaceSelectionModalContent';

export interface SaveProps {
	fileUploadSelector?: string;
	groupId?: number | string;
	objectEntryFolderExternalReferenceCode?: string;
}

interface ImageMessageBalloonProps {
	images: string[];
	saveProps?: SaveProps;
}

const ImageMessageBalloon: React.FC<ImageMessageBalloonProps> = ({
	images,
	saveProps = {},
}) => {
	const {
		fileUploadSelector,
		groupId,
		objectEntryFolderExternalReferenceCode,
	} = saveProps;

	const multiple = images.length > 1;

	const [selectedIndexes, setSelectedIndexes] = useState<Set<number>>(
		() => new Set(images.map((_, index) => index))
	);
	const [saving, setSaving] = useState<boolean>(false);
	const [selectingSpace, setSelectingSpace] = useState<boolean>(false);

	const pendingImagesRef = useRef<string[]>([]);

	function toggleSelected(index: number) {
		setSelectedIndexes((previousSelectedIndexes) => {
			const nextSelectedIndexes = new Set(previousSelectedIndexes);

			if (nextSelectedIndexes.has(index)) {
				nextSelectedIndexes.delete(index);
			}
			else {
				nextSelectedIndexes.add(index);
			}

			return nextSelectedIndexes;
		});
	}

	const selectedImages = images.filter((_, index) =>
		selectedIndexes.has(index)
	);

	async function saveImagesToGroup(
		imagesToSave: string[],
		targetGroupId: number | string
	) {
		setSaving(true);

		try {
			await saveGeneratedImages(imagesToSave, {
				groupId: targetGroupId,
				objectEntryFolderExternalReferenceCode,
			});
		}
		catch {
			Liferay.Util.openToast({
				message: Liferay.Language.get(
					'the-generated-images-could-not-be-saved'
				),
				type: 'danger',
			});
		}
		finally {
			setSaving(false);
		}
	}

	function saveImagesToFiles(imagesToSave: string[]) {
		if (Number(groupId) > 0) {
			saveImagesToGroup(imagesToSave, groupId as number | string);

			return;
		}

		pendingImagesRef.current = imagesToSave;

		setSelectingSpace(true);
	}

	function handleSave() {
		if (!selectedImages.length) {
			return;
		}

		if (
			fileUploadSelector &&
			injectImageIntoFileUploadField(
				fileUploadSelector,
				selectedImages[0]
			)
		) {
			const remainingImages = selectedImages.slice(1);

			if (remainingImages.length) {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'the-first-image-was-added-to-the-selected-field-and-the-remaining-images-were-saved-to-files'
					),
					type: 'info',
				});

				saveImagesToFiles(remainingImages);
			}

			return;
		}

		saveImagesToFiles(selectedImages);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__image-message-balloon">
			<ul className="ai-assistant-chat__image-message-balloon-images">
				{images.map((image, index) => (
					<li
						className="ai-assistant-chat__image-message-balloon-item"
						key={index}
					>
						<ClayCard displayType="image" selectable={multiple}>
							{multiple ? (
								<ClayCheckbox
									checked={selectedIndexes.has(index)}
									disabled={saving}
									onChange={() => toggleSelected(index)}
								>
									<ClayCard.AspectRatio className="card-item-first card-item-last">
										<img
											alt={Liferay.Language.get(
												'generated-image'
											)}
											className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
											src={image}
										/>
									</ClayCard.AspectRatio>
								</ClayCheckbox>
							) : (
								<ClayCard.AspectRatio className="card-item-first card-item-last">
									<img
										alt={Liferay.Language.get(
											'generated-image'
										)}
										className="aspect-ratio-item-center-middle aspect-ratio-item-fluid"
										src={image}
									/>
								</ClayCard.AspectRatio>
							)}
						</ClayCard>
					</li>
				))}
			</ul>

			<div className="ai-assistant-chat__image-message-balloon-actions">
				<ClayButton
					disabled={
						saving || selectingSpace || !selectedImages.length
					}
					displayType="primary"
					onClick={handleSave}
				>
					{saving ? (
						<>
							<ClayLoadingIndicator size="sm" />

							{Liferay.Language.get('saving')}
						</>
					) : selectedImages.length > 1 ? (
						Liferay.Language.get('save-images')
					) : (
						Liferay.Language.get('save-image')
					)}
				</ClayButton>
			</div>

			{selectingSpace && (
				<SpaceSelectionModalContent
					onSelectSpace={(chosenGroupId) => {
						setSelectingSpace(false);

						if (chosenGroupId) {
							saveImagesToGroup(
								pendingImagesRef.current,
								chosenGroupId
							);
						}
					}}
				/>
			)}
		</div>
	);
};

export default ImageMessageBalloon;
