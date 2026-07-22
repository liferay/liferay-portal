/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/context';
import ProjectsTabContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/tab_content/ProjectsTabContent';

const mockLinkedProjects = jest.fn();

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/components/LinkedProjects',
	() => ({
		__esModule: true,
		default: (props: unknown) => {
			mockLinkedProjects(props);

			return null;
		},
	})
);

describe('ProjectsTabContent', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('maps the selected asset to the linked projects identity', () => {
		render(
			<AssetTypeInfoPanelContext.Provider
				value={
					{
						asset: {
							externalReferenceCode: 'ASSET-1',
							keywords: ['L_CMP_TASK_X'],
							systemProperties: {
								scope: {externalReferenceCode: 'SPACE-1'},
							},
						},
						cmpProjectAssetRelationshipObjectDefinitionId: 11,
						cmpProjectObjectDefinitionId: 22,
						cmpProjectViewURL: '/project/',
						cmpTaskObjectDefinitionId: 33,
						cmpTaskViewURL: '/task/',
						entryClassName: 'com.example.Content',
					} as unknown as IAssetTypeInfoPanelContext
				}
			>
				<ProjectsTabContent />
			</AssetTypeInfoPanelContext.Provider>
		);

		expect(mockLinkedProjects).toHaveBeenCalledWith({
			assetKeywords: ['L_CMP_TASK_X'],
			cmpProjectAssetRelationshipObjectDefinitionId: 11,
			cmpProjectObjectDefinitionId: 22,
			cmpTaskObjectDefinitionId: 33,
			entryClassName: 'com.example.Content',
			entryExternalReferenceCode: 'ASSET-1',
			entryGroupExternalReferenceCode: 'SPACE-1',
			projectViewURL: '/project/',
			taskViewURL: '/task/',
		});
	});
});
