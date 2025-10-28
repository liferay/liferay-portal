/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';

import '@testing-library/jest-dom';
import React from 'react';

import {NodeInformationBaseSection} from '../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/NodeInformationBaseSection';

const mockElements = [];
const mockErrors = {};
const mockSelectedItem = {
	data: {
		description: '',
		label: {},
	},
	id: 'existing-node-id',
};
const mockSetErrors = jest.fn();
const mockSetSelectedItem = jest.fn();
const mockSetSelectedItemNewId = jest.fn();

describe('NodeInformationBaseSection component', () => {
	it('renders correctly with label, name and description inputs with the correct ID', () => {
		render(
			<NodeInformationBaseSection
				elements={mockElements}
				errors={mockErrors}
				selectedItem={mockSelectedItem}
				selectedItemNewId=""
				selectedLanguageId=""
				setErrors={mockSetErrors}
				setSelectedItem={mockSetSelectedItem}
				setSelectedItemNewId={mockSetSelectedItemNewId}
			/>
		);

		expect(
			document.getElementById('workflowDefinitionBaseNodeName')
		).toBeInTheDocument();
		expect(
			document.getElementById('workflowDefinitionBaseNodeLabel')
		).toBeInTheDocument();
		expect(
			document.getElementById('workflowDefinitionBaseNodeDescription')
		).toBeInTheDocument();
	});
});
