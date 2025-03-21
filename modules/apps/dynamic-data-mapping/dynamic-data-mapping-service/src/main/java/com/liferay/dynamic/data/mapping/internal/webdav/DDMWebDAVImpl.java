/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.webdav;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMXML;
import com.liferay.dynamic.data.mapping.webdav.DDMWebDAV;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.webdav.BaseResourceImpl;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(service = DDMWebDAV.class)
public class DDMWebDAVImpl implements DDMWebDAV {

	@Override
	public int addResource(WebDAVRequest webDAVRequest, long classNameId)
		throws Exception {

		String[] pathArray = webDAVRequest.getPathArray();

		if (pathArray.length != 4) {
			return HttpServletResponse.SC_FORBIDDEN;
		}

		String type = pathArray[2];

		if (type.equals(TYPE_STRUCTURES)) {
			HttpServletRequest httpServletRequest =
				webDAVRequest.getHttpServletRequest();

			String definition = StringUtil.read(
				httpServletRequest.getInputStream());

			DDMForm ddmForm = _getDDMForm(definition);

			DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

			Map<Locale, String> nameMap = HashMapBuilder.put(
				ddmForm.getDefaultLocale(), pathArray[3]
			).build();

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			_ddmStructureLocalService.addStructure(
				webDAVRequest.getUserId(), webDAVRequest.getGroupId(),
				classNameId, nameMap, null, ddmForm, ddmFormLayout,
				StorageType.DEFAULT.toString(), serviceContext);

			return HttpServletResponse.SC_CREATED;
		}
		else if (type.equals(TYPE_TEMPLATES)) {

			// DDM templates can not be added via WebDAV because there is no way
			// to know the associated class name or class PK

			return HttpServletResponse.SC_FORBIDDEN;
		}

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public int deleteResource(
			WebDAVRequest webDAVRequest, String rootPath, String token,
			long classNameId)
		throws WebDAVException {

		try {
			Resource resource = getResource(
				webDAVRequest, rootPath, token, classNameId);

			if (resource == null) {
				return HttpServletResponse.SC_NOT_FOUND;
			}

			Object model = resource.getModel();

			if (model instanceof DDMStructure) {
				DDMStructure structure = (DDMStructure)model;

				_ddmStructureService.deleteStructure(
					structure.getStructureId());

				return HttpServletResponse.SC_NO_CONTENT;
			}
			else if (model instanceof DDMTemplate) {
				DDMTemplate template = (DDMTemplate)model;

				_ddmTemplateService.deleteTemplate(template.getTemplateId());

				return HttpServletResponse.SC_NO_CONTENT;
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public Resource getResource(
			WebDAVRequest webDAVRequest, String rootPath, String token,
			long classNameId)
		throws WebDAVException {

		try {
			String[] pathArray = webDAVRequest.getPathArray();

			if (pathArray.length == 2) {
				String path = rootPath + webDAVRequest.getPath();

				return new BaseResourceImpl(path, StringPool.BLANK, token);
			}
			else if (pathArray.length == 3) {
				String type = pathArray[2];

				return toResource(webDAVRequest, type, rootPath, false);
			}
			else if (pathArray.length == 4) {
				String type = pathArray[2];
				String typeId = pathArray[3];

				if (type.equals(TYPE_STRUCTURES)) {
					DDMStructure structure =
						_ddmStructureLocalService.fetchStructure(
							GetterUtil.getLong(typeId));

					if (structure == null) {
						structure = _ddmStructureLocalService.fetchStructure(
							webDAVRequest.getGroupId(), classNameId, typeId);
					}

					if (structure == null) {
						return null;
					}

					return toResource(
						webDAVRequest, structure, rootPath, false);
				}
				else if (type.equals(TYPE_TEMPLATES)) {
					DDMTemplate template =
						_ddmTemplateLocalService.fetchDDMTemplate(
							GetterUtil.getLong(typeId));

					if (template == null) {
						template = _ddmTemplateLocalService.fetchTemplate(
							webDAVRequest.getGroupId(), classNameId, typeId);
					}

					if (template == null) {
						return null;
					}

					return toResource(webDAVRequest, template, rootPath, false);
				}
			}

			return null;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public int putResource(
			WebDAVRequest webDAVRequest, String rootPath, String token,
			long classNameId)
		throws WebDAVException {

		try {
			Resource resource = getResource(
				webDAVRequest, rootPath, token, classNameId);

			if (resource == null) {
				return addResource(webDAVRequest, classNameId);
			}

			Object model = resource.getModel();

			if (model instanceof DDMStructure) {
				DDMStructure structure = (DDMStructure)model;

				HttpServletRequest httpServletRequest =
					webDAVRequest.getHttpServletRequest();

				String definition = StringUtil.read(
					httpServletRequest.getInputStream());

				DDMForm ddmForm = _getDDMForm(definition);

				DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(
					ddmForm);

				_ddmStructureService.updateStructure(
					structure.getGroupId(), structure.getParentStructureId(),
					structure.getClassNameId(), structure.getStructureKey(),
					structure.getNameMap(), structure.getDescriptionMap(),
					ddmForm, ddmFormLayout, new ServiceContext());

				return HttpServletResponse.SC_CREATED;
			}
			else if (model instanceof DDMTemplate) {
				DDMTemplate template = (DDMTemplate)model;

				HttpServletRequest httpServletRequest =
					webDAVRequest.getHttpServletRequest();

				String script = StringUtil.read(
					httpServletRequest.getInputStream());

				_ddmTemplateService.updateTemplate(
					template.getTemplateId(), template.getClassPK(),
					template.getNameMap(), template.getDescriptionMap(),
					template.getType(), template.getMode(),
					template.getLanguage(), script, template.isCacheable(),
					template.isSmallImage(), template.getSmallImageURL(), null,
					new ServiceContext());

				return HttpServletResponse.SC_CREATED;
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return HttpServletResponse.SC_FORBIDDEN;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	@Override
	public Resource toResource(
		WebDAVRequest webDAVRequest, DDMStructure structure, String rootPath,
		boolean appendPath) {

		String parentPath = rootPath + webDAVRequest.getPath();

		String name = StringPool.BLANK;

		if (appendPath) {
			name = String.valueOf(structure.getStructureId());
		}

		return new DDMStructureResourceImpl(structure, parentPath, name);
	}

	@Override
	public Resource toResource(
		WebDAVRequest webDAVRequest, DDMTemplate template, String rootPath,
		boolean appendPath) {

		String parentPath = rootPath + webDAVRequest.getPath();

		String name = StringPool.BLANK;

		if (appendPath) {
			name = String.valueOf(template.getTemplateId());
		}

		return new DDMTemplateResourceImpl(template, parentPath, name);
	}

	@Override
	public Resource toResource(
		WebDAVRequest webDAVRequest, String type, String rootPath,
		boolean appendPath) {

		String parentPath = rootPath + webDAVRequest.getPath();

		String name = StringPool.BLANK;

		if (appendPath) {
			name = type;
		}

		Resource resource = new BaseResourceImpl(parentPath, name, type);

		resource.setModel(type);

		return resource;
	}

	private DDMForm _getDDMForm(String definition) throws PortalException {
		_ddmXML.validateXML(definition);

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				definition);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_xsdDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private static final Log _log = LogFactoryUtil.getLog(DDMWebDAVImpl.class);

	@Reference
	private DDM _ddm;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private DDMXML _ddmXML;

	@Reference(target = "(ddm.form.deserializer.type=xsd)")
	private DDMFormDeserializer _xsdDDMFormDeserializer;

}