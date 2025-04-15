/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
	service = PortletDataHandler.class
)
public class TemplatePortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "template";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public StagedModelType[] getDeletionSystemEventStagedModelTypes() {
		return ArrayUtil.append(
			_getStagedModelTypes(), new StagedModelType(TemplateEntry.class));
	}

	@Override
	public PortletDataHandlerControl[] getExportControls() {
		return _portletDataHandlerControlsDCLSingleton.getSingleton(
			this::_getPortletDataHandlerControls);
	}

	@Override
	public long getExportModelCount(ManifestSummary manifestSummary) {
		long totalModelCount = -1;

		StagedModelType[] stagedModelTypes = ArrayUtil.append(
			_getStagedModelTypes(), new StagedModelType(TemplateEntry.class));

		for (StagedModelType stagedModelType : stagedModelTypes) {
			long modelCount = manifestSummary.getModelAdditionCount(
				stagedModelType);

			if (modelCount == -1) {
				continue;
			}

			if (totalModelCount == -1) {
				totalModelCount = modelCount;
			}
			else {
				totalModelCount += modelCount;
			}
		}

		return totalModelCount;
	}

	@Override
	public PortletDataHandlerControl[] getImportControls() {
		return _portletDataHandlerControlsDCLSingleton.getSingleton(
			this::_getPortletDataHandlerControls);
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Override
	public PortletDataHandlerControl[] getStagingControls() {
		return _portletDataHandlerControlsDCLSingleton.getSingleton(
			this::_getPortletDataHandlerControls);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		for (long classNameId : TemplateHandlerRegistryUtil.getClassNameIds()) {
			_ddmTemplateLocalService.deleteTemplates(
				portletDataContext.getScopeGroupId(), classNameId);
		}

		_ddmTemplateLocalService.deleteTemplates(
			portletDataContext.getScopeGroupId(),
			_portal.getClassNameId(TemplateEntry.class));

		_templateEntryLocalService.deleteTemplateEntries(
			portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		List<Long> classNameIds = _getClassNameIds(portletDataContext);

		if (!classNameIds.isEmpty()) {
			ActionableDynamicQuery actionableDynamicQuery =
				_getDDMTemplateActionableDynamicQuery(
					portletDataContext, classNameIds.toArray(new Long[0]),
					new StagedModelType(
						_portal.getClassNameId(DDMTemplate.class),
						StagedModelType.REFERRER_CLASS_NAME_ID_ALL));

			actionableDynamicQuery.performActions();
		}

		ActionableDynamicQuery templateEntryExportActionableDynamicQuery =
			_templateEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		templateEntryExportActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		List<Long> classNameIds = _getClassNameIds(portletDataContext);

		Element ddmTemplatesElement =
			portletDataContext.getImportDataGroupElement(DDMTemplate.class);

		List<Element> ddmTemplateElements = ddmTemplatesElement.elements();

		for (Element ddmTemplateElement : ddmTemplateElements) {
			if (classNameIds.contains(
					_portal.getClassNameId(
						ddmTemplateElement.attributeValue(
							"attached-class-name")))) {

				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, ddmTemplateElement);
			}
		}

		Element templateEntriesElement =
			portletDataContext.getImportDataGroupElement(TemplateEntry.class);

		List<Element> templateEntriesElements =
			templateEntriesElement.elements();

		for (Element templateEntryElement : templateEntriesElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, templateEntryElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			StagedModelType[] stagedModelTypes = ArrayUtil.append(
				_getStagedModelTypes(),
				new StagedModelType(TemplateEntry.class));

			_staging.populateLastPublishDateCounts(
				portletDataContext, stagedModelTypes);

			return;
		}

		for (StagedModelType stagedModelType : _getStagedModelTypes()) {
			ActionableDynamicQuery actionableDynamicQuery =
				_getDDMTemplateActionableDynamicQuery(
					portletDataContext,
					new Long[] {stagedModelType.getReferrerClassNameId()},
					stagedModelType);

			actionableDynamicQuery.performCount();
		}

		ActionableDynamicQuery templateEntryExportActionableDynamicQuery =
			_templateEntryStagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		templateEntryExportActionableDynamicQuery.performCount();
	}

	private List<Long> _getClassNameIds(PortletDataContext portletDataContext) {
		List<Long> classNameIds = new ArrayList<>();

		for (TemplateHandler templateHandler :
				TemplateHandlerRegistryUtil.getTemplateHandlers()) {

			ClassName className = _classNameLocalService.fetchClassName(
				templateHandler.getClassName());

			if (className == null) {
				continue;
			}

			if (portletDataContext.getBooleanParameter(
					NAMESPACE,
					templateHandler.getName(LocaleUtil.getSiteDefault()))) {

				classNameIds.add(
					_portal.getClassNameId(templateHandler.getClassName()));
			}
		}

		return classNameIds;
	}

	private ActionableDynamicQuery _getDDMTemplateActionableDynamicQuery(
		PortletDataContext portletDataContext, Long[] classNameIds,
		StagedModelType stagedModelType) {

		ExportActionableDynamicQuery exportActionableDynamicQuery =
			_ddmTemplateLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		ActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
			exportActionableDynamicQuery.getAddCriteriaMethod();

		exportActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				addCriteriaMethod.addCriteria(dynamicQuery);

				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				dynamicQuery.add(classNameIdProperty.in(classNameIds));

				Property classPKProperty = PropertyFactoryUtil.forName(
					"classPK");

				dynamicQuery.add(classPKProperty.eq(0L));

				Property typeProperty = PropertyFactoryUtil.forName("type");

				dynamicQuery.add(
					typeProperty.eq(
						DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY));
			});

		exportActionableDynamicQuery.setStagedModelType(stagedModelType);

		return exportActionableDynamicQuery;
	}

	private PortletDataHandlerControl[] _getPortletDataHandlerControls() {
		List<PortletDataHandlerControl> portletDataHandlerControls =
			new ArrayList<>();

		portletDataHandlerControls.add(
			new PortletDataHandlerBoolean(
				NAMESPACE, "information-templates", true, false, null,
				TemplateEntry.class.getName()));

		for (TemplateHandler templateHandler :
				TemplateHandlerRegistryUtil.getTemplateHandlers()) {

			ClassName className = _classNameLocalService.getClassName(
				templateHandler.getClassName());

			portletDataHandlerControls.add(
				new PortletDataHandlerBoolean(
					NAMESPACE,
					templateHandler.getName(LocaleUtil.getSiteDefault()), true,
					false, null, DDMTemplate.class.getName(),
					className.getValue()));
		}

		return portletDataHandlerControls.toArray(
			new PortletDataHandlerControl[0]);
	}

	private StagedModelType[] _getStagedModelTypes() {
		return getStagedModelTypes(
			() -> {
				List<StagedModelType> stagedModelTypes = new ArrayList<>();

				long ddmTemplateClassNameId = _portal.getClassNameId(
					DDMTemplate.class);

				for (long classNameId :
						TemplateHandlerRegistryUtil.getClassNameIds()) {

					stagedModelTypes.add(
						new StagedModelType(
							ddmTemplateClassNameId, classNameId));
				}

				return stagedModelTypes;
			});
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	private final DCLSingleton<PortletDataHandlerControl[]>
		_portletDataHandlerControlsDCLSingleton = new DCLSingleton<>();

	@Reference
	private Staging _staging;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.template.model.TemplateEntry)"
	)
	private StagedModelRepository<TemplateEntry>
		_templateEntryStagedModelRepository;

}