/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import crypto from 'crypto';
import fs from 'fs';
import path from 'path';

class Project {
	constructor(projectDir) {
		this._loadFrom(projectDir);
	}

	init(options) {
		if (this._initialized) {
			throw new Error('Project can only be initialized once');
		}

		this._initialized = true;

		this._options = processOptions(this, options);
	}

	get dir() {
		return this._dir;
	}

	get options() {
		return this._options;
	}

	get pkgJson() {
		return this._pkgJson;
	}

	get themeConfig() {
		return this._pkgJson.liferayTheme;
	}

	get workspaceDir() {
		return this._workspaceDir;
	}

	/**
	 * Return a temporary directory inside the project's tree. The directory is
	 * created and emptied if necessary.
	 *
	 * Each call to this method will return a new directory.
	 *
	 * The returned directory is guaranteed to exist during the execution of the
	 * current build but no more than that. However, it is not deleted on exit.
	 *
	 * @return {string}
	 */
	tmpdir() {
		const tmpdir = path.join(
			this.options.pathBuild,
			'.tmp',
			crypto.randomBytes(4).toString('hex')
		);

		fs.rmSync(tmpdir, {force: true, recursive: true});
		fs.mkdirSync(tmpdir, {recursive: true});

		return tmpdir;
	}

	_loadFrom(projectDir) {
		this._dir = path.resolve(projectDir);
		this._initialized = false;
		this._pkgJson = JSON.parse(
			fs.readFileSync(path.join(this._dir, 'package.json'), 'utf8')
		);
		this._workspaceDir = this._findWorkspaceDir(projectDir);
	}

	_findWorkspaceDir(dir) {
		dir = path.resolve(dir);

		if (fs.existsSync(path.join(dir, 'yarn.lock'))) {
			return dir;
		}

		const parentDir = path.dirname(dir);

		if (parentDir === dir) {
			return undefined;
		}

		return this._findWorkspaceDir(parentDir);
	}
}

function processOptions(project, options) {
	options = {
		pathBuild: 'build',
		pathDist: 'dist',
		pathSrc: 'src',
		sassOptions: {},
		...options,
		...project.themeConfig,
	};

	options.distName =
		options.distName || project.pkgJson.name || path.basename(project.dir);
	options.rootDir = options.pathBuild;

	return options;
}

export default new Project('.');
