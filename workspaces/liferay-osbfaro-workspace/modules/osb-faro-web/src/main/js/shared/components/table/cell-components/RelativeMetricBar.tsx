import MetricBar, {Sizes} from 'shared/components/MetricBar';
import React, {FC} from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import {round} from 'lodash';
import {toLocale, toThousands} from 'shared/util/numbers';

interface IRelativeMetricBarProps extends React.HTMLAttributes<HTMLElement> {
	abbreviateCount?: boolean;
	data: {
		count: number;
		name: string;
	};
	empty?: boolean;
	maxCount?: number;
	showName?: boolean;
	total?: number;
	totalCount?: number;
}

const RelativeMetricBar: FC<IRelativeMetricBarProps> = ({
	abbreviateCount = false,
	data: {count, name},
	empty = false,
	showName = false,
	totalCount,
}) => {
	const percent = totalCount ? round(count / totalCount, 2) : 0;

	const displayName = showName ? name : '';

	return (
		<td className="table-cell-expand relative-metric-bar-root">
			<MetricBar percent={percent} size={Sizes.Lg}>
				<TextTruncate className="title" title={displayName} />

				{!empty && (
					<span className="count">
						{abbreviateCount ? toThousands(count) : toLocale(count)}
					</span>
				)}
			</MetricBar>
		</td>
	);
};

export default RelativeMetricBar;
