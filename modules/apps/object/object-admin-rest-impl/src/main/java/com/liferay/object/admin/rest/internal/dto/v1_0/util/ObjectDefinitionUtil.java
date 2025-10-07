/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.util;

import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinitionSetting;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.dto.v1_0.Status;
import com.liferay.object.admin.rest.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectActionUtil;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.util.comparator.ObjectFieldCreateDateComparator;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class ObjectDefinitionUtil {

	public static ObjectDefinition toObjectDefinition(
		GroupLocalService groupLocalService, Locale locale,
		NotificationTemplateLocalService notificationTemplateLocalService,
		ObjectActionLocalService objectActionLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		DTOConverter<com.liferay.object.model.ObjectField, ObjectField>
			objectFieldDTOConverter,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectLayoutLocalService objectLayoutLocalService,
		DTOConverter
			<com.liferay.object.model.ObjectRelationship, ObjectRelationship>
				objectRelationshipDTOConverter,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		DTOConverter
			<com.liferay.object.model.ObjectValidationRule,
			 ObjectValidationRule> objectValidationRuleDTOConverter,
		ObjectValidationRuleLocalService objectValidationRuleLocalService,
		DTOConverter<com.liferay.object.model.ObjectView, ObjectView>
			objectViewDTOConverter,
		ObjectViewLocalService objectViewLocalService, Portal portal,
		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry,
		UserLocalService userLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		if (serviceBuilderObjectDefinition == null) {
			return null;
		}

		String restContextPath = StringPool.BLANK;

		if (serviceBuilderObjectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						serviceBuilderObjectDefinition.getName());

			if (systemObjectDefinitionManager != null) {
				JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
					systemObjectDefinitionManager.
						getJaxRsApplicationDescriptor();

				restContextPath =
					"/o/" + jaxRsApplicationDescriptor.getRESTContextPath();
			}
		}
		else {
			restContextPath =
				"/o" + serviceBuilderObjectDefinition.getRESTContextPath();
		}

		String finalRESTContextPath = restContextPath;

		return new ObjectDefinition() {
			{
				setAccountEntryRestricted(
					serviceBuilderObjectDefinition::isAccountEntryRestricted);
				setAccountEntryRestrictedObjectFieldName(
					() -> {
						com.liferay.object.model.ObjectField
							serviceBuilderObjectField =
								objectFieldLocalService.fetchObjectField(
									serviceBuilderObjectDefinition.
										getAccountEntryRestrictedObjectFieldId());

						if (serviceBuilderObjectField == null) {
							return "";
						}

						return serviceBuilderObjectField.getName();
					});
				setActive(serviceBuilderObjectDefinition::isActive);
				setClassName(serviceBuilderObjectDefinition::getClassName);
				setCreator(
					() -> CreatorUtil.toCreator(
						null, portal,
						userLocalService.fetchUser(
							serviceBuilderObjectDefinition.getUserId())));
				setDateCreated(serviceBuilderObjectDefinition::getCreateDate);
				setDateModified(
					serviceBuilderObjectDefinition::getModifiedDate);
				setDefaultLanguageId(
					() -> LocalizationUtil.getDefaultLanguageId(
						serviceBuilderObjectDefinition.getLabel()));
				setEnableCategorization(
					serviceBuilderObjectDefinition::isEnableCategorization);
				setEnableComments(
					serviceBuilderObjectDefinition::isEnableComments);
				setEnableFormContainer(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled(
								serviceBuilderObjectDefinition.getCompanyId(),
								"LPD-17564")) {

							return null;
						}

						return serviceBuilderObjectDefinition.
							isEnableFormContainer();
					});
				setEnableFriendlyURLCustomization(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
							return null;
						}

						return serviceBuilderObjectDefinition.
							isEnableFriendlyURLCustomization();
					});
				setEnableIndexSearch(
					serviceBuilderObjectDefinition::isEnableIndexSearch);
				setEnableLocalization(
					serviceBuilderObjectDefinition::isEnableLocalization);
				setEnableObjectEntryDraft(
					serviceBuilderObjectDefinition::isEnableObjectEntryDraft);
				setEnableObjectEntryHistory(
					serviceBuilderObjectDefinition::isEnableObjectEntryHistory);
				setEnableObjectEntrySchedule(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
							return null;
						}

						return serviceBuilderObjectDefinition.
							isEnableObjectEntrySchedule();
					});
				setEnableObjectEntrySubscription(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled(
								serviceBuilderObjectDefinition.getCompanyId(),
								"LPD-17564")) {

							return null;
						}

						return serviceBuilderObjectDefinition.
							isEnableObjectEntrySubscription();
					});
				setEnableObjectEntryVersioning(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
							return null;
						}

						return serviceBuilderObjectDefinition.
							isEnableObjectEntryVersioning();
					});
				setExternalReferenceCode(
					serviceBuilderObjectDefinition::getExternalReferenceCode);
				setFriendlyURLSeparator(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
							return null;
						}

						return serviceBuilderObjectDefinition.
							getFriendlyURLSeparator();
					});
				setId(serviceBuilderObjectDefinition::getObjectDefinitionId);
				setLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectDefinition.getLabelMap()));
				setModifiable(serviceBuilderObjectDefinition::isModifiable);
				setName(serviceBuilderObjectDefinition::getShortName);
				setObjectActions(
					() -> TransformUtil.transformToArray(
						objectActionLocalService.getObjectActions(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId()),
						objectAction -> ObjectActionUtil.toObjectAction(
							null, locale, notificationTemplateLocalService,
							objectDefinitionLocalService, objectAction),
						ObjectAction.class));
				setObjectDefinitionSettings(
					() -> TransformUtil.transformToArray(
						serviceBuilderObjectDefinition.
							getObjectDefinitionSettings(),
						objectDefinitionSetting -> _toObjectDefinitionSetting(
							groupLocalService, objectDefinitionLocalService,
							objectDefinitionSetting),
						ObjectDefinitionSetting.class));
				setObjectFields(
					() -> TransformUtil.transformToArray(
						objectFieldLocalService.getObjectFields(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS,
							ObjectFieldCreateDateComparator.getInstance(true)),
						objectField -> objectFieldDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								false, null, null, null, locale, null, null),
							objectField),
						ObjectField.class));
				setObjectFolderExternalReferenceCode(
					serviceBuilderObjectDefinition::
						getObjectFolderExternalReferenceCode);
				setObjectLayouts(
					() -> TransformUtil.transformToArray(
						objectLayoutLocalService.getObjectLayouts(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId()),
						objectLayout -> ObjectLayoutUtil.toObjectLayout(
							null, objectDefinitionLocalService,
							objectFieldLocalService,
							objectRelationshipLocalService, objectLayout),
						ObjectLayout.class));
				setObjectRelationships(
					() -> TransformUtil.transformToArray(
						objectRelationshipLocalService.getObjectRelationships(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS),
						objectRelationship ->
							objectRelationshipDTOConverter.toDTO(
								new DefaultDTOConverterContext(
									false, null, null, null, locale, null,
									null),
								objectRelationship),
						ObjectRelationship.class));
				setObjectValidationRules(
					() -> TransformUtil.transformToArray(
						objectValidationRuleLocalService.
							getObjectValidationRules(
								serviceBuilderObjectDefinition.
									getObjectDefinitionId()),
						objectValidationRule ->
							objectValidationRuleDTOConverter.toDTO(
								new DefaultDTOConverterContext(
									false, null, null, null, locale, null,
									null),
								objectValidationRule),
						ObjectValidationRule.class));
				setObjectViews(
					() -> TransformUtil.transformToArray(
						objectViewLocalService.getObjectViews(
							serviceBuilderObjectDefinition.
								getObjectDefinitionId()),
						objectView -> objectViewDTOConverter.toDTO(
							new DefaultDTOConverterContext(
								false, null, null, null, locale, null, null),
							objectView),
						ObjectView.class));
				setPanelCategoryKey(
					serviceBuilderObjectDefinition::getPanelCategoryKey);
				setParameterRequired(
					() -> finalRESTContextPath.matches(".*/\\{\\w+}/.*"));
				setPluralLabel(
					() -> LocalizedMapUtil.getLanguageIdMap(
						serviceBuilderObjectDefinition.getPluralLabelMap()));
				setPortlet(serviceBuilderObjectDefinition::isPortlet);
				setRestContextPath(() -> finalRESTContextPath);
				setRootObjectDefinitionExternalReferenceCode(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled(
								serviceBuilderObjectDefinition.getCompanyId(),
								"LPD-34594")) {

							return null;
						}

						return serviceBuilderObjectDefinition.
							getRootObjectDefinitionExternalReferenceCode();
					});
				setScope(serviceBuilderObjectDefinition::getScope);
				setStatus(
					() -> new Status() {
						{
							setCode(serviceBuilderObjectDefinition::getStatus);
							setLabel(
								() -> WorkflowConstants.getStatusLabel(
									serviceBuilderObjectDefinition.
										getStatus()));
							setLabel_i18n(
								() -> LanguageUtil.get(
									LanguageResources.getResourceBundle(locale),
									WorkflowConstants.getStatusLabel(
										serviceBuilderObjectDefinition.
											getStatus())));
						}
					});
				setStorageType(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPS-135430")) {
							return null;
						}

						return serviceBuilderObjectDefinition.getStorageType();
					});
				setSystem(serviceBuilderObjectDefinition::isSystem);
				setTitleObjectFieldName(
					() -> {
						com.liferay.object.model.ObjectField
							serviceBuilderObjectField =
								objectFieldLocalService.fetchObjectField(
									serviceBuilderObjectDefinition.
										getTitleObjectFieldId());

						if (serviceBuilderObjectField == null) {
							return null;
						}

						return serviceBuilderObjectField.getName();
					});
				setWorkflowDefinitionLinks(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
							return null;
						}

						List
							<com.liferay.portal.kernel.model.
								WorkflowDefinitionLink>
									serviceBuilderWorkflowDefinitionLinks =
										workflowDefinitionLinkLocalService.
											getWorkflowDefinitionLinks(
												serviceBuilderObjectDefinition.
													getCompanyId(),
												serviceBuilderObjectDefinition.
													getClassName());

						return TransformUtil.transformToArray(
							serviceBuilderWorkflowDefinitionLinks,
							serviceBuilderWorkflowDefinitionLink ->
								new WorkflowDefinitionLink() {
									{
										setGroupExternalReferenceCode(
											() -> {
												Group group =
													groupLocalService.
														fetchGroup(
															serviceBuilderWorkflowDefinitionLink.
																getGroupId());

												if (group != null) {
													return group.
														getExternalReferenceCode();
												}

												return StringPool.BLANK;
											});
										setWorkflowDefinitionName(
											serviceBuilderWorkflowDefinitionLink::
												getWorkflowDefinitionName);
									}
								},
							WorkflowDefinitionLink.class);
					});
			}
		};
	}

	private static ObjectDefinitionSetting _toObjectDefinitionSetting(
		GroupLocalService groupLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		com.liferay.object.model.ObjectDefinitionSetting
			serviceBuilderObjectDefinitionSetting) {

		if (serviceBuilderObjectDefinitionSetting == null) {
			return null;
		}

		if (StringUtil.equals(
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				serviceBuilderObjectDefinitionSetting.getName())) {

			return _toObjectDefinitionSetting(
				ObjectDefinitionSettingConstants.
					NAME_ACCEPTED_GROUP_EXTERNAL_REFERENCE_CODES,
				groupId -> {
					Group group = groupLocalService.getGroup(
						GetterUtil.getLong(groupId));

					return group.getExternalReferenceCode();
				},
				serviceBuilderObjectDefinitionSetting.getValue());
		}
		else if (StringUtil.equals(
					ObjectDefinitionSettingConstants.
						NAME_ROOT_OBJECT_DEFINITION_IDS,
					serviceBuilderObjectDefinitionSetting.getName())) {

			return _toObjectDefinitionSetting(
				ObjectDefinitionSettingConstants.
					NAME_ROOT_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODES,
				rootObjectDefinitionId -> {
					com.liferay.object.model.ObjectDefinition
						serviceBuilderObjectDefinition =
							objectDefinitionLocalService.getObjectDefinition(
								GetterUtil.getLong(rootObjectDefinitionId));

					return serviceBuilderObjectDefinition.
						getExternalReferenceCode();
				},
				serviceBuilderObjectDefinitionSetting.getValue());
		}

		ObjectDefinitionSetting objectDefinitionSetting =
			new ObjectDefinitionSetting();

		objectDefinitionSetting.setName(
			serviceBuilderObjectDefinitionSetting::getName);
		objectDefinitionSetting.setValue(
			serviceBuilderObjectDefinitionSetting::getValue);

		return objectDefinitionSetting;
	}

	private static ObjectDefinitionSetting _toObjectDefinitionSetting(
		String name, UnsafeFunction<String, String, Exception> unsafeFunction,
		String value) {

		if (Objects.equals(value, StringPool.BLANK)) {
			return null;
		}

		ObjectDefinitionSetting objectDefinitionSetting =
			new ObjectDefinitionSetting();

		objectDefinitionSetting.setName(() -> name);
		objectDefinitionSetting.setValue(
			() -> StringUtil.merge(
				TransformUtil.transform(
					value.split("\\s*,\\s*"), unsafeFunction, String.class)));

		return objectDefinitionSetting;
	}

}