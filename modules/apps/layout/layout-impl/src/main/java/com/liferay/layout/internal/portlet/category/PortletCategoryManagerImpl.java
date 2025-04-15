/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.portlet.category;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.portlet.PortletManager;
import com.liferay.layout.portlet.category.PortletCategoryManager;
import com.liferay.layout.util.PortalPreferencesUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletCategoryComparator;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;
import com.liferay.portal.util.PortletCategoryUtil;
import com.liferay.portal.util.WebAppPool;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;
import com.liferay.segments.model.SegmentsExperienceModel;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = PortletCategoryManager.class)
public class PortletCategoryManagerImpl implements PortletCategoryManager {

	@Override
	public JSONArray getPortletsJSONArray(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		PortletCategory rootPortletCategory = (PortletCategory)WebAppPool.get(
			themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory highlightedPortletCategory =
			rootPortletCategory.getCategory("category.highlighted");

		PortletCategory portletCategory =
			PortletCategoryUtil.getRelevantPortletCategory(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getCompanyId(), themeDisplay.getLayout(),
				rootPortletCategory, themeDisplay.getLayoutTypePortlet());

		Map<String, JSONObject> portletCategoryJSONObjectsMap =
			_getPortletCategoryJSONObjectsMap(
				_getHighlightedPortletIds(
					httpServletRequest, highlightedPortletCategory),
				httpServletRequest, portletCategory, themeDisplay);

		List<String> sortedPortletCategoryKeys = _getSortedPortletCategoryKeys(
			_portletPreferencesFactory.getPortalPreferences(
				httpServletRequest));

		if (sortedPortletCategoryKeys.isEmpty()) {
			return JSONUtil.toJSONArray(
				new ArrayList<>(portletCategoryJSONObjectsMap.values()),
				portletCategoryJSONObject -> portletCategoryJSONObject);
		}

		List<JSONObject> sortedPortletCategoryJSONObjectsList =
			new LinkedList<>();

		for (String portletCategoryKey : sortedPortletCategoryKeys) {
			JSONObject portletCategoryJSONObject =
				portletCategoryJSONObjectsMap.remove(portletCategoryKey);

			if (portletCategoryJSONObject == null) {
				continue;
			}

			sortedPortletCategoryJSONObjectsList.add(portletCategoryJSONObject);
		}

		sortedPortletCategoryJSONObjectsList.addAll(
			0, portletCategoryJSONObjectsMap.values());

		return JSONUtil.toJSONArray(
			sortedPortletCategoryJSONObjectsList,
			portletCategoryJSONObject -> portletCategoryJSONObject);
	}

	@Override
	public void updateSortedPortletCategoryKeys(
		PortalPreferences portalPreferences,
		String[] sortedPortletCategoryKeys) {

		PortalPreferencesUtil.updateSortedPortalPreferencesValues(
			portalPreferences,
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
			"sortedPortletCategoryKeys", sortedPortletCategoryKeys);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PortletManager.class, "jakarta.portlet.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Set<String> _getFragmentEntryLinksPortletNames(
		boolean deleted, ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeContent() && !layout.isTypeAssetDisplay()) {
			return Collections.emptySet();
		}

		Set<String> portletIds = new HashSet<>();

		long[] segmentsExperiencesIds = TransformUtil.transformToLongArray(
			_segmentsExperienceLocalService.getSegmentsExperiences(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid()),
			SegmentsExperienceModel::getSegmentsExperienceId);

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						themeDisplay.getScopeGroupId(), segmentsExperiencesIds,
						themeDisplay.getPlid(), deleted)) {

			portletIds.addAll(
				TransformUtil.transform(
					_portletRegistry.getFragmentEntryLinkPortletIds(
						fragmentEntryLink),
					portletId -> PortletIdCodec.decodePortletName(portletId)));
		}

		return portletIds;
	}

	private Set<String> _getHighlightedPortletIds(
		HttpServletRequest httpServletRequest,
		PortletCategory highlightedPortletCategory) {

		Set<String> highlightedPortletIds = new TreeSet<>(
			highlightedPortletCategory.getPortletIds());

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(httpServletRequest);

		highlightedPortletIds.addAll(
			SetUtil.fromArray(
				portalPreferences.getValues(
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
					"highlightedPortletIds", new String[0])));

		highlightedPortletIds.removeAll(
			SetUtil.fromArray(
				portalPreferences.getValues(
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
					"nonhighlightedPortletIds", new String[0])));

		return highlightedPortletIds;
	}

	private Set<String> _getLayoutDecodedPortletNames(
		ThemeDisplay themeDisplay) {

		Set<String> layoutDecodedPortletNames = new HashSet<>();

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		for (Portlet layoutPortlet : layoutTypePortlet.getPortlets()) {
			String decodedPortletName = PortletIdCodec.decodePortletName(
				layoutPortlet.getPortletId());

			layoutDecodedPortletNames.add(decodedPortletName);
		}

		return layoutDecodedPortletNames;
	}

	private String _getPermissionsURL(HttpServletRequest httpServletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest,
					PortletConfigurationApplicationType.PortletConfiguration.
						CLASS_NAME,
					PortletProvider.Action.VIEW)
			).setMVCPath(
				"/edit_permissions.jsp"
			).setPortletResource(
				portletDisplay.getId()
			).setParameter(
				"portletConfiguration", true
			).setParameter(
				"resourcePrimKey",
				PortletPermissionUtil.getPrimaryKey(
					themeDisplay.getPlid(), portletDisplay.getId())
			).setParameter(
				"returnToFullPageURL",
				ParamUtil.getString(httpServletRequest, "returnToFullPageURL")
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private Map<String, JSONObject> _getPortletCategoryJSONObjectsMap(
			Set<String> highlightedPortletIds,
			HttpServletRequest httpServletRequest,
			PortletCategory portletCategory, ThemeDisplay themeDisplay)
		throws Exception {

		Map<String, JSONObject> portletCategoryJSONObjectsMap =
			new LinkedHashMap<>();

		Set<String> deletedFragmentEntryLinksPortletNames =
			_getFragmentEntryLinksPortletNames(true, themeDisplay);
		Set<String> fragmentEntryLinksPortletNames =
			_getFragmentEntryLinksPortletNames(false, themeDisplay);
		Set<String> layoutDecodedPortletNames = _getLayoutDecodedPortletNames(
			themeDisplay);

		List<PortletCategory> portletCategories = ListUtil.fromCollection(
			portletCategory.getCategories());

		portletCategories = ListUtil.sort(
			portletCategories,
			new PortletCategoryComparator(themeDisplay.getLocale()));

		for (PortletCategory currentPortletCategory : portletCategories) {
			if (currentPortletCategory.isHidden()) {
				continue;
			}

			String portletCategoryKey = StringUtil.replace(
				currentPortletCategory.getPath(), new String[] {"/", "."},
				new String[] {"-", "-"});

			Map<String, JSONObject> childPortletCategoryJSONObjectsMap =
				_getPortletCategoryJSONObjectsMap(
					highlightedPortletIds, httpServletRequest,
					currentPortletCategory, themeDisplay);

			JSONArray childPortletCategoriesJSONArray = JSONUtil.toJSONArray(
				childPortletCategoryJSONObjectsMap.values(),
				portletCategoryJSONObject -> portletCategoryJSONObject);

			JSONArray portletsJSONArray = _getPortletsJSONArray(
				deletedFragmentEntryLinksPortletNames,
				fragmentEntryLinksPortletNames, highlightedPortletIds,
				httpServletRequest, layoutDecodedPortletNames,
				currentPortletCategory, themeDisplay);

			if ((childPortletCategoriesJSONArray.length() > 0) ||
				(portletsJSONArray.length() > 0)) {

				portletCategoryJSONObjectsMap.put(
					portletCategoryKey,
					JSONUtil.put(
						"categories", childPortletCategoriesJSONArray
					).put(
						"path", portletCategoryKey
					).put(
						"portlets", portletsJSONArray
					).put(
						"title",
						_getPortletCategoryTitle(
							httpServletRequest, currentPortletCategory,
							themeDisplay)
					));
			}
		}

		return portletCategoryJSONObjectsMap;
	}

	private String _getPortletCategoryTitle(
		HttpServletRequest httpServletRequest, PortletCategory portletCategory,
		ThemeDisplay themeDisplay) {

		for (String portletId :
				PortletCategoryUtil.getFirstChildPortletIds(portletCategory)) {

			Portlet portlet = _portletLocalService.getPortletById(
				themeDisplay.getCompanyId(), portletId);

			if (portlet == null) {
				continue;
			}

			PortletApp portletApp = portlet.getPortletApp();

			if (!portletApp.isWARFile()) {
				continue;
			}

			PortletConfig portletConfig = PortletConfigFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			ResourceBundle portletResourceBundle =
				portletConfig.getResourceBundle(themeDisplay.getLocale());

			String title = ResourceBundleUtil.getString(
				portletResourceBundle, portletCategory.getName());

			if (Validator.isNotNull(title)) {
				return title;
			}
		}

		return _language.get(httpServletRequest, portletCategory.getName());
	}

	private JSONArray _getPortletItemsJSONArray(
		Portlet portlet, ThemeDisplay themeDisplay) {

		List<PortletItem> portletItems =
			_portletItemLocalService.getPortletItems(
				themeDisplay.getScopeGroupId(), portlet.getPortletId(),
				PortletPreferences.class.getName());

		if (ListUtil.isEmpty(portletItems)) {
			return _jsonFactory.createJSONArray();
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (PortletItem portletItem : portletItems) {
			jsonArray.put(
				JSONUtil.put(
					"instanceable", portlet.isInstanceable()
				).put(
					"portletId", portlet.getPortletId()
				).put(
					"portletItemId", portletItem.getPortletItemId()
				).put(
					"title", HtmlUtil.escape(portletItem.getName())
				));
		}

		return jsonArray;
	}

	private List<Portlet> _getPortlets(
		Set<String> highlightedPortletIds, PortletCategory portletCategory,
		ThemeDisplay themeDisplay) {

		Set<String> portletIds = portletCategory.getPortletIds();

		Layout layout = themeDisplay.getLayout();

		if (Objects.equals(portletCategory.getName(), "category.highlighted")) {
			portletIds = highlightedPortletIds;
		}

		return TransformUtil.transform(
			portletIds,
			portletId -> {
				Portlet portlet = _portletLocalService.getPortletById(
					themeDisplay.getCompanyId(), portletId);

				if (!_isVisible(layout, portlet)) {
					return null;
				}

				try {
					if (PortletPermissionUtil.contains(
							themeDisplay.getPermissionChecker(), layout,
							portlet, ActionKeys.ADD_TO_PAGE)) {

						return portlet;
					}
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to check portlet permissions for " +
							portlet.getPortletId(),
						portalException);
				}

				return null;
			});
	}

	private JSONArray _getPortletsJSONArray(
			Set<String> deletedFragmentEntryLinksPortletNames,
			Set<String> fragmentEntryLinksPortletNames,
			Set<String> highlightedPortletIds,
			HttpServletRequest httpServletRequest,
			Set<String> layoutDecodedPortletNames,
			PortletCategory portletCategory, ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		HttpSession httpSession = httpServletRequest.getSession();

		ServletContext servletContext = httpSession.getServletContext();

		List<Portlet> portlets = _getPortlets(
			highlightedPortletIds, portletCategory, themeDisplay);

		portlets = ListUtil.sort(
			portlets,
			new PortletTitleComparator(
				servletContext, themeDisplay.getLocale()));

		for (Portlet portlet : portlets) {
			jsonArray.put(
				JSONUtil.put(
					"deprecated", _isDeprecated(portlet)
				).put(
					"embedded",
					() -> {
						if (deletedFragmentEntryLinksPortletNames.contains(
								portlet.getPortletId()) ||
							fragmentEntryLinksPortletNames.contains(
								portlet.getPortletId())) {

							return false;
						}

						Layout layout = themeDisplay.getLayout();

						long count1 =
							_portletPreferencesLocalService.
								getPortletPreferencesCount(
									PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
									layout.getPlid(), portlet.getPortletId());

						if (count1 > 0) {
							return true;
						}

						return false;
					}
				).put(
					"highlighted",
					highlightedPortletIds.contains(portlet.getPortletId())
				).put(
					"instanceable", portlet.isInstanceable()
				).put(
					"permissionsURL", _getPermissionsURL(httpServletRequest)
				).put(
					"portletId", portlet.getPortletId()
				).put(
					"portletItems",
					_getPortletItemsJSONArray(portlet, themeDisplay)
				).put(
					"title",
					_portal.getPortletTitle(
						portlet, servletContext, themeDisplay.getLocale())
				).put(
					"used",
					() -> {
						Layout layout = themeDisplay.getLayout();

						if (!layout.isTypePortlet() ||
							portlet.isInstanceable() ||
							deletedFragmentEntryLinksPortletNames.contains(
								portlet.getPortletId())) {

							return false;
						}

						LayoutTypePortlet layoutTypePortlet =
							themeDisplay.getLayoutTypePortlet();

						if (layoutDecodedPortletNames.contains(
								portlet.getPortletId()) ||
							layoutTypePortlet.hasPortletId(
								portlet.getPortletId())) {

							return true;
						}

						return false;
					}
				));
		}

		return jsonArray;
	}

	private List<String> _getSortedPortletCategoryKeys(
		PortalPreferences portalPreferences) {

		return PortalPreferencesUtil.getSortedPortalPreferencesValues(
			portalPreferences,
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
			"sortedPortletCategoryKeys");
	}

	private boolean _isDeprecated(Portlet portlet) {
		if (portlet == null) {
			return false;
		}

		PortletManager portletManager = _serviceTrackerMap.getService(
			portlet.getRootPortletId());

		if ((portletManager != null) && portletManager.isDeprecated()) {
			return true;
		}

		return false;
	}

	private boolean _isVisible(Layout layout, Portlet portlet) {
		if (portlet == null) {
			return false;
		}

		PortletManager portletManager = _serviceTrackerMap.getService(
			portlet.getRootPortletId());

		if ((portletManager != null) && !portletManager.isVisible(layout)) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletCategoryManagerImpl.class);

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletItemLocalService _portletItemLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceTrackerMap<String, PortletManager> _serviceTrackerMap;

}