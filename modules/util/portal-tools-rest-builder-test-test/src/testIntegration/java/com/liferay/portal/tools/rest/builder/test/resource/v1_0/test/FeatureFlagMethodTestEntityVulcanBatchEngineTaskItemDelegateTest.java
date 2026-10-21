/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.FeatureFlagMethodTestEntity;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateAdaptorFactory;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;

import jakarta.ws.rs.NotFoundException;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class FeatureFlagMethodTestEntityVulcanBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_batchEngineTaskItemDelegate =
			_vulcanBatchEngineTaskItemDelegateAdaptorFactory.create(
				(VulcanBatchEngineTaskItemDelegate<FeatureFlagMethodTestEntity>)
					_vulcanBatchEngineTaskItemDelegateRegistry.
						getVulcanBatchEngineTaskItemDelegate(
							TestPropsValues.getCompanyId(),
							FeatureFlagMethodTestEntity.class.getName()));

		_batchEngineTaskItemDelegate.setContextCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY),
			@FeatureFlag(
				enable = false,
				value = _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE
			),
			@FeatureFlag(
				enable = false,
				value = _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE
			),
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
		}
	)
	@Test
	public void testCreate() throws Exception {
		_create(new HashMap<>());

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _create(_getSiteParameters()));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		_create(_getSiteParameters());

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _create(_getUpsertParameters("PARTIAL_UPDATE")));

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _create(_getUpsertParameters("PARTIAL_UPDATE")));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		_create(_getUpsertParameters("PARTIAL_UPDATE"));

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _create(_getUpsertParameters("UPDATE")));

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE);

		_create(_getUpsertParameters("UPDATE"));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY),
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
		}
	)
	@Test
	public void testDelete() throws Exception {
		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _delete(RandomTestUtil.randomLong(), _getSiteParameters()));
		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _delete(null, _getSiteParameters()));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		_delete(null, _getSiteParameters());

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		_delete(RandomTestUtil.randomLong(), new HashMap<>());
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY),
			@FeatureFlag(
				enable = false,
				value = _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE
			),
			@FeatureFlag(
				enable = false,
				value = _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE
			)
		}
	)
	@Test
	public void testGetAvailableCreateStrategies() throws Exception {
		Assert.assertEquals(
			SetUtil.fromArray("INSERT"),
			_batchEngineTaskItemDelegate.getAvailableCreateStrategies());

		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		Assert.assertEquals(
			SetUtil.fromArray("INSERT"),
			_batchEngineTaskItemDelegate.getAvailableCreateStrategies());

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		Assert.assertEquals(
			SetUtil.fromArray("INSERT", "UPSERT"),
			_batchEngineTaskItemDelegate.getAvailableCreateStrategies());

		_setFeatureFlagEnabled(
			false, _FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE);

		Assert.assertEquals(
			SetUtil.fromArray("INSERT"),
			_batchEngineTaskItemDelegate.getAvailableCreateStrategies());

		_setFeatureFlagEnabled(false, _FEATURE_FLAG_KEY);
		_setFeatureFlagEnabled(
			true, _FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE);

		Assert.assertEquals(
			SetUtil.fromArray("INSERT", "UPSERT"),
			_batchEngineTaskItemDelegate.getAvailableCreateStrategies());
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Test
	public void testGetAvailableUpdateStrategies() throws Exception {
		Assert.assertEquals(
			Collections.emptySet(),
			_batchEngineTaskItemDelegate.getAvailableUpdateStrategies());

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		Assert.assertEquals(
			SetUtil.fromArray("PARTIAL_UPDATE"),
			_batchEngineTaskItemDelegate.getAvailableUpdateStrategies());
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY),
			@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY_SITE)
		}
	)
	@Test
	public void testRead() throws Exception {
		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _read(new HashMap<>()));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		_read(new HashMap<>());

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _read(_getSiteParameters()));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY_SITE);

		_read(_getSiteParameters());
	}

	@FeatureFlag(enable = false, value = _FEATURE_FLAG_KEY)
	@Test
	public void testUpdate() throws Exception {
		Map<String, Serializable> parameters =
			HashMapBuilder.<String, Serializable>put(
				"updateStrategy", "PARTIAL_UPDATE"
			).build();

		AssertUtils.assertFailure(
			NotFoundException.class, "HTTP 404 Not Found",
			() -> _batchEngineTaskItemDelegate.update(
				Collections.singletonList(new FeatureFlagMethodTestEntity()),
				parameters));

		_setFeatureFlagEnabled(true, _FEATURE_FLAG_KEY);

		_batchEngineTaskItemDelegate.update(
			Collections.singletonList(new FeatureFlagMethodTestEntity()),
			parameters);
	}

	private void _create(Map<String, Serializable> parameters)
		throws Exception {

		_batchEngineTaskItemDelegate.create(
			Collections.singletonList(new FeatureFlagMethodTestEntity()),
			parameters);
	}

	private void _delete(
			Long featureFlagMethodTestEntityId,
			Map<String, Serializable> parameters)
		throws Exception {

		_batchEngineTaskItemDelegate.delete(
			Collections.singletonList(
				new FeatureFlagMethodTestEntity() {
					{
						setExternalReferenceCode(RandomTestUtil.randomString());
						setId(featureFlagMethodTestEntityId);
					}
				}),
			parameters);
	}

	private Map<String, Serializable> _getSiteParameters() throws Exception {
		return HashMapBuilder.<String, Serializable>put(
			"siteId", String.valueOf(TestPropsValues.getGroupId())
		).build();
	}

	private Map<String, Serializable> _getUpsertParameters(
		String updateStrategy) {

		return HashMapBuilder.<String, Serializable>put(
			"createStrategy", "UPSERT"
		).put(
			"updateStrategy", updateStrategy
		).build();
	}

	private void _read(Map<String, Serializable> parameters) throws Exception {
		_batchEngineTaskItemDelegate.read(
			null, Pagination.of(1, 20), null, parameters, null);
	}

	private void _setFeatureFlagEnabled(
		boolean enabled, String featureFlagKey) {

		PropsUtil.set(
			FeatureFlagConstants.getKey(featureFlagKey),
			String.valueOf(enabled));
	}

	private static final String _FEATURE_FLAG_KEY = "METHOD-123";

	private static final String
		_FEATURE_FLAG_KEY_GET_BY_EXTERNAL_REFERENCE_CODE = "METHOD-124";

	private static final String
		_FEATURE_FLAG_KEY_PUT_BY_EXTERNAL_REFERENCE_CODE = "METHOD-125";

	private static final String _FEATURE_FLAG_KEY_SITE = "METHOD-126";

	private BatchEngineTaskItemDelegate<FeatureFlagMethodTestEntity>
		_batchEngineTaskItemDelegate;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private VulcanBatchEngineTaskItemDelegateAdaptorFactory
		_vulcanBatchEngineTaskItemDelegateAdaptorFactory;

	@Inject
	private VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

}