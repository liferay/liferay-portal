import Header from '../Header';
import React from 'react';
import {render} from '@testing-library/react';

describe('BaseDropdownHeader', () => {
	const WrappedComponent = props => (
		<Header
			tabs={[
				{
					onClick: jest.fn(),
					tabId: '0',
					title: 'Tab 0'
				},
				{
					onClick: jest.fn(),
					tabId: '1',
					title: 'Tab 1'
				}
			]}
			title='Title'
			{...props}
		/>
	);

	it('should render', () => {
		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render with "Tab 0" as active', () => {
		const {queryByText} = render(<WrappedComponent activeTabId='0' />);

		expect(queryByText('Tab 0').parentNode.parentNode.className).toContain(
			'active'
		);
	});
});
