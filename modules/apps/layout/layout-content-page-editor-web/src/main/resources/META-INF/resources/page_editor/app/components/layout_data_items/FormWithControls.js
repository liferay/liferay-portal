/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import React, {useEffect} from 'react';

import FormMappingOptions from '../../../plugins/browser/components/page_structure/components/item_configuration_panels/FormMappingOptions';
import {config} from '../../config';
import {useItemLocalConfig} from '../../contexts/LocalConfigContext';
import {useSaveObjectFields} from '../../contexts/ObjectDataContext';
import {useSelector, useSelectorCallback} from '../../contexts/StoreContext';
import selectLanguageId from '../../selectors/selectLanguageId';
import {formIsMapped} from '../../utils/formIsMapped';
import {formIsRestricted} from '../../utils/formIsRestricted';
import {formIsUnavailable} from '../../utils/formIsUnavailable';
import {getEditableLocalizedValue} from '../../utils/getEditableLocalizedValue';
import isItemEmpty from '../../utils/isItemEmpty';
import {useSaveFormConfig} from '../../utils/useSaveFormConfig';
import ContainerWithControls from './ContainerWithControls';

const FormWithControls = React.forwardRef(({children, item, ...rest}, ref) => {
	const localConfig = useItemLocalConfig(item.itemId);

	return (
		<form
			className={classNames('page-editor__form', {
				'page-editor__form--success': localConfig.showMessagePreview,
			})}
			onSubmit={(event) => event.preventDefault()}
			ref={ref}
		>
			<ContainerWithControls {...rest} item={item}>
				<Form item={item}>{children}</Form>
			</ContainerWithControls>
		</form>
	);
});

function Form({children, item}) {
	const localConfig = useItemLocalConfig(item.itemId);

	const showLoadingState = localConfig.loading;

	if (showLoadingState) {
		return <FormLoadingState />;
	}

	if (formIsUnavailable(item)) {
		return (
			<ClayAlert
				displayType="warning"
				title={`${Liferay.Language.get('warning')}:`}
			>
				{Liferay.Language.get(
					'this-content-is-currently-unavailable-or-has-been-deleted.-users-cannot-see-this-fragment'
				)}
			</ClayAlert>
		);
	}
	else if (formIsRestricted(item)) {
		return (
			<ClayAlert displayType="secondary">
				{Liferay.Language.get(
					'this-content-cannot-be-displayed-due-to-permission-restrictions'
				)}
			</ClayAlert>
		);
	}

	if (!formIsMapped(item)) {
		return <UnmappedForm item={item} />;
	}

	return <MappedForm item={item}>{children}</MappedForm>;
}

function MappedForm({children, item}) {
	const localConfig = useItemLocalConfig(item.itemId);

	const isEmpty = useSelectorCallback(
		(state) =>
			isItemEmpty(item, state.layoutData, state.selectedViewportSize),
		[item]
	);

	const {showMessagePreview} = localConfig;

	const {classNameId, classTypeId} = item.config;

	const saveFields = useSaveObjectFields({classNameId, classTypeId});

	useEffect(() => {
		saveFields();
	}, [saveFields]);

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

	return (
		<>
			{showMessagePreview && <FormSuccessMessage item={item} />}

			<div
				className={classNames('page-editor__form-children', {
					'd-none': showMessagePreview,
				})}
			>
				{children}
			</div>
		</>
	);
}

function UnmappedForm({item}) {
	const saveFormConfig = useSaveFormConfig(item);

	const localConfig = useItemLocalConfig(item.itemId);

	if (localConfig.showMessagePreview) {
		return <FormSuccessMessage item={item} />;
	}

	return (
		<div className="align-items-center bg-lighter d-flex flex-column page-editor__form-unmapped-state page-editor__no-fragments-state">
			<p className="page-editor__no-fragments-state__title">
				{Liferay.Language.get('map-your-form')}
			</p>

			<p className="mb-3 page-editor__no-fragments-state__message">
				{Liferay.Language.get(
					'select-a-content-type-to-start-creating-the-form'
				)}
			</p>

			<div
				className="cadmin"
				onClick={(event) => event.stopPropagation()}
			>
				<FormMappingOptions
					hideLabel={true}
					item={item}
					onValueSelect={saveFormConfig}
				/>
			</div>
		</div>
	);
}

function FormLoadingState() {
	return (
		<div className="bg-lighter page-editor__no-fragments-state">
			<ClayLoadingIndicator />

			<p className="m-0 page-editor__no-fragments-state__message">
				{Liferay.Language.get(
					'your-form-is-being-loaded.-this-may-take-some-time'
				)}
			</p>
		</div>
	);
}

function FormSuccessMessage({item}) {
	const languageId = useSelector(selectLanguageId);

	return (
		<div className="align-items-center d-flex justify-content-center p-5 page-editor__form__success-message">
			<span className="font-weight-semi-bold text-secondary">
				{getEditableLocalizedValue(
					item.config?.successMessage?.message,
					languageId,
					Liferay.Language.get(
						'thank-you.-your-information-was-successfully-received'
					)
				)}
			</span>
		</div>
	);
}

export default FormWithControls;
