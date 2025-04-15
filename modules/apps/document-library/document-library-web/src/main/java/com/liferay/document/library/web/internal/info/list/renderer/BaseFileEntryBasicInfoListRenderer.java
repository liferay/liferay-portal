/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.list.renderer;

import com.liferay.document.library.web.internal.info.item.renderer.FileEntryTitleInfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRendererContext;
import com.liferay.info.taglib.list.renderer.BasicInfoListRenderer;
import com.liferay.info.taglib.servlet.taglib.InfoListBasicListTag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
public abstract class BaseFileEntryBasicInfoListRenderer
	implements BasicInfoListRenderer<FileEntry> {

	@Override
	public List<InfoItemRenderer<?>> getAvailableInfoItemRenderers() {
		return infoItemRendererRegistry.getInfoItemRenderers(
			FileEntry.class.getName());
	}

	@Override
	public void render(
		List<FileEntry> fileEntries, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		render(
			fileEntries,
			new DefaultInfoListRendererContext(
				httpServletRequest, httpServletResponse));
	}

	@Override
	public void render(
		List<FileEntry> fileEntries,
		InfoListRendererContext infoListRendererContext) {

		InfoListBasicListTag infoListBasicListTag = new InfoListBasicListTag();

		infoListBasicListTag.setInfoListObjects(fileEntries);

		String listItemRendererKey =
			infoListRendererContext.getListItemRendererKey();

		if (Validator.isNotNull(listItemRendererKey)) {
			infoListBasicListTag.setItemRendererKey(listItemRendererKey);
		}
		else {
			infoListBasicListTag.setItemRendererKey(
				FileEntryTitleInfoItemRenderer.class.getName());
		}

		infoListBasicListTag.setListStyleKey(getListStyle());

		String templateKey = infoListRendererContext.getTemplateKey();

		if (Validator.isNotNull(templateKey)) {
			infoListBasicListTag.setTemplateKey(templateKey);
		}

		try {
			infoListBasicListTag.doTag(
				infoListRendererContext.getHttpServletRequest(),
				infoListRendererContext.getHttpServletResponse());
		}
		catch (Exception exception) {
			_log.error("Unable to render file entries list", exception);
		}
	}

	@Reference
	protected InfoItemRendererRegistry infoItemRendererRegistry;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFileEntryBasicInfoListRenderer.class);

}