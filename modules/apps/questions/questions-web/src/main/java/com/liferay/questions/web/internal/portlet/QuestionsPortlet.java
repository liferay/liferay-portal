/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.portlet;

import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.flags.taglib.servlet.taglib.util.FlagsTagUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.message.boards.moderation.configuration.MBModerationGroupConfiguration;
import com.liferay.message.boards.service.MBStatsUserLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.questions.web.internal.configuration.QuestionsConfiguration;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;
import com.liferay.questions.web.internal.constants.QuestionsWebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	configurationPid = "com.liferay.questions.web.internal.configuration.QuestionsConfiguration",
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-questions",
		"com.liferay.portlet.display-category=category.collaboration",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.single-page-application=false",
		"jakarta.portlet.display-name=Questions",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + QuestionsPortletKeys.QUESTIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class QuestionsPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			QuestionsConfiguration.class.getName(), _questionsConfiguration);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		renderRequest.setAttribute(
			QuestionsWebKeys.COMPANY_NAME, company.getName());

		Properties properties = _portal.getPortalProperties();

		String ranks = properties.getProperty("message.boards.user.ranks");

		if (ranks == null) {
			throw new IllegalArgumentException(
				"No value found for property \"message.boards.user.ranks\"");
		}

		String min = Collections.min(
			StringUtil.split(ranks),
			Comparator.comparing(
				rank -> {
					List<String> rankParts = StringUtil.split(
						rank, CharPool.EQUAL);

					return rankParts.get(1);
				}));

		List<String> minParts = StringUtil.split(min, CharPool.EQUAL);

		String lowestRank = minParts.get(0);

		renderRequest.setAttribute(QuestionsWebKeys.DEFAULT_RANK, lowestRank);

		ItemSelectorCriterion itemSelectorCriterion =
			new ImageItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType(),
			new URLItemSelectorReturnType());

		PortletURL portletURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			"EDITOR_NAME_selectItem", itemSelectorCriterion);

		renderRequest.setAttribute(
			QuestionsWebKeys.IMAGE_BROWSE_URL, portletURL.toString());

		renderRequest.setAttribute(
			QuestionsWebKeys.FLAGS_PROPERTIES,
			HashMapBuilder.<String, Object>put(
				"context",
				HashMapBuilder.<String, Object>put(
					"namespace", _portal.getPortletNamespace(PortletKeys.FLAGS)
				).build()
			).put(
				"props",
				() -> HashMapBuilder.<String, Object>put(
					"captchaURI", FlagsTagUtil.getCaptchaURI(httpServletRequest)
				).put(
					"isFlagEnabled", FlagsTagUtil.isFlagsEnabled(themeDisplay)
				).put(
					"pathTermsOfUse",
					_portal.getPathMain() + "/portal/terms_of_use"
				).put(
					"reasons",
					FlagsTagUtil.getReasons(
						themeDisplay.getCompanyId(), httpServletRequest)
				).put(
					"uri", FlagsTagUtil.getURI(httpServletRequest)
				).put(
					"viewMode",
					Objects.equals(
						Constants.VIEW,
						ParamUtil.getString(
							themeDisplay.getRequest(), "p_l_mode",
							Constants.VIEW))
				).build()
			).build());
		renderRequest.setAttribute(
			QuestionsWebKeys.TAG_SELECTOR_URL,
			_getTagSelectorURL(renderRequest, renderResponse));
		renderRequest.setAttribute(
			QuestionsWebKeys.TRUSTED_USER, _isTrustedUser(renderRequest));

		super.doView(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_questionsConfiguration = ConfigurableUtil.createConfigurable(
			QuestionsConfiguration.class, properties);
	}

	private String _getTagSelectorURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
			new AssetTagsItemSelectorCriterion();

		assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetTagsItemSelectorReturnType());
		assetTagsItemSelectorCriterion.setGroupIds(
			new long[] {
				_portal.getSiteGroupId(themeDisplay.getScopeGroupId())
			});
		assetTagsItemSelectorCriterion.setMultiSelection(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(renderRequest),
				renderResponse.getNamespace() + "selectTag",
				assetTagsItemSelectorCriterion));
	}

	private boolean _isTrustedUser(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			MBModerationGroupConfiguration mbModerationGroupConfiguration =
				_configurationProvider.getGroupConfiguration(
					MBModerationGroupConfiguration.class,
					themeDisplay.getScopeGroupId());

			if (!mbModerationGroupConfiguration.
					enableMessageBoardsModeration()) {

				return true;
			}

			long messageCountByUserId =
				_mbStatsUserLocalService.getMessageCountByUserId(
					themeDisplay.getUserId());

			if (messageCountByUserId <
					mbModerationGroupConfiguration.
						minimumContributedMessages()) {

				return false;
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		QuestionsPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private MBStatsUserLocalService _mbStatsUserLocalService;

	@Reference
	private Portal _portal;

	private volatile QuestionsConfiguration _questionsConfiguration;

}