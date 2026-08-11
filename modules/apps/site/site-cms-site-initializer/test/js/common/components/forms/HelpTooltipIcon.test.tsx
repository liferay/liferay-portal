/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import HelpTooltipIcon from '../../../../../src/main/resources/META-INF/resources/js/common/components/forms/HelpTooltipIcon';

const MESSAGE = 'This value is appended to the friendly URL';

describe('HelpTooltipIcon', () => {
	it('exposes the message as the accessible name', () => {
		render(<HelpTooltipIcon message={MESSAGE} />);

		expect(screen.getByRole('img', {name: MESSAGE})).toBeInTheDocument();
	});

	it('exposes the message to the tooltip', () => {
		render(<HelpTooltipIcon message={MESSAGE} />);

		expect(screen.getByRole('img', {name: MESSAGE})).toHaveAttribute(
			'data-title',
			MESSAGE
		);
	});

	it('is reachable with the keyboard', async () => {
		render(<HelpTooltipIcon message={MESSAGE} />);

		await userEvent.tab();

		expect(screen.getByRole('img', {name: MESSAGE})).toHaveFocus();
	});

	it('keeps the focus where it was when it is clicked', async () => {
		render(
			<>
				<button>Previous</button>

				<HelpTooltipIcon message={MESSAGE} />
			</>
		);

		const button = screen.getByRole('button', {name: 'Previous'});

		button.focus();

		await userEvent.click(screen.getByRole('img', {name: MESSAGE}));

		expect(button).toHaveFocus();
	});

	it('leaves the graphic presentational', () => {
		const {container} = render(<HelpTooltipIcon message={MESSAGE} />);

		const svg = container.querySelector('svg');

		expect(svg).not.toHaveAttribute('aria-label');
		expect(svg).not.toHaveAttribute('tabindex');
	});

	it('replaces the default spacing with the given class', () => {
		render(<HelpTooltipIcon className="ml-2" message={MESSAGE} />);

		const icon = screen.getByRole('img', {name: MESSAGE});

		expect(icon).toHaveClass('ml-2');
		expect(icon).not.toHaveClass('ml-1');
	});

	it('has no accessibility violations', async () => {
		const {container} = render(<HelpTooltipIcon message={MESSAGE} />);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
