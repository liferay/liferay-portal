/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import {EditState, rotatedSize} from '../state/types';
import {imageTransform} from './geometry';
import {LoadedImage} from './loadImage';

export async function exportEditedImage(
	image: LoadedImage,
	state: EditState
): Promise<{blob: Blob; fileName: string}> {
	const dataUrl = await blobToDataURL(image.blob);

	const bounds = rotatedSize(state);

	const markup = renderToStaticMarkup(
		<svg
			height={bounds.height}
			viewBox={`0 0 ${bounds.width} ${bounds.height}`}
			width={bounds.width}
			xmlns="http://www.w3.org/2000/svg"
		>
			<g transform={imageTransform(state)}>
				<image
					height={state.sourceHeight}
					href={dataUrl}
					width={state.sourceWidth}
				/>
			</g>
		</svg>
	);

	const rendered = await loadIntoImage(
		`data:image/svg+xml;charset=utf-8,${encodeURIComponent(markup)}`
	);

	const canvas = document.createElement('canvas');

	canvas.width = bounds.width;
	canvas.height = bounds.height;

	const context = canvas.getContext('2d');

	if (!context) {
		throw new Error('Could not create a 2d context for the export');
	}

	context.drawImage(rendered, 0, 0);

	const type = image.type === 'image/png' ? 'image/png' : 'image/jpeg';

	const blob = await new Promise<Blob>((resolve, reject) =>
		canvas.toBlob(
			(result) =>
				result
					? resolve(result)
					: reject(new Error('Export encoding failed')),
			type,
			0.92
		)
	);

	const baseName = image.fileName.replace(/\.[^.]+$/, '');

	return {
		blob,
		fileName: `${baseName}-edited.${type === 'image/png' ? 'png' : 'jpg'}`,
	};
}

function blobToDataURL(blob: Blob): Promise<string> {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();

		reader.onerror = () => reject(new Error('Could not read the image'));
		reader.onload = () => resolve(reader.result as string);

		reader.readAsDataURL(blob);
	});
}

function loadIntoImage(src: string): Promise<HTMLImageElement> {
	return new Promise((resolve, reject) => {
		const image = new Image();

		image.onerror = () =>
			reject(new Error('Could not rasterize the edited image'));
		image.onload = () => resolve(image);

		image.src = src;
	});
}
