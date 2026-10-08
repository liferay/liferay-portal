/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {FocusScope, InternalDispatch, useControlledState} from '@clayui/shared';
import classNames from 'classnames';
import React, {useEffect, useRef, useState} from 'react';

type ConfigMaxMin = {
	max: number;
	min: number;
};

type ConfigAmpm = {
	am: string;
	pm: string;
};

type Config = {
	ampm: ConfigAmpm;
	hours: ConfigMaxMin;
	minutes: ConfigMaxMin;
};

export type InputAmPm = '--' | 'AM' | 'PM';

export type Input = {
	ampm?: InputAmPm;
	hours: '--' | string;
	minutes: '--' | string;
};

enum TimeType {
	minutes = 'minutes',
	hours = 'hours',
	ampm = 'ampm',
}

interface IProps
	extends Omit<
		React.HTMLAttributes<HTMLDivElement>,
		'onChange' | 'defaultValue'
	> {

	/**
	 * Add the labels for the input elements and the input clear button,
	 * use this to provide accessibility and internationalization.
	 * By default the text is in English.
	 */
	ariaLabels?: {
		ampm: string;
		clear: string;
		hours: string;
		minutes: string;
		timeDown: string;
		timeUp: string;
	};

	/**
	 * Range settings for 12 or 24 hours.
	 */
	config?: {
		use12Hours: Config;
		use24Hours: Config;
	};

	/**
	 * Property to set the default value (uncontrolled).
	 */
	defaultValue?: Input;

	/**
	 * Flag to disable user interactions on the component.
	 */
	disabled?: boolean;

	/**
	 * Flag to indicate if show time icon or not.
	 */
	icon?: boolean;

	/**
	 * Id to be applied to `clay-time-hours` input.
	 */
	id?: string;

	/**
	 * Name attribute for the hidden input (used for form submission).
	 */
	name?: string;

	/**
	 * Called when input values change hour, minutes or ampm (controlled).
	 */
	onChange?: InternalDispatch<Input>;

	/**
	 * Called when input values change hour, minutes or ampm (controlled).
	 * @deprecated since v3.52.0 - use `onChange` instead.
	 */
	onInputChange?: InternalDispatch<Input>;

	/**
	 * The path to the SVG spritemap file containing the icons.
	 */
	spritemap?: string;

	/**
	 * Flag to indicate the timezone of the user.
	 */
	timezone?: string;

	/**
	 * Flag to indicate if 12-hour use, when true, should show am/pm input.
	 */
	use12Hours?: boolean;

	/**
	 * Sets the values for the hour, minute, or am/pm input (controlled).
	 */
	value?: Input;

	/**
	 * Sets the values for the hour, minute, or am/pm input (controlled).
	 * @deprecated since v3.52.0 - use `value` instead.
	 */
	values?: Input;
}

const DEFAULT_VALUE = '--';
const DEFAULT_CONFIG = {
	use12Hours: {
		ampm: {
			am: 'AM',
			pm: 'PM',
		},
		hours: {
			max: 12,
			min: 1,
		},
		minutes: {
			max: 59,
			min: 0,
		},
	},
	use24Hours: {
		ampm: {
			am: 'AM',
			pm: 'PM',
		},
		hours: {
			max: 23,
			min: 0,
		},
		minutes: {
			max: 59,
			min: 0,
		},
	},
};

const regex = /^\d+$/;

function TimePicker({
	ariaLabels = {
		ampm: 'Select time of day (AM/PM) using up (PM) and down (AM) arrow keys',
		clear: 'Delete the entered time',
		hours: 'Enter the hour in 00:00 format',
		minutes: 'Enter the minutes in 00:00 format',
		timeDown: 'Time down',
		timeUp: 'Time up',
	},
	config = DEFAULT_CONFIG,
	disabled = false,
	defaultValue = {
		hours: DEFAULT_VALUE,
		minutes: DEFAULT_VALUE,
	},
	icon = false,
	id,
	name,
	onChange,
	onInputChange,
	spritemap,
	timezone,
	use12Hours = false,
	value,
	values,
}: IProps) {
	const [internalValue, setValue] = useControlledState({
		defaultName: 'defaultValue',
		defaultValue,
		handleName: 'onChange',
		name: 'value',
		onChange: onChange ?? onInputChange,
		value: value ?? values,
	});
	const useConfig: Config = config[use12Hours ? 'use12Hours' : 'use24Hours'];
	const [actionVisible, setActionVisible] = useState(false);
	const elementRef = useRef<null | HTMLDivElement>(null);
	const defaultFocused = {
		configName: TimeType.hours,
		focused: false,
	};
	const [currentInputFocused, setCurrentInputFocused] = useState<{
		configName: TimeType;
		focused: boolean;
	}>(defaultFocused);
	const handleMaxAndMin = (value: string, config: ConfigMaxMin) => {
		const newValue = value.substring(value.length - 2, value.length);
		const intrinsicValue = Number(newValue);
		if (intrinsicValue > config.max) {
			return String(config.min);
		}
		else if (intrinsicValue < config.min) {
			return String(config.max);
		}

		return newValue;
	};
	const handleKeyDown = (
		event: React.KeyboardEvent<HTMLInputElement>,
		value: string,
		configName: TimeType
	) => {
		const config = useConfig[configName];
		const intrinsicValue = Number(value);
		const onValue = (newValue: string | number) => {
			const newVal =
				configName === TimeType.ampm
					? newValue
					: handleMaxAndMin(String(newValue), config as ConfigMaxMin);

			return setValue({
				...internalValue,

				// eslint-disable-next-line sort-keys
				[configName]: String(newVal).padStart(2, '0'),
			});
		};
		switch (event.key) {
			case 'Backspace':
				onValue(DEFAULT_VALUE);
				break;
			case 'ArrowUp':
				event.preventDefault();
				event.stopPropagation();
				if (configName === TimeType.ampm) {
					onValue((config as ConfigAmpm).pm);
				}
				else {
					onValue(
						value !== DEFAULT_VALUE
							? intrinsicValue + 1
							: (config as ConfigMaxMin).min
					);
				}
				break;
			case 'ArrowDown':
				event.preventDefault();
				event.stopPropagation();
				if (configName === TimeType.ampm) {
					onValue((config as ConfigAmpm).am);
				}
				else {
					onValue(
						value !== DEFAULT_VALUE
							? intrinsicValue - 1
							: (config as ConfigMaxMin).max
					);
				}
				break;
			default:
				if (regex.test(event.key) && configName !== TimeType.ampm) {
					const maxSecondDigit = Math.floor(
						(config as ConfigMaxMin).max / 10
					);
					const minFirstDigit = (config as ConfigMaxMin).min;
					const keyVal =
						intrinsicValue < minFirstDigit
							? minFirstDigit
							: event.key;
					const newVal =
						Number(value) > maxSecondDigit
							? `0${keyVal}`
							: (value && value !== DEFAULT_VALUE ? value : '') +
								keyVal;
					onValue(newVal);
				}
				else if (
					configName === TimeType.ampm &&
					(event.key === 'a' || event.key === 'p')
				) {
					onValue(
						event.key === 'a'
							? (config as ConfigAmpm).am
							: (config as ConfigAmpm).pm
					);
				}
				break;
		}
	};
	const handleAction = (direction: number) => {
		const {configName} = currentInputFocused;
		const config = useConfig[configName];
		const prevValue = internalValue[configName];
		let value;
		if (configName === TimeType.ampm) {
			value =
				direction === 1
					? (config as ConfigAmpm).pm
					: (config as ConfigAmpm).am;
		}
		else {
			value = handleMaxAndMin(
				String(
					prevValue !== DEFAULT_VALUE
						? Number(prevValue) + direction
						: direction === -1
							? (config as ConfigMaxMin).max
							: 0
				),
				config as ConfigMaxMin
			);
		}
		setCurrentInputFocused({
			configName,
			focused: true,
		});
		setValue({
			...internalValue,

			// eslint-disable-next-line sort-keys
			[configName]: String(value).padStart(2, '0'),
		});
	};
	const handleDocumentClick = (event: Event) => {
		if (
			elementRef.current &&
			event.target !== null &&
			!elementRef.current.contains(event.target as HTMLDivElement)
		) {
			setActionVisible(false);
			setCurrentInputFocused(defaultFocused);
		}
	};
	const handleInputFocus = (configName: TimeType) => {
		setActionVisible(true);
		setCurrentInputFocused({
			configName,
			focused: true,
		});
	};
	useEffect(() => {
		document.addEventListener('click', handleDocumentClick);

		return () => {
			document.removeEventListener('click', handleDocumentClick);
		};
	}, []);
	const visibleActionReset =
		actionVisible &&
		((internalValue.ampm !== DEFAULT_VALUE &&
			internalValue.ampm !== null) ||
			internalValue.hours !== DEFAULT_VALUE ||
			internalValue.minutes !== DEFAULT_VALUE);

	return (
		<div className="clay-time">
			<ClayInput.Group>
				{icon && (
					<ClayInput.GroupItem shrink>
						<ClayInput.GroupText>
							<ClayIcon spritemap={spritemap} symbol="time" />
						</ClayInput.GroupText>
					</ClayInput.GroupItem>
				)}

				<FocusScope arrowKeysLeftRight arrowKeysUpDown={false}>
					<ClayInput.GroupItem>
						<div
							className={classNames('form-control', {
								disabled,
							})}
							data-testid="formControl"
							onMouseEnter={() => {
								if (!disabled) {
									setActionVisible(true);
								}
							}}
							onMouseLeave={() => {
								if (!currentInputFocused.focused && !disabled) {
									setActionVisible(false);
								}
							}}
							ref={elementRef}
						>
							<div className="clay-time-edit">
								<input
									aria-label={ariaLabels.hours}
									className={classNames(
										'clay-time-hours form-control-inset',
										{
											focus:
												currentInputFocused.configName ===
													TimeType.hours &&
												currentInputFocused.focused,
										}
									)}
									data-testid="hours"
									disabled={disabled}
									id={id}
									maxLength={2}
									onFocus={() =>
										handleInputFocus(TimeType.hours)
									}
									onKeyDown={(event) =>
										handleKeyDown(
											event,
											internalValue.hours,
											TimeType.hours
										)
									}
									readOnly
									type="text"
									value={internalValue.hours}
								/>

								<span className="clay-time-divider">:</span>

								<input
									aria-label={ariaLabels.minutes}
									className={classNames(
										'clay-time-minutes form-control-inset',
										{
											focus:
												currentInputFocused.configName ===
												TimeType.minutes,
										}
									)}
									data-testid="minutes"
									disabled={disabled}
									maxLength={2}
									onFocus={() =>
										handleInputFocus(TimeType.minutes)
									}
									onKeyDown={(event) =>
										handleKeyDown(
											event,
											internalValue.minutes,
											TimeType.minutes
										)
									}
									readOnly
									type="text"
									value={internalValue.minutes}
								/>

								{use12Hours && (
									<input
										aria-label={ariaLabels.ampm}
										className={classNames(
											'clay-time-ampm form-control-inset',
											{
												focus:
													currentInputFocused.configName ===
													TimeType.ampm,
											}
										)}
										data-testid="ampm"
										disabled={disabled}
										maxLength={2}
										onFocus={() =>
											handleInputFocus(TimeType.ampm)
										}
										onKeyDown={(event) =>
											handleKeyDown(
												event,
												internalValue.ampm as InputAmPm,
												TimeType.ampm
											)
										}
										readOnly
										type="text"
										value={
											internalValue.ampm || DEFAULT_VALUE
										}
									/>
								)}

								{name && (
									<input
										name={name}
										type="hidden"
										value={`${internalValue.hours}:${internalValue.minutes}`}
									/>
								)}
							</div>

							<div className="clay-time-action-group">
								<div
									className="clay-time-action-group-item"
									data-testid="containerReset"
									style={{
										opacity: visibleActionReset ? 1 : 0,
										pointerEvents: visibleActionReset
											? 'auto'
											: 'none',
									}}
								>
									<ClayButton
										aria-label={ariaLabels.clear}
										className="clay-time-clear-btn"
										disabled={disabled}
										displayType="unstyled"
										onClick={() =>
											setValue(
												use12Hours
													? {
															ampm: DEFAULT_VALUE,
															hours: DEFAULT_VALUE,
															minutes:
																DEFAULT_VALUE,
														}
													: {
															hours: DEFAULT_VALUE,
															minutes:
																DEFAULT_VALUE,
														}
											)
										}
										tabIndex={visibleActionReset ? 0 : -1}
									>
										<ClayIcon
											spritemap={spritemap}
											symbol="times-circle"
										/>
									</ClayButton>
								</div>

								<div
									className="clay-time-action-group-item"
									data-testid="containerSpin"
									style={{
										opacity: actionVisible ? 1 : 0,
									}}
								>
									<div
										className="btn-group-vertical clay-time-inner-spin"
										role="group"
									>
										<ClayButton
											aria-label={ariaLabels.timeUp}
											className="clay-time-inner-spin-btn clay-time-inner-spin-btn-inc"
											data-testid="spinInc"
											disabled={disabled}
											displayType="secondary"
											onClick={() => handleAction(1)}
										>
											<ClayIcon
												spritemap={spritemap}
												symbol="angle-up"
											/>
										</ClayButton>

										<ClayButton
											aria-label={ariaLabels.timeDown}
											className="clay-time-inner-spin-btn clay-time-inner-spin-btn-dec"
											data-testid="spinDec"
											disabled={disabled}
											displayType="secondary"
											onClick={() => handleAction(-1)}
										>
											<ClayIcon
												spritemap={spritemap}
												symbol="angle-down"
											/>
										</ClayButton>
									</div>
								</div>
							</div>
						</div>
					</ClayInput.GroupItem>
				</FocusScope>

				{timezone && (
					<ClayInput.GroupItem shrink>
						<ClayInput.GroupText>{`(${timezone})`}</ClayInput.GroupText>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</div>
	);
}

export default TimePicker;
