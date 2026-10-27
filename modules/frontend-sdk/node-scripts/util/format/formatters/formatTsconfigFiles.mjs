/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import {PORTAL_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import visitOutdatedTsconfigFiles from '../../tsconfig/visitOutdatedTsconfigFiles.mjs';

export default async function formatTsconfigFiles(check) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> ${check ? 'Checking' : 'Formatting'} 'tsconfig.json' files...\n`
		)
	);

	await visitOutdatedTsconfigFiles(async (filePath, json) => {
		const relFilePath = path.relative(PORTAL_DIR, filePath);

		if (json === null) {
			if (check) {
				print(
					2,
					print.error('ERROR:'),
					'Obsolete file',
					print.underline(relFilePath),
					'was found\n'
				);

				checksPassed = false;
			}
			else {
				await fs.rm(filePath);

				print(
					2,
					print.success('SUCCESS:'),
					'Deleted file',
					print.underline(relFilePath),
					'\n'
				);
			}
		}
		else if (check) {
			print(
				2,
				print.error('ERROR:'),
				'Outdated file',
				print.underline(relFilePath),
				'was found\n'
			);

			checksPassed = false;
		}
		else {
			await fs.writeFile(filePath, json, 'utf-8');

			print(
				2,
				print.success('SUCCESS:'),
				'Regenerated file',
				print.underline(relFilePath),
				'\n'
			);
		}
	});

	return checksPassed;
}
