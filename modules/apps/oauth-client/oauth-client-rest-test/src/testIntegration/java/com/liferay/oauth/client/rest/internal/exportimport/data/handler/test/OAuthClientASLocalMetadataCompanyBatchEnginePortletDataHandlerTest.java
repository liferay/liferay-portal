/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49855"))
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_oAuthClientASLocalMetadataLocalService.addOAuthClientASLocalMetadata(
			externalReferenceCode, TestPropsValues.getUserId(), _getRandomURL(),
			_getRandomURL(), _getRandomURL(), false, _getRandomURL(),
			new String[] {"authorization_code"}, new String[] {"openid"},
			new String[] {"public"}, _getRandomURL(), _getRandomURL());
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(
				_getOAuthClientASLocalMetadata(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_getOAuthClientASLocalMetadata(externalReferenceCode, groupId);

		return oAuthClientASLocalMetadata.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.oauth.client.rest.dto.v1_0.
			OAuthClientASLocalMetadata.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_getOAuthClientASLocalMetadata(externalReferenceCode, groupId);

		return oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				fetchOAuthClientASLocalMetadataByExternalReferenceCode(
					externalReferenceCode, group.getCompanyId());

		if (oAuthClientASLocalMetadata == null) {
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

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_getOAuthClientASLocalMetadata(externalReferenceCode, groupId);

		oAuthClientASLocalMetadata.setUserId(userId);

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_getOAuthClientASLocalMetadata(externalReferenceCode, groupId);

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId(),
				_getRandomURL(), oAuthClientASLocalMetadata.getIssuer(),
				_getRandomURL(),
				oAuthClientASLocalMetadata.isLocalWellKnownEnabled(),
				_getRandomURL(), new String[] {"authorization_code"},
				new String[] {"openid"}, new String[] {"public"}, _getRandomURL(),
				_getRandomURL());
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_getOAuthClientASLocalMetadata(externalReferenceCode, groupId);

		oAuthClientASLocalMetadata.setModifiedDate(modifiedDate);

		_oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	private OAuthClientASLocalMetadata _getOAuthClientASLocalMetadata(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadataByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());
	}

	private String _getRandomURL() {
		return "http://localhost/" + RandomTestUtil.randomString();
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}