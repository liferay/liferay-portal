/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import Layout from '@clayui/layout';
import ClayTooltip from '@clayui/tooltip';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import {
	LengthInput,
	isValidStyleValue,
} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {useGlobalContext} from '../../app/contexts/GlobalContext';
import {useSelector} from '../../app/contexts/StoreContext';
import {getResetLabelByViewport} from '../../app/utils/getResetLabelByViewport';
import {useStyleBook} from '../../plugins/page_design_options/hooks/useStyleBook';

/**
 * These elements must be sorted from the most outer circle to the most inner
 * circle to facilitate keyboard navigation.
 * @type {string[]}
 */
const SPACING_TYPES = ['margin', 'padding'];

/**
 * We want to show spacing options in a clockwise order, according
 * to the standard of most CSS rules (top, right, bottom, left).
 * @type {string[]}
 */
const SPACING_POSITIONS = ['top', 'right', 'bottom', 'left'];

const ARROW_TO_POSITION = {
	ArrowDown: 'bottom',
	ArrowLeft: 'left',
	ArrowRight: 'right',
	ArrowUp: 'top',
};

const REVERSED_POSITION = {
	bottom: 'top',
	left: 'right',
	right: 'left',
	top: 'bottom',
};

const BUTTON_CLASSNAME = 'page-editor__spacing-selector__button';
const DROPDOWN_CLASSNAME = 'page-editor__spacing-selector__dropdown';

const TOOLTIP_SHOW_DELAY = 600;

export default function SpacingBox({
	canSetCustomValue,
	fields,
	onChange,
	value,
}) {
	const ref = useRef();

	const focusButton = (type, position) => {
		const button = document.querySelector(
			`.${BUTTON_CLASSNAME}[data-type=${type}][data-position=${position}]`
		);

		button?.focus();
	};

	const handleKeyDown = (event) => {
		if (
			(event.key === 'Enter' || event.key === ' ') &&
			document.activeElement === ref.current
		) {
			event.preventDefault();
			focusButton('margin', 'top');

			return;
		}

		if (
			!document.activeElement?.classList.contains(BUTTON_CLASSNAME) ||
			document.activeElement?.getAttribute('aria-expanded') === 'true' ||
			!event.key.startsWith('Arrow')
		) {
			return;
		}

		event.preventDefault();

		const {position: currentPosition, type: currentType} =
			document.activeElement.dataset;

		let nextPosition = ARROW_TO_POSITION[event.key];
		let nextType = currentType;

		if (nextPosition === currentPosition) {

			// Move to the outer type.
			// We try to update the type so we can move to the outer circle,
			// or stay in position if it is not possible.

			const currentTypeIndex = SPACING_TYPES.indexOf(currentType);
			nextType = SPACING_TYPES[Math.max(0, currentTypeIndex - 1)];
		}
		else if (nextPosition === REVERSED_POSITION[currentPosition]) {

			// Move to the inner type.
			// We try to update the type so we can move to the inner circle,
			// and keep currentPosition if it succeeds.

			const currentTypeIndex = SPACING_TYPES.indexOf(currentType);

			if (currentTypeIndex < SPACING_TYPES.length - 1) {
				nextType = SPACING_TYPES[currentTypeIndex + 1];
				nextPosition = currentPosition;
			}
		}

		focusButton(nextType, nextPosition);
	};

	return (
		<div
			className="page-editor__spacing-selector"
			onKeyDownCapture={handleKeyDown}
			ref={ref}
			role="grid"
		>
			<SpacingSelectorBackground />

			{SPACING_TYPES.map((type) => (
				<React.Fragment key={type}>
					{SPACING_POSITIONS.map((position) => {
						const key = `${type}${capitalize(position)}`;

						return (
							<SpacingSelectorButton
								canSetCustomValue={canSetCustomValue}
								field={fields[key]}
								key={key}
								onChange={onChange}
								position={position}
								type={type}
								value={value[key]}
							/>
						);
					})}
				</React.Fragment>
			))}
		</div>
	);
}

function SpacingSelectorButton({
	canSetCustomValue,
	field,
	onChange,
	position,
	type,
	value,
}) {
	const [active, setActive] = useState(false);
	const disabled = !field || field.disabled;
	const itemListRef = useRef();
	const lengthInputRef = useRef();
	const [labelElement, setLabelElement] = useState(null);
	const {tokenValues} = useStyleBook();
	const tooltipId = useId();
	const triggerId = useId();
	const headerId = useId();
	const [triggerElement, setTriggerElement] = useState(null);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const resetButtonLabel = useMemo(
		() => getResetLabelByViewport(selectedViewportSize),
		[selectedViewportSize]
	);

	useEffect(() => {
		if (active && itemListRef.current) {
			setTimeout(() => {
				const optionElement = itemListRef.current?.querySelector(
					`button[data-value="${value || field?.defaultValue}"]`
				);

				if (optionElement) {
					optionElement.focus();
				}
				else {
					lengthInputRef.current?.focus();
				}
			}, 10);
		}
	}, [active, field, value]);

	return (
		<ClayDropDown
			active={active}
			className={classNames(
				`${DROPDOWN_CLASSNAME} ${DROPDOWN_CLASSNAME}--${type} ${DROPDOWN_CLASSNAME}--${type}-${position} align-items-stretch d-flex text-center`,
				{disabled}
			)}
			menuElementAttrs={{className: 'cadmin'}}
			onActiveChange={setActive}
			renderMenuOnClick
			trigger={
				<ClayButton
					aria-describedby={tooltipId}
					aria-expanded={active}
					aria-haspopup={true}
					aria-label={field?.label}
					className={`${BUTTON_CLASSNAME} b-0 flex-grow-1 mb-0 text-center`}
					data-position={position}
					data-type={type}
					disabled={disabled}
					displayType="unstyled"
					id={triggerId}
					onClick={() => setActive(!active)}
					ref={setTriggerElement}
					type="button"
				>
					{field ? (
						<Tooltip
							hoverElement={triggerElement}
							id={tooltipId}
							label={
								<>
									{field.label} -{' '}
									<SpacingOptionValue
										position={position}
										tokenValues={tokenValues}
										type={type}
										value={value || field.defaultValue}
									/>
								</>
							}
							positionElement={labelElement}
						/>
					) : null}

					<span className="text-truncate" ref={setLabelElement}>
						<SpacingOptionValue
							position={position}
							removeValueUnit
							tokenValues={tokenValues}
							type={type}
							value={value || field?.defaultValue}
						/>
					</span>
				</ClayButton>
			}
		>
			<div className={DROPDOWN_CLASSNAME} ref={itemListRef}>
				<ClayDropDown.ItemList aria-labelledby={triggerId}>
					{active && canSetCustomValue ? (
						<>
							<li
								aria-hidden="true"
								className={`align-items-center d-flex dropdown-subheader justify-content-between my-2 pr-2 py-0  text-3 ${DROPDOWN_CLASSNAME}__header`}
								id={headerId}
								role="presentation"
							>
								{field?.label}

								{value && (
									<ClayButtonWithIcon
										aria-label={resetButtonLabel}
										borderless
										className="lfr-portal-tooltip text-3"
										displayType="secondary"
										onClick={() => {
											setActive(false);

											onChange(field.name, null, {
												isReset: true,
											});
										}}
										size="sm"
										symbol="restore"
										title={resetButtonLabel}
									/>
								)}
							</li>

							<li role="presentation">
								<ul
									aria-labelledby={headerId}
									className="list-unstyled"
									role="group"
								>
									<LengthInput
										className="mb-3 mt-2 px-3"
										field={field}
										onValueSelect={onChange}
										ref={lengthInputRef}
										showLabel={false}
										value={
											isValidStyleValue(
												field.cssProperty,
												value
											)
												? value
												: ''
										}
									/>
								</ul>
							</li>
						</>
					) : null}

					<ClayDropDown.Group
						header={Liferay.Language.get('existing-tokens')}
					>
						{field?.typeOptions?.validValues?.map((option) => (
							<ClayDropDown.Item
								aria-label={sub(
									Liferay.Language.get('set-x-to-x'),
									[field.label, option.label]
								)}
								className={classNames({
									active:
										value === option.value ||
										(!value && option.value === '0'),
								})}
								data-value={option.value}
								key={option.value}
								onClick={() => {
									onChange(field.name, option.value);
									setActive(false);
									triggerElement?.focus();
								}}
							>
								<Layout.ContentRow>
									<Layout.ContentCol expand>
										<Text size={3} truncate>
											{tokenValues[
												`spacer${option.value}`
											]?.label || option.label}
										</Text>
									</Layout.ContentCol>

									<Layout.ContentCol
										className="text-right"
										expand
									>
										<Text
											size={3}
											truncate
											weight="semi-bold"
										>
											<SpacingOptionValue
												position={position}
												tokenValues={tokenValues}
												type={type}
												value={option.value}
											/>
										</Text>
									</Layout.ContentCol>
								</Layout.ContentRow>
							</ClayDropDown.Item>
						))}
					</ClayDropDown.Group>
				</ClayDropDown.ItemList>
			</div>
		</ClayDropDown>
	);
}

function SpacingOptionValue({
	position,
	removeValueUnit = false,
	tokenValues,
	type,
	value: optionValue,
}) {
	const globalContext = useGlobalContext();
	const [value, setValue] = useState(optionValue);

	useEffect(() => {
		if (tokenValues[`spacer${optionValue}`]) {
			setValue(tokenValues[`spacer${optionValue}`].value);

			return;
		}

		if (isValidStyleValue(`${type}-${position}`, optionValue)) {
			setValue(optionValue);

			return;
		}

		const element = globalContext.document.createElement('div');
		element.style.display = 'none';
		element.classList.add(`${type[0]}${position[0]}-${optionValue}`);
		globalContext.document.body.appendChild(element);

		const nextValue = globalContext.window
			.getComputedStyle(element)
			.getPropertyValue(`${type}-${position}`);

		setValue(nextValue);
		globalContext.document.body.removeChild(element);
	}, [
		globalContext,
		optionValue,
		position,
		removeValueUnit,
		type,
		tokenValues,
	]);

	return value === undefined ? '' : value;
}

function Tooltip({hoverElement, id: tooltipId, label, positionElement}) {
	const [tooltipStyle, setTooltipStyle] = useState(null);

	useEffect(() => {
		if (!hoverElement || !positionElement) {
			return;
		}

		let showTimeoutId;

		const handleMouseLeave = () => {
			clearTimeout(showTimeoutId);
			setTooltipStyle(null);
		};

		const handleMouseOver = () => {
			clearTimeout(showTimeoutId);

			showTimeoutId = setTimeout(() => {
				const rect = positionElement.getBoundingClientRect();

				setTooltipStyle({
					left: rect.left + rect.width / 2,
					top: rect.top,
				});
			}, TOOLTIP_SHOW_DELAY);
		};

		hoverElement.addEventListener('mouseleave', handleMouseLeave);
		hoverElement.addEventListener('mouseover', handleMouseOver);

		return () => {
			clearTimeout(showTimeoutId);
			hoverElement.removeEventListener('mouseleave', handleMouseLeave);
			hoverElement.removeEventListener('mouseover', handleMouseOver);
		};
	}, [hoverElement, positionElement]);

	return tooltipStyle ? (
		<ReactPortal className="cadmin">
			<ClayTooltip
				alignPosition="top"
				className="page-editor__tooltip position-fixed"
				id={tooltipId}
				show
				style={tooltipStyle}
			>
				{label}
			</ClayTooltip>
		</ReactPortal>
	) : null;
}

function capitalize(str) {
	return `${str.substring(0, 1).toUpperCase()}${str.substring(1)}`;
}

function SpacingSelectorBackground() {
	return (
		<svg
			fill="none"
			height="160"
			viewBox="0 0 240 160"
			width="240"
			xmlns="http://www.w3.org/2000/svg"
		>
			<path d="M0 1H240L199.331 31H41.6736L0 1Z" fill="#FFEDE0" />

			<path d="M0 160H240L198.5 130H41L0 160Z" fill="#FFEDE0" />

			<path d="M42 32.475L0 0V160L42 129V32.475Z" fill="#FFF4EC" />

			<path d="M198 32.475L240 0V160L198 129V32.475Z" fill="#FFF4EC" />

			<path
				d="M151 69.5471L193 37V124L151 91.0029V69.5471Z"
				fill="#EDF9F0"
			/>

			<path
				d="M89 69.5471L47 37V124L89 91.0029V69.5471Z"
				fill="#EDF9F0"
			/>

			<path d="M46 36.5L194 36L151.222 70H89.5L46 36.5Z" fill="#E4F6E9" />

			<path
				d="M46 124.5L194 125L151.222 91H89.5L46 124.5Z"
				fill="#E4F6E9"
			/>

			<rect height="88" stroke="#CBEBD3" width="147" x="46.5" y="36.5" />

			<rect height="22" stroke="#CBEBD3" width="63" x="88.5" y="69.5" />

			<path d="M41.5 31.5H198.5V129.5H41.5V31.5Z" stroke="#FFDCC2" />

			<path d="M41.5 31.5L0.5 0.5" stroke="#FFDCC2" />

			<path d="M198.5 31.5L239.5 0.5" stroke="#FFDCC2" />

			<path d="M198.5 129.5L239.5 159" stroke="#FFDCC2" />

			<path d="M41.5 129.5L0.5 159.5" stroke="#FFDCC2" />

			<path d="M151.5 69.5L193.5 36.5" stroke="#CBEBD3" />

			<path d="M88.5 69.5L46.5 36.5" stroke="#CBEBD3" />

			<path d="M88.5 91.5L46.5 124" stroke="#CBEBD3" />

			<path d="M151.5 91.5L193.5 124" stroke="#CBEBD3" />

			<rect height="159" stroke="#FFDCC2" width="239" x="0.5" y="0.5" />
		</svg>
	);
}
