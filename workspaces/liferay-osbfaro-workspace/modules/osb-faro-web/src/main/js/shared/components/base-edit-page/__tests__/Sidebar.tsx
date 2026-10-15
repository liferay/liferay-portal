import Body from '../Body';
import React from 'react';
import Sidebar from '../Sidebar';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

const LABEL = 'Conditions Library';

const setScreenWidth = (width: number) => {
	Object.defineProperty(document.body, 'clientWidth', {
		configurable: true,
		value: width,
	});
};

describe('BaseEditPage.Sidebar', () => {
	describe('from the medium breakpoint up', () => {
		beforeEach(() => setScreenWidth(768));

		it('renders the children inside a complementary landmark', () => {
			render(
				<Sidebar label={LABEL}>
					<p>{'Conditions'}</p>
				</Sidebar>
			);

			expect(screen.getByRole('complementary')).toHaveTextContent(
				'Conditions'
			);
			expect(
				screen.queryByRole('button', {name: `Open ${LABEL}`})
			).not.toBeInTheDocument();
		});

		it('sits on the left by default', () => {
			render(<Sidebar label={LABEL} />);

			const sidebar = screen.getByRole('complementary');

			expect(sidebar).toHaveClass('base-edit-page-sidebar-left');
			expect(sidebar).toHaveClass('border-right');
		});

		it('sits on the right when asked to', () => {
			render(<Sidebar label={LABEL} position="right" />);

			const sidebar = screen.getByRole('complementary');

			expect(sidebar).toHaveClass('base-edit-page-sidebar-right');
			expect(sidebar).toHaveClass('border-left');
		});

		it('keeps the given class name', () => {
			render(<Sidebar className="criteria-sidebar" label={LABEL} />);

			expect(screen.getByRole('complementary')).toHaveClass(
				'base-edit-page-sidebar',
				'criteria-sidebar'
			);
		});
	});

	describe('below the medium breakpoint', () => {
		beforeEach(() => setScreenWidth(767));

		const renderOverlay = () =>
			render(
				<Sidebar label={LABEL}>
					<button type="button">{'Add Condition'}</button>
				</Sidebar>
			);

		it('hides the sidebar behind a trigger', () => {
			renderOverlay();

			const trigger = screen.getByRole('button', {name: `Open ${LABEL}`});

			expect(trigger).toHaveAttribute('aria-expanded', 'false');
			expect(trigger).toHaveAttribute('title', `Open ${LABEL}`);
			expect(trigger).toHaveAttribute('data-tooltip-align', 'bottom');
			expect(trigger).not.toHaveTextContent(`Open ${LABEL}`);
			expect(screen.queryByRole('complementary')).not.toBeInTheDocument();
		});

		it('opens the sidebar as a labelled side panel', async () => {
			renderOverlay();

			fireEvent.click(
				screen.getByRole('button', {name: `Open ${LABEL}`})
			);

			const panel = await screen.findByLabelText(LABEL, {
				selector: '.sidebar',
			});

			expect(panel).toContainElement(
				screen.getByRole('button', {name: 'Add Condition'})
			);
			expect(screen.getByTitle(`Open ${LABEL}`)).toHaveAttribute(
				'aria-expanded',
				'true'
			);
		});

		it('opens the side panel as wide as the inline sidebar', async () => {
			renderOverlay();

			fireEvent.click(
				screen.getByRole('button', {name: `Open ${LABEL}`})
			);

			const panel = await screen.findByLabelText(LABEL, {
				selector: '.sidebar',
			});

			expect(panel).toHaveStyle({width: '320px'});
		});

		it('closes from its close button and returns focus to the trigger', async () => {
			renderOverlay();

			const trigger = screen.getByRole('button', {name: `Open ${LABEL}`});

			trigger.focus();

			fireEvent.click(trigger);

			fireEvent.click(await screen.findByRole('button', {name: 'Close'}));

			await waitFor(() => expect(trigger).toHaveFocus());
			expect(trigger).toHaveAttribute('aria-expanded', 'false');
		});

		it('closes with the Escape key', async () => {
			renderOverlay();

			const trigger = screen.getByRole('button', {name: `Open ${LABEL}`});

			fireEvent.click(trigger);

			await screen.findByRole('button', {name: 'Close'});

			fireEvent.keyDown(document.activeElement ?? document.body, {
				key: 'Escape',
			});

			await waitFor(() =>
				expect(trigger).toHaveAttribute('aria-expanded', 'false')
			);
		});

		it('closes when the backdrop is clicked', async () => {
			renderOverlay();

			const trigger = screen.getByRole('button', {name: `Open ${LABEL}`});

			fireEvent.click(trigger);

			fireEvent.click(await screen.findByTestId('side-panel-backdrop'));

			await waitFor(() =>
				expect(trigger).toHaveAttribute('aria-expanded', 'false')
			);
			expect(
				screen.queryByTestId('side-panel-backdrop')
			).not.toBeInTheDocument();
		});
	});
});

describe('BaseEditPage.Body', () => {
	beforeEach(() => setScreenWidth(1440));

	it('lays the sidebar out beside the main content', () => {
		const {container} = render(
			<Body className="form-body">
				<Sidebar label={LABEL}>{'Conditions'}</Sidebar>

				<main>{'Editor'}</main>
			</Body>
		);

		const body = container.firstElementChild;

		expect(body).toHaveClass('base-edit-page-body', 'form-body');
		expect(body).toContainElement(screen.getByRole('complementary'));
		expect(body).toContainElement(screen.getByRole('main'));
	});
});
