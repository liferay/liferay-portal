/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.journal.web.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalDDMTemplateActionDropdownItemsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalDDMTemplateVerticalCard extends BaseVerticalCard {

	public JournalDDMTemplateVerticalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(baseModel, renderRequest, rowChecker);

		_renderResponse = renderResponse;

		_ddmTemplate = (DDMTemplate)baseModel;
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		JournalDDMTemplateActionDropdownItemsProvider
			ddmTemplateActionDropdownItemsProvider =
				new JournalDDMTemplateActionDropdownItemsProvider(
					_ddmTemplate, renderRequest, _renderResponse);

		try {
			return ddmTemplateActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getHref() {
		try {
			if (!DDMTemplatePermission.contains(
					themeDisplay.getPermissionChecker(), _ddmTemplate,
					ActionKeys.UPDATE)) {

				return StringPool.BLANK;
			}

			return PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_ddm_template.jsp"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setParameter(
				"ddmTemplateId", _ddmTemplate.getTemplateId()
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getImageSrc() {
		return HtmlUtil.escapeAttribute(
			_ddmTemplate.getTemplateImageURL(themeDisplay));
	}

	@Override
	public String getSubtitle() {
		Date createDate = _ddmTemplate.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - createDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "modified-x-ago", modifiedDateDescription);
	}

	@Override
	public String getTitle() {
		return _ddmTemplate.getName(themeDisplay.getLocale());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalDDMTemplateVerticalCard.class);

	private final DDMTemplate _ddmTemplate;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}