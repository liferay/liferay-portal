/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.portal.kernel.exception.DuplicateExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ScopedTestEntityResource;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/scoped-test-entity.properties",
	scope = ServiceScope.PROTOTYPE, service = ScopedTestEntityResource.class
)
public class ScopedTestEntityResourceImpl
	extends BaseScopedTestEntityResourceImpl {

	@Override
	public void deleteAssetLibraryScopedTestEntityByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		_deleteScopedTestEntity(
			String.valueOf(assetLibraryId), externalReferenceCode, 0L);
	}

	@Override
	public void deleteSiteScopedTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		_deleteScopedTestEntity(null, externalReferenceCode, siteId);
	}

	@Override
	public Page<ScopedTestEntity> getAssetLibraryScopedTestEntitiesPage(
			Long assetLibraryId)
		throws Exception {

		DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
			assetLibraryId);

		List<ScopedTestEntity> scopedTestEntities = new ArrayList<>();

		for (ScopedTestEntity scopedTestEntity : _scopedTestEntities) {
			if (Objects.equals(
					String.valueOf(assetLibraryId),
					scopedTestEntity.getAssetLibraryKey())) {

				scopedTestEntities.add(scopedTestEntity);
			}
		}

		return Page.of(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/test/v1.0/asset-libraries/" +
						depotEntry.getDepotEntryId() +
							"/scoped-test-entities/batch"
				).put(
					"method", "POST"
				).build()
			).build(),
			scopedTestEntities);
	}

	@Override
	public ScopedTestEntity
			getAssetLibraryScopedTestEntityByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		ScopedTestEntity scopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				String.valueOf(assetLibraryId), externalReferenceCode, 0L);

		if (scopedTestEntity == null) {
			throw new NoSuchModelException();
		}

		return scopedTestEntity;
	}

	@Override
	public Page<ScopedTestEntity> getSiteScopedTestEntitiesPage(Long siteId)
		throws Exception {

		List<ScopedTestEntity> scopedTestEntities = new ArrayList<>();

		for (ScopedTestEntity scopedTestEntity : _scopedTestEntities) {
			if (Objects.equals(siteId, scopedTestEntity.getSiteId())) {
				scopedTestEntities.add(scopedTestEntity);
			}
		}

		return Page.of(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/test/v1.0/sites/" + siteId +
						"/scoped-test-entities/batch"
				).put(
					"method", "POST"
				).build()
			).build(),
			scopedTestEntities);
	}

	@Override
	public ScopedTestEntity getSiteScopedTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		ScopedTestEntity scopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				null, externalReferenceCode, siteId);

		if (scopedTestEntity == null) {
			throw new NoSuchModelException();
		}

		return scopedTestEntity;
	}

	@Override
	public ScopedTestEntity
			patchAssetLibraryScopedTestEntityByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			getAssetLibraryScopedTestEntityByExternalReferenceCode(
				assetLibraryId, externalReferenceCode);

		_patchProperties(scopedTestEntity, existingScopedTestEntity);

		externalReferenceCode = scopedTestEntity.getExternalReferenceCode();

		preparePatch(scopedTestEntity, existingScopedTestEntity);

		return putAssetLibraryScopedTestEntityByExternalReferenceCode(
			assetLibraryId, externalReferenceCode, existingScopedTestEntity);
	}

	@Override
	public ScopedTestEntity patchSiteScopedTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			getSiteScopedTestEntityByExternalReferenceCode(
				siteId, externalReferenceCode);

		_patchProperties(scopedTestEntity, existingScopedTestEntity);

		externalReferenceCode = scopedTestEntity.getExternalReferenceCode();

		preparePatch(scopedTestEntity, existingScopedTestEntity);

		return putSiteScopedTestEntityByExternalReferenceCode(
			siteId, externalReferenceCode, existingScopedTestEntity);
	}

	@Override
	public ScopedTestEntity postAssetLibraryScopedTestEntity(
			Long assetLibraryId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				String.valueOf(assetLibraryId),
				scopedTestEntity.getExternalReferenceCode(), 0L);

		if (existingScopedTestEntity != null) {
			throw new DuplicateExternalReferenceCodeException();
		}

		scopedTestEntity.setAssetLibraryKey(String.valueOf(assetLibraryId));

		if (Validator.isNull(scopedTestEntity.getExternalReferenceCode())) {
			scopedTestEntity.setExternalReferenceCode(
				StringUtil.randomString());
		}

		scopedTestEntity.setSiteId(0L);

		_scopedTestEntities.add(scopedTestEntity);

		return scopedTestEntity;
	}

	@Override
	public ScopedTestEntity postSiteScopedTestEntity(
			Long siteId, ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				null, scopedTestEntity.getExternalReferenceCode(), siteId);

		if (existingScopedTestEntity != null) {
			throw new DuplicateExternalReferenceCodeException();
		}

		scopedTestEntity.setAssetLibraryKey((String)null);

		if (Validator.isNull(scopedTestEntity.getExternalReferenceCode())) {
			scopedTestEntity.setExternalReferenceCode(
				StringUtil.randomString());
		}

		scopedTestEntity.setSiteId(siteId);

		_scopedTestEntities.add(scopedTestEntity);

		return scopedTestEntity;
	}

	@Override
	public ScopedTestEntity
			putAssetLibraryScopedTestEntityByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				String.valueOf(assetLibraryId), externalReferenceCode, 0L);

		if (existingScopedTestEntity == null) {
			return postAssetLibraryScopedTestEntity(
				assetLibraryId, scopedTestEntity);
		}

		scopedTestEntity.setAssetLibraryKey(String.valueOf(assetLibraryId));
		scopedTestEntity.setSiteId(0L);

		_putScopedTestEntity(
			scopedTestEntity,
			existingScopedTestEntity.getExternalReferenceCode());

		return scopedTestEntity;
	}

	@Override
	public ScopedTestEntity putSiteScopedTestEntityByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			ScopedTestEntity scopedTestEntity)
		throws Exception {

		ScopedTestEntity existingScopedTestEntity =
			_fetchScopedTestEntityByExternalReferenceCode(
				null, externalReferenceCode, siteId);

		if (existingScopedTestEntity == null) {
			return postSiteScopedTestEntity(siteId, scopedTestEntity);
		}

		scopedTestEntity.setAssetLibraryKey((String)null);
		scopedTestEntity.setSiteId(siteId);

		_putScopedTestEntity(
			scopedTestEntity,
			existingScopedTestEntity.getExternalReferenceCode());

		return scopedTestEntity;
	}

	private void _deleteScopedTestEntity(
		String assetLibraryKey,
		String existingScopedTestEntityExternalReferenceCode, Long siteId) {

		ListIterator<ScopedTestEntity> iterator =
			_scopedTestEntities.listIterator();

		while (iterator.hasNext()) {
			ScopedTestEntity existingScopedTestEntity = iterator.next();

			if (Objects.equals(
					existingScopedTestEntity.getExternalReferenceCode(),
					existingScopedTestEntityExternalReferenceCode) &&
				Objects.equals(
					assetLibraryKey,
					existingScopedTestEntity.getAssetLibraryKey()) &&
				Objects.equals(siteId, existingScopedTestEntity.getSiteId())) {

				iterator.remove();
			}
		}
	}

	private ScopedTestEntity _fetchScopedTestEntityByExternalReferenceCode(
			String assetLibraryKey, String externalReferenceCode, Long siteId)
		throws Exception {

		for (ScopedTestEntity scopedTestEntity : _scopedTestEntities) {
			if (Objects.equals(
					assetLibraryKey, scopedTestEntity.getAssetLibraryKey()) &&
				Objects.equals(
					externalReferenceCode,
					scopedTestEntity.getExternalReferenceCode()) &&
				Objects.equals(siteId, scopedTestEntity.getSiteId())) {

				return scopedTestEntity;
			}
		}

		return null;
	}

	private void _patchProperties(
		ScopedTestEntity scopedTestEntity,
		ScopedTestEntity existingScopedTestEntity) {

		if (scopedTestEntity.getDateCreated() != null) {
			existingScopedTestEntity.setDateCreated(
				scopedTestEntity.getDateCreated());
		}

		if (scopedTestEntity.getDateModified() != null) {
			existingScopedTestEntity.setDateModified(
				scopedTestEntity.getDateModified());
		}

		if (scopedTestEntity.getDescription() != null) {
			existingScopedTestEntity.setDescription(
				scopedTestEntity.getDescription());
		}

		if (scopedTestEntity.getExternalReferenceCode() != null) {
			existingScopedTestEntity.setExternalReferenceCode(
				scopedTestEntity.getExternalReferenceCode());
		}

		if (scopedTestEntity.getPermissions() != null) {
			existingScopedTestEntity.setPermissions(
				scopedTestEntity.getPermissions());
		}
	}

	private void _putScopedTestEntity(
		ScopedTestEntity scopedTestEntity,
		String existingScopedTestEntityExternalReferenceCode) {

		ListIterator<ScopedTestEntity> iterator =
			_scopedTestEntities.listIterator();

		while (iterator.hasNext()) {
			ScopedTestEntity existingScopedTestEntity = iterator.next();

			if (Objects.equals(
					existingScopedTestEntity.getExternalReferenceCode(),
					existingScopedTestEntityExternalReferenceCode)) {

				iterator.set(scopedTestEntity);
			}
		}
	}

	private static final List<ScopedTestEntity> _scopedTestEntities =
		new ArrayList<>();

	@Reference
	private DepotEntryService _depotEntryService;

}