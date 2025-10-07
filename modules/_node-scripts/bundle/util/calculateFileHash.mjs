/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import crypto from 'crypto';

export default async function calculateFileHash(content) {

	// Calculate hash (MD5 is enough because we don't need to be crypto-safe)

	let blob = crypto.createHash('md5').update(content).digest();

	// Truncate hash to make URL shorter

	blob = blob.slice(0, 8);

	// Convert bytes to base64 and replace non alphabetic base64 chars
	// (+, /) by URL friendly chars

	let hash = blob.toString('base64');

	hash = hash.replaceAll('+', '$');
	hash = hash.replaceAll('/', '@');

	// Remove the trailing = signs. Since they are base64 padding markers they
	// can be discarded for the purposes of creating a collision resistant hash

	hash = hash.replaceAll('=', '');

	return hash;
}
