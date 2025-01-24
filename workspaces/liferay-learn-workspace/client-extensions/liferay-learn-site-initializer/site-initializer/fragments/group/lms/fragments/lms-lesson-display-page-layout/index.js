/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const copyToClipboardButtons = document.querySelectorAll(
	'.copy-to-clipboard-button'
);

copyToClipboardButtons.forEach((button) => {
	button.addEventListener('click', () => {
		const codeToolbar = button.closest('.code-toolbar');

		if (codeToolbar) {
			if (codeToolbar.querySelector('code.language-bash').innerText) {
				navigator.clipboard
					.writeText(codeToolbar.querySelector('code.language-bash').innerText)
					.then(() => {
						button.setAttribute('data-copy-state', 'copy-success');
					})
					.catch(() => {
						button.setAttribute('data-copy-state', 'copy-failure');
					})
					.finally(() => {
						setTimeout(() => {
							button.setAttribute('data-copy-state', 'copy');
						}, 3000);
					})
			}
		}
	});
});
