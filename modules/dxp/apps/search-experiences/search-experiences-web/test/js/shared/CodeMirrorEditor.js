/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CodeMirror} from '@liferay/frontend-js-codemirror-web';
import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {getCodeMirrorHints} from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/CodeMirrorEditor';
import aggregationConfigurationSchema from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/schemas/aggregation-configuration.schema.json';
import parameterConfigurationSchema from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/schemas/parameter-configuration.schema.json';
import sortConfigurationSchema from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/schemas/sort-configuration.schema.json';
import sxpQueryElementSchema from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/schemas/sxp-query-element.schema.json';

const availableLanguages = {
	de_DE: 'German (Germany)',
	en_US: 'English (United States)',
	es_ES: 'Spanish (Spain)',
	fr_FR: 'French (France)',
};

// Prevents `TypeError: document.body.createTextRange is not a function`
// error, since CodeMirror expects to be running in a browser.
// See https://github.com/neo4j-contrib/cypher-editor/issues/19

global.document.body.createTextRange = () => {
	return {
		getBoundingClientRect: () => {},
		getClientRects: () => [],
	};
};

function createCodeMirrorEditor(props) {
	document.createElement('div');

	const codeMirror = CodeMirror(document.body, {
		hintOptions: {
			completeSingle: false,
		},
		mode: {globalVars: true, name: 'application/json'},
		value: '',
		...props,
	});

	codeMirror.execCommand('goLineEnd');

	return codeMirror;
}

describe('CodeMirrorEditor', () => {
	it('gets hints at the root level of an object', () => {
		const codeMirror = createCodeMirrorEditor({value: '{"'});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual([
			'description_i18n#object',
			'elementDefinition#object',
			'title_i18n#object',
		]);
	});

	it('filters hints for matched strings', () => {
		const codeMirror = createCodeMirrorEditor({value: '{"desc'});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual([
			'description_i18n#object',
		]);
	});

	it('gets hints when property has an enum array', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"uiConfiguration": {"fieldSets": [ {"fields": [{"type":"',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual([
			'date',
			'fieldMapping',
			'fieldMappingList',
			'json',
			'keywords',
			'multiselect',
			'number',
			'searchableType',
			'select',
			'slider',
			'text',
		]);
	});

	it('gets hints when property uses language IDs', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"description_i18n": {"',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual(
			Object.keys(availableLanguages).map(
				(language) => `${language}#string`
			)
		);
	});

	it('gets hints from both individual properties and `allOf` property', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"sorts": [{"_geo_distance": {"',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sortConfigurationSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual([
			'distance_type#string',
			'field#string',
			'locations#array',
			'unit#string',
			'missing#string',
			'mode#string',
			'nested#object',
			'order#string',
		]);
	});

	it('gets hints when schema path uses `additionalProperties`', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"parameters": {"param_name": {"',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			parameterConfigurationSchema,
			availableLanguages
		);

		expect(list.map(({displayText}) => displayText)).toEqual([
			'defaultValue#object',
			'format#string',
			'max#object',
			'min#object',
			'type#string',
		]);
	});

	it('displays the hint name and single type when rendered', () => {
		const {container} = render(<div />);

		const codeMirror = createCodeMirrorEditor({
			value: '{"',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].render(container, codeMirror, list[0]);

		expect(container.querySelector('.hint-name')).toHaveTextContent(
			'description_i18n'
		);
		expect(container.querySelector('.hint-type')).toHaveTextContent(
			'object'
		);
	});

	it('displays the hint name and multiple types when rendered', () => {
		const {container} = render(<div />);

		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"uiConfiguration": {"fieldSets": [{"fields": [{"defaultValue',
		});

		const {list} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].render(container, codeMirror, list[0]);

		expect(container.querySelector('.hint-name')).toHaveTextContent(
			'defaultValue'
		);
		expect(container.querySelector('.hint-type')).toHaveTextContent(
			'array|number|object|string'
		);
	});

	it('autocompletes with [] for array type', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"configuration": {"queryConfiguration": {"qu',
		});

		const {from, list, to} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].hint(codeMirror, {from, to}, {});

		expect(codeMirror.getDoc().getValue()).toEqual(
			'{"elementDefinition": {"configuration": {"queryConfiguration": {"queryEntries": []'
		);
	});

	it('autocompletes with {} for object type', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"configuration": {"qu',
		});

		const {from, list, to} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].hint(codeMirror, {from, to}, {});

		expect(codeMirror.getDoc().getValue()).toEqual(
			'{"elementDefinition": {"configuration": {"queryConfiguration": {}'
		);
	});

	it('autocompletes with "" for string type', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"ca',
		});

		const {from, list, to} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].hint(codeMirror, {from, to}, {});

		expect(codeMirror.getDoc().getValue()).toEqual(
			'{"elementDefinition": {"category": ""'
		);
	});

	it('autocompletes minimally for other type', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"elementDefinition": {"configuration": {"queryConfiguration": {"queryEntries": [{"en',
		});

		const {from, list, to} = getCodeMirrorHints(
			codeMirror,
			sxpQueryElementSchema,
			availableLanguages
		);

		list[0].hint(codeMirror, {from, to}, {});

		expect(codeMirror.getDoc().getValue()).toEqual(
			'{"elementDefinition": {"configuration": {"queryConfiguration": {"queryEntries": [{"enabled": '
		);
	});

	it('autocompletes with specific text for special aggregation case', () => {
		const codeMirror = createCodeMirrorEditor({
			value: '{"aggs',
		});

		const {from, list, to} = getCodeMirrorHints(
			codeMirror,
			aggregationConfigurationSchema,
			availableLanguages
		);

		list[0].hint(codeMirror, {from, to}, {});

		expect(codeMirror.getDoc().getValue()).toEqual(
			'{"aggs": {\n\t"NAME": {\n\t\t"AGG_TYPE": {}\n\t}\n}'
		);
	});
});
