import DataValue from 'shared/components/DataValue';
import React from 'react';
import {toRounded, toThousands} from 'shared/util/numbers';

interface UniqueVisitorsCellIProps
	extends React.TdHTMLAttributes<HTMLTableCellElement> {
	uniqueVisitors: number;
	trafficSplit: number;
}

const UniqueVisitorsCell: React.FC<UniqueVisitorsCellIProps> = ({
	trafficSplit,
	uniqueVisitors,
	...otherProps
}) => (
	<td {...otherProps}>
		<div className="d-flex flex-column w-100">
			<span className="unique-visitors">
				{toThousands(uniqueVisitors)}
			</span>
			<span className="traffic-quota">
				<DataValue
					format={(trafficSplit) => `${toRounded(trafficSplit)}%`}
					value={trafficSplit}
				/>{' '}
				{Liferay.Language.get('traffic-split')}
			</span>
		</div>
	</td>
);

export default UniqueVisitorsCell;
