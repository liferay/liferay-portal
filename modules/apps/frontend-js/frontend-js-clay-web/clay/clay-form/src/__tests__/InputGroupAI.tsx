/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import ClayInputGroupAI from '../InputGroupAI';

describe('BasicRendering', () => {
	afterEach(cleanup);

	it('renders an input group with the input-group-ai class', () => {
		const {container} = render(
			<ClayInputGroupAI onChange={() => {}} value="" />
		);

		const group = container.querySelector('.input-group');

		expect(group!.classList).toContain('input-group-ai');
		expect(group!.getAttribute('data-ai-state')).toBeNull();
	});

	it('forwards textarea props and className to the right elements', () => {
		const {container} = render(
			<ClayInputGroupAI
				className="my-custom-class"
				id="promptInput"
				onChange={() => {}}
				placeholder="Ask me anything..."
				value=""
			/>
		);

		const group = container.querySelector('.input-group-ai');
		const textArea = screen.getByPlaceholderText('Ask me anything...');

		expect(group!.classList).toContain('my-custom-class');
		expect(textArea.classList).toContain('form-control-inset');
		expect(textArea.id).toBe('promptInput');
	});

	it('disables the submit button when the value is empty', () => {
		render(<ClayInputGroupAI onChange={() => {}} value="   " />);

		expect(screen.getByRole('button', {name: 'Submit'})).toBeDisabled();
	});

	it('enables the submit button when the value has content', () => {
		render(<ClayInputGroupAI onChange={() => {}} value="Hello" />);

		expect(screen.getByRole('button', {name: 'Submit'})).toBeEnabled();
	});

	it('renders the working state with a readonly textarea and message', () => {
		const {container} = render(
			<ClayInputGroupAI aiState="working" onChange={() => {}} value="" />
		);

		const group = container.querySelector('.input-group-ai');
		const textArea = container.querySelector('textarea');

		expect(group!.getAttribute('data-ai-state')).toBe('working');
		expect(textArea!.readOnly).toBe(true);
		expect(textArea!.value).toBe('Working on it...');
		expect(
			screen.queryByRole('button', {name: 'Submit'})
		).not.toBeInTheDocument();
	});

	it('renders a readonly textarea in the result-readonly state', () => {
		const {container} = render(
			<ClayInputGroupAI
				aiState="result-readonly"
				onChange={() => {}}
				value="A generated suggestion"
			/>
		);

		const textArea = container.querySelector('textarea');

		expect(textArea!.readOnly).toBe(true);
		expect(textArea!.value).toBe('A generated suggestion');
	});

	it('only renders the retry button in the result state with an onRetryClick handler', () => {
		const {rerender} = render(
			<ClayInputGroupAI
				onChange={() => {}}
				onRetryClick={() => {}}
				value=""
			/>
		);

		expect(
			screen.queryByRole('button', {name: 'Retry'})
		).not.toBeInTheDocument();

		rerender(
			<ClayInputGroupAI aiState="result" onChange={() => {}} value="" />
		);

		expect(
			screen.queryByRole('button', {name: 'Retry'})
		).not.toBeInTheDocument();

		rerender(
			<ClayInputGroupAI
				aiState="result"
				onChange={() => {}}
				onRetryClick={() => {}}
				value=""
			/>
		);

		expect(screen.getByRole('button', {name: 'Retry'})).toBeInTheDocument();
	});

	it('renders custom messages', () => {
		render(
			<ClayInputGroupAI
				aiState="result"
				messages={{retry: 'Reintentar', submit: 'Enviar'}}
				onChange={() => {}}
				onRetryClick={() => {}}
				value=""
			/>
		);

		expect(
			screen.getByRole('button', {name: 'Enviar'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'Reintentar'})
		).toBeInTheDocument();
	});
});

describe('IncrementalInteractions', () => {
	afterEach(cleanup);

	it('applies the focused state while the textarea has focus', () => {
		const {container} = render(
			<ClayInputGroupAI onChange={() => {}} value="" />
		);

		const group = container.querySelector('.input-group-ai');
		const textArea = container.querySelector('textarea');

		fireEvent.focus(textArea!);

		expect(group!.getAttribute('data-ai-state')).toBe('focused');

		fireEvent.blur(textArea!);

		expect(group!.getAttribute('data-ai-state')).toBeNull();
	});

	it('does not override a controlled aiState on focus', () => {
		const {container} = render(
			<ClayInputGroupAI aiState="result" onChange={() => {}} value="" />
		);

		const group = container.querySelector('.input-group-ai');
		const textArea = container.querySelector('textarea');

		fireEvent.focus(textArea!);

		expect(group!.getAttribute('data-ai-state')).toBe('result');
	});

	it('submits the enclosing form and resets the textarea height when Enter is pressed', () => {
		const onSubmit = jest.fn((event) => event.preventDefault());

		const {container} = render(
			<form onSubmit={onSubmit}>
				<ClayInputGroupAI onChange={() => {}} value="Hello" />
			</form>
		);

		const textArea = container.querySelector('textarea')!;

		textArea.style.height = '96px';

		fireEvent.keyDown(textArea, {key: 'Enter'});

		expect(onSubmit).toHaveBeenCalledTimes(1);
		expect(textArea.style.height).toBe('');
	});

	it('does not submit the form or reset the textarea height when Shift + Enter is pressed', () => {
		const onSubmit = jest.fn((event) => event.preventDefault());

		const {container} = render(
			<form onSubmit={onSubmit}>
				<ClayInputGroupAI onChange={() => {}} value="Hello" />
			</form>
		);

		const textArea = container.querySelector('textarea')!;

		textArea.style.height = '96px';

		fireEvent.keyDown(textArea, {key: 'Enter', shiftKey: true});

		expect(onSubmit).not.toHaveBeenCalled();
		expect(textArea.style.height).toBe('96px');
	});

	it('resets the textarea height when the submit button is clicked', () => {
		const {container} = render(
			<form onSubmit={(event) => event.preventDefault()}>
				<ClayInputGroupAI onChange={() => {}} value="Hello" />
			</form>
		);

		const textArea = container.querySelector('textarea')!;

		textArea.style.height = '96px';

		fireEvent.click(screen.getByRole('button', {name: 'Submit'}));

		expect(textArea.style.height).toBe('');
	});

	it('resets the textarea height when the value is cleared externally', () => {
		const {container, rerender} = render(
			<ClayInputGroupAI onChange={() => {}} value={'multi\nline'} />
		);

		const textArea = container.querySelector('textarea')!;

		textArea.style.height = '96px';

		rerender(<ClayInputGroupAI onChange={() => {}} value="" />);

		expect(textArea.style.height).toBe('');
	});

	it('calls onRetryClick when the retry button is clicked', () => {
		const onRetryClick = jest.fn();

		render(
			<ClayInputGroupAI
				aiState="result"
				onChange={() => {}}
				onRetryClick={onRetryClick}
				value=""
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'Retry'}));

		expect(onRetryClick).toHaveBeenCalledTimes(1);
	});

	it('calls onChange when the user types', () => {
		const onChange = jest.fn();

		const {container} = render(
			<ClayInputGroupAI onChange={onChange} value="" />
		);

		fireEvent.change(container.querySelector('textarea')!, {
			target: {value: 'Hello'},
		});

		expect(onChange).toHaveBeenCalledTimes(1);
	});
});
