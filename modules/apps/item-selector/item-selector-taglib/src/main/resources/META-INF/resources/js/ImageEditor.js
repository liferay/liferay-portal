/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayToolbar from '@clayui/toolbar';
import Cropper from 'cropperjs';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

import '../css/image_editor.scss';

import 'cropperjs/dist/cropper.css';

const ratios = [
	{
		label: 'Free',
	},
	{
		label: '16:10',
		value: 16 / 10,
	},
	{
		label: '16:9',
		value: 16 / 9,
	},
	{
		label: '4:3',
		value: 4 / 3,
	},
	{
		label: '1:1',
		value: 1 / 1,
	},
	{
		label: '2:3',
		value: 2 / 3,
	},
];

const ZOOM_CONFIG = {
	max: 200,
	min: 12.5,
	step: 0.5,
};

const noop = () => {};

function ImageEditor({
	imageId,
	imageSrc,
	itemReturnType,
	mimeType = 'image/png',
	onCancel = noop,
	onSave = noop,
	saveURL,
}) {
	const ref = useRef();

	const [disabledZoomIn, setDisabledZoomIn] = useState(false);
	const [disabledZoomOut, setDisabledZoomOut] = useState(false);

	const handleAspectRationChange = (event) => {
		const value = event.target.value;

		ref.current?.cropper?.setAspectRatio(value);
	};

	const handleReset = () => {
		ref.current?.cropper?.reset();
		onCancel();
	};

	const handleRotate = () => {
		ref.current?.cropper?.rotate(90);
	};

	const handleSave = () => {
		const canvas = ref.current?.cropper?.getCroppedCanvas();

		canvas.toBlob((blob) => {
			const formData = new FormData();

			formData.append('fileEntryId', imageId);
			formData.append('imageBlob', blob, imageId);
			formData.append('returnType', itemReturnType);

			fetch(saveURL, {
				body: formData,
				method: 'POST',
			})
				.then((response) => response.json())
				.then((response) => {
					onSave(response);
				});
		}, mimeType);
	};

	const handleZoomIn = () => {
		ref.current?.cropper?.zoom(ZOOM_CONFIG.step);
	};

	const handleZoomOut = () => {
		const cropBoxDdata = ref.current?.cropper?.getCropBoxData();
		const imageData = ref.current?.cropper?.getImageData();

		if (
			Math.round(imageData.width) <= Math.round(cropBoxDdata.width) ||
			Math.round(imageData.height) <= Math.round(cropBoxDdata.height)
		) {
			setDisabledZoomOut(true);

			return;
		}

		ref.current?.cropper?.zoom(-ZOOM_CONFIG.step);
	};

	useEffect(() => {
		const imageElement = ref.current;

		if (imageElement !== null) {
			new Cropper(imageElement, {
				autoCrop: false,
				autoCropArea: 1,
				background: false,
				center: false,
				dragMode: 'move',
				enable: true,
				guides: false,
				rotateTo: 0,
				scaleX: 1,
				scaleY: 1,
				viewMode: 1,
			});

			const handleCropReady = () => {
				const imageData = imageElement?.cropper?.getImageData();

				if (imageData.width > imageData.naturalWidth) {
					imageElement?.cropper?.zoomTo(1);
				}

				imageElement?.cropper?.crop();
			};

			const handleZoomChange = (event) => {
				const zoom = event.detail.ratio * 100;
				const zoomDirection =
					event.detail.ratio > event.detail.oldRatio ? 1 : -1;
				const nextZoom = zoom * (1 + zoomDirection * ZOOM_CONFIG.step);

				if (zoom < ZOOM_CONFIG.min || zoom > ZOOM_CONFIG.max) {
					event.preventDefault();
				}

				if (nextZoom < ZOOM_CONFIG.min) {
					setDisabledZoomOut(true);
				}
				else {
					setDisabledZoomOut(false);
				}

				if (nextZoom > ZOOM_CONFIG.max) {
					setDisabledZoomIn(true);
				}
				else {
					setDisabledZoomIn(false);
				}
			};

			imageElement.addEventListener('ready', handleCropReady);
			imageElement.addEventListener('zoom', handleZoomChange);

			return () => {
				imageElement?.removeEventListener('cropready', handleCropReady);
				imageElement?.removeEventListener(
					'zoomChange',
					handleZoomChange
				);
				imageElement?.cropper?.destroy();
			};
		}
	}, []);

	return (
		<div className="image-editor">
			<div style={{height: '100%'}}>
				<img
					ref={ref}
					src={imageSrc}
					style={{
						display: 'block',
						maxWidth: '100%',
						opacity: 0,
					}}
				/>
			</div>

			<ClayToolbar>
				<ClayToolbar.Nav>
					<ClayToolbar.Item>
						<ClayToolbar.Section>
							<ClayInput.Group>
								<ClayInput.GroupItem prepend shrink>
									<ClayInput.GroupText>
										{Liferay.Language.get('ratio')}:
									</ClayInput.GroupText>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem append>
									<ClaySelectWithOption
										onChange={handleAspectRationChange}
										options={ratios}
									/>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayToolbar.Section>
							<ClayButtonWithIcon
								displayType={null}
								onClick={handleRotate}
								symbol="rotate"
							/>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					<ClayToolbar.Item expand>
						<ClayToolbar.Section>
							<ClayButton.Group spaced>
								<ClayButtonWithIcon
									disabled={disabledZoomOut}
									displayType={null}
									onClick={handleZoomOut}
									symbol="hr"
								/>

								<ClayButtonWithIcon
									disabled={disabledZoomIn}
									displayType={null}
									onClick={handleZoomIn}
									symbol="plus"
								/>
							</ClayButton.Group>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayToolbar.Section>
							<ClayButton
								displayType={null}
								onClick={handleReset}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton onClick={handleSave}>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayToolbar.Section>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>
		</div>
	);
}

ImageEditor.propTypes = {
	imageId: PropTypes.string.isRequired,
	imageSrc: PropTypes.string.isRequired,
	itemReturnType: PropTypes.string,
	mimeType: PropTypes.string,
	onCancel: PropTypes.func,
	onSave: PropTypes.func,
	saveURL: PropTypes.string.isRequired,
};

export default ImageEditor;
