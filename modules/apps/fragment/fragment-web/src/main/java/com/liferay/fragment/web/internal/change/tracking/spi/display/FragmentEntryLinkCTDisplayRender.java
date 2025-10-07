/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.fragment.helper.FragmentEntryLinkHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class FragmentEntryLinkCTDisplayRender
	extends BaseCTDisplayRenderer<FragmentEntryLink> {

	@Override
	public Class<FragmentEntryLink> getModelClass() {
		return FragmentEntryLink.class;
	}

	@Override
	public String getTitle(Locale locale, FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(
			fragmentEntryLink.getPlid());
		String name = _fragmentEntryLinkHelper.getFragmentEntryName(
			fragmentEntryLink, locale);

		if ((layout == null) || name.equals(StringPool.BLANK)) {
			return null;
		}

		return _language.format(
			locale, "x-for-x", new String[] {name, layout.getName(locale)});
	}

	@Override
	public boolean isHideable(FragmentEntryLink fragmentEntryLink) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fragmentEntryLink.getCtCollectionId())) {

			Layout layout = _layoutLocalService.fetchLayout(
				fragmentEntryLink.getClassPK());

			if ((layout != null) &&
				_layoutCTDisplayRenderer.isHideable(layout)) {

				return true;
			}
		}

		if (fragmentEntryLink.getOriginalFragmentEntryLinkId() == 0) {
			return false;
		}

		return true;
	}

	@Override
	public String renderPreview(
			DisplayContext<FragmentEntryLink> displayContext)
		throws Exception {

		FragmentEntryLink fragmentEntryLink = displayContext.getModel();

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(displayContext.getLocale());

		return _fragmentRendererController.render(
			defaultFragmentRendererContext,
			displayContext.getHttpServletRequest(),
			displayContext.getHttpServletResponse());
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<FragmentEntryLink> displayBuilder) {

		FragmentEntryLink fragmentEntryLink = displayBuilder.getModel();

		displayBuilder.display(
			"name",
			_fragmentEntryLinkHelper.getFragmentEntryName(
				fragmentEntryLink, displayBuilder.getLocale())
		).display(
			"create-date", fragmentEntryLink.getCreateDate()
		).display(
			"modified-date", fragmentEntryLink.getModifiedDate()
		).display(
			"css", fragmentEntryLink.getCss(), true, true
		).display(
			"editable-values",
			() -> {
				JSONObject jsonObject =
					fragmentEntryLink.getEditableValuesJSONObject();

				return jsonObject.toString(4);
			},
			true, true
		);
	}

	@Reference
	private FragmentEntryLinkHelper _fragmentEntryLinkHelper;

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private Language _language;

	@Reference(
		target = "(component.name=com.liferay.layout.admin.web.internal.change.tracking.spi.display.LayoutCTDisplayRenderer)"
	)
	private CTDisplayRenderer<Layout> _layoutCTDisplayRenderer;

	@Reference
	private LayoutLocalService _layoutLocalService;

}