/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.batch.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateAdaptorFactory;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class VulcanBatchEngineTaskItemDelegateAdaptorFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCreate() {
		TestVulcanBatchEngineTaskItemDelegate
			vulcanBatchEngineTaskItemDelegate =
				new TestVulcanBatchEngineTaskItemDelegate();

		_vulcanBatchEngineTaskItemDelegateAdaptorFactory.create(
			vulcanBatchEngineTaskItemDelegate);

		Assert.assertSame(
			_groupLocalService,
			vulcanBatchEngineTaskItemDelegate._groupLocalService);
		Assert.assertSame(
			_resourceActionLocalService,
			vulcanBatchEngineTaskItemDelegate._resourceActionLocalService);
		Assert.assertSame(
			_resourcePermissionLocalService,
			vulcanBatchEngineTaskItemDelegate._resourcePermissionLocalService);
		Assert.assertSame(
			_roleLocalService,
			vulcanBatchEngineTaskItemDelegate._roleLocalService);
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private VulcanBatchEngineTaskItemDelegateAdaptorFactory
		_vulcanBatchEngineTaskItemDelegateAdaptorFactory;

	private static class TestVulcanBatchEngineTaskItemDelegate
		implements VulcanBatchEngineTaskItemDelegate<Object> {

		@Override
		public void create(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

		@Override
		public void delete(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

		@Override
		public EntityModel getEntityModel(
				Map<String, List<String>> multivaluedMap)
			throws Exception {

			return null;
		}

		@Override
		public Page<Object> read(
				Filter filter, Pagination pagination, Sort[] sorts,
				Map<String, Serializable> parameters, String search)
			throws Exception {

			return null;
		}

		@Override
		public void setContextBatchUnsafeBiConsumer(
			UnsafeBiConsumer
				<Collection<Object>, UnsafeFunction<Object, Object, Exception>,
				 Exception> contextBatchUnsafeBiConsumer) {
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@Override
		public void setContextUriInfo(UriInfo uriInfo) {
		}

		@Override
		public void setContextUser(User contextUser) {
		}

		@Override
		public void setGroupLocalService(GroupLocalService groupLocalService) {
			_groupLocalService = groupLocalService;
		}

		@Override
		public void setLanguageId(String languageId) {
		}

		@Override
		public void setResourceActionLocalService(
			ResourceActionLocalService resourceActionLocalService) {

			_resourceActionLocalService = resourceActionLocalService;
		}

		@Override
		public void setResourcePermissionLocalService(
			ResourcePermissionLocalService resourcePermissionLocalService) {

			_resourcePermissionLocalService = resourcePermissionLocalService;
		}

		@Override
		public void setRoleLocalService(RoleLocalService roleLocalService) {
			_roleLocalService = roleLocalService;
		}

		@Override
		public void update(
				Collection<Object> items, Map<String, Serializable> parameters)
			throws Exception {
		}

		private GroupLocalService _groupLocalService;
		private ResourceActionLocalService _resourceActionLocalService;
		private ResourcePermissionLocalService _resourcePermissionLocalService;
		private RoleLocalService _roleLocalService;

	}

}