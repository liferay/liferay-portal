/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CharacterCounter from '../../../src/main/resources/META-INF/resources/js/components/CharacterCounter';

describe('CharacterCounter', () => {
	it('renders nothing while the count is far from the limit', () => {
		const {container} = render(<CharacterCounter count={0} max={5000} />);

		expect(container).toBeEmptyDOMElement();
	});

	it('renders nothing one character before the threshold', () => {
		const {container} = render(
			<CharacterCounter count={4499} max={5000} />
		);

		expect(container).toBeEmptyDOMElement();
	});

	it('renders the count once it reaches the threshold', () => {
		render(<CharacterCounter count={4500} max={5000} />);

		expect(screen.getByText('4,500 / 5,000')).toBeInTheDocument();
	});

	it('renders the count at the limit', () => {
		render(<CharacterCounter count={5000} max={5000} />);

		expect(screen.getByText('5,000 / 5,000')).toBeInTheDocument();
	});

	it('is accessible', async () => {
		const {container} = render(
			<CharacterCounter count={5000} max={5000} />
		);

		await checkAccessibility({context: container});
	});
});
