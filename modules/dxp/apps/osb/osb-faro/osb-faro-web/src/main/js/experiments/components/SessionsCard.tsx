import * as d3 from 'd3';
import Card from 'shared/components/Card';
import ChartTooltip, {
	Alignments,
	Weights
} from 'shared/components/chart-tooltip';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {ComposedChart} from './ComposedChart';
import {getAxisFormatter} from 'shared/util/charts';
import {getDate} from 'shared/util/date';
import {Sizes} from 'shared/util/constants';
import {toThousandsABTesting} from 'experiments/util/experiments';

enum SessionView {
	Total = 'total',
	PerVariant = 'per-variant'
}

const TotalSessionsTooltip = ({dataPoint}) => {
	const header = [
		{
			columns: [
				{
					label: d3.utcFormat('%b %-d')(
						getDate(dataPoint[0].payload.key)
					),
					weight: Weights.Semibold,
					width: 100
				},
				{
					label: Liferay.Language.get('sessions'),
					weight: Weights.Semibold
				}
			]
		}
	];

	const rows = [
		{
			columns: [
				{
					color: dataPoint[0].color,
					label: dataPoint[0].name
				},
				{
					align: Alignments.Right,
					label: toThousandsABTesting(dataPoint[0].value)
				}
			]
		}
	];

	return (
		<div className='bb-tooltip-container position-static'>
			<ChartTooltip header={header} rows={rows} />
		</div>
	);
};

const PerVariantTooltip = ({dataPoint}) => {
	const control = dataPoint[0];
	const variant = dataPoint[1];

	const header = [
		{
			columns: [
				{
					label: d3.utcFormat('%b %-d')(getDate(control.payload.key)),
					weight: Weights.Semibold,
					width: 80
				},
				{
					label: Liferay.Language.get('sessions'),
					weight: Weights.Semibold
				}
			]
		}
	];

	const rows = [
		{
			columns: [
				{
					color: control.color,
					label: control.name
				},
				{
					align: Alignments.Right,
					label: toThousandsABTesting(control.value)
				}
			]
		},
		{
			columns: variant
				? [
						{
							color: dataPoint[1].color,
							label: dataPoint[1].name
						},
						{
							align: Alignments.Right,
							label: toThousandsABTesting(dataPoint[1].value)
						}
				  ]
				: []
		}
	];

	return (
		<div className='bb-tooltip-container position-static'>
			<ChartTooltip header={header} rows={rows} />
		</div>
	);
};

const formatTotalSessionsData = experiment => {
	const chartData = [];
	const control = experiment.dxpVariants[0];
	const variant = experiment.dxpVariants[1];

	control.sessionsHistogram.forEach((session, index) => {
		chartData.push({
			data_control:
				session.value + variant.sessionsHistogram?.[index]?.value || 0,
			key: session.key
		});
	});

	return {
		data: {
			controlLabel: Liferay.Language.get('total'),
			data: chartData,
			format: getAxisFormatter('number'),
			intervals: control.sessionsHistogram.map(({key}) => key)
		},
		Tooltip: TotalSessionsTooltip
	};
};

const formatPerVariantsData = experiment => {
	const chartData = [];
	const control = experiment.dxpVariants[0];
	const variant = experiment.dxpVariants[1];

	control.sessionsHistogram.forEach((session, index) => {
		chartData.push({
			data_control: session.value,
			data_variant: variant.sessionsHistogram?.[index]?.value || 0,
			key: session.key
		});
	});

	return {
		data: {
			controlLabel: control.dxpVariantName,
			data: chartData,
			format: getAxisFormatter('number'),
			intervals: control.sessionsHistogram.map(({key}) => key),
			variantLabel: variant.dxpVariantName
		},
		Tooltip: PerVariantTooltip
	};
};

const formatData = (sessionView: SessionView, experiment) => {
	if (sessionView === SessionView.Total) {
		return formatTotalSessionsData(experiment);
	}

	return formatPerVariantsData(experiment);
};

export const SessionsCard = ({experiment}) => {
	const [sessionView, setSessionView] = useState<SessionView>(
		SessionView.Total
	);

	return (
		<Card className='analytics-session-card' minHeight={405}>
			<Card.Header className='align-items-center d-flex justify-content-between'>
				<Card.Title>{Liferay.Language.get('test-sessions')}</Card.Title>

				<ClayButton.Group>
					<ClayButton
						className={getCN('button-root', {
							active: sessionView === SessionView.Total
						})}
						displayType='secondary'
						onClick={() => setSessionView(SessionView.Total)}
						size='sm'
					>
						<ClayIcon
							className='icon-root mr-2'
							symbol='session_single_chart'
						/>

						{Liferay.Language.get('total')}
					</ClayButton>

					<ClayButton
						className={getCN('button-root', {
							active: sessionView === SessionView.PerVariant
						})}
						displayType='secondary'
						onClick={() => setSessionView(SessionView.PerVariant)}
						size='sm'
					>
						<ClayIcon
							className='icon-root mr-2'
							symbol='session_multiple_chart'
						/>

						{Liferay.Language.get('per-variant')}
					</ClayButton>
				</ClayButton.Group>
			</Card.Header>

			<Card.Body>
				<StatesRenderer empty={!experiment.sessionsHistogram.length}>
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

					{!!experiment.sessionsHistogram.length && (
						<ComposedChart
							{...formatData(sessionView, experiment)}
						/>
					)}
				</StatesRenderer>
			</Card.Body>
		</Card>
	);
};
