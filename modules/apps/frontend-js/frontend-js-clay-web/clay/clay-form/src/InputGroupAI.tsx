/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

import ClayForm from './Form';
import ClayInput from './Input';

export type TAIState = 'focused' | 'result' | 'result-readonly' | 'working';

const MAX_ROWS = 4;

function adjustTextAreaHeight(textArea: HTMLTextAreaElement) {
	if (!textArea.value) {
		resetTextAreaHeight(textArea);

		return;
	}

	const style = window.getComputedStyle(textArea);

	const lineHeight =
		parseFloat(style.lineHeight) || parseFloat(style.fontSize) * 1.2;
	const maxHeight = lineHeight * MAX_ROWS;

	textArea.style.height = 'auto';
	textArea.style.height = `${Math.min(textArea.scrollHeight, maxHeight)}px`;
	textArea.style.overflowY =
		textArea.scrollHeight > maxHeight ? 'auto' : 'hidden';
}

function resetTextAreaHeight(textArea: HTMLTextAreaElement) {
	textArea.style.height = '';
	textArea.style.overflowY = '';
}

interface IProps
	extends Omit<
		React.TextareaHTMLAttributes<HTMLTextAreaElement>,
		'onSubmit'
	> {

	/**
	 * State of the AI interaction. When not set, the component tracks focus
	 * internally and applies the `focused` state on its own.
	 */
	aiState?: TAIState;

	/**
	 * Custom controls rendered after the input, replacing the component's
	 * default submit and retry buttons. When omitted, the defaults render.
	 */
	children?: React.ReactNode;

	/**
	 * Messages used by the component. Use this to translate the labels.
	 */
	messages?: {
		retry?: string;
		submit?: string;
		suggestion?: string;
		working?: string;
	};

	/**
	 * Handler called when the retry button is clicked. The retry button is
	 * only rendered in the `result` state.
	 */
	onRetryClick?: (event: React.MouseEvent<HTMLButtonElement>) => void;

	/**
	 * Path to the spritemap that Icon should use when referencing symbols.
	 */
	spritemap?: string;
}

const DEFAULT_MESSAGES = {
	retry: 'Retry',
	submit: 'Submit',
	suggestion: 'Suggestion',
	working: 'Working on it...',
};

/**
 * Multiline prompt input for AI interactions. Renders as an `input-group-ai`
 * with an auto-growing textarea and a submit button, plus a retry button, a
 * suggestion label, and readonly behavior depending on `aiState`. Passing
 * `children` replaces the default submit and retry buttons with custom
 * controls.
 *
 * Pressing `Enter` submits the closest enclosing form; `Shift + Enter`
 * inserts a line break. Textarea props (`value`, `onChange`, `placeholder`,
 * etc.) are forwarded to the textarea, as is `ref`, while `className` is
 * applied to the input group.
 */
const InputGroupAI = React.forwardRef<HTMLTextAreaElement, IProps>(
	(
		{
			aiState,
			children,
			className,
			messages,
			onBlur,
			onChange,
			onFocus,
			onKeyDown,
			onRetryClick,
			readOnly,
			spritemap,
			value,
			...otherProps
		}: IProps,
		ref
	) => {
		const [focused, setFocused] = React.useState<boolean>(false);

		const textAreaRef = React.useRef<HTMLTextAreaElement | null>(null);

		const mergedRef = React.useCallback(
			(node: HTMLTextAreaElement | null) => {
				textAreaRef.current = node;

				if (typeof ref === 'function') {
					ref(node);
				}
				else if (ref) {
					ref.current = node;
				}
			},
			[ref]
		);

		const {retry, submit, suggestion, working} = {
			...DEFAULT_MESSAGES,
			...messages,
		};

		if (!aiState && focused) {
			aiState = 'focused';
		}

		const isResult = aiState === 'result' || aiState === 'result-readonly';
		const isWorking = aiState === 'working';

		const displayValue = isWorking ? working : value;

		React.useEffect(() => {
			if (textAreaRef.current) {
				adjustTextAreaHeight(textAreaRef.current);
			}
		}, [displayValue]);

		const handleKeyDown = (
			event: React.KeyboardEvent<HTMLTextAreaElement>
		) => {
			onKeyDown?.(event);

			if (event.key !== 'Enter') {
				event.stopPropagation();

				return;
			}

			const textArea = event.target as HTMLTextAreaElement;

			if (event.shiftKey) {
				setTimeout(() => adjustTextAreaHeight(textArea), 0);

				return;
			}

			event.preventDefault();

			const form = textArea.closest('form');

			if (form?.requestSubmit) {
				form.requestSubmit();
			}
			else {
				form?.dispatchEvent(
					new Event('submit', {
						bubbles: true,
						cancelable: true,
					})
				);
			}

			resetTextAreaHeight(textArea);
		};

		return (
			<ClayInput.Group
				className={classNames('input-group-ai', className)}
				data-ai-state={aiState}
			>
				<ClayInput.GroupItem>
					<div className="form-control">
						<div className="autofit-row">
							{isWorking && (
								<div className="autofit-col">
									<ClayIcon
										spritemap={spritemap}
										symbol="stars"
									/>
								</div>
							)}

							<div className="autofit-col autofit-col-expand">
								<textarea
									{...otherProps}
									className="form-control-inset"
									onBlur={(event) => {
										setFocused(false);

										onBlur?.(event);
									}}
									onChange={(event) => {
										adjustTextAreaHeight(event.target);

										onChange?.(event);
									}}
									onFocus={(event) => {
										setFocused(true);

										onFocus?.(event);
									}}
									onKeyDown={handleKeyDown}
									readOnly={
										readOnly ||
										isWorking ||
										aiState === 'result-readonly'
									}
									ref={mergedRef}
									rows={1}
									value={displayValue}
								/>
							</div>
						</div>
					</div>

					{isResult && (
						<ClayForm.FeedbackGroup>
							<ClayForm.Text>
								<ClayForm.FeedbackIndicator
									spritemap={spritemap}
									symbol="stars"
								/>

								{suggestion}
							</ClayForm.Text>
						</ClayForm.FeedbackGroup>
					)}
				</ClayInput.GroupItem>

				{children ?? (
					<>
						{!isWorking && (
							<ClayInput.GroupItem shrink>
								<ClayButton
									aria-label={submit}
									disabled={
										typeof value === 'string' &&
										!value.trim()
									}
									displayType="primary"
									monospaced
									onClick={() => {
										if (textAreaRef.current) {
											resetTextAreaHeight(
												textAreaRef.current
											);
										}
									}}
									size="sm"
									type="submit"
								>
									<ClayIcon
										spritemap={spritemap}
										symbol="check"
									/>
								</ClayButton>
							</ClayInput.GroupItem>
						)}

						{aiState === 'result' && onRetryClick && (
							<ClayInput.GroupItem shrink>
								<ClayButton
									aria-label={retry}
									displayType="ai"
									monospaced
									onClick={onRetryClick}
									outline
									rounded
									size="sm"
									type="button"
								>
									<ClayIcon
										spritemap={spritemap}
										symbol="reload"
									/>
								</ClayButton>
							</ClayInput.GroupItem>
						)}
					</>
				)}
			</ClayInput.Group>
		);
	}
);

InputGroupAI.displayName = 'ClayInputGroupAI';

export default InputGroupAI;
