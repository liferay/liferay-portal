import React from 'react';
import SubnavTbar from '../SubnavTbar';
import {cleanup, render} from '@testing-library/react';

describe('SubnavTbar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<SubnavTbar />);
		expect(container).toMatchSnapshot();
	});
});

describe('SubnavTbar.Item', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<SubnavTbar.Item />);
		expect(container).toMatchSnapshot();
	});
});

describe('SubnavTbar.Section', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<SubnavTbar.Section />);
		expect(container).toMatchSnapshot();
	});
});
