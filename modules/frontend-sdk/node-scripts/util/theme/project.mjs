/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const crypto = require('crypto');
const fs = require('fs-extra');
const {
	default: FilePath,
} = require('liferay-npm-build-tools-common/lib/file-path');
const _ = require('lodash');
const path = require('path');

const Gulp = require('./gulp');
const Options = require('./options');
const Store = require('./store');
const ThemeConfig = require('./theme-config');

class Project {
	constructor(projectDir) {
		this._loadFrom(projectDir);
	}

	init(options) {
		if (this._initialized) {
			throw new Error('Project can only be initialized once');
		}

		this._initialized = true;

		this._gulp = new Gulp(this, options.gulp);
		this._options = new Options(this, options);
		this._store = new Store(
			this,
			options.storeConfig.path,
			options.storeConfig.name
		);
		this._watching = false;
	}

	get dir() {
		return this._dir;
	}

	get gulp() {
		return this._gulp.gulp;
	}

	get options() {
		return this._options;
	}

	get pkgJson() {
		return this._pkgJson;
	}

	get store() {
		return this._store;
	}

	get themeConfig() {
		return this._themeConfig;
	}

	get watching() {
		return this._watching;
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
	 * @return {FilePath}
	 */
	tmpdir() {
		const tmpdir = this.options.pathBuild.join(
			'.tmp',
			crypto.randomBytes(4).toString('hex')
		);

		fs.emptyDirSync(tmpdir.asNative);

		return tmpdir;
	}

	modifyPkgJson(modifier) {
		this._pkgJson = modifier(this._pkgJson);

		fs.writeJSONSync(this._pkgJsonPath, this._pkgJson, {spaces: '\t'});
	}

	removeDependencies(dependencies) {
		this.modifyPkgJson((pkgJson) => {
			this._deleteDependencies(pkgJson.dependencies, dependencies);
			this._deleteDependencies(pkgJson.devDependencies, dependencies);

			return pkgJson;
		});
	}

	set watching(watching) {
		this._watching = watching;
	}

	setDependencies(dependencies, devDependencies) {
		this.modifyPkgJson((pkgJson) => {
			const selector = devDependencies
				? 'devDependencies'
				: 'dependencies';

			if (!pkgJson[selector]) {
				pkgJson[selector] = {};
			}

			_.merge(pkgJson[selector], dependencies);

			return pkgJson;
		});
	}

	_deleteDependencies(sourceDependencies, deletedDependencies) {
		_.forEach(sourceDependencies, (item, index) => {
			if (deletedDependencies.indexOf(index) > -1) {
				delete sourceDependencies[index];
			}
		});
	}

	_loadFrom(projectDir) {
		this._dir = path.resolve(projectDir);
		this._initialized = false;
		this._pkgJsonPath = path.join(this.dir, 'package.json');
		this._pkgJson = fs.readJSONSync(this._pkgJsonPath);
		this._themeConfig = new ThemeConfig(this);
		this._workspaceDir = this._findWorkspaceDir(projectDir);
	}

	_findWorkspaceDir(dir) {
		dir = path.resolve(dir);

		if (fs.existsSync(path.join(dir, 'yarn.lock'))) {
			return new FilePath(dir);
		}

		const parentDir = path.dirname(dir);

		if (parentDir === dir) {
			return undefined;
		}

		return this._findWorkspaceDir(parentDir);
	}

	_reload() {
		Object.keys(this).forEach((key) => delete this[key]);

		this._loadFrom('.');
	}
}

module.exports = new Project('.');
