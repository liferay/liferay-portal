import autobind from 'autobind-decorator';
import getCN from 'classnames';
import React from 'react';
import ReactDOM from 'react-dom';
import {align, POSITIONS} from 'shared/util/align';
import {debounce, isNil, noop, uniqueId} from 'lodash';
import {PropTypes} from 'prop-types';

export const ALIGNMENTS = [
	'topCenter',
	'topRight',
	'rightCenter',
	'bottomRight',
	'bottomCenter',
	'bottomLeft',
	'leftCenter',
	'topLeft'
];

const ALIGNMENTS_MAP = {
	bottomCenter: POSITIONS.BottomCenter,
	bottomLeft: POSITIONS.BottomLeft,
	bottomRight: POSITIONS.BottomRight,
	leftCenter: POSITIONS.LeftCenter,
	rightCenter: POSITIONS.RightCenter,
	topCenter: POSITIONS.TopCenter,
	topLeft: POSITIONS.TopLeft,
	topRight: POSITIONS.TopRight
};

/**
 * Visit each overlay and call it's checkOutsideClick method, recursively
 * visiting children first.
 * @param {Iterator<Overlay>} children
 * @return {boolean} - true if an outside click was found
 */
function visitChildren(event, children) {
	let outside = true;

	for (const child of children) {
		if (
			!visitChildren(event, child.getChildren()) ||
			!child.checkOutsideClick(event)
		) {
			outside = false;
		}
	}

	return outside;
}

const OverlayContent = React.forwardRef(
	({children, className, ...otherProps}, ref) => (
		<div
			{...otherProps}
			className={getCN('overlay-content-root', className)}
			data-testid='overlay'
			ref={ref}
		>
			{children}
		</div>
	)
);

export default class Overlay extends React.Component {
	static childContextTypes = {
		parentOverlay: PropTypes.shape({
			addChildOverlay: PropTypes.func,
			onMouseEnter: PropTypes.func,
			onMouseLeave: PropTypes.func,
			removeChildOverlay: PropTypes.func
		})
	};

	static defaultProps = {
		alignment: 'bottomLeft',
		forceAlignment: true,
		hideDelay: 400,
		offset: 8,
		onOutsideClick: noop,
		showDelay: 400,
		usePortal: true
	};

	static propTypes = {
		active: PropTypes.bool,
		alignment: PropTypes.oneOf(ALIGNMENTS),
		containerClass: PropTypes.string,
		forceAlignment: PropTypes.bool,
		hideDelay: PropTypes.number,
		offset: PropTypes.number,
		onOutsideClick: PropTypes.func,
		showDelay: PropTypes.number,
		usePortal: PropTypes.bool
	};

	state = {
		active: false,
		initialClickOnInput: false
	};

	constructor(props) {
		super(props);
		this._contentId = uniqueId('overlay');
		this._childOverlays = new Set();
		const {hideDelay, showDelay} = this.props;

		this.hide = debounce(() => this.setState({active: false}), hideDelay);

		this.show = debounce(() => this.setState({active: true}), showDelay);

		this._contentElementRef = React.createRef();
		this._elementRef = React.createRef();
	}

	getChildContext() {
		return {
			parentOverlay: {
				addChildOverlay: this.addChildOverlay,
				onMouseEnter: this.handleMouseEnter,
				onMouseLeave: this.handleMouseLeave,
				removeChildOverlay: this.removeChildOverlay
			}
		};
	}

	componentDidMount() {
		this.withParent(parent => parent.addChildOverlay(this));

		this.addBodyListener();
	}

	componentDidUpdate() {
		const active = this.getActive();

		if (active) {
			this.alignOverlay();
		}
	}

	componentWillUnmount() {
		this.hide.cancel();
		this.show.cancel();

		document.body.removeEventListener('click', this._onClick);
		document.body.removeEventListener(
			'mousedown',
			this.checkForInitialClickOnInput
		);

		this.withParent(parent => parent.removeChildOverlay(this));
	}

	@autobind
	_onClick(event) {
		visitChildren(event, [this]);
	}

	addBodyListener() {
		if (!this.context.parentOverlay) {
			document.body.addEventListener('click', this._onClick);

			document.body.addEventListener(
				'mousedown',
				this.checkForInitialClickOnInput
			);
		}
	}

	@autobind
	addChildOverlay(overlay) {
		this._childOverlays.add(overlay);
	}

	@autobind
	removeChildOverlay(overlay) {
		this._childOverlays.delete(overlay);
	}

	alignOverlay() {
		const {alignment, forceAlignment, offset} = this.props;

		const elementNode = ReactDOM.findDOMNode(this._elementRef.current);
		const contentNode = ReactDOM.findDOMNode(
			this._contentElementRef.current
		);

		if (contentNode) {
			const position = align(
				contentNode,
				elementNode,
				ALIGNMENTS_MAP[alignment],
				!forceAlignment
			);

			this.setOffset(contentNode, position, offset);
		}
	}

	checkIfEventOutside(event) {
		const elementNode = ReactDOM.findDOMNode(this._elementRef.current);
		const contentNode = ReactDOM.findDOMNode(
			this._contentElementRef.current
		);

		const eventOutsideContent =
			(contentNode && !contentNode.contains(event.target)) ||
			!contentNode;

		return (
			eventOutsideContent &&
			elementNode &&
			!elementNode.contains(event.target)
		);
	}

	@autobind
	checkForInitialClickOnInput(event) {
		this.setState({
			initialClickOnInput: !this.checkIfEventOutside(event)
		});
	}

	/**
	 * For the node visitor to check if the click event was inside the overlay's
	 * trigger or content.
	 * @param {event} event - The click event
	 * @return {boolean} - True if the click was outside of the overlay
	 */
	checkOutsideClick(event) {
		const {
			props: {onOutsideClick},
			state: {initialClickOnInput}
		} = this;

		const clickedOutside = this.checkIfEventOutside(event);

		if (clickedOutside && !initialClickOnInput) {
			onOutsideClick(event);
		}

		return clickedOutside;
	}

	getActive() {
		const {active} = this.props;

		return !isNil(active) ? active : this.state.active;
	}

	/**
	 * Returns the overlay's children
	 * @return {Set<Overlay>}
	 */
	getChildren() {
		return this._childOverlays;
	}

	@autobind
	handleMouseEnter() {
		const {parentOverlay} = this.context;

		if (parentOverlay) {
			parentOverlay.onMouseEnter();
		}

		this.hide.cancel();

		this.show();
	}

	@autobind
	handleMouseLeave() {
		const {parentOverlay} = this.context;

		if (parentOverlay) {
			parentOverlay.onMouseLeave();
		}

		this.show.cancel();

		this.hide();
	}

	/**
	 * Public method for hiding overlay
	 */
	hideOverlay() {
		this.show.cancel();

		this.setState({active: false});
	}

	setOffset(node, position, offset) {
		switch (position) {
			case POSITIONS.BottomCenter:
			case POSITIONS.BottomLeft:
			case POSITIONS.BottomRight:
				node.style.transform = `translateY(${offset}px)`;
				break;
			case POSITIONS.LeftCenter:
				node.style.transform = `translateX(-${offset}px)`;
				break;
			case POSITIONS.RightCenter:
				node.style.transform = `translateX(${offset}px)`;
				break;
			case POSITIONS.TopCenter:
			case POSITIONS.TopLeft:
			case POSITIONS.TopRight:
				node.style.transform = `translateY(-${offset}px)`;
				break;
			default:
				break;
		}
	}

	withParent(fn) {
		const {parentOverlay} = this.context;

		if (parentOverlay) {
			fn(parentOverlay);
		}
	}

	render() {
		const {children, className, containerClass, usePortal} = this.props;

		const triggerProps = {
			'aria-haspopup': true,
			className: getCN(children[0].props.className, className),
			onBlur: this.handleMouseLeave,
			onMouseEnter: this.handleMouseEnter,
			onMouseLeave: this.handleMouseLeave,
			ref: this._elementRef
		};

		const active = this.getActive();

		if (active) {
			triggerProps['aria-owns'] = this.contentId;
		}

		const trigger = React.cloneElement(children[0], triggerProps);

		return (
			<>
				{trigger}

				{usePortal &&
					active &&
					ReactDOM.createPortal(
						<OverlayContent
							className={containerClass}
							id={this._contentId}
							onBlur={this.handleMouseLeave}
							onMouseEnter={this.handleMouseEnter}
							onMouseLeave={this.handleMouseLeave}
							ref={this._contentElementRef}
							role='dialog'
						>
							{children[1]}
						</OverlayContent>,
						document.querySelector('body.dxp') || document.body
					)}

				{!usePortal && active && (
					<OverlayContent
						className={containerClass}
						id={this._contentId}
						onBlur={this.handleMouseLeave}
						onMouseEnter={this.handleMouseEnter}
						onMouseLeave={this.handleMouseLeave}
						ref={this._contentElementRef}
						role='dialog'
					>
						{children[1]}
					</OverlayContent>
				)}
			</>
		);
	}
}
