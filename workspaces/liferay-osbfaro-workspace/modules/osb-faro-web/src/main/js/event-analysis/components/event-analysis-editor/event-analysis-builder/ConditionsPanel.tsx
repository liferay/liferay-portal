import DndProvider from 'shared/components/DndProvider';
import EventAnalysisBuilder from './index';
import React, {useEffect, useRef} from 'react';
import {AnnounceProvider} from 'shared/components/condition-chip/AnnounceContext';
import {Event} from 'event-analysis/utils/types';
import {Heading} from '@clayui/core';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {SortableChipTypes} from './SortableChipTypes';
import {useDragLayer, useDrop} from 'react-dnd';
import {mergeRef} from 'shared/util/util';

const ConditionsDropArea: React.FC<{children: React.ReactNode}> = ({
	children,
}) => {
	const panelRef = useRef<HTMLDivElement>(null);

	const [, drop] = useDrop({
		accept: [SortableChipTypes.Breakdown, SortableChipTypes.Filter],
	});

	const dragging = useDragLayer((monitor) => monitor.isDragging());

	useEffect(() => {
		if (!dragging) {
			return;
		}

		const handleDragOver = (event: DragEvent) => {
			if (panelRef.current?.contains(event.target as Node)) {
				return;
			}

			event.stopPropagation();

			if (event.dataTransfer) {
				event.dataTransfer.dropEffect = 'none';
			}
		};

		window.addEventListener('dragover', handleDragOver, true);

		return () =>
			window.removeEventListener('dragover', handleDragOver, true);
	}, [dragging]);

	return (
		<div
			className="event-analysis-conditions-panel h-100"
			ref={mergeRef(drop, panelRef)}
		>
			{children}
		</div>
	);
};

interface IConditionsPanelProps {
	event?: Event;
	onEventChange: (event: Event | null) => void;
}

const ConditionsPanel: React.FC<IConditionsPanelProps> = ({
	event,
	onEventChange,
}) => (
	<DndProvider backend={HTML5Backend}>
		<ConditionsDropArea>
			<div
				className="d-flex event-analysis-conditions-panel-content flex-column"
				data-report-expand
			>
				<div className="flex-shrink-0 px-4 py-3">
					<Heading fontSize={6} level={2} weight="semi-bold">
						{Liferay.Language.get('conditions-library')}
					</Heading>
				</div>

				<div className="flex-grow-1 overflow-auto" data-report-expand>
					<AnnounceProvider>
						<EventAnalysisBuilder
							event={event}
							onEventChange={onEventChange}
						/>
					</AnnounceProvider>
				</div>
			</div>
		</ConditionsDropArea>
	</DndProvider>
);

export default ConditionsPanel;
