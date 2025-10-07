/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.internal.search.ObjectEntryBatchReindexer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 * @author Gabriel Albuquerque
 */
@Component(service = ModelListener.class)
public class AccountEntryOrganizationRelModelListener
	extends BaseModelListener<AccountEntryOrganizationRel> {

	@Override
	public void onAfterCreate(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Override
	public void onAfterRemove(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws ModelListenerException {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_reindex(accountEntryOrganizationRel);

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, IndexerDocumentBuilder.class, "indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _reindex(
			AccountEntryOrganizationRel accountEntryOrganizationRel)
		throws PortalException {

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(true)) {

			if (!objectDefinition.isEnableIndexSearch()) {
				continue;
			}

			ObjectScopeProvider objectScopeProvider =
				_objectScopeProviderRegistry.getObjectScopeProvider(
					objectDefinition.getScope());

			if (!objectScopeProvider.isGroupAware()) {
				ObjectField objectField =
					ObjectFieldLocalServiceUtil.fetchObjectField(
						objectDefinition.
							getAccountEntryRestrictedObjectFieldId());

				if (objectField == null) {
					continue;
				}

				int count = _objectEntryLocalService.getValuesListCount(
					new Long[] {0L}, accountEntryOrganizationRel.getCompanyId(),
					objectDefinition.getUserId(),
					objectDefinition.getObjectDefinitionId(),
					_filterFactory.create(
						StringBundler.concat(
							objectField.getName(), " eq '",
							accountEntryOrganizationRel.getAccountEntryId(),
							"'"),
						objectDefinition),
					false, null);

				if (count <= 0) {
					continue;
				}
			}

			ObjectEntryBatchReindexer objectEntryBatchReindexer =
				new ObjectEntryBatchReindexer(
					_dynamicQueryBatchIndexingActionableFactory,
					_objectEntryLocalService, objectDefinition);

			IndexerDocumentBuilder indexerDocumentBuilder =
				_serviceTrackerMap.getService(
					objectEntryBatchReindexer.getClassName());

			objectEntryBatchReindexer.reindex(
				indexerDocumentBuilder,
				accountEntryOrganizationRel.getAccountEntryId(),
				accountEntryOrganizationRel.getCompanyId());
		}
	}

	@Reference
	private DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	private ServiceTrackerMap<String, IndexerDocumentBuilder>
		_serviceTrackerMap;

}