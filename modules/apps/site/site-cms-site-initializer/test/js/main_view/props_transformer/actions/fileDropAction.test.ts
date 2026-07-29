/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fileDropAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/fileDropAction';
import multipleFilesUploadAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/multipleFilesUploadAction';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/multipleFilesUploadAction',
	() => ({
		__esModule: true,
		default: jest.fn(),
	})
);

jest.mock('frontend-js-web', () => ({
	navigate: jest.fn(),
}));

const allAssetLibraries = [
	{externalReferenceCode: 'space-a', groupId: 1001, name: 'Space A'},
	{externalReferenceCode: 'space-b', groupId: 1002, name: 'Space B'},
];

const currentSpaceAssetLibraries = [allAssetLibraries[0]];

const objectEntryLinkProps = {
	objectEntryId: '55',
	relationshipObjectFieldName: 'r_cmpTaskToCMPTaskLinks_c_cmpTaskId',
	restContextPath: '/o/cmp/task-links',
	scopeGroupId: '1001',
};

const baseAdditionalProps = {
	assetLibraries: allAssetLibraries,
	baseAssetLibraryViewURL: '/space/',
	baseFolderViewURL: '/folder/',
	candidateAssetLibraries: currentSpaceAssetLibraries,
	documentClassName: 'com.example.CMSBasicDocument',
	objectEntryLinkProps,
	parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
	redirect: '/back',
};

const droppedFile = {name: 'report.pdf', size: 123};

describe('fileDropAction', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('does nothing when no files are dropped', () => {
		fileDropAction(baseAdditionalProps, null);

		expect(multipleFilesUploadAction).not.toHaveBeenCalled();
	});

	it('flattens the object entry link context into the upload data', () => {
		fileDropAction(baseAdditionalProps, [droppedFile]);

		const [data] = (multipleFilesUploadAction as jest.Mock).mock.calls[0];

		expect(data).toMatchObject({
			...objectEntryLinkProps,
			documentClassName: 'com.example.CMSBasicDocument',
		});
	});

	it('omits the link fields when the drop has no object entry to link to', () => {
		const {objectEntryLinkProps: _unused, ...withoutContext} =
			baseAdditionalProps;

		fileDropAction(withoutContext, [droppedFile]);

		const [data] = (multipleFilesUploadAction as jest.Mock).mock.calls[0];

		expect(data.objectEntryId).toBeUndefined();
		expect(data.restContextPath).toBeUndefined();
	});

	it('passes candidateAssetLibraries to the upload modal so the current Space is implicit', () => {
		fileDropAction(baseAdditionalProps, [droppedFile]);

		const [data] = (multipleFilesUploadAction as jest.Mock).mock.calls[0];

		expect(data.assetLibraries).toEqual(currentSpaceAssetLibraries);
		expect(data.assetLibraries).not.toEqual(allAssetLibraries);
	});

	it('uses the parent folder ERC from additionalProps when no dropTarget is provided', () => {
		fileDropAction(baseAdditionalProps, [droppedFile]);

		const [data] = (multipleFilesUploadAction as jest.Mock).mock.calls[0];

		expect(data.parentObjectEntryFolderExternalReferenceCode).toBe(
			'L_FILES'
		);
	});

	it('uses the dropTarget folder ERC when a dropTarget is provided', () => {
		const dropTarget = {
			embedded: {externalReferenceCode: 'subfolder-erc', id: 42},
		};

		fileDropAction(baseAdditionalProps, [droppedFile], dropTarget);

		const [data] = (multipleFilesUploadAction as jest.Mock).mock.calls[0];

		expect(data.parentObjectEntryFolderExternalReferenceCode).toBe(
			'subfolder-erc'
		);
	});
});
