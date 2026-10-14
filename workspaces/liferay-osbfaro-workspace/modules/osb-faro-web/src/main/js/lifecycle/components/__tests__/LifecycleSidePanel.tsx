import LifecycleSidePanel from '../LifecycleSidePanel';
import React, {useRef} from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const Panel = ({
	onOpenChange = jest.fn(),
	open,
	overlay = false,
}: {
	onOpenChange?: (open: boolean) => void;
	open: boolean;
	overlay?: boolean;
}) => {
	const containerRef = useRef<HTMLDivElement>(null);

	return (
		<>
			<div data-testid="container" ref={containerRef} />

			<LifecycleSidePanel
				containerRef={containerRef}
				onOpenChange={onOpenChange}
				open={open}
				overlay={overlay}
			>
				<button type="button">{'Inside'}</button>
			</LifecycleSidePanel>
		</>
	);
};

describe('LifecycleSidePanel', () => {
	afterEach(cleanup);

	it('pushes the content without an overlay', () => {
		render(<Panel open />);

		expect(
			screen.queryByTestId('lifecycle-side-panel-backdrop')
		).toBeNull();
		expect(screen.getByTestId('container')).toHaveClass(
			'c-slideout-push-end'
		);
	});

	it('overlays the content in overlay mode', () => {
		render(<Panel open overlay />);

		expect(
			screen.getByTestId('lifecycle-side-panel-backdrop')
		).toBeInTheDocument();
	});

	it('shows no overlay while closed', () => {
		render(<Panel open={false} overlay />);

		expect(
			screen.queryByTestId('lifecycle-side-panel-backdrop')
		).toBeNull();
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

			expect(isLocked()).toBe(false);
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

		fireEvent.mouseDown(
			screen.getByTestId('lifecycle-side-panel-backdrop')
		);

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('closes when a click lands outside the inert page', () => {
		const onOpenChange = jest.fn();

		render(<Panel onOpenChange={onOpenChange} open overlay />);

		fireEvent.mouseDown(document.body);

		expect(onOpenChange).toHaveBeenCalledWith(false);
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
