import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import Popover from 'shared/components/Popover';
import React, {useRef, useState} from 'react';
import ReactDOM from 'react-dom';

export interface IInfoPopoverProps {
	className?: string;
	content?: React.ReactText;
	popOverAttr?: {className: string};
	title?: string;
}

const InfoPopover: React.FC<IInfoPopoverProps> = ({
	className,
	content,
	popOverAttr,
	title,
}) => {
	const _iconSpanRef = useRef<HTMLSpanElement>(null);

	const [showPopover, setShowPopover] = useState(false);

	return (
		<>
			<span
				aria-label={title || Liferay.Language.get('more-information')}
				className={getCN('info-popover-root', className)}
				onBlur={() => setShowPopover(false)}
				onFocus={() => setShowPopover(true)}
				onMouseOut={() => setShowPopover(false)}
				onMouseOver={() => setShowPopover(true)}
				ref={_iconSpanRef}
				role="button"
				tabIndex={0}
			>
				<ClayIcon className="icon-root" symbol="question-circle-full" />
			</span>

			{ReactDOM.createPortal(
				<Popover
					alignElement={_iconSpanRef.current!}
					content={content}
					title={title}
					visible={showPopover}
					{...popOverAttr}
				/>,
				document.querySelector('body.dxp') || document.body
			)}
		</>
	);
};

export default InfoPopover;
