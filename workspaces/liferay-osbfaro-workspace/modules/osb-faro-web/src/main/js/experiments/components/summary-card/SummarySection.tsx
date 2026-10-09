import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';
import {sub} from 'shared/util/lang';

interface SummarySectionIProps extends React.HTMLAttributes<HTMLElement> {
	title: string;
}

export const SummarySection: React.FC<SummarySectionIProps> & {
	Variant: typeof Variant;
	Description: typeof Description;
	Heading: typeof Heading;
	ProgressBar: typeof ProgressBar;
	MetricType: typeof MetricType;
} = ({children, className, title, ...otherProps}) => {
	const classes = getCN('analytics-summary-section', className);

	return (
		<div className={classes} {...otherProps}>
			<div className="analytics-summary-section-title">{title}</div>

			{children}
		</div>
	);
};

const Description = ({value}: {value: React.ReactNode}) => (
	<div className="analytics-summary-section-description">{value}</div>
);

const Heading = ({value}: {value: React.ReactNode}) => (
	<h2 className="analytics-summary-section-heading">{value}</h2>
);

const MetricType = ({value}: {value: React.ReactNode}) => (
	<div className="analytics-summary-section-metric-type">
		<span className="analytics-summary-section-metric-type-icon">
			<ClayIcon className="icon-root" symbol="web-content" />
		</span>

		{value}
	</div>
);

const ProgressBar = ({value}: {value: number}) => (
	<div
		className={getCN('analytics-summary-section-progress', {
			complete: value === 100,
		})}
	>
		<div
			className="analytics-summary-section-progress-bar"
			style={{width: `${value}%`}}
		/>
	</div>
);

const Variant = ({
	lift,
	status,
}: {
	lift: number | string;
	status: 'up' | 'down';
}) => {
	const symbol = status === 'up' ? 'caret-top' : 'caret-bottom';

	return (
		<div className="analytics-summary-section-variant">
			<span
				className={getCN('analytics-summary-section-variant-status', {
					[`analytics-summary-section-variant-status-${status}`]:
						status,
				})}
			>
				<ClayIcon className="icon-root" symbol={symbol} />

				{sub(Liferay.Language.get('x-lift'), [lift])}
			</span>

			<span>{Liferay.Language.get('over-control')}</span>
		</div>
	);
};

SummarySection.Description = Description;
SummarySection.Variant = Variant;
SummarySection.Heading = Heading;
SummarySection.MetricType = MetricType;
SummarySection.ProgressBar = ProgressBar;
