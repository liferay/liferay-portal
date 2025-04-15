/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.handler.LayoutPageTemplateEntryExceptionRequestHandlerUtil;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/add_layout_prototype"
	},
	service = MVCActionCommand.class
)
public class AddLayoutPrototypeMVCActionCommand extends BaseMVCActionCommand {

	protected LayoutPrototype addLayoutPrototype(ActionRequest actionRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");

		Locale defaultLocale = LocaleUtil.getDefault();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			themeDisplay.getSiteDefaultLocale(), name
		).put(
			defaultLocale,
			() -> {
				if (themeDisplay.getSiteDefaultLocale() != defaultLocale) {
					return name;
				}

				return null;
			}
		).build();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			LayoutPrototype.class.getName(), actionRequest);

		LayoutPrototype layoutPrototype =
			_layoutPrototypeService.addLayoutPrototype(
				nameMap, new HashMap<>(), true, serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchFirstLayoutPageTemplateEntry(
					layoutPrototype.getLayoutPrototypeId());

		if (layoutPageTemplateEntry == null) {
			return layoutPrototype;
		}

		layoutPageTemplateEntry.setGroupId(themeDisplay.getScopeGroupId());
		layoutPageTemplateEntry.setLayoutPageTemplateCollectionId(
			ParamUtil.getLong(actionRequest, "layoutPageTemplateCollectionId"));

		_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntry);

		return layoutPrototype;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Callable<LayoutPrototype> addLayoutPrototypeCallable =
			new AddLayoutPrototypeCallable(actionRequest);

		try {
			LayoutPrototype layoutPrototype = TransactionInvokerUtil.invoke(
				_transactionConfig, addLayoutPrototypeCallable);

			Group layoutPrototypeGroup = layoutPrototype.getGroup();

			String redirectURL = layoutPrototypeGroup.getDisplayURL(
				themeDisplay, true);

			String backURL = ParamUtil.getString(actionRequest, "backURL");

			if (Validator.isNotNull(backURL)) {
				redirectURL = HttpComponentsUtil.addParameters(
					redirectURL, "p_l_back_url", backURL, "p_l_back_url_title",
					_language.get(themeDisplay.getLocale(), "page-templates"));
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("redirectURL", redirectURL));
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}

			if (throwable instanceof LayoutNameException) {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"error",
						_language.get(
							themeDisplay.getRequest(),
							"please-enter-a-valid-name")));
			}
			else if (throwable instanceof
						LayoutPageTemplateEntryNameException) {

				LayoutPageTemplateEntryNameException
					layoutPageTemplateEntryNameException =
						(LayoutPageTemplateEntryNameException)throwable;

				LayoutPageTemplateEntryExceptionRequestHandlerUtil.
					handlePortalException(
						actionRequest, actionResponse,
						layoutPageTemplateEntryNameException);
			}
			else {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"error",
						_language.get(
							themeDisplay.getRequest(),
							"an-unexpected-error-occurred")));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddLayoutPrototypeMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

	private class AddLayoutPrototypeCallable
		implements Callable<LayoutPrototype> {

		@Override
		public LayoutPrototype call() throws Exception {
			return addLayoutPrototype(_actionRequest);
		}

		private AddLayoutPrototypeCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}