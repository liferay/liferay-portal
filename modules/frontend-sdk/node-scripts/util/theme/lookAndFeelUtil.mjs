/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const fs = require('fs-extra');
const _ = require('lodash');
const path = require('path');
const util = require('util');
const xml2js = require('xml2js');

const project = require('../../lib/project');

const QUERY_ELEMENTS = {
	'color-scheme': 'id',
	'layout-templates.0.custom.0.layout-template': 'id',
	'layout-templates.0.standard.0.layout-template': 'id',
	'portlet-decorator': 'id',
	'roles': 'single',
	'settings.0.setting': 'key',
};

const STR_LOOK_AND_FEEL = 'look-and-feel';

const THEME_CHILD_ORDER = [
	'$',
	'root-path',
	'templates-path',
	'css-path',
	'images-path',
	'javascript-path',
	'virtual-path',
	'template-extension',
	'settings',
	'control-panel-theme',
	'page-theme',
	'wap-theme',
	'roles',
	'color-scheme',
	'layout-templates',
	'portlet-decorator',
];

function buildXML(lookAndFeelJSON, doctypeElement) {
	const themeQuery = 'look-and-feel.theme.0';

	let themeElement = _.get(lookAndFeelJSON, themeQuery);

	themeElement = _.reduce(
		THEME_CHILD_ORDER,
		(result, item) => {
			if (themeElement[item]) {
				result[item] = themeElement[item];
			}

			return result;
		},
		{}
	);

	_.set(lookAndFeelJSON, themeQuery, themeElement);

	const builder = new xml2js.Builder({
		renderOpts: {
			indent: '\t',
			pretty: true,
		},
		xmldec: {
			encoding: null,
			standalone: null,
		},
	});

	let xml = builder.buildObject(lookAndFeelJSON);

	xml = xml.replace(/(<\?xml.*>)/, '$1\n' + doctypeElement + '\n');

	return xml;
}

function correctJSONIdentifiers(lookAndFeelJSON, name) {
	const themeAttrs = lookAndFeelJSON[STR_LOOK_AND_FEEL].theme[0].$;

	if (name !== themeAttrs.name) {
		themeAttrs.name = name;
		themeAttrs.id = _.kebabCase(name);
	}

	return lookAndFeelJSON;
}

function getLookAndFeelDoctype(themePath) {
	const xmlString = readLookAndFeelXML(themePath);

	let match;

	if (xmlString) {
		match = xmlString.match(/(<!DOCTYPE.*>)/);
	}

	return match ? match[0] : null;
}

function getLookAndFeelDoctypeByVersion(version) {
	version += '.0';

	return util.format(
		'<!DOCTYPE look-and-feel PUBLIC "-//Liferay//DTD Look and Feel %s//EN" "http://www.liferay.com/dtd/liferay-look-and-feel_%s.dtd">',
		version,
		version.replace(/\./g, '_')
	);
}

function getLookAndFeelJSON(themePath, callback) {
	const xmlString = readLookAndFeelXML(themePath);

	if (!xmlString) {
		return callback();
	}

	xml2js.parseString(xmlString, (error, result) => {
		if (error) {
			throw error;
		}

		callback(result);
	});
}

function getNameFromPluginPackageProperties(themePath) {
	const {pathSrc} = project.options;

	const pluginPackageProperties = fs.readFileSync(
		path.join(
			themePath,
			pathSrc.asNative,
			'WEB-INF',
			'liferay-plugin-package.properties'
		),
		{
			encoding: 'utf8',
		}
	);

	const match = pluginPackageProperties.match(/name=(.*)/);

	return match ? match[1] : null;
}

function mergeLookAndFeelJSON(themePath, lookAndFeelJSON, callback) {
	getLookAndFeelJSON(themePath, (json) => {
		if (_.isEmpty(lookAndFeelJSON)) {
			lookAndFeelJSON = json;
		}
		else if (json) {
			lookAndFeelJSON = mergeJSON(lookAndFeelJSON, json);
		}

		// eslint-disable-next-line @liferay/no-dynamic-require
		const {liferayTheme: themeInfo} = require(path.join(
			themePath,
			'package.json'
		));

		const baseTheme = themeInfo.baseTheme;

		if (_.isObject(baseTheme)) {
			themePath = path.join(themePath, 'node_modules', baseTheme.name);

			mergeLookAndFeelJSON(themePath, lookAndFeelJSON, callback);
		}
		else {
			callback(lookAndFeelJSON);
		}
	});
}

function readLookAndFeelXML(themePath) {
	let xmlString = xmlCache[themePath];

	if (xmlString) {
		return xmlString;
	}

	const lookAndFeelDefaultPath = path.join(
		themePath,
		'src',
		'WEB-INF',
		'liferay-look-and-feel.xml'
	);

	const {pathSrc} = project.options;

	let lookAndFeelPath = path.join(
		themePath,
		pathSrc.asNative,
		'WEB-INF',
		'liferay-look-and-feel.xml'
	);

	try {
		fs.statSync(lookAndFeelPath);
	}
	catch (error) {
		lookAndFeelPath = lookAndFeelDefaultPath;
	}

	try {
		xmlString = fs.readFileSync(lookAndFeelPath, 'utf8');

		xmlCache[themePath] = xmlString;
	}
	catch (error) {

		// Swallow.

	}

	return xmlString;
}

module.exports = {
	buildXML,
	correctJSONIdentifiers,
	getLookAndFeelDoctype,
	getLookAndFeelDoctypeByVersion,
	getLookAndFeelJSON,
	getNameFromPluginPackageProperties,
	mergeLookAndFeelJSON,
	readLookAndFeelXML,
};

const xmlCache = {};

function extractThemeElement(object, key) {
	return object[STR_LOOK_AND_FEEL].theme[0][key];
}

function mergeJSON(themeObj, baseThemeObj) {
	_.forEach(QUERY_ELEMENTS, (item, index) => {
		let mergedElement;
		const queryString = 'look-and-feel.theme.0.' + index;

		const baseThemeElement = _.get(baseThemeObj, queryString);
		const themeElement = _.get(themeObj, queryString);

		if (item === 'value') {
			mergedElement = mergeThemeElementByValue(
				themeElement,
				baseThemeElement
			);
		}
		else if (item === 'single') {
			mergedElement = themeElement || baseThemeElement;
		}
		else {
			mergedElement = mergeThemeElementById(
				themeElement,
				baseThemeElement,
				item
			);
		}

		if (mergedElement) {
			_.set(themeObj, queryString, mergedElement);
		}
	});

	return themeObj;
}

function mergeThemeElementById(themeElements, baseThemeElements, identifier) {
	if (!themeElements || !baseThemeElements) {
		return themeElements ? themeElements : baseThemeElements;
	}

	identifier = identifier || 'id';

	const allElements = themeElements.concat(baseThemeElements);
	const elementIds = [];

	return _.reduce(
		allElements,
		(result, item) => {
			const id = item.$[identifier];

			if (elementIds.indexOf(id) < 0) {
				elementIds.push(id);

				result.push(item);
			}

			return result;
		},
		[]
	);
}

function mergeThemeElementByValue(themeElements, baseThemeElements) {
	if (!themeElements || !baseThemeElements) {
		return themeElements ? themeElements : baseThemeElements;
	}

	return _.uniq(themeElements.concat(baseThemeElements));
}

// Export private methods when in tests

if (typeof jest !== 'undefined') {
	Object.assign(module.exports, {
		extractThemeElement,
		mergeJSON,
		mergeThemeElementById,
		mergeThemeElementByValue,
		xmlCache,
	});
}
