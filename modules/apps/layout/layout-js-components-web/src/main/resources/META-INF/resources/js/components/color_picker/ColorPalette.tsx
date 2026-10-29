/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {InternalDispatch} from '@clayui/shared';
import classNames from 'classnames';
import React, {
	Dispatch,
	KeyboardEvent,
	KeyboardEventHandler,
	MouseEventHandler,
	RefObject,
	SetStateAction,
	forwardRef,
	useLayoutEffect,
	useRef,
} from 'react';

import {Color, ColorCategoryMap} from '../../types/ColorPicker';
import SearchForm from '../search_form/SearchForm';

interface SplotchProps {
	active?: boolean;
	className?: string;
	disabled?: boolean;
	onClick: MouseEventHandler;
	onKeyPress: KeyboardEventHandler;
	size?: number | string;
	title: string;
	value: unknown;
}

const Splotch = forwardRef<HTMLButtonElement, SplotchProps>(
	(
		{
			active = false,
			className = '',
			disabled = false,
			onClick,
			onKeyPress,
			size,
			title,
			value,
		},
		ref
	) => (
		<button
			className={classNames(
				'btn clay-color-btn clay-color-btn-bordered lfr-portal-tooltip rounded-circle',
				{
					active,
					[className]: Boolean(className),
				}
			)}
			data-tooltip-delay="0"
			disabled={disabled}
			onClick={onClick}
			onKeyPress={onKeyPress}
			ref={ref}
			style={{
				background: `${value}`,
				height: size,
				width: size,
			}}
			title={title}
			type="button"
		/>
	)
);

interface ColorPaletteProps {
	active: boolean;
	colors: ColorCategoryMap;
	dropdownContainerRef?: RefObject<HTMLElement>;
	onActiveChange: InternalDispatch<boolean>;
	onKeyDown?: (
		event: KeyboardEvent<Element>,
		items: NodeListOf<HTMLInputElement | HTMLButtonElement> | null
	) => void;
	onSetSearchValue: Dispatch<SetStateAction<string>>;
	onValueChange?: (color: Omit<Color, 'disabled'>) => void;
	triggerElementRef?: RefObject<HTMLDivElement | HTMLButtonElement>;
}

export default function ColorPalette({
	active,
	colors,
	dropdownContainerRef,
	onActiveChange,
	onSetSearchValue,
	onValueChange,
	triggerElementRef,
}: ColorPaletteProps) {
	const focusableItemsRef = useRef<NodeListOf<
		HTMLButtonElement | HTMLInputElement
	> | null>(null);

	useLayoutEffect(() => {
		focusableItemsRef.current =
			dropdownContainerRef?.current?.querySelectorAll('button, input') ??
			null;

		focusableItemsRef.current?.[0]?.focus();
	}, [dropdownContainerRef]);

	return (
		<>
			<SearchForm
				className="flex-grow-1 layout__color-picker__search-form mb-2 px-3"
				onChange={onSetSearchValue}
			/>

			{Object.keys(colors).length ? (
				Object.keys(colors).map((category) => (
					<div
						className="layout__color-picker__color-palette"
						key={category}
					>
						<span className="mb-0 p-3 sheet-subtitle text-2">
							{category}
						</span>

						{Object.keys(colors[category]).map((tokenSet) => (
							<div className="px-3" key={tokenSet}>
								<span className="text-secondary">
									{tokenSet}
								</span>

								<div className="clay-color-swatch mb-0 mt-3">
									{colors[category][tokenSet].map(
										({disabled, label, name, value}) => (
											<div
												className="clay-color-swatch-item"
												key={name}
											>
												<Splotch
													disabled={disabled}
													onClick={() => {
														onValueChange?.({
															label,
															name,
															value,
														});
														onActiveChange(!active);
													}}
													onKeyPress={() => {
														triggerElementRef?.current?.focus();
													}}
													title={label}
													value={value}
												/>
											</div>
										)
									)}
								</div>
							</div>
						))}
					</div>
				))
			) : (
				<ClayEmptyState
					className="mt-4"
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					image={
						<ClayIcon
							className="c-empty-state-icon c-empty-state-icon-search-state"
							illustration
							symbol="search-state"
						/>
					}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			)}
		</>
	);
}
