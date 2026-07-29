/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.rest.dto.v1_0.Autofix;
import com.liferay.seo.studio.rest.internal.auto.fix.AutofixTracker;
import com.liferay.seo.studio.rest.internal.web.cache.SEOStudioInstanceAccessTokenWebCacheItem;
import com.liferay.seo.studio.rest.resource.v1_0.AutofixResource;
import com.liferay.seo.studio.spi.autofix.AutofixResult;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/autofix.properties",
	scope = ServiceScope.PROTOTYPE, service = AutofixResource.class
)
public class AutofixResourceImpl extends BaseAutofixResourceImpl {

	@Override
	public Response postAutofix(Autofix autofix) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-44511")) {

			throw new NotFoundException("SEO Studio is not enabled");
		}

		com.liferay.seo.studio.spi.autofix.Autofix spiAutofix =
			_autofixTracker.getAutofix(autofix.getInsightType());

		if (spiAutofix == null) {
			throw new BadRequestException(
				"Unable to apply the fix for an unrecognized insight type");
		}

		URI uri = new URI(autofix.getPageURL());

		ObjectEntry objectEntry = _getObjectEntry(
			contextCompany.getCompanyId(), "L_SEO_STUDIO_INSTANCE",
			uri.getAuthority());

		if (objectEntry == null) {
			throw new NotFoundException(
				"No SEO Studio instance is registered for the page host");
		}

		if (!_objectEntryService.hasModelResourcePermission(
				contextUser, objectEntry.getObjectEntryId(),
				ActionKeys.UPDATE)) {

			throw new PrincipalException.MustHavePermission(
				contextUser.getUserId(), ObjectEntry.class.getName(),
				objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
		}

		Map<String, Serializable> values = objectEntry.getValues();

		String baseURL = uri.getScheme() + "://" + uri.getAuthority();

		String accessToken = SEOStudioInstanceAccessTokenWebCacheItem.get(
			baseURL, GetterUtil.getString(values.get("clientId")),
			GetterUtil.getString(values.get("clientSecret")),
			contextCompany.getCompanyId());

		if (Validator.isNull(accessToken)) {
			throw new InternalServerErrorException(
				"Unable to authenticate against the customer instance");
		}

		AutofixResult autofixResult = spiAutofix.apply(
			accessToken, baseURL,
			GetterUtil.getString(values.get("siteExternalReferenceCode")),
			uri.getPath(), autofix.getValue());

		if (!autofixResult.isSuccess()) {
			throw new InternalServerErrorException(autofixResult.getMessage());
		}

		return Response.noContent(
		).build();
	}

	private ObjectEntry _getObjectEntry(
			long companyId, String externalReferenceCode, String hostname)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> values = objectEntry.getValues();

			if (Objects.equals(
					GetterUtil.getString(values.get("hostname")), hostname)) {

				return objectEntry;
			}
		}

		return null;
	}

	@Reference
	private AutofixTracker _autofixTracker;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

}