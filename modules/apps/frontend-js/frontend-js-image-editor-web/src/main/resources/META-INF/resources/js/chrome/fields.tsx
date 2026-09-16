/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClaySlider from '@clayui/slider';
import classNames from 'classnames';
import React, {useEffect, useRef, useState} from 'react';

interface FieldProps {
	id: string;
	label: string;
}

interface NumberInputProps {
	ariaLabel?: string;
	id: string;
	max?: number;
	min: number;
	onCommit: (value: number) => void;
	onPreview?: (value: number) => void;
	value: number;
}

function NumberInput({
	ariaLabel,
	id,
	max,
	min,
	onCommit,
	onPreview,
	value,
}: NumberInputProps) {
	const [draft, setDraft] = useState(String(value));

	useEffect(() => setDraft(String(value)), [value]);

	const clamp = (raw: number) =>
		Math.min(Math.max(raw, min), max ?? Infinity);

	const commit = () => {
		const parsed = Number.parseInt(draft, 10);

		if (Number.isNaN(parsed)) {
			setDraft(String(value));

			return;
		}

		onCommit(clamp(parsed));
	};

	const step = (direction: -1 | 1, large: boolean) => {
		const parsed = Number.parseInt(draft, 10);

		const next = clamp(
			(Number.isNaN(parsed) ? value : parsed) +
				direction * (large ? 10 : 1)
		);

		setDraft(String(next));

		onPreview?.(next);
	};

	return (
		<ClayInput
			aria-label={ariaLabel}
			id={id}
			max={max}
			min={Number.isFinite(min) ? min : undefined}
			onBlur={commit}
			onChange={(event) => setDraft(event.target.value)}
			onKeyDown={(event: React.KeyboardEvent) => {
				if (event.key === 'Enter') {
					event.preventDefault();
					commit();
				}
				else if (
					event.key === 'ArrowUp' ||
					event.key === 'ArrowDown'
				) {
					event.preventDefault();

					step(event.key === 'ArrowUp' ? 1 : -1, event.shiftKey);
				}
			}}
			sizing="sm"
			type="number"
			value={draft}
		/>
	);
}

interface ColorInputProps {
	ariaLabel?: string;
	className?: string;
	id: string;
	onCommit: (value: string) => void;
	onPreview: (value: string) => void;
	value: string;
}

function ColorInput({
	ariaLabel,
	className,
	id,
	onCommit,
	onPreview,
	value,
}: ColorInputProps) {
	const draggingRef = useRef(false);

	return (
		<input
			aria-label={ariaLabel}
			className={classNames(
				'editor-color-input form-control form-control-sm',
				className
			)}
			id={id}
			onBlur={() => {
				if (draggingRef.current) {
					draggingRef.current = false;

					onCommit(value);
				}
			}}
			onChange={(event) => {
				draggingRef.current = true;

				onPreview(event.target.value);
			}}
			type="color"
			value={value}
		/>
	);
}

export function NumberField({
	id,
	label,
	max,
	min = 1,
	onCommit,
	onPreview,
	suffix,
	value,
}: FieldProps & {
	max?: number;
	min?: number;
	onCommit: (value: number) => void;

	onPreview?: (value: number) => void;

	suffix?: string;
	value: number;
}) {
	const input = (
		<NumberInput
			id={id}
			max={max}
			min={min}
			onCommit={onCommit}
			onPreview={onPreview}
			value={value}
		/>
	);

	return (
		<ClayForm.Group small>
			<label htmlFor={id}>{label}</label>

			{suffix ? (
				<ClayInput.Group small>
					<ClayInput.GroupItem prepend>{input}</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ClayInput.GroupText>{suffix}</ClayInput.GroupText>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			) : (
				input
			)}
		</ClayForm.Group>
	);
}

export function TextField({
	id,
	label,
	onCommit,
	value,
}: FieldProps & {onCommit: (value: string) => void; value: string}) {
	const [draft, setDraft] = useState(value);

	useEffect(() => setDraft(value), [value]);

	const commit = () => {
		if (!draft.trim()) {
			setDraft(value);

			return;
		}

		onCommit(draft.trim());
	};

	return (
		<ClayForm.Group small>
			<label htmlFor={id}>{label}</label>

			<ClayInput
				id={id}
				onBlur={commit}
				onChange={(event) => setDraft(event.target.value)}
				onKeyDown={(event: React.KeyboardEvent) => {
					if (event.key === 'Enter') {
						event.preventDefault();
						commit();
					}
				}}
				sizing="sm"
				type="text"
				value={draft}
			/>
		</ClayForm.Group>
	);
}

export function ColorField({
	fill,
	id,
	label,
	onCommit,
	onPreview,
	value,
}: FieldProps & {
	fill?: boolean;

	onCommit: (value: string) => void;
	onPreview: (value: string) => void;
	value: string;
}) {
	return (
		<ClayForm.Group small>
			<label htmlFor={id}>{label}</label>

			<ColorInput
				className={classNames({'editor-color-fill': fill})}
				id={id}
				onCommit={onCommit}
				onPreview={onPreview}
				value={value}
			/>
		</ClayForm.Group>
	);
}

interface Props extends FieldProps {
	children?: React.ReactNode;
	max: number;
	min: number;
	onCancel?: () => void;
	onCommit: (value: number) => void;
	onPreview: (value: number) => void;
	shiftStep?: number;
	value: number;
	valueLabel: string;
}

/**
 * A slider whose drag is one history entry. Every step is reported as a
 * preview so the stage follows the control, and the value that the
 * gesture lands on is committed once, when the pointer or the key is
 * released, or when focus leaves.
 */
export function CommitSlider({
	children,
	id,
	label,
	max,
	min,
	onCancel,
	onCommit,
	onPreview,
	shiftStep,
	value,
	valueLabel,
}: Props) {
	const draggingRef = useRef(false);

	const commit = () => {
		if (!draggingRef.current) {
			return;
		}

		draggingRef.current = false;

		onCommit(value);
	};

	// A cancelled pointer (an alert, a palm rejection, a tab switch)
	// reverts the preview instead of committing a value nobody chose.
	// The event bubbles, so the group catches what the input saw.

	const cancel = () => {
		if (!draggingRef.current) {
			return;
		}

		draggingRef.current = false;

		onCancel?.();
	};

	return (
		<ClayForm.Group onPointerCancel={cancel} small>
			<div className="editor-slider-row">
				<label htmlFor={id}>{label}</label>

				<span aria-hidden="true" className="editor-slider-value">
					{valueLabel}
				</span>

				{children}
			</div>

			<ClaySlider
				id={id}
				max={max}
				min={min}
				onBlur={commit}
				onChange={(next: number) => {
					draggingRef.current = true;

					onPreview(next);
				}}
				onKeyDown={(event: React.KeyboardEvent) => {

					// Native ranges step by one; Shift takes the larger
					// stride, for sliders that ask for one.

					if (!shiftStep || !event.shiftKey) {
						return;
					}

					const delta =
						event.key === 'ArrowRight' || event.key === 'ArrowUp'
							? shiftStep
							: event.key === 'ArrowLeft' ||
								  event.key === 'ArrowDown'
								? -shiftStep
								: 0;

					if (!delta) {
						return;
					}

					event.preventDefault();

					draggingRef.current = true;

					onPreview(Math.max(min, Math.min(max, value + delta)));
				}}
				onKeyUp={commit}
				onPointerUp={commit}
				showTooltip={false}
				value={value}
			/>
		</ClayForm.Group>
	);
}

/**
 * A width and a colour as one control, because a border is one decision
 * with two halves: how thick, and what colour. They sit in a single Clay
 * input group so the pair occupies one cell of the properties grid rather
 * than a row of its own, and each half keeps its own accessible name.
 */
export function BorderField({
	colorLabel,
	colorValue,
	id,
	label,
	onColorCommit,
	onColorPreview,
	onWidthCommit,
	onWidthPreview,
	widthLabel,
	widthValue,
}: FieldProps & {
	colorLabel: string;
	colorValue: string;
	onColorCommit: (value: string) => void;
	onColorPreview: (value: string) => void;
	onWidthCommit: (value: number) => void;
	onWidthPreview?: (value: number) => void;
	widthLabel: string;
	widthValue: number;
}) {
	return (
		<ClayForm.Group small>

			{/*
			 * A real label, pointing at the first of the two controls, so
			 * it needs no styling of its own and clicking it lands on the
			 * number. The group is named by it as well, which is what
			 * makes the pair announce as "Border, group".
			 */}

			<label htmlFor={id} id={`${id}-label`}>
				{label}
			</label>

			<div aria-labelledby={`${id}-label`} role="group">
				<ClayInput.Group small>
					<ClayInput.GroupItem prepend>
						<NumberInput
							ariaLabel={widthLabel}
							id={id}
							min={0}
							onCommit={onWidthCommit}
							onPreview={onWidthPreview}
							value={widthValue}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ColorInput
							ariaLabel={colorLabel}
							id={`${id}-color`}
							onCommit={onColorCommit}
							onPreview={onColorPreview}
							value={colorValue}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</div>
		</ClayForm.Group>
	);
}
