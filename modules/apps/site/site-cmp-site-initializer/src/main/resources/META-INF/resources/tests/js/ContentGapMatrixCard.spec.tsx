/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ContentGapMatrixCard from '../../js/components/content_gap_matrix/ContentGapMatrixCard';

describe('ContentGapMatrixCard', () => {
	it('hides the edit project button on the unconfigured state when no edit project URL is provided', () => {
		render(
			<ContentGapMatrixCard
				assetFDSId="fdsId"
				cmpProjectObjectEntryId="123"
				cmpProjectObjectEntryTitle="Project"
				hasFunnelStagesOrPersonas={false}
			/>
		);

		expect(screen.queryByText('edit-project')).not.toBeInTheDocument();
	});

	it('sizes the unconfigured state to its content so the asset list stays above the fold', () => {
		render(
			<ContentGapMatrixCard
				assetFDSId="fdsId"
				cmpProjectObjectEntryId="123"
				cmpProjectObjectEntryTitle="Project"
				hasFunnelStagesOrPersonas={false}
			/>
		);

		expect(
			screen
				.getByText('no-personas-or-funnel-stages-configured')
				.closest('.c-empty-state')
		).toHaveClass('c-empty-state-sm');
	});

	it('shows the edit project button on the unconfigured state when an edit project URL is provided', () => {
		render(
			<ContentGapMatrixCard
				assetFDSId="fdsId"
				cmpProjectObjectEntryId="123"
				cmpProjectObjectEntryTitle="Project"
				editProjectURL="/edit-project"
				hasFunnelStagesOrPersonas={false}
			/>
		);

		expect(screen.getByText('edit-project')).toBeInTheDocument();
	});
});
