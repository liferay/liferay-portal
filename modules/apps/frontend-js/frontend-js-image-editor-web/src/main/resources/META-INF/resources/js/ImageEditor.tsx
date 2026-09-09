/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../css/ImageEditor.scss';

import {ClayIconSpriteContext} from '@clayui/icon';
import React from 'react';

import {AnnouncerProvider} from './chrome/Announcer';
import EditorModal, {EditorSaveResult} from './editor/EditorModal';
import {LoadedImage} from './imaging/loadImage';

export type {EditorSaveResult};

export function sessionKeyOf(image: LoadedImage): string {
	return image.previewUrl;
}

export interface ImageEditorProps {
	image: LoadedImage;
	onClose: () => void;

	onSave: (
		result: EditorSaveResult,
		signal: AbortSignal
	) => Promise<void> | void;

	spritemap: string;
}

export function ImageEditor({
	image,
	onClose,
	onSave,
	spritemap,
}: ImageEditorProps) {
	return (
		<ClayIconSpriteContext.Provider value={spritemap}>
			<AnnouncerProvider>
				<EditorModal
					image={image}
					key={sessionKeyOf(image)}
					onClose={onClose}
					onSave={onSave}
				/>
			</AnnouncerProvider>
		</ClayIconSpriteContext.Provider>
	);
}
