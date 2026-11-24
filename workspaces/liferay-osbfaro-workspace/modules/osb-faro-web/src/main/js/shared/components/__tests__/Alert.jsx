import Alert, {AlertTypes} from '../Alert';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

describe('Alert', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Alert />);
		expect(container).toMatchSnapshot();
	});

	it('should render as dismissable', () => {
		const {container} = render(<Alert onClose={jest.fn()} />);
		expect(container.querySelector('.close')).toBeTruthy();
	});

	it('should render with close', () => {
		const spy = jest.fn();

		const {container} = render(<Alert onClose={spy} />);

		expect(spy).not.toBeCalled;

		fireEvent.click(container.querySelector('.close'));

		expect(spy).toBeCalled();
	});

	it('should render with stripe', () => {
		const {container} = render(<Alert stripe />);
		expect(container.querySelector('.container')).toBeTruthy();
	});

	it('should render as dismissable', () => {
		const {container} = render(<Alert onClose={jest.fn()} />);
		expect(container.querySelector('.close')).toBeTruthy();
	});

	it('should render with a title', () => {
		const {getByText} = render(<Alert title='hello world' />);
		expect(getByText(/hello world/)).toBeTruthy();
	});

	it('should render with a specific type', () => {
		const {container} = render(<Alert type={AlertTypes.Info} />);
		expect(container.querySelector('.alert-info')).toBeTruthy();
	});

	it('should render with content', () => {
		const {getByText} = render(<Alert children={['hello children']} />);
		expect(getByText(/hello children/)).toBeTruthy();
	});

	it('should render with multiple config set', () => {
		const {getByText} = render(
			<Alert
				children={['hello children']}
				onClose={jest.fn()}
				title='this is a title'
			/>
		);

		expect(getByText(/hello children/)).toBeTruthy();
		expect(getByText(/this is a title/)).toBeTruthy();
	});
});
