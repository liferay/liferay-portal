/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.configuration.JournalGroupServiceConfiguration;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.display.context.helper.JournalWebRequestHelper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.journal.web.internal.configuration.JournalWebConfiguration",
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = ConfigurationAction.class
)
public class JournalConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);
		httpServletRequest.setAttribute(
			JournalWebConfiguration.class.getName(), _journalWebConfiguration);

		return "/configuration_browse.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			JournalWebConfiguration.class.getName(), _journalWebConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void postProcess(
			long companyId, PortletRequest portletRequest, Settings settings)
		throws PortalException {

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		JournalWebRequestHelper journalWebRequestHelper =
			new JournalWebRequestHelper(
				_portal.getHttpServletRequest(portletRequest));

		JournalGroupServiceConfiguration journalGroupServiceConfiguration =
			journalWebRequestHelper.getJournalGroupServiceConfiguration();

		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleAddedBody",
			journalGroupServiceConfiguration.emailArticleAddedBody());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleAddedSubject",
			journalGroupServiceConfiguration.emailArticleAddedSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalDeniedBody",
			journalGroupServiceConfiguration.emailArticleApprovalDeniedBody());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalDeniedSubject",
			journalGroupServiceConfiguration.
				emailArticleApprovalDeniedSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalGrantedBody",
			journalGroupServiceConfiguration.emailArticleApprovalGrantedBody());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalGrantedSubject",
			journalGroupServiceConfiguration.
				emailArticleApprovalGrantedSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalRequestedBody",
			journalGroupServiceConfiguration.
				emailArticleApprovalRequestedBody());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleApprovalRequestedSubject",
			journalGroupServiceConfiguration.
				emailArticleApprovalRequestedSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleMovedFromFolderBody",
			journalGroupServiceConfiguration.emailArticleMovedFromFolderBody());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleMovedFromFolderSubject",
			journalGroupServiceConfiguration.
				emailArticleMovedFromFolderSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleMovedToFolderBody",
			journalGroupServiceConfiguration.emailArticleMovedToFolderBody());
		removeDefaultValue(
			portletRequest, modifiableSettings,
			"emailArticleMovedToFolderSubject",
			journalGroupServiceConfiguration.
				emailArticleMovedToFolderSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleReviewBody",
			journalGroupServiceConfiguration.emailArticleReviewBody());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleReviewSubject",
			journalGroupServiceConfiguration.emailArticleReviewSubject());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleUpdatedBody",
			journalGroupServiceConfiguration.emailArticleUpdatedBody());
		removeDefaultValue(
			portletRequest, modifiableSettings, "emailArticleUpdatedSubject",
			journalGroupServiceConfiguration.emailArticleUpdatedSubject());
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateEmail(actionRequest, "emailArticleAdded");
		validateEmail(actionRequest, "emailArticleApprovalDenied");
		validateEmail(actionRequest, "emailArticleApprovalGranted");
		validateEmail(actionRequest, "emailArticleApprovalRequested");
		validateEmail(actionRequest, "emailArticleExpired");
		validateEmail(actionRequest, "emailArticleMovedFromFolder");
		validateEmail(actionRequest, "emailArticleMovedToFolder");
		validateEmail(actionRequest, "emailArticleReview");
		validateEmail(actionRequest, "emailArticleUpdated");

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalWebConfiguration = ConfigurableUtil.createConfigurable(
			JournalWebConfiguration.class, properties);
	}

	@Reference
	private ItemSelector _itemSelector;

	private volatile JournalWebConfiguration _journalWebConfiguration;

	@Reference
	private Portal _portal;

}