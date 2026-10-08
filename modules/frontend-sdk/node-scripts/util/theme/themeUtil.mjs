/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const colors = require('ansi-colors');
const spawn = require('cross-spawn');
const es = require('event-stream');
const log = require('fancy-log');
const fs = require('fs-extra');
const _ = require('lodash');
const minimist = require('minimist');
const path = require('path');
const tar = require('tar-fs');

const project = require('./project');

const CUSTOM_DEP_PATH_ENV_VARIABLE_MAP = {
	'liferay-frontend-common-css': 'LIFERAY_COMMON_CSS_PATH',
	'liferay-frontend-theme-styled': 'LIFERAY_THEME_STYLED_PATH',
	'liferay-frontend-theme-unstyled': 'LIFERAY_THEME_UNSTYLED_PATH',
};
const CUSTOM_DEP_PATH_FLAG_MAP = {
	'liferay-frontend-common-css': 'css-common-path',
	'liferay-frontend-theme-styled': 'styled-path',
	'liferay-frontend-theme-unstyled': 'unstyled-path',
};

const DEPLOYMENT_STRATEGIES = {
	DOCKER_CONTAINER: 'DockerContainer',
	LOCAL_APP_SERVER: 'LocalAppServer',
	OTHER: 'Other',
};

function dockerCopy(
	containerName,
	sourceFolder,
	destFolder,
	sourceFiles,
	callback
) {
	if (_.isFunction(sourceFiles)) {
		callback = sourceFiles;
		sourceFiles = undefined;
	}

	const wcb = (error) => {
		if (!callback) {
			return;
		}

		if (error) {
			callback(error);
		}
		else {
			callback();
		}
	};

	const packConfig = {
		dmode: parseInt(755, 8),
		fmode: parseInt(644, 8),
	};

	if (sourceFiles) {
		_.assign(packConfig, {
			entries: sourceFiles,
		});
	}

	tar.pack(sourceFolder, packConfig).pipe(
		es.wait((error, body) => {
			if (error) {
				throw error;
			}

			const proc = spawn.sync(
				'docker',
				[
					'exec',
					'-i',
					containerName,
					'sh',
					'-c',
					'"tar xp -C ' + destFolder + '"',
				],
				{
					input: body,
					shell: true,
				}
			);

			if (proc.error) {
				// eslint-disable-next-line no-console
				console.error(proc.stderr.toString());
			}

			wcb(proc.error);
		})
	);
}

function dockerExec(containerName, command) {
	return spawn.sync(
		'docker',
		['exec', containerName, 'sh', '-c', '"' + command + '"'],
		{
			shell: true,
		}
	);
}

function getArgv() {
	return minimist(process.argv.slice(2));
}

function getLanguageProperties(pathBuild) {
	const pathContent = path.join(pathBuild, 'WEB-INF', 'src', 'content');

	const languageKeys = [];

	if (fs.existsSync(pathContent) && fs.statSync(pathContent).isDirectory()) {
		const contentFiles = fs.readdirSync(pathContent);

		_.forEach(contentFiles, (item) => {
			if (item.match(/Language.*properties/)) {
				const xmlElement =
					'<language-properties>content/' +
					item +
					'</language-properties>';

				languageKeys.push(xmlElement);
			}
		});
	}

	return languageKeys;
}

function isCssFile(name) {
	return _.endsWith(name, '.css') || _.endsWith(name, '.scss');
}

function isSassPartial(name) {
	return _.startsWith(path.basename(name), '_');
}

function resolveDependency(dependency) {
	const customPath = getCustomDependencyPath(dependency);

	if (customPath) {
		return customPath;
	}

	return path.dirname(require.resolve(dependency, {paths: [project.dir]}));
}

function getCustomDependencyPath(dependency) {
	let customPath;
	const envVariable = CUSTOM_DEP_PATH_ENV_VARIABLE_MAP[dependency];
	const flag = CUSTOM_DEP_PATH_FLAG_MAP[dependency];
	const argv = getArgv();

	if (flag && argv[flag]) {
		customPath = argv[flag];
	}
	else if (envVariable && process.env[envVariable]) {
		customPath = process.env[envVariable];
	}

	if (customPath) {
		log(
			colors.magenta(dependency),
			'using custom path:',
			colors.magenta(customPath)
		);

		validateCustomDependencyPath(customPath);
	}

	return customPath;
}

module.exports = {
	DEPLOYMENT_STRATEGIES,
	dockerCopy,
	dockerExec,
	getArgv,
	getCustomDependencyPath,
	getLanguageProperties,
	isCssFile,
	isSassPartial,
	resolveDependency,
};

function validateCustomDependencyPath(customPath) {
	const stats = fs.statSync(customPath);

	if (!stats.isDirectory()) {
		throw new Error(customPath + ' is not a directory');
	}
}

// Export private methods when in tests

if (typeof jest !== 'undefined') {
	Object.assign(module.exports, {
		validateCustomDependencyPath,
	});
}
