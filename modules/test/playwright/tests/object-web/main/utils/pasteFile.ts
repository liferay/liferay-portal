/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator} from '@playwright/test';

export async function pasteFile(
	element: Locator,
	{
		buffer,
		fileName,
		fileType,
	}: {buffer: Buffer; fileName: string; fileType: string}
) {
	await element.evaluate(
		(element, {bytes, fileName, fileType}) => {
			const blob = new Blob([new Uint8Array(bytes)], {type: fileType});
			const file = new File([blob], fileName, {type: fileType});

			const dataTransfer = new DataTransfer();
			dataTransfer.items.add(file);

			const pasteEvent = new ClipboardEvent('paste', {
				bubbles: true,
				cancelable: true,
				clipboardData: dataTransfer,
			});

			element.dispatchEvent(pasteEvent);
		},
		{bytes: Array.from(buffer), fileName, fileType}
	);
}
