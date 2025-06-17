import ClayLayout from '@clayui/layout';
import React from 'react';
import {Cell, Label, Pie, PieChart, ResponsiveContainer} from 'recharts';

type PieSpecs = {
	endAngle: number;
	innerRadius: number;
	startAngle: number;
	outerRadius: number;
	pieColor: string;
};

const PIE_SPECS: PieSpecs = {
	endAngle: 235,
	innerRadius: 50,
	outerRadius: 70,
	pieColor: '#80ACFF',
	startAngle: 90
};

interface IGenericDonutChart {
	capacity: string;
	measurement: string;
}

export const GenericDonutChart: React.FC<IGenericDonutChart> = ({
	capacity,
	measurement
}) => {
	const data = [{value: 10}];

	return (
		<>
			<ClayLayout.Col className='d-flex mt-4 donut-chart-item' xl={4}>
				<ResponsiveContainer width='50%'>
					<PieChart>
						<Pie
							cornerRadius={5}
							data={[{value: 10}]}
							dataKey='value'
							endAngle={PIE_SPECS.endAngle}
							fill={PIE_SPECS.pieColor}
							innerRadius={PIE_SPECS.innerRadius}
							outerRadius={PIE_SPECS.outerRadius}
							startAngle={PIE_SPECS.startAngle}
						>
							{data.map((entry, index) => (
								<Cell key={`cell-${index}`} stroke='none' />
							))}
							<Label
								className='generic-pie-label'
								position='center'
								value='##'
							/>
						</Pie>
					</PieChart>
				</ResponsiveContainer>
				<div className='d-flex flex-column justify-content-center'>
					<h4>{capacity}</h4>
					<h4>{measurement}</h4>
				</div>
			</ClayLayout.Col>
		</>
	);
};
