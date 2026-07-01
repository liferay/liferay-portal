/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from '../util/fetch.es';
import objectToFormData from '../util/form/object_to_form_data.es';
import getPortletId from '../util/get_portlet_id';
import createPortletURL from '../util/portlet_url/create_portlet_url.es';
import setCSPNonce from '../util/set_csp_nonce';
import register from './register.es';

/**
 * Minimizes portlet
 * @param {String} portletSelector Portlet container selector
 * @param {HTMLElement} trigger Trigger element
 * @param {Object} options Additional options
 */
export function minimizePortlet(portletSelector, trigger, options) {
	options = {
		doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
		plid: themeDisplay.getPlid(),
		...options,
	};

	const portlet = document.querySelector(portletSelector);

	if (portlet) {
		const content = portlet.querySelector('.portlet-content-container');

		if (content) {
			const minimized = content.classList.contains('d-none');

			if (minimized) {
				content.classList.remove('d-none');

				portlet.classList.remove('portlet-minimized');
			}
			else {
				content.classList.add('d-none');

				portlet.classList.add('portlet-minimized');
			}

			if (trigger) {
				const title = minimized
					? Liferay.Language.get('minimize')
					: Liferay.Language.get('restore');

				trigger.setAttribute('alt', title);
				trigger.setAttribute('title', title);

				trigger.innerHTML = title;

				const icon = trigger.querySelector('i');

				if (icon) {
					icon.classList.remove('icon-minus', 'icon-resize-vertical');

					if (minimized) {
						icon.classList.add('icon-minus');
						icon.classList.remove('icon-resize-vertical');
					}
					else {
						icon.classList.add('icon-resize-vertical');
						icon.classList.remove('icon-minus');
					}
				}
			}

			const portletId = getPortletId(portlet.id);

			const formData = objectToFormData({
				cmd: 'minimize',
				doAsUserId: options.doAsUserId,
				p_auth: Liferay.authToken,
				p_l_id: options.plid,
				p_p_id: portletId,
				p_p_restore: minimized,
				p_v_l_s_g_id: themeDisplay.getSiteGroupId(),
			});

			fetch(themeDisplay.getPathMain() + '/portal/update_layout', {
				body: formData,
				method: 'POST',
			})
				.then((response) => {
					if (response.ok && minimized) {
						const params = {
							doAsUserId: options.doAsUserId,
							p_l_id: options.plid,
							p_p_boundary: false,
							p_p_id: portletId,
							p_p_isolated: true,
						};

						fetch(
							createPortletURL(
								themeDisplay.getPathMain() +
									'/portal/render_portlet',
								params
							)
						)
							.then((response) => response.text())
							.then((response) => {
								response = setCSPNonce(response);

								const range = document.createRange();

								range.selectNode(portlet);

								portlet.innerHTML = '';

								const fragment =
									range.createContextualFragment(response);

								portlet.appendChild(fragment);
							})
							.catch((error) => {
								if (process.env.NODE_ENV === 'development') {
									console.error(error);
								}
							});
					}
				})
				.catch((error) => {
					if (process.env.NODE_ENV === 'development') {
						console.error(error);
					}
				});
		}
	}
}

export default {
	register,
};
