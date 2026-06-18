/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {
	getCachedScreenName,
	getScreenName,
} from './services/UserAccountService';

interface Creator {
	givenName?: string;
	id?: number;
	name?: string;
}

const GUEST_ACCOUNT_SUFFIX = 'guest-service-account';

function isGuest(creator: Creator): boolean {
	const givenName = creator.givenName ?? '';

	return givenName.endsWith(GUEST_ACCOUNT_SUFFIX);
}

export default function UserTableCell({value}: {value: Creator | null}) {
	const userId = value?.id;

	const [screenName, setScreenName] = useState(() =>
		getCachedScreenName(userId)
	);

	useEffect(() => {
		if (!userId) {
			return;
		}

		let active = true;

		getScreenName(userId).then((name) => {
			if (active && name) {
				setScreenName(name);
			}
		});

		return () => {
			active = false;
		};
	}, [userId]);

	if (!value) {
		return null;
	}

	if (isGuest(value)) {
		return <>{Liferay.Language.get('anonymous')}</>;
	}

	return <>{screenName || value.name}</>;
}
