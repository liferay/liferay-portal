/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.exception.MailingListEmailAddressException;
import com.liferay.message.boards.exception.MailingListInServerNameException;
import com.liferay.message.boards.exception.MailingListInUserNameException;
import com.liferay.message.boards.exception.MailingListOutEmailAddressException;
import com.liferay.message.boards.exception.MailingListOutServerNameException;
import com.liferay.message.boards.exception.MailingListOutUserNameException;
import com.liferay.message.boards.internal.messaging.MailingListRequest;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.service.base.MBMailingListLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Closeable;

import java.util.Calendar;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Moreira
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBMailingList",
	service = AopService.class
)
public class MBMailingListLocalServiceImpl
	extends MBMailingListLocalServiceBaseImpl {

	@Override
	public MBMailingList addMailingList(
			long userId, long groupId, long categoryId, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean active, ServiceContext serviceContext)
		throws PortalException {

		// Message boards mailing list

		User user = _userLocalService.getUser(userId);

		_validate(
			emailAddress, inServerName, inUserName, outEmailAddress, outCustom,
			outServerName, outUserName, active);

		long mailingListId = counterLocalService.increment();

		MBMailingList mailingList = mbMailingListPersistence.create(
			mailingListId);

		mailingList.setUuid(serviceContext.getUuid());
		mailingList.setGroupId(groupId);
		mailingList.setCompanyId(user.getCompanyId());
		mailingList.setUserId(user.getUserId());
		mailingList.setUserName(user.getFullName());
		mailingList.setCategoryId(categoryId);
		mailingList.setEmailAddress(emailAddress);
		mailingList.setInProtocol(inUseSSL ? inProtocol + "s" : inProtocol);
		mailingList.setInServerName(inServerName);
		mailingList.setInServerPort(inServerPort);
		mailingList.setInUseSSL(inUseSSL);
		mailingList.setInUserName(inUserName);
		mailingList.setInPassword(inPassword);
		mailingList.setInReadInterval(inReadInterval);
		mailingList.setOutEmailAddress(outEmailAddress);
		mailingList.setOutCustom(outCustom);
		mailingList.setOutServerName(outServerName);
		mailingList.setOutServerPort(outServerPort);
		mailingList.setOutUseSSL(outUseSSL);
		mailingList.setOutUserName(outUserName);
		mailingList.setOutPassword(outPassword);
		mailingList.setAllowAnonymous(allowAnonymous);
		mailingList.setActive(active);

		mailingList = mbMailingListPersistence.update(mailingList);

		// Scheduler

		if (active) {
			_scheduleMailingList(mailingList);
		}

		return mailingList;
	}

	@Override
	public void deleteMailingList(long mailingListId) throws PortalException {
		MBMailingList mailingList = mbMailingListPersistence.findByPrimaryKey(
			mailingListId);

		deleteMailingList(mailingList);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteMailingList(MBMailingList mailingList)
		throws PortalException {

		_unscheduleMailingList(mailingList);

		mbMailingListPersistence.remove(mailingList);
	}

	@Override
	public MBMailingList fetchCategoryMailingList(
		long groupId, long categoryId) {

		return mbMailingListPersistence.fetchByG_C(groupId, categoryId);
	}

	@Override
	public MBMailingList updateMailingList(
			long mailingListId, String emailAddress, String inProtocol,
			String inServerName, int inServerPort, boolean inUseSSL,
			String inUserName, String inPassword, int inReadInterval,
			String outEmailAddress, boolean outCustom, String outServerName,
			int outServerPort, boolean outUseSSL, String outUserName,
			String outPassword, boolean allowAnonymous, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		// Message boards mailing list

		_validate(
			emailAddress, inServerName, inUserName, outEmailAddress, outCustom,
			outServerName, outUserName, active);

		MBMailingList mailingList = mbMailingListPersistence.findByPrimaryKey(
			mailingListId);

		mailingList.setEmailAddress(emailAddress);
		mailingList.setInProtocol(inUseSSL ? inProtocol + "s" : inProtocol);
		mailingList.setInServerName(inServerName);
		mailingList.setInServerPort(inServerPort);
		mailingList.setInUseSSL(inUseSSL);
		mailingList.setInUserName(inUserName);
		mailingList.setInPassword(inPassword);
		mailingList.setInReadInterval(inReadInterval);
		mailingList.setOutEmailAddress(outEmailAddress);
		mailingList.setOutCustom(outCustom);
		mailingList.setOutServerName(outServerName);
		mailingList.setOutServerPort(outServerPort);
		mailingList.setOutUseSSL(outUseSSL);
		mailingList.setOutUserName(outUserName);
		mailingList.setOutPassword(outPassword);
		mailingList.setAllowAnonymous(allowAnonymous);
		mailingList.setActive(active);

		mailingList = mbMailingListPersistence.update(mailingList);

		// Scheduler

		_unscheduleMailingList(mailingList);

		if (active) {
			_scheduleMailingList(mailingList);
		}

		return mailingList;
	}

	@Activate
	protected void activate() {
		_unregister = _liferayJSONDeserializationWhitelist.register(
			MailingListRequest.class.getName());
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		try {
			_unregister.close();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getSchedulerGroupName(MBMailingList mailingList) {
		return StringBundler.concat(
			DestinationNames.MESSAGE_BOARDS_MAILING_LIST, StringPool.SLASH,
			mailingList.getMailingListId(), StringPool.AT,
			mailingList.getCompanyId());
	}

	private void _scheduleMailingList(MBMailingList mailingList)
		throws PortalException {

		String groupName = _getSchedulerGroupName(mailingList);

		Calendar startDate = CalendarFactoryUtil.getCalendar();

		Trigger trigger = _triggerFactory.createTrigger(
			groupName, groupName, startDate.getTime(), null,
			mailingList.getInReadInterval(), TimeUnit.MINUTE);

		MailingListRequest mailingListRequest = new MailingListRequest();

		mailingListRequest.setCompanyId(mailingList.getCompanyId());
		mailingListRequest.setUserId(mailingList.getUserId());
		mailingListRequest.setGroupId(mailingList.getGroupId());
		mailingListRequest.setCategoryId(mailingList.getCategoryId());
		mailingListRequest.setInProtocol(mailingList.getInProtocol());
		mailingListRequest.setInServerName(mailingList.getInServerName());
		mailingListRequest.setInServerPort(mailingList.getInServerPort());
		mailingListRequest.setInUseSSL(mailingList.isInUseSSL());
		mailingListRequest.setInUserName(mailingList.getInUserName());
		mailingListRequest.setInPassword(mailingList.getInPassword());
		mailingListRequest.setAllowAnonymous(mailingList.isAllowAnonymous());

		Message message = new Message();

		message.put("companyId", mailingList.getCompanyId());

		message.setPayload(mailingListRequest);

		SchedulerEngineHelperUtil.schedule(
			trigger, StorageType.PERSISTED, null,
			DestinationNames.MESSAGE_BOARDS_MAILING_LIST, message);
	}

	private void _unscheduleMailingList(MBMailingList mailingList)
		throws PortalException {

		String groupName = _getSchedulerGroupName(mailingList);

		SchedulerEngineHelperUtil.delete(groupName, StorageType.PERSISTED);
	}

	private void _validate(
			String emailAddress, String inServerName, String inUserName,
			String outEmailAddress, boolean outCustom, String outServerName,
			String outUserName, boolean active)
		throws PortalException {

		if (!active) {
			return;
		}

		if (!Validator.isEmailAddress(emailAddress)) {
			throw new MailingListEmailAddressException(emailAddress);
		}
		else if (Validator.isNull(inServerName)) {
			throw new MailingListInServerNameException(
				"In server name is null");
		}
		else if (Validator.isNull(inUserName)) {
			throw new MailingListInUserNameException("In user name is null");
		}
		else if (Validator.isNull(outEmailAddress)) {
			throw new MailingListOutEmailAddressException(
				"Out email address is null");
		}
		else if (outCustom) {
			if (Validator.isNull(outServerName)) {
				throw new MailingListOutServerNameException(
					"Out server name is null");
			}
			else if (Validator.isNull(outUserName)) {
				throw new MailingListOutUserNameException(
					"Out user name is null");
			}
		}
	}

	@Reference
	private LiferayJSONDeserializationWhitelist
		_liferayJSONDeserializationWhitelist;

	@Reference
	private TriggerFactory _triggerFactory;

	private Closeable _unregister;

	@Reference
	private UserLocalService _userLocalService;

}