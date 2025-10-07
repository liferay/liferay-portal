/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.constants.CommerceReturnConstants;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceReturn;
import com.liferay.commerce.model.CommerceReturnItem;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceReturnRequestHelper;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.comment.DiscussionComment;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class CommerceReturnEditDisplayContext {

	public CommerceReturnEditDisplayContext(
			AccountEntryLocalService accountEntryLocalService,
			ClassNameLocalService classNameLocalService,
			CommerceOrderItemLocalService commerceOrderItemLocalService,
			CommerceOrderLocalService commerceOrderLocalService,
			CommercePaymentEntryService commercePaymentEntryService,
			CommercePriceFormatter commercePriceFormatter, Language language,
			ListTypeDefinitionService listTypeDefinitionService,
			ListTypeEntryService listTypeEntryService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			RenderRequest renderRequest)
		throws PortalException {

		_accountEntryLocalService = accountEntryLocalService;
		_classNameLocalService = classNameLocalService;
		_commerceOrderItemLocalService = commerceOrderItemLocalService;
		_commerceOrderLocalService = commerceOrderLocalService;
		_commercePaymentEntryService = commercePaymentEntryService;
		_commercePriceFormatter = commercePriceFormatter;
		_language = language;
		_listTypeDefinitionService = listTypeDefinitionService;
		_listTypeEntryService = listTypeEntryService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;

		_commerceReturnRequestHelper = new CommerceReturnRequestHelper(
			renderRequest);

		ThemeDisplay themeDisplay =
			_commerceReturnRequestHelper.getThemeDisplay();

		_commerceDateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.SHORT, DateFormat.SHORT, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long commerceReturnId = getCommerceReturnId();

		if (commerceReturnId > 0) {
			_commerceReturn = new CommerceReturn(
				objectEntryLocalService.getObjectEntry(commerceReturnId));
		}
		else {
			_commerceReturn = null;
		}
	}

	public String getAmountFormatted(BigDecimal amount) throws PortalException {
		CommerceOrder commerceOrder = getCommerceReturnCommerceOrder();

		return _commercePriceFormatter.format(
			commerceOrder.getCommerceCurrency(), amount,
			_commerceReturnRequestHelper.getLocale());
	}

	public String getCommerceOrderShippingAmountFormatted()
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceReturnCommerceOrder();

		return _commercePriceFormatter.format(
			commerceOrder.getCommerceCurrency(),
			commerceOrder.getShippingAmount(),
			_commerceReturnRequestHelper.getLocale());
	}

	public CommerceReturn getCommerceReturn() {
		return _commerceReturn;
	}

	public AccountEntry getCommerceReturnAccountEntry() throws PortalException {
		if (_commerceReturn == null) {
			return null;
		}

		if (_accountEntry != null) {
			return _accountEntry;
		}

		_accountEntry = _accountEntryLocalService.getAccountEntry(
			_commerceReturn.getAccountId());

		return _accountEntry;
	}

	public String getCommerceReturnAccountEntryThumbnailURL()
		throws PortalException {

		if (_commerceReturn == null) {
			return StringPool.BLANK;
		}

		AccountEntry accountEntry = getCommerceReturnAccountEntry();

		ThemeDisplay themeDisplay =
			_commerceReturnRequestHelper.getThemeDisplay();

		StringBundler sb = new StringBundler(5);

		sb.append(themeDisplay.getPathImage());
		sb.append("/organization_logo?img_id=");
		sb.append(accountEntry.getLogoId());

		if (accountEntry.getLogoId() > 0) {
			sb.append("&t=");
			sb.append(
				WebServerServletTokenUtil.getToken(accountEntry.getLogoId()));
		}

		return sb.toString();
	}

	public CommerceOrder getCommerceReturnCommerceOrder()
		throws PortalException {

		if (_commerceReturn == null) {
			return null;
		}

		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceReturn.getOrderId());

		return _commerceOrder;
	}

	public long getCommerceReturnId() {
		if (_commerceReturnId > 0) {
			return _commerceReturnId;
		}

		_commerceReturnId = ParamUtil.getLong(
			_commerceReturnRequestHelper.getRequest(), "commerceReturnId");

		return _commerceReturnId;
	}

	public CommerceReturnItem getCommerceReturnItem() {
		if (_commerceReturnItem != null) {
			return _commerceReturnItem;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			getCommerceReturnItemId());

		if (objectEntry == null) {
			return _commerceReturnItem;
		}

		_commerceReturnItem = new CommerceReturnItem(objectEntry);

		return _commerceReturnItem;
	}

	public String getCommerceReturnItemClassName() throws PortalException {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_commerceReturnRequestHelper.getCompanyId(),
				"CommerceReturnItem");

		return objectDefinition.getClassName();
	}

	public DropdownItemList getCommerceReturnItemCommentDropdownItemList(
			DiscussionComment discussionComment)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			_commerceReturnRequestHelper.getRequest();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.create(
						PortletProviderUtil.getPortletURL(
							_commerceReturnRequestHelper.getRequest(),
							CommerceReturn.class.getName(),
							PortletProvider.Action.MANAGE)
					).setMVCRenderCommandName(
						"/commerce_return/edit_commerce_return_item_comment"
					).setParameter(
						"commentId", discussionComment.getCommentId()
					).setParameter(
						"commerceReturnId", getCommerceReturnId()
					).setParameter(
						"commerceReturnItemId", discussionComment.getClassPK()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildRenderURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "edit"));
			}
		).add(
			dropdownItem -> {
				PortletDisplay portletDisplay =
					_commerceReturnRequestHelper.getPortletDisplay();

				ThemeDisplay themeDisplay =
					_commerceReturnRequestHelper.getThemeDisplay();

				dropdownItem.setHref(
					PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							_commerceReturnRequestHelper.getRequest(),
							portletDisplay.getId(), themeDisplay.getPlid(),
							PortletRequest.ACTION_PHASE)
					).setActionName(
						"/commerce_return/edit_commerce_return_item_comment"
					).setCMD(
						Constants.DELETE
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"commentId", discussionComment.getCommentId()
					).buildActionURL());

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
			}
		).build();
	}

	public CommerceOrderItem getCommerceReturnItemCommerceOrderItem()
		throws PortalException {

		if (_commerceOrderItem != null) {
			return _commerceOrderItem;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			getCommerceReturnItemId());

		if (objectEntry == null) {
			return null;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		_commerceOrderItem =
			_commerceOrderItemLocalService.fetchCommerceOrderItem(
				GetterUtil.getLong(
					values.get(
						"r_commerceOrderItemToCommerceReturnItems_" +
							"commerceOrderItemId")));

		return _commerceOrderItem;
	}

	public List<FDSActionDropdownItem>
			getCommerceReturnItemFDSActionDropdownItems()
		throws Exception {

		CommerceReturn commerceReturn = getCommerceReturn();

		if (StringUtil.equals(
				commerceReturn.getReturnStatus(),
				CommerceReturnConstants.RETURN_STATUS_DRAFT)) {

			return Collections.emptyList();
		}

		HttpServletRequest httpServletRequest =
			_commerceReturnRequestHelper.getRequest();

		Map<String, Integer> returnItemStatusMap = _toReturnItemStatusMap(
			_getCommerceReturnItemObjectEntries());

		int processedCount = GetterUtil.getInteger(
			returnItemStatusMap.get("processedCount"));

		if (ArrayUtil.contains(
				CommerceReturnConstants.RETURN_STATUSES_LATEST,
				commerceReturn.getReturnStatus()) ||
			(processedCount > 0)) {

			return ListUtil.fromArray(
				new FDSActionDropdownItem(
					PortletURLBuilder.create(
						PortletProviderUtil.getPortletURL(
							_commerceReturnRequestHelper.getRequest(),
							CommerceReturn.class.getName(),
							PortletProvider.Action.MANAGE)
					).setMVCRenderCommandName(
						"/commerce_return/edit_commerce_return_item"
					).setParameter(
						"commerceReturnId", getCommerceReturnId()
					).setParameter(
						"commerceReturnItemId", "{id}"
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString(),
					null, "edit",
					_language.get(httpServletRequest, "view-details"), "get",
					"get", "sidePanel"));
		}

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						_commerceReturnRequestHelper.getRequest(),
						CommerceReturn.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_return/edit_commerce_return_item"
				).setParameter(
					"commerceReturnId", this::getCommerceReturnId
				).setParameter(
					"commerceReturnItemId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				null, "edit", _language.get(httpServletRequest, "edit"), "get",
				"get", "sidePanel"),
			new FDSActionDropdownItem(
				null, null, "removeReturnItem",
				_language.get(httpServletRequest, "delete"), null, null, null));
	}

	public long getCommerceReturnItemId() {
		if (_commerceReturnItemId > 0) {
			return _commerceReturnItemId;
		}

		_commerceReturnItemId = ParamUtil.getLong(
			_commerceReturnRequestHelper.getRequest(), "commerceReturnItemId");

		return _commerceReturnItemId;
	}

	public String getDateTimeFormatted(Date date) {
		if (date == null) {
			return StringPool.BLANK;
		}

		return _commerceDateTimeFormat.format(date);
	}

	public String getDescriptiveAddress(CommerceAddress commerceAddress) {
		StringBundler sb = new StringBundler(5);

		sb.append(HtmlUtil.escape(commerceAddress.getCity()));
		sb.append(StringPool.COMMA_AND_SPACE);

		try {
			Region region = commerceAddress.getRegion();

			if (region != null) {
				sb.append(HtmlUtil.escape(region.getName()));
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		sb.append(HtmlUtil.escape(commerceAddress.getZip()));

		return sb.toString();
	}

	public List<DiscussionComment> getDiscussionComments()
		throws PortalException {

		if (!CommentManagerUtil.hasDiscussion(
				getCommerceReturnItemClassName(), getCommerceReturnItemId())) {

			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay =
			_commerceReturnRequestHelper.getThemeDisplay();

		Discussion discussion = CommentManagerUtil.getDiscussion(
			themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
			getCommerceReturnItemClassName(), getCommerceReturnItemId(),
			new ServiceContextFunction(
				_commerceReturnRequestHelper.getRequest()));

		DiscussionComment discussionComment =
			discussion.getRootDiscussionComment();

		return discussionComment.getDescendantComments();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		LiferayPortletResponse liferayPortletResponse =
			_commerceReturnRequestHelper.getLiferayPortletResponse();

		CommerceReturn commerceReturn = getCommerceReturn();

		if (StringUtil.equals(
				commerceReturn.getReturnStatus(),
				CommerceReturnConstants.RETURN_STATUS_PROCESSING)) {

			Map<String, Integer> returnItemStatusMap = _toReturnItemStatusMap(
				_getCommerceReturnItemObjectEntries());

			int toBeProcessedCount = GetterUtil.getInteger(
				returnItemStatusMap.get("toBeProcessedCount"));

			if ((toBeProcessedCount > 0) && _hasStatusCompleted()) {
				headerActionModels.add(
					new HeaderActionModel(
						"btn-secondary", null,
						PortletURLBuilder.create(
							PortletProviderUtil.getPortletURL(
								_commerceReturnRequestHelper.getRequest(),
								CommercePaymentEntry.class.getName(),
								PortletProvider.Action.MANAGE)
						).setMVCRenderCommandName(
							"/commerce_payment/edit_commerce_payment_entry"
						).setParameter(
							"className", CommercePaymentEntry.class.getName()
						).setParameter(
							"classPK", _commercePaymentEntryId
						).setParameter(
							"commerceReturnId", getCommerceReturnId()
						).buildString(),
						null, "create-refund"));
			}

			int processedCount = GetterUtil.getInteger(
				returnItemStatusMap.get("processedCount"));

			if ((toBeProcessedCount > 0) || (processedCount > 0)) {
				headerActionModels.add(
					new HeaderActionModel(
						"btn-primary",
						liferayPortletResponse.getNamespace() + "fm", null,
						liferayPortletResponse.getNamespace() +
							"markAsCompletedButton",
						"mark-as-completed"));
			}
		}

		if (StringUtil.equals(
				commerceReturn.getReturnStatus(),
				CommerceReturnConstants.RETURN_STATUS_REJECTED)) {

			headerActionModels.add(
				new HeaderActionModel(
					"btn-secondary disabled",
					liferayPortletResponse.getNamespace() + "fm",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/commerce_return/edit_commerce_return"
					).buildString(),
					null, "reopen-return"));
		}

		return headerActionModels;
	}

	public String getListTypeEntriesByExternalReferenceCodeURL() {
		return StringBundler.concat(
			"/o/headless-admin-list-type/v1.0/list-type-definitions",
			"/by-external-reference-code/L_COMMERCE_RETURN_RESOLUTION_METHODS",
			"/list-type-entries");
	}

	public String getResolutionMethodName() throws PortalException {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_COMMERCE_RETURN_RESOLUTION_METHODS",
					_commerceReturnRequestHelper.getCompanyId());

		if (listTypeDefinition == null) {
			return StringPool.BLANK;
		}

		CommerceReturnItem commerceReturnItem = getCommerceReturnItem();

		if (commerceReturnItem == null) {
			return StringPool.BLANK;
		}

		for (ListTypeEntry listTypeEntry :
				_listTypeEntryService.getListTypeEntries(
					listTypeDefinition.getListTypeDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (Objects.equals(
					listTypeEntry.getKey(),
					commerceReturnItem.getReturnResolutionMethod())) {

				return listTypeEntry.getName(
					_commerceReturnRequestHelper.getLocale());
			}
		}

		return StringPool.BLANK;
	}

	public String getResolutionMethodName(String resolutionMethodKey)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_COMMERCE_RETURN_RESOLUTION_METHODS",
					_commerceReturnRequestHelper.getCompanyId());

		if (listTypeDefinition == null) {
			return StringPool.BLANK;
		}

		CommerceReturnItem commerceReturnItem = getCommerceReturnItem();

		if (commerceReturnItem == null) {
			return StringPool.BLANK;
		}

		List<ListTypeEntry> listTypeEntries = ListUtil.filter(
			_listTypeEntryService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			listTypeEntry -> StringUtil.equals(
				listTypeEntry.getKey(), resolutionMethodKey));

		if (ListUtil.isNotEmpty(listTypeEntries)) {
			ListTypeEntry listTypeEntry = listTypeEntries.get(0);

			return listTypeEntry.getName(
				_commerceReturnRequestHelper.getLocale());
		}

		return StringPool.BLANK;
	}

	public String getReturnReasonName() throws PortalException {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_COMMERCE_RETURN_REASONS",
					_commerceReturnRequestHelper.getCompanyId());

		if (listTypeDefinition == null) {
			return StringPool.BLANK;
		}

		CommerceReturnItem commerceReturnItem = getCommerceReturnItem();

		if (commerceReturnItem == null) {
			return StringPool.BLANK;
		}

		for (ListTypeEntry listTypeEntry :
				_listTypeEntryService.getListTypeEntries(
					listTypeDefinition.getListTypeDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (Objects.equals(
					listTypeEntry.getKey(),
					commerceReturnItem.getReturnReason())) {

				return listTypeEntry.getName(
					_commerceReturnRequestHelper.getLocale());
			}
		}

		return StringPool.BLANK;
	}

	private List<ObjectEntry> _getCommerceReturnItemObjectEntries()
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			getCommerceReturnId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectEntry.getObjectDefinitionId(),
				"commerceReturnToCommerceReturnItems");

		return _objectEntryLocalService.getOneToManyObjectEntries(
			objectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, false,
			objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	private boolean _hasStatusCompleted() throws Exception {
		CommerceOrder commerceOrder = getCommerceReturnCommerceOrder();

		List<CommercePaymentEntry> commercePaymentEntries =
			_commercePaymentEntryService.getCommercePaymentEntries(
				commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (CommercePaymentEntry commercePaymentEntry :
				commercePaymentEntries) {

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_COMPLETED) {

				_commercePaymentEntryId =
					commercePaymentEntry.getCommercePaymentEntryId();

				break;
			}
		}

		if ((CommerceOrderPaymentConstants.STATUS_COMPLETED ==
				_commerceOrder.getPaymentStatus()) &&
			(_commercePaymentEntryId > 0)) {

			return true;
		}

		return false;
	}

	private Map<String, Integer> _toReturnItemStatusMap(
		List<ObjectEntry> objectEntries) {

		Map<String, Integer> commerceReturnItemMap = new HashMap<>();

		for (ObjectEntry objectEntry : objectEntries) {
			String key = null;

			Map<String, Serializable> commerceReturnItemValues =
				objectEntry.getValues();

			String returnItemStatus = GetterUtil.getString(
				commerceReturnItemValues.get("returnItemStatus"));

			if (StringUtil.equals(
					returnItemStatus,
					CommerceReturnConstants.
						RETURN_ITEM_STATUS_TO_BE_PROCESSED)) {

				key = "toBeProcessedCount";
			}

			if (StringUtil.equals(
					returnItemStatus,
					CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED)) {

				key = "processedCount";
			}

			if (Validator.isNotNull(key)) {
				Integer count = commerceReturnItemMap.get(key);

				if (count == null) {
					count = 0;
				}

				count += 1;
				commerceReturnItemMap.put(key, count);
			}
		}

		return commerceReturnItemMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceReturnEditDisplayContext.class);

	private AccountEntry _accountEntry;
	private final AccountEntryLocalService _accountEntryLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final Format _commerceDateTimeFormat;
	private CommerceOrder _commerceOrder;
	private CommerceOrderItem _commerceOrderItem;
	private final CommerceOrderItemLocalService _commerceOrderItemLocalService;
	private final CommerceOrderLocalService _commerceOrderLocalService;
	private long _commercePaymentEntryId;
	private final CommercePaymentEntryService _commercePaymentEntryService;
	private final CommercePriceFormatter _commercePriceFormatter;
	private final CommerceReturn _commerceReturn;
	private long _commerceReturnId;
	private CommerceReturnItem _commerceReturnItem;
	private long _commerceReturnItemId;
	private final CommerceReturnRequestHelper _commerceReturnRequestHelper;
	private final Language _language;
	private final ListTypeDefinitionService _listTypeDefinitionService;
	private final ListTypeEntryService _listTypeEntryService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}