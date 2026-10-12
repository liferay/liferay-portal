/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const showFallbackIcon = (previewImage) => {
	previewImage.hidden = true;

	const icon = previewImage.parentElement.querySelector('.dsr-document-icon');

	if (icon) {
		icon.hidden = false;
	}
};

fragmentElement
	.querySelectorAll('.dsr-document-preview-image')
	.forEach((previewImage) => {
		if (previewImage.complete && previewImage.naturalWidth === 0) {
			showFallbackIcon(previewImage);
		}
		else {
			previewImage.addEventListener('error', () => {
				showFallbackIcon(previewImage);
			});
		}
	});
