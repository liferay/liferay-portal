/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import {EditState} from '../state/types';
import {FilterDefs, isIdentityFilter} from './FilterDefs';
import {FrameShape} from './frameShapes';
import {imageTransform} from './geometry';
import {LoadedImage} from './loadImage';
import {OverlayShape, overlayTransform} from './overlayShapes';

export function editedImageMarkup(
	state: EditState,
	dataUrl: string,
	pixelUrls: LoadedImage['pixelUrls']
): string {
	const {crop} = state;

	return renderToStaticMarkup(
		<svg
			height={crop.height}
			viewBox={`${crop.x} ${crop.y} ${crop.width} ${crop.height}`}
			width={crop.width}
			xmlns="http://www.w3.org/2000/svg"
		>
			<defs>
				<FilterDefs
					adjustments={state.adjustments}
					filter={state.filter}
					id="export-filter"
				/>
			</defs>

			<g transform={imageTransform(state)}>
				<image
					filter={
						isIdentityFilter(state.adjustments, state.filter)
							? undefined
							: 'url(#export-filter)'
					}
					height={state.sourceHeight}
					href={dataUrl}
					width={state.sourceWidth}
				/>
			</g>

			{!state.frame.overAnnotations && (
				<FrameShape crop={crop} frame={state.frame} />
			)}

			{state.overlays.map((overlay) => (
				<g key={overlay.id} transform={overlayTransform(overlay)}>
					<OverlayShape
						overlay={overlay}
						redactSource={{
							filter: isIdentityFilter(
								state.adjustments,
								state.filter
							)
								? undefined
								: 'url(#export-filter)',

							// The same data URL the picture itself uses:
							// the rasteriser runs in secure static mode
							// and cannot fetch a `blob:` subresource.

							imageUrl: dataUrl,
							pixelUrls,
							sourceHeight: state.sourceHeight,
							sourceWidth: state.sourceWidth,
							transform: imageTransform(state),
						}}
					/>
				</g>
			))}

			{state.frame.overAnnotations && (
				<FrameShape crop={crop} frame={state.frame} />
			)}
		</svg>
	);
}

export async function exportEditedImage(
	image: LoadedImage,
	state: EditState
): Promise<{blob: Blob; fileName: string}> {
	const dataUrl = await blobToDataURL(image.blob);

	const {crop} = state;

	const markup = editedImageMarkup(state, dataUrl, image.pixelUrls);

	const rendered = await loadIntoImage(
		`data:image/svg+xml;charset=utf-8,${encodeURIComponent(markup)}`
	);

	const canvas = document.createElement('canvas');

	canvas.width = crop.width;
	canvas.height = crop.height;

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
