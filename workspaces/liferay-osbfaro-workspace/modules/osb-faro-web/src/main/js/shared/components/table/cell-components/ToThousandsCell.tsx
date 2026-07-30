import React from 'react';
import {toThousands} from 'shared/util/numbers';

const ToThousandsCell = ({
	data,
	getCount = ({individualCount}: any) => individualCount,
}: {
	data: any;
	getCount?: (data: any) => number;
}) => {
	const formattedCount = toThousands(getCount(data));

	return (
		<td className="table-cell-expand ">
			<div className="text-truncate text-right">{formattedCount}</div>
		</td>
	);
};

export default ToThousandsCell;
