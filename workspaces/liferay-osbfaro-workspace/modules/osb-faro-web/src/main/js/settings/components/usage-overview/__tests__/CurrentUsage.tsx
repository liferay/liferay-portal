import React from 'react';
import {CurrentUsage} from '../CurrentUsage';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const renderCurrentUsage = (props = {}) =>
	render(
		<CurrentUsage
			count={50}
			items={{itemA: {color: '#000', label: 'Page Views', value: 50}}}
			legendText="Legend"
			limit={200}
			percentageText={() => '{0}% used'}
			{...props}
		/>
	);

describe('CurrentUsage', () => {
	it('should render the used percentage', () => {
		const {container} = renderCurrentUsage();

		expect(container).toHaveTextContent('50 of 200 - 25% used');
	});

	it('should render an unlimited limit without the used percentage', () => {
		const {container} = renderCurrentUsage({limit: -1});

		expect(container).toHaveTextContent('50 of Unlimited');
		expect(container).not.toHaveTextContent('used');
	});

	it('should render a zero percentage when the count is zero', () => {
		const {container} = renderCurrentUsage({count: 0});

		expect(container).toHaveTextContent('0 of 200 - 0% used');
	});
});
