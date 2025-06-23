/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {openToast, useId} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

import {CheckboxField} from '../../../../../../app/components/fragment_configuration_fields/CheckboxField';
import {SelectField} from '../../../../../../app/components/fragment_configuration_fields/SelectField';
import {TextField} from '../../../../../../app/components/fragment_configuration_fields/TextField';
import {COMMON_STYLES_ROLES} from '../../../../../../app/config/constants/commonStylesRoles';
import {
	useItemLocalConfig,
	useUpdateItemLocalConfig,
} from '../../../../../../app/contexts/LocalConfigContext';
import {
	useSelector,
	useSelectorRef,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import {formIsMapped} from '../../../../../../app/utils/formIsMapped';
import {formIsRestricted} from '../../../../../../app/utils/formIsRestricted';
import {formIsUnavailable} from '../../../../../../app/utils/formIsUnavailable';
import {getEditableLocalizedValue} from '../../../../../../app/utils/getEditableLocalizedValue';
import {hasLocalizableFields} from '../../../../../../app/utils/hasLocalizableFields';
import {setIn} from '../../../../../../app/utils/setIn';
import {useSaveFormConfig} from '../../../../../../app/utils/useSaveFormConfig';
import CurrentLanguageFlag from '../../../../../../common/components/CurrentLanguageFlag';
import DisplayPageSelector from '../../../../../../common/components/DisplayPageSelector';
import {LayoutSelector} from '../../../../../../common/components/LayoutSelector';
import {CommonStyles} from './CommonStyles';
import ContainerDisplayOptions from './ContainerDisplayOptions';
import FormMappingOptions from './FormMappingOptions';
import FormMultistepOptions from './FormMultistepOptions';

export function FormGeneralPanel({item}) {
	const isMounted = useIsMounted();
	const updateItemLocalConfig = useUpdateItemLocalConfig();

	const saveFormConfig = useSaveFormConfig(item);

	useEffect(() => {
		return () => {
			if (!isMounted()) {
				updateItemLocalConfig(item.itemId, {
					showMessagePreview: false,
				});
			}
		};
	}, [isMounted, item.itemId, updateItemLocalConfig]);

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

	return (
		<>
			<FormOptions item={item} onValueSelect={saveFormConfig} />

			{formIsMapped(item) && (
				<>
					<div className="mb-3 panel-group-sm">
						<ClayPanel
							collapsable
							defaultExpanded
							displayTitle={Liferay.Language.get(
								'actions-after-submit'
							)}
							displayType="unstyled"
							showCollapseIcon
						>
							<ClayPanel.Body>
								<SuccessInteractionOptions
									item={item}
									onValueSelect={saveFormConfig}
								/>
							</ClayPanel.Body>
						</ClayPanel>
					</div>

					<LocalizationOptions
						item={item}
						onValueSelect={saveFormConfig}
					/>
				</>
			)}

			<div className="mb-3 panel-group-sm">
				<ClayPanel
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get('frame')}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						{formIsMapped(item) ? (
							<ContainerDisplayOptions item={item} />
						) : null}

						<CommonStyles
							commonStylesValues={item.config.styles || {}}
							embedInCollapsableSection={false}
							item={item}
							role={COMMON_STYLES_ROLES.general}
						/>
					</ClayPanel.Body>
				</ClayPanel>
			</div>
		</>
	);
}

function FormOptions({item, onValueSelect}) {
	return (
		<div className="mb-3 panel-group-sm">
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('form-container-options')}
				displayType="unstyled"
				showCollapseIcon
			>
				<ClayPanel.Body>
					<FormMappingOptions
						item={item}
						onValueSelect={onValueSelect}
					/>

					{formIsMapped(item) ? (
						<FormMultistepOptions
							item={item}
							onValueSelect={onValueSelect}
						/>
					) : null}
				</ClayPanel.Body>
			</ClayPanel>
		</div>
	);
}

const EMBEDDED_OPTION = 'embedded';
const LAYOUT_OPTION = 'page';
const URL_OPTION = 'url';
const DISPLAY_PAGE_OPTION = 'displayPage';
const STAY_OPTION = 'none';

const SUCCESS_MESSAGE_OPTIONS = [
	{
		label: Liferay.Language.get('stay-in-page'),
		value: STAY_OPTION,
	},
	{
		label: Liferay.Language.get('show-embedded-message'),
		value: EMBEDDED_OPTION,
	},
	{
		label: Liferay.Language.get('go-to-page'),
		value: LAYOUT_OPTION,
	},
	{
		label: Liferay.Language.get('go-to-external-url'),
		value: URL_OPTION,
	},
	{
		label: Liferay.Language.get('go-to-entry-display-page'),
		value: DISPLAY_PAGE_OPTION,
	},
];

function SuccessInteractionOptions({item, onValueSelect}) {
	const localConfig = useItemLocalConfig(item.itemId);
	const updateItemLocalConfig = useUpdateItemLocalConfig();

	const {successMessage: interactionConfig = {}} = item.config;

	const {
		displayPage,
		layout,
		message,
		notificationText,
		showNotification,
		type,
		url,
	} = interactionConfig || {};

	const languageId = useSelector(selectLanguageId);

	const helpTextId = useId();
	const previewId = useId();

	const localizedNotificationText = getEditableLocalizedValue(
		notificationText,
		languageId,
		Liferay.Language.get('your-information-was-successfully-received')
	);

	const onConfigChange = useCallback(
		(config, override = false) => {
			const nextConfig = override
				? config
				: {
						...interactionConfig,
						...config,
					};

			onValueSelect({successMessage: nextConfig});
		},
		[interactionConfig, onValueSelect]
	);

	const [showNotificationPreview, setShowNotificationPreview] = useState(
		item.config.showNotificationPreview
	);

	const hidePreview = () => {
		const previewElement = document.getElementById(previewId);

		previewElement?.remove();
	};

	return (
		<>
			<SelectField
				field={{
					label: Liferay.Language.get('success-action'),
					name: 'source',
					typeOptions: {
						validValues: SUCCESS_MESSAGE_OPTIONS,
					},
				}}
				onValueSelect={(_name, type) => onConfigChange({type}, true)}
				value={type || EMBEDDED_OPTION}
			/>

			{type === LAYOUT_OPTION && (
				<LayoutSelector
					mappedLayout={layout}
					onLayoutSelect={(selectedLayout) =>
						onConfigChange({layout: selectedLayout})
					}
				/>
			)}

			{(!type || type === EMBEDDED_OPTION) && (
				<>
					<ClayForm.Group small>
						<ClayInput.Group className="align-items-end" small>
							<ClayInput.GroupItem>
								<TextField
									field={{
										label: Liferay.Language.get(
											'embedded-message'
										),
									}}
									onValueSelect={(_, value) =>
										onConfigChange({
											message: setIn(
												message || {},
												languageId,
												value
											),
											type: EMBEDDED_OPTION,
										})
									}
									value={getEditableLocalizedValue(
										message,
										languageId,
										Liferay.Language.get(
											'thank-you.-your-information-was-successfully-received'
										)
									)}
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem shrink>
								<CurrentLanguageFlag />
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayForm.Group>
					<ClayForm.Group small>
						<ClayToggle
							label={Liferay.Language.get(
								'preview-success-message'
							)}
							onToggle={(checked) =>
								updateItemLocalConfig(item.itemId, {
									showMessagePreview: checked,
								})
							}
							toggled={localConfig.showMessagePreview}
						/>
					</ClayForm.Group>
				</>
			)}

			{type === URL_OPTION && (
				<ClayForm.Group small>
					<ClayInput.Group className="align-items-end" small>
						<ClayInput.GroupItem>
							<TextField
								aria-describedby={helpTextId}
								field={{
									label: Liferay.Language.get('external-url'),
									typeOptions: {
										placeholder: 'https://url.com',
									},
								}}
								onValueSelect={(_, value) =>
									onConfigChange({
										url: setIn(
											url || {},
											languageId,
											value
										),
									})
								}
								value={getEditableLocalizedValue(
									url,
									languageId
								)}
							/>
						</ClayInput.GroupItem>

						<ClayInput.GroupItem shrink>
							<CurrentLanguageFlag />
						</ClayInput.GroupItem>
					</ClayInput.Group>

					<p
						className="m-0 mt-1 small text-secondary"
						id={helpTextId}
					>
						{Liferay.Language.get(
							'urls-must-have-a-valid-protocol'
						)}
					</p>
				</ClayForm.Group>
			)}

			{type === DISPLAY_PAGE_OPTION && (
				<DisplayPageSelector
					mappingIds={item.config}
					onConfigChange={onConfigChange}
					selectedValue={displayPage}
				/>
			)}

			{type !== URL_OPTION && (
				<>
					<ClayForm.Group small>
						<CheckboxField
							field={{
								label: Liferay.Language.get(
									'show-notification-after-submit'
								),
								name: 'showNotification',
							}}
							onValueSelect={(name, value) => {
								onConfigChange({[name]: value});
							}}
							value={showNotification}
						/>
					</ClayForm.Group>

					{showNotification && (
						<>
							<ClayForm.Group small>
								<ClayInput.Group
									className="align-items-end c-mb-2"
									small
								>
									<ClayInput.GroupItem>
										<TextField
											field={{
												label: Liferay.Language.get(
													'success-notification-text'
												),
											}}
											onValueSelect={(_, value) => {
												if (showNotificationPreview) {
													setShowNotificationPreview(
														false
													);
													hidePreview();
												}
												onConfigChange({
													notificationText: setIn(
														notificationText || {},
														languageId,
														value
													),
												});
											}}
											value={localizedNotificationText}
										/>
									</ClayInput.GroupItem>

									<ClayInput.GroupItem shrink>
										<CurrentLanguageFlag />
									</ClayInput.GroupItem>
								</ClayInput.Group>

								<ClayButton
									aria-label={Liferay.Language.get(
										'preview-success-notification'
									)}
									disabled={showNotificationPreview}
									displayType="secondary"
									onClick={() => {
										setShowNotificationPreview(true);

										openToast({
											message: localizedNotificationText,
											onClose: () =>
												setShowNotificationPreview(
													false
												),
											toastProps: {
												id: previewId,
											},
										});
									}}
								>
									{Liferay.Language.get('preview')}
								</ClayButton>
							</ClayForm.Group>
						</>
					)}
				</>
			)}
		</>
	);
}

const DISABLED_OPTION = 'disabled';
const READ_ONLY_OPTION = 'read-only';

const UNLOCALIZED_FIELDS_STATE_OPTIONS = [
	{
		label: Liferay.Language.get('disabled'),
		value: DISABLED_OPTION,
	},
	{
		label: Liferay.Language.get('read-only'),
		value: READ_ONLY_OPTION,
	},
];

function LocalizationOptions({item, onValueSelect}) {
	const languageId = useSelector(selectLanguageId);

	const stateRef = useSelectorRef((state) => state);

	const {localizationConfig = {}} = item.config;

	const {unlocalizedFieldsMessage, unlocalizedFieldsState} =
		localizationConfig || {};

	const unlocalizedMessage = getEditableLocalizedValue(
		unlocalizedFieldsMessage,
		languageId,
		Liferay.Language.get('this-field-cannot-be-localized')
	);

	const helpTextId = useId();

	const [showLocalizationOptions, setShowLocalizationOptions] =
		useState(false);

	useEffect(() => {
		hasLocalizableFields(stateRef.current, item.itemId).then(
			(hasLocalizableFields) => {
				setShowLocalizationOptions(hasLocalizableFields);
			}
		);
	}, [stateRef, item.itemId]);

	return showLocalizationOptions ? (
		<div className="mb-3 panel-group-sm">
			<ClayPanel
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('unlocalizable-fields')}
				displayType="unstyled"
				showCollapseIcon
			>
				<ClayPanel.Body>
					<p className="text-secondary">
						{Liferay.Language.get(
							'configure-unlocalizable-fields-when-localization-action-is-taken'
						)}
					</p>

					<ClayForm.Group small>
						<SelectField
							field={{
								label: Liferay.Language.get(
									'unlocalizable-fields-state'
								),
								name: 'unlocalizableFieldsState',
								typeOptions: {
									validValues:
										UNLOCALIZED_FIELDS_STATE_OPTIONS,
								},
							}}
							onValueSelect={(_name, value) =>
								onValueSelect({
									localizationConfig: {
										...localizationConfig,
										unlocalizedFieldsState: value,
									},
								})
							}
							value={unlocalizedFieldsState || DISABLED_OPTION}
						/>
					</ClayForm.Group>

					<ClayForm.Group small>
						<ClayInput.Group className="align-items-end" small>
							<ClayInput.GroupItem>
								<TextField
									aria-describedby={helpTextId}
									field={{
										label: Liferay.Language.get(
											'unlocalizable-fields-message'
										),
									}}
									onValueSelect={(_, value) =>
										onValueSelect({
											localizationConfig: setIn(
												localizationConfig || {},
												[
													'unlocalizedFieldsMessage',
													languageId,
												],

												value
											),
										})
									}
									value={unlocalizedMessage}
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem shrink>
								<CurrentLanguageFlag />
							</ClayInput.GroupItem>
						</ClayInput.Group>

						<p
							className="m-0 mt-1 small text-secondary"
							id={helpTextId}
						>
							{Liferay.Language.get(
								'this-message-appears-over-the-help-icon-of-unlocalizable-fields'
							)}
						</p>
					</ClayForm.Group>
				</ClayPanel.Body>
			</ClayPanel>
		</div>
	) : null;
}
