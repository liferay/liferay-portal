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
			const codeText =
				codeToolbar.querySelector('code.language-bash').innerText;

			if (codeText) {
				navigator.clipboard
					.writeText(codeText)
					.then(() => {
						button.setAttribute('data-copy-state', 'copy-success');

						setTimeout(() => {
							button.setAttribute('data-copy-state', 'copy');
						}, 3000);
					})
					.catch(() => {
						button.setAttribute('data-copy-state', 'copy-failure');

						setTimeout(() => {
							button.setAttribute('data-copy-state', 'copy');
						}, 3000);
					});
			}
		}
	});
});
