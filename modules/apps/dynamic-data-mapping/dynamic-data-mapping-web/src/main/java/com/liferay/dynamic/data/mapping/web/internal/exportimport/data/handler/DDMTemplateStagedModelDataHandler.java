/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.exportimport.data.handler;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 * @author Daniel Kocsis
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
	service = StagedModelDataHandler.class
)
public class DDMTemplateStagedModelDataHandler
	extends BaseStagedModelDataHandler<DDMTemplate> {

	public static final String[] CLASS_NAMES = {DDMTemplate.class.getName()};

	@Override
	public void deleteStagedModel(DDMTemplate template) throws PortalException {
		_ddmTemplateLocalService.deleteTemplate(template);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		DDMTemplate ddmTemplate = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (ddmTemplate != null) {
			deleteStagedModel(ddmTemplate);
		}
	}

	@Override
	public DDMTemplate fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmTemplateLocalService.fetchDDMTemplateByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<DDMTemplate> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _ddmTemplateLocalService.getDDMTemplatesByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<DDMTemplate>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(DDMTemplate template) {
		return template.getNameCurrentValue();
	}

	@Override
	public Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, DDMTemplate template) {

		Map<String, String> referenceAttributes = HashMapBuilder.put(
			"referenced-class-name", template.getClassName()
		).put(
			"template-key", template.getTemplateKey()
		).build();

		long guestUserId = 0;

		try {
			guestUserId = _userLocalService.getGuestUserId(
				template.getCompanyId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return referenceAttributes;
		}

		referenceAttributes.put(
			"preloaded",
			String.valueOf(_isPreloadedTemplate(guestUserId, template)));

		return referenceAttributes;
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		if (!preloaded) {
			return super.validateMissingReference(uuid, groupId);
		}

		long classNameId = _portal.getClassNameId(
			referenceElement.attributeValue("referenced-class-name"));
		String templateKey = referenceElement.attributeValue("template-key");

		DDMTemplate existingTemplate = _fetchExistingTemplateWithParentGroups(
			uuid, groupId, classNameId, templateKey, preloaded);

		if (existingTemplate == null) {
			return false;
		}

		return true;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, DDMTemplate template)
		throws Exception {

		Element templateElement = portletDataContext.getExportDataElement(
			template);

		DDMStructure structure = _ddmStructureLocalService.fetchStructure(
			template.getClassPK());

		if (structure != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, template, structure,
				PortletDataContext.REFERENCE_TYPE_STRONG);
		}

		if (template.isSmallImage()) {
			if (Validator.isNotNull(template.getSmallImageURL())) {
				String smallImageURL =
					_ddmTemplateExportImportContentProcessor.
						replaceExportContentReferences(
							portletDataContext, template,
							template.getSmallImageURL(), true, true);

				template.setSmallImageURL(smallImageURL);
			}
			else {
				Image smallImage = _imageLocalService.fetchImage(
					template.getSmallImageId());

				if ((smallImage != null) && (smallImage.getTextObj() != null)) {
					String smallImagePath = ExportImportPathUtil.getModelPath(
						template,
						smallImage.getImageId() + StringPool.PERIOD +
							template.getSmallImageType());

					templateElement.addAttribute(
						"small-image-path", smallImagePath);

					template.setSmallImageType(smallImage.getType());

					portletDataContext.addZipEntry(
						smallImagePath, smallImage.getTextObj());
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to export small image ",
								template.getSmallImageId(), " to template ",
								template.getTemplateKey()));
					}

					template.setSmallImage(false);
					template.setSmallImageId(0);
				}
			}
		}

		String script =
			_ddmTemplateExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, template, template.getScript(),
					portletDataContext.getBooleanParameter(
						BaseDDMPortletDataHandler.NAMESPACE,
						"referenced-content"),
					false);

		template.setScript(script);

		if (_isPreloadedTemplate(
				_userLocalService.getGuestUserId(template.getCompanyId()),
				template)) {

			templateElement.addAttribute("preloaded", "true");
		}

		if (template.getResourceClassNameId() > 0) {
			templateElement.addAttribute(
				"resource-class-name",
				_portal.getClassName(template.getResourceClassNameId()));
		}

		portletDataContext.addClassedModel(
			templateElement, ExportImportPathUtil.getModelPath(template),
			template);

		try {
			portletDataContext.addPermissions(
				getResourceName(template), template.getPrimaryKey());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, Element referenceElement)
		throws PortletDataException {

		importMissingGroupReference(portletDataContext, referenceElement);

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		long classNameId = _portal.getClassNameId(
			referenceElement.attributeValue("referenced-class-name"));
		String templateKey = referenceElement.attributeValue("template-key");
		boolean preloaded = GetterUtil.getBoolean(
			referenceElement.attributeValue("preloaded"));

		DDMTemplate existingTemplate;

		if (!preloaded) {
			existingTemplate = fetchMissingReference(uuid, groupId);
		}
		else {
			existingTemplate = _fetchExistingTemplateWithParentGroups(
				uuid, groupId, classNameId, templateKey, preloaded);
		}

		if (existingTemplate == null) {
			return;
		}

		Map<Long, Long> templateIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMTemplate.class);

		long templateId = GetterUtil.getLong(
			referenceElement.attributeValue("class-pk"));

		templateIds.put(templateId, existingTemplate.getTemplateId());

		Map<String, String> templateKeys =
			(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
				DDMTemplate.class + ".ddmTemplateKey");

		templateKeys.put(templateKey, existingTemplate.getTemplateKey());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, DDMTemplate template)
		throws Exception {

		long userId = portletDataContext.getUserId(template.getUserUuid());

		long classPK = template.getClassPK();

		if (classPK > 0) {
			Map<Long, Long> structureIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					DDMStructure.class);

			classPK = MapUtil.getLong(structureIds, classPK, classPK);
		}

		Element element = portletDataContext.getImportDataStagedModelElement(
			template);

		File smallFile = null;

		try {
			if (template.isSmallImage()) {
				String smallImagePath = element.attributeValue(
					"small-image-path");

				if (Validator.isNotNull(template.getSmallImageURL())) {
					String smallImageURL =
						_ddmTemplateExportImportContentProcessor.
							replaceImportContentReferences(
								portletDataContext, template,
								template.getSmallImageURL());

					template.setSmallImageURL(smallImageURL);
				}
				else if (Validator.isNotNull(smallImagePath)) {
					byte[] bytes = portletDataContext.getZipEntryAsByteArray(
						smallImagePath);

					if (bytes != null) {
						smallFile = FileUtil.createTempFile(
							template.getSmallImageType());

						FileUtil.write(smallFile, bytes);
					}
				}
			}

			String script =
				_ddmTemplateExportImportContentProcessor.
					replaceImportContentReferences(
						portletDataContext, template, template.getScript());

			template.setScript(script);

			long resourceClassNameId = 0L;

			if (template.getResourceClassNameId() > 0) {
				String resourceClassName = element.attributeValue(
					"resource-class-name");

				if (Validator.isNotNull(resourceClassName)) {
					resourceClassNameId = _portal.getClassNameId(
						resourceClassName);
				}
			}

			ServiceContext serviceContext =
				portletDataContext.createServiceContext(template);

			DDMTemplate importedTemplate = null;

			if (portletDataContext.isDataStrategyMirror()) {
				boolean preloaded = GetterUtil.getBoolean(
					element.attributeValue("preloaded"));

				DDMTemplate existingTemplate = _fetchExistingTemplate(
					template.getUuid(), portletDataContext.getScopeGroupId(),
					template.getClassNameId(), template.getTemplateKey(),
					preloaded);

				if (existingTemplate == null) {
					serviceContext.setUuid(template.getUuid());

					existingTemplate = _ddmTemplateLocalService.fetchTemplate(
						portletDataContext.getScopeGroupId(),
						template.getClassNameId(), template.getTemplateKey());

					String externalReferenceCode = null;
					String templateKey = null;

					if (existingTemplate == null) {
						externalReferenceCode =
							template.getExternalReferenceCode();
						templateKey = template.getTemplateKey();
					}

					importedTemplate = _ddmTemplateLocalService.addTemplate(
						externalReferenceCode, userId,
						portletDataContext.getScopeGroupId(),
						template.getClassNameId(), classPK, resourceClassNameId,
						templateKey, template.getNameMap(),
						template.getDescriptionMap(), template.getType(),
						template.getMode(), template.getLanguage(),
						template.getScript(), template.isCacheable(),
						template.isSmallImage(), template.getSmallImageURL(),
						smallFile, serviceContext);
				}
				else {
					importedTemplate = _ddmTemplateLocalService.updateTemplate(
						userId, existingTemplate.getTemplateId(), classPK,
						template.getNameMap(), template.getDescriptionMap(),
						template.getType(), template.getMode(),
						template.getLanguage(), template.getScript(),
						template.isCacheable(), template.isSmallImage(),
						template.getSmallImageURL(), smallFile, serviceContext);
				}
			}
			else {
				importedTemplate = _ddmTemplateLocalService.addTemplate(
					null, userId, portletDataContext.getScopeGroupId(),
					template.getClassNameId(), classPK, resourceClassNameId,
					null, template.getNameMap(), template.getDescriptionMap(),
					template.getType(), template.getMode(),
					template.getLanguage(), template.getScript(),
					template.isCacheable(), template.isSmallImage(),
					template.getSmallImageURL(), smallFile, serviceContext);
			}

			portletDataContext.importClassedModel(template, importedTemplate);

			try {
				portletDataContext.importPermissions(
					getResourceName(importedTemplate), template.getPrimaryKey(),
					importedTemplate.getPrimaryKey());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			Map<String, String> ddmTemplateKeys =
				(Map<String, String>)portletDataContext.getNewPrimaryKeysMap(
					DDMTemplate.class + ".ddmTemplateKey");

			ddmTemplateKeys.put(
				template.getTemplateKey(), importedTemplate.getTemplateKey());
		}
		finally {
			if (smallFile != null) {
				smallFile.delete();
			}
		}
	}

	protected String getResourceName(DDMTemplate template)
		throws PortalException {

		return ddmPermissionSupport.getTemplateModelResourceName(
			template.getResourceClassName());
	}

	@Reference
	protected DDMPermissionSupport ddmPermissionSupport;

	private DDMTemplate _fetchExistingTemplate(
		String uuid, long groupId, long classNameId, String templateKey,
		boolean preloaded) {

		DDMTemplate existingTemplate = null;

		if (!preloaded) {
			existingTemplate = fetchStagedModelByUuidAndGroupId(uuid, groupId);
		}
		else {
			existingTemplate = _ddmTemplateLocalService.fetchTemplate(
				groupId, classNameId, templateKey);
		}

		return existingTemplate;
	}

	private DDMTemplate _fetchExistingTemplateWithParentGroups(
		String uuid, long groupId, long classNameId, String templateKey,
		boolean preloaded) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		long companyId = group.getCompanyId();

		while (group != null) {
			DDMTemplate existingTemplate = _fetchExistingTemplate(
				uuid, group.getGroupId(), classNameId, templateKey, preloaded);

			if (existingTemplate != null) {
				return existingTemplate;
			}

			group = group.getParentGroup();
		}

		Group companyGroup = _groupLocalService.fetchCompanyGroup(companyId);

		if (companyGroup == null) {
			return null;
		}

		return _fetchExistingTemplate(
			uuid, companyGroup.getGroupId(), classNameId, templateKey,
			preloaded);
	}

	private boolean _isPreloadedTemplate(
		long guestUserId, DDMTemplate template) {

		if (guestUserId == template.getUserId()) {
			return true;
		}

		DDMTemplateVersion ddmTemplateVersion = null;

		try {
			ddmTemplateVersion =
				_ddmTemplateVersionLocalService.getTemplateVersion(
					template.getTemplateId(),
					DDMTemplateConstants.VERSION_DEFAULT);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if ((ddmTemplateVersion != null) &&
			(guestUserId == ddmTemplateVersion.getUserId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTemplateStagedModelDataHandler.class);

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplate)"
	)
	private ExportImportContentProcessor<String>
		_ddmTemplateExportImportContentProcessor;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}