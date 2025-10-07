/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.FragmentLink;
import com.liferay.headless.admin.site.dto.v1_0.FragmentLinkInlineValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentLinkMappedValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentLinkValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentMappedValueItemReference;
import com.liferay.headless.admin.site.dto.v1_0.Mapping;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class FragmentLinkUtil {

	public static FragmentLink toFragmentLink(
		InfoItemServiceRegistry infoItemServiceRegistry, JSONObject jsonObject,
		long scopeGroupId) {

		if (jsonObject == null) {
			return null;
		}

		boolean mappedValue = _isMappedValue(jsonObject);

		if (jsonObject.isNull("href") && !mappedValue) {
			return null;
		}

		return new FragmentLink() {
			{
				setTarget(
					() -> {
						String target = jsonObject.getString("target");

						if (Validator.isNull(target)) {
							return null;
						}

						if (StringUtil.equalsIgnoreCase(target, "_parent") ||
							StringUtil.equalsIgnoreCase(target, "_top")) {

							target = "_self";
						}

						return Target.create(
							TargetUtil.toExternalValue(target));
					});
				setValue(
					() -> _toFragmentLinkValue(
						infoItemServiceRegistry, jsonObject, mappedValue,
						scopeGroupId));
			}
		};
	}

	public static JSONObject toJSONObject(
		FragmentLink fragmentLink,
		InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId) {

		if ((fragmentLink == null) || (fragmentLink.getValue() == null)) {
			return null;
		}

		FragmentLinkValue fragmentLinkValue = fragmentLink.getValue();

		if (fragmentLinkValue == null) {
			return null;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentLinkValue instanceof FragmentLinkInlineValue) {
			FragmentLinkInlineValue fragmentLinkInlineValue =
				(FragmentLinkInlineValue)fragmentLinkValue;

			jsonObject.put(
				"href",
				LocalizedValueUtil.toJSONObject(
					fragmentLinkInlineValue.getValue_i18n()));
		}
		else {
			jsonObject = _getFragmentMappedValueJSONObject(
				(FragmentLinkMappedValue)fragmentLinkValue,
				infoItemServiceRegistry, scopeGroupId);

			if (jsonObject == null) {
				return null;
			}
		}

		FragmentLink.Target target = fragmentLink.getTarget();

		if (target != null) {
			jsonObject.put(
				"target", TargetUtil.toInternalValue(target.getValue()));
		}

		return JSONUtil.put("link", jsonObject);
	}

	private static ClassPKInfoItemIdentifier _getClassPKInfoItemIdentifier(
		String className,
		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference,
		InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId) {

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		InfoItemDetailsProvider<Object> infoItemDetailsProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if ((infoItemObjectProvider == null) ||
			(infoItemDetailsProvider == null)) {

			return null;
		}

		try {
			Object infoItem = infoItemObjectProvider.getInfoItem(
				scopeGroupId,
				new ERCInfoItemIdentifier(
					fragmentMappedValueItemExternalReference.
						getExternalReferenceCode(),
					ScopeUtil.getScopeExternalReferenceCode(
						fragmentMappedValueItemExternalReference.getScope(),
						scopeGroupId)));

			InfoItemDetails infoItemDetails =
				infoItemDetailsProvider.getInfoItemDetails(
					scopeGroupId, ClassPKInfoItemIdentifier.class, infoItem);

			if (infoItemDetails == null) {
				return null;
			}

			InfoItemReference infoItemReference =
				infoItemDetails.getInfoItemReference();

			if (infoItemReference == null) {
				return null;
			}

			return (ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			throw new UnsupportedOperationException();
		}
	}

	private static Long _getCompanyId(long scopeGroupId) {
		Group group = GroupLocalServiceUtil.fetchGroup(scopeGroupId);

		if (group != null) {
			return group.getCompanyId();
		}

		Long companyId = CompanyThreadLocal.getCompanyId();

		if (companyId != null) {
			return companyId;
		}

		return null;
	}

	private static String _getFieldKey(JSONObject jsonObject) {
		String fieldId = jsonObject.getString("fieldId");

		if (Validator.isNotNull(fieldId)) {
			return fieldId;
		}

		String mappedField = jsonObject.getString("mappedField");

		if (Validator.isNotNull(mappedField)) {
			return mappedField;
		}

		return null;
	}

	private static FragmentMappedValueItemExternalReference
		_getFragmentMappedValueItemExternalReference(
			InfoItemServiceRegistry infoItemServiceRegistry,
			JSONObject jsonObject, long scopeGroupId) {

		String fieldId = jsonObject.getString("fieldId");
		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (Validator.isNull(fieldId) && (layoutJSONObject == null)) {
			return null;
		}

		if (layoutJSONObject != null) {
			return _toLayoutFragmentMappedValueItemExternalReference(
				layoutJSONObject, scopeGroupId);
		}

		String className = _toItemClassName(jsonObject);
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		if ((className == null) || (externalReferenceCode == null)) {
			return null;
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(() -> className);

		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		if (infoItemObjectProvider != null) {
			try {
				GroupedModel groupedModel =
					(GroupedModel)infoItemObjectProvider.getInfoItem(
						new ERCInfoItemIdentifier(
							externalReferenceCode,
							GroupUtil.getExternalReferenceCode(
								jsonObject.getString(
									"scopeExternalReferenceCode"),
								scopeGroupId)));

				fragmentMappedValueItemExternalReference.setScope(
					() -> ScopeUtil.getScope(
						groupedModel.getGroupId(), scopeGroupId));
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Item external reference could not be set since no " +
							"item could be obtained",
						portalException);
				}

				throw new UnsupportedOperationException();
			}
		}

		return fragmentMappedValueItemExternalReference;
	}

	private static FragmentMappedValueItemReference
		_getFragmentMappedValueItemReference(
			InfoItemServiceRegistry infoItemServiceRegistry,
			JSONObject jsonObject, long scopeGroupId) {

		if (!jsonObject.has("mappedField")) {
			return _getFragmentMappedValueItemExternalReference(
				infoItemServiceRegistry, jsonObject, scopeGroupId);
		}

		FragmentMappedValueItemContextReference
			fragmentMappedValueItemContextReference =
				new FragmentMappedValueItemContextReference();

		fragmentMappedValueItemContextReference.setContextSource(
			() ->
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM);

		return fragmentMappedValueItemContextReference;
	}

	private static JSONObject _getFragmentMappedValueJSONObject(
		FragmentLinkMappedValue fragmentLinkMappedValue,
		InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId) {

		Mapping mapping = fragmentLinkMappedValue.getMapping();

		if (mapping == null) {
			return null;
		}

		FragmentMappedValueItemReference fragmentMappedValueItemReference =
			mapping.getItemReference();

		if ((fragmentMappedValueItemReference == null) ||
			(fragmentMappedValueItemReference.getType() == null)) {

			return null;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (Objects.equals(
				fragmentMappedValueItemReference.getType(),
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE)) {

			String fieldKey = mapping.getFieldKey();

			if (Validator.isNotNull(fieldKey)) {
				jsonObject.put("mappedField", fieldKey);
			}

			return jsonObject;
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				(FragmentMappedValueItemExternalReference)
					fragmentMappedValueItemReference;

		String className =
			fragmentMappedValueItemExternalReference.getClassName();

		String externalReferenceCode =
			fragmentMappedValueItemExternalReference.getExternalReferenceCode();

		if ((className == null) || (externalReferenceCode == null)) {
			return null;
		}

		String fieldKey = mapping.getFieldKey();

		if (Validator.isNotNull(fieldKey)) {
			jsonObject.put("fieldId", fieldKey);
		}

		if (Objects.equals(className, Layout.class.getName())) {
			jsonObject.put(
				"layout",
				_getMappedLayoutJSONObject(
					fragmentMappedValueItemExternalReference, scopeGroupId));
		}
		else {
			_setMappedItemJSONObject(
				fragmentMappedValueItemExternalReference,
				infoItemServiceRegistry, jsonObject, scopeGroupId);
		}

		return jsonObject;
	}

	private static Long _getGroupId(Scope scope, long scopeGroupId) {
		if ((scope == null) || (scope.getExternalReferenceCode() == null)) {
			return scopeGroupId;
		}

		Long companyId = _getCompanyId(scopeGroupId);

		if (companyId == null) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			scope.getExternalReferenceCode(), companyId);

		if (group == null) {
			return null;
		}

		return group.getGroupId();
	}

	private static JSONObject _getMappedLayoutJSONObject(
		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference,
		long scopeGroupId) {

		Long groupId = _getGroupId(
			fragmentMappedValueItemExternalReference.getScope(), scopeGroupId);

		if (groupId == null) {
			return null;
		}

		Layout layout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode(),
				groupId);

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		return JSONUtil.put(
			"groupId", String.valueOf(layout.getGroupId())
		).put(
			"id", layout.getUuid()
		).put(
			"layoutId", String.valueOf(layout.getLayoutId())
		).put(
			"layoutUuid", layout.getUuid()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
		).put(
			"value", layout.getFriendlyURL()
		);
	}

	private static boolean _isMappedValue(JSONObject jsonObject) {
		if (jsonObject == null) {
			return false;
		}

		if (jsonObject.has("classNameId") &&
			jsonObject.has("externalReferenceCode") &&
			jsonObject.has("fieldId")) {

			return true;
		}

		if (jsonObject.has("layout") || jsonObject.has("mappedField")) {
			return true;
		}

		return false;
	}

	private static void _setMappedItemJSONObject(
		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference,
		InfoItemServiceRegistry infoItemServiceRegistry, JSONObject jsonObject,
		long scopeGroupId) {

		String className =
			fragmentMappedValueItemExternalReference.getClassName();
		String classNameId = null;

		try {
			classNameId = String.valueOf(PortalUtil.getClassNameId(className));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process mapping because class name ID could " +
						"not be obtained for class name " + className,
					exception);
			}
		}

		if (classNameId == null) {
			return;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			_getClassPKInfoItemIdentifier(
				className, fragmentMappedValueItemExternalReference,
				infoItemServiceRegistry, scopeGroupId);

		jsonObject.put(
			"className", className
		).put(
			"classNameId", classNameId
		).put(
			"classPK",
			() -> {
				if (classPKInfoItemIdentifier == null) {
					return null;
				}

				return classPKInfoItemIdentifier.getClassPK();
			}
		).put(
			"externalReferenceCode",
			fragmentMappedValueItemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode",
			() -> ScopeUtil.getScopeExternalReferenceCode(
				fragmentMappedValueItemExternalReference.getScope(),
				scopeGroupId)
		);
	}

	private static FragmentLinkMappedValue _toFragmentLinkMappedValue(
		InfoItemServiceRegistry infoItemServiceRegistry, JSONObject jsonObject,
		long scopeGroupId) {

		FragmentLinkMappedValue fragmentLinkMappedValue =
			new FragmentLinkMappedValue();

		fragmentLinkMappedValue.setMapping(
			() -> new Mapping() {
				{
					setFieldKey(() -> _getFieldKey(jsonObject));
					setItemReference(
						() -> _getFragmentMappedValueItemReference(
							infoItemServiceRegistry, jsonObject, scopeGroupId));
				}
			});

		return fragmentLinkMappedValue;
	}

	private static FragmentLinkValue _toFragmentLinkValue(
		InfoItemServiceRegistry infoItemServiceRegistry, JSONObject jsonObject,
		boolean mappedValue, long scopeGroupId) {

		if (mappedValue) {
			return _toFragmentLinkMappedValue(
				infoItemServiceRegistry, jsonObject, scopeGroupId);
		}

		FragmentLinkInlineValue fragmentLinkInlineValue =
			new FragmentLinkInlineValue();

		fragmentLinkInlineValue.setValue_i18n(
			() -> LocalizedValueUtil.toLocalizedValues(
				jsonObject.getJSONObject("href")));

		return fragmentLinkInlineValue;
	}

	private static String _toItemClassName(JSONObject jsonObject) {
		String classNameIdString = jsonObject.getString("classNameId");

		if (Validator.isNull(classNameIdString)) {
			return null;
		}

		long classNameId = 0;

		try {
			classNameId = Long.parseLong(classNameIdString);
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					String.format(
						"Item class name could not be set since class name " +
							"ID %s could not be parsed to a long",
						classNameIdString),
					numberFormatException);
			}

			return null;
		}

		String className = null;

		try {
			className = PortalUtil.getClassName(classNameId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Item class name could not be set since no class name " +
						"could be obtained for class name ID " + classNameId,
					exception);
			}

			return null;
		}

		return className;
	}

	private static FragmentMappedValueItemExternalReference
		_toLayoutFragmentMappedValueItemExternalReference(
			JSONObject layoutJSONObject, long scopeGroupId) {

		Layout layout;

		try {
			layout = LayoutLocalServiceUtil.getLayout(
				layoutJSONObject.getLong("groupId"),
				layoutJSONObject.getBoolean("privateLayout"),
				layoutJSONObject.getLong("layoutId"));
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Item reference could not be set since no layout could " +
						"be obtained",
					portalException);
			}

			throw new UnsupportedOperationException();
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(
			Layout.class::getName);
		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			layout::getExternalReferenceCode);
		fragmentMappedValueItemExternalReference.setScope(
			() -> ScopeUtil.getScope(layout.getGroupId(), scopeGroupId));

		return fragmentMappedValueItemExternalReference;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentLinkUtil.class);

}