/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {commerceEvents} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React from 'react';

export function changeAccount(accountId, setCurrentAccountURL) {
	selectAccount(accountId, setCurrentAccountURL)
		.then(() => {
			Liferay.fire(commerceEvents.CURRENT_ACCOUNT_UPDATED, {
				id: accountId,
			});
		})
		.catch(() => {
			openToast({
				closeable: true,
				delay: {
					hide: 5000,
					show: 0,
				},
				duration: 500,
				message: Liferay.Language.get('unexpected-error'),
				title: Liferay.Language.get('danger'),
				type: 'danger',
			});
		});
}

const selectAccount = (id, actionURL) => {
	const endpointURL = new URL(actionURL, Liferay.ThemeDisplay.getPortalURL());

	endpointURL.searchParams.append(
		'groupId',
		Liferay.ThemeDisplay.getScopeGroupId()
	);
	endpointURL.searchParams.append('p_auth', Liferay.authToken);

	const body = new FormData();

	body.append('accountId', id);

	return fetch(endpointURL, {body, method: 'POST'});
};

const AccountNameDataRenderer = ({setCurrentAccountURL, ...props}) => {
	return (
		<div className="table-list-title">
			<ClayLink
				data-senna-off
				href="#"
				onClick={(event) => {
					event.preventDefault();

					changeAccount(props.itemId, setCurrentAccountURL);
				}}
				role="button"
			>
				{props.value}
			</ClayLink>
		</div>
	);
};

export default AccountNameDataRenderer;
