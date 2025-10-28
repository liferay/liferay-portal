/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import isRecycleBinRootPage from '../../../../src/main/resources/META-INF/resources/js/common/utils/isRecycleBinRootPage';
import RecycleBinToolbar from '../../../../src/main/resources/META-INF/resources/js/main_view/recycle_bin/RecycleBinToolbar';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/recycle_bin/RecycleBinToolbar',
	() => {
		return jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/main_view/recycle_bin/RecycleBinToolbar'
		);
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/isRecycleBinRootPage',
	() => {
		return {
			__esModule: true,
			default: jest.fn(),
		};
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/Breadcrumb',
	() => {
		return {
			__esModule: true,
			default: ({breadcrumbItems}: {breadcrumbItems: any[]}) => (
				<nav>
					{breadcrumbItems.map((item, i) => (
						<a href={item.href} key={i}>
							{item.label}
						</a>
					))}
				</nav>
			),
		};
	}
);

describe('RecycleBinToolbar', () => {
	const breadcrumbItems = [
		{href: '/', label: 'Home'},
		{href: '/child', label: 'Child'},
	];

	it('renders header text when isRecycleBinRootPage returns true', () => {
		(isRecycleBinRootPage as jest.Mock).mockReturnValue(true);

		render(<RecycleBinToolbar breadcrumbItems={breadcrumbItems} />);

		expect(screen.getAllByText('recycle-bin')).toHaveLength(1);

		expect(screen.queryByText('Home')).not.toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'more-actions'})
		).toBeInTheDocument();
	});

	it('renders breadcrumb links when isRecycleBinRootPage returns false', () => {
		(isRecycleBinRootPage as jest.Mock).mockReturnValue(false);

		render(<RecycleBinToolbar breadcrumbItems={breadcrumbItems} />);

		expect(screen.getByRole('link', {name: 'Home'})).toHaveAttribute(
			'href',
			'/'
		);
		expect(screen.getByRole('link', {name: 'Child'})).toHaveAttribute(
			'href',
			'/child'
		);

		expect(
			screen.queryByRole('button', {name: 'more-actions'})
		).not.toBeInTheDocument();
	});
});
