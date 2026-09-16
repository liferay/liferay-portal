/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import ShortcutManager from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/ShortcutManager';
import {
	State,
	useStateDispatch,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import handleDeleteChildren from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handleDeleteChildren';
import handlePublishStructure from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handlePublishStructure';
import handleSaveStructure from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handleSaveStructure';
import isReferenced from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/isReferenced';
import isRenamable from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/isRenamable';
import openHelpModal from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/openHelpModal';
import openReferencedStructureModal from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/openReferencedStructureModal';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockStateProvider} from '../mocks/MockStateProvider';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext',
	() => {
		const actual = jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext'
		);

		return {
			...actual,
			useStateDispatch: jest.fn(),
		};
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handleDeleteChildren',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handlePublishStructure',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handleSaveStructure',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/isRenamable',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/isReferenced',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/openHelpModal',
	() => jest.fn()
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/openReferencedStructureModal',
	() => jest.fn()
);

const FIELD_UUID = getUuid();
const FIELD = {
	locked: false,
	type: 'text',
	uuid: FIELD_UUID,
} as Field;

const REPEATABLE_GROUP_UUID = getUuid();
const REPEATABLE_GROUP = {
	children: new Map(),
	type: 'group',
	uuid: REPEATABLE_GROUP_UUID,
} as RepeatableGroup;

const STRUCTURE: Partial<Structure> = {
	children: new Map<Uuid, StructureChild>([
		[FIELD_UUID, FIELD],
		[REPEATABLE_GROUP_UUID, REPEATABLE_GROUP],
	]),
};

const renderComponent = ({
	selection,
}: {
	selection?: State['selection'];
} = {}) => {
	return render(
		<MockCacheProvider objectDefinitions={{}} spaces={[]}>
			<MockStateProvider
				state={{
					selection,
					structure: STRUCTURE,
				}}
			>
				<ShortcutManager />
			</MockStateProvider>
		</MockCacheProvider>
	);
};

const mockDispatch = jest.fn();

describe('ShortcutManager', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(useStateDispatch as jest.Mock).mockReturnValue(mockDispatch);
		(isReferenced as jest.Mock).mockReturnValue(false);
	});

	it('executes duplicate with Ctrl+D if it is enabled', () => {
		renderComponent({selection: [FIELD_UUID]});

		fireEvent.keyDown(document.body, {code: 'KeyD', ctrlKey: true});

		expect(mockDispatch).toHaveBeenCalledWith({
			type: 'duplicate-children',
			uuids: [FIELD_UUID],
		});
	});

	it('does not execute shortcut when target is an input element', () => {
		renderComponent({selection: [FIELD_UUID]});

		const input = document.createElement('input');

		document.body.appendChild(input);

		fireEvent.keyDown(input, {code: 'KeyD', ctrlKey: true});

		expect(mockDispatch).not.toHaveBeenCalled();

		document.body.removeChild(input);
	});

	it('executes rename with Ctrl+Alt+R if it is enabled', () => {
		(isRenamable as jest.Mock).mockReturnValue(true);

		renderComponent({selection: [FIELD_UUID]});

		fireEvent.keyDown(document.body, {
			altKey: true,
			code: 'KeyR',
			ctrlKey: true,
		});

		expect(mockDispatch).toHaveBeenCalledWith({
			type: 'set-renaming-item-uuid',
			uuid: FIELD_UUID,
		});
	});

	it('executes handleDeleteChildren with Backspace when selection exists', () => {
		renderComponent({selection: [FIELD_UUID]});

		fireEvent.keyDown(document.body, {code: 'Backspace'});

		expect(handleDeleteChildren).toHaveBeenCalled();
	});

	it('executes handleDeleteChildren with Delete when selection exists', () => {
		renderComponent({selection: [FIELD_UUID]});

		fireEvent.keyDown(document.body, {code: 'Delete'});

		expect(handleDeleteChildren).toHaveBeenCalled();
	});

	it('opens referenced structures modal with Shift+Enter', () => {
		renderComponent();

		fireEvent.keyDown(document.body, {code: 'Enter', shiftKey: true});

		expect(openReferencedStructureModal).toHaveBeenCalled();
	});

	it('executes add repeatable group with Ctrl+R when selection exists', () => {
		renderComponent({selection: [FIELD_UUID]});

		fireEvent.keyDown(document.body, {code: 'KeyG', ctrlKey: true});

		expect(mockDispatch).toHaveBeenCalledWith({
			type: 'add-repeatable-group',
			uuids: [FIELD_UUID],
		});
	});

	it('executes ungroup with Ctrl+Shift+G if a repeatable group is selected', () => {
		renderComponent({
			selection: [REPEATABLE_GROUP_UUID],
		});

		fireEvent.keyDown(document.body, {
			code: 'KeyG',
			ctrlKey: true,
			shiftKey: true,
		});

		expect(mockDispatch).toHaveBeenCalledWith({
			type: 'ungroup',
			uuid: REPEATABLE_GROUP_UUID,
		});
	});

	it('executes save structure with Ctrl+S', () => {
		renderComponent();

		fireEvent.keyDown(document.body, {code: 'KeyS', ctrlKey: true});

		expect(handleSaveStructure).toHaveBeenCalled();
	});

	it('executes publish structure with Ctrl+P', () => {
		renderComponent();

		fireEvent.keyDown(document.body, {code: 'KeyP', ctrlKey: true});

		expect(handlePublishStructure).toHaveBeenCalled();
	});

	it('opens help modal with Shift+?', () => {
		renderComponent();

		fireEvent.keyDown(document.body, {key: '?', shiftKey: true});

		expect(openHelpModal).toHaveBeenCalled();
	});
});
