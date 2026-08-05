/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useEffect, useState} from 'react';

import {fireContentChanged} from '../serverEvents';
import {Space, getSpaces} from '../services/getSpaces';
import {saveGeneratedImages} from '../services/saveGeneratedImages';

import '../chat.scss';
import injectImageIntoFileUploadField from '../utils/injectImageIntoFileUploadField';
import SpaceSelect from './SpaceSelect';

export interface SaveProps {
	fileUploadSelector?: string;
	groupId?: number | string;
	objectEntryFolderExternalReferenceCode?: string;
}

interface ImageMessageBalloonProps {
	images: string[];
	saveProps?: SaveProps;
	scrollToBottom?: () => void;
}

interface PendingImageSave {
	images: string[];
	spaces: Space[];
	successMessage?: string;
}

const ImageMessageBalloon: React.FC<ImageMessageBalloonProps> = ({
	images,
	saveProps = {},
	scrollToBottom,
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
	const [pendingImageSave, setPendingImageSave] =
		useState<PendingImageSave | null>(null);

	useEffect(() => {
		if (pendingImageSave) {
			scrollToBottom?.();
		}
	}, [pendingImageSave, scrollToBottom]);

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
		targetGroupId: number | string,
		successMessage?: string
	) {
		setSaving(true);

		try {
			await saveGeneratedImages(imagesToSave, {
				groupId: targetGroupId,
				objectEntryFolderExternalReferenceCode,
			});

			fireContentChanged();

			if (successMessage) {
				Liferay.Util.openToast({
					message: successMessage,
					type: 'info',
				});
			}
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

	async function saveImagesToFiles(
		imagesToSave: string[],
		successMessage?: string
	) {
		if (Number(groupId) > 0) {
			saveImagesToGroup(
				imagesToSave,
				groupId as number | string,
				successMessage
			);

			return;
		}

		setSelectingSpace(true);

		try {
			const spaces = await getSpaces();

			if (!spaces.length) {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'there-are-no-spaces-available-to-save-the-image'
					),
					type: 'info',
				});

				return;
			}

			if (spaces.length === 1) {
				await saveImagesToGroup(
					imagesToSave,
					spaces[0].siteId,
					successMessage
				);

				return;
			}

			setPendingImageSave({images: imagesToSave, spaces, successMessage});
		}
		catch {
			Liferay.Util.openToast({
				message: Liferay.Language.get('the-spaces-could-not-be-loaded'),
				type: 'danger',
			});
		}
		finally {
			setSelectingSpace(false);
		}
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
				saveImagesToFiles(
					remainingImages,
					Liferay.Language.get(
						'the-generated-images-were-added-to-the-upload-fields-the-remaining-images-were-saved-successfully'
					)
				);
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
									disabled={saving || !!pendingImageSave}
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
						saving ||
						selectingSpace ||
						!!pendingImageSave ||
						!selectedImages.length
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

			{pendingImageSave && (
				<div className="ai-assistant-chat__content-generation-balloon-form">
					<span>
						{pendingImageSave.images.length > 1
							? Liferay.Language.get(
									'in-which-space-do-you-want-to-save-the-images'
								)
							: Liferay.Language.get(
									'in-which-space-do-you-want-to-save-the-image'
								)}
					</span>

					<SpaceSelect
						onSelectSpace={(space) => {
							setPendingImageSave(null);

							saveImagesToGroup(
								pendingImageSave.images,
								space.siteId,
								pendingImageSave.successMessage
							);
						}}
						spaces={pendingImageSave.spaces}
					/>
				</div>
			)}
		</div>
	);
};

export default ImageMessageBalloon;
