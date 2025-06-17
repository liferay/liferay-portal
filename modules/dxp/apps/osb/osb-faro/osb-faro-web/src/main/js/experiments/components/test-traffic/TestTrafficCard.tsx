import Card from 'shared/components/Card';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {ComposedChart} from '../ComposedChart';
import {getAxisFormatter} from 'shared/util/charts';
import {Sizes} from 'shared/util/constants';
import {Tooltip} from './Tooltip';

const formatData = experiment => {
	const chartData = [];
	const control = experiment.dxpVariants[0];
	const variant = experiment.dxpVariants[1];

	control.sessionsHistogram.forEach((session, index) => {
		chartData.push({
			data_control: session.value,
			data_control_traffic_split: control.trafficSplit,
			data_variant: variant.sessionsHistogram[index].value,
			data_variant_traffic_split: variant.trafficSplit,
			key: session.key
		});
	});

	return {
		controlLabel: control.dxpVariantName,
		data: chartData,
		format: getAxisFormatter('number'),
		intervals: control.sessionsHistogram.map(({key}) => key),
		variantLabel: variant.dxpVariantName
	};
};

const TestTraffic = ({experiment}) => (
	<Card className='analytics-session-card' minHeight={405}>
		<Card.Header className='align-items-center d-flex justify-content-between'>
			<Card.Title>{Liferay.Language.get('test-traffic')}</Card.Title>
		</Card.Header>

		<Card.Body>
			<StatesRenderer empty={!experiment.dxpVariants.length}>
				<StatesRenderer.Empty
					description={Liferay.Language.get(
						'metrics-will-show-once-there-are-visitors-to-your-variants'
					)}
					icon={{
						border: false,
						size: Sizes.XLarge,
						symbol: 'ac_chart'
					}}
					title={Liferay.Language.get(
						'we-are-currently-collecting-data'
					)}
				/>

				{!!experiment.dxpVariants.length && (
					<ComposedChart
						data={formatData(experiment)}
						Tooltip={Tooltip}
					/>
				)}
			</StatesRenderer>
		</Card.Body>
	</Card>
);

export default TestTraffic;
