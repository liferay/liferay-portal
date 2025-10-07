/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

'use strict';

const crypto = require('crypto');
const fs = require('fs');
const path = require('path');

function hookFn(gulp) {
	gulp.hook('before:build:war', (done) => {
		collectFiles('.css').forEach(hashify);
		collectFiles('.js').forEach(hashify);

		done();
	});
}

function collectFiles(extension, dir) {
	if (!dir) {
		dir = './build';
	}

	const files = [];

	for (const dirent of fs.readdirSync(dir, {withFileTypes: true})) {
		if (dirent.isDirectory()) {
			files.push(...collectFiles(extension, path.join(dir, dirent.name)));
		}
		else if (dirent.name.endsWith(extension)) {
			files.push(path.join(dir, dirent.name));
		}
	}

	return files;
}

function hashify(filePath) {
	const content = fs.readFileSync(filePath, 'utf-8');

	const hash = calculateFileHash(content);

	const fileName = path.basename(filePath);

	const i = fileName.lastIndexOf('.');

	const hashedFileName =
		fileName.substring(0, i) + `.(${hash})` + fileName.substring(i);

	fs.renameSync(filePath, `${path.dirname(filePath)}/${hashedFileName}`);
}

function calculateFileHash(content) {

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

module.exports = {
	hookFn,
};
