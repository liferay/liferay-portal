/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import Sidebar from '../../../src/main/resources/META-INF/resources/js/components/Sidebar';

import '@testing-library/jest-dom';

const ControlledSidebar = ({open, title = ''}) => {
	const [isOpen, setIsOpen] = useState(open);

	return (
		<Sidebar onClose={() => setIsOpen(false)} open={isOpen}>
			<Sidebar.Header title={title} />
		</Sidebar>
	);
};

describe('Sidebar', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();
	});

	it('renders an open sidebar', () => {
		const {container} = render(<Sidebar />);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
		expect(document.body).toHaveAttribute('class', 'sidebar-open');
	});

	it('renders a closed sidebar', () => {
		const {container} = render(<Sidebar open={false} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(container).toMatchSnapshot();
		expect(document.body).not.toHaveAttribute('class', 'sidebar-open');
	});

	it('renders a sidebar with a header with title and subtitle', () => {
		const {container, getByText} = render(
			<Sidebar>
				<Sidebar.Header title="Title" />
			</Sidebar>
		);

		expect(container).toMatchSnapshot();

		expect(getByText('Title')).toBeInTheDocument();
	});

	it('renders a sidebar with body with custom content', () => {
		const {container, getByText} = render(
			<Sidebar>
				<Sidebar.Body>
					<div>Custom content text</div>
				</Sidebar.Body>
			</Sidebar>
		);

		const customChildren = getByText('Custom content text');

		expect(container).toMatchSnapshot();
		expect(customChildren).toBeInTheDocument();
		expect(customChildren.parentNode).toHaveAttribute(
			'class',
			'sidebar-body'
		);
	});

	it('renders an open sidebar with header and close it on close button click', () => {
		const {getByRole} = render(<ControlledSidebar open={true} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.body).toHaveAttribute('class', 'sidebar-open');

		const closeIcon = getByRole('presentation');

		expect(closeIcon).toBeInTheDocument();

		fireEvent(
			closeIcon,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		expect(document.body).not.toHaveAttribute('class', 'sidebar-open');
	});
});
