/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMounted} from '@liferay/frontend-js-react-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import ImageEditor from '../ImageEditor';
import Carousel from './Carousel';
import Footer from './Footer';
import Header from './Header';

import '../../css/item_selector_preview.scss';

const KEY_CODE = {
	ESC: 27,
	LEFT: 37,
	RIGTH: 39,
};

const noop = () => {};

const itemIsImage = ({mimeType, type}) =>
	type === 'image' || Boolean(mimeType?.match(/image.*/));

const ItemSelectorPreview = ({
	currentIndex = 0,
	editImageURL,
	handleClose = noop,
	handleSelectedItem,
	headerTitle,
	itemReturnType,
	items,
	reloadOnHide: initialReloadOnHide = false,
}) => {
	const [currentItemIndex, setCurrentItemIndex] = useState(currentIndex);
	const [isEditing, setIsEditing] = useState();
	const [isImage, setIsImage] = useState(itemIsImage(items[currentIndex]));
	const [itemList, setItemList] = useState(items);
	const [reloadOnHide, setReloadOnHide] = useState(initialReloadOnHide);

	const currentItem = itemList[currentItemIndex];

	const infoButtonRef = React.createRef();

	const isMounted = useIsMounted();

	const handleCancelEditing = () => {
		setIsEditing(false);
	};

	const handleClickBack = () => {
		handleClose();

		if (reloadOnHide) {
			const frame = window.frameElement;

			if (frame) {
				frame.contentWindow.location.reload();
			}
		}
	};

	const handleClickDone = () => {
		handleSelectedItem(currentItem);
	};

	const handleClickEdit = () => {
		setIsEditing(true);
	};

	const handleClickNext = useCallback(() => {
		if (itemList.length > 1) {
			setCurrentItemIndex((index) => {
				const lastIndex = itemList.length - 1;
				const shouldResetIndex = index === lastIndex;

				return shouldResetIndex ? 0 : index + 1;
			});
		}
	}, [itemList.length]);

	const handleClickPrevious = useCallback(() => {
		if (itemList.length > 1) {
			setCurrentItemIndex((index) => {
				const lastIndex = itemList.length - 1;
				const shouldResetIndex = index === 0;

				return shouldResetIndex ? lastIndex : index - 1;
			});
		}
	}, [itemList.length]);

	const handleOnKeyDown = useCallback(
		(event) => {
			if (!isMounted()) {
				return;
			}

			switch (event.which || event.keyCode) {
				case KEY_CODE.LEFT:
					handleClickPrevious();
					break;
				case KEY_CODE.RIGTH:
					handleClickNext();
					break;
				case KEY_CODE.ESC:
					event.preventDefault();
					event.stopPropagation();
					handleClose();
					break;
				default:
					break;
			}
		},
		[handleClickNext, handleClickPrevious, handleClose, isMounted]
	);

	const handleSaveEditedImage = ({file, success}) => {
		if (success) {
			const newItem = {
				...currentItem,
				fileEntryId: file.fileEntryId,
				groupId: file.groupId,
				title: file.title,
				url: file.url,
				uuid: file.uuid,
				value: file.resolvedValue,
			};

			if (!newItem.value) {
				const imageValue = {
					fileEntryId: newItem.fileEntryId,
					groupId: newItem.groupId,
					title: newItem.title,
					type: newItem.type,
					url: newItem.url,
					uuid: newItem.uuid,
				};

				newItem.value = JSON.stringify(imageValue);
			}

			setIsEditing(false);

			handleClose();
			handleSelectedItem(newItem);
		}
	};

	const updateItemList = (newItemList) => {
		setItemList(newItemList);
		setReloadOnHide(true);
	};

	const updateCurrentItem = useCallback(
		(itemData) => {
			if (isMounted()) {
				const newItemList = [...itemList];

				newItemList[currentItemIndex] = {...currentItem, ...itemData};

				updateItemList(newItemList);
			}
		},
		[currentItem, currentItemIndex, isMounted, itemList]
	);

	useEffect(() => {
		document.documentElement.addEventListener('keydown', handleOnKeyDown);

		const updateCurrentItemHandler = Liferay.on(
			'updateCurrentItem',
			updateCurrentItem
		);

		Liferay.component('ItemSelectorPreview', ItemSelectorPreview);

		return () => {
			document.documentElement.removeEventListener(
				'keydown',
				handleOnKeyDown
			);

			Liferay.detach(updateCurrentItemHandler);
			Liferay.component('ItemSelectorPreview', null);
		};
	}, [handleOnKeyDown, updateCurrentItem]);

	useEffect(() => {
		const sidenavToggle = infoButtonRef.current;

		if (sidenavToggle) {
			Liferay.SideNavigation.initialize(sidenavToggle, {
				container: '.sidenav-container',
				position: 'right',
				typeMobile: 'fixed',
				width: '320px',
			});
		}

		return () => {
			Liferay.SideNavigation.destroy(sidenavToggle);
		};
	}, [infoButtonRef]);

	useEffect(() => {
		setIsImage(itemIsImage(currentItem));
	}, [currentItem]);

	return (
		<div className="fullscreen item-selector-preview">
			<Header
				disabledAddButton={!currentItem.url}
				handleClickAdd={handleClickDone}
				handleClickBack={handleClickBack}
				handleClickEdit={handleClickEdit}
				headerTitle={headerTitle}
				infoButtonRef={infoButtonRef}
				showEditIcon={isImage}
				showInfoIcon={!!currentItem.metadata}
				showNavbar={!isEditing}
			/>

			{isEditing ? (
				<ImageEditor
					imageId={currentItem.fileEntryId || currentItem.fileentryid}
					imageSrc={currentItem.url}
					itemReturnType={itemReturnType}
					mimeType={currentItem.mimetype}
					onCancel={handleCancelEditing}
					onSave={handleSaveEditedImage}
					saveURL={editImageURL}
				/>
			) : (
				<>
					<Carousel
						currentItem={currentItem}
						handleClickNext={handleClickNext}
						handleClickPrevious={handleClickPrevious}
						isImage={isImage}
						showArrows={itemList.length > 1}
					/>

					<Footer
						currentIndex={currentItemIndex}
						title={currentItem.title}
						totalItems={itemList.length}
					/>
				</>
			)}
		</div>
	);
};

ItemSelectorPreview.propTypes = {
	currentIndex: PropTypes.number,
	editImageURL: PropTypes.string,
	handleSelectedItem: PropTypes.func.isRequired,
	headerTitle: PropTypes.string.isRequired,
	itemReturnType: PropTypes.string,
	items: PropTypes.arrayOf(
		PropTypes.shape({
			base64: PropTypes.string,
			fileEntryId: PropTypes.string,
			metadata: PropTypes.string,
			mimetype: PropTypes.string,
			returntype: PropTypes.string.isRequired,
			title: PropTypes.string.isRequired,
			url: PropTypes.string,
			value: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
		})
	).isRequired,
};

export default ItemSelectorPreview;
