import DataValue from 'shared/components/DataValue';
import getCN from 'classnames';
import React from 'react';

interface IAccountNamesProps {
	className?: string;
	data: {
		accountName: string;
	};
}

const AccountNames: React.FC<IAccountNamesProps> = ({
	className,
	data: {accountName},
}) => (
	<td className={getCN('name-cell-root', className)}>
		<div className="text-truncate">
			<DataValue value={accountName} />
		</div>
	</td>
);

export default AccountNames;
