/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ConfigProvider} from '../../../../../src/main/resources/META-INF/resources/js/core/hooks/useConfig.es';
import {PaginationControls} from '../../../../../src/main/resources/META-INF/resources/js/custom/form/components/PaginationControls.es';

const INITIAL_CONFIG = {
	cancelLabel: 'Cancel',
	redirectURL: null,
	showCancelButton: false,
	showPartialResultsToRespondents: true,
	showSubmitButton: true,
	submitLabel: 'Submit',
};

const WithProvider = ({children, config}) => (
	<ConfigProvider initialConfig={config}>{children}</ConfigProvider>
);

describe('Pagination Controls', () => {
	it('shows see partial results button if showPartialResultsToRespondents settings is enabled', () => {
		const {queryByRole} = render(
			<WithProvider config={INITIAL_CONFIG}>
				<PaginationControls />
			</WithProvider>
		);

		expect(
			queryByRole('button', {name: /preview-existing-submissions/i})
		).toBeInTheDocument();
	});

	it('hides see partial results button if showPartialResultsToRespondents settings is disabled', () => {
		const {queryByRole} = render(
			<WithProvider
				config={{
					...INITIAL_CONFIG,
					showPartialResultsToRespondents: false,
				}}
			>
				<PaginationControls />
			</WithProvider>
		);

		expect(
			queryByRole('button', {name: /preview-existing-submissions/i})
		).not.toBeInTheDocument();
	});
});
