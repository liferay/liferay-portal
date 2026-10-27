import {ClayButtonWithIcon} from '@clayui/button';
import getCN from 'classnames';
import React, {useEffect, useRef, useState} from 'react';
import SidePanelBackdrop from 'shared/components/SidePanelBackdrop';
import {SidePanel} from '@clayui/core';
import {sub} from 'shared/util/lang';
import {useIsMobile} from 'shared/hooks/useIsMobile';

// Matches `$criteriaSidebarWidth`, overriding Clay's narrower mobile default.

const PANEL_WIDTH = 320;

interface ISidebarProps {
	children?: React.ReactNode;
	className?: string;
	label: string;
	position?: 'left' | 'right';
}

const OverlaySidebar: React.FC<ISidebarProps> = ({
	children,
	className,
	label,
	position = 'left',
}) => {
	const [open, setOpen] = useState(false);

	const containerRef = useRef<HTMLDivElement>(null);
	const triggerRef = useRef<HTMLButtonElement>(null);

	const wasOpenRef = useRef(open);

	useEffect(() => {
		if (wasOpenRef.current && !open) {
			triggerRef.current?.focus();
		}

		wasOpenRef.current = open;
	}, [open]);

	const openLabel = sub(Liferay.Language.get('open-x'), [label]) as string;

	const panelId = `baseEditPageSidebar-${position}`;

	return (
		<div className="base-edit-page-sidebar-overlay" ref={containerRef}>
			<ClayButtonWithIcon
				aria-controls={panelId}
				aria-expanded={open}
				aria-label={openLabel}
				className="base-edit-page-sidebar-trigger"
				data-tooltip-align="bottom"
				displayType="secondary"
				monospaced
				onClick={() => setOpen(true)}
				ref={triggerRef}
				size="sm"
				symbol={
					position === 'left'
						? 'angle-double-right'
						: 'angle-double-left'
				}
				title={openLabel}
			/>

			<SidePanel
				aria-label={label}
				className={getCN('base-edit-page-sidebar-panel', className)}
				closeOnEscape
				containerRef={containerRef}
				direction={position}
				id={panelId}
				onOpenChange={setOpen}
				open={open}
				panelWidth={PANEL_WIDTH}
				position="fixed"
				triggerRef={triggerRef}
			>
				{open && <SidePanelBackdrop onClick={() => setOpen(false)} />}

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('close')}
					borderless
					className="base-edit-page-sidebar-close"
					displayType="secondary"
					monospaced
					onClick={() => setOpen(false)}
					size="sm"
					symbol="times"
					title={Liferay.Language.get('close')}
				/>

				{children}
			</SidePanel>
		</div>
	);
};

/**
 * Must be placed inside BaseEditPage.Body. From 768px up it sits beside the
 * content and pushes it; below that it collapses into an icon button, with an
 * "Open <label>" tooltip, that opens it as an overlay. Render the sidebar's own
 * heading inside it, next to the overlay's close button, and avoid sticky
 * positioning in it, since both the overlay and the side column, which stays
 * under the toolbar, scroll on their own.
 */
const Sidebar: React.FC<ISidebarProps> = (props) => {
	const isMobile = useIsMobile();

	if (isMobile) {
		return <OverlaySidebar {...props} />;
	}

	const {children, className, position = 'left'} = props;

	return (
		<aside
			className={getCN(
				'base-edit-page-sidebar bg-white',
				`base-edit-page-sidebar-${position}`,
				{
					'border-left': position === 'right',
					'border-right': position === 'left',
				},
				className
			)}
		>
			{children}
		</aside>
	);
};

export default Sidebar;
