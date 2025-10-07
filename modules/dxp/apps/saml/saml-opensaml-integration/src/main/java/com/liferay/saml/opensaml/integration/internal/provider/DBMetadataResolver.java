/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.provider;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.internal.util.SamlUtil;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.StringReader;

import java.util.Collections;
import java.util.List;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * @author Mika Koivisto
 */
public class DBMetadataResolver extends AbstractMetadataResolver {

	public DBMetadataResolver(
		ParserPool parserPool,
		SamlIdpSpConnectionLocalService samlIdpSpConnectionLocalService,
		SamlProviderConfigurationHelper samlProviderConfigurationHelper,
		SamlSpIdpConnectionLocalService samlSpIdpConnectionLocalService) {

		setParserPool(parserPool);

		_samlIdpSpConnectionLocalService = samlIdpSpConnectionLocalService;
		_samlProviderConfigurationHelper = samlProviderConfigurationHelper;
		_samlSpIdpConnectionLocalService = samlSpIdpConnectionLocalService;
	}

	@Nonnull
	@Override
	public Iterable<EntityDescriptor> resolve(@Nullable CriteriaSet criteriaSet)
		throws ResolverException {

		if (criteriaSet == null) {
			return Collections.emptyList();
		}

		EntityIdCriterion entityIdCriterion = criteriaSet.get(
			EntityIdCriterion.class);

		if (entityIdCriterion == null) {
			throw new ResolverException("Entity ID criterion is null");
		}

		try {
			EntityDescriptor entityDescriptor = _getEntityDescriptor(
				entityIdCriterion.getEntityId());

			if (isValid(entityDescriptor)) {
				return Collections.singletonList(entityDescriptor);
			}

			return Collections.emptyList();
		}
		catch (Exception exception) {
			throw new ResolverException(exception);
		}
	}

	@Nonnull
	@Override
	protected List<EntityDescriptor> lookupEntityID(@Nonnull String entityID)
		throws ResolverException {

		try {
			EntityDescriptor entityDescriptor = _getEntityDescriptor(entityID);

			if (entityDescriptor == null) {
				return Collections.emptyList();
			}

			return Collections.singletonList(entityDescriptor);
		}
		catch (Exception exception) {
			throw new ResolverException(exception);
		}
	}

	private String _fetchSamlIdpSPMetadataXml(long companyId, String entityId) {
		try {
			SamlIdpSpConnection samlIdpSpConnection =
				_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			if (samlIdpSpConnection.isEnabled()) {
				return samlIdpSpConnection.getMetadataXml();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private String _fetchSamlSpIdpMetadataXml(long companyId, String entityId) {
		try {
			SamlSpIdpConnection samlSpIdpConnection =
				_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
					companyId, entityId);

			if (samlSpIdpConnection.isEnabled()) {
				return samlSpIdpConnection.getMetadataXml();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private EntityDescriptor _getEntityDescriptor(String entityID)
		throws Exception {

		return SamlUtil.getEntityDescriptorById(
			entityID, _getMetadata(entityID));
	}

	private XMLObject _getMetadata(String entityID) throws Exception {
		String metadataXml = _getMetadataXml(entityID);

		if (Validator.isNull(metadataXml)) {
			return null;
		}

		XMLObject metadataXMLObject = XMLObjectSupport.unmarshallFromReader(
			getParserPool(), new StringReader(metadataXml));

		MetadataFilter metadataFilter = getMetadataFilter();

		if (metadataFilter != null) {
			metadataXMLObject = metadataFilter.filter(metadataXMLObject);
		}

		return metadataXMLObject;
	}

	private String _getMetadataXml(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (_samlProviderConfigurationHelper.isRoleIb()) {
			String metadataXml = _fetchSamlSpIdpMetadataXml(
				companyId, entityId);

			if (Validator.isNotNull(metadataXml)) {
				return metadataXml;
			}

			return _fetchSamlIdpSPMetadataXml(companyId, entityId);
		}
		else if (_samlProviderConfigurationHelper.isRoleIdp()) {
			return _fetchSamlIdpSPMetadataXml(companyId, entityId);
		}
		else if (_samlProviderConfigurationHelper.isRoleSp()) {
			return _fetchSamlSpIdpMetadataXml(companyId, entityId);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DBMetadataResolver.class);

	private final SamlIdpSpConnectionLocalService
		_samlIdpSpConnectionLocalService;
	private final SamlProviderConfigurationHelper
		_samlProviderConfigurationHelper;
	private final SamlSpIdpConnectionLocalService
		_samlSpIdpConnectionLocalService;

}