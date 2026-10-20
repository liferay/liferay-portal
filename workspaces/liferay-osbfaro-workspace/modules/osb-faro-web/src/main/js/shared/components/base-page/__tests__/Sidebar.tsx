import BasePage from '..';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {resetBodyWidth, setBodyWidth} from 'test/body-width';

jest.unmock('react-dom');

const PUSH_WIDTH = 1440;

const OVERLAY_WIDTH = 1024;

const Panel = ({
	onOpenChange = jest.fn(),
	open,
	overlay = false,
}: {
	onOpenChange?: (open: boolean) => void;
	open: boolean;
	overlay?: boolean;
}) => {
	setBodyWidth(overlay ? OVERLAY_WIDTH : PUSH_WIDTH);

	return (
		<Provider store={mockStore()}>
			<MemoryRouter>
				<BasePage className="test-page" documentTitle="Test">
					<div data-testid="page-content" />

					<BasePage.Sidebar onOpenChange={onOpenChange} open={open}>
						<button type="button">{'Inside'}</button>
					</BasePage.Sidebar>
				</BasePage>
			</MemoryRouter>
		</Provider>
	);
};

const getPage = () => document.querySelector('.test-page');

describe('BasePage.Sidebar', () => {
	afterEach(() => {
		cleanup();

		resetBodyWidth();
	});

	it('pushes the content without an overlay', () => {
		render(<Panel open />);

		expect(screen.queryByTestId('base-page-sidebar-backdrop')).toBeNull();
		expect(getPage()).toHaveClass('c-slideout-push-end');
		expect(getPage()).not.toHaveClass('base-page-sidebar-overlay');
	});

	it('overlays the content in overlay mode', () => {
		render(<Panel open overlay />);

		expect(
			screen.getByTestId('base-page-sidebar-backdrop')
		).toBeInTheDocument();
		expect(getPage()).toHaveClass('base-page-sidebar-overlay');
	});

	it('sets its width on the page', () => {
		render(<Panel open />);

		expect(
			(getPage() as HTMLElement).style.getPropertyValue(
				'--base-page-sidebar-width'
			)
		).toBe('512px');
	});

	it('shows no overlay while closed', () => {
		render(<Panel open={false} overlay />);

		expect(screen.queryByTestId('base-page-sidebar-backdrop')).toBeNull();
	});

	it('keeps the panel inert while closed', () => {
		render(<Panel open={false} />);

		expect(
			screen
				.getByRole('button', {hidden: true, name: 'Inside'})
				.closest('.sidebar')
		).toHaveAttribute('inert');
	});

	describe('the page scroll', () => {
		const isLocked = () =>
			document.documentElement.style.overflow === 'hidden';

		const setViewportWidth = (width: number) =>
			Object.defineProperty(document.documentElement, 'clientWidth', {
				configurable: true,
				value: width,
			});

		afterEach(() => {
			Reflect.deleteProperty(document.documentElement, 'clientWidth');

			document.documentElement.style.removeProperty('overflow');
			document.body.style.removeProperty('padding-right');
		});

		it('is locked while the overlay is open', () => {
			render(<Panel open overlay />);

			expect(isLocked()).toBe(true);
		});

		it('stays free while the panel pushes the page', () => {
			render(<Panel open />);

			expect(isLocked()).toBe(false);
		});

		it('stays free while the overlay is closed', () => {
			render(<Panel open={false} overlay />);

			expect(isLocked()).toBe(false);
		});

		it('is released when the overlay closes', () => {
			const {rerender} = render(<Panel open overlay />);

			rerender(<Panel open={false} overlay />);

			expect(isLocked()).toBe(false);
		});

		it('is released when the panel stops overlaying the page', () => {
			const {rerender} = render(<Panel open overlay />);

			rerender(<Panel open />);

			fireEvent(window, new Event('resize'));

			expect(isLocked()).toBe(false);
			expect(getPage()).not.toHaveClass('base-page-sidebar-overlay');
		});

		it('is released when the panel unmounts', () => {
			const {unmount} = render(<Panel open overlay />);

			unmount();

			expect(isLocked()).toBe(false);
		});

		it('keeps the page from shifting under the hidden scrollbar', () => {
			setViewportWidth(window.innerWidth - 15);

			const {unmount} = render(<Panel open overlay />);

			expect(document.body.style.paddingRight).toBe('15px');

			unmount();

			expect(document.body.style.paddingRight).toBe('');
		});

		it('adds no padding when the scrollbar cannot be measured', () => {
			setViewportWidth(0);

			render(<Panel open overlay />);

			expect(document.body.style.paddingRight).toBe('');
		});

		it('puts back the padding the page had before', () => {
			setViewportWidth(window.innerWidth - 15);

			document.body.style.paddingRight = '4px';

			const {unmount} = render(<Panel open overlay />);

			unmount();

			expect(document.body.style.paddingRight).toBe('4px');

			document.body.style.removeProperty('padding-right');
		});
	});

	it('closes when the backdrop is clicked', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(screen.getByTestId('base-page-sidebar-backdrop'));

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('closes when a click lands outside the inert page', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(document.body);

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('closes when a click passes through the inert backdrop to the page', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(getPage()!);

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('stays open when the page content is clicked', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(screen.getByTestId('page-content'));

		expect(onOpenChange).not.toHaveBeenCalled();
	});

	it('stays open when the panel itself is clicked', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(
			screen.getByRole('button', {hidden: true, name: 'Inside'})
		);

		expect(onOpenChange).not.toHaveBeenCalled();
	});

	it('ignores outside clicks while it pushes the page', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open />);

		fireEvent.mouseDown(document.body);

		expect(onOpenChange).not.toHaveBeenCalled();
	});

	it('closes on Escape', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open />);

		fireEvent.keyDown(document, {key: 'Escape'});

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});
});
