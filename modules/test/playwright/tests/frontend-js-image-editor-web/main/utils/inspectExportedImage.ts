/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Download, Page} from '@playwright/test';
import {readFileSync} from 'fs';

interface Point {
	x: number;
	y: number;
}

interface Pixel {
	blue: number;
	green: number;
	red: number;
}

interface ExportedImage {
	height: number;
	pixels: Pixel[];
	width: number;
}

/**
 * Decode the picture the host received and read its size and, optionally, some
 * of its pixels. The points are relative to the picture, so `{x: 0.5, y: 0.5}`
 * is its center whatever the export ends up measuring.
 */
export default async function inspectExportedImage(
	page: Page,
	download: Download,
	points: Point[] = []
): Promise<ExportedImage> {
	const fileName = download.suggestedFilename();

	const type = fileName.endsWith('.png') ? 'image/png' : 'image/jpeg';

	const source = readFileSync(await download.path()).toString('base64');

	return page.evaluate(
		async ({points, source, type}) => {
			const response = await fetch(`data:${type};base64,${source}`);

			const bitmap = await createImageBitmap(await response.blob());

			const canvas = document.createElement('canvas');

			canvas.height = bitmap.height;
			canvas.width = bitmap.width;

			const context = canvas.getContext('2d');

			context.drawImage(bitmap, 0, 0);

			return {
				height: bitmap.height,
				pixels: points.map(({x, y}) => {
					const {data} = context.getImageData(
						Math.round(x * (bitmap.width - 1)),
						Math.round(y * (bitmap.height - 1)),
						1,
						1
					);

					return {blue: data[2], green: data[1], red: data[0]};
				}),
				width: bitmap.width,
			};
		},
		{points, source, type}
	);
}
