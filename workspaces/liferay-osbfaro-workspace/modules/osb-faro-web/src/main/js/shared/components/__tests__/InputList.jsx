import InputList from '../InputList';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createEvent} from '@testing-library/dom';

const createPasteEvent = html => {
	const text = html.replace('<[^>]*>', '');
	return {
		clipboardData: {
			getData: type => (type === 'text/plain' ? text : html),
			types: ['text/plain', 'text/html']
		}
	};
};

describe('InputList', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<InputList />);
		expect(container).toMatchSnapshot();
	});

	it('should render with existing items', () => {
		const {container} = render(<InputList items={['one', 'two']} />);
		expect(container).toMatchSnapshot();
	});

	it('should render with an error message', () => {
		const {container} = render(<InputList errorMessage='error' />);

		jest.runAllTimers();
		expect(container).toMatchSnapshot();
	});

	it('should render without an error message if no content', () => {
		const {container} = render(<InputList errorMessage='error' />);

		fireEvent.keyDown(container.querySelector('.form-control-inset'), {
			keyCode: 13,
			preventDefault: () => {},
			target: {value: 'fail'}
		});

		jest.runAllTimers();
		expect(container).toMatchSnapshot();
	});

	it('should only add item if validation passes', () => {
		const onItemsChange = jest.fn();

		const {container} = render(
			<InputList
				onItemsChange={onItemsChange}
				validationFn={val => val === 'pass'}
			/>
		);

		fireEvent.keyDown(container.querySelector('.form-control-inset'), {
			keyCode: 188,
			preventDefault: () => {},
			target: {value: 'fail'}
		});

		expect(onItemsChange).not.toHaveBeenCalled();

		fireEvent.keyDown(container.querySelector('.form-control-inset'), {
			keyCode: 188,
			preventDefault: () => {},
			target: {value: 'pass'}
		});

		expect(onItemsChange).toHaveBeenCalled();
	});

	it('should remove item from list', () => {
		const onItemsChange = jest.fn();

		const {container} = render(
			<InputList items={['one']} onItemsChange={onItemsChange} />
		);

		fireEvent.click(container.querySelector('.close'));

		expect(onItemsChange).toHaveBeenCalledWith([]);
	});

	it('should render as disabled', () => {
		const onItemsChange = jest.fn();

		const {container} = render(
			<InputList
				disabled
				items={['one', 'two', 'three']}
				onItemsChange={onItemsChange}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should only blur item if validation passes', () => {
		const onItemsChange = jest.fn();

		const {container} = render(
			<InputList
				items={['one']}
				onItemsChange={onItemsChange}
				validateOnBlur={jest.fn()}
				validationFn={val => val === 'pass'}
			/>
		);

		fireEvent.blur(container.querySelector('.form-control-inset'), {
			target: {value: 'fail'}
		});

		expect(onItemsChange).not.toHaveBeenCalled();

		fireEvent.blur(container.querySelector('.form-control-inset'), {
			target: {value: 'pass'}
		});

		expect(onItemsChange).toHaveBeenCalled();
	});

	it('should paste a text in inputList', () => {
		const onItemsChange = jest.fn();

		const {container} = render(
			<InputList
				onItemsChange={onItemsChange}
				validationFn={val => val === 'pass'}
			/>
		);

		const containerNode = container.querySelector('.form-control-inset');
		const eventProperties = createPasteEvent('test');

		const pasteEvent = createEvent.paste(containerNode, eventProperties);

		fireEvent(containerNode, pasteEvent);

		expect(container).toMatchSnapshot();
	});
});
