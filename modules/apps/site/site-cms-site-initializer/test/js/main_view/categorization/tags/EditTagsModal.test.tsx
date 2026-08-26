/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ApiHelper from '../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import ProjectLinkService from '../../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';
import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import EditTagsModalContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/tags/EditTagsModal';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper'
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService'
);

const defaultProps = {
	assetLibraries: [],
	closeModal: jest.fn(),
	editTagURL: '',
	loadData: jest.fn(),
	projects: [],
	tagId: 1,
	tagName: 'tag',
};

describe('EditTagsModal', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-99403'] = true;

		jest.spyOn(SpaceService, 'getSpaces').mockResolvedValue([] as any);
		jest.spyOn(ApiHelper, 'getAll').mockResolvedValue([]);
		jest.spyOn(
			ProjectLinkService,
			'getNonDraftProjectScopeIds'
		).mockResolvedValue({data: new Set<number>(), error: null});

		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('does not render the project scope selector when CMP is disabled', async () => {
		render(<EditTagsModalContent {...defaultProps} cmpEnabled={false} />);

		await waitFor(() => {
			expect(SpaceService.getSpaces).toHaveBeenCalled();
		});

		expect(screen.getByLabelText('space-selector')).toBeInTheDocument();

		expect(
			screen.queryByLabelText('project-selector')
		).not.toBeInTheDocument();

		expect(ApiHelper.getAll).not.toHaveBeenCalled();
	});

	it('renders the project scope selector when CMP is enabled', async () => {
		render(<EditTagsModalContent {...defaultProps} cmpEnabled />);

		await waitFor(() => {
			expect(ApiHelper.getAll).toHaveBeenCalled();
		});

		expect(screen.getByLabelText('project-selector')).toBeInTheDocument();
	});

	it('sends an empty project scope when the tag is available in all projects', async () => {
		jest.spyOn(ApiHelper, 'put').mockResolvedValue({
			data: {},
			error: null,
			status: 'OK',
		} as any);

		render(
			<EditTagsModalContent
				{...defaultProps}
				assetLibraries={[{id: -1}] as any}
				cmpEnabled
				projects={[{id: -1}] as any}
			/>
		);

		await waitFor(() => {
			expect(ApiHelper.getAll).toHaveBeenCalled();
		});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(ApiHelper.put).toHaveBeenCalled();
		});

		const [, body] = (ApiHelper.put as jest.Mock).mock.calls[0];

		expect(body.assetLibraries).toEqual([]);
		expect(body.projects).toEqual([]);
	});
});
