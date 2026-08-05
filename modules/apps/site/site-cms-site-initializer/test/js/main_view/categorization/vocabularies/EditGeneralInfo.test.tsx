/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import EditGeneralInfo from '../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/vocabularies/EditGeneralInfo';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper'
);

const defaultProps = {
	assetLibraries: [],
	defaultLanguageId: 'en_US',
	externalReferenceCodeInputError: '',
	externalReferenceCodeMaxLength: 255,
	isNew: true,
	locales: [
		{
			id: 'en_US',
			label: 'en-US',
			name: 'English (United States)',
			symbol: 'us',
		},
	],
	nameInputError: '',
	onChangeVocabulary: jest.fn(),
	projects: [],
	setExternalReferenceCodeInputError: jest.fn(),
	setNameInputError: jest.fn(),
	setProjectChange: jest.fn(),
	setProjectInputError: jest.fn(),
	setSpaceChange: jest.fn(),
	setSpaceInputError: jest.fn(),
	setVocabularyPermissions: jest.fn(),
	showPermissions: false,
	spritemap: '',
	vocabulary: {
		assetLibraries: [],
		assetTypes: [],
		multiValued: true,
		name: '',
		name_i18n: {'en-US': ''},
		projects: [],
		visibilityType: 'PUBLIC' as const,
	},
};

describe('EditGeneralInfo', () => {
	beforeEach(() => {
		jest.spyOn(SpaceService, 'getSpaces').mockResolvedValue([] as any);
		jest.spyOn(ApiHelper, 'getAll').mockResolvedValue([]);

		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-58677'] = false;

		jest.clearAllMocks();
	});

	it('does not render the project scope selector when the CMP feature flag is disabled', async () => {
		render(<EditGeneralInfo {...defaultProps} />);

		await waitFor(() => {
			expect(SpaceService.getSpaces).toHaveBeenCalled();
		});

		expect(screen.getByLabelText('space-selector')).toBeInTheDocument();

		expect(
			screen.queryByLabelText('project-selector')
		).not.toBeInTheDocument();

		expect(ApiHelper.getAll).not.toHaveBeenCalled();
	});

	it('renders the project scope selector when the CMP feature flag is enabled', async () => {
		Liferay.FeatureFlags['LPD-58677'] = true;

		render(<EditGeneralInfo {...defaultProps} />);

		await waitFor(() => {
			expect(ApiHelper.getAll).toHaveBeenCalled();
		});

		expect(screen.getByLabelText('project-selector')).toBeInTheDocument();
	});
});
