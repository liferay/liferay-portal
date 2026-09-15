/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClaySlider from '@clayui/slider';
import classNames from 'classnames';
import React, {useRef} from 'react';

interface FieldProps {
	id: string;
	label: string;
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
	const draggingRef = useRef(false);

	return (
		<ClayForm.Group small>
			<label htmlFor={id}>{label}</label>

			<input
				className={classNames(
					'editor-color-input form-control form-control-sm',
					{'editor-color-fill': fill}
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
