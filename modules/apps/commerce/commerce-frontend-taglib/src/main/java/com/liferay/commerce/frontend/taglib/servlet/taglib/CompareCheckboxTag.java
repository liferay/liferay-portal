/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.util.CPCompareHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Fabio Diego Mastrorilli
 */
public class CompareCheckboxTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			if ((commerceContext == null) ||
				(commerceContext.getCommerceChannelId() == 0)) {

				return SKIP_BODY;
			}

			long commerceAccountId = CommerceUtil.getCommerceAccountId(
				commerceContext);

			_commerceChannelGroupId =
				commerceContext.getCommerceChannelGroupId();

			List<Long> cpDefinitionIds = _getCPDefinitionIds(
				commerceContext.getCommerceChannelGroupId(), commerceAccountId,
				CookiesManagerUtil.getCookieValue(
					_getCPDefinitionIdsCookieKey(
						commerceContext.getCommerceChannelGroupId()),
					httpServletRequest));

			_inCompare = cpDefinitionIds.contains(
				_cpCatalogEntry.getCPDefinitionId());

			CPDefinition cpDefinition =
				CPDefinitionLocalServiceUtil.getCPDefinition(
					_cpCatalogEntry.getCPDefinitionId());

			_pictureUrl = cpDefinition.getDefaultImageThumbnailSrc(
				commerceAccountId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public String getLabel() {
		return _label;
	}

	public boolean getRefreshOnRemove() {
		return _refreshOnRemove;
	}

	public void setCPCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRefreshOnRemove(boolean refreshOnRemove) {
		_refreshOnRemove = refreshOnRemove;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_commerceChannelGroupId = 0;
		_cpCatalogEntry = null;
		_disabled = false;
		_inCompare = false;
		_label = StringPool.BLANK;
		_pictureUrl = null;
		_refreshOnRemove = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:commerceChannelGroupId",
			_commerceChannelGroupId);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:cpCatalogEntry",
			_cpCatalogEntry);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:disabled", _disabled);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:inCompare", _inCompare);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:label", _label);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:pictureUrl", _pictureUrl);
		httpServletRequest.setAttribute(
			"liferay-commerce:compare-checkbox:refreshOnRemove",
			_refreshOnRemove);
	}

	private List<Long> _getCPDefinitionIds(
			long groupId, long commerceAccountId,
			String cpDefinitionIdsCookieValue)
		throws PortalException {

		CPCompareHelper cpCompareHelper =
			ServletContextUtil.getCPCompareHelper();

		return cpCompareHelper.getCPDefinitionIds(
			groupId, commerceAccountId, cpDefinitionIdsCookieValue);
	}

	private String _getCPDefinitionIdsCookieKey(long commerceChannelGroupId) {
		CPCompareHelper cpCompareHelper =
			ServletContextUtil.getCPCompareHelper();

		return cpCompareHelper.getCPDefinitionIdsCookieKey(
			commerceChannelGroupId);
	}

	private static final String _PAGE = "/compare_checkbox/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		CompareCheckboxTag.class);

	private long _commerceChannelGroupId;
	private CPCatalogEntry _cpCatalogEntry;
	private boolean _disabled;
	private boolean _inCompare;
	private String _label = StringPool.BLANK;
	private String _pictureUrl;
	private boolean _refreshOnRemove;

}