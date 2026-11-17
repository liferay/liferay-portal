/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useState} from 'react';

import {useGetFieldValue} from '../../app/contexts/CollectionItemContext';
import {useGlobalContext} from '../../app/contexts/GlobalContext';
import {useSelector} from '../../app/contexts/StoreContext';
import selectLanguageId from '../../app/selectors/selectLanguageId';
import ImageService from '../../app/services/ImageService';
import isMapped from '../../app/utils/editable_value/isMapped';
import resolveEditableValue from '../../app/utils/editable_value/resolveEditableValue';

export const DEFAULT_IMAGE_SIZE_ID = 'auto';

const DEFAULT_GET_EDITABLE_ELEMENT = () => null;

const DEFAULT_IMAGE_SIZE = {
	size: null,
	value: DEFAULT_IMAGE_SIZE_ID,
	width: null,
};

/**
 * @param {object} props
 * @param {number} [props.imageSizeLimit] Image size limit to show warnings, expressed in KB.
 */
export function ImageSelectorSize({
	fieldValue,
	getEditableElement = DEFAULT_GET_EDITABLE_ELEMENT,
	imageSizeId,
	imageSizeLimit,
	onImageSizeIdChanged = null,
}) {
	const [fileEntryId, setFileEntryId] = useState(
		fieldValue.fileEntryId || ''
	);
	const getFieldValue = useGetFieldValue();
	const globalContext = useGlobalContext();
	const imageSizeSelectId = useId();
	const [imageSize, setImageSize] = useState(DEFAULT_IMAGE_SIZE);
	const [imageSizes, setImageSizes] = useState([]);
	const isMounted = useIsMounted();
	const languageId = useSelector(selectLanguageId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const showImageSizeWarning = useMemo(() => {
		if (isNullOrUndefined(imageSizeLimit)) {
			return false;
		}

		const imageSizeValue = Number(imageSize.size);

		if (isNaN(imageSizeValue)) {
			return false;
		}

		return imageSizeValue >= imageSizeLimit;
	}, [imageSize.size, imageSizeLimit]);

	useEffect(() => {
		if (fieldValue.fileEntryId) {
			setFileEntryId(fieldValue.fileEntryId);
		}
		else if (isMapped(fieldValue)) {
			resolveEditableValue(fieldValue, languageId, getFieldValue).then(
				(value) => {
					if (isMounted()) {
						setFileEntryId(value?.fileEntryId || '');
					}
				}
			);
		}
	}, [fieldValue, getFieldValue, isMounted, languageId]);

	useEffect(() => {
		const computedImageSize =
			imageSizes.find((imageSize) => imageSize.value === imageSizeId) ||
			DEFAULT_IMAGE_SIZE;

		// If selected imageSizeId is 'auto', we try to resolve the
		// computed real image size based on current viewport and the image
		// HTMLElement.

		if (computedImageSize.value === DEFAULT_IMAGE_SIZE_ID) {
			const editableElement = getEditableElement();

			const setAutoSize = () => {
				editableElement?.removeEventListener('load', setAutoSize);

				const autoSize =
					imageSizes.find(({mediaQuery}) => {
						const globalWindow = globalContext.window;

						return mediaQuery
							? globalWindow.matchMedia(mediaQuery).matches
							: true;
					}) ||
					imageSizes.find(({value}) => {
						return value === DEFAULT_IMAGE_SIZE_ID;
					}) ||
					DEFAULT_IMAGE_SIZE;

				setImageSize({
					...autoSize,
					width: parseInt(
						autoSize.width ||
							editableElement?.naturalWidth ||
							editableElement?.getBoundingClientRect().width ||
							globalContext.document.body.getBoundingClientRect()
								.width,
						10
					),
				});
			};

			if (
				!editableElement ||
				editableElement.complete ||
				editableElement.tagName !== 'IMG'
			) {
				setAutoSize();
			}
			else {
				editableElement.addEventListener('load', setAutoSize);

				return () => {
					editableElement.removeEventListener('load', setAutoSize);
				};
			}
		}
		else {
			setImageSize(computedImageSize);
		}
	}, [
		getEditableElement,
		globalContext,
		imageSizeId,
		imageSizes,
		selectedViewportSize,
	]);

	useEffect(() => {
		if (!fileEntryId) {
			setImageSizes([]);

			return;
		}

		ImageService.getAvailableImageConfigurations({
			fileEntryId,
		}).then((availableImageSizes) => {
			setImageSizes(
				[...availableImageSizes].sort(
					(imageSizeA, imageSizeB) =>
						imageSizeA.width - imageSizeB.width
				)
			);
		});
	}, [fileEntryId]);

	if (!fileEntryId) {
		return null;
	}

	const warningText = `${Liferay.Language.get(
		'big-image-file-size-used'
	)} ${Liferay.Language.get(
		'please-consider-configuring-adaptive-media-lazy-loading-or-reducing-the-image-size'
	)}`;

	return (
		<ClayForm.Group
			className={classNames('mb-3', {
				'has-warning': showImageSizeWarning,
			})}
		>
			{onImageSizeIdChanged && (
				<ClayForm.Group className="mb-2">
					<label htmlFor={imageSizeSelectId}>
						{Liferay.Language.get('resolution')}

						{showImageSizeWarning ? (
							<span className="sr-only">({warningText})</span>
						) : null}
					</label>

					<ClaySelectWithOption
						className="form-control form-control-sm"
						id={imageSizeSelectId}
						name={imageSizeSelectId}
						onChange={(event) =>
							onImageSizeIdChanged(event.target.value)
						}
						options={imageSizes.map(({label, value}) => ({
							label,
							value,
						}))}
						value={imageSizeId || DEFAULT_IMAGE_SIZE_ID}
					/>
				</ClayForm.Group>
			)}

			{imageSize.width ? (
				<p className="m-0 text-2 text-secondary">
					<strong>{Liferay.Language.get('width')}:</strong>

					<span className="ml-1">{imageSize.width}px</span>
				</p>
			) : null}

			{imageSize.size ? (
				<p className="m-0 text-2 text-secondary">
					<strong>{Liferay.Language.get('file-size')}:</strong>

					<span className="ml-1">
						{Number(imageSize.size).toFixed(2)}kB
					</span>
				</p>
			) : null}

			{showImageSizeWarning ? (
				<ClayForm.FeedbackGroup>
					<ClayForm.FeedbackItem className="font-weight-normal text-2">
						<ClayForm.FeedbackIndicator symbol="warning-full" />

						{warningText}
					</ClayForm.FeedbackItem>
				</ClayForm.FeedbackGroup>
			) : null}
		</ClayForm.Group>
	);
}

ImageSelectorSize.propTypes = {
	fieldValue: PropTypes.oneOfType([
		PropTypes.shape({
			fileEntryId: PropTypes.string.isRequired,
		}),
		PropTypes.shape({
			classNameId: PropTypes.string.isRequired,
			classPK: PropTypes.string.isRequired,
			fieldId: PropTypes.string.isRequired,
		}),
		PropTypes.shape({
			collectionFieldId: PropTypes.string.isRequired,
		}),
	]).isRequired,
	getEditableElement: PropTypes.func,
	imageSizeId: PropTypes.string,
	imageSizeLimit: PropTypes.number,
	onImageSizeIdChanged: PropTypes.func,
};
