/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.util;

import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.exception.LayoutConvertException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.BulkLayoutConverter;
import com.liferay.layout.util.template.LayoutConversionResult;
import com.liferay.layout.util.template.LayoutConverter;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.layout.util.template.LayoutData;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.PortletDecorator;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ReadOnlyException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = BulkLayoutConverter.class)
public class BulkLayoutConverterImpl implements BulkLayoutConverter {

	@Override
	public void convertLayout(long plid) throws PortalException {
		_convertLayout(plid);
	}

	@Override
	public long[] convertLayouts(long groupId) throws PortalException {
		return convertLayouts(getConvertibleLayoutPlids(groupId));
	}

	@Override
	public long[] convertLayouts(long[] plids) {
		List<Long> convertedLayoutPlids = new ArrayList<>();

		for (long plid : plids) {
			try {
				Callable<Layout> callable = new ConvertLayoutCallable(plid);

				Layout layout = TransactionInvokerUtil.invoke(
					_transactionConfig, callable);

				convertedLayoutPlids.add(layout.getPlid());
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						String.format(
							"Layout with PLID %s cannot be converted", plid),
						throwable);
				}
			}
		}

		return ArrayUtil.toLongArray(convertedLayoutPlids);
	}

	@Override
	public LayoutConversionResult generatePreviewLayout(
			long plid, Locale locale)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			throw new LayoutConvertException(
				"Layout with PLID " + layout.getPlid() + " is not convertible");
		}

		LayoutConverter layoutConverter = _getLayoutConversionResult(layout);

		if (!layoutConverter.isConvertible(layout)) {
			throw new LayoutConvertException(
				"Layout with PLID " + layout.getPlid() + " is not convertible");
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		serviceContext.setScopeGroupId(layout.getGroupId());

		User user = _userLocalService.fetchUser(layout.getUserId());

		if (user != null) {
			serviceContext.setUserId(user.getUserId());
		}

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			Layout draftLayout = _getOrCreateDraftLayout(
				layout, serviceContext);

			LayoutConversionResult layoutConversionResult =
				layoutConverter.convert(draftLayout, locale);

			_addOrUpdateLayoutPageTemplateStructure(
				draftLayout, layoutConversionResult.getLayoutData(),
				serviceContext);

			draftLayout = _layoutLocalService.fetchLayout(
				draftLayout.getPlid());

			_updatePortletDecorator(draftLayout);

			return LayoutConversionResult.of(
				null, layoutConversionResult.getConversionWarningMessages(),
				draftLayout);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Override
	public long[] getConvertibleLayoutPlids(long groupId)
		throws PortalException {

		List<Long> convertibleLayoutPlids = new ArrayList<>();

		ActionableDynamicQuery actionableDynamicQuery =
			_layoutLocalService.getActionableDynamicQuery();

		if (groupId > 0) {
			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					dynamicQuery.add(
						RestrictionsFactoryUtil.eq("groupId", groupId));
					dynamicQuery.add(
						RestrictionsFactoryUtil.eq(
							"type", LayoutConstants.TYPE_PORTLET));

					Property property = PropertyFactoryUtil.forName("system");

					dynamicQuery.add(property.eq(false));
				});
			actionableDynamicQuery.setPerformActionMethod(
				(Layout layout) -> {
					UnicodeProperties typeSettingsUnicodeProperties =
						layout.getTypeSettingsProperties();

					String layoutTemplateId =
						typeSettingsUnicodeProperties.getProperty(
							LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

					if (layoutTemplateId != null) {
						LayoutConverter layoutConverter =
							_layoutConverterRegistry.getLayoutConverter(
								layoutTemplateId);

						if ((layoutConverter != null) &&
							layoutConverter.isConvertible(layout)) {

							convertibleLayoutPlids.add(layout.getPlid());
						}
					}
				});
		}

		actionableDynamicQuery.performActions();

		return ArrayUtil.toLongArray(convertibleLayoutPlids);
	}

	private void _addOrUpdateLayoutPageTemplateStructure(
			Layout layout, LayoutData layoutData, ServiceContext serviceContext)
		throws Exception {

		JSONObject layoutDataJSONObject = layoutData.getLayoutDataJSONObject();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			_layoutPageTemplateStructureLocalService.
				addLayoutPageTemplateStructure(
					serviceContext.getUserId(), layout.getGroupId(),
					layout.getPlid(), defaultSegmentsExperienceId,
					layoutDataJSONObject.toString(), serviceContext);
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				layout.getGroupId(), layout.getPlid(),
				defaultSegmentsExperienceId, layoutDataJSONObject.toString());
	}

	private Layout _convertLayout(long plid) throws PortalException {
		try {
			LayoutConversionResult layoutConversionResult =
				generatePreviewLayout(plid, LocaleUtil.getSiteDefault());

			Layout draftLayout = layoutConversionResult.getDraftLayout();

			Layout layout = _layoutLocalService.getLayout(
				draftLayout.getClassPK());

			_layoutLocalService.copyLayoutContent(draftLayout, layout);

			draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

			draftLayout.setLayoutPrototypeLinkEnabled(false);

			UnicodeProperties typeSettingsUnicodeProperties =
				draftLayout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED,
				Boolean.TRUE.toString());

			draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

			draftLayout = _layoutLocalService.updateLayout(draftLayout);

			layout = _layoutLocalService.getLayout(layout.getPlid());

			layout.setType(draftLayout.getType());
			layout.setLayoutPrototypeUuid(StringPool.BLANK);
			layout.setLayoutPrototypeLinkEnabled(false);
			layout.setStatus(WorkflowConstants.STATUS_APPROVED);

			return _layoutLocalService.updateLayout(layout);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private String _getDefaultPortletDecoratorId(Layout layout)
		throws Exception {

		Theme theme = layout.getTheme();

		List<PortletDecorator> filteredPortletDecorators = ListUtil.filter(
			theme.getPortletDecorators(),
			portletDecorator -> portletDecorator.isDefaultPortletDecorator());

		if (ListUtil.isEmpty(filteredPortletDecorators)) {
			return StringPool.BLANK;
		}

		PortletDecorator defaultPortletDecorator =
			filteredPortletDecorators.get(0);

		return defaultPortletDecorator.getPortletDecoratorId();
	}

	private LayoutConverter _getLayoutConversionResult(Layout layout)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		String layoutTemplateId = typeSettingsUnicodeProperties.getProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

		if (Validator.isNull(layoutTemplateId)) {
			throw new LayoutConvertException(
				"Layout template ID cannot be null");
		}

		return _layoutConverterRegistry.getLayoutConverter(layoutTemplateId);
	}

	private Layout _getOrCreateDraftLayout(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		if (layout.isDraftLayout()) {
			throw new PortalException(
				StringBundler.concat(
					"Layout with PLID ", layout.getPlid(),
					" is a draft. You cannot get the draft of a draft."));
		}

		Layout draftLayout = layout.fetchDraftLayout();

		long userId = serviceContext.getUserId();

		User user = _userLocalService.fetchUser(layout.getUserId());

		if (user != null) {
			userId = user.getUserId();
		}

		if (draftLayout == null) {
			draftLayout = _layoutLocalService.addLayout(
				null, userId, layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId(),
				_classNameLocalService.getClassNameId(Layout.class),
				layout.getPlid(), layout.getNameMap(), layout.getTitleMap(),
				layout.getDescriptionMap(), layout.getKeywordsMap(),
				layout.getRobotsMap(), LayoutConstants.TYPE_CONTENT,
				layout.getTypeSettings(), true, true, Collections.emptyMap(),
				layout.getMasterLayoutPlid(), serviceContext);
		}

		try {
			return _layoutLocalService.copyLayoutContent(layout, draftLayout);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private void _updatePortletDecorator(Layout layout) throws Exception {
		String defaultPortletDecoratorId = _getDefaultPortletDecoratorId(
			layout);

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			String portletSetupPortletDecoratorId =
				jxPortletPreferences.getValue(
					"portletSetupPortletDecoratorId", StringPool.BLANK);

			if (Validator.isNotNull(portletSetupPortletDecoratorId)) {
				continue;
			}

			try {
				jxPortletPreferences.setValue(
					"portletSetupPortletDecoratorId",
					defaultPortletDecoratorId);
			}
			catch (ReadOnlyException readOnlyException) {
				throw new PortalException(readOnlyException);
			}

			_portletPreferencesLocalService.updatePreferences(
				portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(), portletPreferences.getPlid(),
				portletPreferences.getPortletId(), jxPortletPreferences);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BulkLayoutConverterImpl.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private LayoutConverterRegistry _layoutConverterRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class ConvertLayoutCallable implements Callable<Layout> {

		@Override
		public Layout call() throws PortalException {
			return _convertLayout(_layoutPlid);
		}

		private ConvertLayoutCallable(Long layoutPlid) {
			_layoutPlid = layoutPlid;
		}

		private final long _layoutPlid;

	}

}