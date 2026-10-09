import Cell from './Cell';
import ClayIcon from 'shared/components/Icon';
import ClayTable from '@clayui/table';
import getCN from 'classnames';
import React, {useEffect, useRef, useState} from 'react';
import {DropTargetMonitor, useDrag, useDrop} from 'react-dnd';

const HOVER_TYPES = {
	BOTTOM: 'bottom',
	TOP: 'top',
};

const ITEM_TYPES = {
	ROW: 'row',
};

export type Column = Omit<React.ComponentProps<typeof Cell>, 'data'> & {
	label: string;
	title: string;
};

interface DragItem {
	index: number;
	type: string;
}

interface IRowProps extends React.HTMLAttributes<HTMLElement> {
	columns: Column[];
	data: object;
	draggable: boolean;
	index: number;
	onMove: (from: number, to: number) => void;
}

const Row: React.FC<IRowProps> = ({
	columns,
	data,
	draggable = true,
	index,
	onMove,
}) => {
	const _itemRef = useRef<HTMLTableRowElement>(null);

	const [hoverPosition, setHoverPosition] = useState<string | null>(null);

	const [{canDrop, isOver}, drop] = useDrop({
		accept: ITEM_TYPES.ROW,
		canDrop: (item: DragItem) => {
			const {index: dragIndex} = item;

			const dropIndex = index;

			return draggable && dragIndex !== dropIndex;
		},
		collect: (monitor: DropTargetMonitor) => ({
			canDrop: monitor.canDrop(),
			isOver: monitor.isOver(),
		}),
		drop: (item: DragItem) => {
			const {index: dragIndex} = item;

			let dropIndex = index;

			const insertAbove =
				hoverPosition === HOVER_TYPES.TOP && dragIndex < dropIndex;

			const insertBelow =
				hoverPosition === HOVER_TYPES.BOTTOM && dragIndex > dropIndex;

			if (insertAbove) {
				dropIndex = index - 1;
			}
			else if (insertBelow) {
				dropIndex = index + 1;
			}

			onMove(dragIndex, dropIndex);
		},
		hover: (item: DragItem, monitor: DropTargetMonitor) => {
			const {index: dragIndex} = item;

			const hoverIndex = index;

			if (_itemRef.current) {
				const {bottom, height} =
					_itemRef.current.getBoundingClientRect();

				const targetMiddleY = height / 2;

				const clientOffset = monitor.getClientOffset();

				if (!clientOffset) {
					return;
				}

				const {y} = clientOffset;

				const hoverAbove = y < bottom - targetMiddleY;

				const destIndex = hoverAbove ? hoverIndex - 1 : hoverIndex + 1;

				if (destIndex === dragIndex) {
					setHoverPosition(null);
				}
				else if (hoverAbove) {
					setHoverPosition(HOVER_TYPES.TOP);
				}
				else {
					setHoverPosition(HOVER_TYPES.BOTTOM);
				}
			}
		},
	});

	const [{isDragging}, drag, preview] = useDrag({
		collect: (monitor: any) => ({
			isDragging: monitor.isDragging(),
		}),
		item: {
			index,
			type: ITEM_TYPES.ROW,
		},
	});

	useEffect(() => {
		drop(preview(_itemRef));
	}, [drop, preview]);

	return (
		<ClayTable.Row
			className={getCN('table-row', {
				dragging: isDragging,
				[`hover-${hoverPosition}`]: isOver && canDrop && hoverPosition,
			})}
			ref={_itemRef}
		>
			<ClayTable.Cell>
				{draggable && (
					<div className="drag-handle" ref={drag}>
						<ClayIcon className="icon-root" symbol="drag" />
					</div>
				)}
			</ClayTable.Cell>

			{columns.map(
				(
					{
						accessor,
						cellRenderer,
						cellRendererProps,
						className,
						dataFormatter,
					},
					i
				) => (
					<Cell
						accessor={accessor}
						cellRenderer={cellRenderer}
						cellRendererProps={cellRendererProps}
						className={className}
						data={data}
						dataFormatter={dataFormatter}
						key={i}
					/>
				)
			)}
		</ClayTable.Row>
	);
};

export default Row;
