/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

interface Creator {
	givenName?: string;
	id?: number;
	name?: string;
}

const GUEST_ACCOUNT_SUFFIX = 'guest-service-account';

const screenNameCache = new Map<number, string>();

function isGuest(creator: Creator): boolean {
	const givenName = creator.givenName ?? '';

	return givenName.endsWith(GUEST_ACCOUNT_SUFFIX);
}

export default function UserTableCell({value}: {value: Creator | null}) {
	const [screenName, setScreenName] = useState(() =>
		value?.id ? screenNameCache.get(value.id) ?? '' : ''
	);

	const userId = value?.id;

	useEffect(() => {
		if (!userId || screenNameCache.has(userId)) {
			return;
		}

		fetch(
			`/o/headless-admin-user/v1.0/user-accounts/${userId}?fields=alternateName`,
			{headers: new Headers({Accept: 'application/json'})}
		)
			.then((response) => response.json())
			.then((data) => {
				if (data?.alternateName) {
					screenNameCache.set(userId, data.alternateName);

					setScreenName(data.alternateName);
				}
			})
			.catch(() => {});
	}, [userId]);

	if (!value) {
		return null;
	}

	if (isGuest(value)) {
		return <>{Liferay.Language.get('anonymous')}</>;
	}

	return <>{screenName || value.name}</>;
}
