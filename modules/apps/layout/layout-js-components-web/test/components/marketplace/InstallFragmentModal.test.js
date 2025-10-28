/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import InstallFragmentModal from '../../../src/main/resources/META-INF/resources/js/components/marketplace/InstallFragmentModal';

const renderComponent = ({name} = {}) =>
	render(<InstallFragmentModal name={name} />);

describe('InstallFragmentModal', () => {
	it('renders correctly', async () => {
		renderComponent();

		await waitFor(() => {
			expect(
				document.querySelector('.loading-animation-squares')
			).toBeInTheDocument();

			expect(
				screen.getByText(
					/the-installation-process-is-ongoing-and-may-take-some-time/
				)
			).toBeInTheDocument();

			expect(
				screen.getByText(
					/closing-the-window-will-not-cancel-the-process/
				)
			).toBeInTheDocument();
		});
	});
});
