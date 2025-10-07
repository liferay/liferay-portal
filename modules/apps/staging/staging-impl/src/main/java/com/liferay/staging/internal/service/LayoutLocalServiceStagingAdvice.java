/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.internal.service;

import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutStagingHandler;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionPersistence;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntry;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.impl.LayoutLocalServiceHelper;
import com.liferay.portlet.exportimport.staging.ProxiedLayoutsThreadLocal;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import java.io.Closeable;
import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
@Component(service = {})
public class LayoutLocalServiceStagingAdvice {

	public void deleteLayout(
			LayoutLocalService layoutLocalService, Layout layout,
			ServiceContext serviceContext)
		throws PortalException {

		long layoutSetBranchId = ParamUtil.getLong(
			serviceContext, "layoutSetBranchId");

		if (layoutSetBranchId > 0) {
			_layoutRevisionLocalService.deleteLayoutRevisions(
				layoutSetBranchId, layout.getPlid());

			List<LayoutRevision> notIncompleteLayoutRevisions =
				_layoutRevisionPersistence.findByP_NotS(
					layout.getPlid(), WorkflowConstants.STATUS_INCOMPLETE);

			if (notIncompleteLayoutRevisions.isEmpty()) {
				_layoutRevisionLocalService.deleteLayoutLayoutRevisions(
					layout.getPlid());

				doDeleteLayout(layoutLocalService, layout, serviceContext);
			}
		}
		else {
			doDeleteLayout(layoutLocalService, layout, serviceContext);
		}
	}

	public void deleteLayout(
			LayoutLocalService layoutLocalService, long groupId,
			boolean privateLayout, long layoutId, ServiceContext serviceContext)
		throws PortalException {

		Layout layout = layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		deleteLayout(layoutLocalService, layout, serviceContext);
	}

	public Layout updateLayout(
			LayoutLocalService layoutLocalService, long groupId,
			boolean privateLayout, long layoutId, long parentLayoutId,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, boolean hidden,
			Map<Locale, String> friendlyURLMap, boolean hasIconImage,
			byte[] iconBytes, long styleBookEntryId, long faviconFileEntryId,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException {

		// Layout

		parentLayoutId = _layoutLocalServiceHelper.getParentLayoutId(
			groupId, privateLayout, parentLayoutId);

		Layout layout = _layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		if (nameMap.isEmpty()) {
			nameMap = _localization.getLocalizationMap(layout.getName(), true);
		}

		String name = nameMap.get(LocaleUtil.getSiteDefault());

		if (Validator.isNull(name) && nameMap.isEmpty()) {
			name = _getLayoutPageTemplateEntryName(layout);

			if (Validator.isNull(name)) {
				throw new LayoutNameException(
					"Name is required for layout PLID " + layout.getPlid(),
					LayoutNameException.REQUIRED);
			}

			nameMap.put(LocaleUtil.getSiteDefault(), name);
		}
		else if (Validator.isNull(name)) {
			List<String> values = new ArrayList<>(nameMap.values());

			name = values.get(0);
		}

		Map<Locale, String> layoutFriendlyURLMap =
			_layoutLocalServiceHelper.getFriendlyURLMap(
				groupId, privateLayout, layoutId, name, friendlyURLMap);

		_layoutLocalServiceHelper.validate(
			groupId, privateLayout, layoutId, parentLayoutId,
			layout.getClassNameId(), layout.getClassPK(), name, type,
			layoutFriendlyURLMap, serviceContext);

		_layoutLocalServiceHelper.validateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			layout = getProxiedLayout(layout);
		}

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision == null) {
			return layoutLocalService.updateLayout(
				groupId, privateLayout, layoutId, parentLayoutId, nameMap,
				titleMap, descriptionMap, keywordsMap, robotsMap, type, hidden,
				friendlyURLMap, hasIconImage, iconBytes, styleBookEntryId,
				faviconFileEntryId, masterLayoutPlid, serviceContext);
		}

		layoutLocalService.updateAsset(
			serviceContext.getUserId(), layout,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		if (parentLayoutId != layout.getParentLayoutId()) {
			int priority = _layoutLocalServiceHelper.getNextPriority(
				groupId, layout.getLayoutSetPrototypeLayoutERC(), privateLayout,
				parentLayoutId, -1);

			layout.setPriority(priority);
		}

		layout.setParentLayoutId(parentLayoutId);
		layoutRevision.setNameMap(nameMap);
		layoutRevision.setTitleMap(titleMap);
		layoutRevision.setDescriptionMap(descriptionMap);
		layoutRevision.setKeywordsMap(keywordsMap);
		layoutRevision.setRobotsMap(robotsMap);
		layout.setType(type);
		layout.setHidden(hidden);
		layout.setFriendlyURL(
			layoutFriendlyURLMap.get(LocaleUtil.getSiteDefault()));

		if (!hasIconImage) {
			layout.setIconImageId(0);
			layoutRevision.setIconImageId(0);
		}
		else {
			_portal.updateImageId(
				layout, hasIconImage, iconBytes, "iconImageId", 0, 0, 0);
		}

		layout.setLayoutPrototypeLinkEnabled(
			ParamUtil.getBoolean(serviceContext, "layoutPrototypeLinkEnabled"));
		layout.setExpandoBridgeAttributes(serviceContext);

		layout = _layoutPersistence.update(layout);

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURLs(
			layout.getUserId(), layout.getCompanyId(), layout.getGroupId(),
			layout.getPlid(), layout.isPrivateLayout(), layoutFriendlyURLMap,
			serviceContext);

		boolean hasWorkflowTask = _staging.hasWorkflowTask(
			serviceContext.getUserId(), layoutRevision);

		serviceContext.setAttribute("revisionInProgress", hasWorkflowTask);

		int workflowAction = serviceContext.getWorkflowAction();

		try {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			_layoutRevisionLocalService.updateLayoutRevision(
				serviceContext.getUserId(),
				layoutRevision.getLayoutRevisionId(),
				layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
				layoutRevision.getTitle(), layoutRevision.getDescription(),
				layoutRevision.getKeywords(), layoutRevision.getRobots(),
				layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
				layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
				layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
				serviceContext);
		}
		finally {
			serviceContext.setWorkflowAction(workflowAction);
		}

		return layout;
	}

	public Layout updateLayout(
			LayoutLocalService layoutLocalService, long groupId,
			boolean privateLayout, long layoutId, String typeSettings)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return layoutLocalService.updateLayout(
				groupId, privateLayout, layoutId, typeSettings);
		}

		Layout layout = _layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			layout = getProxiedLayout(layout);
		}

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision == null) {
			return layoutLocalService.updateLayout(
				groupId, privateLayout, layoutId, typeSettings);
		}

		layout.setTypeSettings(typeSettings);

		boolean hasWorkflowTask = _staging.hasWorkflowTask(
			serviceContext.getUserId(), layoutRevision);

		serviceContext.setAttribute("revisionInProgress", hasWorkflowTask);

		if (!MergeLayoutPrototypesThreadLocal.isInProgress()) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		_layoutRevisionLocalService.updateLayoutRevision(
			serviceContext.getUserId(), layoutRevision.getLayoutRevisionId(),
			layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
			layoutRevision.getTitle(), layoutRevision.getDescription(),
			layoutRevision.getKeywords(), layoutRevision.getRobots(),
			layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
			layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
			layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
			serviceContext);

		return layout;
	}

	public Layout updateLookAndFeel(
			LayoutLocalService layoutLocalService, long groupId,
			boolean privateLayout, long layoutId, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return layoutLocalService.updateLookAndFeel(
				groupId, privateLayout, layoutId, themeId, colorSchemeId, css);
		}

		Layout layout = _layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			layout = getProxiedLayout(layout);
		}

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision == null) {
			return layoutLocalService.updateLookAndFeel(
				groupId, privateLayout, layoutId, themeId, colorSchemeId, css);
		}

		layout.setThemeId(themeId);
		layout.setColorSchemeId(colorSchemeId);
		layout.setCss(css);

		boolean hasWorkflowTask = _staging.hasWorkflowTask(
			serviceContext.getUserId(), layoutRevision);

		serviceContext.setAttribute("revisionInProgress", hasWorkflowTask);

		if (!MergeLayoutPrototypesThreadLocal.isInProgress()) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		_layoutRevisionLocalService.updateLayoutRevision(
			serviceContext.getUserId(), layoutRevision.getLayoutRevisionId(),
			layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
			layoutRevision.getTitle(), layoutRevision.getDescription(),
			layoutRevision.getKeywords(), layoutRevision.getRobots(),
			layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
			layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
			layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
			serviceContext);

		return layout;
	}

	public Layout updateName(
			LayoutLocalService layoutLocalService, Layout layout, String name,
			String languageId)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return layoutLocalService.updateName(layout, name, languageId);
		}

		layout = wrapLayout(layout);

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (layoutRevision == null) {
			return layoutLocalService.updateName(layout, name, languageId);
		}

		_layoutLocalServiceHelper.validateName(name, languageId);

		layout.setName(name, LocaleUtil.fromLanguageId(languageId));

		boolean hasWorkflowTask = _staging.hasWorkflowTask(
			serviceContext.getUserId(), layoutRevision);

		serviceContext.setAttribute("revisionInProgress", hasWorkflowTask);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_layoutRevisionLocalService.updateLayoutRevision(
			serviceContext.getUserId(), layoutRevision.getLayoutRevisionId(),
			layoutRevision.getLayoutBranchId(), layoutRevision.getName(),
			layoutRevision.getTitle(), layoutRevision.getDescription(),
			layoutRevision.getKeywords(), layoutRevision.getRobots(),
			layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
			layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
			layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
			serviceContext);

		return layout;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_closeable = StagingAdviceUtil.register(
			bundleContext, LayoutLocalServiceStagingInvocationHandler::new,
			_layoutLocalService, LayoutLocalService.class);
	}

	@Deactivate
	protected void deactivate() throws IOException {
		_closeable.close();
	}

	protected void doDeleteLayout(
			LayoutLocalService layoutLocalService, Layout layout,
			ServiceContext serviceContext)
		throws PortalException {

		boolean mergeLayoutPrototypesIsInProgress = false;

		try {
			mergeLayoutPrototypesIsInProgress =
				MergeLayoutPrototypesThreadLocal.isInProgress();

			MergeLayoutPrototypesThreadLocal.setInProgress(true);

			SystemEventHierarchyEntry systemEventHierarchyEntry =
				SystemEventHierarchyEntryThreadLocal.push(
					Layout.class, layout.getPlid());

			if (systemEventHierarchyEntry == null) {
				layoutLocalService.deleteLayout(layout, serviceContext);
			}
			else {
				try {
					layoutLocalService.deleteLayout(layout, serviceContext);

					systemEventHierarchyEntry =
						SystemEventHierarchyEntryThreadLocal.peek();

					_systemEventLocalService.addSystemEvent(
						0, layout.getGroupId(), null, Layout.class.getName(),
						layout.getPlid(), layout.getUuid(), null,
						SystemEventConstants.TYPE_DELETE,
						systemEventHierarchyEntry.getExtraData());
				}
				finally {
					SystemEventHierarchyEntryThreadLocal.pop(
						Layout.class, layout.getPlid());
				}
			}
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setInProgress(
				mergeLayoutPrototypesIsInProgress);
		}
	}

	protected Layout getProxiedLayout(Layout layout) {
		ObjectValuePair<ServiceContext, Map<Layout, Object>> objectValuePair =
			ProxiedLayoutsThreadLocal.getProxiedLayouts();

		ServiceContext currentServiceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (objectValuePair != null) {
			ServiceContext serviceContext = objectValuePair.getKey();

			if (serviceContext == currentServiceContext) {
				Map<Layout, Object> proxiedLayouts = objectValuePair.getValue();

				Object proxiedLayout = proxiedLayouts.get(layout);

				if (proxiedLayout != null) {
					Layout cachedProxiedLayout = (Layout)proxiedLayout;

					if (layout.getMvccVersion() ==
							cachedProxiedLayout.getMvccVersion()) {

						return cachedProxiedLayout;
					}

					proxiedLayouts.remove(layout);
				}

				proxiedLayout = _proxyProviderFunction.apply(
					new LayoutStagingHandler(layout));

				proxiedLayouts.put(layout, proxiedLayout);

				return (Layout)proxiedLayout;
			}
		}

		Object proxiedLayout = _proxyProviderFunction.apply(
			new LayoutStagingHandler(layout));

		ProxiedLayoutsThreadLocal.setProxiedLayouts(
			new ObjectValuePair<>(
				currentServiceContext,
				HashMapBuilder.<Layout, Object>put(
					layout, proxiedLayout
				).build()));

		return (Layout)proxiedLayout;
	}

	protected Layout unwrapLayout(Layout layout) {
		LayoutStagingHandler layoutStagingHandler =
			LayoutStagingUtil.getLayoutStagingHandler(layout);

		if (layoutStagingHandler == null) {
			return layout;
		}

		return layoutStagingHandler.getLayout();
	}

	protected Layout wrapLayout(Layout layout) {
		LayoutStagingHandler layoutStagingHandler =
			LayoutStagingUtil.getLayoutStagingHandler(layout);

		if ((layoutStagingHandler != null) ||
			!LayoutStagingUtil.isBranchingLayout(layout)) {

			return layout;
		}

		return getProxiedLayout(layout);
	}

	protected List<Layout> wrapLayouts(
		List<Layout> layouts, boolean showIncomplete) {

		if (layouts.isEmpty()) {
			return layouts;
		}

		Layout firstLayout = layouts.get(0);

		Layout wrappedFirstLayout = wrapLayout(firstLayout);

		if (wrappedFirstLayout == firstLayout) {
			return layouts;
		}

		long layoutSetBranchId = 0;

		if (!showIncomplete) {
			long userId = 0;

			try {
				userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

				if (userId > 0) {
					LayoutSet layoutSet = firstLayout.getLayoutSet();

					layoutSetBranchId = _staging.getRecentLayoutSetBranchId(
						_userLocalService.getUser(userId),
						layoutSet.getLayoutSetId());
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"No layout set branch found for user " + userId,
						exception);
				}
			}
		}

		List<Layout> wrappedLayouts = new ArrayList<>(layouts.size());

		for (Layout layout : layouts) {
			Layout wrappedLayout = wrapLayout(layout);

			if (showIncomplete ||
				!_staging.isIncomplete(wrappedLayout, layoutSetBranchId)) {

				wrappedLayouts.add(wrappedLayout);
			}
		}

		return wrappedLayouts;
	}

	protected Object wrapReturnValue(
		Object returnValue, boolean showIncomplete) {

		if (returnValue instanceof Layout) {
			returnValue = wrapLayout((Layout)returnValue);
		}
		else if (returnValue instanceof List<?>) {
			List<?> list = (List<?>)returnValue;

			if (!list.isEmpty()) {
				Object object = list.get(0);

				if (object instanceof Layout) {
					returnValue = wrapLayouts(
						(List<Layout>)returnValue, showIncomplete);
				}
			}
		}
		else if (returnValue instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>)returnValue;

			if (map.isEmpty()) {
				return returnValue;
			}

			map.replaceAll(
				(key, value) -> wrapReturnValue(value, showIncomplete));
		}

		return returnValue;
	}

	private String _getLayoutPageTemplateEntryName(Layout layout) {
		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return null;
		}

		long plid = layout.getPlid();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry.getName();
		}

		return null;
	}

	private static final Class<?>[] _GET_LAYOUTS_TYPES = {
		Long.TYPE, Boolean.TYPE, Long.TYPE
	};

	private static final Class<?>[] _UPDATE_LAYOUT_PARAMETER_TYPES = {
		long.class, boolean.class, long.class, long.class, Map.class, Map.class,
		Map.class, Map.class, Map.class, String.class, boolean.class,
		String.class, Boolean.class, byte[].class, ServiceContext.class
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutLocalServiceStagingAdvice.class);

	private static final Set<String>
		_layoutLocalServiceStagingAdviceMethodNames = new HashSet<>(
			Arrays.asList(
				"create", "createLayout", "deleteLayout", "getLayouts",
				"updateLayout", "updateLookAndFeel", "updateName"));
	private static final Function<InvocationHandler, Layout>
		_proxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			Layout.class, ModelWrapper.class);

	private Closeable _closeable;

	@Reference
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLocalServiceHelper _layoutLocalServiceHelper;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPersistence _layoutPersistence;

	@Reference
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Reference
	private LayoutRevisionPersistence _layoutRevisionPersistence;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class LayoutLocalServiceStagingInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] arguments)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("getWrappedService")) {
				return _targetObject;
			}

			if (methodName.equals("setWrappedService")) {
				_targetObject = arguments[0];

				return null;
			}

			if (!StagingAdvicesThreadLocal.isEnabled()) {
				return _invoke(method, arguments);
			}

			if (!_layoutLocalServiceStagingAdviceMethodNames.contains(
					methodName)) {

				return wrapReturnValue(_invoke(method, arguments), false);
			}

			Object returnValue = null;

			Class<?>[] parameterTypes = method.getParameterTypes();

			if (methodName.equals("create") ||
				methodName.equals("createLayout")) {

				return _invoke(method, arguments);
			}
			else if (methodName.equals("deleteLayout")) {
				if ((arguments.length == 2) &&
					(arguments[0] instanceof Layout)) {

					deleteLayout(
						(LayoutLocalService)_targetObject, (Layout)arguments[0],
						(ServiceContext)arguments[1]);
				}
				else if (arguments.length == 3) {
					deleteLayout(
						(LayoutLocalService)_targetObject, (Layout)arguments[0],
						(ServiceContext)arguments[2]);
				}
				else if (arguments.length == 4) {
					deleteLayout(
						(LayoutLocalService)_targetObject, (Long)arguments[0],
						(Boolean)arguments[1], (Long)arguments[2],
						(ServiceContext)arguments[3]);
				}
				else {
					return wrapReturnValue(_invoke(method, arguments), false);
				}
			}
			else if (methodName.equals("getLayouts")) {
				boolean showIncomplete = false;

				if ((arguments.length == 6) &&
					parameterTypes[3].equals(Boolean.TYPE)) {

					showIncomplete = (Boolean)arguments[3];
				}
				else if ((arguments.length == 7) &&
						 parameterTypes[3].equals(Boolean.TYPE)) {

					showIncomplete = (Boolean)arguments[3];
				}
				else if (Arrays.equals(parameterTypes, _GET_LAYOUTS_TYPES)) {
					showIncomplete = true;
				}

				return wrapReturnValue(
					_invoke(method, arguments), showIncomplete);
			}
			else if (methodName.equals("updateLayout") &&
					 ((arguments.length == 15) || (arguments.length == 16) ||
					  (arguments.length == 18))) {

				Map<Locale, String> friendlyURLMap = null;

				if (Arrays.equals(
						parameterTypes, _UPDATE_LAYOUT_PARAMETER_TYPES)) {

					friendlyURLMap = HashMapBuilder.put(
						LocaleUtil.getSiteDefault(), (String)arguments[11]
					).build();
				}
				else {
					friendlyURLMap = (Map<Locale, String>)arguments[11];
				}

				long styleBookEntryId = 0;
				long faviconFileEntryId = 0;
				long masterLayoutPlid = 0;

				ServiceContext serviceContext = null;

				if (arguments.length == 15) {
					serviceContext = (ServiceContext)arguments[14];
				}
				else if (arguments.length == 16) {
					masterLayoutPlid = (Long)arguments[14];

					serviceContext = (ServiceContext)arguments[15];
				}
				else if (arguments.length == 18) {
					styleBookEntryId = (Long)arguments[14];
					faviconFileEntryId = (Long)arguments[15];
					masterLayoutPlid = (Long)arguments[16];

					serviceContext = (ServiceContext)arguments[17];
				}

				returnValue = updateLayout(
					(LayoutLocalService)_targetObject, (Long)arguments[0],
					(Boolean)arguments[1], (Long)arguments[2],
					(Long)arguments[3], (Map<Locale, String>)arguments[4],
					(Map<Locale, String>)arguments[5],
					(Map<Locale, String>)arguments[6],
					(Map<Locale, String>)arguments[7],
					(Map<Locale, String>)arguments[8], (String)arguments[9],
					(Boolean)arguments[10], friendlyURLMap,
					(Boolean)arguments[12], (byte[])arguments[13],
					styleBookEntryId, faviconFileEntryId, masterLayoutPlid,
					serviceContext);
			}
			else {
				if (methodName.equals("updateLayout") &&
					(arguments.length == 11)) {

					updateLookAndFeel(
						(LayoutLocalService)_targetObject, (Long)arguments[0],
						(Boolean)arguments[1], (Long)arguments[2],
						(String)arguments[5], (String)arguments[6],
						(String)arguments[8]);
				}

				try {
					Class<?> clazz = LayoutLocalServiceStagingAdvice.class;

					parameterTypes = ArrayUtil.append(
						new Class<?>[] {LayoutLocalService.class},
						parameterTypes);

					Method layoutLocalServiceStagingAdviceMethod =
						clazz.getMethod(methodName, parameterTypes);

					arguments = ArrayUtil.append(
						new Object[] {_targetObject}, arguments);

					returnValue = layoutLocalServiceStagingAdviceMethod.invoke(
						LayoutLocalServiceStagingAdvice.this, arguments);
				}
				catch (InvocationTargetException invocationTargetException) {
					throw invocationTargetException.getTargetException();
				}
				catch (NoSuchMethodException noSuchMethodException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchMethodException);
					}

					returnValue = _invoke(method, arguments);
				}
			}

			return wrapReturnValue(returnValue, false);
		}

		private LayoutLocalServiceStagingInvocationHandler(
			Object targetObject) {

			_targetObject = targetObject;
		}

		private Object _invoke(Method method, Object[] arguments)
			throws Throwable {

			try {
				return method.invoke(_targetObject, arguments);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getCause();
			}
		}

		private volatile Object _targetObject;

	}

}