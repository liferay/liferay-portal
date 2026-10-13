import Canvas from '../Canvas';
import React from 'react';
import {act, cleanup, render, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

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
		expect(getByText('Action').closest('.canvas-actions')).toBeTruthy();
		expect(getByText('Action').closest('.canvas-header')).toBeTruthy();
	});

	it('keeps the gap before the body unless the header has no body', () => {
		const {getByText} = render(
			<Canvas>
				<Canvas.Header title="With Body" />

				<Canvas.Header hasBody={false} title="Header Only" />
			</Canvas>
		);

		expect(getByText('With Body').closest('.canvas-header')).toHaveClass(
			'mb-5',
			'pb-0'
		);
		expect(
			getByText('Header Only').closest('.canvas-header')
		).not.toHaveClass('mb-5', 'pb-0');
	});

	it('renders the body children', () => {
		const {getByText} = render(
			<Canvas>
				<Canvas.Body>
					<span>{'Content'}</span>
				</Canvas.Body>
			</Canvas>
		);

		expect(getByText('Content').closest('.canvas-body')).toBeTruthy();
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

	it('renders the root and the slots as card parts', () => {
		const {container, getByTestId} = render(
			<Canvas className="custom-canvas" testId="canvas">
				<Canvas.Header className="custom-header" title="Canvas Title" />

				<Canvas.Body className="custom-body" />
			</Canvas>
		);

		expect(getByTestId('canvas')).toHaveClass(
			'canvas-root',
			'card',
			'custom-canvas'
		);
		expect(container.querySelector('.canvas-header')).toHaveClass(
			'card-header',
			'custom-header'
		);
		expect(container.querySelector('.canvas-body')).toHaveClass(
			'card-body',
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
		expect(container.querySelector('.c-empty-state')).not.toHaveClass(
			'invisible'
		);
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
			expect(container.querySelector('.c-empty-state')).toHaveClass(
				'invisible'
			);
		}
	);
});
