import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {noop} from 'lodash';
import {TitleEditor} from '../TitleEditor';

describe('TitleEditor', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<TitleEditor name='test' onChange={noop} />);
		expect(container).toMatchSnapshot();
	});

	it('should render with placeholder', () => {
		const placeholder = 'test placeholder';

		const {queryByPlaceholderText} = render(
			<TitleEditor
				name='test'
				onChange={noop}
				placeholder={placeholder}
			/>
		);

		expect(queryByPlaceholderText(placeholder)).toBeTruthy();
	});

	it('should render with value', () => {
		const value = 'foo';

		const {queryByText} = render(
			<TitleEditor name='test' onChange={noop} value={value} />
		);

		expect(queryByText(value)).toBeTruthy();
	});

	it('should render as editing', () => {
		const placeholder = 'test placeholder';

		const {container, getByPlaceholderText} = render(
			<TitleEditor
				name='test'
				onChange={noop}
				placeholder={placeholder}
			/>
		);

		const input = getByPlaceholderText(placeholder);

		expect(input).not.toBeVisible();

		fireEvent.click(container.querySelector('.btn'));

		expect(input).toBeVisible();
	});

	it('should not be editable if editable prop is false', () => {
		const {container} = render(
			<TitleEditor editable={false} name='test' onChange={noop} />
		);

		expect(container.querySelector('.btn')).toBeNull();
	});
});
