/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import sha256 from 'hash.js/lib/hash/sha/256';

function sort(object) {
	if (typeof object !== 'object' || !object) {
		return object;
	}
	else if (Array.isArray(object)) {
		return object.sort().map(sort);
	}

	return Object.keys(object)
		.sort()
		.reduce((acc, cur) => {
			acc[cur] = sort(object[cur]);

			return acc;
		}, {});
}

function hash(value) {
	let toHash = value;

	if (typeof value === 'object') {
		toHash = JSON.stringify(sort(value));
	}

	return sha256().update(toHash).digest('hex');
}

export {hash};
export default hash;
