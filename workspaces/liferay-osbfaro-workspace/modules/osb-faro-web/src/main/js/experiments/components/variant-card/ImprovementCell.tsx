import ClayIcon from 'shared/components/Icon';
import DataValue from 'shared/components/DataValue';
import React from 'react';
import {formatPercent} from 'shared/util/numbers';

interface ImprovementCellIProps
	extends React.TdHTMLAttributes<HTMLTableCellElement> {
	improvement?: number | null;
}

const ImprovementCell: React.FC<ImprovementCellIProps> = ({
	improvement,
	...otherProps
}) => {
	const classname = !improvement ? '' : improvement > 0 ? 'lift' : 'loss';
	const prefix =
		improvement && improvement > 0 ? (
			<ClayIcon className="icon-root" symbol="caret-top" />
		) : (
			<ClayIcon className="icon-root" symbol="caret-bottom" />
		);
	const sufix =
		improvement && improvement > 0
			? Liferay.Language.get('lift').toLowerCase()
			: Liferay.Language.get('loss').toLowerCase();

	return (
		<td {...otherProps}>
			<span className={classname}>
				<DataValue
					format={(improvement: number) =>
						improvement !== 0 ? (
							<>
								{prefix} {formatPercent(Math.abs(improvement))}{' '}
								{sufix}
							</>
						) : (
							formatPercent(0)
						)
					}
					type="number"
					value={improvement}
				/>
			</span>
		</td>
	);
};

export default ImprovementCell;
