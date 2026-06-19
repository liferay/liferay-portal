/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const stylelint = require('stylelint');

const noBlockComments = require('./rules/no-block-comments.js');
const noHardcodedColors = require('./rules/no-hardcoded-colors.js');
const noImportExtension = require('./rules/no-import-extension.js');
const singleImports = require('./rules/single-imports.js');
const sortImports = require('./rules/sort-imports.js');
const trimComments = require('./rules/trim-comments.js');

const rulesPlugins = [
	noBlockComments,
	noHardcodedColors,
	noImportExtension,
	singleImports,
	sortImports,
	trimComments,
].map((rule) => stylelint.createPlugin(rule.ruleName, rule));

module.exports = rulesPlugins;
