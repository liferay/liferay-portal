import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayNavigationBar from '@clayui/navigation-bar';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {MediansChart} from 'experiments/components/variant-card/MediansChart';
import {PerDayChart} from 'experiments/components/variant-card/per-day-chart/PerDayChart';
import {Sizes} from 'shared/util/constants';
import {VariantTable} from './VariantTable';

enum VariantView {
	Medians = 'medians',
	PerDay = 'per-day'
}

export const VariantCard = ({experiment}) => {
	const [variantView, setVariantView] = useState<VariantView>(
		VariantView.Medians
	);

	const Component =
		variantView === VariantView.Medians ? MediansChart : PerDayChart;

	return (
		<Card className='analytics-variant-card'>
			<Card.Header>
				<Card.Title>
					{Liferay.Language.get('variant-report')}
				</Card.Title>
				<ClayNavigationBar
					triggerLabel={
						variantView === VariantView.Medians
							? Liferay.Language.get('medians')
							: Liferay.Language.get('per-day')
					}
				>
					<ClayNavigationBar.Item
						active={variantView === VariantView.Medians}
					>
						<ClayButton
							onClick={() => setVariantView(VariantView.Medians)}
						>
							{Liferay.Language.get('medians')}
						</ClayButton>
					</ClayNavigationBar.Item>

					<ClayNavigationBar.Item
						active={variantView === VariantView.PerDay}
					>
						<ClayButton
							onClick={() => setVariantView(VariantView.PerDay)}
						>
							{Liferay.Language.get('per-day')}
						</ClayButton>
					</ClayNavigationBar.Item>
				</ClayNavigationBar>
			</Card.Header>

			<Card.Body>
				<div className='analytics-variant-card-charts'>
					<StatesRenderer empty={!experiment.metricsHistogram.length}>
						<StatesRenderer.Empty
							className='my-6'
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

						{!!experiment.metricsHistogram.length && (
							<Component experiment={experiment} />
						)}
					</StatesRenderer>

					<VariantTable experiment={experiment} />
				</div>
			</Card.Body>
		</Card>
	);
};
