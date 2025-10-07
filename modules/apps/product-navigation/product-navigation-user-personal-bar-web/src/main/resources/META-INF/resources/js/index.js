/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';

export function signInButtonPropsTransformer({
	additionalProps: {redirect: initialRedirect, signInURL},
	...props
}) {
	const signInButton = document.querySelector('.sign-in > div > button');
	const modalSignInURL = addParams('windowState=exclusive', signInURL);
	let loading = false;
	let modalContentHTML = '';
	let isModalOpen = false;
	let shouldRedirect = initialRedirect;

	const updateModalContent = (html) => {
		const modalBody = document.querySelector('.liferay-modal-body');
		if (modalBody) {
			const fragment = document
				.createRange()
				.createContextualFragment(html);
			modalBody.innerHTML = '';
			modalBody.appendChild(fragment);
		}
	};

	const loadModalContent = () => {
		if (loading || modalContentHTML) {
			return;
		}

		loading = true;

		fetch(modalSignInURL)
			.then((response) => response.text())
			.then((responseHTML) => {
				if (!loading) {
					return;
				}

				loading = false;

				if (!responseHTML) {
					shouldRedirect = true;

					return;
				}

				modalContentHTML = responseHTML;

				if (isModalOpen) {
					updateModalContent(responseHTML);
					if (document.readyState === 'complete') {
						const signInButton = document.getElementsByClassName(
							'btn disabled btn-primary'
						)[0];

						if (signInButton) {
							signInButton.classList.remove('disabled');
							signInButton.disabled = false;
						}
					}
				}
			})
			.catch(() => {
				shouldRedirect = true;
			});
	};

	return {
		...props,
		onClick() {
			loadModalContent();

			if (shouldRedirect) {
				navigate(signInURL);

				return;
			}
			if (signInButton) {
				openModal({
					bodyHTML:
						modalContentHTML || '<span class="loading-animation">',
					containerProps: {
						className: '',
					},
					onClose() {
						loading = false;
						shouldRedirect = initialRedirect;
						modalContentHTML = '';
						isModalOpen = false;
					},
					onOpen() {
						isModalOpen = true;
						if (
							modalContentHTML &&
							document.querySelector('.loading-animation')
						) {
							updateModalContent(modalContentHTML);
							const signInButton =
								document.getElementsByClassName(
									'btn disabled btn-primary'
								)[0];

							if (signInButton) {
								signInButton.classList.remove('disabled');
								signInButton.disabled = false;
							}
						}
					},
					size: 'md',
					title: Liferay.Language.get('sign-in'),
				});
			}
		},
	};
}
