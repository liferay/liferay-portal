import ClayIcon from '@clayui/icon';
import React, {FC} from 'react';
import {ClayButtonWithIcon} from '@clayui/button';

const RowMain: FC<{
	children: React.ReactNode;
	expanded: boolean;
	infoButton?: boolean;
	onToggle: () => void;
}> = ({children, expanded, infoButton, onToggle}) =>
	infoButton ? (
		<div className="row-main d-flex align-items-start">
			{children}

			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('show-payload')}
				borderless
				className="payload-button ml-3 flex-shrink-0"
				displayType="secondary"
				onClick={onToggle}
				size="sm"
				symbol="info-circle"
			/>
		</div>
	) : (
		<div
			className="row-main clickable d-flex align-items-start"
			onClick={onToggle}
			onKeyPress={onToggle}
			role="button"
			tabIndex={0}
		>
			{children}

			<ClayIcon
				className="angle-icon icon-root ml-3 flex-shrink-0 text-secondary"
				symbol={expanded ? 'angle-up' : 'angle-down'}
			/>
		</div>
	);

export default RowMain;
