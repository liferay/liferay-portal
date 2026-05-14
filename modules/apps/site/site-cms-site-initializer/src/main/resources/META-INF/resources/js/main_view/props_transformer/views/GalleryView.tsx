/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {Card, ICardSchema} from '@liferay/frontend-data-set-web';
import React, {Context, useContext, useMemo, useState} from 'react';

import '../../../../css/props_transformer/GalleryView.scss';

// eslint-disable-next-line
import {IFrontendDataSetContext} from '@liferay/frontend-data-set-web/src/main/resources/META-INF/resources/FrontendDataSetContext';
import classNames from 'classnames';

import AssetPreview from '../../../common/components/AssetPreview';

const VISIBLE_ITEMS_COUNT = 5;
const MAX_VISIBLE_INDEX = (itemsLength: number) =>
	Math.max(0, itemsLength - VISIBLE_ITEMS_COUNT);

const GalleryView = ({
	frontendDataSetContext,
	items,
	schema,
	...otherProps
}: {
	frontendDataSetContext: Context<IFrontendDataSetContext>;
	items: any[];
	schema: ICardSchema;
}) => {
	const {selectedItems} = useContext(frontendDataSetContext);
	const [selectedIndex, setSelectedIndex] = useState(0);
	const [visibleStartIndex, setVisibleStartIndex] = useState(0);

	const handlePrevClick = () => {
		const itemsLength = items.length;

		let newSelectedIndex;
		let newVisibleIndex;

		if (selectedIndex === 0) {
			newSelectedIndex = itemsLength - 1;
			newVisibleIndex = MAX_VISIBLE_INDEX(itemsLength);
		}
		else {
			newSelectedIndex = selectedIndex - 1;
			newVisibleIndex = visibleStartIndex;

			if (newSelectedIndex < visibleStartIndex) {
				newVisibleIndex = visibleStartIndex - 1;
			}
		}

		setSelectedIndex(newSelectedIndex);
		setVisibleStartIndex(newVisibleIndex);
	};

	const handleNextClick = () => {
		const itemsLength = items.length;
		const maxVisibleIndex = MAX_VISIBLE_INDEX(itemsLength);

		let newSelectedIndex;
		let newVisibleIndex;

		if (selectedIndex === itemsLength - 1) {
			newSelectedIndex = 0;
			newVisibleIndex = 0;
		}
		else {
			newSelectedIndex = selectedIndex + 1;
			newVisibleIndex = visibleStartIndex;

			if (newSelectedIndex >= visibleStartIndex + VISIBLE_ITEMS_COUNT) {
				newVisibleIndex = Math.min(
					maxVisibleIndex,
					visibleStartIndex + 1
				);
			}
		}

		setSelectedIndex(newSelectedIndex);
		setVisibleStartIndex(newVisibleIndex);
	};

	const handleItemClick = (index: number) => {
		setSelectedIndex(index);

		const itemsLength = items.length;
		const maxVisibleIndex = MAX_VISIBLE_INDEX(itemsLength);

		if (itemsLength > VISIBLE_ITEMS_COUNT) {
			if (index < visibleStartIndex) {
				setVisibleStartIndex(index);
			}
			else if (index >= visibleStartIndex + VISIBLE_ITEMS_COUNT) {
				setVisibleStartIndex(
					Math.min(maxVisibleIndex, index - VISIBLE_ITEMS_COUNT + 1)
				);
			}
		}
	};

	const handleKeyDown = (event: React.KeyboardEvent, index: number) => {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			handleItemClick(index);
		}
	};

	const visibleItems = items.slice(
		visibleStartIndex,
		visibleStartIndex + VISIBLE_ITEMS_COUNT
	);

	const currentItem = useMemo(
		() => items[selectedIndex],
		[items, selectedIndex]
	);

	const cardWidth = `calc((100% - ${VISIBLE_ITEMS_COUNT - 1}rem) / ${VISIBLE_ITEMS_COUNT})`;

	const isNavigationDisabled = items.length === 1;

	return (
		<div className="fds-gallery-view">
			<div className="fds-gallery-view__preview">
				<div className="align-items-center d-flex fds-gallery-view__preview-wrapper h-100 justify-content-center w-100">
					{selectedItems && selectedItems.length >= 2 ? (
						<div className="bg-light d-flex h-100 justify-content-center w-100">
							<ClayEmptyState
								description={Liferay.Language.get(
									'select-a-single-file-to-preview-its-content'
								)}
								imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state_preview.svg`}
								title={Liferay.Language.get(
									'no-preview-available'
								)}
							/>
						</div>
					) : (
						<AssetPreview
							item={currentItem}
							key={selectedIndex}
							showContentPreview={false}
							url=""
						/>
					)}
				</div>
			</div>

			<div className="align-items-center c-gap-3 d-flex fds-gallery-view__navigation justify-content-center mt-4 px-4">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('previous')}
					className="flex-shrink-0"
					disabled={isNavigationDisabled}
					displayType="secondary"
					onClick={handlePrevClick}
					rounded
					symbol="angle-left"
				/>

				<div className="c-gap-3 d-flex fds-gallery-view__thumbnails flex-grow-1">
					{visibleItems.map((item, index) => {
						const actualIndex = visibleStartIndex + index;
						const classes = classNames(
							'fds-gallery-view__thumbnail',
							{
								selected: actualIndex === selectedIndex,
							}
						);

						return (
							<div
								className={classes}
								key={actualIndex}
								onClick={() => handleItemClick(actualIndex)}
								onKeyDown={(event) =>
									handleKeyDown(event, actualIndex)
								}
								style={{
									width: cardWidth,
								}}
								tabIndex={0}
							>
								<Card
									item={item}
									items={items}
									schema={schema}
									{...otherProps}
								/>
							</div>
						);
					})}
				</div>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('next')}
					className="flex-shrink-0"
					disabled={isNavigationDisabled}
					displayType="secondary"
					onClick={handleNextClick}
					rounded
					symbol="angle-right"
				/>
			</div>
		</div>
	);
};

export default GalleryView;
