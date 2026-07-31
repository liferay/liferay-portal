/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayAIButton} from '@clayui/button';
import {ClayInput, ClayInputGroupAI} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {
	InternalDispatch,
	getFocusableList,
	useControlledState,
	useId,
} from '@clayui/shared';
import React, {useRef, useState} from 'react';

import DropDown from './DropDown';
import {FocusMenu} from './FocusMenu';

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

interface IPromptProps {

	/**
	 * Aria label for the close button.
	 */
	closeAriaLabel?: string;

	/**
	 * Callback for when the composer is dismissed with the close button.
	 */
	onClose?: () => void;

	/**
	 * Callback for when the prompt is submitted with the current value.
	 */
	onSubmit?: (value: string) => void;

	/**
	 * Placeholder for the prompt input.
	 */
	placeholder?: string;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * Aria label for the submit button.
	 */
	submitAriaLabel?: string;
}

function Prompt({
	closeAriaLabel = 'Close',
	onClose,
	onSubmit,
	placeholder = 'What do you need?',
	spritemap,
	submitAriaLabel = 'Submit prompt',
}: IPromptProps) {
	const [value, setValue] = useState('');

	return (
		<div className="dropdown-section">
			<form
				onSubmit={(event) => {
					event.preventDefault();

					const trimmedValue = value.trim();

					if (trimmedValue) {
						onSubmit?.(trimmedValue);
					}
				}}
			>
				<ClayInputGroupAI
					autoFocus
					onChange={(event) => setValue(event.target.value)}
					placeholder={placeholder}
					value={value}
				>
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
				</ClayInputGroupAI>
			</form>
		</div>
	);
}

interface IMenuStateProps {
	active: boolean;
	items: Array<IItem>;
	menuRef: React.RefObject<HTMLDivElement>;
	onSelect: (item: IItem) => void;
	spritemap?: string;
}

// Focus moves into the menu on open so the options are reachable by keyboard.

function AIMenuState({
	active,
	items,
	menuRef,
	onSelect,
	spritemap,
}: IMenuStateProps) {
	return (
		<FocusMenu
			condition={active}
			onRender={() => {
				setTimeout(() => {
					const list = getFocusableList(menuRef);

					if (list.length) {
						list[0]!.focus();
					}
				}, 10);
			}}
		>
			<DropDown.ItemList>
				{items.map((item, index) => (
					<DropDown.Item
						key={index}
						onClick={() => onSelect(item)}
						spritemap={spritemap}
						symbolLeft={item.symbolLeft}
						symbolRight={item.symbolRight}
					>
						{item.label}
					</DropDown.Item>
				))}
			</DropDown.ItemList>
		</FocusMenu>
	);
}

interface IWorkingStateProps {
	onStop: () => void;
	spritemap?: string;
	stopAriaLabel: string;
	workingLabel: string;
}

function AIWorkingState({
	onStop,
	spritemap,
	stopAriaLabel,
	workingLabel,
}: IWorkingStateProps) {
	return (
		<div
			aria-live="polite"
			className="align-items-center d-flex dropdown-section justify-content-between"
		>
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
				monospaced
				onClick={onStop}
				size="sm"
				spritemap={spritemap}
				symbol="square"
				type="button"
			/>
		</div>
	);
}

interface IResultStateProps {
	acceptLabel: string;
	onAccept?: () => void;
	onReset?: () => void;
	resetAriaLabel: string;
	spritemap?: string;
}

function AIResultState({
	acceptLabel,
	onAccept,
	onReset,
	resetAriaLabel,
	spritemap,
}: IResultStateProps) {
	return (
		<div
			aria-live="polite"
			className="align-items-center c-gap-2 d-flex dropdown-section"
		>
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
				link
				monospaced
				onClick={onReset}
				size="sm"
				spritemap={spritemap}
				symbol="reset"
				type="button"
			/>
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
	 * Callback fired when the running request is stopped (`working` state).
	 * Use it to cancel the in-flight AI request; the flow returns to `menu`.
	 */
	onStop?: () => void;

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
	onStop,
	onSubmit,
	openOnClick = true,
	placeholder = 'What do you need?',
	resetAriaLabel = 'Reset to original value',
	spritemap,
	stopAriaLabel = 'Stop',
	submitAriaLabel = 'Submit prompt',
	trigger,
	workingLabel = 'Working on it…',
}: Props) {
	const menuId = useId();

	const menuElementRef = useRef<HTMLDivElement>(null);

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

	const toggle = () => {
		if (!internalActive || dismissable) {
			setInternalActive(!internalActive);
		}
	};

	const states: Record<AIState, React.ReactNode> = {
		menu: (
			<AIMenuState
				active={internalActive}
				items={items}
				menuRef={menuElementRef}
				onSelect={(item) => {
					item.onClick?.();

					onItemClick?.(item);

					setAiState('prompt');
				}}
				spritemap={spritemap}
			/>
		),
		prompt: (
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
		),
		result: (
			<AIResultState
				acceptLabel={acceptLabel}
				onAccept={onAccept}
				onReset={onReset}
				resetAriaLabel={resetAriaLabel}
				spritemap={spritemap}
			/>
		),
		working: (
			<AIWorkingState
				onStop={() => {
					onStop?.();

					setAiState('menu');
				}}
				spritemap={spritemap}
				stopAriaLabel={stopAriaLabel}
				workingLabel={workingLabel}
			/>
		),
	};

	return (
		<>
			{React.cloneElement(trigger, {
				'aria-controls': internalActive ? menuId : undefined,
				'aria-expanded': internalActive,
				'aria-haspopup': true,
				'onClick': (event: React.MouseEvent) => {
					trigger.props.onClick?.(event);

					if (openOnClick) {
						toggle();
					}
				},
				'onKeyDown': (event: React.KeyboardEvent) => {
					trigger.props.onKeyDown?.(event);

					if (
						openOnClick &&
						(event.key === 'Enter' || event.key === ' ')
					) {
						event.preventDefault();

						toggle();
					}
				},
				'ref': (node: HTMLElement | null) => {
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
				id={menuId}
				onActiveChange={setInternalActive}
				onKeyDown={(event) => {
					if (
						aiState !== 'menu' ||
						(event.key !== 'ArrowDown' && event.key !== 'ArrowUp')
					) {
						return;
					}

					event.preventDefault();

					const list = getFocusableList(menuElementRef);

					if (!list.length) {
						return;
					}

					const currentIndex = list.indexOf(
						document.activeElement as HTMLElement
					);

					const nextIndex =
						event.key === 'ArrowDown'
							? (currentIndex + 1) % list.length
							: (currentIndex - 1 + list.length) % list.length;

					list[nextIndex]!.focus();
				}}
				ref={menuElementRef}
				triggerRef={triggerElementRef}
			>
				{states[aiState]}
			</DropDown.Menu>
		</>
	);
}

ClayDropDownWithAI.displayName = 'ClayDropDownWithAI';
