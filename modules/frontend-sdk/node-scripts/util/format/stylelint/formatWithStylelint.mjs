/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';
import stylelint from 'stylelint';

import {MODULES_DIR, PORTAL_DIR} from '../../locations.mjs';
import printEslintErrors from '../util/printEslintErrors.mjs';

const {default: STYLELINT_CONFIG} = await import(
	'file://' + path.join(MODULES_DIR, '.stylelintrc.js')
);

const SUPPRESSED_ERRORS = await fs.readFile(
	path.join(import.meta.dirname, 'suppressed_errors.txt'),
	'utf-8'
);

export default async function formatWithStylelint(
	input,
	filePath,
	emitSuppressed
) {
	const extName = path.extname(filePath);

	// NOTE: Overriding the base configuration is not supported because we've
	// never needed. If we wanted to support it in the future we would need to
	// manually load cascaded configuration files from here based on file path.

	const {output, results} = await stylelint.lint({
		code: input,
		codeFilename: filePath,
		config: STYLELINT_CONFIG,
		fix: true,
		syntax: extName.replace('.', ''),
	});

	if (results?.length) {
		const errors = [];

		results.forEach((result) => {
			if (result.warnings.length) {
				errors.push(
					...result.warnings.map(({column, line, rule, text}) => ({
						column,
						filepath: filePath,
						line,
						message: text,
						ruleId: rule,
						severity: 2,
					}))
				);
			}
		});

		const portalRelativePath = path.relative(PORTAL_DIR, filePath);

		// When emitting, write every error to stdout in 'suppressed_errors.txt'
		// format so the grandfathered list can be regenerated.

		if (emitSuppressed) {
			const set = new Set();

			errors.forEach((error) => {
				set.add(`${portalRelativePath}:${error.message}`);
			});

			set.forEach((item) => {
				process.stdout.write(`${item}\n`);
			});

			return {
				errorsPresent: false,
				output: output.endsWith('\n') ? output : `${output}\n`,
			};
		}

		// Drop errors grandfathered in 'suppressed_errors.txt' so only new
		// violations fail the build.

		const filteredErrors = errors.filter(
			(error) =>
				!SUPPRESSED_ERRORS.includes(
					`${portalRelativePath}:${error.message}\n`
				)
		);

		printEslintErrors(portalRelativePath, filteredErrors);

		return {
			errorsPresent: !!filteredErrors.length,
			output: output.endsWith('\n') ? output : `${output}\n`,
		};
	}

	return {
		errorsPresent: false,
		output: input,
	};
}
