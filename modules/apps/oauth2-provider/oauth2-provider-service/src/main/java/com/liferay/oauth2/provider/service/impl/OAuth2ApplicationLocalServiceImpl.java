/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.impl;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.exception.DuplicateOAuth2ApplicationClientIdException;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationClientGrantTypeException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationHomePageURLException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationHomePageURLSchemeException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationJWKSException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationNameException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationPrivacyPolicyURLException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationPrivacyPolicyURLSchemeException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIFragmentException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIMissingException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURIPathException;
import com.liferay.oauth2.provider.exception.OAuth2ApplicationRedirectURISchemeException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.redirect.OAuth2RedirectURIInterpolator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.oauth2.provider.service.base.OAuth2ApplicationLocalServiceBaseImpl;
import com.liferay.oauth2.provider.util.OAuth2JWKValidatorUtil;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.oauth2.provider.util.builder.OAuth2ScopeBuilder;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth2.provider.model.OAuth2Application",
	service = AopService.class
)
public class OAuth2ApplicationLocalServiceImpl
	extends OAuth2ApplicationLocalServiceBaseImpl {

	@Override
	public OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			Consumer<OAuth2ScopeBuilder> builderConsumer,
			ServiceContext serviceContext)
		throws PortalException {

		if (allowedGrantTypesList == null) {
			allowedGrantTypesList = new ArrayList<>();
		}

		if (Validator.isBlank(clientAuthenticationMethod)) {
			clientAuthenticationMethod =
				OAuthConstants.TOKEN_ENDPOINT_AUTH_POST;
		}

		if (Validator.isBlank(clientId)) {
			clientId = OAuth2SecureRandomGenerator.generateClientId();
		}
		else {
			clientId = StringUtil.trim(clientId);
		}

		homePageURL = StringUtil.trim(homePageURL);
		name = StringUtil.trim(name);
		privacyPolicyURL = StringUtil.trim(privacyPolicyURL);

		if (redirectURIsList == null) {
			redirectURIsList = new ArrayList<>();
		}

		_validate(
			companyId, allowedGrantTypesList, clientAuthenticationMethod,
			clientId, clientSecret, homePageURL, jwks, name, privacyPolicyURL,
			redirectURIsList);

		long oAuth2ApplicationId = counterLocalService.increment(
			OAuth2Application.class.getName());

		User user = _userLocalService.getUser(clientCredentialUserId);

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.create(oAuth2ApplicationId);

		oAuth2Application.setCompanyId(companyId);
		oAuth2Application.setUserId(userId);
		oAuth2Application.setUserName(userName);
		oAuth2Application.setCreateDate(new Date());
		oAuth2Application.setModifiedDate(new Date());
		oAuth2Application.setAllowedGrantTypesList(allowedGrantTypesList);
		oAuth2Application.setClientAuthenticationMethod(
			clientAuthenticationMethod);
		oAuth2Application.setClientCredentialUserId(user.getUserId());
		oAuth2Application.setClientCredentialUserName(user.getScreenName());
		oAuth2Application.setClientId(clientId);
		oAuth2Application.setClientProfile(clientProfile);
		oAuth2Application.setClientSecret(clientSecret);
		oAuth2Application.setDescription(description);
		oAuth2Application.setFeaturesList(featuresList);
		oAuth2Application.setHomePageURL(homePageURL);
		oAuth2Application.setIconFileEntryId(iconFileEntryId);
		oAuth2Application.setJwks(jwks);
		oAuth2Application.setName(name);
		oAuth2Application.setPrivacyPolicyURL(privacyPolicyURL);
		oAuth2Application.setRedirectURIsList(redirectURIsList);
		oAuth2Application.setRememberDevice(rememberDevice);
		oAuth2Application.setTrustedApplication(trustedApplication);

		if (builderConsumer != null) {
			OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
				_oAuth2ApplicationScopeAliasesLocalService.
					addOAuth2ApplicationScopeAliases(
						companyId, userId, userName, oAuth2ApplicationId,
						builderConsumer);

			oAuth2Application.setOAuth2ApplicationScopeAliasesId(
				oAuth2ApplicationScopeAliases.
					getOAuth2ApplicationScopeAliasesId());
		}

		_resourceLocalService.addResources(
			oAuth2Application.getCompanyId(), 0, oAuth2Application.getUserId(),
			OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(), false, false, false);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication, ServiceContext serviceContext)
		throws PortalException {

		if (allowedGrantTypesList == null) {
			allowedGrantTypesList = new ArrayList<>();
		}

		if (Validator.isBlank(clientAuthenticationMethod)) {
			clientAuthenticationMethod =
				OAuthConstants.TOKEN_ENDPOINT_AUTH_POST;
		}

		if (Validator.isBlank(clientId)) {
			clientId = OAuth2SecureRandomGenerator.generateClientId();
		}
		else {
			clientId = StringUtil.trim(clientId);
		}

		homePageURL = StringUtil.trim(homePageURL);
		name = StringUtil.trim(name);
		privacyPolicyURL = StringUtil.trim(privacyPolicyURL);

		if (redirectURIsList == null) {
			redirectURIsList = new ArrayList<>();
		}

		if (scopeAliasesList == null) {
			scopeAliasesList = new ArrayList<>();
		}

		_validate(
			companyId, allowedGrantTypesList, clientAuthenticationMethod,
			clientId, clientSecret, homePageURL, jwks, name, privacyPolicyURL,
			redirectURIsList);

		long oAuth2ApplicationId = counterLocalService.increment(
			OAuth2Application.class.getName());

		User user = _userLocalService.getUser(clientCredentialUserId);

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.create(oAuth2ApplicationId);

		oAuth2Application.setCompanyId(companyId);
		oAuth2Application.setUserId(userId);
		oAuth2Application.setUserName(userName);
		oAuth2Application.setCreateDate(new Date());
		oAuth2Application.setModifiedDate(new Date());
		oAuth2Application.setAllowedGrantTypesList(allowedGrantTypesList);
		oAuth2Application.setClientAuthenticationMethod(
			clientAuthenticationMethod);
		oAuth2Application.setClientCredentialUserId(user.getUserId());
		oAuth2Application.setClientCredentialUserName(user.getScreenName());
		oAuth2Application.setClientId(clientId);
		oAuth2Application.setClientProfile(clientProfile);
		oAuth2Application.setClientSecret(clientSecret);
		oAuth2Application.setDescription(description);
		oAuth2Application.setFeaturesList(featuresList);
		oAuth2Application.setHomePageURL(homePageURL);
		oAuth2Application.setIconFileEntryId(iconFileEntryId);
		oAuth2Application.setJwks(jwks);
		oAuth2Application.setName(name);
		oAuth2Application.setPrivacyPolicyURL(privacyPolicyURL);
		oAuth2Application.setRedirectURIsList(redirectURIsList);
		oAuth2Application.setRememberDevice(rememberDevice);
		oAuth2Application.setTrustedApplication(trustedApplication);

		if (ListUtil.isNotEmpty(scopeAliasesList)) {
			OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
				_oAuth2ApplicationScopeAliasesLocalService.
					addOAuth2ApplicationScopeAliases(
						companyId, userId, userName, oAuth2ApplicationId,
						scopeAliasesList);

			oAuth2Application.setOAuth2ApplicationScopeAliasesId(
				oAuth2ApplicationScopeAliases.
					getOAuth2ApplicationScopeAliasesId());
		}

		_resourceLocalService.addResources(
			oAuth2Application.getCompanyId(), 0, oAuth2Application.getUserId(),
			OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(), false, false, false);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			Consumer<OAuth2ScopeBuilder> builderConsumer,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		OAuth2Application oAuth2Application =
			fetchOAuth2ApplicationByExternalReferenceCode(
				externalReferenceCode, user.getCompanyId());

		if (oAuth2Application != null) {
			long oAuth2ApplicationScopeAliasesId =
				oAuth2Application.getOAuth2ApplicationScopeAliasesId();

			if (builderConsumer != null) {
				OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
					_oAuth2ApplicationScopeAliasesLocalService.
						addOAuth2ApplicationScopeAliases(
							user.getCompanyId(), userId, userName,
							oAuth2Application.getOAuth2ApplicationId(),
							builderConsumer);

				oAuth2ApplicationScopeAliasesId =
					oAuth2ApplicationScopeAliases.
						getOAuth2ApplicationScopeAliasesId();
			}

			return updateOAuth2Application(
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2ApplicationScopeAliasesId, allowedGrantTypesList,
				clientAuthenticationMethod, clientCredentialUserId, clientId,
				clientProfile, clientSecret, description, featuresList,
				homePageURL, iconFileEntryId, jwks, name, privacyPolicyURL,
				redirectURIsList, rememberDevice, trustedApplication);
		}

		oAuth2Application = addOAuth2Application(
			user.getCompanyId(), userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, trustedApplication, builderConsumer,
			serviceContext);

		oAuth2Application.setExternalReferenceCode(externalReferenceCode);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		OAuth2Application oAuth2Application =
			fetchOAuth2ApplicationByExternalReferenceCode(
				externalReferenceCode, user.getCompanyId());

		if (oAuth2Application != null) {
			long oAuth2ApplicationScopeAliasesId =
				oAuth2Application.getOAuth2ApplicationScopeAliasesId();

			if (scopeAliasesList != null) {
				oAuth2ApplicationScopeAliasesId =
					_getOAuth2ApplicationScopeAliasesId(
						oAuth2Application.getCompanyId(), userId, userName,
						oAuth2Application.getOAuth2ApplicationId(),
						scopeAliasesList);
			}

			return updateOAuth2Application(
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2ApplicationScopeAliasesId, allowedGrantTypesList,
				clientAuthenticationMethod, clientCredentialUserId, clientId,
				clientProfile, clientSecret, description, featuresList,
				homePageURL, iconFileEntryId, jwks, name, privacyPolicyURL,
				redirectURIsList, rememberDevice, trustedApplication);
		}

		oAuth2Application = addOAuth2Application(
			user.getCompanyId(), userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, scopeAliasesList, trustedApplication,
			serviceContext);

		oAuth2Application.setExternalReferenceCode(externalReferenceCode);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application deleteOAuth2Application(long oAuth2ApplicationId)
		throws PortalException {

		OAuth2Application oAuth2Application = fetchOAuth2Application(
			oAuth2ApplicationId);

		return deleteOAuth2Application(oAuth2Application);
	}

	@Override
	public OAuth2Application deleteOAuth2Application(
			OAuth2Application oAuth2Application)
		throws PortalException {

		oAuth2Application = oAuth2ApplicationPersistence.remove(
			oAuth2Application);

		_resourceLocalService.deleteResource(
			oAuth2Application, ResourceConstants.SCOPE_INDIVIDUAL);

		List<OAuth2Authorization> oAuth2Authorizations =
			_oAuth2AuthorizationLocalService.getOAuth2Authorizations(
				oAuth2Application.getOAuth2ApplicationId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (OAuth2Authorization oAuth2Authorization : oAuth2Authorizations) {
			_oAuth2AuthorizationLocalService.deleteOAuth2Authorization(
				oAuth2Authorization.getOAuth2AuthorizationId());
		}

		List<OAuth2ApplicationScopeAliases> oAuth2ApplicationScopeAliaseses =
			_oAuth2ApplicationScopeAliasesLocalService.
				getOAuth2ApplicationScopeAliaseses(
					oAuth2Application.getOAuth2ApplicationId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases :
				oAuth2ApplicationScopeAliaseses) {

			_oAuth2ApplicationScopeAliasesLocalService.
				deleteOAuth2ApplicationScopeAliases(
					oAuth2ApplicationScopeAliases.
						getOAuth2ApplicationScopeAliasesId());
		}

		return oAuth2Application;
	}

	@Override
	public void deleteOAuth2Applications(long companyId)
		throws PortalException {

		for (OAuth2Application oAuth2Application :
				oAuth2ApplicationPersistence.findByCompanyId(companyId)) {

			deleteOAuth2Application(oAuth2Application.getOAuth2ApplicationId());
		}
	}

	@Override
	public OAuth2Application fetchOAuth2Application(
		long companyId, String clientId) {

		return oAuth2ApplicationPersistence.fetchByC_C(companyId, clientId);
	}

	@Override
	public OAuth2Application getOAuth2Application(
			long companyId, String clientId)
		throws NoSuchOAuth2ApplicationException {

		return oAuth2ApplicationPersistence.findByC_C(companyId, clientId);
	}

	@Override
	public List<OAuth2Application> getOAuth2Applications(long companyId) {
		return oAuth2ApplicationPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<OAuth2Application> getOAuth2Applications(
		long companyId, int clientProfile) {

		return oAuth2ApplicationPersistence.findByC_CP(
			companyId, clientProfile);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public OAuth2Application updateExternalReferenceCode(
			long oAuth2ApplicationId, String externalReferenceCode)
		throws PortalException {

		return updateExternalReferenceCode(
			getOAuth2Application(oAuth2ApplicationId), externalReferenceCode);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public OAuth2Application updateExternalReferenceCode(
			OAuth2Application oAuth2Application, String externalReferenceCode)
		throws PortalException {

		if (Objects.equals(
				oAuth2Application.getExternalReferenceCode(),
				externalReferenceCode)) {

			return oAuth2Application;
		}

		oAuth2Application.setExternalReferenceCode(externalReferenceCode);

		return updateOAuth2Application(oAuth2Application);
	}

	@Override
	public OAuth2Application updateIcon(
			long oAuth2ApplicationId, InputStream inputStream)
		throws PortalException {

		OAuth2Application oAuth2Application = getOAuth2Application(
			oAuth2ApplicationId);

		long oldIconFileEntryId = oAuth2Application.getIconFileEntryId();

		if (inputStream == null) {
			if (oldIconFileEntryId > 0) {
				_portletFileRepository.deletePortletFileEntry(
					oldIconFileEntryId);

				oAuth2Application.setIconFileEntryId(-1);

				oAuth2Application = updateOAuth2Application(oAuth2Application);
			}

			return oAuth2Application;
		}

		Group group = _groupLocalService.getCompanyGroup(
			oAuth2Application.getCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);

		Repository repository = _portletFileRepository.addPortletRepository(
			group.getGroupId(), OAuth2ProviderConstants.SERVICE_NAME,
			serviceContext);

		Folder folder = _portletFileRepository.addPortletFolder(
			_userLocalService.getGuestUserId(oAuth2Application.getCompanyId()),
			repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "icons",
			serviceContext);

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try {
			ImageBag imageBag = ImageToolUtil.read(inputStream);

			RenderedImage renderedImage = imageBag.getRenderedImage();

			if (renderedImage == null) {
				throw new ImageTypeException("Unable to read icon");
			}

			renderedImage = ImageToolUtil.scale(renderedImage, 160, 160);

			ImageToolUtil.write(
				renderedImage, imageBag.getType(), unsyncByteArrayOutputStream);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}

		String fileName = _portletFileRepository.getUniqueFileName(
			group.getGroupId(), folder.getFolderId(),
			oAuth2Application.getClientId());

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			null, group.getGroupId(), oAuth2Application.getUserId(),
			OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(),
			OAuth2ProviderConstants.SERVICE_NAME, folder.getFolderId(),
			new UnsyncByteArrayInputStream(
				unsyncByteArrayOutputStream.toByteArray()),
			fileName, null, false);

		oAuth2Application.setIconFileEntryId(fileEntry.getFileEntryId());

		oAuth2Application = updateOAuth2Application(oAuth2Application);

		if (oldIconFileEntryId > 0) {
			_portletFileRepository.deletePortletFileEntry(oldIconFileEntryId);
		}

		return oAuth2Application;
	}

	@Override
	public OAuth2Application updateOAuth2Application(
			long oAuth2ApplicationId, long oAuth2ApplicationScopeAliasesId,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication)
		throws PortalException {

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.findByPrimaryKey(oAuth2ApplicationId);

		if (Validator.isBlank(clientAuthenticationMethod)) {
			clientAuthenticationMethod =
				OAuthConstants.TOKEN_ENDPOINT_AUTH_POST;
		}

		clientId = StringUtil.trim(clientId);
		homePageURL = StringUtil.trim(homePageURL);
		name = StringUtil.trim(name);
		privacyPolicyURL = StringUtil.trim(privacyPolicyURL);

		if (redirectURIsList == null) {
			redirectURIsList = new ArrayList<>();
		}

		_validate(
			oAuth2Application.getCompanyId(), oAuth2ApplicationId,
			allowedGrantTypesList, clientAuthenticationMethod, clientId,
			clientSecret, homePageURL, jwks, name, privacyPolicyURL,
			redirectURIsList);

		User user = _userLocalService.getUser(clientCredentialUserId);

		oAuth2Application.setModifiedDate(new Date());
		oAuth2Application.setOAuth2ApplicationScopeAliasesId(
			oAuth2ApplicationScopeAliasesId);
		oAuth2Application.setAllowedGrantTypesList(allowedGrantTypesList);
		oAuth2Application.setClientAuthenticationMethod(
			clientAuthenticationMethod);
		oAuth2Application.setClientCredentialUserId(user.getUserId());
		oAuth2Application.setClientCredentialUserName(user.getScreenName());
		oAuth2Application.setClientId(clientId);
		oAuth2Application.setClientProfile(clientProfile);
		oAuth2Application.setClientSecret(clientSecret);
		oAuth2Application.setDescription(description);
		oAuth2Application.setFeaturesList(featuresList);
		oAuth2Application.setHomePageURL(homePageURL);
		oAuth2Application.setIconFileEntryId(iconFileEntryId);
		oAuth2Application.setJwks(jwks);
		oAuth2Application.setName(name);
		oAuth2Application.setPrivacyPolicyURL(privacyPolicyURL);
		oAuth2Application.setRedirectURIsList(redirectURIsList);
		oAuth2Application.setRememberDevice(rememberDevice);
		oAuth2Application.setTrustedApplication(trustedApplication);

		return oAuth2ApplicationPersistence.update(oAuth2Application);
	}

	@Override
	public OAuth2Application updateScopeAliases(
			long userId, String userName, long oAuth2ApplicationId,
			List<String> scopeAliasesList)
		throws PortalException {

		OAuth2Application oAuth2Application =
			oAuth2ApplicationPersistence.findByPrimaryKey(oAuth2ApplicationId);

		long oAuth2ApplicationScopeAliasesId =
			_getOAuth2ApplicationScopeAliasesId(
				oAuth2Application.getCompanyId(), userId, userName,
				oAuth2Application.getOAuth2ApplicationId(), scopeAliasesList);

		if (oAuth2Application.getOAuth2ApplicationScopeAliasesId() !=
				oAuth2ApplicationScopeAliasesId) {

			oAuth2Application.setModifiedDate(new Date());
			oAuth2Application.setOAuth2ApplicationScopeAliasesId(
				oAuth2ApplicationScopeAliasesId);

			return oAuth2ApplicationPersistence.update(oAuth2Application);
		}

		return oAuth2Application;
	}

	@Activate
	protected void activate() {
		String ianaRegisteredSchemes = PropsUtil.get(
			"iana.registered.uri.schemes");

		if (!Validator.isBlank(ianaRegisteredSchemes)) {
			_ianaRegisteredUriSchemes = new HashSet<>(
				Arrays.asList(StringUtil.split(ianaRegisteredSchemes)));
		}
	}

	private long _getOAuth2ApplicationScopeAliasesId(
			long companyId, long userId, String userName,
			long oAuth2ApplicationId, List<String> scopeAliasesList)
		throws PortalException {

		if (ListUtil.isEmpty(scopeAliasesList)) {
			return 0;
		}

		OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
			_oAuth2ApplicationScopeAliasesLocalService.
				fetchOAuth2ApplicationScopeAliases(
					oAuth2ApplicationId, scopeAliasesList);

		if (oAuth2ApplicationScopeAliases != null) {
			oAuth2ApplicationScopeAliases.setUserId(userId);
			oAuth2ApplicationScopeAliases.setUserName(userName);

			oAuth2ApplicationScopeAliases =
				_oAuth2ApplicationScopeAliasesLocalService.
					updateOAuth2ApplicationScopeAliases(
						oAuth2ApplicationScopeAliases);
		}
		else {
			oAuth2ApplicationScopeAliases =
				_oAuth2ApplicationScopeAliasesLocalService.
					addOAuth2ApplicationScopeAliases(
						companyId, userId, userName, oAuth2ApplicationId,
						scopeAliasesList);
		}

		return oAuth2ApplicationScopeAliases.
			getOAuth2ApplicationScopeAliasesId();
	}

	private void _validate(
			long companyId, List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, String clientId,
			String clientSecret, String homePageURL, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList)
		throws PortalException {

		_validate(
			companyId, 0, allowedGrantTypesList, clientAuthenticationMethod,
			clientId, clientSecret, homePageURL, jwks, name, privacyPolicyURL,
			redirectURIsList);
	}

	private void _validate(
			long companyId, long oAuth2ApplicationId,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, String clientId,
			String clientSecret, String homePageURL, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList)
		throws PortalException {

		if (clientAuthenticationMethod.equals(
				OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE)) {

			// Public client

			for (GrantType grantType : allowedGrantTypesList) {
				if (!grantType.isSupportsPublicClients()) {
					throw new OAuth2ApplicationClientGrantTypeException(
						grantType.name());
				}
			}
		}
		else if (clientAuthenticationMethod.equals(
					OAuthConstants.TOKEN_ENDPOINT_AUTH_POST) ||
				 clientAuthenticationMethod.equals("client_secret_jwt")) {

			// Confidential client with client secret

			for (GrantType grantType : allowedGrantTypesList) {
				if (!grantType.isSupportsConfidentialClients()) {
					throw new OAuth2ApplicationClientGrantTypeException(
						grantType.name());
				}
			}

			if (Validator.isNull(clientSecret)) {
				throw new PortalException(
					"Client needs to specify client secret");
			}
		}
		else if (clientAuthenticationMethod.equals("private_key_jwt")) {

			// Confidential client with private key

			for (GrantType grantType : allowedGrantTypesList) {
				if (!grantType.isSupportsConfidentialClients()) {
					throw new OAuth2ApplicationClientGrantTypeException(
						grantType.name());
				}
			}

			if (Validator.isNull(jwks)) {
				throw new PortalException("Client needs to specify JWKS");
			}

			try {
				OAuth2JWKValidatorUtil.validateJWKS(jwks);
			}
			catch (SecurityException securityException) {
				throw new OAuth2ApplicationJWKSException(
					securityException.getMessage(), securityException);
			}
		}
		else {
			throw new PortalException(
				"Unrecognized client authentication method");
		}

		if (!Validator.isBlank(clientId)) {
			OAuth2Application existingOAuth2Application =
				oAuth2ApplicationPersistence.fetchByC_C(companyId, clientId);

			if ((existingOAuth2Application != null) &&
				(existingOAuth2Application.getOAuth2ApplicationId() !=
					oAuth2ApplicationId)) {

				throw new DuplicateOAuth2ApplicationClientIdException();
			}
		}

		if (!Validator.isBlank(homePageURL)) {
			if (!StringUtil.startsWith(homePageURL, Http.HTTP_WITH_SLASH) &&
				!StringUtil.startsWith(homePageURL, Http.HTTPS_WITH_SLASH)) {

				throw new OAuth2ApplicationHomePageURLSchemeException();
			}

			if (!Validator.isUri(homePageURL)) {
				throw new OAuth2ApplicationHomePageURLException();
			}
		}

		if (Validator.isBlank(name)) {
			throw new OAuth2ApplicationNameException();
		}

		if (!Validator.isBlank(privacyPolicyURL)) {
			if (!StringUtil.startsWith(
					privacyPolicyURL, Http.HTTP_WITH_SLASH) &&
				!StringUtil.startsWith(
					privacyPolicyURL, Http.HTTPS_WITH_SLASH)) {

				throw new OAuth2ApplicationPrivacyPolicyURLSchemeException();
			}

			if (!Validator.isUri(privacyPolicyURL)) {
				throw new OAuth2ApplicationPrivacyPolicyURLException();
			}
		}

		if (redirectURIsList.isEmpty()) {
			for (GrantType grantType : allowedGrantTypesList) {
				if (grantType.isRequiresRedirectURI()) {
					throw new OAuth2ApplicationRedirectURIMissingException(
						grantType.name());
				}
			}
		}

		for (String redirectURI : redirectURIsList) {
			int index = redirectURI.indexOf(StringPool.POUND);

			if (index != -1) {
				throw new OAuth2ApplicationRedirectURIFragmentException(
					redirectURI);
			}

			index = redirectURI.indexOf(Http.PROTOCOL_DELIMITER);

			if (index == -1) {
				throw new OAuth2ApplicationRedirectURISchemeException(
					redirectURI);
			}

			String scheme = StringUtil.toLowerCase(
				redirectURI.substring(0, index));

			if (!Objects.equals(
					scheme, OAuth2RedirectURIInterpolator.TOKEN_PROTOCOL) &&
				!Objects.equals(scheme, Http.HTTP) &&
				!Objects.equals(scheme, Http.HTTPS) &&
				_ianaRegisteredUriSchemes.contains(scheme)) {

				throw new OAuth2ApplicationHomePageURLSchemeException(
					redirectURI);
			}

			String domainAndPath = redirectURI.substring(
				index + Http.PROTOCOL_DELIMITER.length());

			index = domainAndPath.indexOf(StringPool.SLASH);

			if (index > -1) {
				String path = domainAndPath.substring(index);

				String normalizedPath = HttpComponentsUtil.normalizePath(path);

				if (!Objects.equals(path, normalizedPath)) {
					throw new OAuth2ApplicationRedirectURIPathException(
						redirectURI);
				}
			}
		}
	}

	private static Set<String> _ianaRegisteredUriSchemes = SetUtil.fromArray(
		"aaa", "aaas", "about", "acap", "acct", "acr", "adiumxtra", "afp",
		"afs", "aim", "appdata", "apt", "attachment", "aw", "barion", "beshare",
		"bitcoin", "blob", "bolo", "browserext", "callto", "cap", "chrome",
		"chrome-extension", "cid", "coap", "coap+tcp", "coap+ws", "coaps",
		"coaps+tcp", "coaps+ws", "com-eventbrite-attendee", "content", "conti",
		"crid", "cvs", "data", "dav", "diaspora", "dict", "dis",
		"dlna-playcontainer", "dlna-playsingle", "dns", "dntp", "dtn", "dvb",
		"ed2k", "example", "facetime", "fax", "feed", "feedready", "file",
		"filesystem", "finger", "fish", "ftp", "geo", "gg", "git",
		"gizmoproject", "go", "gopher", "graph", "gtalk", "h323", "ham", "hcp",
		"http", "https", "hxxp", "hxxps", "hydrazone", "iax", "icap", "icon",
		"im", "imap", "info", "iotdisco", "ipn", "ipp", "ipps", "irc", "irc6",
		"ircs", "iris", "iris.beep", "iris.lwz", "iris.xpc", "iris.xpcs",
		"isostore", "itms", "jabber", "jar", "jms", "keyparc", "lastfm", "ldap",
		"ldaps", "lvlt", "magnet", "mailserver", "mailto", "maps", "market",
		"message", "mid", "mms", "modem", "mongodb", "moz", "ms-access",
		"ms-browser-extension", "ms-drive-to", "ms-enrollment", "ms-excel",
		"ms-gamebarservices", "ms-gamingoverlay", "ms-getoffice", "ms-help",
		"ms-infopath", "ms-inputapp", "ms-lockscreencomponent-config",
		"ms-media-stream-id", "ms-mixedrealitycapture", "ms-officeapp",
		"ms-people", "ms-project", "ms-powerpoint", "ms-publisher",
		"ms-restoretabcompanion", "ms-search-repair",
		"ms-secondary-screen-controller", "ms-secondary-screen-setup",
		"ms-settings", "ms-settings-airplanemode", "ms-settings-bluetooth",
		"ms-settings-camera", "ms-settings-cellular",
		"ms-settings-cloudstorage", "ms-settings-connectabledevices",
		"ms-settings-displays-topology", "ms-settings-emailandaccounts",
		"ms-settings-language", "ms-settings-location", "ms-settings-lock",
		"ms-settings-nfctransactions", "ms-settings-notifications",
		"ms-settings-power", "ms-settings-privacy", "ms-settings-proximity",
		"ms-settings-screenrotation", "ms-settings-wifi",
		"ms-settings-workplace", "ms-spd", "ms-sttoverlay", "ms-transit-to",
		"ms-useractivityset", "ms-virtualtouchpad", "ms-visio", "ms-walk-to",
		"ms-whiteboard", "ms-whiteboard-cmd", "ms-word", "msnim", "msrp",
		"msrps", "mtqp", "mumble", "mupdate", "mvn", "news", "nfs", "ni", "nih",
		"nntp", "notes", "ocf", "oid", "onenote", "onenote-cmd",
		"opaquelocktoken", "pack", "palm", "paparazzi", "pkcs11", "platform",
		"pop", "pres", "prospero", "proxy", "pwid", "psyc", "qb", "query",
		"redis", "rediss", "reload", "res", "resource", "rmi", "rsync", "rtmfp",
		"rtmp", "rtsp", "rtsps", "rtspu", "secondlife", "service", "session",
		"sftp", "sgn", "shttp", "sieve", "sip", "sips", "skype", "smb", "sms",
		"smtp", "snews", "snmp", "soap.beep", "soap.beeps", "soldat", "spiffe",
		"spotify", "ssh", "steam", "stun", "stuns", "submit", "svn", "tag",
		"teamspeak", "tel", "teliaeid", "telnet", "tftp", "things",
		"thismessage", "tip", "tn3270", "tool", "turn", "turns", "tv", "udp",
		"unreal", "urn", "ut2004", "v-event", "vemmi", "ventrilo", "videotex",
		"vnc", "view-source", "wais", "webcal", "wpid", "ws", "wss", "wtai",
		"wyciwyg", "xcon", "xcon-userid", "xfire", "xmlrpc.beep",
		"xmlrpc.beeps", "xmpp", "xri", "ymsgr", "z39.50", "z39.50r", "z39.50s");

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}