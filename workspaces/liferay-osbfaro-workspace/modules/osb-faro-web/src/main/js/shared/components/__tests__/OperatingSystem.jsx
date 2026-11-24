import OperatingSystem from '../OperatingSystem';
import React from 'react';
import {render} from '@testing-library/react';

const devices = [
	{
		data: [
			{percentage: 89.80645161290323, type: 'Windows', views: 696},
			{percentage: 0.12903225806451613, type: 'Linux', views: 1}
		],
		percentageOfTotal: 89.93548387096774,
		totalViews: 697,
		type: 'Desktop'
	},
	{
		data: [{percentage: 10.967741935483872, type: 'iOS', views: 85}],
		percentageOfTotal: 10.967741935483872,
		totalViews: 85,
		type: 'Tablet'
	}
];

describe('OperatingSystem', () => {
	it('should render', () => {
		const {container} = render(<OperatingSystem devices={devices} />);

		expect(container).toMatchSnapshot();
	});
});
