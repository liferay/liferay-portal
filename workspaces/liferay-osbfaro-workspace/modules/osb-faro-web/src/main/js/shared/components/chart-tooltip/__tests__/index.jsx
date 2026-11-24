import React from 'react';
import TooltipChart from '../index';
import {render} from '@testing-library/react';

describe('TooltipChart', () => {
	it('should render', () => {
		const {container} = render(
			<TooltipChart
				className='custom-tooltip-class'
				header={[
					{
						columns: [
							{
								label: 'title'
							},
							{
								label: 'description'
							}
						]
					}
				]}
				rows={[
					{
						className: 'class-custom-row',
						columns: [
							{
								align: 'left',
								className: 'class-custom-column-1',
								color: 'red',
								label: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius hic ex, vero laboriosam necessitatibus, repudiandae est voluptatem.',
								truncated: true,
								weight: 'normal',
								width: 100
							},
							{
								align: 'right',
								className: 'class-custom-column-2',
								label: 'column 2 description',
								weight: 'semibold',
								width: 50
							}
						]
					},
					{
						columns: [
							{
								align: 'left',
								color: 'blue',
								label: 'column 1 title',
								weight: 'light'
							},
							{
								align: 'right',
								label: 'column 2 description',
								weight: 'semibold'
							}
						]
					}
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
