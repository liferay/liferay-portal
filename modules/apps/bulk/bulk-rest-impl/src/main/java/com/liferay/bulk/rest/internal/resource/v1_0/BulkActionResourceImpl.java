/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.dto.v1_0.KeywordBulkAction;
import com.liferay.bulk.rest.internal.odata.entity.v1_0.BulkActionEntityModel;
import com.liferay.bulk.rest.internal.selection.v1_0.BulkActionSelectionFactory;
import com.liferay.bulk.rest.internal.selection.v1_0.DocumentBulkSelectionFactory;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionInputParameters;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.Serializable;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/bulk-action.properties",
	scope = ServiceScope.PROTOTYPE, service = BulkActionResource.class
)
public class BulkActionResourceImpl extends BaseBulkActionResourceImpl {

	@Override
	public BulkActionTask postBulkAction(
		String search, Filter filter,
		BulkAction bulkAction)
		throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
			contextCompany.getCompanyId(), "LPD-17564")) {

			throw new UnsupportedOperationException();
		}

		BulkAction.Type type = bulkAction.getType();

		if (BulkAction.Type.KEYWORD_BULK_ACTION.equals(type)) {
			return _executeKeywordBulkAction(
				search, filter, bulkAction);
		}

		throw new UnsupportedOperationException();
	}

	private BulkActionTask _executeKeywordBulkAction(
		String search, Filter filter, BulkAction bulkAction)
		throws Exception {

		KeywordBulkAction keywordBulkAction = (KeywordBulkAction)bulkAction;

		BulkSelection<?> bulkSelection = _bulkActionSelectionFactory.create(
			search, filter, keywordBulkAction );

		if (bulkSelection.getSize() == 0 ) {
			return new BulkActionTask();
		}

		_bulkSelectionRunner.run(
			contextUser, bulkSelection.toAssetEntryBulkSelection(),
			_editTagsBulkSelectionAction,
			HashMapBuilder.<String, Serializable>put(
				BulkSelectionInputParameters.ASSET_ENTRY_BULK_SELECTION, true
			).put(
				"append", true
			).put(
				"toAddTagNames", keywordBulkAction.getKeywordsToAdd()
			).put(
				"toRemoveTagNames", keywordBulkAction.getKeywordsToRemove()
			).build());

		return new BulkActionTask() {
			{

			}
		};
	}

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference
	private BulkActionSelectionFactory _bulkActionSelectionFactory;

	@Reference(target = "(bulk.selection.action.key=edit.tags)")
	private BulkSelectionAction<AssetEntry> _editTagsBulkSelectionAction;

	private static final EntityModel _entityModel = new BulkActionEntityModel();

	@Override
	public Page<BulkActionItem> postBulkActionItemPreviewPage(
		Boolean fetchChildren, String search, Filter filter,
		Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {
		return super.postBulkActionItemPreviewPage(
			fetchChildren, search, filter, pagination, sorts, bulkAction);
	}
}