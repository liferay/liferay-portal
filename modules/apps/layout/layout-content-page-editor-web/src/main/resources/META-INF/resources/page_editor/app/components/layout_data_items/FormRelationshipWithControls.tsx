/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React from 'react';

import FormRelationshipMappingOptions from '../../../plugins/browser/components/page_structure/components/item_configuration_panels/FormRelationshipMappingOptions';
import {FormRelationshipLayoutDataItem} from '../../../types/layout_data/FormRelationshipLayoutDataItem';
import {LayoutData} from '../../../types/layout_data/LayoutData';
import {config} from '../../config/index';
import {useItemLocalConfig} from '../../contexts/LocalConfigContext';
import {useSelector, useSelectorCallback} from '../../contexts/StoreContext';
import {ContainerWithControls} from '../../js-index';
import selectLanguageId from '../../selectors/selectLanguageId';
import isItemEmpty from '../../utils/isItemEmpty';
import FormRelationship from './FormRelationship';

export default React.forwardRef<
	HTMLDivElement,
	{
		children: React.ReactNode;
		item: FormRelationshipLayoutDataItem;
		layoutData: LayoutData;
	}
>(({children, item, ...rest}, ref) => {
	return (
		<ContainerWithControls
			className="page-editor__form-relationship"
			{...rest}
			item={item}
			ref={ref}
		>
			<FormRelationshipWithControls item={item}>
				{children}
			</FormRelationshipWithControls>
		</ContainerWithControls>
	);
});

function FormRelationshipWithControls({
	children,
	item,
}: {
	children: React.ReactNode;
	item: FormRelationshipLayoutDataItem;
}) {
	const localConfig = useItemLocalConfig(item.itemId);

	if (localConfig.loading) {
		return <LoadingState />;
	}

	const isMapped = Boolean(item.config.contentType);

	if (!isMapped) {
		return <UnmappedFormRelationship item={item} />;
	}

	return (
		<>
			<MappedFormRelationship item={item}>
				{children}
			</MappedFormRelationship>

			<AddButton label={item.config.buttonLabel} />
		</>
	);
}

function UnmappedFormRelationship({
	item,
}: {
	item: FormRelationshipLayoutDataItem;
}) {
	return (
		<div className="align-items-center bg-lighter d-flex flex-column page-editor__form-unmapped-state page-editor__no-fragments-state">
			<p className="page-editor__no-fragments-state__title">
				{Liferay.Language.get('map-your-form-relationship')}
			</p>

			<p className="mb-3 page-editor__no-fragments-state__message">
				{Liferay.Language.get('select-a-content-type')}
			</p>

			<div className="cadmin">
				<FormRelationshipMappingOptions item={item} showLabel={false} />
			</div>
		</div>
	);
}

function MappedFormRelationship({
	children,
	item,
}: {
	children: React.ReactNode;
	item: FormRelationshipLayoutDataItem;
}) {
	const isEmpty = useSelectorCallback(
		(state) =>
			isItemEmpty(item, state.layoutData, state.selectedViewportSize),
		[item]
	);

	if (isEmpty) {
		return (
			<div className="page-editor__no-fragments-state text-center">
				<img
					className="page-editor__no-fragments-state__image"
					src={`${config.imagesPath}/drag_and_drop.svg`}
				/>

				<p className="page-editor__no-fragments-state__message">
					{Liferay.Language.get(
						'drag-and-drop-fragments-or-widgets-here'
					)}
				</p>
			</div>
		);
	}

	return <FormRelationship item={item}>{children}</FormRelationship>;
}

function LoadingState() {
	return (
		<div className="bg-lighter page-editor__no-fragments-state">
			<ClayLoadingIndicator />

			<p className="m-0 page-editor__no-fragments-state__message">
				{Liferay.Language.get(
					'your-form-relationship-is-being-loaded.-this-may-take-some-time'
				)}
			</p>
		</div>
	);
}

function AddButton({label}: {label: Liferay.Language.LocalizedValue<string>}) {
	const languageId = useSelector(selectLanguageId);

	const value =
		label?.[languageId] ??
		label?.[config.defaultLanguageId] ??
		Liferay.Language.get('add-new');

	return (
		<ClayButton
			aria-label={value ? '' : Liferay.Language.get('add-new')}
			borderless
			displayType="primary"
			size="sm"
		>
			<ClayIcon
				className={classNames('text-primary', {
					'mr-2': value,
				})}
				style={{transform: 'rotate(45deg)'}}
				symbol="times-circle-full"
			/>

			{value}
		</ClayButton>
	);
}
