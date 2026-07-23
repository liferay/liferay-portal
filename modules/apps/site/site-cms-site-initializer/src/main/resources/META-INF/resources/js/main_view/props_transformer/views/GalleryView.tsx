/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {
	Card,
	ICardSchema,
	IFileDropSettings,
} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line
import {IFrontendDataSetContext} from '@liferay/frontend-data-set-web/src/main/resources/META-INF/resources/FrontendDataSetContext';
import classNames from 'classnames';
import {getObjectValueFromPath, sub} from 'frontend-js-web';
import React, {Context, useContext, useEffect, useRef, useState} from 'react';
import {useDropzone} from 'react-dropzone';

import AssetPreview from '../../../common/components/AssetPreview';
import useResizeObserver from '../../../common/hooks/useResizeObserver';

import '../../../../css/props_transformer/GalleryView.scss';

const THUMBNAIL_WIDTH = 190;
const THUMBNAIL_GAP = 16;
const DEFAULT_VISIBLE_ITEMS_COUNT = 5;

const GalleryView = ({
	fileDropSettings,
	frontendDataSetContext,
	items,
	schema,
	...otherProps
}: {
	fileDropSettings?: IFileDropSettings;
	frontendDataSetContext: Context<IFrontendDataSetContext>;
	items: any[];
	schema: ICardSchema;
}) => {
	const {selectedItems} = useContext(frontendDataSetContext);
	const [selectedIndex, setSelectedIndex] = useState(0);
	const [visibleStartIndex, setVisibleStartIndex] = useState(0);
	const [visibleItemsCount, setVisibleItemsCount] = useState(
		DEFAULT_VISIBLE_ITEMS_COUNT
	);
	const thumbnailsRef = useRef<HTMLDivElement>(null);

	const safeSelectedIndex = Math.min(
		selectedIndex,
		Math.max(0, items.length - 1)
	);

	useResizeObserver(thumbnailsRef, (element, entry) => {
		const {width} = entry?.contentRect ?? element.getBoundingClientRect();

		const fittingCount = Math.floor(
			(width + THUMBNAIL_GAP) / (THUMBNAIL_WIDTH + THUMBNAIL_GAP)
		);

		setVisibleItemsCount(Math.max(1, fittingCount));
	});

	useEffect(() => {
		setVisibleStartIndex((start) => {
			const maxStart = Math.max(0, items.length - visibleItemsCount);

			if (safeSelectedIndex < start) {
				return safeSelectedIndex;
			}

			if (safeSelectedIndex >= start + visibleItemsCount) {
				return Math.min(
					safeSelectedIndex - visibleItemsCount + 1,
					maxStart
				);
			}

			return Math.min(start, maxStart);
		});
	}, [items.length, safeSelectedIndex, visibleItemsCount]);

	const handlePrevClick = () => {
		setSelectedIndex(
			safeSelectedIndex === 0 ? items.length - 1 : safeSelectedIndex - 1
		);
	};

	const handleNextClick = () => {
		setSelectedIndex(
			safeSelectedIndex === items.length - 1 ? 0 : safeSelectedIndex + 1
		);
	};

	const handleKeyDown = (event: React.KeyboardEvent, index: number) => {
		if (event.target !== event.currentTarget) {
			return;
		}

		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			setSelectedIndex(index);
		}
	};

	const visibleItems = items.slice(
		visibleStartIndex,
		visibleStartIndex + visibleItemsCount
	);

	const currentItem = items[safeSelectedIndex];

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
							key={safeSelectedIndex}
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

				<div
					className="d-flex fds-gallery-view__thumbnails flex-grow-1 justify-content-center mb-3"
					ref={thumbnailsRef}
				>
					{visibleItems.map((item, index) => {
						const actualIndex = visibleStartIndex + index;

						return (
							<GalleryThumbnail
								fileDropSettings={fileDropSettings}
								item={item}
								items={items}
								key={item.id}
								onClick={() => setSelectedIndex(actualIndex)}
								onKeyDown={(event) =>
									handleKeyDown(event, actualIndex)
								}
								schema={schema}
								selected={actualIndex === safeSelectedIndex}
								{...otherProps}
							/>
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

function GalleryThumbnail({
	fileDropSettings,
	item,
	items,
	onClick,
	onKeyDown,
	schema,
	selected,
	...otherProps
}: {
	fileDropSettings?: IFileDropSettings;
	item: any;
	items: any[];
	onClick: () => void;
	onKeyDown: (event: React.KeyboardEvent) => void;
	schema: ICardSchema;
	selected: boolean;
}) {
	const isDropTarget = Boolean(
		fileDropSettings?.enabled && fileDropSettings.isDropTarget({item})
	);

	// Only folders accept drops

	const {getRootProps, isDragActive} = useDropzone({
		disabled: !isDropTarget,
		noClick: true,
		noDragEventsBubbling: true,
		noKeyboard: true,
		onDrop: (droppedFiles) => {
			fileDropSettings?.onFileDrop?.(droppedFiles, item);
		},
	});

	const title = schema.title
		? getObjectValueFromPath({object: item, path: schema.title})
		: '';

	return (
		<div
			{...getRootProps()}
			aria-label={
				title
					? sub(Liferay.Language.get('preview-x'), title)
					: undefined
			}
			className={classNames('fds-gallery-view__thumbnail', {
				'drop-target': isDragActive,
				selected,
			})}
			onClick={onClick}
			onKeyDown={onKeyDown}
			tabIndex={0}
		>
			<Card item={item} items={items} schema={schema} {...otherProps} />
		</div>
	);
}
