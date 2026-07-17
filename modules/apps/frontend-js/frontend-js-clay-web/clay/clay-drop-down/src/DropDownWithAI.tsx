/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayAIButton} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {InternalDispatch, useControlledState} from '@clayui/shared';
import React, {useRef} from 'react';

import DropDown from './DropDown';

type AIState = 'menu' | 'prompt' | 'result' | 'working';

interface IItem {

	/**
	 * Text of the menu option.
	 */
	label: string;

	/**
	 * Callback fired when the option is clicked, before moving to `prompt`.
	 */
	onClick?: () => void;

	/**
	 * Icon rendered on the left of the option.
	 */
	symbolLeft?: string;

	/**
	 * Icon rendered on the right of the option.
	 */
	symbolRight?: string;
}

interface IPromptProps
	extends Omit<
		React.InputHTMLAttributes<HTMLInputElement>,
		'onChange' | 'onSubmit'
	> {

	/**
	 * Aria label for the close button.
	 */
	closeAriaLabel?: string;

	/**
	 * Initial value of the input (uncontrolled).
	 */
	defaultValue?: string;

	/**
	 * Callback for when the input value changes (controlled).
	 */
	onChange?: InternalDispatch<string>;

	/**
	 * Callback for when the composer is dismissed with the close button.
	 */
	onClose?: () => void;

	/**
	 * Callback for when the prompt is submitted with the current value.
	 */
	onSubmit?: (value: string) => void;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * Aria label for the submit button.
	 */
	submitAriaLabel?: string;

	/**
	 * Current value of the input (controlled).
	 */
	value?: string;
}

// The inline prompt composer (input + AI submit + close) shown in the `prompt`
// state. It is intentionally private to this flow.

function Prompt({
	closeAriaLabel = 'Close',
	defaultValue = '',
	onChange,
	onClose,
	onSubmit,
	placeholder = 'What do you need?',
	spritemap,
	submitAriaLabel = 'Submit prompt',
	value: valueProp,
	...otherProps
}: IPromptProps) {
	const [value, setValue] = useControlledState({
		defaultName: 'defaultValue',
		defaultValue,
		handleName: 'onChange',
		name: 'value',
		onChange,
		value: valueProp,
	});

	return (
		<div className="dropdown-section">
			<form
				onSubmit={(event) => {
					event.preventDefault();

					if (value.trim()) {
						onSubmit?.(value);
					}
				}}
			>
				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							{...otherProps}
							autoFocus
							className="form-control-sm rounded-lg"
							onChange={(event) => setValue(event.target.value)}
							placeholder={placeholder}
							type="text"
							value={value}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<ClayAIButton
							aria-label={submitAriaLabel}
							disabled={!value.trim()}
							monospaced
							size="sm"
							spritemap={spritemap}
							type="submit"
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<ClayAIButton
							aria-label={closeAriaLabel}
							link
							monospaced
							onClick={onClose}
							size="sm"
							spritemap={spritemap}
							symbol="times"
							type="button"
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</form>
		</div>
	);
}

export type Props = {

	/**
	 * Visible text of the accept button. Defaults to `Accept Suggestion`.
	 */
	acceptLabel?: string;

	/**
	 * Flag to indicate if the flow is open (controlled). Opening it (e.g. from
	 * a right-click on a field) is the consumer's responsibility.
	 */
	active?: boolean;

	/**
	 * Current state of the AI flow (controlled).
	 */
	aiState?: AIState;

	/**
	 * Aria label for the prompt's close button. Defaults to `Close`.
	 */
	closeAriaLabel?: string;

	/**
	 * Initial open state (uncontrolled).
	 */
	defaultActive?: boolean;

	/**
	 * Initial state of the AI flow (uncontrolled). Defaults to `menu`.
	 */
	defaultAiState?: AIState;

	/**
	 * The AI options shown in the `menu` state.
	 */
	items: Array<IItem>;

	/**
	 * Callback fired when the suggestion is accepted (`result` state).
	 */
	onAccept?: () => void;

	/**
	 * Callback for when the open state changes.
	 */
	onActiveChange?: InternalDispatch<boolean>;

	/**
	 * Callback for when the AI state changes.
	 */
	onAiStateChange?: InternalDispatch<AIState>;

	/**
	 * Callback fired when a menu option is clicked, before moving to `prompt`.
	 */
	onItemClick?: (item: IItem) => void;

	/**
	 * Callback fired when the suggestion is reset/rejected (`result` state).
	 */
	onReset?: () => void;

	/**
	 * Callback fired with the prompt value when it is submitted (`prompt`
	 * state). Resolving the AI request and moving to `result` is the
	 * consumer's responsibility.
	 */
	onSubmit?: (value: string) => void;

	/**
	 * Flag to open the flow when the trigger is clicked. Turn it off when the
	 * consumer wants to control opening itself (e.g. only on right-click of a
	 * field). Defaults to `true`.
	 */
	openOnClick?: boolean;

	/**
	 * Placeholder for the prompt input. Defaults to `What do you need?`.
	 */
	placeholder?: string;

	/**
	 * Aria label for the reset button.
	 */
	resetAriaLabel?: string;

	/**
	 * The text the consumer captured (e.g. from a field selection) that the
	 * AI acts on. Shown as context in the `prompt` state.
	 */
	selectedText?: string;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * Aria label for the working state's stop button. Defaults to `Stop`.
	 */
	stopAriaLabel?: string;

	/**
	 * Aria label for the prompt's submit button. Defaults to `Submit prompt`.
	 */
	submitAriaLabel?: string;

	/**
	 * Element that triggers the AI dropdown.
	 */
	trigger: React.ReactElement;

	/**
	 * Text shown in the `working` state.
	 */
	workingLabel?: string;
};

export function ClayDropDownWithAI({
	acceptLabel = 'Accept Suggestion',
	active,
	aiState: aiStateProp,
	closeAriaLabel = 'Close',
	defaultActive,
	defaultAiState = 'menu',
	items,
	onAccept,
	onActiveChange,
	onAiStateChange,
	onItemClick,
	onReset,
	onSubmit,
	openOnClick = true,
	placeholder = 'What do you need?',
	resetAriaLabel = 'Reset to original value',
	selectedText,
	spritemap,
	stopAriaLabel = 'Stop',
	submitAriaLabel = 'Submit prompt',
	trigger,
	workingLabel = 'Working on it…',
}: Props) {
	const triggerElementRef = useRef<HTMLElement | null>(null);

	const [aiState, setAiState] = useControlledState({
		defaultName: 'defaultAiState',
		defaultValue: defaultAiState,
		handleName: 'onAiStateChange',
		name: 'aiState',
		onChange: onAiStateChange,
		value: aiStateProp,
	});

	const [internalActive, setInternalActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive ?? false,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: active,
	});

	// The working and result states can only be dismissed by their own controls
	// (stop / accept / reset), not by clicking the trigger or outside the box.

	const dismissable = aiState === 'menu' || aiState === 'prompt';

	return (
		<>
			{React.cloneElement(trigger, {
				onClick: (event: React.MouseEvent) => {
					trigger.props.onClick?.(event);

					if (openOnClick && (!internalActive || dismissable)) {
						setInternalActive(!internalActive);
					}
				},
				ref: (node: HTMLElement | null) => {
					triggerElementRef.current = node;

					// Preserve a ref the consumer set on the trigger.

					const {ref} = trigger as any;

					if (typeof ref === 'function') {
						ref(node);
					}
					else if (ref) {
						ref.current = node;
					}
				},
			})}

			<DropDown.Menu
				active={internalActive}
				ai
				alignElementRef={triggerElementRef}
				closeOnClickOutside={dismissable}
				hasLeftSymbols
				hasRightSymbols
				onActiveChange={setInternalActive}
				triggerRef={triggerElementRef}
			>
				{aiState === 'menu' && (
					<DropDown.ItemList>
						{items.map((item, index) => (
							<DropDown.Item
								key={index}
								onClick={() => {
									item.onClick?.();

									onItemClick?.(item);

									setAiState('prompt');
								}}
								spritemap={spritemap}
								symbolLeft={item.symbolLeft}
								symbolRight={item.symbolRight}
							>
								{item.label}
							</DropDown.Item>
						))}
					</DropDown.ItemList>
				)}

				{aiState === 'prompt' && (
					<>
						{selectedText ? (
							<div className="dropdown-section text-secondary text-truncate">
								{selectedText}
							</div>
						) : null}

						<Prompt
							closeAriaLabel={closeAriaLabel}
							onClose={() => setAiState('menu')}
							onSubmit={(value) => {
								onSubmit?.(value);

								setAiState('working');
							}}
							placeholder={placeholder}
							spritemap={spritemap}
							submitAriaLabel={submitAriaLabel}
						/>
					</>
				)}

				{aiState === 'working' && (
					<div className="align-items-center d-flex dropdown-section">
						<span className="align-items-center d-flex text-purple">
							<ClayIcon
								className="mr-2"
								spritemap={spritemap}
								symbol="stars"
							/>

							{workingLabel}
						</span>

						<ClayAIButton
							aria-label={stopAriaLabel}
							className="ml-auto"
							monospaced
							onClick={() => setAiState('menu')}
							size="sm"
							spritemap={spritemap}
							symbol="square"
							type="button"
						/>
					</div>
				)}

				{aiState === 'result' && (
					<div className="align-items-center d-flex dropdown-section">
						<ClayAIButton
							label={acceptLabel}
							onClick={onAccept}
							size="sm"
							spritemap={spritemap}
							symbol="check"
							type="button"
						/>

						<ClayAIButton
							aria-label={resetAriaLabel}
							className="ml-2"
							link
							monospaced
							onClick={onReset}
							size="sm"
							spritemap={spritemap}
							symbol="reset"
							type="button"
						/>
					</div>
				)}
			</DropDown.Menu>
		</>
	);
}

ClayDropDownWithAI.displayName = 'ClayDropDownWithAI';
