/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.SiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SiteTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.permission.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site-test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteTestEntityResource.class
)
public class SiteTestEntityResourceImpl extends BaseSiteTestEntityResourceImpl {

	@Override
	public void deleteSiteSiteTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		SiteTestEntity siteTestEntity =
			_fetchSiteSiteTestEntityByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (siteTestEntity == null) {
			throw new NoSuchModelException();
		}

		_siteTestEntities.remove(siteTestEntity);
	}

	@Override
	public Page<Permission> getSiteTestEntityPermissionsPage(
			Long siteTestEntityId, String roleNames)
		throws Exception {

		SiteTestEntity siteTestEntity = doGetSiteTestEntity(siteTestEntityId);

		if (!_permissions.containsKey(siteTestEntity.getId())) {
			_permissions.put(
				siteTestEntity.getId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(
								new String[] {
									"DELETE", "PERMISSIONS", "UPDATE", "VIEW"
								});
							setRoleName("Owner");
						}
					}
				});
		}

		return Page.of(Arrays.asList(_permissions.get(siteTestEntity.getId())));
	}

	@Override
	public Page<Permission> putSiteTestEntityPermissionsPage(
			Long siteTestEntityId, Permission[] permissions)
		throws Exception {

		for (Permission permission : permissions) {
			_roleLocalService.getRole(
				CompanyThreadLocal.getCompanyId(), permission.getRoleName());
		}

		SiteTestEntity siteTestEntity = doGetSiteTestEntity(siteTestEntityId);

		_permissions.put(siteTestEntity.getId(), permissions);

		return getSiteTestEntityPermissionsPage(siteTestEntityId, null);
	}

	@Override
	protected Page<SiteTestEntity> doGetSiteSiteTestEntitiesPage(Long siteId)
		throws Exception {

		List<SiteTestEntity> siteTestEntities = new ArrayList<>();

		for (SiteTestEntity siteTestEntity : _siteTestEntities) {
			if (Objects.equals(siteId, siteTestEntity.getSiteId())) {
				siteTestEntities.add(siteTestEntity);
			}
		}

		return Page.of(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/test/v1.0/sites/" + siteId +
						"/site-test-entities/batch"
				).put(
					"method", "POST"
				).build()
			).build(),
			siteTestEntities);
	}

	@Override
	protected SiteTestEntity doGetSiteSiteTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		SiteTestEntity siteTestEntity =
			_fetchSiteSiteTestEntityByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (siteTestEntity == null) {
			throw new NoSuchModelException();
		}

		return siteTestEntity;
	}

	@Override
	protected SiteTestEntity doGetSiteTestEntity(Long siteTestEntityId)
		throws Exception {

		SiteTestEntity siteTestEntity = _fetchSiteTestEntity(siteTestEntityId);

		if (siteTestEntity == null) {
			throw new NoSuchModelException();
		}

		return siteTestEntity;
	}

	@Override
	protected SiteTestEntity doPostSiteSiteTestEntity(
			Long siteId, SiteTestEntity siteTestEntity)
		throws Exception {

		if (Validator.isNull(siteTestEntity.getExternalReferenceCode())) {
			siteTestEntity.setExternalReferenceCode(StringUtil.randomString());
		}
		else {
			SiteTestEntity existingSiteTestEntity =
				_fetchSiteSiteTestEntityByExternalReferenceCode(
					siteTestEntity.getExternalReferenceCode(), siteId);

			if (existingSiteTestEntity != null) {
				throw new DuplicateExternalReferenceCodeException();
			}
		}

		siteTestEntity.setId(Long.valueOf(_siteTestEntities.size()));
		siteTestEntity.setSiteId(siteId);

		_siteTestEntities.add(siteTestEntity);

		return siteTestEntity;
	}

	@Override
	protected SiteTestEntity doPutSiteSiteTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			SiteTestEntity siteTestEntity)
		throws Exception {

		siteTestEntity.setExternalReferenceCode(externalReferenceCode);

		SiteTestEntity existingSiteTestEntity =
			_fetchSiteSiteTestEntityByExternalReferenceCode(
				externalReferenceCode, siteId);

		if (existingSiteTestEntity == null) {
			return postSiteSiteTestEntity(siteId, siteTestEntity);
		}

		return putSiteTestEntity(
			existingSiteTestEntity.getId(), siteTestEntity);
	}

	@Override
	protected SiteTestEntity doPutSiteTestEntity(
			Long siteTestEntityId, SiteTestEntity siteTestEntity)
		throws Exception {

		SiteTestEntity existingSiteTestEntity =
			_fetchSiteSiteTestEntityByExternalReferenceCode(
				siteTestEntity.getExternalReferenceCode(), siteTestEntityId);

		if ((existingSiteTestEntity != null) &&
			!Objects.equals(existingSiteTestEntity.getId(), siteTestEntityId)) {

			throw new DuplicateExternalReferenceCodeException();
		}

		existingSiteTestEntity = _fetchSiteTestEntity(siteTestEntityId);

		if (existingSiteTestEntity == null) {
			throw new NoSuchModelException();
		}

		siteTestEntity.setExternalReferenceCode(
			siteTestEntity.getExternalReferenceCode());
		siteTestEntity.setId(siteTestEntityId);
		siteTestEntity.setSiteId(existingSiteTestEntity.getSiteId());

		_siteTestEntities.set(
			Math.toIntExact(siteTestEntityId), siteTestEntity);

		return siteTestEntity;
	}

	private SiteTestEntity _fetchSiteSiteTestEntityByExternalReferenceCode(
			String externalReferenceCode, Long siteId)
		throws Exception {

		for (SiteTestEntity siteTestEntity : _siteTestEntities) {
			if (Objects.equals(
					externalReferenceCode,
					siteTestEntity.getExternalReferenceCode()) &&
				Objects.equals(siteId, siteTestEntity.getSiteId())) {

				return siteTestEntity;
			}
		}

		return null;
	}

	private SiteTestEntity _fetchSiteTestEntity(long id) {
		if (_siteTestEntities.size() > id) {
			return _siteTestEntities.get(Math.toIntExact(id));
		}

		return null;
	}

	private static final Map<Long, Permission[]> _permissions = new HashMap<>();
	private static final List<SiteTestEntity> _siteTestEntities =
		new ArrayList<>();

	@Reference
	private RoleLocalService _roleLocalService;

}