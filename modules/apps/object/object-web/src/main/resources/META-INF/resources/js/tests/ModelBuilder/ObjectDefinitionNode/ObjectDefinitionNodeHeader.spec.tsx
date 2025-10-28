/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ObjectDefinitionNodeHeader from '../../../components/ModelBuilder/ObjectDefinitionNode/ObjectDefinitionNodeHeader';

jest.mock(
	'../../../components/ModelBuilder/ModelBuilderContext/objectFolderContext',
	() => {
		return {
			useObjectFolderContext() {
				return [{}, jest.fn()];
			},
		};
	}
);

jest.mock('react-flow-renderer', () => {
	return {
		useStore() {
			return {
				edges: [],
				nodes: [],
			};
		},
	};
});

describe('Object Definition Node Header', () => {
	afterAll(() => {
		window.Liferay.FeatureFlags['LPD-34594'] = false;
	});

	beforeAll(() => {
		window.Liferay.FeatureFlags['LPD-34594'] = true;
	});

	it('has root object label when it is a parent', () => {
		render(
			<ObjectDefinitionNodeHeader
				dbTableName=""
				dropDownItems={[]}
				handleSelectObjectDefinitionNode={() => {}}
				isLinkedObjectDefinition={false}
				isRootDescendantNode={false}
				isRootNode={true}
				objectDefinitionLabel="Parent Object Definition"
				status={{code: 0, label: 'approved', label_i18n: 'Approved'}}
				system={false}
			/>
		);

		const [objecDefinitionNodeInheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(objecDefinitionNodeInheritanceLabel).toBeVisible();
		expect(objecDefinitionNodeInheritanceLabel).toHaveTextContent(
			'root-object'
		);
	});

	it('has inherited object label when it is a child', () => {
		render(
			<ObjectDefinitionNodeHeader
				dbTableName=""
				dropDownItems={[]}
				handleSelectObjectDefinitionNode={() => {}}
				isLinkedObjectDefinition={false}
				isRootDescendantNode={true}
				isRootNode={false}
				objectDefinitionLabel="Child Object Definition"
				status={{code: 0, label: 'approved', label_i18n: 'Approved'}}
				system={false}
			/>
		);

		const [objecDefinitionNodeInheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(objecDefinitionNodeInheritanceLabel).toBeVisible();
		expect(objecDefinitionNodeInheritanceLabel).toHaveTextContent(
			'inherited'
		);
	});

	it('has standard object label when it is not in a tree structure', () => {
		render(
			<ObjectDefinitionNodeHeader
				dbTableName=""
				dropDownItems={[]}
				handleSelectObjectDefinitionNode={() => {}}
				isLinkedObjectDefinition={false}
				isRootDescendantNode={false}
				isRootNode={false}
				objectDefinitionLabel="Standard Object Definition"
				status={{code: 0, label: 'approved', label_i18n: 'Approved'}}
				system={false}
			/>
		);

		const [objecDefinitionNodeInheritanceLabel] =
			document.getElementsByClassName('label-inverse-secondary');

		expect(objecDefinitionNodeInheritanceLabel).toBeVisible();
		expect(objecDefinitionNodeInheritanceLabel).toHaveTextContent(
			'standard'
		);
	});
});
