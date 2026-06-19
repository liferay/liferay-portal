/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import {PORTAL_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import formatWithEslint from '../eslint/formatWithEslint.mjs';
import formatScriptTagsWithPrettier from '../jsp/formatScriptTagsWithPrettier.mjs';
import formatWithPrettier from '../prettier/formatWithPrettier.mjs';
import formatWithStylelint from '../stylelint/formatWithStylelint.mjs';

/**
 *
 * @param {string} filePath
 * @param skip
 * @param {boolean} skip.eslint
 * @param {boolean} skip.prettier
 * @param {boolean} skip.stylelint
 * @param options
 * @param {boolean} options.check
 * @param {boolean} options.emitSuppressed
 * @param {boolean} options.emitSuppressedCss
 * @return {Promise<boolean>} false if file could not be formatted correctly
 */
export default async function formatSourceFile(filePath, skip, options) {
	let checksPassed = true;

	const source = await fs.readFile(filePath, 'utf-8');

	if (!source.length) {
		return checksPassed;
	}

	const portalRelativeFilePath = path.relative(PORTAL_DIR, filePath);

	let transformedContent = source;

	try {
		switch (path.extname(filePath)) {
			case '.graphql': {
				if (!skip.prettier) {
					transformedContent = await formatWithPrettier(
						transformedContent,
						filePath
					);
				}
				break;
			}

			case '.jsp':
			case '.jspf': {
				if (!skip.prettier) {
					transformedContent =
						await formatScriptTagsWithPrettier(source);
				}
				break;
			}

			case '.css':
			case '.scss': {
				if (!skip.prettier) {
					transformedContent = await formatWithPrettier(
						transformedContent,
						filePath
					);
				}

				if (!skip.stylelint) {
					const {errorsPresent, output} = await formatWithStylelint(
						transformedContent,
						filePath,
						options.emitSuppressedCss
					);

					if (errorsPresent) {
						checksPassed = false;
					}

					transformedContent = output;
				}
				break;
			}

			default: {
				if (!skip.prettier) {
					transformedContent = await formatWithPrettier(
						transformedContent,
						filePath
					);
				}

				if (!skip.eslint) {
					const {errorsPresent, output} = await formatWithEslint(
						transformedContent,
						filePath,
						options.emitSuppressed
					);

					if (!errorsPresent) {
						checksPassed = false;
					}

					transformedContent = output;
				}
				break;
			}
		}
	}
	catch (error) {
		print(
			2,
			print.error('ERROR:'),
			'Unhandled error formatting file',
			print.underline(portalRelativeFilePath)
		);
		print(3, error, '\n');

		checksPassed = false;
	}

	if (transformedContent !== source) {
		if (!options.check) {
			await fs.writeFile(filePath, transformedContent);

			print(
				2,
				print.success('SUCCESS:'),
				'Formatted file',
				print.underline(portalRelativeFilePath),
				'\n'
			);
		}
		else {
			print(
				2,
				print.error('ERROR:'),
				'File',
				print.underline(portalRelativeFilePath),
				'has format errors.\n'
			);

			checksPassed = false;
		}
	}

	return checksPassed;
}
