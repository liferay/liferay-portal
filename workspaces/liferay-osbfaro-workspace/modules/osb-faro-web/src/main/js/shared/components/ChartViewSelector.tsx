import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';
import {Option, Picker} from '@clayui/core';

export type ChartView = 'bar' | 'line';

export const DEFAULT_CHART_VIEW: ChartView = 'bar';

const CHART_VIEW_OPTIONS: Record<ChartView, {icon: string; label: string}> = {
	bar: {
		icon: 'chart-bar-x-axis',
		label: Liferay.Language.get('bar-chart'),
	},
	line: {
		icon: 'chart-line',
		label: Liferay.Language.get('line-chart'),
	},
};

const CHART_VIEWS = Object.keys(CHART_VIEW_OPTIONS) as ChartView[];

interface IChartViewTriggerProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	icon?: string;
	label?: string;
}

const ChartViewTrigger = React.forwardRef<
	HTMLButtonElement,
	IChartViewTriggerProps
>(({icon, label, ...otherProps}, ref) => (
	<ClayButton
		{...otherProps}
		className="button-root"
		displayType="secondary"
		ref={ref}
		size="sm"
	>
		{icon && <ClayIcon className="icon-root mr-2" symbol={icon} />}

		{label}

		<ClayIcon className="icon-root ml-2" symbol="caret-bottom" />
	</ClayButton>
));

ChartViewTrigger.displayName = 'ChartViewTrigger';

interface IChartViewSelectorProps {
	chartView: ChartView;
	onChange: (chartView: ChartView) => void;
}

const ChartViewSelector: React.FC<IChartViewSelectorProps> = ({
	chartView,
	onChange,
}) => {
	const {icon, label} = CHART_VIEW_OPTIONS[chartView];

	return (
		<Picker
			aria-label={Liferay.Language.get('chart-view')}
			as={ChartViewTrigger}
			icon={icon}
			items={CHART_VIEWS}
			label={label}
			onSelectionChange={(key) => onChange(key as ChartView)}
			selectedKey={chartView}
		>
			{(chartViewOption: ChartView) => (
				<Option key={chartViewOption}>
					<ClayIcon
						className="mr-2"
						symbol={CHART_VIEW_OPTIONS[chartViewOption].icon}
					/>

					{CHART_VIEW_OPTIONS[chartViewOption].label}
				</Option>
			)}
		</Picker>
	);
};

export default ChartViewSelector;
