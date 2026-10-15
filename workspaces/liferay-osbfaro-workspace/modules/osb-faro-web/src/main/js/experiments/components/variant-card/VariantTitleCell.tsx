import ClayLabel, {LabelDisplayType} from '@clayui/label';
import Popover from 'shared/components/Popover';
import React, {useRef, useState} from 'react';
import ReactDOM from 'react-dom';
import {isEllipisActive} from 'shared/util/util';

interface IVariantTitleProps {
	labels: Array<{value: string; status: string}>;
	title: string;
}

const VariantTitle: React.FC<IVariantTitleProps> = ({labels = [], title}) => {
	const [showPopover, setShowPopover] = useState(false);
	const titleRef = useRef<HTMLDivElement>(null);

	const handleMouseOut = () => setShowPopover(false);
	const handleMouseOver = (event: React.SyntheticEvent) =>
		setShowPopover(isEllipisActive(event));

	return (
		<td className="table-cell-expanded">
			<div
				className="h5 mb-1 text-truncate variant-title"
				onBlur={handleMouseOut}
				onFocus={handleMouseOver}
				onMouseOut={handleMouseOut}
				onMouseOver={handleMouseOver}
				ref={titleRef}
			>
				{title}
			</div>

			{!!labels.length &&
				labels.map(({status, value}, index) => (
					<ClayLabel
						displayType={status as LabelDisplayType}
						inverse
						key={index}
					>
						{value}
					</ClayLabel>
				))}

			{ReactDOM.createPortal(
				<Popover
					alignElement={titleRef.current}
					title={title}
					visible={showPopover}
				/>,
				document.querySelector('body.dxp') ?? document.body
			)}
		</td>
	);
};

export default VariantTitle;
