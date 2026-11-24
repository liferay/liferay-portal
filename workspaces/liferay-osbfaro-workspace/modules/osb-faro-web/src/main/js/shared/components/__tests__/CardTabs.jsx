import CardTabs from '../CardTabs';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

const mockTabs = [
	{
		secondaryInfo: 'Foo secondary info',
		tabId: 'foo',
		title: 'Foo Tab'
	},
	{
		secondaryInfo: 'Bar secondary info',
		tabId: 'bar',
		title: 'Bar Tab'
	}
];

describe('CardTabs', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<CardTabs activeTabId='bar' tabs={mockTabs} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should should call onChange prop when a tab is clicked', () => {
		const onChangeSpy = jest.fn();

		const {getByText} = render(
			<CardTabs
				activeTabId='bar'
				onChange={onChangeSpy}
				tabs={mockTabs}
			/>
		);

		fireEvent.click(getByText(/Foo Tab/));

		expect(onChangeSpy).toHaveBeenCalledWith('foo');
	});
});
