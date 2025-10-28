/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {MimeTypes} from '../../../../../src/main/resources/META-INF/resources/js/common/components/AssetIcon';
import {AssetUsageListModal} from '../../../../../src/main/resources/META-INF/resources/js/common/components/asset_usage/AssetUsageListModal';

describe('AssetUsageListModal', () => {
	it('renders the usage modal with a single asset and deletion type is Permanent Deletion', () => {
		const handleDeleteFn = jest.fn();
		const handleCloseModalFn = jest.fn();

		render(
			<AssetUsageListModal
				apiParams={{
					apiURL: undefined,
					selectAll: false,
				}}
				closeModal={handleCloseModalFn}
				initialData={{
					items: [
						{
							attributes: {
								deletionType: 'PERMANENT_DELETION',
								mimeType: MimeTypes.BasicWebContent,
								type: 'ASSET',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 36774,
							name: 'my asset',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 1,
					totalCount: 1,
				}}
				onDelete={handleDeleteFn}
			/>
		);

		expect(screen.getByText('my asset')).toBeInTheDocument();
		expect(screen.getByText('delete-x')).toBeInTheDocument();
		expect(
			screen.getByText(
				'this-item-is-being-used-in-other-assets-or-pages.-deleting-it-will-break-those-references-and-cause-broken-links-or-missing-content.-this-action-cannot-be-undone.-are-you-sure-you-want-to-continue?'
			)
		).toBeInTheDocument();

		expect(handleDeleteFn).not.toHaveBeenCalled();
		expect(handleCloseModalFn).not.toHaveBeenCalled();

		fireEvent.click(screen.getByText('delete-entry'));

		expect(handleDeleteFn).toHaveBeenCalledTimes(1);
		expect(handleCloseModalFn).toHaveBeenCalledTimes(1);
	});

	it('renders the usage modal with a single asset and deletion type is Recycle Bin', () => {
		render(
			<AssetUsageListModal
				apiParams={{
					apiURL: undefined,
					selectAll: false,
				}}
				closeModal={jest.fn()}
				initialData={{
					items: [
						{
							attributes: {
								deletionType: 'RECYCLE_BIN',
								mimeType: MimeTypes.BasicWebContent,
								type: 'ASSET',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 36774,
							name: 'my asset',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 1,
					totalCount: 1,
				}}
				onDelete={jest.fn()}
			/>
		);

		expect(screen.getByText('my asset')).toBeInTheDocument();
		expect(screen.getByText('delete-x')).toBeInTheDocument();
		expect(
			screen.getByText(
				'this-item-is-being-used-in-other-assets-or-pages.-sending-it-to-the-recycle-will-break-those-references-and-cause-broken-links-or-missing-content.-this-action-cannot-be-undone.-are-you-sure-you-want-to-continue?'
			)
		).toBeInTheDocument();
	});

	it('renders the usage modal with a single folder and deletion type is Permanent Deletion', () => {
		render(
			<AssetUsageListModal
				apiParams={{
					apiURL: undefined,
					selectAll: false,
				}}
				closeModal={jest.fn()}
				initialData={{
					items: [
						{
							attributes: {
								deletionType: 'PERMANENT_DELETION',
								itemsCount: 1,
								mimeType: MimeTypes.Folder,
								type: 'FOLDER',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 36774,
							name: 'my folder',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 1,
					totalCount: 1,
				}}
				onDelete={jest.fn()}
			/>
		);

		expect(screen.getByText('my folder')).toBeInTheDocument();
		expect(screen.getByText('delete-x')).toBeInTheDocument();
		expect(
			screen.getByText(
				'the-contents-of-this-folder-may-be-used-in-other-assets-or-pages.-deleting-it-will-permanently-remove-all-files-and-subfolders.-do-you-want-to-continue?'
			)
		).toBeInTheDocument();
	});

	it('renders the usage modal with a single folder and deletion type is Recycle Bin', () => {
		render(
			<AssetUsageListModal
				apiParams={{
					apiURL: undefined,
					selectAll: false,
				}}
				closeModal={jest.fn()}
				initialData={{
					items: [
						{
							attributes: {
								deletionType: 'RECYCLE_BIN',
								itemsCount: 1,
								mimeType: MimeTypes.Folder,
								type: 'FOLDER',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 36774,
							name: 'my folder',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 1,
					totalCount: 1,
				}}
				onDelete={jest.fn()}
			/>
		);

		expect(screen.getByText('my folder')).toBeInTheDocument();
		expect(screen.getByText('delete-x')).toBeInTheDocument();
		expect(
			screen.getByText(
				'the-contents-of-this-folder-may-be-used-in-other-assets-or-pages.-sending-it-to-the-recycle-bin-will-break-references-to-its-files-and-subfolders,-potentially-causing-broken-links.-do-you-want-to-continue?'
			)
		).toBeInTheDocument();
	});

	it('renders the usage modal with multiple items', () => {
		render(
			<AssetUsageListModal
				apiParams={{
					apiURL: undefined,
					selectAll: false,
				}}
				closeModal={jest.fn()}
				initialData={{
					items: [
						{
							attributes: {
								deletionType: 'RECYCLE_BIN',
								itemsCount: 1,
								mimeType: MimeTypes.Folder,
								type: 'FOLDER',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 123,
							name: 'my folder',
						},
						{
							attributes: {
								deletionType: 'PERMANENT_DELETION',
								mimeType: MimeTypes.BasicWebContent,
								type: 'ASSET',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 456,
							name: 'my web content',
						},
						{
							attributes: {
								deletionType: 'RECYCLE_BIN',
								mimeType: MimeTypes.Blog,
								type: 'ASSET',
								usages: 2,
							},
							classExternalReferenceCode:
								'69fd959a-8457-f4f2-3a0a-7c58e5ec9767',
							className:
								'com.liferay.object.model.ObjectDefinition#1',
							classPK: 789,
							name: 'my blog',
						},
					],
					lastPage: 1,
					page: 1,
					pageSize: 1,
					totalCount: 3,
				}}
				onDelete={jest.fn()}
			/>
		);

		expect(screen.getByText('delete-entries')).toBeInTheDocument();
		expect(screen.getByText('delete')).toBeInTheDocument();
		expect(
			screen.getByText(
				'some-items-are-being-used-in-other-assets-or-pages.-deleting-them-will-break-those-references-and-cause-broken-links-or-missing-content.-this-action-cannot-be-undone.-are-you-sure-you-want-to-continue?'
			)
		).toBeInTheDocument();
	});
});
