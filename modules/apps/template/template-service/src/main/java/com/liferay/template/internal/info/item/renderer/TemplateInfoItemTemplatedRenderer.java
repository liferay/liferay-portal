/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.info.item.renderer;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectVariationProvider;
import com.liferay.info.item.renderer.InfoItemTemplatedRenderer;
import com.liferay.info.item.renderer.template.InfoItemRendererTemplate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.internal.transformer.TemplateDisplayTemplateTransformer;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;
import com.liferay.template.transformer.TemplateNodeFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class TemplateInfoItemTemplatedRenderer<T>
	implements InfoItemTemplatedRenderer<T> {

	public TemplateInfoItemTemplatedRenderer(
		String className, DDMTemplateLocalService ddmTemplateLocalService,
		InfoItemServiceRegistry infoItemServiceRegistry,
		StagingGroupHelper stagingGroupHelper,
		TemplateEntryLocalService templateEntryLocalService,
		TemplateNodeFactory templateNodeFactory) {

		_className = className;
		_ddmTemplateLocalService = ddmTemplateLocalService;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_stagingGroupHelper = stagingGroupHelper;
		_templateEntryLocalService = templateEntryLocalService;
		_templateNodeFactory = templateNodeFactory;
	}

	@Override
	public List<InfoItemRendererTemplate> getInfoItemRendererTemplates(
		String className, String classTypeKey, Locale locale) {

		if (!Objects.equals(_className, className)) {
			return Collections.emptyList();
		}

		List<InfoItemRendererTemplate> infoItemRendererTemplates =
			new ArrayList<>();

		for (TemplateEntry templateEntry :
				_getTemplateEntries(_className, classTypeKey)) {

			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				templateEntry.getDDMTemplateId());

			infoItemRendererTemplates.add(
				new InfoItemRendererTemplate(
					ddmTemplate.getName(locale),
					String.valueOf(templateEntry.getTemplateEntryId())));
		}

		return infoItemRendererTemplates;
	}

	@Override
	public List<InfoItemRendererTemplate> getInfoItemRendererTemplates(
		T t, Locale locale) {

		String infoItemFormVariationKey = StringPool.BLANK;

		InfoItemObjectVariationProvider<T> infoItemObjectVariationProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectVariationProvider.class, _className);

		if (infoItemObjectVariationProvider != null) {
			infoItemFormVariationKey =
				infoItemObjectVariationProvider.getInfoItemFormVariationKey(t);
		}

		return getInfoItemRendererTemplates(
			_className, infoItemFormVariationKey, locale);
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "templates");
	}

	@Override
	public void render(
		T t, String templateEntryId, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return;
		}

		try {
			Writer writer = httpServletResponse.getWriter();

			TemplateEntry templateEntry =
				_templateEntryLocalService.fetchTemplateEntry(
					GetterUtil.getLong(templateEntryId));

			InfoItemFieldValues infoItemFieldValues =
				InfoItemFieldValues.builder(
				).build();

			InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class,
					templateEntry.getInfoItemClassName());

			if (infoItemFieldValuesProvider != null) {
				infoItemFieldValues =
					infoItemFieldValuesProvider.getInfoItemFieldValues(t);
			}

			TemplateDisplayTemplateTransformer
				templateDisplayTemplateTransformer =
					new TemplateDisplayTemplateTransformer(
						templateEntry, infoItemFieldValues,
						_templateNodeFactory);

			writer.write(
				templateDisplayTemplateTransformer.transform(
					serviceContext.getThemeDisplay()));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<TemplateEntry> _getTemplateEntries(
		String infoItemClassName, String infoItemFormVariationKey) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return Collections.emptyList();
		}

		return _templateEntryLocalService.getTemplateEntries(
			PortalUtil.getCurrentAndAncestorSiteGroupIds(
				_stagingGroupHelper.getStagedPortletGroupId(
					serviceContext.getScopeGroupId(),
					TemplatePortletKeys.TEMPLATE)),
			infoItemClassName, infoItemFormVariationKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	private final String _className;
	private final DDMTemplateLocalService _ddmTemplateLocalService;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final StagingGroupHelper _stagingGroupHelper;
	private final TemplateEntryLocalService _templateEntryLocalService;
	private final TemplateNodeFactory _templateNodeFactory;

}