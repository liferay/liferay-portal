/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';

import ObjectDefinitionInheritanceDataRenderer from '../../components/ViewObjectDefinitions/FDSDataRenderers/ObjectDefinitionInheritanceDataRenderer';

const standardObjectDefinitionMock = {
	active: false,
	defaultLanguageId: 'en_US',
	externalReferenceCode: 'e11aa801-238b-f2d2-195c-e96b6b03dbd5',
	id: 0,
	label: {en_US: 'Label Test'},
	modifiable: false,
	name: 'Object Name',
	pluralLabel: {en_US: 'Plural Label Test'},
	system: false,
	titleObjectFieldName: '',
} as ObjectDefinition;

const rootObjectDefinitionMock = {
	...standardObjectDefinitionMock,
	objectDefinitionSettings: [
		{
			name: 'rootObjectDefinitionExternalReferenceCodes',
			value: 'e11aa801-238b-f2d2-195c-e96b6b03dbd5',
		},
	],
};

const inheritedObjectDefinitionMock = {
	...standardObjectDefinitionMock,
	objectDefinitionSettings: [
		{
			name: 'rootObjectDefinitionExternalReferenceCodes',
			value: '660defb8-7549-8191-3174-fca8bad17656',
		},
	],
};

describe('The ObjectDefinitionInheritanceDataRenderer component should', () => {
	it('return inherited label for the root object children', () => {
		render(
			ObjectDefinitionInheritanceDataRenderer({
				itemData: inheritedObjectDefinitionMock,
			})
		);
		const [objecDefinitionInheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(objecDefinitionInheritanceLabel).toBeVisible();
		expect(objecDefinitionInheritanceLabel).toHaveTextContent('inherited');
	});

	it('return root object label for objects that are the root object', () => {
		render(
			ObjectDefinitionInheritanceDataRenderer({
				itemData: rootObjectDefinitionMock,
			})
		);
		const [objecDefinitionInheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(objecDefinitionInheritanceLabel).toBeVisible();
		expect(objecDefinitionInheritanceLabel).toHaveTextContent(
			'root-object'
		);
	});

	it('return standard label for objects that are not within root model', () => {
		render(
			ObjectDefinitionInheritanceDataRenderer({
				itemData: standardObjectDefinitionMock,
			})
		);
		const [objecDefinitionInheritanceLabel] =
			document.getElementsByClassName('label-inverse-secondary');

		expect(objecDefinitionInheritanceLabel).toBeVisible();
		expect(objecDefinitionInheritanceLabel).toHaveTextContent('standard');
	});
});
