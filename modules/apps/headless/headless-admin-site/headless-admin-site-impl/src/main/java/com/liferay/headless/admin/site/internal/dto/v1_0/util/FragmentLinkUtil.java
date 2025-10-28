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
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
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
		long companyId, InfoItemServiceRegistry infoItemServiceRegistry,
		JSONObject jsonObject, long scopeGroupId) {

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
						companyId, infoItemServiceRegistry, jsonObject,
						mappedValue, scopeGroupId));
			}
		};
	}

	public static JSONObject toJSONObject(
			long companyId, FragmentLink fragmentLink,
			InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId)
		throws PortalException {

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
				companyId, (FragmentLinkMappedValue)fragmentLinkValue,
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
				long companyId, InfoItemServiceRegistry infoItemServiceRegistry,
				JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		String fieldId = jsonObject.getString("fieldId");
		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (Validator.isNull(fieldId) && (layoutJSONObject == null)) {
			return null;
		}

		if (layoutJSONObject != null) {
			return _toLayoutFragmentMappedValueItemExternalReference(
				companyId, layoutJSONObject, scopeGroupId);
		}

		String className = _toItemClassName(jsonObject);

		if (className == null) {
			return null;
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(() -> className);

		if (jsonObject.has("classPK")) {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				InfoItemUtil.getERCInfoItemIdentifier(
					className, jsonObject.getLong("classPK"),
					infoItemServiceRegistry, scopeGroupId);

			if (ercInfoItemIdentifier != null) {
				fragmentMappedValueItemExternalReference.
					setExternalReferenceCode(
						ercInfoItemIdentifier::getExternalReferenceCode);
				fragmentMappedValueItemExternalReference.setScope(
					() -> ItemScopeUtil.getItemScope(
						companyId,
						ercInfoItemIdentifier.getScopeExternalReferenceCode(),
						scopeGroupId));

				return fragmentMappedValueItemExternalReference;
			}
		}

		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		fragmentMappedValueItemExternalReference.setScope(
			() -> ItemScopeUtil.getItemScope(
				companyId, jsonObject.getString("scopeExternalReferenceCode"),
				scopeGroupId));

		return fragmentMappedValueItemExternalReference;
	}

	private static FragmentMappedValueItemReference
			_getFragmentMappedValueItemReference(
				long companyId, InfoItemServiceRegistry infoItemServiceRegistry,
				JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		if (!jsonObject.has("mappedField")) {
			return _getFragmentMappedValueItemExternalReference(
				companyId, infoItemServiceRegistry, jsonObject, scopeGroupId);
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
			long companyId, FragmentLinkMappedValue fragmentLinkMappedValue,
			InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId)
		throws PortalException {

		Mapping mapping = fragmentLinkMappedValue.getMapping();

		if (mapping == null) {
			return null;
		}

		FragmentMappedValueItemReference fragmentMappedValueItemReference =
			mapping.getItemReference();

		if (fragmentMappedValueItemReference == null) {
			return null;
		}

		String fieldKey = mapping.getFieldKey();

		if (fragmentMappedValueItemReference instanceof
				FragmentMappedValueItemContextReference) {

			if (Validator.isNotNull(fieldKey)) {
				return JSONUtil.put("mappedField", fieldKey);
			}

			return null;
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				(FragmentMappedValueItemExternalReference)
					fragmentMappedValueItemReference;

		String className =
			fragmentMappedValueItemExternalReference.getClassName();

		if (Validator.isNull(className) ||
			Validator.isNull(
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode())) {

			return null;
		}

		if (Objects.equals(className, Layout.class.getName())) {
			return JSONUtil.put(
				"layout",
				_getMappedLayoutJSONObject(
					companyId, fragmentMappedValueItemExternalReference,
					scopeGroupId));
		}

		return InfoItemUtil.getMappedItemJSONObject(
			fragmentMappedValueItemExternalReference.getClassName(),
			fragmentMappedValueItemExternalReference.getExternalReferenceCode(),
			fieldKey, infoItemServiceRegistry,
			fragmentMappedValueItemExternalReference.getScope(), scopeGroupId);
	}

	private static String _getLayoutExternalReferenceCode(
		Layout layout, JSONObject layoutJSONObject) {

		if (layout != null) {
			return layout.getExternalReferenceCode();
		}

		return layoutJSONObject.getString("externalReferenceCode");
	}

	private static Scope _getLayoutScope(
			long companyId, Layout layout, JSONObject layoutJSONObject,
			long scopeGroupId)
		throws Exception {

		if (layout != null) {
			return ItemScopeUtil.getItemScope(
				layout.getGroupId(), scopeGroupId);
		}

		return ItemScopeUtil.getItemScope(
			companyId, layoutJSONObject.getString("scopeExternalReferenceCode"),
			scopeGroupId);
	}

	private static JSONObject _getMappedLayoutJSONObject(
			long companyId,
			FragmentMappedValueItemExternalReference
				fragmentMappedValueItemExternalReference,
			long scopeGroupId)
		throws PortalException {

		String scopeExternalReferenceCode =
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				fragmentMappedValueItemExternalReference.getScope(),
				scopeGroupId);

		Long groupId = ItemScopeUtil.getGroupId(
			companyId, fragmentMappedValueItemExternalReference.getScope(),
			scopeGroupId);

		JSONObject jsonObject = JSONUtil.put(
			"externalReferenceCode",
			fragmentMappedValueItemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		);

		if (groupId == null) {
			LogUtil.logOptionalReference(
				fragmentMappedValueItemExternalReference.getClassName(),
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode(),
				fragmentMappedValueItemExternalReference.getScope(),
				scopeGroupId);

			return jsonObject;
		}

		Layout layout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode(),
				groupId);

		if (layout == null) {
			LogUtil.logOptionalReference(
				fragmentMappedValueItemExternalReference.getClassName(),
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode(),
				fragmentMappedValueItemExternalReference.getScope(),
				scopeGroupId);

			return jsonObject;
		}

		return JSONUtil.put(
			"externalReferenceCode",
			fragmentMappedValueItemExternalReference.getExternalReferenceCode()
		).put(
			"groupId", String.valueOf(layout.getGroupId())
		).put(
			"layoutId", String.valueOf(layout.getLayoutId())
		).put(
			"layoutUuid", layout.getUuid()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"scopeExternalReferenceCode", scopeExternalReferenceCode
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
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

	private static FragmentLinkMappedValue _toFragmentLinkMappedValue(
			long companyId, InfoItemServiceRegistry infoItemServiceRegistry,
			JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		FragmentMappedValueItemReference fragmentMappedValueItemReference =
			_getFragmentMappedValueItemReference(
				companyId, infoItemServiceRegistry, jsonObject, scopeGroupId);

		if (fragmentMappedValueItemReference == null) {
			return null;
		}

		FragmentLinkMappedValue fragmentLinkMappedValue =
			new FragmentLinkMappedValue();

		fragmentLinkMappedValue.setMapping(
			() -> new Mapping() {
				{
					setFieldKey(() -> _getFieldKey(jsonObject));
					setItemReference(() -> fragmentMappedValueItemReference);
				}
			});

		return fragmentLinkMappedValue;
	}

	private static FragmentLinkValue _toFragmentLinkValue(
			long companyId, InfoItemServiceRegistry infoItemServiceRegistry,
			JSONObject jsonObject, boolean mappedValue, long scopeGroupId)
		throws Exception {

		if (mappedValue) {
			return _toFragmentLinkMappedValue(
				companyId, infoItemServiceRegistry, jsonObject, scopeGroupId);
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
				long companyId, JSONObject layoutJSONObject, long scopeGroupId)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			layoutJSONObject.getLong("groupId"),
			layoutJSONObject.getBoolean("privateLayout"),
			layoutJSONObject.getLong("layoutId"));

		String externalReferenceCode = _getLayoutExternalReferenceCode(
			layout, layoutJSONObject);

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(
			Layout.class::getName);
		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		fragmentMappedValueItemExternalReference.setScope(
			() -> _getLayoutScope(
				companyId, layout, layoutJSONObject, scopeGroupId));

		return fragmentMappedValueItemExternalReference;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentLinkUtil.class);

}