import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import React from 'react';
import {
	ConnectDragPreview,
	ConnectDragSource,
	DragSource as dragSource,
} from 'react-dnd';
import {Criterion} from '../utils/types';
import {DragTypes} from '../utils/drag-types';
import {generateRowId} from '../utils/utils';
import {getEmptyImage} from 'react-dnd-html5-backend';
import {getStickerStyle} from './stickerColors';
import {KeyboardMovementContext} from '../context/keyboardMovement';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../utils/constants';
import {sub} from 'shared/util/lang';

const TYPE_ICON_MAP = {
	[PropertyTypes.Behavior]: 'click',
	[PropertyTypes.Boolean]: 'check',
	[PropertyTypes.AccountDate]: 'date',
	[PropertyTypes.AccountSelectText]: 'text',
	[PropertyTypes.AccountNumber]: 'integer',
	[PropertyTypes.AccountText]: 'text',
	[PropertyTypes.Date]: 'date',
	[PropertyTypes.DateTime]: 'date',
	[PropertyTypes.Duration]: 'time',
	[PropertyTypes.Event]: 'click',
	[PropertyTypes.Number]: 'integer',
	[PropertyTypes.OrganizationBoolean]: 'check',
	[PropertyTypes.OrganizationDate]: 'date',
	[PropertyTypes.OrganizationDateTime]: 'date',
	[PropertyTypes.OrganizationNumber]: 'integer',
	[PropertyTypes.OrganizationSelectText]: 'text',
	[PropertyTypes.OrganizationText]: 'text',
	[PropertyTypes.SessionChannel]: 'check',
	[PropertyTypes.SessionDateTime]: 'date',
	[PropertyTypes.SessionNumber]: 'integer',
	[PropertyTypes.SessionText]: 'text',
	[PropertyTypes.SessionUtmParameter]: 'text',
	[PropertyTypes.Vocabulary]: 'text',
	[PropertyTypes.Interest]: 'check',
	[PropertyTypes.SearchTerm]: 'check',
	[PropertyTypes.Tag]: 'text',
	[PropertyTypes.Text]: 'text',
};

export const getTypeIcon = (type: string): string =>
	TYPE_ICON_MAP[type as keyof typeof TYPE_ICON_MAP] || 'text';

/**
 * Passes the required values to the drop target.
 * This method must be called `beginDrag`.
 * @param {Object} props Component's current props
 * @returns {Object} The props to be passed to the drop target.
 */
export const beginDrag = ({
	defaultValue,
	name,
	property,
	type,
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

		// asset starts invalid: a behavior criterion requires the user to select
		// an asset type (or Page) before the segment can be saved.

		valid = {asset: false, dateFilter: true, occurenceCount: true};
	}
	else if (type === PropertyTypes.Event) {
		touched = {occurenceCount: false};
		valid = {occurenceCount: true};
	}
	else if (type === PropertyTypes.SessionGeolocation) {
		touched = {country: false, dateFilter: false};
		valid = {country: false, dateFilter: true};
	}
	else if (type === PropertyTypes.SessionChannel) {

		// Unlike UTM Parameter, Channel's default value is already a
		// concrete option (the first CHANNEL_OPTIONS entry), so it starts
		// valid without requiring the user to touch the picker first.

		touched = {customInput: false};
		valid = {customInput: true};
	}
	else if (type === PropertyTypes.SessionUtmParameter) {
		touched = {customInput: false};
		valid = {customInput: false};
	}
	else if (
		[PropertyTypes.SessionNumber, PropertyTypes.SessionText].includes(type)
	) {
		touched = {customInput: false, dateFilter: false};
		valid = {customInput: false, dateFilter: true};
	}
	else if (
		[
			PropertyTypes.AccountSelectText,
			PropertyTypes.AccountNumber,
			PropertyTypes.AccountText,
			PropertyTypes.Duration,
			PropertyTypes.Number,
			PropertyTypes.OrganizationNumber,
			PropertyTypes.OrganizationSelectText,
			PropertyTypes.OrganizationText,
			PropertyTypes.SelectText,
			PropertyTypes.Text,
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
			valid,
		},
		property,
	};
};

interface ICriteriaSidebarItemProps {
	className?: string;
	connectDragPreview?: ConnectDragPreview;
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
	static contextType = KeyboardMovementContext;

	constructor(props: ICriteriaSidebarItemProps) {
		super(props);

		this.handleKeyDown = this.handleKeyDown.bind(this);
	}

	componentDidMount() {
		const {connectDragPreview} = this.props;

		if (connectDragPreview) {
			connectDragPreview(getEmptyImage(), {captureDraggingState: true});
		}
	}

	declare context: React.ContextType<typeof KeyboardMovementContext>;

	handleKeyDown(event: React.KeyboardEvent<HTMLLIElement>) {
		const {startMovement} = this.context;

		if (
			!startMovement ||
			event.target !== event.currentTarget ||
			(event.key !== 'Enter' && event.key !== ' ')
		) {
			return;
		}

		event.preventDefault();

		const {defaultValue, name, property, type} = this.props;

		startMovement(
			beginDrag({
				defaultValue,
				name,
				property,
				type: type as PropertyTypes,
			})
		);
	}

	render() {
		const {
			className,
			connectDragSource,
			dragging,
			label,
			name,
			propertyKey,
			type,
		} = this.props;

		const {source} = this.context;

		const movementSource =
			!!source &&
			source.criterion.propertyName === name &&
			source.property.propertyKey === propertyKey;

		const classes = getCN(
			'align-items-center c-gap-3 criteria-sidebar-item-root d-flex mb-1 mx-4 p-2 text-4',
			{dragging: dragging || movementSource},
			className
		);

		return connectDragSource(
			<li
				aria-label={
					sub(Liferay.Language.get('add-x'), [label]) as string
				}
				className={classes}
				data-testid={`criteria-item-${label}`}
				onKeyDown={this.handleKeyDown}
				role="menuitem"
				tabIndex={-1}
			>
				<ClaySticker
					className="criteria-sidebar-item-sticker flex-shrink-0"
					style={getStickerStyle(propertyKey)}
				>
					<ClayIcon symbol={getTypeIcon(type)} />
				</ClaySticker>

				<span className="autofit-col-expand">{label}</span>

				<ClayIcon
					aria-hidden="true"
					className="criteria-sidebar-item-add flex-shrink-0 mr-2 text-secondary"
					symbol="plus"
				/>
			</li>
		);
	}
}

export default dragSource(
	DragTypes.Property,
	{
		beginDrag,
	},
	(connect, monitor) => ({
		connectDragPreview: connect.dragPreview(),
		connectDragSource: connect.dragSource(),
		dragging: monitor.isDragging(),
	})
)(CriteriaSidebarItem);
