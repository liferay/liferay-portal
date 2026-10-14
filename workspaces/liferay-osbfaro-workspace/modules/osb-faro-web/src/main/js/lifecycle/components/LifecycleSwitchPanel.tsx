import LifecycleSidePanel from './LifecycleSidePanel';
import React from 'react';
import {MAX_PAGE_WIDTH} from 'shared/util/constants';
import {SidePanel} from '@clayui/core';
import {useBodyWidthBelow} from 'shared/hooks/useBodyWidthBelow';

export type LifecyclePanelView = {type: 'switch'};

interface ILifecycleSwitchPanelProps {
	containerRef: React.RefObject<HTMLElement>;
	onViewChange: (view: LifecyclePanelView | null) => void;
	triggerRef?: React.RefObject<HTMLElement>;
	view: LifecyclePanelView | null;
}

const LifecycleSwitchPanel: React.FC<ILifecycleSwitchPanelProps> = ({
	containerRef,
	onViewChange,
	triggerRef,
	view,
}) => {
	const overlay = useBodyWidthBelow(MAX_PAGE_WIDTH, {inclusive: true});

	return (
		<LifecycleSidePanel
			containerRef={containerRef}
			onOpenChange={(open) => !open && onViewChange(null)}
			open={!!view}
			overlay={overlay}
			triggerRef={triggerRef}
		>
			<SidePanel.Header
				className="border-bottom"
				messages={{closeAriaLabel: Liferay.Language.get('close')}}
			>
				<SidePanel.Title>
					{Liferay.Language.get('switch-lifecycle-dashboard')}
				</SidePanel.Title>
			</SidePanel.Header>

			<SidePanel.Body>{null}</SidePanel.Body>
		</LifecycleSidePanel>
	);
};

export default LifecycleSwitchPanel;
