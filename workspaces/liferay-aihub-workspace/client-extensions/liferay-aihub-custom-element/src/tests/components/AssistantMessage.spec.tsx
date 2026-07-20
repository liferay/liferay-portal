/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';
import {describe, expect, it} from 'vitest';

import AssistantMessage from '../../components/AssistantMessage';

describe('AssistantMessage', () => {
	it('renders a bare URL in the response as a clickable link', () => {
		render(
			<AssistantMessage
				text="For more details, please visit: https://liferay.com/docs"
				title="AskWA"
			/>
		);

		const link = screen.getByRole('link', {
			name: 'https://liferay.com/docs',
		});

		expect(link).toHaveAttribute('href', 'https://liferay.com/docs');
	});

	it('renders a Markdown link in the response as a clickable link', () => {
		render(
			<AssistantMessage
				text="See the [documentation](https://liferay.com/docs)."
				title="AskWA"
			/>
		);

		expect(
			screen.getByRole('link', {name: 'documentation'})
		).toHaveAttribute('href', 'https://liferay.com/docs');
	});

	it('opens response links safely in a new tab', () => {
		render(
			<AssistantMessage text="https://liferay.com/docs" title="AskWA" />
		);

		const link = screen.getByRole('link');

		expect(link).toHaveAttribute('target', '_blank');
		expect(link).toHaveAttribute('rel', 'noopener noreferrer');
	});
});
