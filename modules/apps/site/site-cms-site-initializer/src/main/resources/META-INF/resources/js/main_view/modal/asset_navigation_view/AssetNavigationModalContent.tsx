/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClayModal from '@clayui/modal';
import {addParams, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import CommentsPanel from '../../../content_editor/components/panels/CommentsPanel';
import AssetTypeInfoPanel from '../../info_panel/AssetTypeInfoPanelContent';
import Carousel from './Carousel';
import Header from './Header';

import '../../../../css/components/AssetNavigation.scss';
import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
interface AssetNavigationModalContentProps {
	additionalProps: any;
	contentViewURL: string;
	currentIndex: number;
	items: ISearchAssetObjectEntry[];
	showInfoPanel?: boolean;
}

const KEY_CODE = {
	LEFT: 37,
	RIGHT: 39,
};

const PANELS = {
	commentPanel: 'commentPanel',
	infoPanel: 'infoPanel',
};

const AssetNavigationCommentsPanel = ({
	additionalProps: {commentsProps},
	item,
}: {
	additionalProps: any;
	item: ISearchAssetObjectEntry;
}) => {
	const {addCommentURL, editCommentURL, getCommentsURL} = commentsProps;
	const {
		embedded: {id},
		entryClassName,
	}: ISearchAssetObjectEntry = item;

	const params = `?className=${encodeURIComponent(entryClassName)}&classPK=${id}`;

	return (
		<>
			<h3 className="font-weight-semi-bold px-3 py-4 text-7">
				{Liferay.Language.get('comments')}
			</h3>

			<CommentsPanel
				{...commentsProps}
				addCommentURL={addParams(params, addCommentURL)}
				editCommentURL={addParams(params, editCommentURL)}
				getCommentsURL={addParams(params, getCommentsURL)}
			></CommentsPanel>
		</>
	);
};

export default function AssetNavigationModalContent({
	additionalProps,
	contentViewURL,
	currentIndex = 0,
	items,
	showInfoPanel = true,
}: AssetNavigationModalContentProps) {
	const [currentItemIndex, setCurrentItemIndex] = useState(currentIndex);
	const [openSidePanel, setOpenSidePanel] = useState(false);
	const [currentPanel, setCurrentPanel] = useState<String>();
	const containerRef = useRef(null);
	const currentItem = items[currentItemIndex];

	const handleClickNext = useCallback(() => {
		if (items.length > 1) {
			setCurrentItemIndex((index) => {
				const lastIndex = items.length - 1;
				const shouldResetIndex = index === lastIndex;

				return shouldResetIndex ? 0 : index + 1;
			});
		}
	}, [items.length]);

	const handleClickPrevious = useCallback(() => {
		if (items.length > 1) {
			setCurrentItemIndex((index) => {
				const lastIndex = items.length - 1;
				const shouldResetIndex = index === 0;

				return shouldResetIndex ? lastIndex : index - 1;
			});
		}
	}, [items.length]);

	const handleOnKeyDown = useCallback(
		(event: any) => {
			switch (event.which || event.keyCode) {
				case KEY_CODE.LEFT:
					handleClickPrevious();
					break;
				case KEY_CODE.RIGHT:
					handleClickNext();
					break;
				default:
					break;
			}
		},
		[handleClickNext, handleClickPrevious]
	);

	const handleClickComments = () => {
		if (currentPanel === PANELS.commentPanel && openSidePanel) {
			setOpenSidePanel(false);

			return;
		}

		setCurrentPanel(PANELS.commentPanel);
		setOpenSidePanel(true);
	};

	const handleClickInfo = () => {
		if (currentPanel === PANELS.infoPanel && openSidePanel) {
			setOpenSidePanel(false);

			return;
		}

		setCurrentPanel(PANELS.infoPanel);
		setOpenSidePanel(true);
	};

	useEffect(() => {
		document.documentElement.addEventListener('keydown', handleOnKeyDown);

		return () => {
			document.documentElement.removeEventListener(
				'keydown',
				handleOnKeyDown
			);
		};
	}, [handleOnKeyDown]);

	return (
		<>
			<ClayModal.Header>
				<Header
					handleClickComments={handleClickComments}
					handleClickInfo={handleClickInfo}
					item={currentItem}
					showInfoPanel={showInfoPanel}
				/>
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<div
					className="asset-navigation-container h-100"
					ref={containerRef}
				>
					<Carousel
						contentViewURL={contentViewURL}
						currentItem={currentItem}
						handleClickNext={handleClickNext}
						handleClickPrevious={handleClickPrevious}
						showArrows={items.length > 1}
					/>

					<SidePanel
						containerRef={containerRef}
						onOpenChange={setOpenSidePanel}
						open={openSidePanel}
					>
						{currentPanel === PANELS.commentPanel ? (
							<AssetNavigationCommentsPanel
								additionalProps={additionalProps as any}
								item={currentItem}
							/>
						) : showInfoPanel ? (
							<AssetTypeInfoPanel
								additionalProps={additionalProps as any}
								items={[currentItem]}
							/>
						) : null}
					</SidePanel>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				className="text-center"
				middle={
					<span className="text-3">
						{sub(Liferay.Language.get('x-of-x'), [
							currentItemIndex + 1,
							items.length,
						])}
					</span>
				}
			/>
		</>
	);
}
