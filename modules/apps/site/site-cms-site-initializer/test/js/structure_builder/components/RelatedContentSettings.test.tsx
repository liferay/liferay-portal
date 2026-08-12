/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ObjectDefinitions} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import RelatedContentSettings from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/settings/RelatedContentSettings';
import {State} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {RelatedContent} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockState, MockStateProvider} from '../mocks/MockStateProvider';

const RELATED_CONTENT_UUID = getUuid();

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {en_US: 'Related Content'},
	multiselection: false,
	name: 'relatedContent',
	parent: getUuid(),
	relatedStructureERC: 'structure1ERC',
	type: 'related-content',
	uuid: RELATED_CONTENT_UUID,
};

const OBJECT_DEFINITIONS: ObjectDefinitions = {
	structure1ERC: buildObjectDefinition({
		children: new Map(),
		erc: 'structure1ERC',
		label: {
			en_US: 'Structure1',
		},
		name: 'structure1Name',
		spaces: [],
	}),
	structure2ERC: buildObjectDefinition({
		children: new Map(),
		erc: 'structure2ERC',
		label: {
			en_US: 'Structure2',
		},
		name: 'structure2Name',
		spaces: [],
	}),
};

const DEFAULT_STATE: State = {
	clipboard: null,
	history: {
		deletedChildren: [],
		deletedGroupERCs: [],
		deletedRelationships: [],
		modifiedNames: new Set(),
		modifiedSlugs: new Set(),
	},
	invalids: new Map(),
	operation: null,
	publishedChildren: new Set(),
	renamingItemUuid: null,
	savedChildren: new Set(),
	selection: [],
	structure: {
		children: new Map([[RELATED_CONTENT_UUID, RELATED_CONTENT]]),
		erc: 'main-erc',
		label: {en_US: 'MainStructure'},
		name: 'mainStructure',
		path: '',
		slug: '',
		spaces: [],
		status: 'draft',
		system: false,
		type: 'L_CMS_CONTENT_STRUCTURES',
		uuid: getUuid(),
		workflows: {},
	},
	unsavedChanges: false,
};

const MOCK_DISPATCH = jest.fn();

const renderComponent = ({
	disabled = false,
	dispatch = MOCK_DISPATCH,
	objectDefinitions = OBJECT_DEFINITIONS,
	relatedContent = RELATED_CONTENT,
	state = DEFAULT_STATE,
}: {
	disabled?: boolean;
	dispatch?: jest.Mock;
	objectDefinitions?: ObjectDefinitions;
	relatedContent?: RelatedContent;
	state?: MockState;
} = {}) => {
	return render(
		<MockCacheProvider objectDefinitions={objectDefinitions}>
			<MockStateProvider dispatch={dispatch} state={state}>
				<RelatedContentSettings
					disabled={disabled}
					relatedContent={relatedContent}
				/>
			</MockStateProvider>
		</MockCacheProvider>
	);
};

describe('RelatedContentSettings', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('loads correct values', async () => {
		renderComponent();

		expect(screen.getByText('Related Content')).toBeInTheDocument();
		expect(screen.getByText('Structure1')).toBeInTheDocument();
		expect(screen.getByLabelText('multiselection')).not.toBeChecked();
	});

	it('allow changing the erc', async () => {
		renderComponent();

		expect(screen.getByText('select-related-content')).toBeInTheDocument();

		await userEvent.click(screen.getByLabelText('multiselection'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			multiselection: true,
			type: 'update-related-content',
			uuid: RELATED_CONTENT_UUID,
		});

		const ercInput = screen.getByLabelText('erc');

		await userEvent.clear(ercInput);
		await userEvent.type(ercInput, 'next-erc');

		fireEvent.blur(ercInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			erc: 'next-erc',
			type: 'update-related-content',
			uuid: RELATED_CONTENT_UUID,
		});
	});

	it('allow changing multiselection erc', async () => {
		renderComponent();

		await userEvent.click(screen.getByLabelText('multiselection'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			multiselection: true,
			type: 'update-related-content',
			uuid: RELATED_CONTENT_UUID,
		});
	});

	it('allow self-selection', async () => {
		const objectDefinitions: ObjectDefinitions = {
			...OBJECT_DEFINITIONS,
			'main-erc': buildObjectDefinition({
				children: new Map(),
				erc: 'main-erc',
				label: {en_US: 'MainStructure'},
				name: 'mainStructure',
				spaces: [],
			}),
		};

		renderComponent({objectDefinitions});

		const user = userEvent.setup();

		await user.click(
			screen.getByRole('combobox', {name: 'related-content'})
		);

		expect(
			screen.getByRole('option', {name: 'MainStructure'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Structure1'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Structure2'})
		).toBeInTheDocument();
	});
});
