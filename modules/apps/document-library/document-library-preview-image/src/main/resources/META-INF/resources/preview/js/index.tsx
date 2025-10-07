/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {debounce} from 'frontend-js-web';
import React, {useLayoutEffect, useRef, useState} from 'react';

import '@liferay/document-library-preview-css';

const MIN_ZOOM_RATIO_AUTOCENTER = 3;
const ZOOM_LEVELS: number[] = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1];
const ZOOM_LEVELS_REVERSED: number[] = ZOOM_LEVELS.slice().reverse();

/**
 * Component that create an image preview to allow zoom
 */
interface ImagePreviewerProps {
	alt: string;
	imageURL: string;
}

const ImagePreviewer = ({alt, imageURL}: ImagePreviewerProps) => {
	const [currentZoom, setCurrentZoom] = useState<number>(1);
	const [imageHeight, setImageHeight] = useState<number | null>(null);
	const [imageWidth, setImageWidth] = useState<number | null>(null);
	const [imageMargin, setImageMargin] = useState<string | null>(null);
	const [zoomInDisabled, setZoomInDisabled] = useState<boolean>(true);
	const [zoomOutDisabled, setZoomOutDisabled] = useState<boolean>(false);
	const [zoomRatio, setZoomRatio] = useState<number | null>(null);

	const imageRef = useRef<HTMLImageElement>(null);
	const imageContainerRef = useRef<HTMLDivElement>(null);

	const updateToolbar = (zoom: number) => {
		setCurrentZoom(zoom);
		setZoomInDisabled(ZOOM_LEVELS_REVERSED[0] === zoom);
		setZoomOutDisabled(ZOOM_LEVELS[0] >= zoom);
	};

	const applyZoom = (zoom: number) => {
		const imageElement = imageRef.current;
		if (!imageElement) {
			return;
		}

		setImageHeight(imageElement.naturalHeight * zoom);
		setImageWidth(imageElement.naturalWidth * zoom);
		setZoomRatio(zoom / currentZoom);

		updateToolbar(zoom);
	};

	const getFittingZoom = (): number => {
		const imageElement = imageRef.current;
		if (!imageElement) {
			return 1;
		}

		return imageElement.width / imageElement.naturalWidth;
	};

	const getImageStyles = (): React.CSSProperties => {
		const imageStyles: React.CSSProperties = {};

		if (imageHeight && imageWidth) {
			imageStyles.height = imageHeight;
			imageStyles.maxHeight = imageHeight;
			imageStyles.maxWidth = imageWidth;
			imageStyles.width = imageWidth;
		}

		if (imageMargin) {
			imageStyles.margin = imageMargin;
		}

		return imageStyles;
	};

	const handleImageLoad = () => {
		updateToolbar(getFittingZoom());
	};

	const handlePercentButtonClick = () => {
		if (currentZoom === 1) {
			setImageHeight(null);
			setImageWidth(null);
		}
		else {
			applyZoom(1);
		}
	};

	useLayoutEffect(() => {
		const imageContainerElement = imageContainerRef.current;
		const imageElement = imageRef.current;

		if (!imageContainerElement || !imageElement) {
			return;
		}

		setImageMargin(
			`${imageHeight && imageHeight > imageContainerElement.clientHeight ? 0 : 'auto'} ${
				imageWidth && imageWidth > imageContainerElement.clientWidth
					? 0
					: 'auto'
			}`
		);

		if (
			zoomRatio &&
			(imageContainerElement.clientWidth < imageElement.naturalWidth ||
				imageContainerElement.clientHeight < imageElement.naturalHeight)
		) {
			let scrollLeft: number;
			let scrollTop: number;

			if (zoomRatio < MIN_ZOOM_RATIO_AUTOCENTER) {
				scrollLeft =
					(imageContainerElement.clientWidth * (zoomRatio - 1)) / 2 +
					imageContainerElement.scrollLeft * zoomRatio;
				scrollTop =
					(imageContainerElement.clientHeight * (zoomRatio - 1)) / 2 +
					imageContainerElement.scrollTop * zoomRatio;
			}
			else {
				scrollTop =
					(imageHeight! - imageContainerElement.clientHeight) / 2;
				scrollLeft =
					(imageWidth! - imageContainerElement.clientWidth) / 2;
			}

			imageContainerElement.scrollLeft = scrollLeft;
			imageContainerElement.scrollTop = scrollTop;

			setZoomRatio(null);
		}

		if (!imageElement.style.width) {
			updateToolbar(getFittingZoom());
		}
	}, [imageHeight, imageWidth, zoomRatio, imageMargin]);

	useLayoutEffect(() => {
		const handleResize = debounce(() => {
			if (imageRef.current && !imageRef.current.style.width) {
				updateToolbar(getFittingZoom());
			}
		}, 250);

		window.addEventListener('resize', handleResize);

		return () => window.removeEventListener('resize', handleResize);
	}, [currentZoom, imageHeight, imageWidth]);

	return (
		<div className="preview-file">
			<div
				className="preview-file-container preview-file-max-height"
				ref={imageContainerRef}
			>
				<img
					alt={alt}
					className="preview-file-image"
					onLoad={handleImageLoad}
					ref={imageRef}
					src={imageURL}
					style={getImageStyles()}
				/>
			</div>

			<div className="preview-toolbar-container">
				<ClayButton.Group className="floating-bar">
					<ClayButton
						aria-label={Liferay.Language.get('zoom-out')}
						className="btn-floating-bar"
						disabled={zoomOutDisabled}
						displayType={null}
						monospaced
						onClick={() => {
							const nextZoom = ZOOM_LEVELS_REVERSED.find(
								(zoom) => zoom < currentZoom
							);
							if (nextZoom) {
								applyZoom(nextZoom);
							}
						}}
						title={Liferay.Language.get('zoom-out')}
					>
						<ClayIcon symbol="hr" />
					</ClayButton>

					<ClayButton
						aria-label={
							currentZoom === 1
								? Liferay.Language.get('zoom-to-fit')
								: Liferay.Language.get('real-size')
						}
						className="btn-floating-bar btn-floating-bar-text"
						displayType={null}
						onClick={handlePercentButtonClick}
						title={
							currentZoom === 1
								? Liferay.Language.get('zoom-to-fit')
								: Liferay.Language.get('real-size')
						}
					>
						<span className="preview-toolbar-label-percent">
							{Math.round((currentZoom || 0) * 100)}%
						</span>
					</ClayButton>

					<ClayButton
						aria-label={Liferay.Language.get('zoom-in')}
						className="btn-floating-bar"
						disabled={zoomInDisabled}
						displayType={null}
						monospaced
						onClick={() => {
							const nextZoom = ZOOM_LEVELS.find(
								(zoom) => zoom > currentZoom
							);
							if (nextZoom) {
								applyZoom(nextZoom);
							}
						}}
						title={Liferay.Language.get('zoom-in')}
					>
						<ClayIcon symbol="plus" />
					</ClayButton>
				</ClayButton.Group>
			</div>
		</div>
	);
};

export {ImagePreviewer};
