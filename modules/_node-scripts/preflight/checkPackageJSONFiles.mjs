/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fg from 'fast-glob';
import fs from 'fs';
import path from 'path';

import {getRootDir} from '../util/constants.mjs';
import projectScopeRequire from '../util/projectScopeRequire.mjs';

/**
 * Runs checks against package.json files; detects:
 *
 * - Bad package names (ie. packages without named scopes).
 * - Banned dependencies.
 *
 * Returns a (possibly empty) array of error messages.
 */
export async function checkPackageJSONFiles() {
	let packages = await fg('**/package.json', {
		ignore: [
			'_node-scripts',
			'**/build',
			'**/classes',
			'**/frontend-js-jquery-web',
			'**/node_modules',
			'**/osb-faro-theme',
			'**/osb-faro-web',
			'**/sdk',
			'test',
		],
	});

	packages = packages.filter((packagePath) => {

		// Ignore serveral package.json files

		if (
			packagePath.endsWith('modules/package.json') ||
			packagePath.endsWith('modules/_node-scripts/package.json') ||
			packagePath.endsWith('modules/test/playwright/package.json') ||
			packagePath.includes('/workspaces/')
		) {
			return false;
		}

		// Filters out packages that have their own yarn.lock

		return !fs.existsSync(
			path.join(path.dirname(packagePath), 'yarn.lock')
		);
	});

	const errors = [];

	const definedDependenciesSet = await collectDefinedDependencies();

	packages.forEach((pkg) => {
		const bad = (message) => errors.push(`${pkg}: BAD - ${message}`);

		try {
			const {dependencies, main, name} = JSON.parse(
				fs.readFileSync(pkg),
				'utf8'
			);

			// Check for bad package names.

			if (
				name &&
				!name.startsWith('@liferay/') &&
				!ALLOWED_NAMED_SCOPE_EXCEPTIONS.includes(name)
			) {
				bad(
					`package name ${name} should be under @liferay/ named scope - https://git.io/JOgy7`
				);
			}

			// Check for banned dependencies.

			const dependencyNames = dependencies
				? Object.keys(dependencies)
				: [];

			dependencyNames.forEach((name) => {
				if (
					!definedDependenciesSet.has(name) &&
					!ALLOWED_NON_GLOBAL_DEPENDENCIES.includes(name)
				) {
					bad(
						`dependency not provided by a specific module: ${name} - See https://issues.liferay.com/browse/LPS-168443\n`
					);
				}
			});

			// Check for main entry point

			const moduleDir = path.join(pkg, '..');

			if (!main) {
				const indexExists = [
					'index.js',
					'index.es.js',
					'index.ts',
					'index.tsx',
				].find(
					(file) =>
						fs.existsSync(
							path.join(
								moduleDir,
								'src/main/resources/META-INF/resources',
								file
							)
						) ||
						fs.existsSync(
							path.join(
								moduleDir,
								'src/main/resources/META-INF/resources/js',
								file
							)
						)
				);

				if (indexExists) {
					bad(
						`package.json doesn't contain a "main" entry point when you have an ${indexExists} file - https://github.com/liferay/liferay-frontend-projects/issues/719`
					);
				}
			}

			// Check that main entry point doesn't 'export default'

			if (main && main !== 'package.json' && !pkg.includes('client-js')) {
				const filePath = path.join(
					moduleDir,
					'src/main/resources/META-INF/resources',
					main
				);

				if (!fs.existsSync(filePath)) {
					bad(
						`package.json contains a "main" entry point that doesn't exist.`
					);
				}
				else {
					const entryFile = fs.readFileSync(filePath);

					if (entryFile.toString().match(/\s*export\s+default\s*/i)) {
						bad(
							`package.json's "main" entry point contains "export default". Use named exports only.`
						);
					}
				}
			}
		}
		catch (error) {
			bad(`error thrown during checks: ${error}`);
		}
	});

	return errors;
}

async function collectDefinedDependencies() {
	let rootConfig = {};

	const rootDir = await getRootDir();

	if (!rootDir) {
		return new Set();
	}

	try {
		rootConfig = await projectScopeRequire(
			path.join(rootDir, 'node-scripts.config.js')
		);
	}
	catch (error) {
		return new Set();
	}

	const esmImports = new Set(
		Object.entries(rootConfig.imports).reduce(
			(acc, [moduleName, dependencies]) => {
				return [...acc, moduleName, ...dependencies];
			},
			[]
		)
	);

	return new Set([...esmImports]);
}

const ALLOWED_NAMED_SCOPE_EXCEPTIONS = [
	'account-admin-web',
	'adaptive-media-image-js-web',
	'adaptive-media-web',
	'admin-dxp-theme',
	'analytics-client-js',
	'analytics-reports-web',
	'announcements-web',
	'app-builder-web',
	'app-builder-workflow-web',
	'asset-categories-admin-web',
	'asset-categories-item-selector-web',
	'asset-list-web',
	'asset-publisher-web',
	'asset-taglib',
	'asset-tags-admin-web',
	'blogs-web',
	'bookmarks-web',
	'calendar-web',
	'change-tracking-web',
	'classic-dxp-theme',
	'click-to-chat-web',
	'com-liferay-dynamic-data-mapping-test',
	'com-liferay-osb-loop-private',
	'com.liferay.osb.www.resources',
	'commerce-bom-admin-web',
	'commerce-bom-web',
	'commerce-cart-taglib',
	'commerce-dashboard-web',
	'commerce-frontend-impl',
	'commerce-frontend-js',
	'commerce-frontend-taglib',
	'commerce-organization-web',
	'commerce-product-content-web',
	'commerce-product-options-web',
	'contacts-web',
	'content-dashboard-web',
	'data-engine-js-components-web',
	'data-engine-rest-impl',
	'data-engine-taglib',
	'depot-web',
	'document-library-opener-onedrive-web',
	'document-library-preview-audio',
	'document-library-preview-document',
	'document-library-preview-image',
	'document-library-preview-video',
	'document-library-video',
	'document-library-web',
	'dxp-cloud-emulator',
	'dynamic-data-lists-web',
	'dynamic-data-mapping-data-provider-web',
	'dynamic-data-mapping-form-builder',
	'dynamic-data-mapping-form-field-type',
	'dynamic-data-mapping-form-renderer',
	'dynamic-data-mapping-form-report-web',
	'dynamic-data-mapping-form-web',
	'dynamic-data-mapping-web',
	'expando-web',
	'export-import-changeset-taglib',
	'exportimport-web',
	'flags-taglib',
	'forms-theme-contributor',
	'fragment-display-web',
	'fragment-renderer-collection-filter-impl',
	'fragment-renderer-react-impl',
	'fragment-resources',
	'fragment-web',
	'frontend-compatibility-ie',
	'frontend-editor-alloyeditor-web',
	'frontend-editor-ckeditor-web',
	'frontend-image-editor-capability-brightness',
	'frontend-image-editor-capability-contrast',
	'frontend-image-editor-capability-crop',
	'frontend-image-editor-capability-effects',
	'frontend-image-editor-capability-resize',
	'frontend-image-editor-capability-rotate',
	'frontend-image-editor-capability-saturation',
	'frontend-image-editor-web',
	'frontend-js-aui-web',
	'frontend-js-clay-sample-web',
	'frontend-js-components-web',
	'frontend-js-jquery-web',
	'frontend-js-loader-modules-extender',
	'frontend-js-lodash-web',
	'frontend-js-react-web',
	'frontend-js-recharts',
	'frontend-js-spa-web',
	'frontend-js-svg4everybody-web',
	'frontend-js-tabs-support-web',
	'frontend-js-tooltip-support-web',
	'frontend-js-web',
	'frontend-taglib',
	'frontend-taglib-chart',
	'frontend-taglib-clay',
	'frontend-taglib-clay-sample-web',
	'frontend-taglib-clay-test-alert-toast-sample-web',
	'frontend-theme-classic-style-guide-sample-web',
	'frontend-theme-font-awesome-web',
	'headless-discovery-web',
	'hello-soy-navigation-web',
	'hello-soy-web',
	'hubspot-js',
	'invitation-invite-members-web',
	'item-selector-taglib',
	'item-selector-upload-web',
	'item-selector-url-web',
	'item-selector-web',
	'japan-theme',
	'journal-article-dynamic-data-mapping-form-field-type',
	'journal-web',
	'knowledge-base-web',
	'layout-admin-web',
	'layout-content-page-editor-web',
	'layout-dynamic-data-mapping-form-field-type',
	'layout-item-selector-web',
	'layout-reports-web',
	'layout-seo-web',
	'layout-set-prototype-web',
	'layout-taglib',
	'lfris-www-components',
	'liferay-admin-theme',
	'liferay-classic-theme',
	'liferay-fjord-theme',
	'liferay-frontend-theme-styled',
	'liferay-frontend-theme-unstyled',
	'liferay-learn',
	'liferay-node-assert',
	'liferay-node-buffer',
	'liferay-node-console',
	'liferay-node-constants',
	'liferay-node-domain',
	'liferay-node-events',
	'liferay-node-os',
	'liferay-node-path',
	'liferay-node-process',
	'liferay-node-punycode',
	'liferay-node-querystring',
	'liferay-node-setimmediate',
	'liferay-node-string_decoder',
	'liferay-node-timers',
	'liferay-node-tty',
	'liferay-node-url',
	'liferay-node-util',
	'liferay-node-vm',
	'liferay-porygon-theme',
	'liferay-user-dashboard-theme',
	'liferay-user-profile-theme',
	'liferay-watson-web',
	'liferay-westeros-bank-theme',
	'map-google-maps',
	'map-openstreetmap',
	'marketing-fragments',
	'marketplace-store-web',
	'message-boards-web',
	'minium-theme',
	'multi-factor-authentication-fido2-web',
	'multi-factor-authentication-timebased-otp-web',
	'my-configurable-fragment',
	'my-sites-web',
	'my-subscriptions-web',
	'notifications-web',
	'oauth2-provider-web',
	'osb-commerce-portal-instance-admin-theme',
	'osb-commerce-provisioning-theme',
	'osb-commerce-provisioning-theme-impl',
	'osb-commerce-provisioning-web',
	'osb-community-doc-project-heading-web',
	'osb-community-doc-project-index-web',
	'osb-community-doc-project-random-nine-web',
	'osb-community-github-top-contributors-web',
	'osb-community-meetup-web',
	'osb-community-theme',
	'osb-customer-account-entry-details',
	'osb-customer-downloads-display',
	'osb-customer-release-tool',
	'osb-customer-theme',
	'osb-emulator',
	'osb-events-theme',
	'osb-faro-theme',
	'osb-faro-web',
	'osb-knowledge-base-theme',
	'osb-loop-theme',
	'osb-provisioning-theme',
	'osb-provisioning-web',
	'osb-www-foundations-theme-contributor',
	'osb-www-theme',
	'password-policies-admin-web',
	'polls-web',
	'portal-portlet-bridge-soy-impl',
	'portal-reports-engine-console-web',
	'portal-search-admin-web',
	'portal-search-ranking-web',
	'portal-search-synonyms-web',
	'portal-search-web',
	'portal-template-react-renderer-impl',
	'portal-workflow-kaleo-designer-web',
	'portal-workflow-kaleo-forms-web',
	'portal-workflow-metrics-web',
	'portal-workflow-task-web',
	'portal-workflow-web',
	'portlet-configuration-css-web',
	'portlet-configuration-web',
	'poshi-language-support',
	'product-navigation-applications-menu-web',
	'product-navigation-control-menu',
	'product-navigation-control-menu-web',
	'product-navigation-simulation-device',
	'product-navigation-taglib',
	'questions-web',
	'ratings-taglib',
	'redirect-web',
	'remote-app-client-js',
	'remote-app-support-web',
	'roles-admin-web',
	'segments-experiment-web',
	'segments-simulation-web',
	'segments-web',
	'server-admin-web',
	'sharing-taglib',
	'sharing-web',
	'site-admin-web',
	'site-membership-web',
	'site-navigation-admin-web',
	'site-navigation-item-selector-web',
	'site-navigation-menu-item-layout',
	'site-navigation-menu-web',
	'site-teams-web',
	'social-bookmarks-taglib',
	'speedwell-theme',
	'staging-bar-web',
	'staging-processes-web',
	'staging-taglib',
	'style-book-web',
	'testray-theme',
	'theme-contributor',
	'translation-web',
	'trash-web',
	'user-associated-data-web',
	'user-dashboard-dxp-theme',
	'user-groups-admin-web',
	'user-profile-dxp-theme',
	'users-admin-web',
	'watson-theme',
	'wiki-web',
	'youtube-web',
];

const ALLOWED_NON_GLOBAL_DEPENDENCIES = [
	'@ckeditor/ckeditor5-adapter-ckfinder',
	'@ckeditor/ckeditor5-alignment',
	'@ckeditor/ckeditor5-autoformat',
	'@ckeditor/ckeditor5-autosave',
	'@ckeditor/ckeditor5-basic-styles',
	'@ckeditor/ckeditor5-block-quote',
	'@ckeditor/ckeditor5-bookmark',
	'@ckeditor/ckeditor5-ckbox',
	'@ckeditor/ckeditor5-ckfinder',
	'@ckeditor/ckeditor5-clipboard',
	'@ckeditor/ckeditor5-cloud-services',
	'@ckeditor/ckeditor5-code-block',
	'@ckeditor/ckeditor5-core',
	'@ckeditor/ckeditor5-easy-image',
	'@ckeditor/ckeditor5-editor-balloon',
	'@ckeditor/ckeditor5-editor-classic',
	'@ckeditor/ckeditor5-editor-decoupled',
	'@ckeditor/ckeditor5-editor-inline',
	'@ckeditor/ckeditor5-editor-multi-root',
	'@ckeditor/ckeditor5-emoji',
	'@ckeditor/ckeditor5-engine',
	'@ckeditor/ckeditor5-enter',
	'@ckeditor/ckeditor5-essentials',
	'@ckeditor/ckeditor5-find-and-replace',
	'@ckeditor/ckeditor5-font',
	'@ckeditor/ckeditor5-fullscreen',
	'@ckeditor/ckeditor5-heading',
	'@ckeditor/ckeditor5-highlight',
	'@ckeditor/ckeditor5-horizontal-line',
	'@ckeditor/ckeditor5-html-embed',
	'@ckeditor/ckeditor5-html-support',
	'@ckeditor/ckeditor5-icons',
	'@ckeditor/ckeditor5-image',
	'@ckeditor/ckeditor5-indent',
	'@ckeditor/ckeditor5-language',
	'@ckeditor/ckeditor5-link',
	'@ckeditor/ckeditor5-list',
	'@ckeditor/ckeditor5-markdown-gfm',
	'@ckeditor/ckeditor5-media-embed',
	'@ckeditor/ckeditor5-mention',
	'@ckeditor/ckeditor5-minimap',
	'@ckeditor/ckeditor5-page-break',
	'@ckeditor/ckeditor5-paragraph',
	'@ckeditor/ckeditor5-paste-from-office',
	'@ckeditor/ckeditor5-react',
	'@ckeditor/ckeditor5-remove-format',
	'@ckeditor/ckeditor5-restricted-editing',
	'@ckeditor/ckeditor5-select-all',
	'@ckeditor/ckeditor5-show-blocks',
	'@ckeditor/ckeditor5-source-editing',
	'@ckeditor/ckeditor5-special-characters',
	'@ckeditor/ckeditor5-style',
	'@ckeditor/ckeditor5-table',
	'@ckeditor/ckeditor5-typing',
	'@ckeditor/ckeditor5-ui',
	'@ckeditor/ckeditor5-undo',
	'@ckeditor/ckeditor5-upload',
	'@ckeditor/ckeditor5-utils',
	'@ckeditor/ckeditor5-watchdog',
	'@ckeditor/ckeditor5-widget',
	'@ckeditor/ckeditor5-word-count',
	'@clayui/css',
	'@liferay/amd-loader',
	'@types/request',
	'@types/node-fetch',
	'@vscode/ripgrep',
	'alloy-ui',
	'alloyeditor',
	'axios',
	'base64-js',
	'bluebird',
	'browser-tabs-lock',
	'ckeditor4-react',
	'ckeditor4',
	'ckeditor5',
	'codemirror',
	'core-js',
	'd3',
	'es-module-shims',
	'esbuild',
	'fetch-mock',
	'fs',
	'gulp',
	'hash.js',
	'history',
	'jest-fetch-mock',
	'leaflet',
	'liferay-font-awesome',
	'liferay-theme-tasks',
	'lodash',
	'mini-css-extract-plugin',
	'minimist',
	'os-browserify',
	'path-browserify',
	'path-to-regexp',
	'react-dnd-test-utils',
	'recharts',
	'request',
	'resize-observer-polyfill',
	'resolve',
	'swagger-ui-react',
	'timers-browserify',
];
