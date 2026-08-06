import getCN from 'classnames';
import MetricBar, {Displays, Sizes} from 'shared/components/MetricBar';
import React from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import {Event} from 'event-analysis/utils/types';
import {toLocale} from 'shared/util/numbers';

export type BarComparisonTableItems = {
	isPreviousValue: boolean;
	name: string;
	percent: number;
	style: {
		'background-color': string;
	};
	value: number;
}[];

interface IBarComparisonTableProps extends React.HTMLAttributes<HTMLElement> {
	event: Event;
	isComparingSegment: boolean;
	items: BarComparisonTableItems;
}

const BarComparisonTable: React.FC<IBarComparisonTableProps> = ({
	event: {displayName, name},
	isComparingSegment,
	items = [],
}) => (
	<table className="table">
		<thead>
			<tr>
				<th className="table-head-title table-column-event-name">
					<TextTruncate
						title={
							!isComparingSegment
								? Liferay.Language.get('event-name')
								: displayName || name
						}
					/>
				</th>

				<th className="table-head-title">
					{Liferay.Language.get('total-events')}
				</th>
			</tr>
		</thead>

		<tbody>
			{items.map(({isPreviousValue, name, percent, style, value}, i) => (
				<tr key={i}>
					<td className="table-column-event-name">
						<TextTruncate title={name} />
					</td>
					<td>
						<MetricBar
							barClassName={getCN('mr-2', {
								['bar-zero']: value === 0,
								lines: isPreviousValue,
							})}
							barStyle={style}
							className="breakdown-table-bar"
							display={Displays.Primary}
							percent={percent}
							size={Sizes.Default}
						>
							<span>{toLocale(Number(value), undefined, 2)}</span>
						</MetricBar>
					</td>
				</tr>
			))}
		</tbody>
	</table>
);

export default BarComparisonTable;
