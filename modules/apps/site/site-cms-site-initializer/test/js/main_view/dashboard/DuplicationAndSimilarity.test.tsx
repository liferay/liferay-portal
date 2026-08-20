/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import React from 'react';

import {GovernanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import {DuplicationAndSimilarity} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/DuplicationAndSimilarity';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
);

const mockedGovernanceService = GovernanceService as jest.Mocked<
	typeof GovernanceService
>;

const CONSTANTS = {
	ercContentStructures: 'ERC_CONTENT_STRUCTURES',
	ercFileTypes: 'ERC_FILE_TYPES',
};

function renderComponent() {
	return render(
		<GovernanceContextProvider>
			<DuplicationAndSimilarity constants={CONSTANTS} />
		</GovernanceContextProvider>
	);
}

describe('[CMS Dashboard] DuplicationAndSimilarity', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedGovernanceService.getCMSEntryClassNames.mockResolvedValue(
			'com.liferay.object.ObjectDefinition#A'
		);

		mockedGovernanceService.getDuplicateTopicsCount.mockResolvedValue(9);

		mockedGovernanceService.getAssetStatistics.mockResolvedValue({
			data: {},
			error: null,
		} as any);
	});

	it('shows a card for each duplication metric', () => {
		renderComponent();

		const section = screen.getByRole('group', {
			name: 'duplication-and-similarity',
		});

		expect(within(section).getAllByRole('button')).toHaveLength(4);

		for (const name of [
			'duplicate-topics',
			'text-similarity',
			'same-metadata',
			'similar-links',
		]) {
			expect(
				within(section).getByRole('button', {name: new RegExp(name)})
			).toBeInTheDocument();
		}
	});

	it('shows a count only for the metric that has data', async () => {
		renderComponent();

		expect(
			await within(
				screen.getByRole('button', {name: /duplicate-topics/})
			).findByText('9')
		).toBeInTheDocument();

		for (const name of [
			'text-similarity',
			'same-metadata',
			'similar-links',
		]) {
			expect(
				within(
					screen.getByRole('button', {name: new RegExp(name)})
				).getByText('—')
			).toBeInTheDocument();
		}
	});

	it('warns that the cards open a dialog', () => {
		renderComponent();

		const section = screen.getByRole('group', {
			name: 'duplication-and-similarity',
		});

		for (const card of within(section).getAllByRole('button')) {
			expect(card).toHaveAttribute('aria-haspopup', 'dialog');
		}
	});

	it('requests all spaces on the initial render', async () => {
		renderComponent();

		await waitFor(() =>
			expect(
				mockedGovernanceService.getCMSEntryClassNames
			).toHaveBeenCalledWith(
				CONSTANTS.ercContentStructures,
				CONSTANTS.ercFileTypes,
				expect.anything()
			)
		);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getDuplicateTopicsCount
			).toHaveBeenCalledWith(
				expect.objectContaining({
					entryClassNames: 'com.liferay.object.ObjectDefinition#A',
					siteId: undefined,
				})
			)
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await waitFor(() => expect(screen.getByText('9')).toBeInTheDocument());

		await checkAccessibility({bestPractices: true, context: container});
	});
});
