/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.importer.type;

import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.configuration.CommerceOrderImporterTypeConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceOrderImporterTypeException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.importer.type.util.CommerceOrderImporterTypeUtil;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItemImpl;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.availability.CPAvailabilityChecker;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.math.BigDecimal;

import java.nio.charset.Charset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.configuration.CommerceOrderImporterTypeConfiguration",
	property = "commerce.order.importer.type.key=" + CSVCommerceOrderImporterTypeImpl.KEY,
	service = CommerceOrderImporterType.class
)
public class CSVCommerceOrderImporterTypeImpl
	implements CommerceOrderImporterType {

	public static final String KEY = "csv";

	@Override
	public Object getCommerceOrderImporterItem(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(
			httpServletRequest, getCommerceOrderImporterItemParamName());

		if (fileEntryId > 0) {
			return _dlAppLocalService.getFileEntry(fileEntryId);
		}

		return null;
	}

	@Override
	public String getCommerceOrderImporterItemParamName() {
		return "fileEntryId";
	}

	@Override
	public List<CommerceOrderImporterItem> getCommerceOrderImporterItems(
			CommerceOrder commerceOrder, FDSPagination fdsPagination,
			Object object)
		throws Exception {

		if ((object == null) || !(object instanceof FileEntry)) {
			throw new CommerceOrderImporterTypeException();
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		_commerceOrderImporterItemImpls = _getCommerceOrderImporterItemImpls(
			commerceOrder.getCompanyId(), commerceOrder.getCommerceAccountId(),
			commerceChannel.getGroupId(),
			commerceOrder.getCommerceOrderTypeId(), (FileEntry)object);

		int start = 0;
		int end = _commerceOrderImporterItemImpls.length;

		if (fdsPagination != null) {
			start = fdsPagination.getStartPosition();

			if (fdsPagination.getEndPosition() < end) {
				end = fdsPagination.getEndPosition();
			}
		}

		return CommerceOrderImporterTypeUtil.getCommerceOrderImporterItems(
			_commerceContextFactory, commerceOrder,
			Arrays.copyOfRange(_commerceOrderImporterItemImpls, start, end),
			_commerceOrderItemService, _commerceOrderPriceCalculation,
			_commerceOrderService, _userLocalService);
	}

	@Override
	public int getCommerceOrderImporterItemsCount(Object object)
		throws Exception {

		if (_commerceOrderImporterItemImpls == null) {
			CSVParser csvParser = _getCSVParser((FileEntry)object);

			List<CSVRecord> csvRecords = csvParser.getRecords();

			return csvRecords.size();
		}

		return _commerceOrderImporterItemImpls.length;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.format(resourceBundle, "import-from-x", KEY);
	}

	@Override
	public boolean isActive(CommerceOrder commerceOrder)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannel.getGroupId(),
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		if (commerceAccountGroupServiceConfiguration.commerceSiteType() ==
				CommerceChannelConstants.SITE_TYPE_B2C) {

			return false;
		}

		return true;
	}

	@Override
	public void render(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/pending_commerce_orders/importer_type/csv.jsp");
	}

	@Override
	public void renderCommerceOrderPreview(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/pending_commerce_orders/importer_type/common/preview.jsp");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_commerceOrderImporterTypeConfiguration =
			ConfigurableUtil.createConfigurable(
				CommerceOrderImporterTypeConfiguration.class, properties);
	}

	private CommerceOrderImporterItemImpl[] _getCommerceOrderImporterItemImpls(
			long companyId, long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, FileEntry fileEntry)
		throws Exception {

		CSVParser csvParser = _getCSVParser(fileEntry);

		return TransformUtil.transformToArray(
			csvParser.getRecords(),
			csvRecord -> _toCommerceOrderImporterItemImpl(
				companyId, accountEntryId, commerceChannelGroupId,
				commerceOrderTypeId, csvRecord),
			CommerceOrderImporterItemImpl.class);
	}

	private CSVParser _getCSVParser(FileEntry fileEntry) throws Exception {
		CSVFormat csvFormat = CommerceOrderImporterTypeUtil.getCSVFormat(
			_commerceOrderImporterTypeConfiguration);

		try {
			return CSVParser.parse(
				_file.createTempFile(fileEntry.getContentStream()),
				Charset.defaultCharset(), csvFormat);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			throw new CommerceOrderImporterTypeException();
		}
	}

	private CommerceOrderImporterItemImpl _toCommerceOrderImporterItemImpl(
			long companyId, long accountEntryId, long commerceChannelGroupId,
			long commerceOrderTypeId, CSVRecord csvRecord)
		throws Exception {

		String sku = GetterUtil.getString(csvRecord.get("sku"));
		BigDecimal quantity = BigDecimal.valueOf(
			GetterUtil.getInteger(csvRecord.get("quantity")));

		CPInstance cpInstance = null;

		if (Validator.isNotNull(sku)) {
			List<CPInstance> cpInstances =
				_cpInstanceLocalService.getCPInstances(companyId, sku);

			if (!cpInstances.isEmpty()) {
				cpInstance = cpInstances.get(0);
			}
		}

		if (cpInstance == null) {
			cpInstance =
				_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
					sku, companyId);
		}

		CommerceOrderImporterItemImpl commerceOrderImporterItemImpl =
			new CommerceOrderImporterItemImpl();

		if (cpInstance == null) {
			Company company = _companyLocalService.getCompany(companyId);

			if (Validator.isNotNull(sku)) {
				commerceOrderImporterItemImpl.setNameMap(
					Collections.singletonMap(company.getLocale(), sku));
			}
			else {
				commerceOrderImporterItemImpl.setNameMap(
					Collections.singletonMap(
						company.getLocale(), String.valueOf(sku)));
			}

			commerceOrderImporterItemImpl.setErrorMessages(
				new String[] {"the-product-is-no-longer-available"});
		}
		else {
			CPInstance firstAvailableReplacementCPInstance =
				_cpInstanceHelper.fetchFirstAvailableReplacementCPInstance(
					accountEntryId, commerceChannelGroupId, commerceOrderTypeId,
					cpInstance.getCPInstanceId());

			if ((firstAvailableReplacementCPInstance != null) &&
				!_cpAvailabilityChecker.check(
					accountEntryId, commerceChannelGroupId, cpInstance,
					StringPool.BLANK, quantity)) {

				commerceOrderImporterItemImpl.setReplacingSKU(
					cpInstance.getSku());

				cpInstance = firstAvailableReplacementCPInstance;
			}

			commerceOrderImporterItemImpl.setCPInstanceId(
				cpInstance.getCPInstanceId());
			commerceOrderImporterItemImpl.setSku(cpInstance.getSku());

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			commerceOrderImporterItemImpl.setCPDefinitionId(
				cpDefinition.getCPDefinitionId());
			commerceOrderImporterItemImpl.setNameMap(cpDefinition.getNameMap());
		}

		commerceOrderImporterItemImpl.setJSON("[]");
		commerceOrderImporterItemImpl.setQuantity(quantity);
		commerceOrderImporterItemImpl.setUnitOfMeasureKey(StringPool.BLANK);

		if (csvRecord.isMapped(_REQUESTED_DELIVERY_DATE_FIELD_NAME) &&
			csvRecord.isSet(_REQUESTED_DELIVERY_DATE_FIELD_NAME)) {

			commerceOrderImporterItemImpl.setRequestedDeliveryDateString(
				GetterUtil.getString(
					csvRecord.get(_REQUESTED_DELIVERY_DATE_FIELD_NAME)));
		}

		return commerceOrderImporterItemImpl;
	}

	private static final String _REQUESTED_DELIVERY_DATE_FIELD_NAME =
		"requestedDeliveryDate";

	private static final Log _log = LogFactoryUtil.getLog(
		CSVCommerceOrderImporterTypeImpl.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	private CommerceOrderImporterItemImpl[] _commerceOrderImporterItemImpls;
	private volatile CommerceOrderImporterTypeConfiguration
		_commerceOrderImporterTypeConfiguration;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPAvailabilityChecker _cpAvailabilityChecker;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private File _file;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}