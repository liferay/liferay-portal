/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	formatVariablesForTextarea,
	parseVariablesInput,
} from '../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/util/parseVariables';

it('Return undefined when there is no input', () => {
	expect(parseVariablesInput('')).toBeUndefined();
	expect(parseVariablesInput(' \n\t')).toBeUndefined();
	expect(parseVariablesInput(undefined)).toBeUndefined();
});

it('Parse a JSON array', () => {
	expect(
		parseVariablesInput('[{"name":"tone","type":"string"}]')
	).toStrictEqual([{name: 'tone', type: 'string'}]);
});

it('Return the raw text when the input is not JSON', () => {
	expect(parseVariablesInput('not json')).toBe('not json');
});

it('Format an undefined value as an empty textarea', () => {
	expect(formatVariablesForTextarea(undefined)).toBe('');
});
