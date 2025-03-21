/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.fragment.helper.FragmentEntryLinkHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
	service = AssetRendererFactory.class
)
public class FragmentEntryLinkAssetRendererFactory
	extends BaseAssetRendererFactory<FragmentEntryLink> {

	public static final String TYPE = "fragmentEntryLink";

	public FragmentEntryLinkAssetRendererFactory() {
		setCategorizable(false);
		setClassName(FragmentEntryLink.class.getName());
		setLinkable(false);
		setPortletId(ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);
		setSearchable(false);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<FragmentEntryLink> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		return new FragmentEntryLinkAssetRenderer(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(classPK));
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "fragment");
	}

	public class FragmentEntryLinkAssetRenderer
		extends BaseAssetRenderer<FragmentEntryLink> {

		public FragmentEntryLinkAssetRenderer(
			FragmentEntryLink fragmentEntryLink) {

			_fragmentEntryLink = fragmentEntryLink;
		}

		@Override
		public FragmentEntryLink getAssetObject() {
			return _fragmentEntryLink;
		}

		@Override
		public String getClassName() {
			return FragmentEntryLink.class.getName();
		}

		@Override
		public long getClassPK() {
			return _fragmentEntryLink.getFragmentEntryLinkId();
		}

		@Override
		public long getGroupId() {
			return _fragmentEntryLink.getGroupId();
		}

		@Override
		public String getSummary(
			PortletRequest portletRequest, PortletResponse portletResponse) {

			return null;
		}

		@Override
		public String getTitle(Locale locale) {
			String fragmentEntryLinkContextTitle =
				_getFragmentEntryLinkContextTitle(_fragmentEntryLink, locale);
			String fragmentEntryName =
				_fragmentEntryLinkHelper.getFragmentEntryName(
					_fragmentEntryLink, locale);

			if (Validator.isNull(fragmentEntryLinkContextTitle)) {
				return fragmentEntryName;
			}

			return _language.format(
				locale, "x-in-x",
				new String[] {
					fragmentEntryName, fragmentEntryLinkContextTitle
				});
		}

		@Override
		public long getUserId() {
			return _fragmentEntryLink.getUserId();
		}

		@Override
		public String getUserName() {
			return _fragmentEntryLink.getUserName();
		}

		@Override
		public String getUuid() {
			return _fragmentEntryLink.getUuid();
		}

		@Override
		public boolean include(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse, String template)
			throws Exception {

			return false;
		}

		private final FragmentEntryLink _fragmentEntryLink;

	}

	private String _getFragmentEntryLinkContextTitle(
		FragmentEntryLink fragmentEntryLink, Locale locale) {

		Layout layout = _layoutLocalService.fetchLayout(
			fragmentEntryLink.getPlid());

		if (layout == null) {
			return StringPool.BLANK;
		}

		return _language.format(
			locale, "the-x-x",
			new String[] {
				layout.getHTMLTitle(locale),
				StringUtil.toLowerCase(
					ResourceActionsUtil.getModelResource(
						locale, Layout.class.getName()))
			},
			false);
	}

	@Reference
	private FragmentEntryLinkHelper _fragmentEntryLinkHelper;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

}