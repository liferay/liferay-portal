/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.health.status;

import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"commerce.health.status.display.order:Integer=200",
		"commerce.health.status.key=" + CommerceHealthStatusConstants.CP_CONFIGURATION_LIST_ELIGIBILITY_COMMERCE_HEALTH_STATUS_KEY
	},
	service = CommerceHealthStatus.class
)
public class CPConfigurationListEligibilityCommerceHealthStatus
	implements CommerceHealthStatus {

	@Override
	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		try {
			_deleteData(serviceContext.getCompanyId());
			_addData(serviceContext.getCompanyId());

			EntityCacheUtil.clearCache(CPConfigurationEntry.class);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				CP_CONFIGURATION_LIST_ELIGIBILITY_COMMERCE_HEALTH_STATUS_DESCRIPTION);
	}

	@Override
	public String getKey() {
		return CommerceHealthStatusConstants.
			CP_CONFIGURATION_LIST_ELIGIBILITY_COMMERCE_HEALTH_STATUS_KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				CP_CONFIGURATION_LIST_ELIGIBILITY_COMMERCE_HEALTH_STATUS_KEY);
	}

	@Override
	public int getType() {
		return CommerceHealthStatusConstants.
			COMMERCE_HEALTH_STATUS_TYPE_VIRTUAL_INSTANCE;
	}

	@Override
	public boolean isActive() {
		return FeatureFlagManagerUtil.isEnabled("LPD-10889");
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		return false;
	}

	private void _addCPConfigurationEntries(
			long accountGroupClassNameId, long cpConfigurationListClassNameId,
			long cpDefinitionClassNameId, long masterCPConfigurationListId,
			PreparedStatement preparedStatement1,
			PreparedStatement preparedStatement2,
			PreparedStatement preparedStatement3)
		throws Exception {

		Set<Long> cpConfigurationListIds = new HashSet<>();
		long curClassPK = 0;
		CPConfigurationEntry masterCPConfigurationEntry = null;

		ResultSet resultSet1 = preparedStatement1.executeQuery();

		while (resultSet1.next()) {
			long classPK = resultSet1.getLong("classPK");

			if (classPK != curClassPK) {
				cpConfigurationListIds = new HashSet<>();

				curClassPK = classPK;

				masterCPConfigurationEntry =
					_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
						cpDefinitionClassNameId, classPK,
						masterCPConfigurationListId);
			}

			if (masterCPConfigurationEntry == null) {
				continue;
			}

			long resourceId = resultSet1.getLong("resourceId");
			ResultSet resultSet2;

			String type = resultSet1.getString("type_");

			if (type.equals("A")) {
				preparedStatement2.setLong(1, accountGroupClassNameId);
				preparedStatement2.setLong(2, resourceId);
				preparedStatement2.setLong(
					3, masterCPConfigurationEntry.getGroupId());

				resultSet2 = preparedStatement2.executeQuery();
			}
			else {
				preparedStatement3.setLong(1, cpConfigurationListClassNameId);
				preparedStatement3.setLong(2, resourceId);
				preparedStatement3.setLong(
					3, masterCPConfigurationEntry.getGroupId());

				resultSet2 = preparedStatement3.executeQuery();
			}

			while (resultSet2.next()) {
				long cpConfigurationListId = resultSet2.getLong(
					"CPConfigurationListId");

				if (cpConfigurationListIds.contains(cpConfigurationListId)) {
					continue;
				}

				CPConfigurationEntry cpConfigurationEntry =
					_cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
						cpDefinitionClassNameId, classPK,
						cpConfigurationListId);

				if (cpConfigurationEntry != null) {
					continue;
				}

				cpConfigurationListIds.add(cpConfigurationListId);

				_cpConfigurationEntryLocalService.addCPConfigurationEntry(
					null, masterCPConfigurationEntry.getUserId(),
					masterCPConfigurationEntry.getGroupId(),
					cpDefinitionClassNameId, classPK, cpConfigurationListId,
					masterCPConfigurationEntry.getCPTaxCategoryId(),
					masterCPConfigurationEntry.getAllowedOrderQuantities(),
					masterCPConfigurationEntry.isBackOrders(),
					masterCPConfigurationEntry.
						getCommerceAvailabilityEstimateId(),
					masterCPConfigurationEntry.getCPDefinitionInventoryEngine(),
					masterCPConfigurationEntry.getDepth(),
					masterCPConfigurationEntry.isDisplayAvailability(),
					masterCPConfigurationEntry.isDisplayStockQuantity(),
					masterCPConfigurationEntry.isFreeShipping(),
					masterCPConfigurationEntry.getHeight(),
					masterCPConfigurationEntry.getLowStockActivity(),
					masterCPConfigurationEntry.getMaxOrderQuantity(),
					masterCPConfigurationEntry.getMinOrderQuantity(),
					masterCPConfigurationEntry.getMinStockQuantity(),
					masterCPConfigurationEntry.getMultipleOrderQuantity(),
					masterCPConfigurationEntry.isPurchasable(),
					masterCPConfigurationEntry.isShippable(),
					masterCPConfigurationEntry.getShippingExtraPrice(),
					masterCPConfigurationEntry.isShipSeparately(),
					masterCPConfigurationEntry.isTaxExempt(), true,
					masterCPConfigurationEntry.getWeight(),
					masterCPConfigurationEntry.getWidth());
			}
		}
	}

	private void _addCPConfigurationLists(
			CPConfigurationList masterCPConfigurationList,
			PreparedStatement preparedStatement)
		throws PortalException, SQLException {

		long currentClassPK = 0;
		String currentType = StringPool.BLANK;
		boolean monoType = true;
		List<Set<String>> monoTypeList = new ArrayList<>();
		List<Set<String>> multiTypeList = new ArrayList<>();
		Set<String> typeSet = new HashSet<>();

		ResultSet resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			long classPK = resultSet.getLong("classPK");
			long resourceId = resultSet.getLong("resourceId");
			String type = resultSet.getString("type_");

			if (classPK != currentClassPK) {
				if (monoType) {
					monoTypeList.add(typeSet);
				}
				else {
					multiTypeList.add(typeSet);
				}

				currentClassPK = classPK;
				currentType = type;
				monoType = true;
				typeSet = new HashSet<>();
			}

			if (!type.equals(currentType) && monoType) {
				monoType = false;
			}

			typeSet.add(type + resourceId);
		}

		if (monoType) {
			monoTypeList.add(typeSet);
		}
		else {
			multiTypeList.add(typeSet);
		}

		_clearList(monoTypeList);
		_clearList(multiTypeList);

		Calendar calendar = Calendar.getInstance();
		long index = 0;

		List<Set<String>> typeList = new ArrayList<>();

		typeList.addAll(monoTypeList);
		typeList.addAll(multiTypeList);

		for (Set<String> currentSet : typeList) {
			if (currentSet.isEmpty()) {
				continue;
			}

			index = index + 1;

			CPConfigurationList cpConfigurationList =
				_cpConfigurationListLocalService.addCPConfigurationList(
					null, masterCPConfigurationList.getUserId(),
					masterCPConfigurationList.getGroupId(),
					masterCPConfigurationList.getCPConfigurationListId(), false,
					masterCPConfigurationList.getName() + " " + index,
					masterCPConfigurationList.getPriority(),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true);

			for (String type : currentSet) {
				long resourceId = GetterUtil.getLong(type.substring(1));

				if (type.startsWith("A")) {
					_cpConfigurationListRelLocalService.
						addCPConfigurationListRel(
							cpConfigurationList.getUserId(),
							AccountGroup.class.getName(), resourceId,
							cpConfigurationList.getCPConfigurationListId());
				}
				else {
					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setCompanyId(
						cpConfigurationList.getCompanyId());
					serviceContext.setUserId(cpConfigurationList.getUserId());

					_commerceChannelRelLocalService.addCommerceChannelRel(
						CPConfigurationList.class.getName(),
						cpConfigurationList.getCPConfigurationListId(),
						resourceId, serviceContext);
				}
			}
		}
	}

	private void _addData(long companyId) {
		List<Group> groups = _groupLocalService.getGroups(
			companyId, CommerceCatalog.class.getName(), 0);

		for (Group group : groups) {
			List<CPConfigurationList> cpConfigurationLists =
				_cpConfigurationListLocalService.getCPConfigurationLists(
					group.getGroupId(), companyId);

			for (CPConfigurationList cpConfigurationList :
					cpConfigurationLists) {

				if (!cpConfigurationList.isMaster()) {
					return;
				}
			}
		}

		long accountGroupClassNameId = _portal.getClassNameId(
			AccountGroup.class.getName());
		long commerceCatalogClassNameId = _portal.getClassNameId(
			CommerceCatalog.class.getName());
		long cpConfigurationListClassNameId = _portal.getClassNameId(
			CPConfigurationList.class.getName());
		long cpDefinitionClassNameId = _portal.getClassNameId(
			CPDefinition.class.getName());

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select Group_.groupId, Group_.companyId from ",
					"CommerceCatalog join Group_ on Group_.companyId = ? and ",
					"Group_.classNameId = ? and Group_.classPK = ",
					"CommerceCatalog.commerceCatalogId"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"select CPDefinition.CPDefinitionId, TEMP_TABLE.type_, ",
					"TEMP_TABLE.classPK, TEMP_TABLE.resourceId from ",
					"CPDefinition join (select 'C' as type_, classPK, ",
					"commerceChannelId as resourceId from CommerceChannelRel ",
					"where classNameId = ? union select 'A' as type_, ",
					"classPK, accountGroupId as resourceId from ",
					"AccountGroupRel where classNameId = ?) TEMP_TABLE on ",
					"(CPDefinition.CPDefinitionId = TEMP_TABLE.classPK and ",
					"CPDefinition.groupId = ?) order by TEMP_TABLE.classPK"));
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct CPConfigurationListRel.",
					"CPConfigurationListId from CPConfigurationListRel join ",
					"CPConfigurationList on CPConfigurationList.",
					"CPConfigurationListId = CPConfigurationListRel.",
					"CPConfigurationListId where CPConfigurationListRel.",
					"classNameId = ? and CPConfigurationListRel.classPK = ? ",
					"and CPConfigurationList.groupId = ?"));
			PreparedStatement preparedStatement4 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct classPK as CPConfigurationListId from ",
					"CommerceChannelRel join CPConfigurationList on ",
					"CPConfigurationList.CPConfigurationListId = ",
					"CommerceChannelRel.classPK where CommerceChannelRel.",
					"classNameId = ? and CommerceChannelRel.commerceChannelId ",
					"= ? and CPConfigurationList.groupId = ?"));
			PreparedStatement preparedStatement5 = connection.prepareStatement(
				StringBundler.concat(
					"update CPConfigurationEntry set visible = ? where ",
					"groupId = ? and classNameId = ? and classPK in (select ",
					"classPK from (select classPK from CPConfigurationEntry ",
					"where groupId = ? and classNameId = ? and ",
					"CPConfigurationListId != ?) TEMP_TABLE) and ",
					"CPConfigurationListId = ?"))) {

			preparedStatement1.setLong(1, companyId);
			preparedStatement1.setLong(2, commerceCatalogClassNameId);

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");

				CPConfigurationList masterCPConfigurationList =
					_cpConfigurationListLocalService.
						getMasterCPConfigurationList(groupId);

				if (masterCPConfigurationList == null) {
					continue;
				}

				CPConfigurationEntry templateCPConfigurationEntry =
					masterCPConfigurationList.
						fetchTemplateCPConfigurationEntry();

				if (templateCPConfigurationEntry == null) {
					continue;
				}

				preparedStatement2.setLong(1, cpDefinitionClassNameId);
				preparedStatement2.setLong(2, cpDefinitionClassNameId);
				preparedStatement2.setLong(3, groupId);

				_addCPConfigurationLists(
					masterCPConfigurationList, preparedStatement2);

				_addCPConfigurationEntries(
					accountGroupClassNameId, cpConfigurationListClassNameId,
					cpDefinitionClassNameId,
					masterCPConfigurationList.getCPConfigurationListId(),
					preparedStatement2, preparedStatement3, preparedStatement4);

				_updateMasterCPConfigurationEntries(
					cpDefinitionClassNameId, groupId, masterCPConfigurationList,
					preparedStatement5);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _clearList(List<Set<String>> list) {
		for (int i = 0; i < list.size(); i++) {
			Set<String> set1 = list.get(i);

			if (set1.isEmpty()) {
				continue;
			}

			for (int j = 0; j < list.size(); j++) {
				if (j == i) {
					continue;
				}

				Set<String> set2 = list.get(j);

				if (set2.isEmpty()) {
					continue;
				}

				if (set2.containsAll(set1)) {
					list.set(i, new HashSet<>());

					break;
				}

				if (set1.containsAll(set2)) {
					list.set(j, new HashSet<>());

					break;
				}
			}
		}
	}

	private void _deleteData(long companyId) throws Exception {
		List<Group> groups = _groupLocalService.getGroups(
			companyId, CommerceCatalog.class.getName(), 0);

		for (Group group : groups) {
			List<CPConfigurationList> cpConfigurationLists =
				_cpConfigurationListLocalService.getCPConfigurationLists(
					group.getGroupId(), companyId);

			for (CPConfigurationList cpConfigurationList :
					cpConfigurationLists) {

				if (cpConfigurationList.isMaster()) {
					continue;
				}

				_commerceChannelRelLocalService.deleteCommerceChannelRels(
					CPConfigurationList.class.getName(),
					cpConfigurationList.getCPConfigurationListId());
				_cpConfigurationListLocalService.deleteCPConfigurationList(
					cpConfigurationList, true);
				_cpConfigurationListRelLocalService.
					deleteCPConfigurationListRels(
						cpConfigurationList.getCPConfigurationListId());
			}
		}
	}

	private void _updateMasterCPConfigurationEntries(
			long cpDefinitionClassNameId, long groupId,
			CPConfigurationList masterCPConfigurationList,
			PreparedStatement preparedStatement)
		throws Exception {

		preparedStatement.setBoolean(1, false);
		preparedStatement.setLong(2, groupId);
		preparedStatement.setLong(3, cpDefinitionClassNameId);
		preparedStatement.setLong(4, groupId);
		preparedStatement.setLong(5, cpDefinitionClassNameId);
		preparedStatement.setLong(
			6, masterCPConfigurationList.getCPConfigurationListId());
		preparedStatement.setLong(
			7, masterCPConfigurationList.getCPConfigurationListId());

		preparedStatement.executeUpdate();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationListEligibilityCommerceHealthStatus.class);

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

	@Reference
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Reference
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}