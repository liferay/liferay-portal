import Input from '../Input';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('Input', () => {
	afterEach(cleanup);

	it('should render input', () => {
		const {container} = render(<Input />);
		expect(container).toMatchSnapshot();
	});

	it('should render input with a different size', () => {
		const {container} = render(<Input size='lg' />);
		expect(container.querySelector('.form-control-lg')).toBeTruthy();
	});

	it('should render input group', () => {
		const {container} = render(<Input.Group />);
		expect(container).toMatchSnapshot();
	});

	it('should render input group with different size', () => {
		const {container} = render(<Input.Group size='lg' />);
		expect(container.querySelector('.input-group-lg')).toBeTruthy();
	});

	it('should render input group with inset', () => {
		const {container} = render(<Input.Group inset />);
		expect(container).toMatchSnapshot();
	});

	it('should render input group item', () => {
		const {container} = render(<Input.GroupItem />);
		expect(container).toMatchSnapshot();
	});

	it('should render input inset', () => {
		const {container} = render(<Input.Inset />);
		expect(container).toMatchSnapshot();
	});

	it('should render input button', () => {
		const {container} = render(
			<Input.Button displayType='secondary' position='append' />
		);
		expect(container).toMatchSnapshot();
	});

	it('should render input text', () => {
		const {container} = render(<Input.Text />);
		expect(container).toMatchSnapshot();
	});
});
