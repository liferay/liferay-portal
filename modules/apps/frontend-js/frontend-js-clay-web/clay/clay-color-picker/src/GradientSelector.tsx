/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from '@clayui/shared';
import React from 'react';
import tinycolor from 'tinycolor2';

import {usePointerPosition} from './hooks';
import {colorToXY, xToSaturation, yToVisibility} from './util';

const MAX = 100;
const MIN = 0;
const STEP = 1;
const STEP_LARGE = 10;

type Props = {

	/**
	 * Labels for the aria attributes
	 */
	ariaLabels?: {
		saturationAndBrightness?: string;
		saturationAndBrightnessIs?: string;
	};

	/**
	 * Color value that is currently selected.
	 */
	color: tinycolor.Instance;

	/**
	 * The numerical hue value of the color
	 */
	hue?: number;

	/**
	 * Callback function for when saturation or visibility values change
	 */
	onChange?: (saturation: number, visibility: number) => void;
};

const clamp = (value: number) => Math.min(MAX, Math.max(MIN, value));

/**
 * The saturation and brightness a key press asks for, or nothing when the
 * key is not one this control answers to. Left and right move along
 * saturation, up and down along brightness, and Shift takes ten at a
 * time.
 */
function stepFrom(
	event: React.KeyboardEvent,
	saturation: number,
	brightness: number
): [number, number] | null {
	const step = event.shiftKey ? STEP_LARGE : STEP;

	switch (event.key) {
		case 'ArrowDown':
			return [saturation, brightness - step];
		case 'ArrowLeft':
			return [saturation - step, brightness];
		case 'ArrowRight':
			return [saturation + step, brightness];
		case 'ArrowUp':
			return [saturation, brightness + step];
		case 'End':
			return [MAX, brightness];
		case 'Home':
			return [MIN, brightness];
		case 'PageDown':
			return [saturation, brightness - STEP_LARGE];
		case 'PageUp':
			return [saturation, brightness + STEP_LARGE];
		default:
			return null;
	}
}

const useIsomorphicLayoutEffect =
	typeof window === 'undefined' ? React.useEffect : React.useLayoutEffect;

/**
 * Renders GradientSelector component
 */
function ClayColorPickerGradientSelector({
	ariaLabels,
	color,
	onChange = () => {},
	hue = 0,
}: Props) {
	const containerRef = React.useRef<HTMLDivElement>(null);
	const selectorActiveRef = React.useRef<boolean>(false);
	const {onPointerMove, setXY, x, y} = usePointerPosition(containerRef);
	const removeListeners = () => {
		selectorActiveRef.current = false;
		window.removeEventListener('pointermove', onPointerMove);
		window.removeEventListener('pointerup', removeListeners);
	};
	useIsomorphicLayoutEffect(() => {
		const {current} = containerRef;
		if (current && selectorActiveRef.current) {
			onChange(xToSaturation(x, current), yToVisibility(y, current));
		}
	}, [x, y]);
	React.useEffect(() => {
		if (containerRef.current && !selectorActiveRef.current) {
			setXY(colorToXY(color, containerRef.current));
		}
	}, [color]);
	React.useEffect(() => removeListeners, []);

	const {s, v} = color.toHsv();

	const saturation = Math.round(s * 100);
	const brightness = Math.round(v * 100);

	return (
		<div
			className="clay-color-map clay-color-map-hsb"
			onPointerDown={(event) => {
				event.preventDefault();
				selectorActiveRef.current = true;
				onPointerMove(event);
				(containerRef.current!.querySelector(
					'.clay-color-map-pointer'
				) as HTMLElement)!.focus();
				window.addEventListener('pointermove', onPointerMove);
				window.addEventListener('pointerup', removeListeners);
			}}
			ref={containerRef}
			style={{
				backgroundColor: `hsl(${hue}, 100%, 50%)`,
				backgroundImage: `linear-gradient(to top, #000, rgba(0, 0, 0, 0)), linear-gradient(to right, #FFF, rgba(255, 255, 255, 0))`,
			}}
		>

			{/*
			 * The handle is the control: it carries the name, the values
			 * and the keys, so the map can be operated without dragging
			 * it (WCAG 2.1.1, 2.5.7 and 4.1.2). The pointer path above is
			 * untouched.
			 */}

			<button
				aria-label={ariaLabels?.saturationAndBrightness}
				aria-valuemax={MAX}
				aria-valuemin={MIN}
				aria-valuenow={saturation}
				aria-valuetext={sub(
					ariaLabels?.saturationAndBrightnessIs || '',
					[saturation, brightness]
				)}
				className="clay-color-map-pointer clay-color-pointer"
				onKeyDown={(event) => {
					const step = stepFrom(event, saturation, brightness);

					if (!step) {
						return;
					}

					event.preventDefault();

					onChange(clamp(step[0]), clamp(step[1]));
				}}
				role="slider"
				style={{
					background: color.toHexString(),
					left: x - 7,
					top: y - 7,
				}}
				type="button"
			/>
		</div>
	);
}

export default ClayColorPickerGradientSelector;
