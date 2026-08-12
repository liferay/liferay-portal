/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import StructureService from '../../../../src/main/resources/META-INF/resources/js/common/services/StructureService';
import buildObjectDefinition from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildObjectDefinition';
import buildState from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/buildState';
import handleSaveStructure from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/handleSaveStructure';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/StructureService',
	() => ({
		__esModule: true,
		default: {updateStructure: jest.fn()},
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => ({
		config: {objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES'},
	})
);

(globalThis as any).Liferay.Util.sub = (template: string) => template;

function buildDraftState() {
	return buildState({
		mainObjectDefinition: buildObjectDefinition({
			erc: 'erc',
			label: {en_US: 'Label'},
			name: 'name',
			spaces: 'all',
		}),
		objectDefinitions: {},
	})!;
}

it('ends the operation when the save fails so the Save button stops loading', async () => {
	(StructureService.updateStructure as jest.Mock).mockResolvedValue({
		error: 'in-use',
	});

	const dispatch = jest.fn();

	await handleSaveStructure({
		dispatch,
		state: buildDraftState(),
		validate: () => true,
	});

	expect(dispatch).toHaveBeenCalledWith({type: 'end-operation'});
});

it('resets the structure status to draft after a successful update save so the Save button becomes interactable again', async () => {
	(StructureService.updateStructure as jest.Mock).mockResolvedValue({
		error: null,
	});

	const dispatch = jest.fn();

	await handleSaveStructure({
		dispatch,
		state: buildDraftState(),
		validate: () => true,
	});

	expect(dispatch).toHaveBeenCalledWith({
		type: 'save-structure',
	});
});
