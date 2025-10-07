/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.renderer;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.Format;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jorge Ferrer
 * @author Guilherme Camacho
 */
public class ObjectEntryRowInfoItemRenderer
	implements InfoItemRenderer<ObjectEntry> {

	public ObjectEntryRowInfoItemRenderer(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ObjectDefinition objectDefinition,
		ObjectEntryManager objectEntryManager,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		ServletContext servletContext) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_objectDefinition = objectDefinition;
		_objectEntryManager = objectEntryManager;
		_objectFieldLocalService = objectFieldLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_servletContext = servletContext;
	}

	@Override
	public String getItemClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getKey() {
		return StringBundler.concat(
			ObjectEntryRowInfoItemRenderer.class.getName(),
			StringPool.UNDERLINE, _objectDefinition.getCompanyId(),
			StringPool.UNDERLINE, _objectDefinition.getName());
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "row");
	}

	@Override
	public void render(
		ObjectEntry objectEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			httpServletRequest.setAttribute(
				AssetDisplayPageFriendlyURLProvider.class.getName(),
				_assetDisplayPageFriendlyURLProvider);
			httpServletRequest.setAttribute(
				ObjectWebKeys.OBJECT_DEFINITION, _objectDefinition);
			httpServletRequest.setAttribute(
				ObjectWebKeys.OBJECT_ENTRY, objectEntry);
			httpServletRequest.setAttribute(
				ObjectWebKeys.OBJECT_ENTRY_VALUES,
				_getValues(
					objectEntry.getExternalReferenceCode(),
					objectEntry.getGroupId(),
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY)));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/object_entry.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Map<String, Serializable> _getValues(
			String externalReferenceCode, long groupId,
			ThemeDisplay themeDisplay)
		throws Exception {

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry;

		try {
			objectEntry = _objectEntryManager.getObjectEntry(
				themeDisplay.getCompanyId(),
				new DefaultDTOConverterContext(
					false, null, null, null, null, themeDisplay.getLocale(),
					null, themeDisplay.getUser()),
				externalReferenceCode, _objectDefinition,
				ObjectEntryInfoItemUtil.getScopeKey(
					groupId, _objectDefinition, _objectScopeProviderRegistry));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return Collections.emptyMap();
		}

		Map<String, Serializable> values = new TreeMap<>();

		for (ObjectField objectField :
				_objectFieldLocalService.getActiveObjectFields(
					_objectFieldLocalService.getObjectFields(
						_objectDefinition.getObjectDefinitionId(), false))) {

			Object value = ObjectEntryUtil.getValue(
				themeDisplay.getLocale(), objectField,
				objectEntry.getProperties());

			if (value == null) {
				values.put(objectField.getName(), StringPool.BLANK);

				continue;
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				FileEntry fileEntry = (FileEntry)value;

				values.put(objectField.getName(), fileEntry.getLink());
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

				Format format = FastDateFormatFactoryUtil.getDate(
					DateFormat.DEFAULT, themeDisplay.getLocale(),
					themeDisplay.getTimeZone());

				values.put(objectField.getName(), format.format(value));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

				Format format = FastDateFormatFactoryUtil.getDateTime(
					DateFormat.DEFAULT, DateFormat.DEFAULT,
					themeDisplay.getLocale(), themeDisplay.getTimeZone());

				ZonedDateTime zonedDateTime = ZonedDateTime.of(
					(LocalDateTime)value, ZoneId.systemDefault());

				values.put(
					objectField.getName(),
					format.format(Date.from(zonedDateTime.toInstant())));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.
							BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				values.put(
					objectField.getName(),
					StringUtil.merge(
						ListUtil.toList(
							(List<ListTypeEntry>)value,
							listTypeEntry -> listTypeEntry.getName(
								themeDisplay.getLocale())),
						StringPool.COMMA_AND_SPACE));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				ListTypeEntry listTypeEntry = (ListTypeEntry)value;

				values.put(
					objectField.getName(),
					listTypeEntry.getName(themeDisplay.getLocale()));
			}
			else {
				values.put(objectField.getName(), (Serializable)value);
			}
		}

		return values;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryRowInfoItemRenderer.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final ServletContext _servletContext;

}