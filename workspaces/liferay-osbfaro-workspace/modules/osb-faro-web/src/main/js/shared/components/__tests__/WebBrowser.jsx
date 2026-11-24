import React from 'react';
import WebBrowser from '../WebBrowser';
import {render} from '@testing-library/react';

const DEFAULT_METRIC_LABEL = 'Views';

const DEFAULT_BROWSERS = [
	{
		color: '#4B9BFF',
		data: [1],
		id: '0Firefox'
	},
	{
		color: '#4B9B00',
		data: [2],
		id: '1Chrome'
	},
	{
		color: '#4B9B99',
		data: [3],
		id: '2Safari'
	},
	{
		color: '#4B9B99',
		data: [4],
		id: '3ChromeMobile'
	},
	{
		color: '#4B9B99',
		data: [5],
		id: '4SafariMobile'
	},
	{
		color: '#4B9B99',
		data: [5],
		id: '5Safari'
	},
	{
		color: '#4B9B99',
		data: [1],
		id: '6WebLight'
	},
	{
		color: '#4B9B99',
		data: [10],
		id: '7Unknown'
	},
	{
		color: '#4B9B99',
		data: [3],
		id: '8Edge'
	},
	{
		color: '#4B9B99',
		data: [11],
		id: '9Opera'
	},
	{
		color: '#4B9B99',
		data: [25],
		id: '10Others'
	}
];

describe('WebBrowser', () => {
	it('should render', () => {
		const {container} = render(
			<WebBrowser
				browsers={DEFAULT_BROWSERS}
				metricLabel={DEFAULT_METRIC_LABEL}
				total={70}
			/>
		);
		expect(container).toMatchSnapshot();
	});
});
