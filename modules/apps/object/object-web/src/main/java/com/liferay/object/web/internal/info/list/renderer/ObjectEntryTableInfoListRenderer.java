/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.list.renderer;

import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererContext;
import com.liferay.info.taglib.servlet.taglib.InfoListBasicTableTag;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.web.internal.info.item.renderer.ObjectEntryRowInfoItemRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryTableInfoListRenderer
	implements InfoListRenderer<ObjectEntry> {

	public ObjectEntryTableInfoListRenderer(
		InfoItemRendererRegistry infoItemRendererRegistry,
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		_infoItemRendererRegistry = infoItemRendererRegistry;
		_objectDefinition = objectDefinition;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	public List<InfoItemRenderer<?>> getAvailableInfoItemRenderers() {
		return _infoItemRendererRegistry.getInfoItemRenderers(
			ObjectEntry.class.getName());
	}

	@Override
	public String getCollectionItemClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getKey() {
		return _getCompanyScopedKey(
			ObjectEntryTableInfoListRenderer.class.getName());
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "table");
	}

	@Override
	public void render(
		List<ObjectEntry> objectEntries, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		render(
			objectEntries,
			new DefaultInfoListRendererContext(
				httpServletRequest, httpServletResponse));
	}

	@Override
	public void render(
		List<ObjectEntry> objectEntries,
		InfoListRendererContext infoListRendererContext) {

		InfoListBasicTableTag infoListBasicTableTag =
			getInfoListBasicTableTag();

		if ((objectEntries != null) && !objectEntries.isEmpty()) {
			List<ObjectField> objectFields =
				_objectFieldLocalService.getObjectFields(
					_objectDefinition.getObjectDefinitionId(), false);

			try {
				objectFields = _objectFieldLocalService.getActiveObjectFields(
					objectFields);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}

			infoListBasicTableTag.setInfoListObjectColumnNames(
				ListUtil.toList(
					objectFields,
					objectField -> objectField.getLabel(
						PortalUtil.getLocale(
							infoListRendererContext.getHttpServletRequest()))));
		}

		infoListBasicTableTag.setInfoListObjects(objectEntries);

		String listItemRendererKey =
			infoListRendererContext.getListItemRendererKey();

		if (Validator.isNotNull(listItemRendererKey)) {
			infoListBasicTableTag.setItemRendererKey(listItemRendererKey);
		}
		else {
			infoListBasicTableTag.setItemRendererKey(
				_getCompanyScopedKey(
					ObjectEntryRowInfoItemRenderer.class.getName()));
		}

		try {
			infoListBasicTableTag.doTag(
				infoListRendererContext.getHttpServletRequest(),
				infoListRendererContext.getHttpServletResponse());
		}
		catch (Exception exception) {
			_log.error("Unable to render object entries list", exception);
		}
	}

	protected InfoListBasicTableTag getInfoListBasicTableTag() {
		return new InfoListBasicTableTag();
	}

	private String _getCompanyScopedKey(String className) {
		return StringBundler.concat(
			className, StringPool.UNDERLINE, _objectDefinition.getCompanyId(),
			StringPool.UNDERLINE, _objectDefinition.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryTableInfoListRenderer.class);

	private final InfoItemRendererRegistry _infoItemRendererRegistry;
	private final ObjectDefinition _objectDefinition;
	private final ObjectFieldLocalService _objectFieldLocalService;

}