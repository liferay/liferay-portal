import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';

interface ICollapsibleOverlayProps extends React.HTMLFactory<HTMLElement> {
	children?: React.ReactNode;
	onClose: (event: React.MouseEvent<HTMLButtonElement>) => void;
	title: string;
	visible: boolean;
}

const CollapsibleOverlay: React.FC<ICollapsibleOverlayProps> = ({
	children,
	onClose,
	title = '',
	visible = false,
}) => (
	<div
		className={getCN('collapsible-overlay-root', {
			hidden: !visible,
		})}
		hidden={!visible}
	>
		<div className="content-wrapper">
			<div className="header">
				<h3>{title}</h3>

				<ClayButton
					aria-label={Liferay.Language.get('close')}
					className="button-root"
					displayType="unstyled"
					onClick={onClose}
				>
					<ClayIcon className="icon-root" symbol="times" />
				</ClayButton>
			</div>

			{children}
		</div>
	</div>
);

export default CollapsibleOverlay;
