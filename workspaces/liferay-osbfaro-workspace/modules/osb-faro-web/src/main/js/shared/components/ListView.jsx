import autobind from 'autobind-decorator';
import Checkbox from './Checkbox';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import ListGroup from './list-group';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import RadioGroup from './RadioGroup';
import React from 'react';
import {isNil, noop} from 'lodash';
import {PropTypes} from 'prop-types';
import {Set} from 'immutable';
import {toggle, toggleSingleton} from '../util/set';

class QuickAction extends React.Component {
	static propTypes = {
		item: PropTypes.object,
		onClick: PropTypes.func,
		symbol: PropTypes.string.isRequired
	};

	@autobind
	handleClick() {
		const {item, onClick} = this.props;

		if (onClick) {
			onClick(item);
		}
	}

	render() {
		const {className, symbol, ...otherProps} = this.props;

		return (
			<ClayButton
				{...omitDefinedProps(otherProps, QuickAction.propTypes)}
				className={getCN('button-root quick-action-item', className)}
				displayType='unstyled'
				onClick={this.handleClick}
			>
				<ClayIcon className='icon-root' symbol={symbol} />
			</ClayButton>
		);
	}
}

class Item extends React.Component {
	static defaultProps = {
		disabled: false
	};

	static propTypes = {
		disabled: PropTypes.bool,
		item: PropTypes.object,
		itemRenderer: PropTypes.func,
		onClick: PropTypes.func,
		quickActions: PropTypes.arrayOf(PropTypes.object),
		selected: PropTypes.bool,
		selectMultiple: PropTypes.bool
	};

	@autobind
	handleClick(event) {
		const {item, onClick} = this.props;

		event.preventDefault();

		onClick(item);
	}

	render() {
		const {
			className,
			disabled,
			item,
			itemRenderer: ItemInternalComponent,
			quickActions,
			selectMultiple,
			selected
		} = this.props;

		return (
			<ListGroup.Item
				active={selected}
				className={className}
				flex
				onClick={this.handleClick}
			>
				{!isNil(selected) && (
					<ListGroup.ItemField>
						{selectMultiple ? (
							<Checkbox
								checked={selected}
								disabled={disabled}
								onChange={this.handleClick}
							/>
						) : (
							<RadioGroup.Option
								checked={selected}
								disabled={disabled}
								name='list'
								onClick={this.handleClick}
							/>
						)}
					</ListGroup.ItemField>
				)}

				<ItemInternalComponent item={item} />

				{quickActions && !!quickActions.length && (
					<ListGroup.ItemField>
						<div className='quick-action-menu'>
							{quickActions.map(action => (
								<QuickAction
									{...action}
									item={item}
									key={item.id}
								/>
							))}
						</div>
					</ListGroup.ItemField>
				)}
			</ListGroup.Item>
		);
	}
}

class ListView extends React.Component {
	static defaultProps = {
		disabledItemsISet: new Set(),
		onClick: noop,
		quickActions: [],
		selectedItemsISet: new Set(),
		selectMultiple: true
	};

	static propTypes = {
		disabledItemsISet: PropTypes.instanceOf(Set),
		itemRenderer: PropTypes.func.isRequired,
		items: PropTypes.array,
		onClick: PropTypes.func,
		onSelectItemsChange: PropTypes.func,
		quickActions: PropTypes.arrayOf(PropTypes.object),
		selectedItemsISet: PropTypes.instanceOf(Set),
		selectMultiple: PropTypes.bool
	};

	@autobind
	handleItemClick(item) {
		const {
			disabledItemsISet,
			onClick,
			onSelectItemsChange,
			selectMultiple,
			selectedItemsISet
		} = this.props;

		if (onSelectItemsChange && !disabledItemsISet.has(item.id)) {
			onSelectItemsChange(
				selectMultiple
					? toggle(selectedItemsISet, item.id)
					: toggleSingleton(selectedItemsISet, item.id)
			);
		}

		onClick(item);
	}

	render() {
		const {
			children,
			disabledItemsISet,
			itemRenderer,
			items,
			onSelectItemsChange,
			quickActions,
			selectMultiple,
			selectedItemsISet,
			...otherProps
		} = this.props;

		return (
			<ListGroup {...omitDefinedProps(otherProps, ListView.propTypes)}>
				{children}

				{items &&
					items.map(item => (
						<Item
							disabled={disabledItemsISet.has(item.id)}
							item={item}
							itemRenderer={itemRenderer}
							key={item.id}
							onClick={this.handleItemClick}
							quickActions={quickActions}
							selected={
								onSelectItemsChange
									? selectedItemsISet.has(item.id)
									: null
							}
							selectMultiple={selectMultiple}
						/>
					))}
			</ListGroup>
		);
	}
}

export default ListView;
