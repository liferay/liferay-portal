/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';
import xml2js from 'xml2js';

import project from './project.mjs';

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

export function buildXML(lookAndFeelJSON, doctypeElement) {
	const lookAndFeel = lookAndFeelJSON[STR_LOOK_AND_FEEL];

	const themeElement = lookAndFeel.theme[0];

	lookAndFeel.theme[0] = THEME_CHILD_ORDER.reduce((result, item) => {
		if (themeElement[item]) {
			result[item] = themeElement[item];
		}

		return result;
	}, {});

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

export function correctJSONIdentifiers(lookAndFeelJSON, name) {
	const themeAttrs = lookAndFeelJSON[STR_LOOK_AND_FEEL].theme[0].$;

	if (name !== themeAttrs.name) {
		themeAttrs.name = name;
		themeAttrs.id = kebabCase(name);
	}

	return lookAndFeelJSON;
}

export function getLookAndFeelDoctype(themePath) {
	const xmlString = readLookAndFeelXML(themePath);

	let match;

	if (xmlString) {
		match = xmlString.match(/(<!DOCTYPE.*>)/);
	}

	return match ? match[0] : null;
}

export function getLookAndFeelDoctypeByVersion(version) {
	version += '.0';

	return (
		`<!DOCTYPE look-and-feel PUBLIC "-//Liferay//DTD Look and Feel ` +
		`${version}//EN" "http://www.liferay.com/dtd/liferay-look-and-feel_` +
		`${version.replace(/\./g, '_')}.dtd">`
	);
}

export async function getLookAndFeelJSON(themePath) {
	const xmlString = readLookAndFeelXML(themePath);

	if (!xmlString) {
		return;
	}

	return await xml2js.parseStringPromise(xmlString);
}

export function getNameFromPluginPackageProperties(themePath) {
	const {pathSrc} = project.options;

	const pluginPackageProperties = fs.readFileSync(
		path.join(
			themePath,
			pathSrc,
			'WEB-INF',
			'liferay-plugin-package.properties'
		),
		'utf8'
	);

	const match = pluginPackageProperties.match(/name=(.*)/);

	return match ? match[1] : null;
}

export function readLookAndFeelXML(themePath) {
	let xmlString = xmlCache[themePath];

	if (xmlString) {
		return xmlString;
	}

	const {pathSrc} = project.options;

	const lookAndFeelPath = path.join(
		themePath,
		pathSrc,
		'WEB-INF',
		'liferay-look-and-feel.xml'
	);

	if (fs.existsSync(lookAndFeelPath)) {
		xmlString = fs.readFileSync(lookAndFeelPath, 'utf8');

		xmlCache[themePath] = xmlString;
	}

	return xmlString;
}

const xmlCache = {};

function kebabCase(string) {
	return string
		.replace(/([a-z\d])([A-Z])/g, '$1 $2')
		.split(/[^A-Za-z\d]+/)
		.filter(Boolean)
		.join('-')
		.toLowerCase();
}
