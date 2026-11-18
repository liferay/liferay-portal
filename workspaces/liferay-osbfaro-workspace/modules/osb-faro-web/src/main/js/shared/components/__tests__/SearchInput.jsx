import React from 'react';
import SearchInput from '../SearchInput';
import {cleanup, createEvent, fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('SearchInput', () => {
	afterEach(cleanup);

	it('should render input', () => {
		const {container} = render(<SearchInput />);
		expect(container).toMatchSnapshot();
	});

	it('should return input value if key is `Enter`', () => {
		const value = 'foo';
		const onSubmit = jest.fn();

		const {getByPlaceholderText} = render(
			<SearchInput onSubmit={onSubmit} value={value} />
		);

		const node = getByPlaceholderText(/^search$/i);
		const event = createEvent.keyDown(node, {key: 'Enter'});
		fireEvent(node, event);
		expect(onSubmit).toHaveBeenCalledWith(value);
	});

	it('should not call onSubmit or event.preventDefault', () => {
		const onSubmit = jest.fn();

		const {getByPlaceholderText} = render(
			<SearchInput onSubmit={onSubmit} />
		);
		const node = getByPlaceholderText(/^search$/i);
		const event = createEvent.keyDown(node, {key: 'a'});
		fireEvent(node, event);

		expect(onSubmit).not.toHaveBeenCalled();
	});

	it('should return input value', () => {
		const value = 'foo';
		const onChange = jest.fn();

		const {getByPlaceholderText} = render(
			<SearchInput onChange={onChange} />
		);

		fireEvent.change(getByPlaceholderText(/^search$/i), {target: {value}});

		expect(onChange).toHaveBeenCalledWith(value);
	});

	it('should render a button to clear input', () => {
		const {container} = render(<SearchInput value='foo' />);
		expect(container.querySelector('.lexicon-icon-times')).toBeTruthy();
	});
});
