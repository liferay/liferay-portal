/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.change.tracking.spi.resolver;

import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRelTable;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.change.tracking.spi.resolver.ConstraintResolver;
import com.liferay.change.tracking.spi.resolver.context.ConstraintResolverContext;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.language.LanguageResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(service = ConstraintResolver.class)
public class AssetListEntryAssetEntryRelConstraintResolver
	implements ConstraintResolver<AssetListEntryAssetEntryRel> {

	@Override
	public String getConflictDescriptionKey() {
		return "duplicate-asset-list-entry-asset-entry-rel-position";
	}

	@Override
	public Class<AssetListEntryAssetEntryRel> getModelClass() {
		return AssetListEntryAssetEntryRel.class;
	}

	@Override
	public String getResolutionDescriptionKey() {
		return "duplicate-asset-list-entry-asset-entry-rel-position-was-" +
			"reordered";
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return LanguageResources.getResourceBundle(locale);
	}

	@Override
	public String[] getUniqueIndexColumnNames() {
		return new String[] {"assetListEntryId", "segmentsEntryId", "position"};
	}

	@Override
	public boolean isModificationConflictCheckEnabled() {
		return true;
	}

	@Override
	public void resolveConflict(
			ConstraintResolverContext<AssetListEntryAssetEntryRel>
				constraintResolverContext)
		throws PortalException {

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			constraintResolverContext.getSourceCTModel();

		long assetListEntryId =
			assetListEntryAssetEntryRel.getAssetListEntryId();
		long segmentsEntryId = assetListEntryAssetEntryRel.getSegmentsEntryId();

		List<AssetListEntryAssetEntryRel> sourceAssetListEntryAssetEntryRels =
			new ArrayList<>();

		for (AssetListEntryAssetEntryRel curAssetListEntryAssetEntryRel :
				_assetListEntryAssetEntryRelLocalService.
					getAssetListEntryAssetEntryRels(
						assetListEntryId, segmentsEntryId, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

			if (constraintResolverContext.isSourceCTModel(
					curAssetListEntryAssetEntryRel)) {

				sourceAssetListEntryAssetEntryRels.add(
					curAssetListEntryAssetEntryRel);
			}
		}

		Set<Integer> targetPositions = _getTargetPositions(
			assetListEntryId, constraintResolverContext, segmentsEntryId);

		List<AssetListEntryAssetEntryRel> movedAssetListEntryAssetEntryRels =
			new ArrayList<>();
		Map<Long, Integer> positionsMap = new HashMap<>();

		int position = 0;

		for (AssetListEntryAssetEntryRel sourceAssetListEntryAssetEntryRel :
				sourceAssetListEntryAssetEntryRels) {

			while (targetPositions.contains(position)) {
				position++;
			}

			if (sourceAssetListEntryAssetEntryRel.getPosition() != position) {
				movedAssetListEntryAssetEntryRels.add(
					sourceAssetListEntryAssetEntryRel);

				positionsMap.put(
					sourceAssetListEntryAssetEntryRel.
						getAssetListEntryAssetEntryRelId(),
					position);
			}

			position++;
		}

		if (movedAssetListEntryAssetEntryRels.isEmpty()) {
			return;
		}

		List<AssetListEntryAssetEntryRel> tempAssetListEntryAssetEntryRels =
			new ArrayList<>();

		int tempPosition = -1;

		for (AssetListEntryAssetEntryRel movedAssetListEntryAssetEntryRel :
				movedAssetListEntryAssetEntryRels) {

			movedAssetListEntryAssetEntryRel.setPosition(tempPosition);

			tempAssetListEntryAssetEntryRels.add(
				_assetListEntryAssetEntryRelLocalService.
					updateAssetListEntryAssetEntryRel(
						movedAssetListEntryAssetEntryRel));

			tempPosition--;
		}

		CTPersistence<AssetListEntryAssetEntryRel> ctPersistence =
			_assetListEntryAssetEntryRelLocalService.getCTPersistence();

		ctPersistence.flush();

		for (AssetListEntryAssetEntryRel tempAssetListEntryAssetEntryRel :
				tempAssetListEntryAssetEntryRels) {

			tempAssetListEntryAssetEntryRel.setPosition(
				positionsMap.get(
					tempAssetListEntryAssetEntryRel.
						getAssetListEntryAssetEntryRelId()));

			_assetListEntryAssetEntryRelLocalService.
				updateAssetListEntryAssetEntryRel(
					tempAssetListEntryAssetEntryRel);
		}

		ctPersistence.flush();
	}

	private Set<Integer> _getTargetPositions(
		long assetListEntryId,
		ConstraintResolverContext<AssetListEntryAssetEntryRel>
			constraintResolverContext,
		long segmentsEntryId) {

		Set<Integer> targetPositions = new HashSet<>();

		List<Object[]> rows = constraintResolverContext.getInTarget(
			() -> _assetListEntryAssetEntryRelLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					AssetListEntryAssetEntryRelTable.INSTANCE.
						assetListEntryAssetEntryRelId,
					AssetListEntryAssetEntryRelTable.INSTANCE.position
				).from(
					AssetListEntryAssetEntryRelTable.INSTANCE
				).where(
					AssetListEntryAssetEntryRelTable.INSTANCE.assetListEntryId.
						eq(
							assetListEntryId
						).and(
							AssetListEntryAssetEntryRelTable.INSTANCE.
								segmentsEntryId.eq(segmentsEntryId)
						)
				)));

		for (Object[] row : rows) {
			AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
				_assetListEntryAssetEntryRelLocalService.
					fetchAssetListEntryAssetEntryRel((Long)row[0]);

			if ((assetListEntryAssetEntryRel != null) &&
				!constraintResolverContext.isSourceCTModel(
					assetListEntryAssetEntryRel)) {

				targetPositions.add((Integer)row[1]);
			}
		}

		return targetPositions;
	}

	@Reference
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

}