import ConditionChip from './ConditionChip';
import getCN from 'classnames';
import React from 'react';
import useSortableChip, {DragStates} from './useSortableChip';
import {ClayButtonWithIcon} from '@clayui/button';
import {IKeyboardReorderProps} from './useKeyboardReorder';
import {sub} from 'shared/util/lang';

interface ISortableConditionChipProps
	extends React.ComponentPropsWithoutRef<typeof ConditionChip> {
	dragType: string;
	index: number;
	keyboard?: IKeyboardReorderProps;
	onMove: (params: {from: number; to: number}) => void;
}

const SortableConditionChip: React.FC<ISortableConditionChipProps> = ({
	dragType,
	index,
	keyboard,
	onMove,
	...otherProps
}) => {
	const {chipRef, containerRef, dragState, hoverPosition} = useSortableChip({
		index,
		onMove,
		type: dragType,
	});

	const indicator = hoverPosition ?? keyboard?.indicator;

	return (
		<div
			className={getCN('sortable-condition-chip', {
				[`hover-${indicator}`]: indicator,
			})}
			ref={containerRef}
		>
			<ConditionChip
				{...otherProps}
				dragState={
					dragState ??
					(keyboard?.moving ? DragStates.Placeholder : undefined)
				}
				handle={
					<ClayButtonWithIcon
						{...keyboard?.handleProps}
						aria-label={
							sub(Liferay.Language.get('drag-x'), [
								otherProps.name,
							]) as string
						}
						className="drag-handle flex-shrink-0 ml-1"
						data-chip-control
						data-html2canvas-ignore
						displayType="unstyled"
						monospaced
						onClick={(event) => event.stopPropagation()}
						size="xs"
						symbol="drag"
						tabIndex={-1}
					/>
				}
				ref={chipRef}
			/>
		</div>
	);
};

export default SortableConditionChip;
