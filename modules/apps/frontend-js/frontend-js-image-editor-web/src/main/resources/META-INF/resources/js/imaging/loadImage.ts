/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const MAX_IMAGE_BYTES = 25 * 1024 * 1024;

export const MAX_IMAGE_PIXELS = 36_000_000;

const PREVIEW_MAX_SIZE = 2048;

export class ImageEditorLoadError extends Error {
	readonly reason: ImageLoadErrorReason;

	constructor(reason: ImageLoadErrorReason, message: string) {
		super(message);

		this.name = 'ImageEditorLoadError';
		this.reason = reason;
	}
}

export type ImageLoadErrorReason =
	| 'decode-failed'
	| 'file-too-large'
	| 'too-many-pixels';

export interface LoadedImage {

	/**
	 * The original, full-resolution file. Only read again at export time.
	 */
	blob: Blob;

	fileName: string;
	height: number;

	/**
	 * Object URL of the downscaled preview bitmap the SVG workspace
	 * displays. Never larger than PREVIEW_MAX_SIZE on its longest side.
	 * A successful load transfers ownership to the host: release it with
	 * `disposeLoadedImage` once the image leaves the editor for good.
	 */
	previewUrl: string;

	type: string;
	width: number;
}

/**
 * Releases the browser resources a successful `loadImage` handed over.
 * The host owns the returned `LoadedImage`; call this once the image
 * leaves the editor for good (close, replacement, unmount). Revoking an
 * already-revoked URL is a no-op, so a defensive second call is safe.
 */
export function disposeLoadedImage(image: LoadedImage): void {
	URL.revokeObjectURL(image.previewUrl);
}

/**
 * Decodes an image and prepares a downscaled preview. The offscreen canvas
 * here is a decoder/scaler only: it is never attached to the DOM and plays
 * no part in the interactive UI.
 */
export async function loadImage(
	blob: Blob,
	fileName: string
): Promise<LoadedImage> {
	const bitmap = await decodeWithinLimits(blob);

	let previewUrl: string | null = null;

	// The bitmap is native memory and the preview URL pins a blob alive:
	// a failure after either exists must release both, or a rejected
	// load leaks what the successful path would have owned.

	try {
		previewUrl = await createPreviewUrl(bitmap, blob);

		return {
			blob,
			fileName,
			height: bitmap.height,
			previewUrl,
			type: blob.type || 'image/jpeg',
			width: bitmap.width,
		};
	}
	catch (error) {
		if (previewUrl) {
			URL.revokeObjectURL(previewUrl);
		}

		throw error;
	}
	finally {
		bitmap.close();
	}
}

async function createPreviewUrl(
	bitmap: ImageBitmap,
	blob: Blob
): Promise<string> {
	const {height, width} = bitmap;

	const longestSide = Math.max(width, height);

	if (longestSide <= PREVIEW_MAX_SIZE) {
		return URL.createObjectURL(blob);
	}

	const scale = PREVIEW_MAX_SIZE / longestSide;

	const canvas = document.createElement('canvas');

	canvas.width = Math.round(width * scale);
	canvas.height = Math.round(height * scale);

	const context = canvas.getContext('2d');

	if (!context) {
		throw new Error('Could not create a 2d context for the preview');
	}

	context.drawImage(bitmap, 0, 0, canvas.width, canvas.height);

	const previewBlob = await new Promise<Blob>((resolve, reject) =>
		canvas.toBlob(
			(result) =>
				result
					? resolve(result)
					: reject(new Error('Preview encoding failed')),
			'image/jpeg',
			0.9
		)
	);

	return URL.createObjectURL(previewBlob);
}

async function decodeWithinLimits(blob: Blob): Promise<ImageBitmap> {
	if (blob.size > MAX_IMAGE_BYTES) {
		throw new ImageEditorLoadError(
			'file-too-large',
			`The file weighs ${blob.size} bytes and the editor accepts up to ${MAX_IMAGE_BYTES}`
		);
	}

	let bitmap: ImageBitmap;

	try {
		bitmap = await createImageBitmap(blob);
	}
	catch (error) {
		throw new ImageEditorLoadError(
			'decode-failed',
			error instanceof Error ? error.message : 'The image did not decode'
		);
	}

	if (bitmap.width * bitmap.height > MAX_IMAGE_PIXELS) {
		const {height, width} = bitmap;

		bitmap.close();

		throw new ImageEditorLoadError(
			'too-many-pixels',
			`The image measures ${width}x${height} and the editor accepts up to ${MAX_IMAGE_PIXELS} pixels`
		);
	}

	return bitmap;
}
