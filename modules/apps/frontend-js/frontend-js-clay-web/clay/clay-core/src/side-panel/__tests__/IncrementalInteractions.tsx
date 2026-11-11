/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import {Provider} from '@clayui/provider';
import {cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useRef, useState} from 'react';

import '@testing-library/jest-dom';

import {SidePanel} from '../SidePanel';

// FocusTrap is mocked in this test because JSDOM does not fully simulate
// browser focus behavior. In a real browser, closing the panel would
// unmount the trap and restore focus to the trigger element. Mocking
// FocusTrap prevents false negatives in tests caused by JSDOM's limitations.

const mockFocusTrap = jest.fn(({children}: any) => <>{children}</>);

jest.mock('../../focus-trap/FocusTrap', () => ({
	FocusTrap: (props: any) => mockFocusTrap(props),
}));

function Example({
	backdrop = false,
	closeOnEscape,
	defaultOpen = false,
	fullHeight = false,
	position,
}: {
	backdrop?: boolean;
	closeOnEscape?: boolean;
	defaultOpen?: boolean;
	fullHeight?: boolean;
	position?: 'absolute' | 'fixed';
}) {
	const [open, setOpen] = useState(defaultOpen);

	const ref = useRef(null);

	return (
		<Provider spritemap="icons.svg">
			<Button
				aria-controls="sidepanel"
				aria-pressed={open}
				onClick={() => setOpen(!open)}
			>
				Open
			</Button>

			<div ref={ref}>
				<SidePanel
					as="aside"
					backdrop={backdrop}
					closeOnEscape={closeOnEscape}
					containerRef={ref}
					fullHeight={fullHeight}
					id="sidepanel"
					onOpenChange={setOpen}
					open={open}
					position={position}
				>
					<SidePanel.Header
						messages={{
							closeAriaLabel: 'Close the Title side panel.',
						}}
					>
						<SidePanel.Title>Title</SidePanel.Title>
					</SidePanel.Header>

					<SidePanel.Body>Body</SidePanel.Body>

					<SidePanel.Footer>Footer</SidePanel.Footer>
				</SidePanel>
			</div>
		</Provider>
	);
}

describe('SidePanel incremental interactions', () => {
	afterEach(cleanup);

	it('when opening move focus to the root element', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});
	});

	it('clicking the close button closes the side panel and moves the focus to the trigger', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button, close] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});

		userEvent.click(close!);

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');
	});

	it('pressing ESC closes and moves focus to the trigger', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});

		userEvent.keyboard('{Escape}');

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');
	});

	it('appends the backdrop to the body and closes when clicking it', async () => {
		const {getAllByRole, getByRole} = render(<Example backdrop />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		expect(document.querySelector('.c-slideout-backdrop')).toBeNull();

		userEvent.click(button!);

		const backdrop = document.querySelector('.c-slideout-backdrop');

		expect(backdrop?.parentElement?.parentElement).toBe(document.body);

		userEvent.click(backdrop!);

		await waitFor(() => {
			expect(document.querySelector('.c-slideout-backdrop')).toBeNull();
			expect(panel).toHaveAttribute('inert');
		});
	});

	it('closes on ESC and traps focus while the backdrop is shown', async () => {
		const {getAllByRole, getByRole} = render(
			<Example backdrop closeOnEscape={false} />
		);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).not.toHaveAttribute('inert');
		});

		expect(mockFocusTrap).toHaveBeenLastCalledWith(
			expect.objectContaining({active: true})
		);
		expect(panel.closest('.c-slideout')).toHaveClass(
			'c-slideout-height-full'
		);

		userEvent.keyboard('{Escape}');

		await waitFor(() => {
			expect(panel).toHaveAttribute('inert');
		});
	});

	it('runs the full height from the top', () => {
		const {container} = render(<Example fullHeight position="fixed" />);

		const slideout = container.querySelector('.c-slideout');

		expect(slideout).toHaveClass('c-slideout-height-full');
		expect(slideout).toHaveStyle({top: '0px'});
	});

	it('runs the full height from the top with a backdrop', () => {
		const {container} = render(<Example backdrop position="fixed" />);

		const slideout = container.querySelector('.c-slideout');

		expect(slideout).toHaveClass('c-slideout-height-full');
		expect(slideout).toHaveStyle({top: '0px'});
	});
});
