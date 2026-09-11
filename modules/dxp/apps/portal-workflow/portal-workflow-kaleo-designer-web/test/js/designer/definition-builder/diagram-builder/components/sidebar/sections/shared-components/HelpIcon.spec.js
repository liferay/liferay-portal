/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import HelpIcon from '../../../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/sections/shared-components/HelpIcon';

describe('The HelpIcon component should', () => {
	it('Be reachable with the keyboard', async () => {
		const user = userEvent.setup();

		render(<HelpIcon message="label-name" />);

		await user.tab();

		expect(screen.getByRole('img', {name: 'label-name'})).toHaveFocus();
	});

	it('Expose its message to assistive technology and to the portal tooltip', () => {
		render(<HelpIcon className="ml-2" message="label-name" />);

		const helpIcon = screen.getByRole('img', {name: 'label-name'});

		expect(helpIcon).toHaveAttribute('title', 'label-name');
		expect(helpIcon).toHaveClass('lfr-portal-tooltip', 'ml-2');
	});
});
