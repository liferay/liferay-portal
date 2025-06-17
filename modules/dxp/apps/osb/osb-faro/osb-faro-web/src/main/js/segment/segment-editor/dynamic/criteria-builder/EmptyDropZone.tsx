import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React, {Component} from 'react';
import {
	AddProperty,
	withReferencedObjectsConsumer
} from '../context/referencedObjects';
import {compose} from 'redux';
import {
	ConnectDropTarget,
	DropTarget as dropTarget,
	DropTargetMonitor
} from 'react-dnd';
import {DragTypes} from '../utils/drag-types';
import {OnCriterionAdd} from '../utils/types';

/**
 * Prevents items from being dropped from other contributors.
 * This method must be called `canDrop`.
 * @returns {boolean} True if the target should accept the item.
 */
const canDrop = (): boolean => true;

/**
 * Implements the behavior of what will occur when an item is dropped.
 * Adds the criterion dropped.
 * This method must be called `drop`.
 */
const drop = (
	{
		addProperty,
		onCriterionAdd
	}: {
		addProperty: AddProperty;
		onCriterionAdd: OnCriterionAdd;
	},
	monitor: DropTargetMonitor
): void => {
	const {criterion, property} = monitor.getItem();

	if (property) {
		addProperty(property);
	}

	onCriterionAdd(0, criterion);
};

interface IEmptyDropZone extends React.HTMLAttributes<HTMLDivElement> {
	addProperty: AddProperty;
	canDrop: boolean;
	connectDropTarget: ConnectDropTarget;
	hover?: boolean;
	onCriterionAdd: OnCriterionAdd;
}

class EmptyDropZone extends Component<IEmptyDropZone> {
	render() {
		const {canDrop, connectDropTarget, hover} = this.props;

		const emptyZoneClasses = getCN('empty-drop-zone-root', {
			'empty-drop-zone-dashed border-primary rounded': !canDrop || !hover
		});

		const targetClasses = getCN('drop-zone-target p-5', {
			'empty-drop-zone-target-solid dnd-hover border-primary rounded':
				canDrop && hover
		});

		return (
			<div className={emptyZoneClasses}>
				{connectDropTarget(
					<div className={targetClasses}>
						<div className='empty-drop-zone-indicator' />

						<div className='criteria-message'>
							<ClayIcon
								className='icon-root icon-size-md'
								symbol='ac_rule'
							/>

							{Liferay.Language.get(
								'drag-and-drop-criterion-from-the-right-to-add-rules'
							)}
						</div>

						<div className='groups-message'>
							<ClayIcon
								className='icon-root icon-size-md'
								symbol='ac_group'
							/>

							{Liferay.Language.get(
								'drag-and-drop-over-an-existing-criteria-to-form-groups'
							)}
						</div>
					</div>
				)}
			</div>
		);
	}
}

export default compose(
	withReferencedObjectsConsumer,
	dropTarget(
		DragTypes.Property,
		{
			canDrop,
			drop
		},
		(connect, monitor) => ({
			canDrop: monitor.canDrop(),
			connectDropTarget: connect.dropTarget(),
			hover: monitor.isOver()
		})
	)
)(EmptyDropZone);
