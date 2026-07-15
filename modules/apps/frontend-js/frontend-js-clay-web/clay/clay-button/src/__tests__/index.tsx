/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button, {ClayAIButton, ClayButtonWithIcon} from '..';
import Icon from '@clayui/icon';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

describe('Button', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(<Button>Button</Button>);

		expect(container).toMatchSnapshot();
	});

	it('renders with a different display type', () => {
		const {container} = render(<Button displayType="link">Button</Button>);

		expect(container).toMatchSnapshot();
	});

	it('renders displayType="ai"', () => {
		const {container} = render(
			<Button displayType="ai" rounded>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders outline displayType="ai"', () => {
		const {container} = render(
			<Button displayType="ai" outline rounded>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders disabled', () => {
		const {container} = render(
			<Button disabled displayType="primary">
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders borderless', () => {
		const {container} = render(
			<Button borderless displayType="primary">
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders block', () => {
		const {container} = render(
			<Button block displayType="primary">
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders outline', () => {
		const {container} = render(
			<Button displayType="primary" outline>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders monospaced', () => {
		const {container} = render(
			<Button displayType="primary" monospaced>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders small', () => {
		const {container} = render(
			<Button displayType="primary" small>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders translucent', () => {
		const {container} = render(
			<Button displayType="info" translucent>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders dark and translucent', () => {
		const {container} = render(
			<Button dark displayType="info" translucent>
				Button
			</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders displayType="beta" as info and translucent', () => {
		const {container} = render(<Button displayType="beta">Button</Button>);

		expect(container).toMatchSnapshot();
	});

	it('renders displayType="beta-dark" as dark, info and translucent', () => {
		const {container} = render(
			<Button displayType="beta-dark">Button</Button>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with a ButtonGroup', () => {
		const {container} = render(
			<Button.Group>
				<Button>Button</Button>
			</Button.Group>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ButtonGroup with spaced', () => {
		const {container} = render(
			<Button.Group spaced>
				<Button>Button</Button>

				<Button>Button</Button>
			</Button.Group>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders ClayButtonWithIcon', () => {
		const {container} = render(
			<ClayButtonWithIcon
				aria-label="Delete"
				spritemap="/some/path"
				symbol="trash"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders ClayButtonWithIcon without monospaced', () => {
		const {container} = render(
			<ClayButtonWithIcon
				aria-label="Delete"
				monospaced={false}
				spritemap="/some/path"
				symbol="trash"
			/>
		);

		expect(container).toMatchSnapshot();
	});
});

describe('ClayAIButton', () => {
	afterEach(cleanup);

	it('renders the solid variant with the AI icon and default label', () => {
		const {container} = render(<ClayAIButton />);

		const button = container.querySelector('button');

		expect(button).toHaveClass('btn', 'btn-ai', 'rounded-pill');
		expect(button).toHaveTextContent('Chat with AI');
		expect(
			container.querySelector('.lexicon-icon-stars')
		).toBeInTheDocument();
	});

	it('renders the gradient variant', () => {
		const {container} = render(<ClayAIButton gradient />);

		expect(container.querySelector('button')).toHaveClass(
			'btn-ai',
			'btn-ai-gradient'
		);
	});

	it('renders a custom label', () => {
		const {container} = render(<ClayAIButton label="Ask AI" />);

		expect(container.querySelector('button')).toHaveTextContent('Ask AI');
	});

	it('renders icon-only with the label as aria-label when monospaced', () => {
		const {container} = render(<ClayAIButton monospaced />);

		const button = container.querySelector('button');

		expect(button).toHaveClass('btn-monospaced', 'btn-ai');
		expect(button).toHaveAttribute('aria-label', 'Chat with AI');
		expect(
			container.querySelector('.lexicon-icon-stars')
		).toBeInTheDocument();
	});

	it('renders the outline variant from the base button', () => {
		const {container} = render(<ClayAIButton outline />);

		expect(container.querySelector('button')).toHaveClass('btn-outline-ai');
	});

	it('renders the link variant as a borderless underline', () => {
		const {container} = render(<ClayAIButton link />);

		expect(container.querySelector('button')).toHaveClass(
			'btn-outline-borderless',
			'btn-outline-ai',
			'btn-ai-link'
		);
	});
});

describe('Button accessibility error', () => {
	afterEach(cleanup);

	it('<Button /> with <Icon /> without an ARIA property throws an exception', () => {
		const originalError = console.error;
		console.error = jest.fn();

		render(
			<Button displayType="primary" outline>
				<Icon spritemap="icons.svg" symbol="plus" />
			</Button>
		);

		expect(console.error).toBeCalledWith(
			expect.stringContaining(
				'Button Accessibility: Component has only the Icon declared.'
			)
		);

		console.error = originalError;
	});

	it.concurrent.each(['aria-label', 'aria-labelledby'])(
		'<Button /> with <Icon /> with `%s` property does not throw an exception',
		(property) => {
			const originalError = console.error;
			console.error = jest.fn();

			const props = {
				[property]: 'some text',
			};

			render(
				<Button {...props} displayType="primary" outline>
					<Icon spritemap="icons.svg" symbol="plus" />
				</Button>
			);

			expect(console.error).not.toBeCalledWith(
				expect.stringContaining(
					'Button Accessibility: Component has only the Icon declared.'
				)
			);

			console.error = originalError;
		}
	);
});
