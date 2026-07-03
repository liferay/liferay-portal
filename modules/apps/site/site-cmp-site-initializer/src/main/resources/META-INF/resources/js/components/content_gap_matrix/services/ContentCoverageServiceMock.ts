/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MatrixData} from '../types';
import {ContentCoverageService} from './ContentCoverageService';
import {
	EMPTY_MATRIX,
	FULL_COVERAGE_MATRIX,
	PARTIAL_COVERAGE_MATRIX,
	UNCATEGORIZED_MATRIX,
} from './fixtures';

/**
 * Sample scenarios for the content-coverage matrix, kept for tests and local
 * development against the headless-cmp endpoint (LPD-96935). A project id selects
 * a scenario; any real (numeric) id falls back to the sample partial-coverage
 * matrix.
 */
const SCENARIOS: Record<string, MatrixData> = {
	empty: EMPTY_MATRIX,
	full: FULL_COVERAGE_MATRIX,
	partial: PARTIAL_COVERAGE_MATRIX,
	uncategorized: UNCATEGORIZED_MATRIX,
};

export const ContentCoverageServiceMock: ContentCoverageService = {
	getMatrix(projectId: string): Promise<MatrixData> {
		return Promise.resolve(SCENARIOS[projectId] ?? PARTIAL_COVERAGE_MATRIX);
	},
};
