import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React from 'react';
import {ConnectDragSource, DragSource as dragSource} from 'react-dnd';
import {Criterion} from '../utils/types';
import {DragTypes} from '../utils/drag-types';
import {generateRowId} from '../utils/utils';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../utils/constants';

const TYPE_ICON_MAP = {
	[PropertyTypes.Behavior]: 'web-content',
	[PropertyTypes.Boolean]: 'check',
	[PropertyTypes.AccountNumber]: 'integer',
	[PropertyTypes.AccountText]: 'text',
	[PropertyTypes.Date]: 'date',
	[PropertyTypes.DateTime]: 'date',
	[PropertyTypes.Duration]: 'time',
	[PropertyTypes.Event]: 'ac_event_analysis',
	[PropertyTypes.Number]: 'integer',
	[PropertyTypes.OrganizationBoolean]: 'check',
	[PropertyTypes.OrganizationDate]: 'date',
	[PropertyTypes.OrganizationDateTime]: 'date',
	[PropertyTypes.OrganizationNumber]: 'integer',
	[PropertyTypes.OrganizationSelectText]: 'text',
	[PropertyTypes.OrganizationText]: 'text',
	[PropertyTypes.SessionDateTime]: 'date',
	[PropertyTypes.SessionNumber]: 'integer',
	[PropertyTypes.SessionText]: 'text',
	[PropertyTypes.Interest]: 'check',
	[PropertyTypes.Text]: 'text'
};

/**
 * Passes the required values to the drop target.
 * This method must be called `beginDrag`.
 * @param {Object} props Component's current props
 * @returns {Object} The props to be passed to the drop target.
 */
const beginDrag = ({
	defaultValue,
	name,
	property,
	type
}: {
	defaultValue: any;
	name: string;
	property: Property;
	type: PropertyTypes;
}): {
	criterion: Criterion;
	property: Property;
} => {
	let touched: boolean | object = false;
	let valid: boolean | object = true;

	if (type === PropertyTypes.Behavior) {
		touched = {asset: false, dateFilter: false, occurenceCount: false};
		valid = {asset: false, dateFilter: true, occurenceCount: true};
	} else if (type === PropertyTypes.Event) {
		touched = {
			attributeValue: false,
			occurenceCount: false
		};
		valid = {
			attributeValue: false,
			occurenceCount: true
		};
	} else if (type === PropertyTypes.SessionGeolocation) {
		touched = {country: false, dateFilter: false};
		valid = {country: false, dateFilter: true};
	} else if (
		[PropertyTypes.SessionNumber, PropertyTypes.SessionText].includes(type)
	) {
		touched = {customInput: false, dateFilter: false};
		valid = {customInput: false, dateFilter: true};
	} else if (
		[
			PropertyTypes.AccountNumber,
			PropertyTypes.AccountText,
			PropertyTypes.Duration,
			PropertyTypes.Number,
			PropertyTypes.OrganizationNumber,
			PropertyTypes.OrganizationSelectText,
			PropertyTypes.OrganizationText,
			PropertyTypes.SelectText,
			PropertyTypes.Text
		].includes(type)
	) {
		valid = false;
	}

	return {
		criterion: {
			defaultValue,
			propertyName: name,
			rowId: generateRowId(),
			touched,
			type,
			valid
		},
		property
	};
};

interface ICriteriaSidebarItemProps {
	className: string;
	connectDragSource: ConnectDragSource;
	defaultValue: any;
	dragging: boolean;
	label: string;
	name: string;
	property: Property;
	propertyKey: string;
	type: string;
}

export class CriteriaSidebarItem extends React.Component<ICriteriaSidebarItemProps> {
	render() {
		const {
			className,
			connectDragSource,
			dragging,
			label,
			type
		} = this.props;

		const classes = getCN(
			'criteria-sidebar-item-root',
			{dragging},
			className
		);

		return connectDragSource(
			<li className={classes} data-testid={`criteria-item-${label}`}>
				<span className='inline-item'>
					<ClayIcon className='icon-root' symbol='drag' />
				</span>

				<span className='criteria-sidebar-item-type sticker'>
					<span className='inline-item'>
						<ClayIcon
							className='icon-root'
							symbol={TYPE_ICON_MAP[type] || 'text'}
						/>
					</span>
				</span>

				{label}
			</li>
		);
	}
}

export default dragSource(
	DragTypes.Property,
	{
		beginDrag
	},
	(connect, monitor) => ({
		connectDragSource: connect.dragSource(),
		dragging: monitor.isDragging()
	})
)(CriteriaSidebarItem);
