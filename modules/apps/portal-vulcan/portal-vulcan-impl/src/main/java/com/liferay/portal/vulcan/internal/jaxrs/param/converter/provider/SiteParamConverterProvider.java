/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.param.converter.provider;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.util.GroupUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;

/**
 * @author Javier Gamarra
 */
@Provider
public class SiteParamConverterProvider
	implements ParamConverter<Long>, ParamConverterProvider {

	public SiteParamConverterProvider(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService) {

		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
	}

	@Override
	public Long fromString(String parameter) {
		MultivaluedMap<String, String> multivaluedMap =
			_uriInfo.getPathParameters();

		Long groupId = _getGroupId(multivaluedMap, parameter);

		if (groupId != null) {
			return groupId;
		}

		StringBundler sb = new StringBundler(4);

		sb.append("Unable to get a valid ");

		if (_contains("assetLibraryId", multivaluedMap, parameter)) {
			sb.append("asset library");
		}
		else {
			sb.append("site");
		}

		sb.append(" with ID ");
		sb.append(parameter);

		throw new NotFoundException(sb.toString());
	}

	@Override
	public <T> ParamConverter<T> getConverter(
		Class<T> clazz, Type type, Annotation[] annotations) {

		if (Long.class.equals(clazz) && _hasSiteIdAnnotation(annotations)) {
			return (ParamConverter<T>)this;
		}

		return null;
	}

	public Long getDepotGroupId(String assetLibraryKey, long companyId) {
		if (assetLibraryKey == null) {
			return null;
		}

		return GroupUtil.getDepotGroupId(
			assetLibraryKey, companyId, _depotEntryLocalService,
			_groupLocalService);
	}

	public Long getGroupId(long companyId, String siteKey) {
		if (siteKey == null) {
			return null;
		}

		return GroupUtil.getGroupId(companyId, siteKey, _groupLocalService);
	}

	@Override
	public String toString(Long parameter) {
		return String.valueOf(parameter);
	}

	private boolean _contains(
		String key, MultivaluedMap<String, String> multivaluedMap,
		String value) {

		List<String> values = multivaluedMap.get(key);

		if ((values != null) && values.contains(value)) {
			return true;
		}

		return false;
	}

	private Long _getGroupId(
		MultivaluedMap<String, String> multivaluedMap, String parameterValue) {

		if (_contains("assetLibraryId", multivaluedMap, parameterValue)) {
			return getDepotGroupId(parameterValue, _company.getCompanyId());
		}

		if (_contains("siteId", multivaluedMap, parameterValue)) {
			return getGroupId(_company.getCompanyId(), parameterValue);
		}

		return null;
	}

	private boolean _hasSiteIdAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if ((annotation.annotationType() == PathParam.class) &&
				StringUtils.equalsAny(
					AnnotationUtils.getAnnotationValue(annotation),
					"assetLibraryId", "siteId")) {

				return true;
			}
		}

		return false;
	}

	@Context
	private Company _company;

	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;

	@Context
	private UriInfo _uriInfo;

}