/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49855"))
@RunWith(Arquillian.class)
public class OAuthClientEntryCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		_companyConfigurationTemporarySwapper =
			_getCompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId());

		User user = getTargetCompanyAdminUser();

		_targetCompanyConfigurationTemporarySwapper =
			_getCompanyConfigurationTemporarySwapper(user.getCompanyId());

		_openIdConnectProviderHttpServer =
			new OpenIdConnectProviderHttpServer();

		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
		_openIdConnectProviderHttpServer.close();
		_targetCompanyConfigurationTemporarySwapper.close();
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			externalReferenceCode, TestPropsValues.getUserId(), _randomJSON(),
			_openIdConnectProviderHttpServer.getURL(), _randomJSON(),
			_getInfoJSON(), "email", 0,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON, 0,
			_randomJSON());
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_oAuthClientEntryLocalService.deleteOAuthClientEntry(
			_getOAuthClientEntry(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = _getOAuthClientEntry(
			externalReferenceCode, groupId);

		return oAuthClientEntry.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.oauth.client.rest.dto.v1_0.OAuthClientEntry.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = _getOAuthClientEntry(
			externalReferenceCode, groupId);

		return oAuthClientEntry.getOAuthClientEntryId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.
				fetchOAuthClientEntryByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (oAuthClientEntry == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = _getOAuthClientEntry(
			externalReferenceCode, groupId);

		oAuthClientEntry.setUserId(userId);

		_oAuthClientEntryLocalService.updateOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = _getOAuthClientEntry(
			externalReferenceCode, groupId);

		_oAuthClientEntryLocalService.updateOAuthClientEntry(
			oAuthClientEntry.getOAuthClientEntryId(),
			oAuthClientEntry.getAuthRequestParametersJSON(),
			oAuthClientEntry.getAuthServerWellKnownURI(),
			oAuthClientEntry.getCustomClaimsJSON(),
			oAuthClientEntry.getInfoJSON(), RandomTestUtil.randomString(),
			oAuthClientEntry.getMetadataCacheTime(),
			oAuthClientEntry.getOIDCUserInfoMapperJSON(),
			oAuthClientEntry.getTokenConnectionTimeout(),
			oAuthClientEntry.getTokenRequestParametersJSON());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = _getOAuthClientEntry(
			externalReferenceCode, groupId);

		oAuthClientEntry.setModifiedDate(modifiedDate);

		_oAuthClientEntryLocalService.updateOAuthClientEntry(oAuthClientEntry);
	}

	private CompanyConfigurationTemporarySwapper
			_getCompanyConfigurationTemporarySwapper(long companyId)
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			companyId, OAuthClientCompanyConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"authServerLocalNetworkAccessEnabled", true
			).build());
	}

	private String _getInfoJSON() {
		return JSONUtil.put(
			"client_id", RandomTestUtil.randomString()
		).put(
			"client_name", "example_client"
		).put(
			"client_secret", RandomTestUtil.randomString()
		).put(
			"scope", "openid email profile"
		).put(
			"subject_type", "public"
		).toString();
	}

	private OAuthClientEntry _getOAuthClientEntry(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _oAuthClientEntryLocalService.
			getOAuthClientEntryByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	private String _randomJSON() {
		return JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).toString();
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private OpenIdConnectProviderHttpServer _openIdConnectProviderHttpServer;
	private CompanyConfigurationTemporarySwapper
		_targetCompanyConfigurationTemporarySwapper;

}