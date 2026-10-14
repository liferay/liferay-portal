import React, {useEffect, useRef} from 'react';
import {FocusTrap, SidePanel} from '@clayui/core';
import {useBodyScrollLock} from 'lifecycle/hooks/useBodyScrollLock';

const LIFECYCLE_SIDE_PANEL_WIDTH = 512;

interface ILifecycleSidePanelProps {
	children: React.ReactNode;
	containerRef: React.RefObject<HTMLElement>;
	onOpenChange: (open: boolean) => void;
	open: boolean;
	overlay: boolean;
	triggerRef?: React.RefObject<HTMLElement>;
}

const LifecycleSidePanel: React.FC<ILifecycleSidePanelProps> = ({
	children,
	containerRef,
	onOpenChange,
	open,
	overlay,
	triggerRef,
}) => {
	const backdropRef = useRef<HTMLDivElement>(null);

	const overlaid = overlay && open;

	useBodyScrollLock(overlaid);

	useEffect(() => {
		if (!overlaid) {
			return;
		}

		const handleMouseDown = ({target}: MouseEvent) => {
			if (
				target === backdropRef.current ||
				target === document.body ||
				target === document.documentElement
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
					className="lifecycle-side-panel-backdrop modal-backdrop show"
					data-testid="lifecycle-side-panel-backdrop"
					ref={backdropRef}
				/>
			)}

			<SidePanel
				className="lifecycle-side-panel"
				containerRef={containerRef}
				direction="right"
				onOpenChange={onOpenChange}
				open={open}
				panelWidth={LIFECYCLE_SIDE_PANEL_WIDTH}
				position="fixed"
				triggerRef={triggerRef}
			>
				<FocusTrap active={overlaid}>{children}</FocusTrap>
			</SidePanel>
		</>
	);
};

export default LifecycleSidePanel;
