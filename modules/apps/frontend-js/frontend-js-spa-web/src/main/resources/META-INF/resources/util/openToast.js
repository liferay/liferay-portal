/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getSpritemap} from '@liferay/frontend-icons-web';

/**
 * This is an alternative lightweight implementation of openToast() method of
 * frontend-js-web.
 *
 * Its main goal is to make Clay and React unnecessary when rendering the popup
 * so that applications using SPA don't pull those dependencies.
 * @param  {!String} type 'warn' or 'error'
 * @param  {!String} title
 * @param  {!String} message
 * @return {!Object}
 * An object containing a dispose field pointing to a function than can be
 * invoked to close the popup.
 */

const CLASS_NAME_MAP = {
	error: 'danger',
	warn: 'warning',
};
const ICON_MAP = {
	error: 'exclamation-full',
	warn: 'warning-full',
};

export function openToast(type, title, message) {
	const spritemap = getSpritemap();

	const html = `
<div class="alert-container cadmin container">
	<div class="alert-notifications alert-notifications-fixed" id="SPAToastAlertContainer">
		<div class="lfr-tooltip-scope">
			<div class="alert alert-dismissible alert-${CLASS_NAME_MAP[type]} mb-3">
				<div class="alert-autofit-row autofit-row" role="alert">
					<div class="autofit-col">
						<div class="autofit-section">
							<span class="alert-indicator">
								<svg class="lexicon-icon lexicon-icon-${ICON_MAP[type]}" role="presentation">
									<use href="${spritemap}#${ICON_MAP[type]}"></use>
								</svg>
							</span>
						</div>
					</div>
					<div class="autofit-col autofit-col-expand">
						<div class="autofit-section">
							<div>
								<strong class="lead">
									${title}
								</strong>
								${message}
							</div>
						</div>
					</div>
				</div>
				<button aria-label="Close" class="close" type="button">
					<svg class="lexicon-icon lexicon-icon-times" role="presentation">
						<use href="${spritemap}#times"></use>
					</svg>
				</button>
			</div>
		</div>
	</div>
</div>`;

	const div = document.createElement('div');
	div.innerHTML = html;

	const button = div.querySelector('button');
	button.onclick = () => div.remove();

	document.body.appendChild(div);

	return {
		dispose: () => div.remove(),
	};
}
