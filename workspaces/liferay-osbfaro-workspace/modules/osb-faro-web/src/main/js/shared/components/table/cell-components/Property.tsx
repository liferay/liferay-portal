import DataValue from 'shared/components/DataValue';
import getCN from 'classnames';
import React from 'react';

interface IPropertyCellProps {
	className?: string;
	data: {
		name: string;
		value: string;
	};
}

const PropertyCell: React.FC<IPropertyCellProps> = ({
	className,
	data: {name, value},
}) => (
	<td className={getCN('property-cell', className)}>
		<div className="name">{name}</div>

		<div className="table-title">
			<DataValue value={value} />
		</div>
	</td>
);

export default PropertyCell;
