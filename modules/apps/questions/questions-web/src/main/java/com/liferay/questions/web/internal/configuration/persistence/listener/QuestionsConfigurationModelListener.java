/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.configuration.persistence.listener;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryService;
import com.liferay.questions.web.internal.asset.model.MBCategoryAssetRendererFactory;
import com.liferay.questions.web.internal.asset.model.MBMessageAssetRendererFactory;
import com.liferay.questions.web.internal.constants.QuestionsPortletKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	property = "model.class.name=com.liferay.questions.web.internal.configuration.QuestionsConfiguration",
	service = ConfigurationModelListener.class
)
public class QuestionsConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		try {
			Map<String, Object> propertiesMap = new HashMap<>();

			Enumeration<String> enumeration = properties.keys();

			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();

				propertiesMap.put(key, properties.get(key));
			}

			_enableAssetRenderer(propertiesMap);

			_enableServiceAccessPolicy(
				GetterUtil.getBoolean(properties.get("enableAnonymousRead")));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_enableAssetRenderer(properties);
	}

	@Deactivate
	protected void deactivate() {
		_unregister();
	}

	private void _enableAssetRenderer(Map<String, Object> properties) {
		if (GetterUtil.getBoolean(
				properties.get("enableCustomAssetRenderer"))) {

			String historyRouterBasePath = GetterUtil.getString(
				properties.get("historyRouterBasePath"));
			Dictionary<String, Object> assetRendererFactoryProperties =
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", QuestionsPortletKeys.QUESTIONS
				).put(
					"service.ranking:Integer", 100
				).build();

			_serviceRegistrations = Arrays.asList(
				_bundleContext.registerService(
					AssetRendererFactory.class,
					new MBCategoryAssetRendererFactory(
						_companyLocalService, historyRouterBasePath,
						_mbCategoryLocalService,
						_mbCategoryModelResourcePermission),
					assetRendererFactoryProperties),
				_bundleContext.registerService(
					AssetRendererFactory.class,
					new MBMessageAssetRendererFactory(
						_companyLocalService, _discussionPermission,
						historyRouterBasePath, _mbMessageLocalService,
						_mbMessageModelResourcePermission),
					assetRendererFactoryProperties));
		}
		else {
			_unregister();
		}
	}

	private void _enableServiceAccessPolicy(boolean enableAnonymousRead)
		throws Exception {

		String name = "QUESTIONS";

		SAPEntry sapEntry = _sapEntryService.fetchSAPEntry(
			CompanyThreadLocal.getCompanyId(), name);

		if (!enableAnonymousRead && (sapEntry != null)) {
			_sapEntryService.deleteSAPEntry(sapEntry);
		}
		else if (enableAnonymousRead && (sapEntry == null)) {
			String headlessDeliveryPackage =
				"com.liferay.headless.delivery.internal.resource.v1_0.";

			_sapEntryService.addSAPEntry(
				StringBundler.concat(
					"com.liferay.headless.admin.taxonomy.internal.resource.",
					"v1_0.KeywordResourceImpl#getKeywordsRankedPage\n",
					"com.liferay.headless.admin.user.internal.resource.v1_0.",
					"SubscriptionResourceImpl#",
					"getMyUserAccountSubscriptionsPage\n",
					headlessDeliveryPackage,
					"MessageBoardMessageResourceImpl#getMessageBoardMessage\n",
					headlessDeliveryPackage, "MessageBoardMessageResourceImpl#",
					"getMessageBoardMessageMessageBoardMessagesPage\n",
					headlessDeliveryPackage, "MessageBoardMessageResourceImpl#",
					"getMessageBoardMessageMyRating\n", headlessDeliveryPackage,
					"MessageBoardMessageResourceImpl#",
					"getMessageBoardThreadMessageBoardMessagesPage\n",
					headlessDeliveryPackage, "MessageBoardMessageResourceImpl#",
					"getSiteMessageBoardMessageByFriendlyUrlPath\n",
					headlessDeliveryPackage, "MessageBoardMessageResourceImpl#",
					"getSiteMessageBoardMessagesPage\n",
					headlessDeliveryPackage, "MessageBoardSectionResourceImpl#",
					"getMessageBoardSection\n", headlessDeliveryPackage,
					"MessageBoardSectionResourceImpl#",
					"getMessageBoardSectionMessageBoardSectionsPage\n",
					headlessDeliveryPackage, "MessageBoardSectionResourceImpl#",
					"getSiteMessageBoardSectionByFriendlyUrlPath\n",
					headlessDeliveryPackage, "MessageBoardSectionResourceImpl#",
					"getSiteMessageBoardSectionsPage\n",
					headlessDeliveryPackage, "MessageBoardThreadResourceImpl#",
					"getMessageBoardSectionMessageBoardThreadsPage\n",
					headlessDeliveryPackage, "MessageBoardThreadResourceImpl#",
					"getMessageBoardThreadMyRating\n", headlessDeliveryPackage,
					"MessageBoardThreadResourceImpl#",
					"getMessageBoardThreadsRankedPage\n",
					headlessDeliveryPackage, "MessageBoardThreadResourceImpl#",
					"getSiteMessageBoardThreadByFriendlyUrlPath\n",
					headlessDeliveryPackage, "MessageBoardThreadResourceImpl#",
					"getSiteMessageBoardThreadsPage\n"),
				true, true, name,
				Collections.singletonMap(
					LocaleThreadLocal.getDefaultLocale(), "Questions"),
				new ServiceContext());
		}
	}

	private void _unregister() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	private volatile BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_mbCategoryModelResourcePermission;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage>
		_mbMessageModelResourcePermission;

	@Reference
	private SAPEntryService _sapEntryService;

	private List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}