/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CategorizationPermissionService from '../../../../../src/main/resources/META-INF/resources/js/common/services/CategorizationPermissionService';
import VocabularyService from '../../../../../src/main/resources/META-INF/resources/js/common/services/VocabularyService';
import EditVocabulary from '../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/vocabularies/EditVocabulary';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/VocabularyService'
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/CategorizationPermissionService'
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/vocabularies/EditGeneralInfo',
	() =>
		({onChangeVocabulary, setProjectChange}: any) => {
			const React = require('react');

			return (
				<div>
					<button
						onClick={() =>
							onChangeVocabulary((vocabulary: any) => ({
								...vocabulary,
								name: 'Fruits',
							}))
						}
					>
						stub-set-name
					</button>

					<button onClick={() => setProjectChange(true)}>
						stub-change-project
					</button>
				</div>
			);
		}
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/vocabularies/EditAssociatedAssetTypes',
	() => () => null
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/vocabularies/ConfirmChangesModal',
	() =>
		({open}: any) => {
			const React = require('react');

			return open ? <div data-testid="confirm-changes-modal" /> : null;
		}
);

const defaultProps = {
	availableAssetTypes: [],
	backURL: '/back',
	cmsGroupId: 123,
	defaultLanguageId: 'en_US',
	externalReferenceCodeMaxLength: 75,
	locales: [],
	spritemap: '/sprite.svg',
	vocabularyPermissionsAPIURL: '/permissions/{taxonomyVocabularyId}',
};

describe('EditVocabulary', () => {
	beforeEach(() => {
		(global as any).Liferay = {
			...(global as any).Liferay,
			Util: {
				...(global as any).Liferay?.Util,
				openToast: jest.fn(),
				sub: (str: string) => str,
			},
		};

		(VocabularyService.createVocabulary as jest.Mock).mockResolvedValue({
			data: {id: 1},
			error: null,
			status: null,
		});
		(VocabularyService.updateVocabulary as jest.Mock).mockResolvedValue({
			error: null,
			status: null,
		});
		(
			CategorizationPermissionService.putPermissions as jest.Mock
		).mockResolvedValue({error: null});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('does not warn about project removal when creating a vocabulary', async () => {
		const user = userEvent.setup();

		render(<EditVocabulary {...defaultProps} vocabularyId={0} />);

		await user.click(screen.getByText('stub-set-name'));
		await user.click(screen.getByText('stub-change-project'));

		await user.click(screen.getByRole('button', {name: 'save'}));

		expect(
			screen.queryByTestId('confirm-changes-modal')
		).not.toBeInTheDocument();

		await waitFor(() => {
			expect(VocabularyService.createVocabulary).toHaveBeenCalledTimes(1);
		});
	});

	it('warns about project removal when editing a vocabulary', async () => {
		(VocabularyService.fetchVocabulary as jest.Mock).mockResolvedValue({
			data: {
				assetLibraries: [],
				assetTypes: [],
				id: 5,
				name: 'Fruits',
				projects: [],
			},
			error: null,
		});

		const user = userEvent.setup();

		render(<EditVocabulary {...defaultProps} vocabularyId={5} />);

		await waitFor(() => {
			expect(VocabularyService.fetchVocabulary).toHaveBeenCalled();
		});

		await user.click(screen.getByText('stub-change-project'));

		await user.click(screen.getByRole('button', {name: 'save'}));

		expect(screen.getByTestId('confirm-changes-modal')).toBeInTheDocument();

		expect(VocabularyService.updateVocabulary).not.toHaveBeenCalled();
	});
});
