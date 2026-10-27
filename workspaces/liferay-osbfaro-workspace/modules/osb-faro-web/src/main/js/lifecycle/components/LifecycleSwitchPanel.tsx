import BasePage from 'shared/components/base-page';
import LifecycleDetailsPanel from './LifecycleDetailsPanel';
import LifecycleSwitchList from './LifecycleSwitchList';
import React from 'react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {useSidebarOverlay} from 'shared/components/base-page/Sidebar';

export type LifecyclePanelView =
	| {type: 'switch'}
	| {lifecycleId: string; parent?: 'switch'; type: 'details'};

interface ILifecycleSwitchPanelProps {
	defaultChangeCount: number;
	onDefaultChange: (lifecycleId: string) => void;
	onViewChange: (view: LifecyclePanelView | null) => void;
	openLifecycle?: ILifecycleDetail | null;
	triggerRef?: React.RefObject<HTMLElement>;
	view: LifecyclePanelView | null;
}

const LifecycleSwitchPanel: React.FC<ILifecycleSwitchPanelProps> = ({
	defaultChangeCount,
	onDefaultChange,
	onViewChange,
	openLifecycle,
	triggerRef,
	view,
}) => {
	const overlay = useSidebarOverlay();

	const backToSwitch = () => onViewChange({type: 'switch'});

	const closeOverlay = () => {
		if (overlay) {
			onViewChange(null);
		}
	};

	const viewFromDetails = () =>
		overlay ? onViewChange(null) : backToSwitch();

	return (
		<BasePage.Sidebar
			onOpenChange={(open) => !open && onViewChange(null)}
			open={!!view}
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
					onView={
						view.parent && view.lifecycleId !== openLifecycle?.id
							? viewFromDetails
							: undefined
					}
				/>
			) : (
				<>
					<BasePage.Sidebar.Header
						className="border-bottom"
						messages={{
							closeAriaLabel: Liferay.Language.get('close'),
						}}
					>
						<BasePage.Sidebar.Title>
							{Liferay.Language.get('switch-lifecycle-dashboard')}
						</BasePage.Sidebar.Title>
					</BasePage.Sidebar.Header>

					<BasePage.Sidebar.Body>
						{view && (
							<LifecycleSwitchList
								defaultChangeCount={defaultChangeCount}
								onDefaultChange={onDefaultChange}
								onNavigate={closeOverlay}
								onShowDetails={(lifecycleId) =>
									onViewChange({
										lifecycleId,
										parent: 'switch',
										type: 'details',
									})
								}
								openLifecycleERC={
									openLifecycle?.externalReferenceCode
								}
							/>
						)}
					</BasePage.Sidebar.Body>
				</>
			)}
		</BasePage.Sidebar>
	);
};

export default LifecycleSwitchPanel;
