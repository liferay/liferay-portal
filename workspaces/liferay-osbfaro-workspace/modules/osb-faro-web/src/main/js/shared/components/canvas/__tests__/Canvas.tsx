import Canvas from '../Canvas';
import React from 'react';
import {act, cleanup, render, screen, waitFor} from '@testing-library/react';

const EMPTY_STATE = {
	description: 'Drop something here.',
	title: 'Nothing Here',
};

describe('Canvas', () => {
	afterEach(cleanup);

	it('renders the title as a heading and the actions in the header', () => {
		const {getByRole, getByText} = render(
			<Canvas>
				<Canvas.Header title="Canvas Title">
					<Canvas.Actions>
						<button type="button">{'Action'}</button>
					</Canvas.Actions>
				</Canvas.Header>
			</Canvas>
		);

		expect(
			getByRole('heading', {level: 2, name: 'Canvas Title'})
		).toBeInTheDocument();
		expect(getByText(/^action$/i).closest('.canvas-actions')).toBeTruthy();
		expect(getByText(/^action$/i).closest('.canvas-header')).toBeTruthy();
	});

	it('renders the body children', () => {
		const {getByText} = render(
			<Canvas>
				<Canvas.Body>
					<span>{'Content'}</span>
				</Canvas.Body>
			</Canvas>
		);

		expect(getByText(/^content$/i).closest('.canvas-body')).toBeTruthy();
	});

	it('renders the empty state as a body child', () => {
		const {getByRole, getByText} = render(
			<Canvas>
				<Canvas.Body>
					<Canvas.EmptyState {...EMPTY_STATE} />
				</Canvas.Body>
			</Canvas>
		);

		expect(
			getByRole('heading', {level: 3, name: EMPTY_STATE.title})
		).toBeInTheDocument();
		expect(getByText(EMPTY_STATE.description)).toBeInTheDocument();
	});

	it('keeps the class names given to the root and the slots', () => {
		const {container, getByTestId} = render(
			<Canvas className="custom-canvas" testId="canvas">
				<Canvas.Header className="custom-header" title="Canvas Title" />

				<Canvas.Body className="custom-body" />
			</Canvas>
		);

		expect(getByTestId('canvas')).toHaveClass('custom-canvas');
		expect(container.querySelector('.canvas-header')).toHaveClass(
			'custom-header'
		);
		expect(container.querySelector('.canvas-body')).toHaveClass(
			'custom-body'
		);
	});
});

describe('Canvas.EmptyState', () => {
	afterEach(cleanup);

	it('renders only the animated illustration by default', () => {
		const {container} = render(<Canvas.EmptyState {...EMPTY_STATE} />);

		const images = container.querySelectorAll('img');

		expect(images).toHaveLength(1);
		expect(images[0]).toHaveAttribute('alt', '');
		expect(images[0]).toHaveAttribute('src', 'empty_state.svg');
	});

	it('swaps to the reduced motion illustration when it is enabled', async () => {
		const {container} = render(<Canvas.EmptyState {...EMPTY_STATE} />);

		act(() => {
			document.body.classList.add('c-prefers-reduced-motion');
		});

		await waitFor(() => {
			const images = container.querySelectorAll('img');

			expect(images).toHaveLength(1);
			expect(images[0]).toHaveAttribute(
				'src',
				'empty_state_reduced_motion.svg'
			);
		});

		document.body.classList.remove('c-prefers-reduced-motion');
	});

	it('shows the content while nothing is dragged', () => {
		const {container} = render(<Canvas.EmptyState {...EMPTY_STATE} />);

		expect(
			container.querySelector('.canvas-empty-state-idle')
		).toBeInTheDocument();
		expect(
			screen.getByRole('heading', {name: EMPTY_STATE.title})
		).toBeInTheDocument();
	});

	it.each(['dragging', 'over'] as const)(
		'hides the content in the %s drop state',
		(dropState) => {
			const {container} = render(
				<Canvas.EmptyState {...EMPTY_STATE} dropState={dropState} />
			);

			expect(
				container.querySelector(`.canvas-empty-state-${dropState}`)
			).toBeInTheDocument();
			expect(
				screen.queryByRole('heading', {name: EMPTY_STATE.title})
			).not.toBeInTheDocument();
		}
	);
});
