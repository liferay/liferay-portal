/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';
import url from 'url';

const __dirname = path.dirname(url.fileURLToPath(import.meta.url));

//
// The convention is that `..._DIR`s and `..._FILE`s are absolute paths to
// directories/files, whereas `..._PATH`s are fragments of paths.
//

//
// Directories
//

export const PORTAL_DIR = path.resolve(__dirname, '..', '..', '..', '..');

export const MODULES_DIR = path.resolve(PORTAL_DIR, 'modules');

export const GLOBAL_NODE_MODULES_DIR = path.resolve(
	MODULES_DIR,
	'node_modules'
);
export const GLOBAL_NODE_MODULES_TYPES_DIR = path.resolve(
	GLOBAL_NODE_MODULES_DIR,
	'@types'
);

export const FRONTEND_SDK_DIR = path.resolve(MODULES_DIR, 'frontend-sdk');
export const NODE_SCRIPTS_DIR = path.resolve(FRONTEND_SDK_DIR, 'node-scripts');

export const PLAYWRIGHT_DIR = path.resolve(MODULES_DIR, 'test', 'playwright');

export const TSC_DIR = path.resolve(MODULES_DIR, '.tsc');
export const TSC_BUILDINFO_DIR = path.resolve(TSC_DIR, 'buildinfo');
export const TSC_TYPES_DIR = path.resolve(TSC_DIR, 'types');

export const NODE_DIR = path.resolve(PORTAL_DIR, 'build', 'node');
export const NODE_MODULES_DIR =
	process.platform === 'win32'
		? path.resolve(NODE_DIR, 'node_modules')
		: path.resolve(NODE_DIR, 'lib', 'node_modules');
export const YARN_SCRIPT_DIR = path.resolve(NODE_MODULES_DIR, 'yarn');

//
// Files
//

export const BUILD_PROPERTIES_FILE = path.resolve(
	PORTAL_DIR,
	'build.properties'
);
export const GLOBAL_D_TS_FILE = path.resolve(MODULES_DIR, 'global.d.ts');
export const GLOBAL_NODE_SCRIPTS_CONFIG_FILE = path.resolve(
	MODULES_DIR,
	'node-scripts.config.js'
);

export const YARN_SCRIPT_FILE = path.join(
	YARN_SCRIPT_DIR,
	fs.readdirSync(YARN_SCRIPT_DIR).find((name) => /^yarn-.*\.js$/.test(name))
);
export const YARN_LOCK_FILE = path.resolve(MODULES_DIR, 'yarn.lock');

//
// Paths to source code
//

export const SRC_PATH = path.join(
	'src',
	'main',
	'resources',
	'META-INF',
	'resources'
);
export const SRC_LANGUAGE_JSON_PATH = path.join(SRC_PATH, 'language.json');
export const SRC_TSCONFIG_PATH = path.join(SRC_PATH, 'tsconfig.json');

//
// Paths to build artifacts
//

export const BUILD_PATH = path.join('build', 'node', 'packageRunBuild');
export const BUILD_RESOURCES_PATH = path.join(BUILD_PATH, 'resources');
export const BUILD_MAIN_EXPORTS_PATH = path.join(
	BUILD_RESOURCES_PATH,
	'__liferay__'
);
export const BUILD_CSS_EXPORTS_PATH = path.join(BUILD_MAIN_EXPORTS_PATH, 'css');
export const BUILD_LANGUAGE_JSON_PATH = path.join(
	BUILD_RESOURCES_PATH,
	'language.json'
);
export const BUILD_NPM_EXPORTS_PATH = path.join(
	BUILD_MAIN_EXPORTS_PATH,
	'exports'
);
export const BUILD_SASS_CACHE_PATH = path.join(
	BUILD_RESOURCES_PATH,
	'.sass-cache'
);

//
// Paths to work (temporary) artifacts
//

export const WORK_PATH = path.join('build', 'node-scripts');
export const WORK_EXPORT_PATH = path.join(WORK_PATH, 'export');
export const WORK_IMPORT_PATH = path.join(WORK_PATH, 'import');

//
// Paths to build reports
//

export const BUNDLE_REPORTS_PATH = path.join('build', 'bundle-reports');
