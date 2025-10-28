/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon, default as ClayButton} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {Key} from 'react';

import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';
import {config} from '../config/index';

interface Props {
	onSizeSelected: (sizeId: ViewportSize) => void;
	selectedSize: ViewportSize;
}

const Trigger = React.forwardRef<HTMLButtonElement, any>(
	({icon, label, ...otherProps}, ref) => (
		<ClayButtonWithIcon
			{...otherProps}
			aria-label={sub(
				Liferay.Language.get('select-a-viewport.-current-viewport-x'),
				label
			)}
			className="d-lg-none form-control-select"
			displayType="secondary"
			monospaced={false}
			ref={ref}
			size="sm"
			symbol={icon}
			title={Liferay.Language.get('select-a-viewport')}
		/>
	)
);

export default function ViewportSizeSelector({
	onSizeSelected,
	selectedSize,
}: Props) {
	const {availableViewportSizes} = config;

	return (
		<>
			<ClayButton.Group className="d-lg-block d-none flex-nowrap flex-shrink-0">
				{Object.values(availableViewportSizes).map(
					({icon, label, sizeId}) => (
						<ClayButtonWithIcon
							aria-label={label}
							aria-pressed={selectedSize === sizeId}
							className={classNames({
								'page-editor__viewport-size-selector--default':
									sizeId === VIEWPORT_SIZES.desktop,
							})}
							displayType="secondary"
							key={sizeId}
							onClick={() =>
								onSizeSelected(sizeId as ViewportSize)
							}
							size="sm"
							symbol={icon}
							title={label}
						/>
					)
				)}
			</ClayButton.Group>

			<Picker
				UNSAFE_menuClassName="cadmin"
				as={Trigger}
				icon={availableViewportSizes[selectedSize].icon}
				items={Object.values(availableViewportSizes)}
				label={availableViewportSizes[selectedSize].label}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				onSelectionChange={(size: Key) =>
					onSizeSelected(size as ViewportSize)
				}
				selectedItem={selectedSize}
				selectedKey={selectedSize}
			>
				{({icon, label, sizeId}) => (
					<Option key={sizeId} textValue={label}>
						<span className="inline-item inline-item-before ml-1 mr-3">
							<ClayIcon symbol={icon} />
						</span>

						{label}
					</Option>
				)}
			</Picker>
		</>
	);
}
