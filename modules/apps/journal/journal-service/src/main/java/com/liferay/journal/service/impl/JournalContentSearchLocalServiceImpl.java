/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.service.base.JournalContentSearchLocalServiceBaseImpl;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.DisplayInformationProvider;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.PortletKeys;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalContentSearch",
	service = AopService.class
)
public class JournalContentSearchLocalServiceImpl
	extends JournalContentSearchLocalServiceBaseImpl {

	@Override
	public void checkContentSearches(long companyId) throws PortalException {
		if (_log.isInfoEnabled()) {
			_log.info("Checking journal content search for " + companyId);
		}

		Map<JournalContentSearchKey, JournalContentSearch>
			orphanedJournalContentSearches = new HashMap<>();

		List<JournalContentSearch> journalContentSearches =
			journalContentSearchPersistence.findByCompanyId(companyId);

		for (JournalContentSearch journalContentSearch :
				journalContentSearches) {

			JournalContentSearchKey journalContentSearchKey =
				new JournalContentSearchKey(journalContentSearch);

			orphanedJournalContentSearches.put(
				journalContentSearchKey, journalContentSearch);
		}

		Set<String> rootPortletIds = _serviceTrackerMap.keySet();

		for (String rootPortletId : rootPortletIds) {
			DisplayInformationProvider displayInformationProvider =
				_serviceTrackerMap.getService(rootPortletId);

			List<PortletPreferences> portletPreferencesList = new ArrayList<>();

			portletPreferencesList.addAll(
				_portletPreferencesLocalService.getPortletPreferences(
					companyId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, rootPortletId));
			portletPreferencesList.addAll(
				_portletPreferencesLocalService.getPortletPreferences(
					companyId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					rootPortletId + "_INSTANCE_%"));

			for (PortletPreferences portletPreferences :
					portletPreferencesList) {

				Layout layout = _layoutLocalService.fetchLayout(
					portletPreferences.getPlid());

				if (layout == null) {
					continue;
				}

				String portletId = portletPreferences.getPortletId();

				jakarta.portlet.PortletPreferences jxPortletPreferences =
					_portletPreferenceValueLocalService.getPreferences(
						portletPreferences);

				String articleId = displayInformationProvider.getClassPK(
					jxPortletPreferences);

				JournalContentSearchKey journalContentSearchKey =
					new JournalContentSearchKey(
						layout.getGroupId(), layout.isPrivateLayout(),
						layout.getLayoutId(), portletId, articleId);

				JournalContentSearch existingJournalContentSearch =
					orphanedJournalContentSearches.remove(
						journalContentSearchKey);

				if (existingJournalContentSearch == null) {
					updateContentSearch(
						layout.getGroupId(), layout.isPrivateLayout(),
						layout.getLayoutId(), portletId, articleId);
				}
			}
		}

		for (JournalContentSearch journalContentSearch :
				orphanedJournalContentSearches.values()) {

			journalContentSearchPersistence.remove(journalContentSearch);
		}
	}

	@Override
	public void deleteArticleContentSearch(
		long groupId, boolean privateLayout, long layoutId, String portletId) {

		journalContentSearchPersistence.removeByG_P_L_P(
			groupId, privateLayout, layoutId, portletId);
	}

	@Override
	public void deleteArticleContentSearch(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId) {

		JournalContentSearch contentSearch =
			journalContentSearchPersistence.fetchByG_P_L_P_A(
				groupId, privateLayout, layoutId, portletId, articleId);

		if (contentSearch != null) {
			deleteJournalContentSearch(contentSearch);
		}
	}

	@Override
	public void deleteArticleContentSearches(long groupId, String articleId) {
		List<JournalContentSearch> contentSearches =
			journalContentSearchPersistence.findByG_A(groupId, articleId);

		for (JournalContentSearch contentSearch : contentSearches) {
			deleteJournalContentSearch(contentSearch);
		}
	}

	@Override
	public void deleteLayoutContentSearches(
		long groupId, boolean privateLayout, long layoutId) {

		List<JournalContentSearch> contentSearches =
			journalContentSearchPersistence.findByG_P_L(
				groupId, privateLayout, layoutId);

		for (JournalContentSearch contentSearch : contentSearches) {
			deleteJournalContentSearch(contentSearch);
		}
	}

	@Override
	public void deleteOwnerContentSearches(
		long groupId, boolean privateLayout) {

		List<JournalContentSearch> contentSearches =
			journalContentSearchPersistence.findByG_P(groupId, privateLayout);

		for (JournalContentSearch contentSearch : contentSearches) {
			deleteJournalContentSearch(contentSearch);
		}
	}

	@Override
	public List<JournalContentSearch> getArticleContentSearches() {
		return journalContentSearchPersistence.findAll();
	}

	@Override
	public List<JournalContentSearch> getArticleContentSearches(
		long groupId, String articleId) {

		return journalContentSearchPersistence.findByG_A(groupId, articleId);
	}

	@Override
	public List<JournalContentSearch> getArticleContentSearches(
		String articleId) {

		return journalContentSearchPersistence.findByArticleId(articleId);
	}

	@Override
	public List<Long> getLayoutIds(
		long groupId, boolean privateLayout, String articleId) {

		return TransformUtil.transform(
			journalContentSearchPersistence.findByG_P_A(
				groupId, privateLayout, articleId),
			contentSearch -> contentSearch.getLayoutId());
	}

	@Override
	public int getLayoutIdsCount(
		long groupId, boolean privateLayout, String articleId) {

		return journalContentSearchPersistence.countByG_P_A(
			groupId, privateLayout, articleId);
	}

	@Override
	public int getLayoutIdsCount(String articleId) {
		return journalContentSearchPersistence.countByArticleId(articleId);
	}

	@Override
	public List<JournalContentSearch> getPortletContentSearches(
		String portletId) {

		return journalContentSearchPersistence.findByPortletId(portletId);
	}

	@Override
	public JournalContentSearch updateContentSearch(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String articleId)
		throws PortalException {

		return updateContentSearch(
			groupId, privateLayout, layoutId, portletId, articleId, false);
	}

	@Override
	public JournalContentSearch updateContentSearch(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String articleId, boolean purge)
		throws PortalException {

		if (purge) {
			journalContentSearchPersistence.removeByG_P_L_P(
				groupId, privateLayout, layoutId, portletId);
		}

		Group group = _groupLocalService.getGroup(groupId);

		JournalContentSearch contentSearch =
			journalContentSearchPersistence.fetchByG_P_L_P_A(
				groupId, privateLayout, layoutId, portletId, articleId);

		if (contentSearch == null) {
			long contentSearchId = counterLocalService.increment();

			contentSearch = journalContentSearchPersistence.create(
				contentSearchId);

			contentSearch.setGroupId(groupId);
			contentSearch.setCompanyId(group.getCompanyId());
			contentSearch.setPrivateLayout(privateLayout);
			contentSearch.setLayoutId(layoutId);
			contentSearch.setPortletId(portletId);
			contentSearch.setArticleId(articleId);
		}

		return journalContentSearchPersistence.update(contentSearch);
	}

	@Override
	public List<JournalContentSearch> updateContentSearch(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String[] articleIds)
		throws PortalException {

		journalContentSearchPersistence.removeByG_P_L_P(
			groupId, privateLayout, layoutId, portletId);

		List<JournalContentSearch> contentSearches = new ArrayList<>();

		for (String articleId : articleIds) {
			JournalContentSearch contentSearch = updateContentSearch(
				groupId, privateLayout, layoutId, portletId, articleId, false);

			contentSearches.add(contentSearch);
		}

		return contentSearches;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DisplayInformationProvider.class,
			"jakarta.portlet.name");
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentSearchLocalServiceImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private ServiceTrackerMap<String, DisplayInformationProvider>
		_serviceTrackerMap;

	private static class JournalContentSearchKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			JournalContentSearchKey journalContentSearchKey =
				(JournalContentSearchKey)object;

			if (Objects.equals(
					journalContentSearchKey._articleId, _articleId) &&
				(journalContentSearchKey._groupId == _groupId) &&
				Objects.equals(journalContentSearchKey._layoutId, _layoutId) &&
				Objects.equals(
					journalContentSearchKey._portletId, _portletId) &&
				(journalContentSearchKey._privateLayout == _privateLayout)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _groupId);

			hashCode = HashUtil.hash(hashCode, _articleId);
			hashCode = HashUtil.hash(hashCode, _layoutId);
			hashCode = HashUtil.hash(hashCode, _privateLayout);

			return HashUtil.hash(hashCode, _portletId);
		}

		private JournalContentSearchKey(
			JournalContentSearch journalContentSearch) {

			_articleId = journalContentSearch.getArticleId();
			_groupId = journalContentSearch.getGroupId();
			_layoutId = journalContentSearch.getLayoutId();
			_portletId = journalContentSearch.getPortletId();
			_privateLayout = journalContentSearch.getPrivateLayout();
		}

		private JournalContentSearchKey(
			long groupId, boolean privateLayout, long layoutId,
			String portletId, String articleId) {

			_groupId = groupId;
			_privateLayout = privateLayout;
			_layoutId = layoutId;
			_portletId = portletId;
			_articleId = articleId;
		}

		private static final long serialVersionUID = 1L;

		private final String _articleId;
		private final long _groupId;
		private final long _layoutId;
		private final String _portletId;
		private final boolean _privateLayout;

	}

}