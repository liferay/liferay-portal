import DndProvider from 'shared/components/DndProvider';
import React, {useEffect, useRef} from 'react';
import {AnnounceProvider} from 'shared/components/condition-chip/AnnounceContext';
import {Heading} from '@clayui/core';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {mergeRef} from 'shared/util/util';
import {useDragLayer, useDrop} from 'react-dnd';

interface IConditionsDropAreaProps {
	children: React.ReactNode;
	dragTypes: string[];
}

const ConditionsDropArea: React.FC<IConditionsDropAreaProps> = ({
	children,
	dragTypes,
}) => {
	const panelRef = useRef<HTMLDivElement>(null);

	const [, drop] = useDrop({
		accept: dragTypes,
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
		<div className="conditions-panel h-100" ref={mergeRef(drop, panelRef)}>
			{children}
		</div>
	);
};

interface IConditionsPanelProps {
	children: React.ReactNode;
	dragTypes: string[];
	title: string;
}

const ConditionsPanel: React.FC<IConditionsPanelProps> = ({
	children,
	dragTypes,
	title,
}) => (
	<DndProvider backend={HTML5Backend}>
		<ConditionsDropArea dragTypes={dragTypes}>
			<div className="d-flex flex-column h-100" data-report-expand>
				<div className="flex-shrink-0 px-4 py-3">
					<Heading fontSize={6} level={2} weight="semi-bold">
						{title}
					</Heading>
				</div>

				<div className="flex-grow-1 overflow-auto" data-report-expand>
					<AnnounceProvider>{children}</AnnounceProvider>
				</div>
			</div>
		</ConditionsDropArea>
	</DndProvider>
);

export default ConditionsPanel;
