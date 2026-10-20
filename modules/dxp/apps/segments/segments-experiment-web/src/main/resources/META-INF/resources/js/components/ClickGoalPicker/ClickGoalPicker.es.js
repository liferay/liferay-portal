/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {ReactPortal, useEventListener} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {sub, throttle} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React from 'react';

import {StateContext as GlobalStateContext} from './../../state/context.es';
import {StateContext, getInitialState, reducer} from './reducer.es';
import {
	GeometryType,
	getElementGeometry,
	getRootElementGeometry,
	getTargetableElements,
	stopImmediatePropagation,
} from './utils.es';

const {
	useCallback,
	useContext,
	useEffect,
	useLayoutEffect,
	useReducer,
	useRef,
	useState,
} = React;

const ENTER_KEY = 'Enter';

const ESCAPE_KEYS = [
	'Escape', // Most browsers.
	'Esc', // IE and Edge.
];

const POPOVER_PADDING = 16;

const THROTTLE_INTERVAL_MS = 100;

const DispatchContext = React.createContext();

const OVERLAY_TARGET_CLASS = 'lfr-segments-experiment-click-goal-target';

/**
 * Top-level entry point for displaying, selecting, editing and removing click
 * goal targets.
 */
function ClickGoalPicker({allowEdit = true, onSelectClickGoalTarget, target}) {
	const [state, dispatch] = useReducer(reducer, target, getInitialState);

	const {isValidTarget, mode, selectedTarget} = state;

	const isSelectedTargetInDOM = document.getElementById(selectedTarget);

	// If the parent passes as a prop an empty target and the old selected target
	// is not in the DOM anymore we must update it in the Context

	if (!target && selectedTarget && !isSelectedTargetInDOM) {
		dispatch({
			selector: target,
			type: 'selectTarget',
		});
	}

	const [selectorInputValue, setSelectorInputValue] =
		useState(selectedTarget);

	const {errors} = useContext(GlobalStateContext);

	const ref = useRef(selectedTarget);

	useEffect(() => {
		ref.current = selectedTarget;
		setSelectorInputValue(selectedTarget);
	}, [selectedTarget]);

	const previousTarget = ref.current;

	if (selectedTarget !== previousTarget && selectedTarget !== target) {
		if (onSelectClickGoalTarget) {
			onSelectClickGoalTarget(selectedTarget);
		}
	}

	if (process.env.NODE_ENV === 'test') {
		if (!document.getElementById('content')) {
			const content = document.createElement('div');

			content.id = 'content';

			document.body.appendChild(content);
		}
	}

	const root = document.getElementById('content');

	if (!root) {
		console.error(
			'Cannot render <SegmentsExperimentsClickGoal /> without #content element',
			document.querySelectorAll('section').length
		);

		return null;
	}

	const scrollIntoView = (event) => {
		const target = document.getElementById(selectedTarget);

		if (target) {
			target.scrollIntoView();

			// Make sure nothing slides under the top nav.

			window.scrollBy(0, -100);
		}

		event.preventDefault();

		dispatch({type: 'activate'});
	};

	const isValidNewClickTargetElement = (value) => {
		const target = value && document.getElementById(value);

		if (!target) {
			dispatch({type: 'invalidTarget'});
		}

		return !!target;
	};

	const selectNewClickTargetElement = (event) => {
		scrollIntoView(event);

		dispatch({
			selector: event.target.value,
			type: 'selectTarget',
		});

		event.preventDefault();
		stopImmediatePropagation(event);
	};

	const handleBlur = (event) => {
		if (!selectedTarget) {
			const value = event.target.value;

			if (isValidNewClickTargetElement(value)) {
				selectNewClickTargetElement(event);
			}
		}
	};

	const handleKeyDown = (event) => {
		if (event.key === ENTER_KEY) {
			const value = event.target.value;

			if (isValidNewClickTargetElement(value)) {
				selectNewClickTargetElement(event);
			}
		}
	};

	const handleInputChange = (event) => {
		setSelectorInputValue(event.target.value);
	};

	const handleDelete = (event) => {
		stopImmediatePropagation(event);

		dispatch({
			selector: '',
			type: 'selectTarget',
		});
	};

	return (
		<DispatchContext.Provider value={dispatch}>
			<StateContext.Provider value={state}>
				<div className="mb-3 mt-4 sheet-subtitle">
					{Liferay.Language.get('click-goal')}

					{allowEdit && (
						<ClayIcon
							className="lexicon-icon-sm ml-1 reference-mark text-warning"
							style={{verticalAlign: 'super'}}
							symbol="asterisk"
						/>
					)}
				</div>

				{allowEdit && (
					<div className="c-mb-2 text-secondary">
						{Liferay.Language.get('click-goal-description')}
					</div>
				)}

				{isValidTarget && errors.clickTargetError && (
					<div className="c-mb-2 c-mt-2 font-weight-semi-bold text-danger">
						<ClayIcon
							className="c-mr-2"
							symbol="exclamation-full"
						/>

						{Liferay.Language.get(
							'an-element-needs-to-be-selected'
						)}
					</div>
				)}

				{allowEdit && (
					<ClayButton
						className="c-mb-2"
						displayType="secondary"
						onClick={() => dispatch({type: 'activate'})}
						small
					>
						{selectedTarget
							? Liferay.Language.get('change-clickable-element')
							: Liferay.Language.get('select-clickable-element')}
					</ClayButton>
				)}

				<ClayForm.Group>
					<label htmlFor="clickableElement">
						{Liferay.Language.get('element-id')}

						<span
							data-tooltip-align="top"
							title={Liferay.Language.get('element-id-help')}
						>
							<ClayIcon
								className="c-ml-1 text-secondary"
								small="true"
								symbol="question-circle"
							/>
						</span>
					</label>

					<ClayInput.Group
						className={classNames({
							'has-error': !isValidTarget,
						})}
					>
						<ClayInput.GroupItem prepend shrink>
							<ClayInput.GroupText>#</ClayInput.GroupText>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem append>
							<ClayInput
								className={classNames({
									'input-group-inset input-group-inset-after':
										allowEdit && selectedTarget,
								})}
								data-tooltip-align="top"
								id="clickableElement"
								onBlur={handleBlur}
								onChange={handleInputChange}
								onKeyDown={handleKeyDown}
								readOnly={!allowEdit || selectedTarget}
								title={selectorInputValue}
								type="text"
								value={selectorInputValue}
							/>

							{allowEdit && selectedTarget && (
								<ClayInput.GroupInsetItem after>
									<ClayButtonWithIcon
										aria-label={sub(
											Liferay.Language.get('clear-x'),
											Liferay.Language.get('text')
										)}
										data-tooltip-align="bottom-right"
										disabled={!selectedTarget}
										displayType="unstyled"
										monospaced={false}
										onClick={handleDelete}
										symbol="times-circle"
										title={Liferay.Language.get('clear')}
									/>
								</ClayInput.GroupInsetItem>
							)}
						</ClayInput.GroupItem>

						<ClayInput.GroupItem shrink>
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'show-element'
								)}
								data-tooltip-align="bottom-right"
								disabled={!selectedTarget}
								displayType="secondary"
								onClick={scrollIntoView}
								symbol="view"
								title={Liferay.Language.get('show-element')}
							/>
						</ClayInput.GroupItem>

						{!isValidTarget && (
							<ClayForm.FeedbackGroup>
								<ClayForm.FeedbackItem>
									<ClayForm.FeedbackIndicator symbol="exclamation-full" />

									{Liferay.Language.get('id-was-not-found')}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						)}
					</ClayInput.Group>
				</ClayForm.Group>

				{mode === 'active' ? (
					<ClickGoalPicker.OverlayContainer
						allowEdit={allowEdit}
						root={root}
					/>
				) : null}
			</StateContext.Provider>
		</DispatchContext.Provider>
	);
}

ClickGoalPicker.propTypes = {
	allowEdit: PropTypes.bool,
	onSelectClickGoalTarget: PropTypes.func,
	target: PropTypes.string,
};

/**
 * Responsible for performing the "full-screen takeover" and mounting the
 * <Overlay /> component when active.
 */
function OverlayContainer({allowEdit, root}) {
	const mousedownRef = useRef(false);

	const cssId = 'segments-experiments-click-goal-css-overrides';

	const dispatch = useContext(DispatchContext);
	const {selectedTarget} = useContext(StateContext);

	const targetableElementsRef = useRef();

	// Before mount.

	if (!targetableElementsRef.current) {

		// Apply CSS overrides.

		const css = `
			#banner, #footer,
			#banner .input-group .input-group-item .input-group-inset-after.form-control:hover {
				cursor: not-allowed;
			}
			#banner a, #banner button,
			#footer a, #footer button {
				cursor: not-allowed;
				pointer-events: none;
			}
			#banner section.portlet:hover .portlet-content.portlet-content-editable {
				border-color: transparent;
			}
			#content {
				position: relative;
				cursor: not-allowed;
			}
			.portlet-topper {
				visibility: hidden;
			}
		`;
		const head = document.head;
		const style = document.createElement('style');

		style.id = cssId;
		style.type = 'text/css';
		style.appendChild(document.createTextNode(css));

		if (Liferay.CSP?.nonce) {
			style.setAttribute('nonce', Liferay.CSP.nonce);
		}

		head.appendChild(style);

		// This must happen after hiding the toppers.

		targetableElementsRef.current = getTargetableElements(
			root,
			selectedTarget
		);
	}

	// On unmount.

	useEffect(() => {
		return () => {

			// Remove CSS overrides.

			const style = document.getElementById(cssId);

			if (style) {
				style.parentNode.removeChild(style);
			}
		};
	}, []);

	const handleKeydown = useCallback(
		(event) => {
			if (ESCAPE_KEYS.includes(event.key)) {
				dispatch({type: 'deactivate'});
				event.preventDefault();
				stopImmediatePropagation(event);
			}
		},
		[dispatch]
	);

	useEventListener('keydown', handleKeydown, true, document);

	const handleMouseDown = useCallback(() => {
		mousedownRef.current = true;
	}, []);

	const handleMouseUp = useCallback(
		(event) => {
			const overlayTarget = event.target.closest(
				`.${OVERLAY_TARGET_CLASS}`
			);

			if (mousedownRef.current === true && !overlayTarget) {
				dispatch({type: 'deactivate'});
			}

			mousedownRef.current = false;
		},
		[dispatch]
	);

	useEventListener('mousedown', handleMouseDown, false, document);
	useEventListener('mouseup', handleMouseUp, false, document);

	return (
		<ReactPortal container={root}>
			<ClickGoalPicker.Overlay
				allowEdit={allowEdit}
				root={root}
				targetableElements={targetableElementsRef.current}
			/>
		</ReactPortal>
	);
}

OverlayContainer.propTypes = {
	allowEdit: PropTypes.bool,
	root: PropTypes.instanceOf(Element).isRequired,
};

/**
 * Covers the main content surface of the page (the "#content" element)
 * and draws UI over each targetable element.
 */
function Overlay({allowEdit, root, targetableElements}) {
	const {editingTarget, selectedTarget} = useContext(StateContext);

	const [geometry, setGeometry] = useState(getRootElementGeometry(root));

	/* eslint-disable-next-line react-hooks/exhaustive-deps */
	const handleResize = useCallback(
		throttle(() => {
			setGeometry(getRootElementGeometry(root));
		}, THROTTLE_INTERVAL_MS),
		[root]
	);

	// For now, treat scrolling just like resizing.

	const handleScroll = handleResize;

	useEventListener('resize', handleResize, false, window);

	// TODO: also consider scrolling of elements with "overflow: auto/scroll";

	useEventListener('scroll', handleScroll, false, window);

	return (
		<div className="lfr-segments-experiment-click-goal-root">
			{targetableElements
				.filter((element) => {
					return allowEdit || element.id === selectedTarget;
				})
				.map((element) => {
					const elementId = element.id;

					const mode =
						editingTarget === elementId && allowEdit
							? 'editing'
							: selectedTarget === elementId
								? 'selected'
								: 'inactive';

					const selector = `#${element.id}`;

					return (
						<ClickGoalPicker.Target
							allowEdit={allowEdit}
							element={element}
							geometry={geometry}
							key={selector}
							mode={mode}
							selector={selector}
						/>
					);
				})}
		</div>
	);
}

Overlay.propTypes = {
	allowEdit: PropTypes.bool,
	root: PropTypes.instanceOf(Element).isRequired,
	targetableElements: PropTypes.arrayOf(PropTypes.instanceOf(Element))
		.isRequired,
};

/**
 * Draws three controls above/on-top-of/below a potential click goal target:
 *
 * - A <TargetTopper />, above the target, which labels the target and can be
 *   used to undo the selection of that target.
 * - An rectangle on-top-of the target which can be clicked on to select it.
 * - A <TargetPopover />, below the target, that provides contextual information
 *   and a button to confirm the selection of that target.
 */
function Target({allowEdit, element, geometry, mode, selector}) {
	const dispatch = useContext(DispatchContext);

	const {selectedTarget} = useContext(StateContext);

	const {bottom, height, left, right, top, width} =
		getElementGeometry(element);

	if (!bottom && !top && !right && !left) {
		return null;
	}

	const handleClick = (event) => {
		dispatch({
			selector: selector.substring(1),
			type: 'editTarget',
		});

		stopImmediatePropagation(event);
	};

	// At this point we don't know the dimensions of our children, but we do
	// know whether we have more space on the left or right of our target, so we
	// flip based on that.

	const spaceOnLeft = left - geometry.left;
	const spaceOnRight = geometry.right - right;
	const spaceOnTop = top - geometry.top;
	const align = spaceOnRight > spaceOnLeft ? 'left' : 'right';

	// TODO: make tooltip match mock and switch to Clay v3 tooltips directly
	// instead of using lfr-portal-tooltip.

	return (
		<div
			className={OVERLAY_TARGET_CLASS}
			style={{
				alignItems: align === 'left' ? 'flex-start' : 'flex-end',
				left: align === 'left' ? spaceOnLeft : null,
				right: align === 'right' ? spaceOnRight : null,
				top: spaceOnTop,
			}}
		>
			<div
				className={classNames({
					'lfr-segments-experiment-click-goal-target-overlay':
						!selectedTarget ||
						mode === 'editing' ||
						mode === 'selected',
					'lfr-segments-experiment-click-goal-target-overlay-editing':
						mode === 'editing',
					'lfr-segments-experiment-click-goal-target-overlay-selected':
						mode === 'selected',
				})}
				data-target-selector={selector}
				data-tooltip-align="bottom-left"
				onClick={handleClick}
				style={{height, width}}
				title={
					mode === 'inactive'
						? Liferay.Language.get(
								'click-element-to-set-as-click-target-for-your-goal'
							)
						: ''
				}
			></div>

			{mode !== 'inactive' && (
				<ClickGoalPicker.TargetTopper
					allowEdit={allowEdit}
					element={element}
					geometry={geometry}
					isEditing={mode === 'editing'}
					selector={selector}
				/>
			)}

			{mode === 'editing' && (
				<ClickGoalPicker.TargetPopover selector={selector} />
			)}
		</div>
	);
}

Target.propTypes = {
	allowEdit: PropTypes.bool,
	element: PropTypes.instanceOf(Element).isRequired,
	geometry: GeometryType.isRequired,
	mode: PropTypes.oneOf(['inactive', 'selected', 'editing']).isRequired,
	selector: PropTypes.string.isRequired,
};

/**
 * Drawn above a click target, and includes a button to undo the selection of
 * that target.
 */
function TargetTopper({allowEdit, geometry, isEditing, selector}) {
	const dispatch = useContext(DispatchContext);

	const topperRef = useRef();

	const [top, setTop] = useState(0);

	const [width, setWidth] = useState(null);

	useLayoutEffect(() => {
		if (topperRef.current) {
			const {height, left, width} =
				topperRef.current.getBoundingClientRect();

			setTop(-height);

			setWidth(Math.min(geometry.width - (left - geometry.left), width));
		}
	}, [geometry.left, geometry.width]);

	const handleClick = (event) => {
		stopImmediatePropagation(event);

		dispatch({
			selector: '',
			type: 'selectTarget',
		});
	};

	return (
		<div
			className={classNames({
				'd-flex': true,
				'lfr-segments-experiment-click-goal-target-topper': true,
				'lfr-segments-experiment-click-goal-target-topper-editing':
					isEditing,
				'px-2': true,
				'small': true,
				'text-white': true,
			})}
			onClick={stopImmediatePropagation}
			ref={topperRef}
			style={{
				maxWidth: width !== null ? `${width}px` : null,
				top: `${top}px`,
			}}
		>
			<span className="mr-2 text-truncate">
				{isEditing ? selector : Liferay.Language.get('target[element]')}
			</span>

			{allowEdit && (
				<ClayButton
					className="lfr-segments-experiment-click-goal-target-delete small text-white"
					displayType="unstyled"
					onClick={handleClick}
				>
					<ClayIcon symbol="times-circle" />
				</ClayButton>
			)}
		</div>
	);
}

TargetTopper.propTypes = {
	allowEdit: PropTypes.bool,
	geometry: GeometryType.isRequired,
	isEditing: PropTypes.bool.isRequired,
	selector: PropTypes.string.isRequired,
};

/**
 * Drawn below a click target, and provides contextual information and a button
 * to confirm the selection of that target.
 */
function TargetPopover({selector}) {
	const dispatch = useContext(DispatchContext);

	const buttonRef = useRef();

	const [buttonWidth, setButtonWidth] = useState(null);

	useLayoutEffect(() => {
		if (buttonRef.current) {
			setButtonWidth(buttonRef.current.offsetWidth);
		}
	}, []);

	// The +1 here is to avoid unwanted wrapping of the button.

	const maxWidth = buttonWidth
		? `${buttonWidth + POPOVER_PADDING * 2 + 1}px`
		: 'none';

	const handleClick = () => {
		dispatch({
			selector: selector.substring(1),
			type: 'selectTarget',
		});
	};

	return (
		<div
			className="lfr-segments-experiment-click-goal-target-popover p-3"
			onClick={stopImmediatePropagation}
			style={{maxWidth}}
		>
			<div className="mb-2 text-secondary text-truncate" title={selector}>
				{selector}
			</div>

			<ClayButton onClick={handleClick} ref={buttonRef}>
				{Liferay.Language.get('set-element-as-click-target')}
			</ClayButton>
		</div>
	);
}

TargetPopover.propTypes = {
	selector: PropTypes.string.isRequired,
};

ClickGoalPicker.Overlay = Overlay;
ClickGoalPicker.OverlayContainer = OverlayContainer;
ClickGoalPicker.Target = Target;
ClickGoalPicker.TargetPopover = TargetPopover;
ClickGoalPicker.TargetTopper = TargetTopper;

export default ClickGoalPicker;
