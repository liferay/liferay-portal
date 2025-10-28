/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import NetworkStatusBar from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/NetworkStatusBar';

const renderComponent = async ({error = null, status = null} = {}) =>
	render(<NetworkStatusBar error={error} status={status} />);

describe('NetworkStatusBar', () => {
	it('renders saved status when the status is 0 (draftSaved)', () => {
		renderComponent({status: 0});

		const savedIcon = screen.getByLabelText('saved');

		expect(savedIcon).toBeInTheDocument();
		expect(savedIcon).toHaveAttribute('data-title', 'saved');
	});

	it('renders error status when the status is 1 (error)', () => {
		renderComponent({status: 1});

		expect(screen.getByText('error:')).toBeInTheDocument();
	});

	it('renders loading status when the status is 2 (savingDraft)', () => {
		renderComponent({status: 2});

		expect(screen.getByLabelText('saving')).toBeInTheDocument();
		expect(screen.getByTitle('saving')).toBeInTheDocument();
		expect(screen.getByLabelText('saving')).toHaveClass(
			'loading-animation-sm'
		);
	});
});
