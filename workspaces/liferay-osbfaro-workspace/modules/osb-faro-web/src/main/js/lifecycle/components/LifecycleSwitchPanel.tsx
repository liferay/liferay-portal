import LifecycleDetailsPanel from './LifecycleDetailsPanel';
import LifecycleSidePanel from './LifecycleSidePanel';
import React from 'react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {MAX_PAGE_WIDTH} from 'shared/util/constants';
import {SidePanel} from '@clayui/core';
import {useBodyWidthBelow} from 'shared/hooks/useBodyWidthBelow';

export type LifecyclePanelView =
	| {type: 'switch'}
	| {lifecycleId: string; parent?: 'switch'; type: 'details'};

interface ILifecycleSwitchPanelProps {
	containerRef: React.RefObject<HTMLElement>;
	onDefaultChange: (lifecycleId: string) => void;
	onViewChange: (view: LifecyclePanelView | null) => void;
	openLifecycle?: ILifecycleDetail | null;
	triggerRef?: React.RefObject<HTMLElement>;
	view: LifecyclePanelView | null;
}

const LifecycleSwitchPanel: React.FC<ILifecycleSwitchPanelProps> = ({
	containerRef,
	onDefaultChange,
	onViewChange,
	openLifecycle,
	triggerRef,
	view,
}) => {
	const overlay = useBodyWidthBelow(MAX_PAGE_WIDTH, {inclusive: true});

	const backToSwitch = () => onViewChange({type: 'switch'});

	return (
		<LifecycleSidePanel
			containerRef={containerRef}
			onOpenChange={(open) => !open && onViewChange(null)}
			open={!!view}
			overlay={overlay}
			triggerRef={triggerRef}
		>
			{view?.type === 'details' ? (
				<LifecycleDetailsPanel
					key={view.lifecycleId}
					lifecycle={
						!view.parent && openLifecycle?.id === view.lifecycleId
							? openLifecycle
							: undefined
					}
					lifecycleId={view.lifecycleId}
					onBack={view.parent ? backToSwitch : undefined}
					onDefaultChange={onDefaultChange}
					onView={view.parent ? backToSwitch : undefined}
				/>
			) : (
				<>
					<SidePanel.Header
						className="border-bottom"
						messages={{
							closeAriaLabel: Liferay.Language.get('close'),
						}}
					>
						<SidePanel.Title>
							{Liferay.Language.get('switch-lifecycle-dashboard')}
						</SidePanel.Title>
					</SidePanel.Header>

					<SidePanel.Body>{null}</SidePanel.Body>
				</>
			)}
		</LifecycleSidePanel>
	);
};

export default LifecycleSwitchPanel;
