/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.messaging;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.internal.util.MBMailMessage;
import com.liferay.message.boards.internal.util.MBMailUtil;
import com.liferay.message.boards.internal.util.MailingListThreadLocal;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.permission.PermissionCheckerUtil;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.URLName;
import jakarta.mail.internet.InternetAddress;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Moreira
 */
@Component(
	property = "destination.name=" + DestinationNames.MESSAGE_BOARDS_MAILING_LIST,
	service = MessageListener.class
)
public class MailingListMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Destination destination = _destinationFactory.createDestination(
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				DestinationNames.MESSAGE_BOARDS_MAILING_LIST));

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Override
	protected void doReceive(
			com.liferay.portal.kernel.messaging.Message message)
		throws Exception {

		MailingListRequest mailingListRequest =
			(MailingListRequest)message.getPayload();

		Store store = null;

		Folder folder = null;

		Message[] messages = null;

		try {
			store = _getStore(mailingListRequest);

			store.connect();

			folder = _getFolder(store);

			messages = folder.getMessages();

			_processMessages(mailingListRequest, messages);
		}
		finally {
			if ((folder != null) && folder.isOpen()) {
				try {
					folder.setFlags(
						messages, new Flags(Flags.Flag.DELETED), true);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				try {
					folder.close(true);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			if ((store != null) && store.isConnected()) {
				try {
					store.close();
				}
				catch (MessagingException messagingException) {
					if (_log.isDebugEnabled()) {
						_log.debug(messagingException);
					}
				}
			}
		}
	}

	private Folder _getFolder(Store store) throws Exception {
		Folder folder = store.getFolder("INBOX");

		if (!folder.exists()) {
			throw new MessagingException("Inbox not found");
		}

		folder.open(Folder.READ_WRITE);

		return folder;
	}

	private Store _getStore(MailingListRequest mailingListRequest)
		throws Exception {

		String protocol = mailingListRequest.getInProtocol();
		String host = mailingListRequest.getInServerName();
		int port = mailingListRequest.getInServerPort();
		String user = mailingListRequest.getInUserName();
		String password = mailingListRequest.getInPassword();

		Account account = Account.getInstance(protocol, port);

		account.setHost(host);
		account.setPort(port);
		account.setUser(user);
		account.setPassword(password);

		Session session = _mailService.getSession(account);

		URLName urlName = new URLName(
			protocol, host, port, StringPool.BLANK, user, password);

		return session.getStore(urlName);
	}

	private void _processMessage(
			MailingListRequest mailingListRequest, Message mailMessage)
		throws Exception {

		if (MBMailUtil.hasMailIdHeader(mailMessage)) {
			return;
		}

		String from = null;

		Address[] addresses = mailMessage.getFrom();

		if (ArrayUtil.isNotEmpty(addresses)) {
			Address address = addresses[0];

			if (address instanceof InternetAddress) {
				InternetAddress internetAddress = (InternetAddress)address;

				from = internetAddress.getAddress();
			}
			else {
				from = address.toString();
			}
		}

		long companyId = mailingListRequest.getCompanyId();

		long categoryId = mailingListRequest.getCategoryId();

		if (_log.isDebugEnabled()) {
			_log.debug("Category id " + categoryId);
		}

		boolean anonymous = false;

		User user = _userLocalService.fetchUserByEmailAddress(companyId, from);

		if (user == null) {
			if (!mailingListRequest.isAllowAnonymous()) {
				return;
			}

			anonymous = true;

			user = _userLocalService.getUserById(
				companyId, mailingListRequest.getUserId());
		}

		long parentMessageId = MBMailUtil.getParentMessageId(mailMessage);

		if (_log.isDebugEnabled()) {
			_log.debug("Parent message id " + parentMessageId);
		}

		MBMessage parentMessage = null;

		if (parentMessageId > 0) {
			parentMessage = _mbMessageLocalService.fetchMBMessage(
				parentMessageId);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Parent message " + parentMessage);
		}

		MBMailMessage mbMailMessage = new MBMailMessage();

		MBMailUtil.collectPartContent(mailMessage, mbMailMessage);

		PermissionCheckerUtil.setThreadValues(user);

		MailingListThreadLocal.setSourceMailingList(true);

		String subject = MBMailUtil.getSubjectWithoutMessageId(mailMessage);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		long groupId = mailingListRequest.getGroupId();

		serviceContext.setLayoutFullURL(
			_portal.getLayoutFullURL(
				groupId,
				PortletProviderUtil.getPortletId(
					MBMessage.class.getName(), PortletProvider.Action.VIEW)));
		serviceContext.setScopeGroupId(groupId);

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			mbMailMessage.getInputStreamOVPs();

		try {
			if (parentMessage == null) {
				_mbMessageService.addMessage(
					groupId, categoryId, subject,
					mbMailMessage.getBody(_htmlParser),
					MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs,
					anonymous, 0.0, true, serviceContext);
			}
			else {
				_mbMessageService.addMessage(
					parentMessage.getMessageId(), subject,
					mbMailMessage.getBody(_htmlParser),
					MBMessageConstants.DEFAULT_FORMAT, inputStreamOVPs,
					anonymous, 0.0, true, serviceContext);
			}
		}
		finally {
			for (ObjectValuePair<String, InputStream> inputStreamOVP :
					inputStreamOVPs) {

				try (InputStream inputStream = inputStreamOVP.getValue()) {
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException);
					}
				}
			}
		}
	}

	private void _processMessages(
			MailingListRequest mailingListRequest, Message[] messages)
		throws Exception {

		for (Message message : messages) {
			try {
				_processMessage(mailingListRequest, message);
			}
			finally {
				PermissionCheckerUtil.setThreadValues(null);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailingListMessageListener.class);

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private MailService _mailService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private Portal _portal;

	private ServiceRegistration<Destination> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

}