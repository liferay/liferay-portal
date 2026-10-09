import autobind from 'autobind-decorator';
import ClayBadge from '@clayui/badge';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';
import ReactDOM from 'react-dom';
import TextTruncate from 'shared/components/TextTruncate';
import {getDeviceLabel} from 'shared/util/lang';
import {PropTypes} from 'prop-types';

const CLASSNAME = 'analytics-dropdown-menu';
const DEBOUNCE = 500;
let timeout;

export class InputItem extends React.Component {
	static propTypes = {
		index: PropTypes.number,
		item: PropTypes.object,
		onMouseOutSubitem: PropTypes.func,
		onMouseOverSubitem: PropTypes.func,
		onSelectItemsChange: PropTypes.func,
		parentName: PropTypes.string
	};

	/**
	 * Handle Item Input Change
	 * @param {number} index
	 */
	@autobind
	handleItemInputChange(e) {
		const {item, onSelectItemsChange} = this.props;

		const {checked} = e.currentTarget;

		onSelectItemsChange({
			dropdownItem: {...item, checked}
		});
	}

	@autobind
	handleMouseOutSubitem() {
		const {index, onMouseOutSubitem} = this.props;

		onMouseOutSubitem(index);
	}

	@autobind
	handleMouseOverSubitem() {
		const {index, onMouseOverSubitem} = this.props;

		onMouseOverSubitem(index);
	}

	render() {
		const {
			item: {checked, inputType, label, name, value},
			parentName
		} = this.props;

		return (
			<li
				className='dropdown-item dropdown-item-custom'
				onBlur={this.handleMouseOutSubitem}
				onFocus={this.handleMouseOverSubitem}
				onMouseOut={this.handleMouseOutSubitem}
				onMouseOver={this.handleMouseOverSubitem}
			>
				<div className={`custom-control custom-${inputType}`}>
					<label>
						<input
							checked={checked}
							className='custom-control-input'
							name={parentName || name}
							onChange={this.handleItemInputChange}
							type={inputType}
							value={value}
						/>

						<span className='custom-control-label'>
							<TextTruncate
								className='custom-control-label-text'
								title={getDeviceLabel(label) || label}
							/>
						</span>
					</label>
				</div>
			</li>
		);
	}
}

export class OptionItem extends React.Component {
	static propTypes = {
		dropdownChildrenParentNode: PropTypes.any,
		index: PropTypes.number,
		item: PropTypes.object,
		onChildOver: PropTypes.func,
		onMouseOutSubitem: PropTypes.func,
		onMouseOverSubitem: PropTypes.func,
		onSelectItemsChange: PropTypes.func,
		parentName: PropTypes.string
	};

	static getDerivedStateFromProps(props) {
		const {item} = props;
		const hasHoverProperty = Object.prototype.hasOwnProperty.call(
			item,
			'hasHover'
		);

		return hasHoverProperty ? {hover: item.hasHover} : null;
	}

	state = {
		hover: false
	};

	@autobind
	handleMouseOutSubitem() {
		const {index, onMouseOutSubitem} = this.props;

		onMouseOutSubitem(index);
	}

	@autobind
	handleMouseOverSubitem() {
		const {index, onMouseOverSubitem} = this.props;

		onMouseOverSubitem(index);
	}

	render() {
		const {
			dropdownChildrenParentNode,
			item: {hasHover, hasSearch, items, label, name, value},
			onChildOver,
			onSelectItemsChange,
			parentName
		} = this.props;

		const {hover} = this.state;

		return (
			<li
				onBlur={this.handleMouseOutSubitem}
				onFocus={this.handleMouseOverSubitem}
				onMouseOut={this.handleMouseOutSubitem}
				onMouseOver={this.handleMouseOverSubitem}
			>
				<button
					className={`dropdown-item ${
						hasHover == true ? 'active' : ''
					}`}
					href='javascript:;'
				>
					<TextTruncate title={label} />

					{value && (
						<div className='dropdown-item-indicator'>
							<ClayBadge displayType='secondary' label={value} />

							{items && !!items.length && (
								<ClayIcon
									className='icon-root ml-1'
									symbol='angle-right-small'
								/>
							)}
						</div>
					)}
				</button>

				{items &&
					!!items.length &&
					ReactDOM.createPortal(
						<DropdownMenu
							hasSearch={hasSearch}
							items={items}
							name={parentName || name}
							onChildOver={onChildOver}
							onSelectItemsChange={onSelectItemsChange}
							parentNode={dropdownChildrenParentNode}
							ref='nestedItem'
							show={hover}
						/>,
						document.querySelector('body.dxp')
					)}
			</li>
		);
	}
}

/**
 * Dropdown Menu
 * @class
 */
class DropdownMenu extends React.Component {
	static defaultProps = {
		items: [],
		itemsIconAlignment: 'right',
		show: false
	};

	static propTypes = {
		hasSearch: PropTypes.bool,
		isTopLevel: PropTypes.bool,
		items: PropTypes.arrayOf(
			PropTypes.shape({
				hasHover: PropTypes.bool,
				hasSearch: PropTypes.bool,
				label: PropTypes.string,
				name: PropTypes.string,
				value: PropTypes.any
			})
		),
		itemsIconAlignment: PropTypes.oneOf(['left', 'right']),
		onChildOver: PropTypes.func,
		onSelectItemsChange: PropTypes.func,
		parentNode: PropTypes.any,
		show: PropTypes.bool
	};

	state = {
		hasChildOpened: false,
		isMouseOver: false,
		keywords: '',
		show: false
	};

	/**
	 * Lifecycle Constructor - ReactJS
	 */
	constructor(props) {
		super(props);

		const {items, show} = this.props;

		this.state = {...this.state, _prevShowProp: show, items, show};

		this._elementRef = React.createRef();
	}

	static getDerivedStateFromProps({show}, prevState) {
		if (show !== prevState._prevShowProp) {
			return {_prevShowProp: show, show};
		}

		return null;
	}

	/**
	 * Lifecycle Component Did Update - ReactJS
	 */
	componentDidUpdate() {
		const {isTopLevel, parentNode} = this.props;
		const {show} = this.state;

		if (
			!isTopLevel &&
			show &&
			parentNode &&
			!parentNode.classList.contains('dropdown-visible')
		) {
			this.setState({
				show: false
			});
		} else if (!isTopLevel && show && parentNode) {
			this._elementRef.current.style.top = `${
				parentNode.getBoundingClientRect().top + scrollY
			}px`;
			this._elementRef.current.style.left = `${
				parentNode.getBoundingClientRect().left +
				parentNode.getBoundingClientRect().width
			}px`;
		}
	}

	/**
	 * Handle Child Out
	 */
	@autobind
	handleChildOut() {
		const {onChildOver} = this.props;

		timeout = setTimeout(() => {
			if (this.state.hasChildOpened) return;

			onChildOver &&
				onChildOver({closeAllChildren: true, isVisible: true});
		}, DEBOUNCE);
	}

	/**
	 * Handle Child Over
	 * @param {object} event - A custom object with details.
	 */
	@autobind
	handleChildOver(event) {
		const {onChildOver} = this.props;

		this.setState({
			hasChildOpened: event.isVisible
		});

		if (event.closeAllChildren && !this.state.isMouseOver) {
			this.removeHasHover();
			this.handleHideAllDropdownChildren();
		}

		if (
			(!this.state.isMouseOver && event.closeAllChildren) ||
			(this.state.isMouseOver && !event.closeAllChildren)
		) {
			onChildOver && onChildOver(event);
		}
	}

	/**
	 * Handle Mouse Over Subitem
	 * @param {number} index
	 */
	@autobind
	handleMouseOverSubitem(index) {
		const {onChildOver} = this.props;

		onChildOver && onChildOver({isVisible: true});

		this.setState({
			hasChildOpened: true,
			isMouseOver: true
		});

		this.updateChildrenState(index);
	}

	/**
	 * Handle Mouse Out Subitem
	 * @param {number} index
	 */
	@autobind
	handleMouseOutSubitem(index) {
		this.setState({
			hasChildOpened: false,
			isMouseOver: false
		});

		clearTimeout(timeout);

		timeout = setTimeout(() => {
			if (
				this.state.hasChildOpened ||
				!this.refs[`item${index}`].refs.nestedItem
			)
				return;

			this.removeHasHover();
			this.refs[`item${index}`].refs.nestedItem.state.show = false;
		}, DEBOUNCE);
	}

	/**
	 * Hide All Dropdown Children
	 */
	@autobind
	handleHideAllDropdownChildren() {
		for (const key in this.refs) {
			if (key) {
				this.refs[key].setState({show: false});
			}
		}
	}

	/**
	 * Handle Search Input
	 * @param {object} event
	 */
	@autobind
	handleSearchInput(event) {
		this.setState({
			keywords: event.target.value
		});
	}

	/**
	 * Update State by Prop
	 * @param {object} properties
	 * @param {string} propertyName
	 * @param {function} cb
	 */
	updateStateByProp(properties, propertyName, cb) {
		if (Object.keys(properties).indexOf(propertyName) > -1) {
			cb(properties[propertyName]);
		}
	}

	/**
	 * Remove Has Hover
	 */
	removeHasHover() {
		this.getItems().forEach(item => {
			item.hasHover = false;
		});

		this.forceUpdate();
	}

	/**
	 * Update Children State
	 * @param {number} index
	 */
	updateChildrenState(index) {
		this.getItems().map((item, itemIndex) => {
			if (itemIndex == index) {
				item.hasHover = true;
			} else {
				item.hasHover = false;
			}

			return item;
		});
	}

	/**
	 * Get Items
	 */
	@autobind
	getItems() {
		const {
			props: {items},
			state: {keywords}
		} = this;

		return keywords
			? items.filter(
					({label}) =>
						label
							.toLowerCase()
							.search(this.state.keywords.toLowerCase()) != -1
			  )
			: items;
	}

	/**
	 * Render Items
	 * @param {string} parentName
	 * @param {object} item
	 * @param {number} index
	 */
	renderItems(parentName, item, index) {
		const {onSelectItemsChange} = this.props;

		const sharedProps = {
			index,
			item,
			key: `${parentName}-${index}`,
			onMouseOutSubitem: this.handleMouseOutSubitem,
			onMouseOverSubitem: this.handleMouseOverSubitem,
			onSelectItemsChange,
			parentName,
			ref: `item${index}`
		};

		if (item.inputType) {
			return <InputItem key={index} {...sharedProps} />;
		} else {
			return (
				<OptionItem
					{...sharedProps}
					dropdownChildrenParentNode={this._elementRef.current}
					key={index}
					onChildOver={this.handleChildOver}
				/>
			);
		}
	}

	/**
	 * Render Search
	 */
	renderSearch() {
		return (
			<div
				className={`${CLASSNAME}-search-container input-group`}
				onBlur={this.handleMouseOutSubitem}
				onFocus={this.handleMouseOverSubitem}
				onMouseEnter={this.handleHideAllDropdownChildren}
				onMouseOut={this.handleMouseOutSubitem}
				onMouseOver={this.handleMouseOverSubitem}
			>
				<div className='input-group-item'>
					<input
						aria-label={Liferay.Language.get('search')}
						className='form-control input-group-inset input-group-inset-after'
						onChange={this.handleSearchInput}
						placeholder={Liferay.Language.get('search')}
						type='text'
					/>

					<div className='input-group-inset-item input-group-inset-item-after'>
						<button
							className='btn btn-unstyled d-md-none'
							type='button'
						>
							<ClayIcon className='icon-root' symbol='times' />
						</button>
						<button
							className='btn btn-unstyled d-none d-md-inline-block'
							type='button'
						>
							<ClayIcon className='icon-root' symbol='search' />
						</button>
					</div>
				</div>
			</div>
		);
	}

	/**
	 * Lifecycle Render - ReactJS
	 */
	render() {
		const {className, hasSearch, itemsIconAlignment, name} = this.props;
		const {show} = this.state;

		const classes = getCN(CLASSNAME, className, 'dropdown-menu', {
			'dropdown-menu-indicator-end': itemsIconAlignment == 'right',
			'dropdown-menu-indicator-start': itemsIconAlignment == 'left',
			'dropdown-visible': show
		});

		const items = this.getItems();

		return (
			<div
				className={classes}
				onMouseLeave={this.handleChildOut}
				ref={this._elementRef}
			>
				{hasSearch && this.renderSearch()}

				<ul className='list-unstyled'>
					{items.map((item, index) =>
						this.renderItems(name, item, index)
					)}
				</ul>
			</div>
		);
	}
}

export default DropdownMenu;
