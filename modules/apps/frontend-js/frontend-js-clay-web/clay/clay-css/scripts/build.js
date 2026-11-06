/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-env node */

'use strict';

const fs = require('fs');
const path = require('path');
const sass = require('sass');

const OUTPUT_DIRECTORY = path.resolve(__dirname, '..', './lib');

const CSS_OUTPUT_DIRECTORY = path.join(OUTPUT_DIRECTORY, 'css');
const SASS_SOURCE_DIRECTORY = path.resolve(__dirname, '..', 'src/scss');

function copyRecursiveSync(sourcePath, destinationPath) {
	const exists = fs.existsSync(sourcePath);
	const stats = exists && fs.statSync(sourcePath);
	const isDirectory = exists && stats.isDirectory();

	if (isDirectory) {
		if (
			!fs.existsSync(destinationPath) ||
			!fs.statSync(destinationPath).isDirectory()
		) {
			fs.mkdirSync(destinationPath, {recursive: true});
		}

		fs.readdirSync(sourcePath).forEach((childItemName) => {
			copyRecursiveSync(
				path.join(sourcePath, childItemName),
				path.join(destinationPath, childItemName)
			);
		});
	}
	else {
		fs.copyFileSync(sourcePath, destinationPath);
	}
}

function ensureDirectory(directoryPath) {
	return new Promise((resolve, reject) => {
		fs.stat(directoryPath, (error) => {
			if (error) {
				if (error.code === 'ENOENT') {
					fs.mkdir(directoryPath, {recursive: true}, (error) => {
						if (error) {
							reject(error);
						}
						else {
							resolve(directoryPath);
						}
					});
				}
				else {
					reject(error);
				}
			}

			resolve(directoryPath);
		});
	});
}

async function build() {
	const licenseText = path.join(
		__dirname,
		'..',
		'src',
		'scss',
		'_license-text.scss'
	);

	fs.readFile(licenseText, 'utf8', (error, data) => {
		if (error) {
			return console.error(error);
		}

		const result = data.replace(
			/\*\s+Clay\s(.+)\n/g,
			`* Clay ${
				JSON.parse(
					fs.readFileSync(path.join(__dirname, '..', 'package.json'))
				).version
			}\n`
		);

		if (result === data) {
			return;
		}

		fs.writeFile(licenseText, result, 'utf8', (error) => {
			if (error) {
				return console.error(error);
			}
		});
	});

	await ensureDirectory(OUTPUT_DIRECTORY);

	copyRecursiveSync(
		path.resolve(__dirname, '..', './src/js'),
		path.join(OUTPUT_DIRECTORY, 'js')
	);
	copyRecursiveSync(
		path.resolve(__dirname, '..', './src/images/icons'),
		path.join(OUTPUT_DIRECTORY, 'images/icons')
	);

	fs.mkdirSync(path.join(OUTPUT_DIRECTORY, 'images/images'), {
		recursive: true,
	});

	fs.copyFileSync(
		path.resolve(__dirname, '..', './src/images/images/empty_states.svg'),
		path.join(OUTPUT_DIRECTORY, 'images/images/empty_states.svg')
	);

	copyRecursiveSync(
		path.join(__dirname, '..', '..', 'LICENSES'),
		path.resolve(__dirname, '..', './LICENSES')
	);

	const fileNames = ['atlas.scss', 'base.scss', 'cadmin.scss'];

	await ensureDirectory(CSS_OUTPUT_DIRECTORY);

	fileNames.forEach((fileName) => {
		const destinationName = `${path.basename(fileName, '.scss')}.css`;
		const destinationPath = path.join(
			CSS_OUTPUT_DIRECTORY,
			destinationName
		);

		const results = sass.renderSync({
			file: path.join(SASS_SOURCE_DIRECTORY, fileName),
			outFile: `./${destinationName}`,
			sourceMap: true,
			sourceMapContents: true,
			sourceMapRoot: '../../',
		});

		fs.writeFileSync(destinationPath, results.css);
		fs.writeFileSync(`${destinationPath}.map`, results.map);
	});
}

build()
	.then(() => {

		// eslint-disable-next-line no-console
		console.log('Build successful');
	})
	.catch((error) => {

		// eslint-disable-next-line no-console
		console.log('Build error:\n', error);
		process.exit(1);
	});
