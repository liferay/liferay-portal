/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React, {useCallback, useState} from 'react';

import {CheckboxField} from '../../../../../../app/components/fragment_configuration_fields/CheckboxField';
import {BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../app/config/constants/backgroundImageFragmentEntryProcessor';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../../../../app/config/constants/editableTypes';
import {STANDARD_IMAGE_SIZE_LIMIT} from '../../../../../../app/config/constants/standardImageSizeLimit';
import {VIEWPORT_SIZES} from '../../../../../../app/config/constants/viewportSizes';
import {config} from '../../../../../../app/config/index';
import {useGlobalContext} from '../../../../../../app/contexts/GlobalContext';
import {
	useDispatch,
	useSelector,
} from '../../../../../../app/contexts/StoreContext';
import selectEditableValueContent from '../../../../../../app/selectors/selectEditableValueContent';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import updateEditableValuesThunk from '../../../../../../app/thunks/updateEditableValues';
import isMapped from '../../../../../../app/utils/editable_value/isMapped';
import isMappedToCollection from '../../../../../../app/utils/editable_value/isMappedToCollection';
import isMappedToInfoItem from '../../../../../../app/utils/editable_value/isMappedToInfoItem';
import {getEditableLocalizedValue} from '../../../../../../app/utils/getEditableLocalizedValue';
import {setIn} from '../../../../../../app/utils/setIn';
import {updateIn} from '../../../../../../app/utils/updateIn';
import {ImageSelector} from '../../../../../../common/components/ImageSelector';
import {ImageSelectorDescription} from '../../../../../../common/components/ImageSelectorDescription';
import {ImageSelectorSize} from '../../../../../../common/components/ImageSelectorSize';
import {getEditableItemPropTypes} from '../../../../../../prop_types/index';
import {MappingPanel} from './MappingPanel';

const SOURCE_OPTIONS = {
	direct: {
		label: Liferay.Language.get('direct'),
		value: 'direct',
	},
	mapping: {
		label: Liferay.Language.get('mapping[content]'),
		value: 'mapping',
	},
};

export default function ImageSourcePanel({item}) {
	const dispatch = useDispatch();
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const sourceSelectionInputId = useId();

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const editableValues =
		fragmentEntryLinks[item.fragmentEntryLinkId].editableValues;

	const editableValue =
		editableValues[item.editableValueNamespace][item.editableId];

	const [source, setSource] = useState(() =>
		isMapped(editableValue)
			? SOURCE_OPTIONS.mapping.value
			: SOURCE_OPTIONS.direct.value
	);

	const handleSourceChanged = (event) => {
		setSource(event.target.value);

		if (Object.keys(editableValue).length) {
			dispatch(
				updateEditableValuesThunk({
					editableValues: setIn(
						editableValues,
						[item.editableValueNamespace, item.editableId],
						{
							config: {
								...(editableValue.config || {}),
								alt: {},
								imageConfiguration: {},
							},
						}
					),
					fragmentEntryLinkId: item.fragmentEntryLinkId,
				})
			);
		}
	};

	let ConfigurationPanel = DirectImagePanel;

	if (source === SOURCE_OPTIONS.mapping.value) {
		ConfigurationPanel = MappingImagePanel;
	}

	return (
		<>
			{selectedViewportSize === VIEWPORT_SIZES.desktop && (
				<ClayForm>
					<ClayForm.Group>
						<label htmlFor={sourceSelectionInputId}>
							{Liferay.Language.get('source-selection')}
						</label>

						<ClaySelectWithOption
							className="form-control form-control-sm mb-3"
							id={sourceSelectionInputId}
							onChange={handleSourceChanged}
							options={Object.values(SOURCE_OPTIONS)}
							value={source}
						/>
					</ClayForm.Group>
				</ClayForm>
			)}

			{ConfigurationPanel && <ConfigurationPanel item={item} />}
		</>
	);
}

ImageSourcePanel.propTypes = {
	item: getEditableItemPropTypes().isRequired,
};

function DirectImagePanel({item}) {
	const {editableId, fragmentEntryLinkId, type} = item;

	const dispatch = useDispatch();
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const languageId = useSelector(selectLanguageId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const processorKey =
		type === EDITABLE_TYPES.backgroundImage
			? BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR
			: EDITABLE_FRAGMENT_ENTRY_PROCESSOR;

	const editableValues =
		fragmentEntryLinks[fragmentEntryLinkId].editableValues;

	const editableValue = editableValues[processorKey]?.[editableId];

	const editableConfig = editableValue.config || {};

	const editableContent = selectEditableValueContent(
		{fragmentEntryLinks, languageId},
		fragmentEntryLinkId,
		editableId,
		processorKey
	);

	const imageUrl =
		typeof editableContent === 'string'
			? editableContent
			: editableContent?.url;

	const imageTitle =
		editableContent?.title ||
		editableConfig.imageTitle ||
		(imageUrl === editableValue.defaultValue ? '' : imageUrl);

	const imageDescription = getEditableLocalizedValue(
		editableConfig.alt,
		languageId
	);

	const handleImageChanged = (nextImage) => {
		let nextEditableValue;

		if (isMapped(editableValue) || isMapped(nextImage)) {
			nextEditableValue = {
				config: setIn(editableValue.config, ['alt'], {}),
				...nextImage,
			};
		}
		else {
			nextEditableValue = setIn(
				editableValue,
				['config', 'alt', languageId],
				''
			);

			if (nextImage) {
				nextEditableValue[languageId] = nextImage;
			}
			else {
				delete nextEditableValue[languageId];
			}
		}

		if (!nextEditableValue.config?.imageConfiguration) {
			nextEditableValue = setIn(
				nextEditableValue,
				['config', 'imageConfiguration'],
				{}
			);
		}

		dispatch(
			updateEditableValuesThunk({
				editableValues: setIn(
					editableValues,
					[processorKey, editableId],
					nextEditableValue
				),
				fragmentEntryLinkId,
			})
		);
	};

	const handleImageDescriptionChanged = (nextImageDescription) => {
		dispatch(
			updateEditableValuesThunk({
				editableValues: updateIn(
					editableValues,
					[processorKey, editableId, 'config', 'alt'],
					(alt) => {

						// If alt is a string (old style), we need to
						// migrate it to an object to allow translations.

						if (typeof alt === 'string') {
							return {
								[config.defaultLanguageId]: alt,
								[languageId]: nextImageDescription,
							};
						}

						return {
							...alt,
							[languageId]: nextImageDescription,
						};
					},
					{}
				),
				fragmentEntryLinkId,
			})
		);
	};

	return (
		<>
			<ImageSelector
				fileEntryId={editableContent.fileEntryId}
				imageTitle={imageTitle}
				label={Liferay.Language.get('image')}
				onClearButtonPressed={() => {
					handleImageChanged(null);
				}}
				onImageSelected={handleImageChanged}
			/>

			<ImagePanelSizeSelector item={item} />

			{item.type === EDITABLE_TYPES.image && (
				<CheckboxField
					field={{
						defaultValue: false,
						description: Liferay.Language.get(
							'lazy-loading-can-help-to-improve-page-performance'
						),
						label: Liferay.Language.get('enable-lazy-loading'),
						name: 'lazyLoading',
					}}
					onValueSelect={(name, value) => {
						dispatch(
							updateEditableValuesThunk({
								editableValues: setIn(
									editableValues,
									[
										EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
										item.editableId,
										'config',
										name,
									],
									value
								),
								fragmentEntryLinkId: item.fragmentEntryLinkId,
							})
						);
					}}
					value={editableValue.config.lazyLoading}
				/>
			)}

			{selectedViewportSize === VIEWPORT_SIZES.desktop &&
				type === EDITABLE_TYPES.image && (
					<ImageSelectorDescription
						imageDescription={imageDescription}
						onImageDescriptionChanged={
							handleImageDescriptionChanged
						}
					/>
				)}
		</>
	);
}

DirectImagePanel.propTypes = {
	item: getEditableItemPropTypes().isRequired,
};

function MappingImagePanel({item}) {
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	return (
		<>
			{selectedViewportSize === VIEWPORT_SIZES.desktop ? (
				<MappingPanel item={item} />
			) : null}
			<ImagePanelSizeSelector item={item} />
		</>
	);
}

MappingImagePanel.propTypes = {
	item: getEditableItemPropTypes().isRequired,
};

function ImagePanelSizeSelector({item}) {
	const {editableId, fragmentEntryLinkId, type} = item;

	const dispatch = useDispatch();
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const globalContext = useGlobalContext();
	const languageId = useSelector(selectLanguageId);
	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const processorKey =
		type === EDITABLE_TYPES.backgroundImage
			? BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR
			: EDITABLE_FRAGMENT_ENTRY_PROCESSOR;

	const editableValues =
		fragmentEntryLinks[fragmentEntryLinkId].editableValues;

	const editableValue = editableValues[processorKey]?.[editableId];
	const editableConfig = editableValue.config || {};

	const getEditableElement = useCallback(() => {
		const fragmentElement = globalContext.document.querySelector(
			`[data-fragment-entry-link-id="${fragmentEntryLinkId}"]`
		);

		if (!fragmentElement) {
			return null;
		}

		return (
			fragmentElement.querySelector(
				`lfr-editable[id="${item.itemId}"]`
			) ||
			fragmentElement.querySelector(
				`[data-lfr-editable-id="${item.itemId}"]`
			)
		);
	}, [fragmentEntryLinkId, item.itemId, globalContext.document]);

	const editableContent = selectEditableValueContent(
		{fragmentEntryLinks, languageId},
		fragmentEntryLinkId,
		editableId,
		processorKey
	);

	const imageSizeId =
		editableConfig.imageConfiguration?.[selectedViewportSize];

	const handleImageSizeChanged = (imageSizeId) => {
		dispatch(
			updateEditableValuesThunk({
				editableValues: setIn(
					editableValues,
					[
						processorKey,
						editableId,
						'config',
						'imageConfiguration',
						selectedViewportSize,
					],
					imageSizeId
				),
				fragmentEntryLinkId,
			})
		);
	};

	return editableContent?.fileEntryId ||
		isMappedToInfoItem(editableValue) ||
		isMappedToCollection(editableValue) ? (
		<ImageSelectorSize
			fieldValue={
				isMapped(editableValue) ? editableValue : editableContent
			}
			getEditableElement={getEditableElement}
			imageSizeId={imageSizeId}
			imageSizeLimit={
				editableConfig.lazyLoading ? null : STANDARD_IMAGE_SIZE_LIMIT
			}
			onImageSizeIdChanged={
				item.type === EDITABLE_TYPES.image
					? handleImageSizeChanged
					: null
			}
		/>
	) : null;
}

ImagePanelSizeSelector.propTypes = {
	item: getEditableItemPropTypes().isRequired,
};
