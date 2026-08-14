/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';

import ItemSelector from '../../../common/components/ItemSelector';
import {ConfigurationFieldPropTypes} from '../../../prop_types/index';
import {CollectionItemContext} from '../../contexts/CollectionItemContext';
import {useDispatch} from '../../contexts/StoreContext';
import InfoItemService from '../../services/InfoItemService';
import itemSelectorValueToInfoItem from '../../utils/item_selector_value/itemSelectorValueToInfoItem';

export function ItemSelectorField({field, onValueSelect, value = {}}) {
	const collectionItemContext = useContext(CollectionItemContext);

	const {typeOptions = {}} = field;

	const collectionItem = collectionItemContext.collectionItem;

	const isWithinCollection = collectionItem !== null;

	const {className, classPK, externalReferenceCode} = isWithinCollection
		? collectionItem
		: value;

	return (
		<>
			<ItemSelector
				helpText={field.description}
				itemSelectorURL={typeOptions.infoItemSelectorURL}
				itemSubtype={typeOptions.itemSubtype}
				itemType={typeOptions.itemType}
				label={field.label}
				mimeTypes={typeOptions.mimeTypes}
				modalProps={{height: '60vh', size: typeOptions.modalSize}}
				onItemSelect={(item) => {
					onValueSelect(field.name, item);
				}}
				selectedItem={
					isWithinCollection
						? {
								...collectionItem,
								title:
									collectionItem.title ||
									Liferay.Language.get('collection-item'),
							}
						: value
				}
				showEditControls={!isWithinCollection}
				transformValueCallback={itemSelectorValueToInfoItem}
			/>

			{typeOptions.enableSelectTemplate && className && (
				<ClayForm.Group small>
					<TemplateSelector
						className={className}
						classPK={classPK}
						externalReferenceCode={externalReferenceCode}
						onTemplateSelect={(template) => {
							onValueSelect(field.name, {...value, template});
						}}
						selectedTemplate={value.template}
					/>
				</ClayForm.Group>
			)}
		</>
	);
}

ItemSelectorField.propTypes = {
	field: PropTypes.shape({
		...ConfigurationFieldPropTypes,
		typeOptions: PropTypes.shape({
			enableSelectTemplate: PropTypes.bool,
		}),
	}),
	onValueSelect: PropTypes.func.isRequired,
	value: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
};

const TemplateSelector = ({
	className,
	classPK,
	externalReferenceCode,
	onTemplateSelect,
	selectedTemplate,
}) => {
	const dispatch = useDispatch();
	const [availableTemplates, setAvailableTemplates] = useState([]);
	const isMounted = useIsMounted();
	const itemSelectorTemplateSelectId = useId();

	useEffect(() => {
		if (isMounted()) {
			InfoItemService.getAvailableTemplates({
				className,
				classPK,
				externalReferenceCode,
			}).then((response) => {
				setAvailableTemplates(response);
			});
		}
	}, [className, classPK, externalReferenceCode, dispatch, isMounted]);

	return (
		<>
			<ClayForm.Group small>
				<label htmlFor={itemSelectorTemplateSelectId}>
					{Liferay.Language.get('template')}
				</label>

				<select
					className="form-control"
					id={itemSelectorTemplateSelectId}
					onChange={(event) => {
						onTemplateSelect(
							event.target.options[event.target.selectedIndex]
								.dataset
						);
					}}
				>
					{availableTemplates.map((entry) => {
						if (entry.templates) {
							return (
								<optgroup key={entry.label} label={entry.label}>
									{entry.templates.map((template) => (
										<option
											data-info-item-renderer-key={
												template.infoItemRendererKey
											}
											data-template-key={
												template.templateKey
											}
											key={template.label}
											selected={
												selectedTemplate &&
												selectedTemplate.infoItemRendererKey ===
													template.infoItemRendererKey &&
												(!selectedTemplate.templateKey ||
													(selectedTemplate.templateKey &&
														selectedTemplate.templateKey ===
															template.templateKey))
											}
										>
											{template.label}
										</option>
									))}
								</optgroup>
							);
						}
						else {
							return (
								<option
									data-info-item-renderer-key={
										entry.infoItemRendererKey
									}
									key={entry.label}
									selected={
										selectedTemplate &&
										selectedTemplate.infoItemRendererKey ===
											entry.infoItemRendererKey
									}
								>
									{entry.label}
								</option>
							);
						}
					})}
				</select>
			</ClayForm.Group>
		</>
	);
};

TemplateSelector.propTypes = {
	className: PropTypes.string.isRequired,
	classPK: PropTypes.string.isRequired,
	onTemplateSelect: PropTypes.func.isRequired,
	selectedTemplate: PropTypes.shape({
		infoItemRendererKey: PropTypes.string,
		templateKey: PropTypes.string,
	}),
};
