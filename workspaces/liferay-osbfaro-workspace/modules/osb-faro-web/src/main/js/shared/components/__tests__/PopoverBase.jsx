import PopoverBase from '../PopoverBase';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('PopoverBase', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<PopoverBase placement='bottom' visible />);

		expect(container).toMatchSnapshot();
	});

	it('should render hidden', () => {
		const {container} = render(<PopoverBase visible={false} />);

		expect(container.querySelector('.hide')).toBeTruthy();
	});
});

describe('PopoverBase.Header', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<PopoverBase.Header children='Header' />);

		expect(container).toMatchSnapshot();
	});
});

describe('PopoverBase.Body', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<PopoverBase.Body children='Body' />);

		expect(container).toMatchSnapshot();
	});
});

describe('PopoverBase.Footer', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<PopoverBase.Footer children='Footer' />);

		expect(container).toMatchSnapshot();
	});
});
