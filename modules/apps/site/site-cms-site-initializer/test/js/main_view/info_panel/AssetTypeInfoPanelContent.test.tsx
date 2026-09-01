/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {SidePanel} from '@clayui/core';
import {cleanup, render, screen, within} from '@testing-library/react';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import AssetTypeInfoPanelContent from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/AssetTypeInfoPanelContent';
import ObjectEntryService from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService';
import {
	CONTENT_OBJECT_ENTRY,
	DOCUMENT_OBJECT_ENTRY,
	FOLDER_OBJECT_ENTRY,
} from './mocks';

const testObjectEntryData = {
	data: {
		numberOfObjectEntries:
			FOLDER_OBJECT_ENTRY.embedded.numberOfObjectEntries,
		numberOfObjectEntryFolders:
			FOLDER_OBJECT_ENTRY.embedded.numberOfObjectEntryFolders,
	},
};

const testSpace = {
	externalReferenceCode: 'a0a0a05e-61d2-248e-4ffa-ae1df094e695',
	id: 36030,
	name: 'GSpace',
	settings: {
		logoColor: 'outline-0',
	},
} as Space;

const testAdditionalProps = {
	breadcrumbProps: {
		breadcrumbItems: [
			{
				label: testSpace.name,
			},
			{
				label: 'content',
			},
			{
				label: 'content-folder',
			},
		],
		displayType: 'outline-0',
		size: 'sm',
	},
	candidateAssetLibraries: [testSpace.id],
	fileMimeTypeIcons: {
		default: 'document-default',
		image: 'document-image',
	},
	objectDefinitionCssClasses: {
		L_CMS_BASIC_WEB_CONTENT: 'content-icon-basic-content',
		default: 'content-icon-custom-structure',
	},
	objectDefinitionIcons: {
		L_CMS_BASIC_WEB_CONTENT: 'forms',
		default: 'web-content',
	},
};

describe('CMS Asset Type Info Panel', () => {
	beforeEach(() => {
		SpaceService.getSpace = jest.fn().mockResolvedValue(testSpace);
		ObjectEntryService.getObjectEntry = jest
			.fn()
			.mockResolvedValue(testObjectEntryData);
	});

	afterEach(() => {
		jest.resetAllMocks();
		cleanup();

		(global as any).Liferay.FeatureFlags = {};
	});

	it('does not render the Projects tab when the CMP feature flag is disabled', () => {
		(global as any).Liferay.FeatureFlags = {};

		render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testAdditionalProps}
					items={[CONTENT_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		expect(screen.getByRole('tab', {name: 'versions'})).toBeInTheDocument();

		expect(
			screen.queryByRole('tab', {name: 'projects'})
		).not.toBeInTheDocument();
	});

	it('renders the Projects tab when the CMP feature flag is enabled', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-58677': true};

		render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testAdditionalProps}
					items={[CONTENT_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		expect(screen.getByRole('tab', {name: 'projects'})).toBeInTheDocument();
	});

	it('renders the tabs with the icon only and the label on hover', () => {
		render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testAdditionalProps}
					items={[CONTENT_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		const tab = screen.getByRole('tab', {name: 'comments'});

		expect(tab).toHaveAttribute('title', 'comments');

		expect(tab.querySelector('.lexicon-icon-comments')).toBeInTheDocument();

		expect(tab.querySelector('.sr-only')).toHaveTextContent('comments');
	});

	it('renders the component for a Web Content asset type', async () => {
		const {container} = render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testAdditionalProps}
					items={[CONTENT_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		expect(container).toBeInTheDocument();

		expect(
			screen.getByRole('heading', {
				name: CONTENT_OBJECT_ENTRY.embedded.title,
			})
		).toBeInTheDocument();

		expect(container.querySelector('use')?.getAttribute('href')).toContain(
			'forms'
		);

		expect(screen.queryByRole('img')).not.toBeInTheDocument();

		expect(screen.getAllByRole('tab')).toHaveLength(5);

		expect(screen.getByRole('tab', {name: 'details'})).toBeInTheDocument();
		expect(
			screen.getByRole('tab', {name: 'categorization'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('tab', {name: 'performance'})
		).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: 'versions'})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: 'comments'})).toBeInTheDocument();

		expect(screen.getByText('metadata')).toBeInTheDocument();

		expect(screen.queryByText('url')).not.toBeInTheDocument();

		expect(screen.getByText('location')).toBeInTheDocument();

		expect(screen.getByText('author')).toBeInTheDocument();

		expect(
			screen.getByText(CONTENT_OBJECT_ENTRY.embedded.creator.name)
		).toBeInTheDocument();

		expect(screen.getByText('created')).toBeInTheDocument();

		expect(screen.getByText('modified')).toBeInTheDocument();

		expect(screen.getByText('never-expire')).toBeInTheDocument();

		expect(screen.getByText('never-review')).toBeInTheDocument();

		const breadcrumb = screen.getByLabelText('Breadcrumb');

		expect(
			await within(breadcrumb).findByRole('button', {
				name: testSpace.name,
			})
		).toBeInTheDocument();

		expect(
			within(breadcrumb).getByRole('button', {name: 'content'})
		).toBeInTheDocument();
	});

	it('renders the component for an Image asset type', async () => {
		const testFileAdditionalProps = {
			...testAdditionalProps,
			breadcrumbProps: {
				...testAdditionalProps.breadcrumbProps,
				breadcrumbItems: [
					{
						label: testSpace.name,
					},
					{
						label: 'files',
					},
				],
			},
		};

		const {container} = render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testFileAdditionalProps}
					items={[DOCUMENT_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		expect(container).toBeInTheDocument();

		expect(
			screen.getByRole('heading', {
				name: DOCUMENT_OBJECT_ENTRY.embedded.title,
			})
		).toBeInTheDocument();

		expect(container.querySelector('use')?.getAttribute('href')).toContain(
			'document-image'
		);

		const documentImagePreview = screen.getByRole('img');

		expect(documentImagePreview).toBeInTheDocument();
		expect(documentImagePreview).toHaveAttribute(
			'src',
			DOCUMENT_OBJECT_ENTRY.embedded.file.thumbnailURL
		);

		expect(screen.getAllByRole('tab')).toHaveLength(5);

		expect(screen.getByRole('tab', {name: 'details'})).toBeInTheDocument();
		expect(
			screen.getByRole('tab', {name: 'categorization'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('tab', {name: 'performance'})
		).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: 'versions'})).toBeInTheDocument();
		expect(screen.getByRole('tab', {name: 'comments'})).toBeInTheDocument();

		expect(screen.getByText('metadata')).toBeInTheDocument();

		expect(screen.queryByText('url')).toBeInTheDocument();

		expect(screen.getByText('jpeg')).toBeInTheDocument();

		expect(screen.getByText('1.2 MB')).toBeInTheDocument();

		expect(screen.getByText('1920x1080')).toBeInTheDocument();

		expect(screen.getByText('tall')).toBeInTheDocument();

		expect(
			screen.getByPlaceholderText(
				DOCUMENT_OBJECT_ENTRY.embedded.file.link.href
			)
		).toBeInTheDocument();

		expect(screen.getByText('location')).toBeInTheDocument();

		expect(screen.getByText('author')).toBeInTheDocument();

		expect(
			screen.getByText(DOCUMENT_OBJECT_ENTRY.embedded.creator.name)
		).toBeInTheDocument();

		expect(screen.getByText('created')).toBeInTheDocument();

		expect(screen.getByText('modified')).toBeInTheDocument();

		expect(screen.getByText('never-expire')).toBeInTheDocument();

		expect(screen.getByText('never-review')).toBeInTheDocument();

		const breadcrumb = screen.getByLabelText('Breadcrumb');

		expect(
			await within(breadcrumb).findByRole('button', {
				name: testSpace.name,
			})
		).toBeInTheDocument();

		expect(
			within(breadcrumb).getByRole('button', {name: 'files'})
		).toBeInTheDocument();
	});

	it('renders the component for a Folder asset type', async () => {
		const {container} = render(
			<SidePanel containerRef={{current: null}}>
				<AssetTypeInfoPanelContent
					additionalProps={testAdditionalProps}
					items={[FOLDER_OBJECT_ENTRY] as any}
				/>
			</SidePanel>
		);

		expect(container).toBeInTheDocument();

		expect(
			screen.getByRole('heading', {
				name: FOLDER_OBJECT_ENTRY.embedded.title,
			})
		).toBeInTheDocument();

		expect(container.querySelector('use')?.getAttribute('href')).toContain(
			'folder'
		);

		expect(screen.queryByRole('img')).not.toBeInTheDocument();

		expect(screen.getAllByRole('tab')).toHaveLength(1);

		expect(screen.getByRole('tab', {name: 'details'})).toBeInTheDocument();

		expect(screen.getByText('metadata')).toBeInTheDocument();

		expect(screen.queryByText('url')).not.toBeInTheDocument();

		const numberOfAssets =
			FOLDER_OBJECT_ENTRY.embedded.numberOfObjectEntries +
			FOLDER_OBJECT_ENTRY.embedded.numberOfObjectEntryFolders;

		const numberOfAssetsElement = screen.getByTestId('number-of-assets');

		expect(
			await within(numberOfAssetsElement).findByText(numberOfAssets)
		).toBeInTheDocument();

		expect(screen.getByText('location')).toBeInTheDocument();

		expect(screen.getByText('author')).toBeInTheDocument();

		expect(
			screen.getByText(FOLDER_OBJECT_ENTRY.embedded.creator.name)
		).toBeInTheDocument();

		expect(screen.getByText('created')).toBeInTheDocument();

		expect(screen.getByText('modified')).toBeInTheDocument();

		const breadcrumb = screen.getByLabelText('Breadcrumb');

		expect(
			await within(breadcrumb).findByRole('button', {
				name: testSpace.name,
			})
		).toBeInTheDocument();

		expect(
			within(breadcrumb).getByRole('button', {name: 'content'})
		).toBeInTheDocument();

		expect(
			within(breadcrumb).getByRole('button', {name: 'content-folder'})
		).toBeInTheDocument();
	});

	describe('user time zone', () => {
		const originalGetTimeZone = global.Liferay.ThemeDisplay.getTimeZone;

		beforeEach(() => {
			global.Liferay.ThemeDisplay.getTimeZone = jest
				.fn()
				.mockReturnValue('America/Los_Angeles');
		});

		afterEach(() => {
			global.Liferay.ThemeDisplay.getTimeZone = originalGetTimeZone;
		});

		it('shows the metadata dates in the user time zone', () => {
			render(
				<SidePanel containerRef={{current: null}}>
					<AssetTypeInfoPanelContent
						additionalProps={testAdditionalProps}
						items={
							[
								{
									...CONTENT_OBJECT_ENTRY,
									embedded: {
										...CONTENT_OBJECT_ENTRY.embedded,
										expirationDate: '2026-03-01T02:30:00Z',
										reviewDate: '2026-03-02T01:15:00Z',
									},
								},
							] as any
						}
					/>
				</SidePanel>
			);

			expect(
				screen.getByText('02/28/2026, 06:30 PM')
			).toBeInTheDocument();
			expect(
				screen.getByText('03/01/2026, 05:15 PM')
			).toBeInTheDocument();
		});
	});
});
