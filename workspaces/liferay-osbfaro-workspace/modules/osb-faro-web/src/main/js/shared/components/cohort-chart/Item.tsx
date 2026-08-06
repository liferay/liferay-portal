import Popover from 'shared/components/Popover';
import React, {useRef, useState} from 'react';
import ReactDOM from 'react-dom';
import {CohortHeatMapType} from './index';
import {sub} from 'shared/util/lang';
import {formatPercent, toLocale} from 'shared/util/numbers';

const Item: React.FC<CohortHeatMapType> = ({
	colorHex,
	date,
	dateLabelFn,
	periodLabel,
	retention,
	value,
}) => {

	// eslint-disable-next-line no-undef
	const _ref = useRef<HTMLTableDataCellElement>(null);

	const [visible, setVisible] = useState(false);

	const handleMouseOut = () => setVisible(false);
	const handleMouseOver = () => setVisible(true);

	const content = (
		<span className="cohort-item-popover d-flex justify-content-between">
			<span className="period">{periodLabel}</span>

			<span className="visitors">
				{sub(Liferay.Language.get('x-visitors'), [toLocale(value)])}
			</span>
		</span>
	);

	return (
		<td
			className="cohort-item-root table-column-text-center"
			onBlur={handleMouseOver}
			onFocus={handleMouseOut}
			onMouseOut={handleMouseOut}
			onMouseOver={handleMouseOver}
			ref={_ref}
			style={{background: colorHex || undefined}}
		>
			<span className="retention">{formatPercent(retention)}</span>

			{ReactDOM.createPortal(
				<Popover
					alignElement={_ref.current!}
					content={content}
					title={sub(Liferay.Language.get('x-cohort'), [
						dateLabelFn(date, true),
					])}
					visible={visible}
				/>,
				document.querySelector('body.dxp') || document.body
			)}
		</td>
	);
};

export default Item;
