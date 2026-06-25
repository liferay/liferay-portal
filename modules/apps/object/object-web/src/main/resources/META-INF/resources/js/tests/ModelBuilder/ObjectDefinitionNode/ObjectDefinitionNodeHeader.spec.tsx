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

const baseProps = {
	allowStandaloneObjectEntry: false,
	dbTableName: '',
	dropDownItems: [],
	handleSelectObjectDefinitionNode: () => {},
	isLinkedObjectDefinition: false,
	isRootDescendantNode: false,
	isRootNode: false,
	objectDefinitionLabel: 'Object Definition',
	status: {code: 0, label: 'approved', label_i18n: 'Approved'},
	system: false,
};

describe('Object Definition Node Header', () => {
	it('has root object label when it is a parent', () => {
		render(
			<ObjectDefinitionNodeHeader
				{...baseProps}
				isRootNode={true}
				objectDefinitionLabel="Parent Object Definition"
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
				{...baseProps}
				isRootDescendantNode={true}
				objectDefinitionLabel="Child Object Definition"
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
				{...baseProps}
				objectDefinitionLabel="Standard Object Definition"
			/>
		);

		const [objecDefinitionNodeInheritanceLabel] =
			document.getElementsByClassName('label-inverse-secondary');

		expect(objecDefinitionNodeInheritanceLabel).toBeVisible();
		expect(objecDefinitionNodeInheritanceLabel).toHaveTextContent(
			'standard'
		);
	});

	it('shows lock icon and strict tooltip on a descendant when standalone entries are disabled', () => {
		render(
			<ObjectDefinitionNodeHeader
				{...baseProps}
				allowStandaloneObjectEntry={false}
				isRootDescendantNode={true}
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(inheritanceLabel).toHaveAttribute(
			'title',
			'strict-inheritance-tooltip'
		);
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeTruthy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeFalsy();
	});

	it('shows unlock icon and flexible tooltip on a descendant when standalone entries are allowed', () => {
		render(
			<ObjectDefinitionNodeHeader
				{...baseProps}
				allowStandaloneObjectEntry={true}
				isRootDescendantNode={true}
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(inheritanceLabel).toHaveAttribute(
			'title',
			'flexible-inheritance-tooltip'
		);
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeTruthy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeFalsy();
	});

	it('does not show a lock or unlock icon on a root node', () => {
		render(
			<ObjectDefinitionNodeHeader
				{...baseProps}
				allowStandaloneObjectEntry={false}
				isRootNode={true}
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(inheritanceLabel).not.toHaveAttribute('title');
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeFalsy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeFalsy();
	});

	it('does not show a lock or unlock icon on a standard (non-tree) object', () => {
		render(<ObjectDefinitionNodeHeader {...baseProps} />);

		const [inheritanceLabel] = document.getElementsByClassName(
			'label-inverse-secondary'
		);

		expect(inheritanceLabel).not.toHaveAttribute('title');
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeFalsy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeFalsy();
	});
});
