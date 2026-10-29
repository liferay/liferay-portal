/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FileContainer from '../../js/Attachment/FileContainer';

describe('The FileContainer component', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-100522'] = true;
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-100522'] = false;
	});

	it('shows the file size', () => {
		render(
			<FileContainer
				attachment={{
					contentURL: '/documents/report.pdf',
					fileEntryId: '1',
					size: '94 KB',
					title: 'report.pdf',
				}}
				onDelete={() => {}}
				readOnly={false}
			/>
		);

		expect(screen.getByText('94 KB')).toBeInTheDocument();
	});
});
