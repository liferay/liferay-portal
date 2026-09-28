/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {
	EditorSaveResult,
	ImageEditor,
	LoadedImage,
	disposeLoadedImage,
	loadImage,
} from '@liferay/frontend-js-image-editor-web';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML, fetch, getFileAsBase64} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ApiHelper from '../../common/services/ApiHelper';
import {IAssetFile} from '../../common/types/AssetType';

interface EditImageModalContentProps {
	closeModal: () => void;
	file: IAssetFile;
	loadData?: () => void;
	updateURL: string;
}

const THUMBNAIL_POLL_DELAY = 300;

const THUMBNAIL_POLL_TRIES = 10;

async function waitForThumbnail(updateURL: string) {
	for (let attempt = 0; attempt < THUMBNAIL_POLL_TRIES; attempt++) {
		const {data} = await ApiHelper.get<{file?: {thumbnailURL?: string}}>(
			`${updateURL}?nestedFields=file.thumbnailURL`
		);

		if (data?.file?.thumbnailURL) {
			return;
		}

		await new Promise((resolve) =>
			setTimeout(resolve, THUMBNAIL_POLL_DELAY)
		);
	}
}

function versionName(name: string, exportedName: string) {
	return (
		name.replace(/\.[^.]+$/, '') +
		exportedName.slice(exportedName.lastIndexOf('.'))
	);
}

export default function EditImageModalContent({
	closeModal,
	file,
	loadData,
	updateURL,
}: EditImageModalContentProps) {
	const [image, setImage] = useState<LoadedImage | null>(null);
	const [loadFailed, setLoadFailed] = useState(false);

	useEffect(() => {
		const abortController = new AbortController();

		let loadedImage: LoadedImage | null = null;

		const load = async () => {
			try {
				const response = await fetch(file.link.href, {
					signal: abortController.signal,
				});

				loadedImage = await loadImage(await response.blob(), file.name);

				if (abortController.signal.aborted) {
					disposeLoadedImage(loadedImage);

					return;
				}

				setImage(loadedImage);
			}
			catch {
				if (!abortController.signal.aborted) {
					setLoadFailed(true);
				}
			}
		};

		load();

		return () => {
			abortController.abort();

			if (loadedImage) {
				disposeLoadedImage(loadedImage);
			}
		};
	}, [file]);

	const onSave = async ({blob, fileName}: EditorSaveResult) => {
		const name = versionName(file.name, fileName);

		const {error} = await ApiHelper.patch(
			{
				file: {
					fileBase64: await getFileAsBase64(
						new File([blob], name, {type: blob.type})
					),
					name,
				},
			},
			updateURL
		);

		if (error) {
			openToast({
				message: escapeHTML(error),
				type: 'danger',
			});

			throw new Error(error);
		}

		await waitForThumbnail(updateURL);

		loadData?.();

		openToast({
			message: Liferay.Language.get('the-image-was-edited-successfully'),
			type: 'success',
		});
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
				withTitle
			>
				{file.name}
			</ClayModal.Header>

			<ClayModal.Body className="overflow-hidden p-0">
				{loadFailed && (
					<ClayAlert className="m-4" displayType="danger">
						{Liferay.Language.get('an-unexpected-error-occurred')}
					</ClayAlert>
				)}

				{!loadFailed && !image && (
					<ClayLoadingIndicator displayType="secondary" size="md" />
				)}

				{image && (
					<ImageEditor
						image={image}
						onClose={closeModal}
						onSave={onSave}
						spritemap={Liferay.Icons.spritemap}
					/>
				)}
			</ClayModal.Body>
		</>
	);
}
