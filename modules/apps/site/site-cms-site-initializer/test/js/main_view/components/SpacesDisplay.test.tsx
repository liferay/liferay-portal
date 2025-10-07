/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SpacesDisplay from '../../../../src/main/resources/META-INF/resources/js/common/components/SpacesDisplay';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('{0}', arg),
}));

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === 'available-in-spaces-x') {
		return 'Available in spaces: {0}';
	}

	return key;
});

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
};

const spaces = [
	{
		name: 'First space',
		settings: {
			logoColor: 'outline-1',
		},
	},
	{
		name: 'Second space',
		settings: {
			logoColor: 'outline-1',
		},
	},
	{
		name: 'Third space',
		settings: {
			logoColor: 'outline-1',
		},
	},
] as Space[];

const allSpaces = [
	{
		id: -1,
	},
] as Space[];

describe('SpacesDisplay', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders all spaces when no spaces are provided', () => {
		render(<SpacesDisplay spaces={[]} />);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	it('renders all spaces when a space with id -1 is provided', () => {
		render(<SpacesDisplay spaces={allSpaces} />);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	describe('When one space is provided', () => {
		it('renders the first letter and the name for the single space', () => {
			render(<SpacesDisplay spaces={[spaces[0]]} />);

			expect(screen.getByText(spaces[0].name)).toBeInTheDocument();

			expect(
				screen.getByText(spaces[0].name.charAt(0).toUpperCase())
			).toBeInTheDocument();

			expect(screen.queryByText('+')).not.toBeInTheDocument();
		});
	});

	describe('When multiple spaces are provided', () => {
		it('renders the first letter and the name for the first space', () => {
			render(<SpacesDisplay spaces={spaces} />);

			expect(screen.getByText(spaces[0].name)).toBeInTheDocument();
			expect(
				screen.getByText(spaces[0].name.charAt(0).toUpperCase())
			).toBeInTheDocument();
		});

		it('renders a badge with the count of additional spaces', () => {
			render(<SpacesDisplay spaces={spaces} />);

			const additionalSpacesCount = spaces.length - 1;
			expect(
				screen.getByText(`+${additionalSpacesCount}`)
			).toBeInTheDocument();
		});

		it('shows a tooltip with the names of additional spaces on badge hover', () => {
			render(<SpacesDisplay spaces={spaces} />);

			const additionalSpacesCount = spaces.length - 1;
			const spaceNames = `${spaces[0].name}, ${spaces[1].name}, ${spaces[2].name}`;

			const badge = screen.getByText(`+${additionalSpacesCount}`);
			expect(badge.parentElement).toHaveAttribute(
				'title',
				`Available in spaces: ${spaceNames}`
			);
		});
	});
});
