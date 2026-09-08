/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	ImageEditorLoadError,
	MAX_IMAGE_BYTES,
	MAX_IMAGE_PIXELS,
	disposeLoadedImage,
	loadImage,
} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';

function bitmapOf(width: number, height: number) {
	return {close: jest.fn(), height, width};
}

const decoder = jest.fn();

beforeEach(() => {
	decoder.mockReset();

	(globalThis as any).createImageBitmap = decoder;
});

afterEach(() => {
	delete (globalThis as any).createImageBitmap;
});

describe('the loading limits', () => {
	it('refuses an oversized file before decoding anything', async () => {
		const blob = {size: MAX_IMAGE_BYTES + 1} as Blob;

		await expect(loadImage(blob, 'huge.jpg')).rejects.toMatchObject({
			name: 'ImageEditorLoadError',
			reason: 'file-too-large',
		});

		expect(decoder).not.toHaveBeenCalled();
	});

	it('refuses an oversized area before any URL exists', async () => {
		const side = Math.ceil(Math.sqrt(MAX_IMAGE_PIXELS)) + 1;

		const bitmap = bitmapOf(side, side);

		decoder.mockResolvedValue(bitmap);

		const createObjectURL = jest.spyOn(URL, 'createObjectURL');

		await expect(
			loadImage(new Blob(['x']), 'vast.jpg')
		).rejects.toMatchObject({reason: 'too-many-pixels'});

		expect(createObjectURL).not.toHaveBeenCalled();
		expect(bitmap.close).toHaveBeenCalled();

		createObjectURL.mockRestore();
	});

	it('wraps a decoder failure in the typed error', async () => {
		decoder.mockRejectedValue(new Error('bad bytes'));

		const failure = loadImage(new Blob(['x']), 'broken.jpg');

		await expect(failure).rejects.toBeInstanceOf(ImageEditorLoadError);
		await expect(failure).rejects.toMatchObject({
			message: 'bad bytes',
			reason: 'decode-failed',
		});
	});
});

describe('the preview URL ownership', () => {
	it('hands the URL to the host and disposeLoadedImage revokes it', async () => {
		decoder.mockResolvedValue(bitmapOf(120, 80));

		const createObjectURL = jest
			.spyOn(URL, 'createObjectURL')
			.mockReturnValue('blob:preview');
		const revokeObjectURL = jest.spyOn(URL, 'revokeObjectURL');

		const image = await loadImage(new Blob(['x']), 'small.jpg');

		expect(image.previewUrl).toBe('blob:preview');
		expect(revokeObjectURL).not.toHaveBeenCalled();

		disposeLoadedImage(image);

		expect(revokeObjectURL).toHaveBeenCalledTimes(1);
		expect(revokeObjectURL).toHaveBeenCalledWith('blob:preview');

		createObjectURL.mockRestore();
		revokeObjectURL.mockRestore();
	});
});
