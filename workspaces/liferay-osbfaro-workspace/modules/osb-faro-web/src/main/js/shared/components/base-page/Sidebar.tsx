import getCN from 'classnames';
import React, {createContext, useContext, useEffect, useRef} from 'react';
import {FocusTrap, SidePanel} from '@clayui/core';
import {MAX_PAGE_WIDTH} from 'shared/util/constants';
import {useBodyScrollLock} from 'shared/hooks/useBodyScrollLock';
import {useBodyWidthBelow} from 'shared/hooks/useBodyWidthBelow';

const OVERLAY_CLASS_NAME = 'base-page-sidebar-overlay';

const SIDEBAR_WIDTH = 512;

const WIDTH_PROPERTY = '--base-page-sidebar-width';

export const SidebarContainerContext = createContext<
	React.RefObject<HTMLElement>
>({current: null});

/**
 * Whether the sidebar covers the page instead of pushing it aside. The page is
 * too narrow to share with the sidebar at `MAX_PAGE_WIDTH` and below.
 */
export const useSidebarOverlay = () =>
	useBodyWidthBelow(MAX_PAGE_WIDTH, {inclusive: true});

interface ISidebarProps {
	children: React.ReactNode;
	className?: string;
	direction?: 'left' | 'right';
	onOpenChange: (open: boolean) => void;
	open: boolean;
	triggerRef?: React.RefObject<HTMLElement>;
}

const Sidebar: React.FC<ISidebarProps> & {
	Body: typeof SidePanel.Body;
	Header: typeof SidePanel.Header;
	Title: typeof SidePanel.Title;
} = ({
	children,
	className,
	direction = 'right',
	onOpenChange,
	open,
	triggerRef,
}) => {
	const backdropRef = useRef<HTMLDivElement>(null);

	const containerRef = useContext(SidebarContainerContext);

	const overlay = useSidebarOverlay();

	const overlaid = overlay && open;

	useBodyScrollLock(overlaid);

	useEffect(() => {
		const container = containerRef.current;

		if (!container) {
			return;
		}

		container.classList.toggle(OVERLAY_CLASS_NAME, overlay);
		container.style.setProperty(WIDTH_PROPERTY, `${SIDEBAR_WIDTH}px`);

		return () => {
			container.classList.remove(OVERLAY_CLASS_NAME);
			container.style.removeProperty(WIDTH_PROPERTY);
		};
	}, [containerRef, overlay]);

	useEffect(() => {
		if (!overlaid) {
			return;
		}

		// The focus trap makes the backdrop inert, so a click on it lands on
		// whichever of its ancestors is still hit testable.

		const handleMouseDown = ({target}: MouseEvent) => {
			if (
				target instanceof Node &&
				backdropRef.current &&
				target.contains(backdropRef.current)
			) {
				onOpenChange(false);
			}
		};

		document.addEventListener('mousedown', handleMouseDown);

		return () => document.removeEventListener('mousedown', handleMouseDown);
	}, [onOpenChange, overlaid]);

	return (
		<>
			{overlaid && (
				<div
					className="base-page-sidebar-backdrop modal-backdrop show"
					data-testid="base-page-sidebar-backdrop"
					ref={backdropRef}
				/>
			)}

			<SidePanel
				className={getCN('base-page-sidebar', className)}
				containerRef={containerRef}
				direction={direction}
				onOpenChange={onOpenChange}
				open={open}
				panelWidth={SIDEBAR_WIDTH}
				position="fixed"
				triggerRef={triggerRef}
			>
				<FocusTrap active={overlaid}>{children}</FocusTrap>
			</SidePanel>
		</>
	);
};

Sidebar.Body = SidePanel.Body;
Sidebar.Header = SidePanel.Header;
Sidebar.Title = SidePanel.Title;

export default Sidebar;
