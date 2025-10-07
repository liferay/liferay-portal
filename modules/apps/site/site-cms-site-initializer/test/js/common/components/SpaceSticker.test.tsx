/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SpaceSticker from '../../../../src/main/resources/META-INF/resources/js/common/components/SpaceSticker';

const spaceTitle = 'First space';

describe('SpaceSticker', () => {
	it('renders the first letter of the name in uppercase', () => {
		render(<SpaceSticker name={spaceTitle} />);

		expect(
			screen.getByText(spaceTitle.charAt(0).toUpperCase())
		).toBeInTheDocument();
	});

	it('renders the full name next to the sticker', () => {
		render(<SpaceSticker name={spaceTitle} />);

		expect(screen.getByText(spaceTitle)).toBeInTheDocument();
	});

	it('render component without name if "hideName" is true', () => {
		render(<SpaceSticker hideName name={spaceTitle} />);

		expect(screen.queryByText(spaceTitle)).not.toBeInTheDocument();
	});

	it('applies the provided style to the ClaySticker', () => {
		const {container} = render(
			<SpaceSticker displayType="outline-3" name={spaceTitle} />
		);

		expect(container.getElementsByClassName('sticker')[0]).toHaveClass(
			'sticker-outline-3'
		);
	});

	it('applies the provided size prop to the ClaySticker', () => {
		const {container} = render(
			<SpaceSticker name={spaceTitle} size="sm" />
		);

		expect(container.getElementsByClassName('sticker')[0]).toHaveClass(
			'sticker-sm'
		);
	});
});
