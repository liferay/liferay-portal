/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {PerformanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceContext';
import {TopAssets} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/TopAssets';

const mockFrontendDataSet = jest.fn();

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as any),
	FrontendDataSet: (props: any) => mockFrontendDataSet(props),
}));

describe('TopAssets', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('requests the file preview fields so the View modal can render file previews', () => {
		render(
			<PerformanceContextProvider additionalProps={{} as any}>
				<TopAssets />
			</PerformanceContextProvider>
		);

		const {additionalAPIURLParameters} =
			mockFrontendDataSet.mock.calls[0][0];

		expect(additionalAPIURLParameters).toContain('file.metadata');
		expect(additionalAPIURLParameters).toContain('file.previewURL');
		expect(additionalAPIURLParameters).toContain('file.thumbnailURL');
	});
});
